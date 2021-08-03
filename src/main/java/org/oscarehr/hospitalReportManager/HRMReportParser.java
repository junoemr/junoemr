/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.cxf.helpers.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Provider;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentSubClassDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentSubClass;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.xml.sax.SAXException;
import oscar.util.ConversionUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class HRMReportParser
{

	private static final Logger logger = MiscUtils.getLogger();
	private static final HRMDocumentDao hrmDocumentDao = SpringUtils.getBean(HRMDocumentDao.class);
	
	private HRMReportParser() {}


	public static HRMReport parseReport(String hrmReportFileLocation, String schemaVersion)
	{
		logger.info("Parsing the Report in the location:" + hrmReportFileLocation);
		
		if(hrmReportFileLocation != null)
		{
			try
			{
				GenericFile hrmXML = FileFactory.getExistingFile(hrmReportFileLocation);
				if(!hrmXML.getFileObject().exists())
				{
					logger.warn("unable to find the HRM report. checked " + hrmReportFileLocation + ", and in the document_dir");
					return null;
				}

				return parseReport(hrmXML, schemaVersion);
			}
			catch(SAXException e)
			{
				logger.error("SAX ERROR PARSING XML " + e);
			}
			catch(IOException | JAXBException e)
			{
				logger.error("error", e);
			}
		}
		return null;
	}

	// TODO:  Have to put the schema version back, in case there's older reports
	public static HRMReport parseReport(GenericFile hrmFile, String schemaVersion) throws IOException, SAXException, JAXBException
	{
		String fileData = FileUtils.getStringFromFile(hrmFile.getFileObject());
		
		HRMFileParser hrmParser = new HRMFileParser();
		
		xml.hrm.v4_3.OmdCds root = hrmParser.parse(hrmFile);
			
		HRMReport_4_3 report = new HRMReport_4_3(root, hrmFile.getPath(), fileData);
		
		return report;
	}
	
	/**
	 * legacy method signature
 	 */
	public static HRMReport parseReport(LoggedInInfo loggedInInfo, String hrmReportFileLocation)
	{
		return parseReport(hrmReportFileLocation, "4.3");
	}

	public static void addReportToInbox(LoggedInInfo loggedInInfo, HRMReport report) {
		
		if(report == null) {
			logger.info("addReportToInbox cannot continue, report parameter is null");
			return;
		}

		logger.info("Adding Report to Inbox, for file:"+report.getFileLocation());
		
		HRMDocument document = new HRMDocument();

		File fileLocation = new File(report.getFileLocation());

		document.setReportFile(fileLocation.getName());
		document.setReportStatus(report.getResultStatus());
		document.setReportType(report.getFirstReportClass());
		document.setTimeReceived(new Date());

		fillDocumentHashData(document, report.getFileData());
		document.setReportDate(HRMReportParser.getAppropriateDateFromReport(report));

		document.setDescription("");
		
		// We're going to check to see if there's a match in the database already for either of these
		// report hash matches = duplicate report for same recipient
		// no transaction info hash matches = duplicate report, but different recipient
		HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
		List<Integer> exactMatchList = hrmDocumentDao.findByHash(document.getReportHash());

		if (exactMatchList == null || exactMatchList.size() == 0) {
			List<HRMDocument> sameReportDifferentRecipientReportList = hrmDocumentDao.findByNoTransactionInfoHash(document.getReportLessTransactionInfoHash());

			if (sameReportDifferentRecipientReportList != null && sameReportDifferentRecipientReportList.size() > 0) {
				logger.info("Same Report Different Recipient, for file:"+report.getFileLocation());
				HRMReportParser.routeReportToProvider(sameReportDifferentRecipientReportList.get(0), report);
			} else {
				// New report
				hrmDocumentDao.persist(document);
				logger.debug("MERGED DOCUMENTS ID"+document.getId());


				HRMReportParser.routeReportToDemographic(report, document);
				HRMReportParser.doSimilarReportCheck(loggedInInfo, report, document);
				// Attempt a route to the provider listed in the report -- if they don't exist, note that in the record
				Boolean routeSuccess = HRMReportParser.routeReportToProvider(report, document.getId());
				if (!routeSuccess) {
					
					logger.info("Adding the provider name to the list of unidentified providers, for file:"+report.getFileLocation());
					
					// Add the provider name to the list of unidentified providers for this report
					document.setUnmatchedProviders((document.getUnmatchedProviders() != null ? document.getUnmatchedProviders() : "") + "|" + ((report.getDeliverToUserIdLastName()!=null)?report.getDeliverToUserIdLastName() + ", " + report.getDeliverToUserIdFirstName():report.getDeliverToUserId()) + " (" + report.getDeliverToUserId() + ")");
					hrmDocumentDao.merge(document);
					// Route this report to the "system" user so that a search for "all" in the inbox will come up with them
					HRMReportParser.routeReportToProvider(document.getId().toString(), "-1");
				}

				HRMReportParser.routeReportToSubClass(report, document);
			}
		} else if (exactMatchList != null && exactMatchList.size() > 0) {
			// We've seen this one before.  Increment the counter on how many times we've seen it before
			
			logger.info("We've seen this report before. Increment the counter on how many times we've seen it before, for file:"+report.getFileLocation());
			
			HRMDocument existingDocument = hrmDocumentDao.findById(exactMatchList.get(0)).get(0);
			existingDocument.setNumDuplicatesReceived((existingDocument.getNumDuplicatesReceived() != null ? existingDocument.getNumDuplicatesReceived() : 0) + 1);

			hrmDocumentDao.merge(existingDocument);
		}
	}

	/**
	 * fill hrm document hash data based on the file string
	 * @param document
	 */
	public static void fillDocumentHashData(HRMDocument document, String reportFileData)
	{
		String noMessageIdFileData = reportFileData.replaceAll("<MessageUniqueID>.*?</MessageUniqueID>", "<MessageUniqueID></MessageUniqueID>");
		String noTransactionInfoFileData = reportFileData.replaceAll("<TransactionInformation>.*?</TransactionInformation>", "<TransactionInformation></TransactionInformation>");
		String noDemograhpicInfoFileData = reportFileData.replaceAll("<Demographics>.*?</Demographics>", "<Demographics></Demographics").replaceAll("<MessageUniqueID>.*?</MessageUniqueID>", "<MessageUniqueID></MessageUniqueID>");

		String noMessageIdHash = DigestUtils.md5Hex(noMessageIdFileData);
		String noTransactionInfoHash = DigestUtils.md5Hex(noTransactionInfoFileData);
		String noDemographicInfoHash = DigestUtils.md5Hex(noDemograhpicInfoFileData);

		document.setReportHash(noMessageIdHash);
		document.setReportLessTransactionInfoHash(noTransactionInfoHash);
		document.setReportLessDemographicInfoHash(noDemographicInfoHash);
	}

	public static void fillDocumentHashData(HRMDocument document, GenericFile hrmFile)
	{
		String reportFileData = FileUtils.getStringFromFile(hrmFile.getFileObject());
		fillDocumentHashData(document, reportFileData);
	}

	private static void routeReportToDemographic(HRMReport report, HRMDocument mergedDocument) {
		
		if(report == null) {
			logger.info("routeReportToDemographic cannot continue, report parameter is null");
			return;
		}
		

		logger.info("Routing Report To Demographic, for file:"+report.getFileLocation());
		
		// Search the demographics on the system for a likely match and route it to them automatically
		org.oscarehr.common.dao.DemographicDao demographicDao = (org.oscarehr.common.dao.DemographicDao) SpringUtils.getBean("demographicDao");

		List<Demographic> matchingDemographicListByName = demographicDao.searchDemographic(report.getLegalName());

		if (matchingDemographicListByName.size() == 1) {
			// Found a match by name
			HRMReportParser.routeReportToDemographic(mergedDocument.getId(), matchingDemographicListByName.get(0).getDemographicNo());
		} else {
			for (Demographic d : matchingDemographicListByName) {

				if (report.getHCN().equalsIgnoreCase(d.getHin())) { // Check health card no.
					HRMReportParser.routeReportToDemographic(mergedDocument.getId(), d.getDemographicNo());
					return;
				} else if (report.getGender().equalsIgnoreCase(d.getSex()) && report.getDateOfBirthAsString().equalsIgnoreCase(d.getBirthDayAsString())) { // Check dob & sex
					HRMReportParser.routeReportToDemographic(mergedDocument.getId(), d.getDemographicNo());
					return;
				}
			}
		}
	}


	private static boolean hasSameStatus(HRMReport report, HRMReport loadedReport) {
		if(report.getResultStatus() != null) {
			return report.getResultStatus().equalsIgnoreCase(loadedReport.getResultStatus());
		}
		 
		return true;
	}
	private static void doSimilarReportCheck(LoggedInInfo loggedInInfo, HRMReport report, HRMDocument mergedDocument) {
		
		if(report == null) {
			logger.info("doSimilarReportCheck cannot continue, report parameter is null");
			return;
		}
		logger.info("Identifying if this is a report that we received before, but was sent to the wrong demographic, for file:"+report.getFileLocation());
		
		HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");

		// Check #1: Identify if this is a report that we received before, but was sent to the wrong demographic
		List<Integer> parentReportList = hrmDocumentDao.findAllWithSameNoDemographicInfoHash(mergedDocument.getReportLessDemographicInfoHash());
		if (parentReportList != null && parentReportList.size() > 0) {
			for (Integer id : parentReportList) {
				if (id != null && id.intValue() != mergedDocument.getId().intValue()) {
					mergedDocument.setParentReport(id);
					hrmDocumentDao.merge(mergedDocument);
					return;
				}
			}
		}

		// Load all the reports for this demographic into memory -- check by name only
		List<HRMReport> thisDemoHrmReportList = HRMReportParser.loadAllReportsRoutedToDemographic(loggedInInfo, report.getLegalName());

		for (HRMReport loadedReport : thisDemoHrmReportList) {
			boolean hasSameReportContent = report.getFirstReportTextContent().equalsIgnoreCase(loadedReport.getFirstReportTextContent());
			boolean hasSameStatus = hasSameStatus(report,loadedReport);
			boolean hasSameClass = report.getFirstReportClass().equalsIgnoreCase(loadedReport.getFirstReportClass());
			boolean hasSameDate = false;

			hasSameDate = HRMReportParser.getAppropriateDateFromReport(report).equals(HRMReportParser.getAppropriateDateFromReport(loadedReport));

			Integer threshold = 0;

			if (hasSameReportContent)
				threshold += 100;
			else
				threshold += 10;

			if (hasSameStatus)
				threshold += 5;
			else
				threshold += 10;

			if (hasSameClass)
				threshold += 10;
			else
				threshold += 10;

			if (hasSameDate)
				threshold += 20;
			else
				threshold += 5;

			if (threshold >= 45) {
				// This is probably a changed report addressed to the same demographic, so set the parent id (as long as this isn't the same report) and we're done!
				if (loadedReport.getHrmParentDocumentId() != null && loadedReport.getHrmDocumentId().intValue() != mergedDocument.getId().intValue()) {
					mergedDocument.setParentReport(loadedReport.getHrmParentDocumentId());
					hrmDocumentDao.merge(mergedDocument);
					return;
				} else if (loadedReport.getHrmParentDocumentId() == null) {
					mergedDocument.setParentReport(loadedReport.getHrmDocumentId());
					hrmDocumentDao.merge(mergedDocument);
					return;
				}
			}
		}
	}


	private static List<HRMReport> loadAllReportsRoutedToDemographic(LoggedInInfo loggedInInfo, String legalName) {
		org.oscarehr.common.dao.DemographicDao demographicDao = (org.oscarehr.common.dao.DemographicDao) SpringUtils.getBean("demographicDao");
		HRMDocumentToDemographicDao hrmDocumentToDemographicDao = (HRMDocumentToDemographicDao) SpringUtils.getBean("HRMDocumentToDemographicDao");
		HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");

		List<Demographic> matchingDemographicListByName = demographicDao.searchDemographic(legalName);

		List<HRMReport> allRoutedReports = new LinkedList<HRMReport>();

		for (Demographic d : matchingDemographicListByName) {
			List<HRMDocumentToDemographic> matchingHrmDocumentList = hrmDocumentToDemographicDao.findByDemographicNo(d.getDemographicNo());
			for (HRMDocumentToDemographic matchingHrmDocument : matchingHrmDocumentList) {
				HRMDocument hrmDocument = hrmDocumentDao.find(matchingHrmDocument.getHrmDocumentId());

				HRMReport hrmReport = HRMReportParser.parseReport(hrmDocument.getReportFile(), hrmDocument.getReportFileSchemaVersion());
				hrmReport.setHrmDocumentId(hrmDocument.getId());
				hrmReport.setHrmParentDocumentId(hrmDocument.getParentReport());
				allRoutedReports.add(hrmReport);
			}
		}

		return allRoutedReports;

	}


	public static void routeReportToSubClass(HRMReport report, HRMDocument document)
	{
		if(report == null) {
			logger.info("routeReportToSubClass cannot continue, report parameter is null");
			return;
		}
		
		logger.info("Routing Report To SubClass, for file:"+report.getFileLocation());
		
		HRMDocumentSubClassDao hrmDocumentSubClassDao = (HRMDocumentSubClassDao) SpringUtils.getBean("HRMDocumentSubClassDao");

		if(report.getFirstReportClass().equalsIgnoreCase(HrmDocument.REPORT_CLASS.DIAGNOSTIC_IMAGING.getValue())
				|| report.getFirstReportClass().equalsIgnoreCase(HrmDocument.REPORT_CLASS.CARDIO_RESPIRATORY.getValue()))
		{
			List<HrmObservation> subClassList = report.getObservations();

			boolean firstSubClass = true;
			
			for (HrmObservation subClass : subClassList)
			{
				HRMDocumentSubClass newSubClass = new HRMDocumentSubClass();

				newSubClass.setSubClass(subClass.getAccompanyingSubClass());
				newSubClass.setSubClassMnemonic(subClass.getAccompanyingMnemonic());
				newSubClass.setSubClassDescription(subClass.getAccompanyingDescription());
				newSubClass.setSubClassDateTime(ConversionUtils.toLegacyDateTime(subClass.getObservationDateTime()));

				if(firstSubClass)
				{
					newSubClass.setActive(true);
					firstSubClass = false;
				}
				newSubClass.setHrmDocument(document);

				hrmDocumentSubClassDao.merge(newSubClass);
			}
		}
		// There aren't subclasses on a Medical Records Report
	}

	public static Date getAppropriateDateFromReport(HRMReport report)
	{
		if(report.getFirstReportClass().equalsIgnoreCase(HrmDocument.REPORT_CLASS.DIAGNOSTIC_IMAGING.getValue())
				|| report.getFirstReportClass().equalsIgnoreCase(HrmDocument.REPORT_CLASS.CARDIO_RESPIRATORY.getValue()))
		{
			List<HrmObservation> subClassList = report.getObservations();
			if(!subClassList.isEmpty())
			{
				return ConversionUtils.toNullableLegacyDateTime(subClassList.get(0).getObservationDateTime());
			}
		}

		// Medical Records Report
		return report.getFirstReportEventTime().getTime();
	}

	public static boolean routeReportToProvider(HRMReport report, Integer reportId) {
		if(report == null) {
			logger.info("routeReportToProvider cannot continue, report parameter is null");
			return false;
		}
		
		logger.info("Routing Report to Provider, for file:"+report.getFileLocation());
		
		HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
		ProviderDao providerDao = (ProviderDao) SpringUtils.getBean("providerDao"); 

		String providerNo = report.getDeliverToUserId().substring(1); // We have to remove the first "D"
		//		String providerLastName = report.getDeliverToUserIdLastName();
		//		String providerFirstName = report.getDeliverToUserIdFirstName();

		Provider sendToProvider = providerDao.getProviderByPractitionerNo(providerNo);
		List<Provider> sendToProviderList = new LinkedList<Provider>();
		//		if (sendToProvider == null) {
		//			// Check to see if there's a match with first and last name
		//			List<Provider> potentialProviderMatchList = providerDao.getProviderLikeFirstLastName(providerFirstName, providerLastName);
		//			if (potentialProviderMatchList != null && potentialProviderMatchList.size() >= 1) {
		//				for (Provider p : potentialProviderMatchList)
		//					sendToProviderList.add(p);
		//			}
		//		} else {
		if (sendToProvider != null) {	
			sendToProviderList.add(sendToProvider);
		}
		//		}

		HRMDocument hrmDocument = hrmDocumentDao.find(reportId);
		for (Provider p : sendToProviderList) {
						
			List<HRMDocumentToProvider> existingHRMDocumentToProviders =  hrmDocumentToProviderDao.findByHrmDocumentIdAndProviderNoList(reportId, p.getProviderNo());
			
			if (existingHRMDocumentToProviders == null || existingHRMDocumentToProviders.size() == 0) {	
				HRMDocumentToProvider providerRouting = new HRMDocumentToProvider();
				providerRouting.setHrmDocument(hrmDocument);
	
				providerRouting.setProviderNo(p.getProviderNo());
				providerRouting.setSignedOff(false);
	
				hrmDocumentToProviderDao.merge(providerRouting);
			}	
		}

		return sendToProviderList.size() > 0;

	}

	public static void setDocumentParent(String reportId, String childReportId) {
		HRMDocumentDao hrmDocumentDao = (HRMDocumentDao) SpringUtils.getBean("HRMDocumentDao");
		try {
			HRMDocument childDocument = hrmDocumentDao.find(childReportId);
			childDocument.setParentReport(Integer.parseInt(reportId));

			hrmDocumentDao.merge(childDocument);
		} catch (Exception e) {
			MiscUtils.getLogger().error("Can't set HRM document parent", e);
		}
	}

	public static void routeReportToProvider(HRMDocument originalDocument, HRMReport newReport) {
		routeReportToProvider(newReport, originalDocument.getId());
	}

	public static void routeReportToProvider(String reportId, String providerNo) {
		HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
		HRMDocumentToProvider providerRouting = new HRMDocumentToProvider();
		HRMDocument hrmDocument = hrmDocumentDao.find(Integer.parseInt(reportId));

		providerRouting.setHrmDocument(hrmDocument);
		providerRouting.setProviderNo(providerNo);

		hrmDocumentToProviderDao.merge(providerRouting);

	}

	public static void signOffOnReport(String providerRoutingId, Integer signOffStatus) {
		HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
		HRMDocumentToProvider providerRouting = hrmDocumentToProviderDao.find(providerRoutingId);

		if (providerRouting != null) {
			providerRouting.setSignedOff(signOffStatus == 1);
			providerRouting.setSignedOffTimestamp(new Date());
			hrmDocumentToProviderDao.merge(providerRouting);
		}
	}

	public static void routeReportToDemographic(Integer reportId, Integer demographicNo) {
		HRMDocumentToDemographicDao hrmDocumentToDemographicDao = (HRMDocumentToDemographicDao) SpringUtils.getBean("HRMDocumentToDemographicDao");

		HRMDocumentToDemographic demographicRouting = new HRMDocumentToDemographic();
		demographicRouting.setDemographicNo(demographicNo);
		demographicRouting.setHrmDocumentId(reportId);
		demographicRouting.setTimeAssigned(new Date());

		hrmDocumentToDemographicDao.merge(demographicRouting);

	}
}
