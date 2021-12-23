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
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser4_1;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMObservation;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentSubClassDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToDemographicDao;
import org.oscarehr.hospitalReportManager.dao.HRMDocumentToProviderDao;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_1;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.xml.sax.SAXException;
import oscar.util.ConversionUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class HRMReportParser
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final HRMDocumentDao hrmDocumentDao = SpringUtils.getBean(HRMDocumentDao.class);
	
	public static HRMReport parseReport(String hrmReportFileLocation, String schemaVersion)
	{
		logger.info("Parsing the Report in the location:" + hrmReportFileLocation);
		
		if(hrmReportFileLocation != null)
		{
			try
			{
				GenericFile hrmXML = FileFactory.getExistingFile(hrmReportFileLocation);
				return parseReport(hrmXML, schemaVersion);
			}
			catch(SAXException | JAXBException e)
			{
				logger.error("Error parsing HRM XML " + e);
			}
			catch(IOException e)
			{
				logger.error("Error accessing HRM file", e);
			}
		}
		
		return null;
	}
	
	public static HRMReport parseReport(GenericFile hrmFile, String schemaVersion) throws IOException, SAXException, JAXBException
	{
		String hrmXML = FileUtils.getStringFromFile(hrmFile.getFileObject());
		
		if (schemaVersion.equals("4.3"))
		{
			HRMFileParser hrmParser = new HRMFileParser();
			xml.hrm.v4_3.OmdCds root = hrmParser.parse(hrmFile);
			HRMReport_4_3 report = new HRMReport_4_3(root, hrmFile.getPath(), hrmXML);
			
			return report;
		}
		else
		{
			// Legacy Implementation for HRM v4.1
			HRMFileParser4_1 hrmParser = new HRMFileParser4_1();
			xml.hrm.v4_1.OmdCds root = hrmParser.parse(hrmFile);
			HRMReport_4_1 report = new HRMReport_4_1(root, hrmFile.getPath(), hrmXML);
			
			return report;
		}
	}
	
	public static HRMReport parseRelativeLocation(String relativeLocation, String schemaVersion) throws IOException, SAXException, JAXBException
	{
		GenericFile hrmFile = FileFactory.getHrmFile(relativeLocation);
		return HRMReportParser.parseReport(hrmFile, schemaVersion);
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
		document.setNumDuplicatesReceived(0);
	}

	public static void fillDocumentHashData(HRMDocument document, GenericFile hrmFile)
	{
		String reportFileData = FileUtils.getStringFromFile(hrmFile.getFileObject());
		fillDocumentHashData(document, reportFileData);
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

		if(report.getFirstReportClass().equalsIgnoreCase(HrmDocument.ReportClass.DIAGNOSTIC_IMAGING.getValue())
				|| report.getFirstReportClass().equalsIgnoreCase(HrmDocument.ReportClass.CARDIO_RESPIRATORY.getValue()))
		{
			List<HrmObservation> subClassList = report.getObservations();

			boolean firstSubClass = true;
			
			for (HrmObservation subClass : subClassList)
			{
				HRMObservation observation = new HRMObservation();

				observation.setAccompanyingSubClassName(subClass.getAccompanyingSubClass());
				observation.setAccompanyingSubClassMnemonic(subClass.getAccompanyingMnemonic());
				observation.setAccompanyingSubClassDescription(subClass.getAccompanyingDescription());
				observation.setAccompanyingSubClassObrDate(ConversionUtils.toLegacyDateTime(subClass.getObservationDateTime()));

				if(firstSubClass)
				{
					observation.setActive(true);
					firstSubClass = false;
				}
				observation.setHrmDocument(document);

				hrmDocumentSubClassDao.merge(observation);
			}
		}
		// There aren't subclasses on a Medical Records Report
	}

	public static Date getAppropriateDateFromReport(HRMReport report)
	{
		if(report.getFirstReportClass().equalsIgnoreCase(HrmDocument.ReportClass.DIAGNOSTIC_IMAGING.getValue())
				|| report.getFirstReportClass().equalsIgnoreCase(HrmDocument.ReportClass.CARDIO_RESPIRATORY.getValue()))
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

	public static void signOffOnReport(String providerRoutingId, Integer signOffStatus) {
		HRMDocumentToProviderDao hrmDocumentToProviderDao = (HRMDocumentToProviderDao) SpringUtils.getBean("HRMDocumentToProviderDao");
		HRMDocumentToProvider providerRouting = hrmDocumentToProviderDao.find(providerRoutingId);

		if (providerRouting != null) {
			providerRouting.setSignedOff(signOffStatus == 1);
			providerRouting.setSignedOffTimestamp(new Date());
			hrmDocumentToProviderDao.merge(providerRouting);
		}
	}
}