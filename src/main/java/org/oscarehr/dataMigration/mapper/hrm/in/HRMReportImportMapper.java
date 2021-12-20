/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.dataMigration.mapper.hrm.in;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.helpers.FileUtils;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.model.hrm.HrmCategoryModel;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;

import org.oscarehr.dataMigration.model.hrm.HrmDocumentMatchingData;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;

import org.oscarehr.hospitalReportManager.service.HRMCategoryService;
import org.oscarehr.integration.clinicaid.dto.v2.MasterNumber;
import org.oscarehr.integration.clinicaid.service.v2.ClinicAidService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import oscar.util.ConversionUtils;
import xml.hrm.v4_3.ReportClass;
import xml.hrm.v4_3.ReportsReceived;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class HRMReportImportMapper extends AbstractHRMImportMapper<HRMReport_4_3, HrmDocument>
{
	@Autowired
	private HRMReportDocumentMapper documentMapper;
	
	@Autowired
	private ClinicAidService clinicAidService;

	@Autowired
	private HRMCategoryService categoryService;

	private static final String SCHEMA_VERSION = "4.3";
	
	@Override
	public HrmDocument importToJuno(HRMReport_4_3 importStructure) throws IOException
	{
		HrmDocument model = new HrmDocument();
		model.setParentReport(null);
		
		model.setMessageUniqueId(importStructure.getMessageUniqueId());
		model.setDeliverToUserId(importStructure.getDeliverToUserId());
		
		// Despite the name, there is exactly one report per imported HRMReport
		ReportsReceived report = importStructure.getDocumentRoot().getPatientRecord().getReportsReceived().get(0);
		
		ReportClass reportClass = report.getClazz();
		model.setReportClass(fromNullableString(reportClass));
		
		// DI (Diagnostic Imaging) and CRT (Cardio-respiratory reports seem to not have an event date time, so use the
		// observation time of the first procedure in it's place, if one can be found.
		if (reportClass.equals(ReportClass.DIAGNOSTIC_IMAGING_REPORT) || reportClass.equals(ReportClass.CARDIO_RESPIRATORY_REPORT) &&
			toNullableLocalDateTime(report.getEventDateTime()) == null &&
			importStructure.getObservations() != null && !importStructure.getObservations().isEmpty())
		{
			HrmObservation firstProcedure = importStructure.getObservations().get(0);
			model.setReportDateTime(firstProcedure.getObservationDateTime());
		}
		else
		{
			model.setReportDateTime(toNullableLocalDateTime(report.getEventDateTime()));
		}
		
		model.setReceivedDateTime(LocalDateTime.now());
		model.setCreatedBy(stubProviderFromPersonName(report.getAuthorPhysician()));
		model.setReportStatus(fromNullableString(report.getResultStatus()));
		model.setReportSubClass(report.getSubClass());
		model.setReportFileSchemaVersion(SCHEMA_VERSION);
		
		String facilityMasterNumber = importStructure.getSendingFacilityId();
		model.setSendingFacilityId(facilityMasterNumber);
		model.setSendingFacilityReport(importStructure.getSendingFacilityReportNo());
		
		if (clinicAidService != null && ConversionUtils.hasContent(facilityMasterNumber))
		{
			try
			{
				MasterNumber masterNumberLookup = clinicAidService.getOntarioMasterNumber(facilityMasterNumber);
				if (masterNumberLookup != null)
				{
					model.setSendingFacility(masterNumberLookup.getName());
				}
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error("Could not establish connection to clinicaid api", e);
			}
		}

		model.setObservations(importStructure.getObservations());

		Optional<HrmCategoryModel> category = categoryService.categorize(model);

		if (category.isPresent())
		{
			model.setCategory(category.get());
			model.setDescription(category.get().getName());
		}
		else
		{
			model.setDescription(model.getReportClass().getValue());
		}


		model.setDocument(documentMapper.importToJuno(importStructure));
		model.setReportFile(FileFactory.getExistingFile(importStructure.getFileLocation()));

		model.setMatchingData(createMatchingData(FileUtils.getStringFromFile(model.getReportFile().getFileObject())));

		return model;
	}

	protected HrmDocumentMatchingData createMatchingData(String fileContents)
	{
		String noMessageIdFileData = fileContents.replaceAll("<MessageUniqueID>.*?</MessageUniqueID>", "<MessageUniqueID></MessageUniqueID>");
		String noTransactionInfoFileData = fileContents.replaceAll("<TransactionInformation>.*?</TransactionInformation>", "<TransactionInformation></TransactionInformation>");
		String noDemographicInfoFileData = fileContents.replaceAll("<Demographics>.*?</Demographics>", "<Demographics></Demographics").replaceAll("<MessageUniqueID>.*?</MessageUniqueID>", "<MessageUniqueID></MessageUniqueID>");

		String noMessageIdHash = DigestUtils.md5Hex(noMessageIdFileData);
		String noTransactionInfoHash = DigestUtils.md5Hex(noTransactionInfoFileData);
		String noDemographicInfoHash = DigestUtils.md5Hex(noDemographicInfoFileData);

		HrmDocumentMatchingData matchingData = new HrmDocumentMatchingData();

		matchingData.setReportHash(noMessageIdHash);
		matchingData.setReportLessTransactionInfoHash(noTransactionInfoHash);
		matchingData.setReportLessDemographicInfoHash(noDemographicInfoHash);
		matchingData.setNumDuplicatesReceived(0);

		return matchingData;
	}
}