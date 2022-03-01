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
import java.util.ArrayList;
import java.util.HashSet;

import lombok.Synchronized;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.dataMigration.converter.out.hrm.HrmDocumentDbToModelConverter;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.dto.HRMDemographicDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.model.HrmFetchResultsModel;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
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
	private HrmDocumentModelToDbConverter modelToEntity;

	@Autowired
	private HrmDocumentDbToModelConverter entityToModel;

	@Autowired
	private HRMDocumentDao hrmDocumentDao;

	@Autowired
	private HRMDocumentToDemographicDao hrmDocumentToDemographicDao;

	@Autowired
	private HRMDocumentToProviderDao hrmDocumentToProviderDao;
	
	private Logger logger = MiscUtils.getLogger();
	
	// Access only via synchronized methods
	private HrmFetchResultsModel lastFetchResults = null;
	
	/**
	 * Get the results of the last fetch operation
	 * @return
	 */
	@Synchronized
	public HrmFetchResultsModel getLastFetchResults()
	{
		return this.lastFetchResults;
	}
	
	/**
	 * Download and process HRM documents from the OMD sftp server.
	 *
	 * @return object containing results of the operation
	 */
	@Synchronized
	public HrmFetchResultsModel consumeRemoteHRMDocuments()
	{
		HrmFetchResultsModel results = new HrmFetchResultsModel();
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
	HrmFetchResultsModel consumeLocalHRMDocuments(Path localHRMPath)
	{
		HrmFetchResultsModel results = new HrmFetchResultsModel();
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
		HRMDocument hrmDocument = modelToEntity.convert(hrmDocumentModel);
		HRMReportParser.fillDocumentHashData(hrmDocument, hrmDocumentModel.getReportFile());

		// The intent here was to move the document into documents folder as close to the end of the import
		// process as possible, such that a failure earlier on doesn't result in an orphaned file in the
		// documents directory with no database references
		Path hrmBaseDirectory = Paths.get(GenericFile.HRM_BASE_DIR);

		GenericFile hrmReportFile = hrmDocumentModel.getReportFile();
		hrmReportFile.moveToHRMDocuments();
		Path relativePath = hrmBaseDirectory.relativize(Paths.get(hrmReportFile.getPath()));
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
		List<Integer> duplicateIds = hrmDocumentDao.findByMessageUniqueId(model.getMessageUniqueId());
		return duplicateIds != null && duplicateIds.size() > 0;
	}
	
	/**
	 * Persist HRMDocument and any associated provider and demographic linkages through cascade.
	 *
	 * @param hrmDocument duplicate to persist
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

	public List<HRMDemographicDocument> getHrmDocumentsForDemographic(Integer demographicNo)
	{
		Set<String> uniqueDocumentKeys = new HashSet<>();
		List<HRMDocument> allHrmDocsForDemo = hrmDocumentDao.findByDemographicId(demographicNo);
		List<HRMDemographicDocument> uniqueHrmDocsForDemo = new ArrayList<>();

		for (HRMDocument doc : allHrmDocsForDemo)
		{
			// filter duplicate reports
			String key;

			// A duplicate is simply an exact match on the contents of the message, minus the transactional segment
			// and there is no versioning system.
			if (ConversionUtils.hasContent(doc.getReportLessTransactionInfoHash()))
			{
				key = doc.getReportLessTransactionInfoHash();
			}
			else
			{
				// if we are missing too much data (cds imports can cause this), we don't want to filter the reports, just choose a unique key
				key = String.valueOf(doc.getId());
			}

			if (!uniqueDocumentKeys.contains(key))
			{
				HRMDemographicDocument demographicDocument = new HRMDemographicDocument();
				demographicDocument.setHrmDocument(doc);
				uniqueHrmDocsForDemo.add(demographicDocument);
				uniqueDocumentKeys.add(key);
			}
		}

		return uniqueHrmDocsForDemo;
	}
	
	public void handleDuplicateDocument(HRMDocument duplicate)
	{
		List<Integer> matchingDocuments = hrmDocumentDao.findByMessageUniqueId(duplicate.getMessageUniqueId());
		if (matchingDocuments != null && !matchingDocuments.isEmpty())
		{
			if (matchingDocuments.size() > 1)
			{
				logger.warn(String.format("Multiple HRM documents have the same unique id %s", duplicate.getMessageUniqueId()));
			}

			HRMDocument original = hrmDocumentDao.find(matchingDocuments.get(0));
			original.setNumDuplicatesReceived(original.getNumDuplicatesReceived() + 1);
			hrmDocumentDao.merge(original);
		}
	}
}