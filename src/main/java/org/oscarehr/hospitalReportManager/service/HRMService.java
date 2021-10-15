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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.model.HRMFetchResults;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import oscar.oscarLab.ca.all.upload.ProviderLabRouting;
import oscar.util.ConversionUtils;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class HRMService
{
	@Autowired
	private HRMSftpService sftpService;
	
	@Autowired
	private HRMReportProcessor reportProcessor;
	
	@Autowired
	private HrmDocumentModelToDbConverter hrmDocumentModelToDbConverter;

	@Autowired
	private HRMDocumentDao hrmDocumentDao;

	@Autowired
	private HRMDocumentToDemographicDao hrmDocumentToDemographicDao;

	@Autowired
	private HRMDocumentToProviderDao hrmDocumentToProviderDao;
	
	private Logger logger = MiscUtils.getLogger();
	
	// Access only via synchronized methods
	private HRMFetchResults lastFetchResults = null;
	
	/**
	 * Get the results of the last fetch operation
	 * @return
	 */
	@Synchronized
	public HRMFetchResults getLastFetchResults()
	{
		return this.lastFetchResults;
	}
	
	/**
	 * Download and process HRM documents from the OMD sftp server.
	 *
	 * @return object containing results of the operation
	 */
	@Synchronized
	public HRMFetchResults consumeRemoteHRMDocuments()
	{
		HRMFetchResults results = new HRMFetchResults();
		List<GenericFile> downloadedFiles = sftpService.pullHRMFromSource(results);
		reportProcessor.processHRMFiles(downloadedFiles, true, results);
		
		this.lastFetchResults = results;
		return results;
	}
	
	/**
	 * Skip downloading, and process the results of the local override directory instead.  These files
	 * will not be decrypted, only parsed and routed.
	 *
	 * @return object containing results of the operation
	 */
	@Synchronized
	HRMFetchResults consumeLocalHRMDocuments(Path localHRMPath)
	{
		HRMFetchResults results = new HRMFetchResults();
		results.setLoginSuccess(true);
		results.setReportsDownloaded(0);
		results.setDownloadSuccess(true);
		
		try
		{
			List<GenericFile> localFiles = Files.list(localHRMPath)
			                                    .map(path -> new XMLFile(path.toFile()))
			                                    .collect(Collectors.toList());
			reportProcessor.processHRMFiles(localFiles, true, results);
		}
		catch(IOException e)
		{
			results.setProcessingSuccess(false);
		}
		finally
		{
			this.lastFetchResults = results;
		}
		
		return results;
	}
	
	/**
	 * Upload a new HRM document to the database and move the associated hrm report xml file to the to the HRM documents folder.
	 *
	 * @param hrmDocumentModel - the model
	 * @param demographic - the demographic entity
	 * @return - the persisted HRM entity
	 * @throws IOException
	 */
	public HRMDocument uploadNewHRMDocument(HrmDocument hrmDocumentModel, Demographic demographic) throws IOException
	{
		HRMDocument hrmDocument = hrmDocumentModelToDbConverter.convert(hrmDocumentModel);
		HRMReportParser.fillDocumentHashData(hrmDocument, hrmDocumentModel.getReportFile());

		// The intent here was to move the document into documents folder as close to the end of the import
		// process as possible, such that a failure earlier on doesn't result in an orphaned file in the
		// documents directory with no database references
		Path hrmBaseDirectory = Paths.get(GenericFile.HRM_BASE_DIR);

		GenericFile newFileLocation = hrmDocumentModel.getReportFile().moveToHRMDocuments();
		Path relativePath = hrmBaseDirectory.relativize(Paths.get(newFileLocation.getPath()));
		hrmDocument.setReportFile(relativePath.toString());

		HRMDocument documentModel = persistAndLinkHRMDocument(hrmDocument, demographic);

		return documentModel;
	}
	
	public void uploadAllNewHRMDocuments(List<HrmDocument> hrmDocumentModels, Demographic demographic) throws IOException
	{
		for(HrmDocument documentModel : hrmDocumentModels)
		{
			uploadNewHRMDocument(documentModel, demographic);
		}
	}
	
	public boolean isDuplicateReport(HRMDocument model)
	{
		// report hash matches = duplicate report for same recipient
		// no transaction info hash matches = duplicate report, but different recipient TODO handle somewhere?
		
		List<Integer> duplicateIds = hrmDocumentDao.findByHash(model.getReportHash());
		return duplicateIds != null && duplicateIds.size() > 0;
	}
	
	/**
	 * Persist HRMDocument and any associated provider and demographic linkages through cascade.
	 *
	 * @param hrmDocument hrmDocument to persist
	 * @param demographic <optional>Demographic to associate with the HRM document</optional>
	 * @return HRMDocument entity
	 */
	public HRMDocument persistAndLinkHRMDocument(HRMDocument hrmDocument, Demographic demographic)
	{
		hrmDocumentDao.persist(hrmDocument);
		
		if (demographic != null && demographic.getId() != null)
		{
			routeToDemographic(hrmDocument, demographic);
		}
		
		if (!hrmDocument.getDocumentToProviderList().isEmpty())
		{
			// includes the deliverTo provider and any associated reviewers
			routeToProviders(hrmDocument.getDocumentToProviderList());
		}
		else
		{
			routeToGeneralInbox(hrmDocument);
		}
		
		return hrmDocument;
	}
	
	private void routeToProviders(List<HRMDocumentToProvider> providerLinks)
	{
		for(HRMDocumentToProvider documentToProvider : providerLinks)
		{
			hrmDocumentToProviderDao.persist(documentToProvider);
			
			
			
			ProviderLabRouting inboxRouting = new ProviderLabRouting();
			inboxRouting.routeMagic(documentToProvider.getHrmDocument().getId(),
			                        documentToProvider.getProviderNo(),
			                        ProviderLabRoutingModel.LAB_TYPE_HRM,
			                        documentToProvider.getHrmDocument().getReportDate());
		}
	}
	
	private void routeToGeneralInbox(HRMDocument hrmDocument)
	{
		ProviderLabRouting inboxRouting = new ProviderLabRouting();
		inboxRouting.routeMagic(hrmDocument.getId(),
		                        String.valueOf(ProviderLabRoutingModel.PROVIDER_UNMATCHED),
		                        ProviderLabRoutingModel.LAB_TYPE_HRM,
		                        hrmDocument.getReportDate());
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
			
			ProviderLabRouting inboxRouting = new ProviderLabRouting();
			inboxRouting.routeMagic(document.getId(),
			                        String.valueOf(provider.getProviderNo()),
			                        ProviderLabRoutingModel.LAB_TYPE_HRM,
			                        document.getReportDate());
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
			if (!HRMFileParser.SCHEMA_VERSION.equals(doc.getReportFileSchemaVersion())) // legacy xml lookup
			{
				HRMReport hrmReport = HRMReportParser.parseReport(doc.getReportFile(), doc.getReportFileSchemaVersion());
				if (hrmReport != null)
				{
					facilityId = hrmReport.getSendingFacilityId();
					facilityReportId = hrmReport.getSendingFacilityReportNo();
					deliverToUserId = hrmReport.getDeliverToUserId();
				}
			}
			
			// The commented code below is legacy behaviour kept in for reference (for now).
			// Previously if a message matched on the set { facility, reportId, deliverToUser }, some overly complicated
			// logic was applied to determine duplication/versioning.
			
			// Now, a duplicate is simply an exact match on the contents of the message, minus the transactional segment
			// and there is no versioning system.
			
			
			if (ConversionUtils.hasContent(doc.getReportLessTransactionInfoHash()))
			{
				duplicateKey = doc.getReportLessTransactionInfoHash();
			}
			else
			{
				// if we are missing too much data (cds imports can cause this), we don't want to filter the reports, just choose a unique key
				duplicateKey = String.valueOf(doc.getId());
			}
			
			/*
			// if we are missing too much data (cds imports can cause this), we don't want to filter the reports, just choose a unique key
			if (facilityId == null && facilityReportId == null)
			{
				duplicateKey = String.valueOf(doc.getId());
			}
			else
			{
				// the key = SendingFacility+':'+ReportNumber+':'+DeliverToUserID as per HRM spec can be used to signify duplicate report
				duplicateKey = facilityId + ':' + facilityReportId + ':' + deliverToUserId;
			}*/
			
			if (!out.containsKey(duplicateKey))
			{
				HRMDemographicDocument demographicDocument = new HRMDemographicDocument();
				demographicDocument.setHrmDocument(doc);
				out.put(duplicateKey, demographicDocument);
				
				labReports.put(duplicateKey, doc);
			}
		}
/*

			List<HRMDocument> relationshipDocs = hrmDocumentDao.findAllDocumentsWithRelationship(doc.getId());

			HRMDocument oldestDocForTree = doc;
			for(HRMDocument relationshipDoc : relationshipDocs)
			{
				if(relationshipDoc.getId().intValue() != doc.getId().intValue())
				{
					if(relationshipDoc.getReportDate().compareTo(oldestDocForTree.getReportDate()) >= 0
						|| relationshipDoc.getReportStatus().equals(HRMDocument.STATUS.CANCELLED))
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
*/

		return out;
	}
	
	public void handleDuplicate(HRMDocument hrmDocument)
	{
		List<Integer> matchingDocuments = hrmDocumentDao.findByHash(hrmDocument.getReportHash());
		
		if (matchingDocuments != null && !matchingDocuments.isEmpty())
		{
			HRMDocument originalDocument = hrmDocumentDao.find(matchingDocuments.get(0));
			originalDocument.setNumDuplicatesReceived(originalDocument.getNumDuplicatesReceived() + 1);
			hrmDocumentDao.merge(originalDocument);
		}
		
		if (matchingDocuments != null && matchingDocuments.size() > 1)
		{
			logger.warn(String.format("Multiple HRM documents have the same report hash %s", hrmDocument.getReportHash()));
		}
	}
}