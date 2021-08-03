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


import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.converter.out.ProviderDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;


import org.oscarehr.dataMigration.model.hrm.HrmDocumentMatchingData;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;

import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xml.hrm.v4_3.PersonNameSimple;
import xml.hrm.v4_3.ReportsReceived;
import xml.hrm.v4_3.TransactionInformation;

import java.util.ArrayList;
import java.util.List;

@Component
public class HRMReportImportMapper extends AbstractHRMImportMapper<HRMReport_4_3, HrmDocument>
{
	@Autowired
	static HRMReportDocumentMapper documentMapper = new HRMReportDocumentMapper();
	
	@Autowired
	static ProviderDataDao providerDao = SpringUtils.getBean(ProviderDataDao.class);
	
	@Autowired
	static ProviderDbToModelConverter providerConverter = SpringUtils.getBean(ProviderDbToModelConverter.class);
	
	private static final String SCHEMA_VERSION = "4.3";
	
	@Override
	public HrmDocument importToJuno(HRMReport_4_3 importStructure) throws Exception
	{
		HrmDocument model = new HrmDocument();
		model.setParentReport(null);
		
		model.setMessageUniqueId(importStructure.getMessageUniqueId());
		model.setDescription("Downloaded HRM Document");
		model.setDeliverToUserId(importStructure.getDeliverToUserId());
		
		// Despite the name, there is exactly one report per imported HRMReport
		ReportsReceived report = importStructure.getDocumentRoot().getPatientRecord().getReportsReceived().get(0);
		
		model.setReportDateTime(toNullableLocalDateTime(report.getEventDateTime()));
		model.setReceivedDateTime(toNullableLocalDateTime(report.getReceivedDateTime()));
		model.setCreatedBy(stubProviderFromPersonName(report.getAuthorPhysician()));
		model.setReportStatus(getStatus(report.getResultStatus()));
		model.setInternalReviewers(stubReviewers(report.getReviewingOHIPPhysicianId(), report.getReviewedDateTime()));  // TODO: can only stub?
		model.setReportClass(getReportClass(report.getClazz()));
		model.setReportSubClass(report.getSubClass());
		model.setReportFileSchemaVersion(SCHEMA_VERSION);
		
		model.setSourceFacility(importStructure.getSendingFacilityId());    // TODO: unsure of this.  Source facility in schema is under the xsd:SendingFacility
		model.setSendingFacilityId(model.getSourceFacility());              // TODO: unsure of this.  Source facility in schema is under the xsd:SendingFacility
		model.setSendingFacilityReport(importStructure.getSendingFacilityReportNo());
		
		model.setObservations(importStructure.getObservations());
		
		model.setHashData(mapMatchingData(importStructure));
		
		model.setDocument(documentMapper.importToJuno(importStructure));
		model.setReportFile(FileFactory.getExistingFile(importStructure.getFileLocation()));   // IO Exception
		
		// According to the schema there is exactly one transaction information per message
		//model.setDeliverToUsers(mapTargetProviders(importStructure.getDocumentRoot().getPatientRecord().getTransactionInformation()));
		
		// category (HrmCategory)               // TODO: Leave blank???
		// hashData (HrmDocumentMatchingData)   // TODO: HashData??
		
		// comments List<HrmComments>           // TODO: leave this blank??
		
		return model;
	}
	
	protected HrmDocumentMatchingData mapMatchingData(HRMReport_4_3 importStructure)
	{
		HrmDocumentMatchingData data = new HrmDocumentMatchingData();
		data.setReportHash(importStructure.getMessageUniqueId());
		data.setNumDuplicatesReceived(0);
		
		return data;
	}
	
	protected List<Provider> mapTargetProviders(List<TransactionInformation> transactionInfo)
	{
		List<Provider> providers = new ArrayList<Provider>();
		
		for (TransactionInformation transInfo : transactionInfo)
		{
			PersonNameSimple providerName = transInfo.getProvider();
			String deliverToID = transInfo.getDeliverToUserID();
			
			ProviderCriteriaSearch searchParams = createProviderCriteriaSearch(providerName, deliverToID);
			
			List<ProviderData> knownProviders = providerDao.criteriaSearch(searchParams);
			if (knownProviders.size() == 1)
			{
				providers.add(providerConverter.convert(knownProviders.get(0)));
			}
		}
		
		return providers;
	}
	
	private ProviderCriteriaSearch createProviderCriteriaSearch(PersonNameSimple providerName, String deliverToID)
	{
		final String PREFIX_PHYSICIAN = "D";
		final String PREFIX_NURSE = "N";
		
		ProviderCriteriaSearch searchParams = new ProviderCriteriaSearch();
		
		if (providerName.getFirstName() != null)
		{
			searchParams.setFirstName(providerName.getFirstName());
		}
		
		if (providerName.getLastName() != null )
		{
			searchParams.setLastName(providerName.getLastName());
		}
		
		if (deliverToID != null)
		{
			switch (deliverToID)
			{
				case PREFIX_PHYSICIAN:
					searchParams.setPractitionerNo(deliverToID);
					break;
				case PREFIX_NURSE:
					searchParams.setOntarioCnoNumber(deliverToID);
					break;
				default:
					break;
			}
		}
		
		return searchParams;
	}
	
}
