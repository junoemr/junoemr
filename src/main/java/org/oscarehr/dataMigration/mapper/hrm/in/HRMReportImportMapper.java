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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.converter.out.ProviderDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;

import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;

import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xml.hrm.v4_3.ReportsReceived;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class HRMReportImportMapper extends AbstractHRMImportMapper<HRMReport_4_3, HrmDocument>
{
	@Autowired
	static HRMReportDocumentMapper documentMapper = new HRMReportDocumentMapper();
	
	@Autowired
	static ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	
	@Autowired
	static ProviderDbToModelConverter providerConverter = SpringUtils.getBean(ProviderDbToModelConverter.class);
	
	static final Logger logger = MiscUtils.getLogger();
	
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
		
		model.setReportDateTime(toNullableLocalDateTime(report.getEventDateTime()));
		model.setReceivedDateTime(LocalDateTime.now());
		model.setCreatedBy(stubProviderFromPersonName(report.getAuthorPhysician()));
		model.setReportStatus(getStatus(report.getResultStatus()));
		model.setReportClass(getReportClass(report.getClazz()));
		model.setDescription(model.getReportClass().getValue());
		
		model.setReportSubClass(report.getSubClass());
		model.setReportFileSchemaVersion(SCHEMA_VERSION);
		
		//model.setSourceFacility(importStructure.getSendingFacilityId());              // TODO: unsure of this.  Source facility in schema is under the xsd:SendingFacility
		model.setSendingFacilityId(importStructure.getSendingFacilityId());             // TODO: unsure of this.  Source facility in schema is under the xsd:SendingFacility
		model.setSendingFacilityReport(importStructure.getSendingFacilityReportNo());
		
		model.setObservations(importStructure.getObservations());
		
		model.setDocument(documentMapper.importToJuno(importStructure));
		model.setReportFile(FileFactory.getExistingFile(importStructure.getFileLocation()));
		
		// Document matching data is mapped at the service level
		
		return model;
	}
}
