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
package org.oscarehr.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.service.CoPDMessageStream;
import org.oscarehr.dataMigration.service.CoPDPreProcessorService;
import org.oscarehr.dataMigration.transfer.CoPDRecordData;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class CopdCommandLineImporter
{
	private static final Logger logger = Logger.getLogger(CopdCommandLineImporter.class);

	private static CoPDImportService coPDImportService;
	private static CoPDPreProcessorService coPDPreProcessorService;

	/**
	 * Run this in the WEB-INF folder in one of 2 ways:
	 *
	 * deployed: in ~tomcat/webapps/context_path/WEB-INF
	 * un-deployed: in [code_source]/target/oscar-14.0.0-SNAPSHOT/WEB-INF
	 * java -cp "classes/:lib/*:/usr/java/apache-tomcat/lib/*" org.oscarehr.util.CommandLineRunner
	 *   [logger_config.properties] [oscar_config.properties] [topd_files_directory] [topd_documents_directory]
	 */
	public static void main (String [] args)
	{
		if(args == null || args.length != 7)
		{
			BasicConfigurator.configure();
			logger.error("Invalid argument count");
			return;
		}
		else if(!(new File(args[0]).exists()))
		{
			BasicConfigurator.configure();
			logger.warn("Invalid log configuration file. Default logger initialized");
		}
		else
		{
			// set up the logger
			PropertyConfigurator.configure(args[0]);
			logger.info("Log properties loaded success");
		}

		String propertiesFileName = args[1];
		String copdFileLocation = args[2];
		String copdDocumentLocation = args[3];

		File copdDirectory = new File(copdFileLocation);
		File documentDirectory = new File(copdDocumentLocation);

		if(!(copdDirectory.exists() && copdDirectory.isDirectory()))
		{
			logger.error("Invalid CoPD directory");
			return;
		}
		if(!(documentDirectory.exists() && documentDirectory.isDirectory()))
		{
			logger.error("Invalid document directory");
			return;
		}

		String importSourceStr = args[4];
		CoPDImportService.IMPORT_SOURCE importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;

		try
		{
			logger.info("Import source: " + importSourceStr);
			importSource = CoPDImportService.IMPORT_SOURCE.valueOf(importSourceStr);
		}
		catch(IllegalArgumentException e)
		{
			logger.error("Unknown import source. Defaulting to UNKNOWN");
			importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;
		}

		// flag to allow importing demographics with missing document files by skipping those records.
		boolean skipMissingDocs = Boolean.parseBoolean(args[5]);
		boolean mergeDemographics = Boolean.parseBoolean(args[6]);

		ClassPathXmlApplicationContext ctx = null;
		long importCount = 0;
		long failureCount = 0;
		long fileCounter = 0;

		try
		{
			ctx = loadSpring(propertiesFileName);

			coPDImportService = ctx.getBean(CoPDImportService.class);
			coPDPreProcessorService = ctx.getBean(CoPDPreProcessorService.class);

			// -------------------------------------------------------------------
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");
			logger.info("DATA TYPE: " + importSource.name());

			File[] fileList = copdDirectory.listFiles();
			for(File file : fileList)
			{
				fileCounter++;
				logger.info("==== Process file " + fileCounter + "/" + fileList.length + " ====");

				// skip sub directories
				if(file.isDirectory())
				{
					logger.info("Skip Directory");
					continue;
				}
				GenericFile copdFile = FileFactory.getExistingFile(file);
				if(copdFile instanceof XMLFile)
				{
					if(coPDPreProcessorService.looksLikeCoPDFormat(copdFile))
					{
						logger.info("Import from file: " + copdFile.getName());

						try
						{
							importFileMessages(new CoPDMessageStream(copdFile), copdDocumentLocation, importSource, skipMissingDocs, mergeDemographics);
							importCount++;
							moveToCompleted(copdFile, copdDirectory);
						}
						catch(Exception e)
						{
							logger.error("Failed to import " + copdFile.getName(), e);
							failureCount++;
							moveToFailed(copdFile, copdDirectory);
						}
					}
					else
					{
						logger.warn(copdFile.getName() + " does not look like a valid import xml file.");
					}
				}
			}
		}
		catch(Exception e)
		{
			logger.error("Unknown Error", e);
		}
		finally
		{
			if(ctx != null)
			{
				ctx.close();
			}
		}

		if(skipMissingDocs)
		{
			long totalSkippedDocuments = coPDImportService.getMissingDocumentCount();
			logger.info(totalSkippedDocuments + " documents failed to upload and were skipped.");
		}
		logger.info("IMPORT PROCESS COMPLETE (" + importCount + " files imported. " + failureCount + " failures)");
	}

	/**
	 * load spring beans.
	 * @param propertiesFileName the name of the oscar properties file to use during loading
	 * @throws IOException
	 */
	public static ClassPathXmlApplicationContext loadSpring(String propertiesFileName) throws IOException{
		// load properties from file
		OscarProperties properties = OscarProperties.getInstance();
		// This has been used to look in the users home directory that started tomcat
		properties.readFromFile(propertiesFileName);
		logger.info("loading properties from " + propertiesFileName);

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
//		context.setConfigLocations(new String[]{"/applicationContext.xml","/applicationContextBORN.xml"});
		context.setConfigLocations(new String[]{"/applicationContext.xml"});
		context.refresh();
		SpringUtils.beanFactory = context;

		return context;
	}


	private static void importFileMessages(CoPDMessageStream messageStream, String documentDirectory, CoPDImportService.IMPORT_SOURCE importSource, boolean skipMissingDocs, boolean mergeDemographics)
			throws Exception
	{
		boolean hasFailure = false;
		int failureCount = 0;
		int messageCount = 0;

		String message;
		while (!(message = messageStream.getNextMessage()).isEmpty())
		{
			CoPDRecordData recordData = new CoPDRecordData();
			try
			{
				messageCount += 1;
				Instant startPreProcess = Instant.now();
				message = coPDPreProcessorService.preProcessMessage(message, importSource, recordData);
				Instant startImport = Instant.now();
				logger.info("[DURATION] Pre-Processing took " + Duration.between(startPreProcess, startImport));

				coPDImportService.importFromHl7Message(message, documentDirectory, importSource, recordData, skipMissingDocs, mergeDemographics);
				Instant endImport = Instant.now();
				logger.info("[DURATION] Import process took " + Duration.between(startImport, endImport));
			}
			catch (Exception e)
			{
				logger.error("failed to import message: " + messageCount + "\n With error:", e);
				hasFailure = true;
				failureCount += 1;
			}
			finally
			{
				//TODO - potentially print this to it's own log file
				recordData.print();
			}
		}

		if (hasFailure)
		{
			throw new RuntimeException("[" + failureCount + "] messages failed to import");
		}
	}

	private static void moveToFailed(GenericFile genericFile, File baseDir)
	{
		File failedDir = new File(baseDir, "failed");
		try
		{
			genericFile.moveFile(failedDir);
		}
		catch(IOException e)
		{
			logger.error("IO ERROR: Failed to move " + genericFile.getName() + " to failed folder!", e);
		}
	}

	private static void moveToCompleted(GenericFile genericFile, File baseDir)
	{
		File failedDir = new File(baseDir, "completed");
		try
		{
			genericFile.moveFile(failedDir);
		}
		catch(IOException e)
		{
			logger.error("IO ERROR: Failed to move " + genericFile.getName() + " to completed folder!", e);
		}
	}
}