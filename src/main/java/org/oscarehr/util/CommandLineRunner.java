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

import ca.uhn.hl7v2.HL7Exception;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.demographicImport.service.CoPDPreProcessorService;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommandLineRunner
{
	private static final Logger logger = Logger.getLogger(CommandLineRunner.class);

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

		if(args == null || args.length != 4)
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


		ClassPathXmlApplicationContext ctx = null;
		long importCount = 0;
		long failureCount = 0;
		long fileCounter = 0;

		try
		{
			// load properties from file
			OscarProperties properties = OscarProperties.getInstance();
			// This has been used to look in the users home directory that started tomcat
			properties.readFromFile(propertiesFileName);
			logger.info("loading properties from " + propertiesFileName);

			ctx = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			// initialize spring bean factory for old style access
			SpringUtils.beanFactory = ctx.getBeanFactory();

			coPDImportService = ctx.getBean(CoPDImportService.class);
			coPDPreProcessorService = ctx.getBean(CoPDPreProcessorService.class);

			// -------------------------------------------------------------------
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

			File[] fileList = copdDirectory.listFiles();
			for(File file : fileList)
			{
				fileCounter++;
				logger.info("Process file " + fileCounter + "/" + fileList.length);

				// skip sub directories
				if(file.isDirectory())
				{
					logger.info("Skip Directory");
					continue;
				}
				GenericFile copdFile = FileFactory.getExistingFile(file);
				if(copdFile instanceof XMLFile)
				{
					String fileString = coPDPreProcessorService.getFileString(copdFile);
					if(coPDPreProcessorService.looksLikeCoPDFormat(fileString))
					{
						logger.info("Import from file: " + copdFile.getName());

						try
						{
							importFileString(fileString, copdDocumentLocation);
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
		logger.info("IMPORT PROCESS COMPLETE (" + importCount + " files imported. " + failureCount + " failures)");
	}

	private static void importFileString(String fileString, String documentDirectory) throws HL7Exception, IOException, InterruptedException
	{
		List<String> messageList = coPDPreProcessorService.separateMessages(fileString);
		for(String message : messageList)
		{
			message = coPDPreProcessorService.preProcessMessage(message);
			coPDImportService.importFromHl7Message(message, documentDirectory);
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