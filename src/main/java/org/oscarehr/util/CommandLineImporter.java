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
import org.oscarehr.demographicImport.service.ImportExportService;
import org.oscarehr.demographicImport.service.ImporterExporterFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import oscar.OscarProperties;

import java.io.File;
import java.io.IOException;

public class CommandLineImporter
{
	private static final Logger logger = Logger.getLogger(CommandLineImporter.class);

	private static ImportExportService importExportService;

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
		if(args == null || args.length != 5)
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
		String importFileLocation = args[2];

		File importFileDirectory = new File(importFileLocation);
		if(!(importFileDirectory.exists() && importFileDirectory.isDirectory()))
		{
			logger.error("Invalid import directory");
			return;
		}

		// flag to allow importing demographics with missing document files by skipping those records.
		boolean skipMissingDocs = Boolean.parseBoolean(args[3]);
		boolean mergeDemographics = Boolean.parseBoolean(args[4]);

		ClassPathXmlApplicationContext ctx = null;
		long importCount = 0;
		long failureCount = 0;
		long fileCounter = 0;

		try
		{
			ctx = loadSpring(propertiesFileName);
			importExportService = ctx.getBean(ImportExportService.class);

			// -------------------------------------------------------------------
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

			File[] fileList = importFileDirectory.listFiles();
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
				GenericFile importFile = FileFactory.getExistingFile(file);

				try
				{
					importExportService.importDemographic(ImporterExporterFactory.IMPORTER_TYPE.CDS_5, importFile);

					importCount++;
					moveToCompleted(importFile, importFileDirectory);
				}
				catch(Exception e)
				{
					logger.error("Failed to import " + importFile.getName(), e);
					failureCount++;
					moveToFailed(importFile, importFileDirectory);
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

//		if(skipMissingDocs)
//		{
//			long totalSkippedDocuments = importService.getMissingDocumentCount();
//			logger.info(totalSkippedDocuments + " documents failed to upload and were skipped.");
//		}
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
		context.setConfigLocations(new String[]{"/applicationContext.xml"});
		context.refresh();
		SpringUtils.beanFactory = context;

		return context;
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