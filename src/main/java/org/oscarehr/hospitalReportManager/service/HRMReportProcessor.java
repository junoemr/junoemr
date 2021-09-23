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

package org.oscarehr.hospitalReportManager.service;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Provider;
import org.oscarehr.dataMigration.converter.in.hrm.HrmDocumentModelToDbConverter;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportDemographicMatcher;
import org.oscarehr.dataMigration.mapper.hrm.in.HRMReportImportMapper;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.model.HRMFetchResults;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class HRMReportProcessor
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String SCHEMA_VERSION = "4.3";
	private static final String DECRYPTION_KEY = OscarProperties.getInstance().getProperty("omd.hrm.decryption_key");
	
	@Autowired
	private HRMReportImportMapper reportMapper;
	
	@Autowired
	private HRMReportDemographicMatcher demoMatcher;
	
	@Autowired
	private HrmDocumentModelToDbConverter hrmDocumentModelToDbConverter;
	
	@Autowired
	private HRMService hrmService;
	
	/**
	 * Process the list of files:  Decrypt (optional), parse, and route.
	 * @param unprocessedFiles files to process
	 * @param doDecrypt decrypt file prior to parsing and routing
	 * @param results storage object, contains result of the processing operation.
	 */
	public void processHRMFiles(List<GenericFile> unprocessedFiles, boolean doDecrypt, HRMFetchResults results)
	{
		LocalDate today = LocalDate.now();
		
		AtomicInteger processedFiles = new AtomicInteger(0);
		
		if (unprocessedFiles != null && !unprocessedFiles.isEmpty())
		{
			for (GenericFile unprocessedFile : unprocessedFiles)
			{
				GenericFile workingFile = unprocessedFile;
				
				if (doDecrypt)
				{
					 workingFile = decryptFile(unprocessedFile, today);
				}
				
				boolean success = processHRMFile_43(workingFile);
				if (success)
				{
					processedFiles.getAndIncrement();
				}
			}
		}
		
		results.setProcessingSuccess(unprocessedFiles != null && processedFiles.get() == unprocessedFiles.size());
		results.setReportsProcessed(processedFiles.get());
		results.setEndTime(LocalDateTime.now());
	}
	
	public boolean processHRMFile_43(GenericFile hrmFile)
	{
		try
		{
			HRMReport hrmReport = HRMReportParser.parseReport(hrmFile, SCHEMA_VERSION);
			HrmDocument model = reportMapper.importToJuno((HRMReport_4_3) hrmReport);
			
			HRMDocument hrmDocument = hrmDocumentModelToDbConverter.convert(model);
			HRMReportParser.fillDocumentHashData(hrmDocument, hrmFile);
			
			if (!hrmService.isDuplicateReport(hrmDocument))
			{
				List<Demographic> demographicMatches = demoMatcher.importToJuno((HRMReport_4_3) hrmReport);
				Demographic demographicToLink = null;
				
				if (demographicMatches.size() > 1)
				{
					logger.info(String.format("Multiple demographics matched for HRM file, leaving unlinked: %s", hrmFile.getPath()));
				}
				if (demographicMatches.size() == 1)
				{
					demographicToLink = demographicMatches.get(0);
				}
				if (demographicMatches.size() == 0)
				{
					logger.info(String.format("No demographics matched for HRM file: %s", hrmFile.getPath()));
				}
				
				// sending null here is ok, will not associate with a demographic if one can't be found
				hrmService.persistAndLinkHRMDocument(hrmDocument, demographicToLink);
			}
			else
			{
				logger.info(String.format("Duplicate report hash (%s) for file: %s", hrmDocument.getReportHash(), hrmDocument.getReportFile()));
				hrmService.handleDuplicate(hrmDocument);
			}
			
			return true;
		}
		catch (Exception e)
		{
			logger.error(String.format("Could not process HRM file: %s", hrmFile.getPath()), e);
			
			LogAction.addLogEntry(Provider.SYSTEM_PROVIDER_NO, null, LogConst.ACTION_PROCESS, LogConst.CON_HRM, LogConst.STATUS_FAILURE, hrmFile.getName(), null,  e.getMessage());
			return false;
		}
	}
	
	/**
	 * Process a file by decrypting it if necessary, and moving it to the correct location on disk
	 *
	 * @param encryptedFile temporary file
	 * @param dateSubDirectory date subdirectory to put the file in
	 *
	 * @return processed file
	 */
	private GenericFile decryptFile(GenericFile encryptedFile, LocalDate dateSubDirectory)
	{
		try
		{
			String plainText = decryptContents(encryptedFile);
			GenericFile hrmFile = FileFactory.createHRMFile(encryptedFile.getName().replaceAll("_encrypted", ""), dateSubDirectory);
			
			FileOutputStream stream = new FileOutputStream(hrmFile.getFileObject());
			stream.write(plainText.getBytes(StandardCharsets.UTF_8));
			
			return hrmFile;
		}
		catch (Exception e)
		{
			logger.error("Could not decrypt file: " + encryptedFile.getPath(), e);
			LogAction.addLogEntry(Provider.SYSTEM_PROVIDER_NO, null, LogConst.ACTION_PROCESS, LogConst.CON_HRM, LogConst.STATUS_FAILURE, encryptedFile.getName(), null,  "Decryption error");
			return null;
		}
	}
	
	/**
	 * Decrypt the contents of the HRM file
	 * @param encryptedFile encrypted file
	 * @return plain text string
	 * @throws Exception decryption failed
	 */
	private String decryptContents(GenericFile encryptedFile) throws Exception
	{
		try (FileInputStream inputStream = new FileInputStream(encryptedFile.getFileObject()))
		{
			byte[] buffer = new byte[(int) encryptedFile.getFileObject().length()];
			inputStream.read(buffer);
			
			Hex hex = new Hex(StandardCharsets.UTF_8.toString());
			byte[] keyBytes = hex.decode(DECRYPTION_KEY.getBytes(StandardCharsets.UTF_8));
			
			SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");;
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding", "SunJCE");
			cipher.init(Cipher.DECRYPT_MODE, key);
			
			String plaintext = new String(cipher.doFinal(buffer));
			return plaintext;
		}
	}
}
