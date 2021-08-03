/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.hospitalReportManager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import oscar.oscarLab.ca.on.HRMResultsData;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMService
{
	@Autowired
	private HrmDocumentModelToDbConverter hrmDocumentModelToDbConverter;

	@Autowired
	private HRMDocumentDao hrmDocumentDao;

	@Autowired
	private HRMDocumentToDemographicDao hrmDocumentToDemographicDao;

	@Autowired
	private HRMDocumentToProviderDao hrmDocumentToProviderDao;

	/**
	 * upload a new HRM document to the database with an associated document reference.
	 * @param hrmDocumentModel - the model
	 * @param demographic - the demographic entity
	 * @return - the persisted HRM entity
	 * @throws IOException
	 */
	public HRMDocument uploadNewHRMDocument(HrmDocument hrmDocumentModel, Demographic demographic) throws IOException
	{
		HRMDocument hrmDocument = hrmDocumentModelToDbConverter.convert(hrmDocumentModel);
		
		// persist hrm database info and associated objects through cascade
		HRMReportParser.fillDocumentHashData(hrmDocument, hrmDocumentModel.getReportFile());
		hrmDocumentDao.persist(hrmDocument);
		//hrmDocumentModel.getReportFile().moveToHRMDocuments();

		// assign the hrm document to the demographic
		routeToDemographic(hrmDocument, demographic);
		
		// link the deliverToProvider
		
		
		// link the associated reviewers
		for(HRMDocumentToProvider documentToProvider : hrmDocument.getDocumentToProviderList())
		{
			hrmDocumentToProviderDao.persist(documentToProvider);
		}

		
		return hrmDocument;
	}

	public void uploadAllNewHRMDocuments(List<HrmDocument> hrmDocumentModels, Demographic demographic) throws IOException
	{
		for(HrmDocument documentModel : hrmDocumentModels)
		{
			uploadNewHRMDocument(documentModel, demographic);
		}
	}

	public void routeToDemographic(HRMDocument document, Demographic demographic)
	{
		HRMDocumentToDemographic hrmDocumentToDemographic = hrmDocumentToDemographicDao.findByHrmDocumentIdAndDemographicNo(document.getId(), demographic.getId());
		if(hrmDocumentToDemographic == null)
		{
			hrmDocumentToDemographic = new HRMDocumentToDemographic();
			hrmDocumentToDemographic.setDemographic(demographic);
			hrmDocumentToDemographic.setHrmDocument(document);
			hrmDocumentToDemographic.setDemographicNo(demographic.getId());
			hrmDocumentToDemographic.setHrmDocumentId(document.getId());
			hrmDocumentToDemographic.setTimeAssigned(new Date());

			hrmDocumentToDemographicDao.persist(hrmDocumentToDemographic);
		}
	}

	public void routeToProvider(HRMDocument document, ProviderData ... providers)
	{
		for(ProviderData provider : providers)
		{
			// check for existing connection first
			HRMDocumentToProvider hrmDocumentToProvider = hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNo(document.getId(), provider.getId());
			if(hrmDocumentToProvider == null)
			{
				hrmDocumentToProvider = new HRMDocumentToProvider();
				hrmDocumentToProvider.setHrmDocument(document);
				hrmDocumentToProvider.setProviderNo(provider.getId());
				hrmDocumentToProvider.setProvider(provider);
				hrmDocumentToProviderDao.persist(hrmDocumentToProvider);
			}
		}
	}

	public Map<String, HRMDemographicDocument> getHrmDocumentsForDemographic(Integer demographicNo)
	{
		List<HRMDocument> allHrmDocsForDemo = hrmDocumentDao.findByDemographicId(demographicNo);

		List<Integer> doNotShowList = new LinkedList<>();
		HashMap<String, HRMDocument> labReports = new HashMap<>();

		Map<String, HRMDemographicDocument> out = new HashMap<>();

		for (HRMDocument doc : allHrmDocsForDemo)
		{
			String facilityId = doc.getSendingFacilityId();
			String facilityReportId = doc.getSendingFacilityReportId();
			String deliverToUserId = doc.getDeliverToUserId();

			// filter duplicate reports
			String duplicateKey;
			if(!HRMFileParser.SCHEMA_VERSION.equals(doc.getReportFileSchemaVersion())) // legacy xml lookup
			{
				HRMReport hrmReport = HRMReportParser.parseReport(doc.getReportFile(), doc.getReportFileSchemaVersion());
				if(hrmReport != null)
				{
					facilityId = hrmReport.getSendingFacilityId();
					facilityReportId = hrmReport.getSendingFacilityReportNo();
					deliverToUserId = hrmReport.getDeliverToUserId();
				}
			}

			// if we are missing too much data (cds imports can cause this), we don't want to filter the reports, just choose a unique key
			if(facilityId == null && facilityReportId == null)
			{
				duplicateKey = String.valueOf(doc.getId());
			}
			else
			{
				// the key = SendingFacility+':'+ReportNumber+':'+DeliverToUserID as per HRM spec can be used to signify duplicate report
				duplicateKey = facilityId + ':' + facilityReportId + ':' + deliverToUserId;
			}

			List<HRMDocument> relationshipDocs = hrmDocumentDao.findAllDocumentsWithRelationship(doc.getId());

			HRMDocument oldestDocForTree = doc;
			for(HRMDocument relationshipDoc : relationshipDocs)
			{
				if(relationshipDoc.getId().intValue() != doc.getId().intValue())
				{
					if(relationshipDoc.getReportDate().compareTo(oldestDocForTree.getReportDate()) >= 0
						|| relationshipDoc.getReportStatus().equalsIgnoreCase(HrmDocument.REPORT_STATUS.CANCELLED.getValue()))
					{
						doNotShowList.add(oldestDocForTree.getId());
						oldestDocForTree = relationshipDoc;
					}
				}
			}

			boolean addToList = true;
			for(HRMDemographicDocument demographicDocument: out.values())
			{
				HRMDocument displayDoc = demographicDocument.getHrmDocument();
				if(displayDoc.getId().intValue() == oldestDocForTree.getId().intValue())
				{
					addToList = false;
					break;
				}
			}

			for(Integer doNotShowId : doNotShowList)
			{
				if(doNotShowId.intValue() == oldestDocForTree.getId().intValue())
				{
					addToList = false;
					break;
				}
			}

			if (addToList)
			{
				// if no duplicate
				if (!out.containsKey(duplicateKey))
				{
					HRMDemographicDocument demographicDocument = new HRMDemographicDocument();
					demographicDocument.setHrmDocument(oldestDocForTree);

					out.put(duplicateKey, demographicDocument);
					labReports.put(duplicateKey, doc);
				}
				else // there exists an entry like this one
				{
					Integer duplicateIdToAdd;

					HRMDocument previousHrmReport = labReports.get(duplicateKey);
					HRMDemographicDocument demographicDocument = out.get(duplicateKey);

					// if the current entry is newer than the previous one then replace it, other wise just keep the previous entry
					if (HRMResultsData.isNewer(doc, previousHrmReport))
					{
						HRMDocument previousHRMDocument = demographicDocument.getHrmDocument();
						duplicateIdToAdd = previousHRMDocument.getId();

						demographicDocument.setHrmDocument(oldestDocForTree);
						labReports.put(duplicateKey, doc);
					}
					else
					{
						duplicateIdToAdd = doc.getId();
					}

					if (demographicDocument.getDuplicateIds() == null)
					{
						demographicDocument.setDuplicateIds(new ArrayList<>());
					}

					demographicDocument.getDuplicateIds().add(duplicateIdToAdd);
				}
			}
		}

		return out;
	}
}
