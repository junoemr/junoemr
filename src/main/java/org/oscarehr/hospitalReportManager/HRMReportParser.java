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
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser;
import org.oscarehr.dataMigration.parser.hrm.HRMFileParser4_1;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_1;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.util.MiscUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class HRMReportParser
{
	private static final Logger logger = MiscUtils.getLogger();
	
	public static HRMReport parseReport(String hrmReportFileLocation, String schemaVersion)
	{
		logger.info("Parsing the Report at location:" + hrmReportFileLocation);
		
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
}