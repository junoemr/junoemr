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

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.service.ImportExportService;
import org.oscarehr.demographicImport.service.ImportLogger;
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
		if(args == null || args.length != 8)
		{
			BasicConfigurator.configure();
			logger.error("Invalid argument count");
			return;
		}

		String logLocation = args[0];
		String propertiesFileName = args[1];
		String importType = args[2];
		String importFileLocation = args[3];
		String importDocumentLocation = args[4];
		String importSourceStr = args[5];
		// flag to allow importing demographics with missing document files by skipping those records.
		boolean skipMissingDocs = Boolean.parseBoolean(args[6]);
		boolean mergeDemographics = Boolean.parseBoolean(args[7]);

		if(!(new File(logLocation).exists()))
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

		File importFileDirectory = new File(importFileLocation);
		if(!(importFileDirectory.exists() && importFileDirectory.isDirectory()))
		{
			logger.error("Invalid import directory: " + importFileLocation);
			return;
		}

		File importDocumentDirectory = new File(importDocumentLocation);
		if(!(importDocumentDirectory.exists() && importDocumentDirectory.isDirectory()))
		{
			logger.error("Invalid document directory: " + importDocumentLocation);
			return;
		}

		if(!EnumUtils.isValidEnum(ImporterExporterFactory.IMPORTER_TYPE.class, importType))
		{
			logger.error(importType + " is not a valid IMPORTER_TYPE enum. must be one of " +
					java.util.Arrays.asList(ImporterExporterFactory.IMPORTER_TYPE.values()));
			return;
		}
		ImporterExporterFactory.IMPORTER_TYPE importerType = ImporterExporterFactory.IMPORTER_TYPE.valueOf(importType);

		ImporterExporterFactory.IMPORT_SOURCE importSource;
		if(EnumUtils.isValidEnum(ImporterExporterFactory.IMPORT_SOURCE.class, importSourceStr))
		{
			logger.info("Import source: " + importSourceStr);
			importSource = ImporterExporterFactory.IMPORT_SOURCE.valueOf(importSourceStr);
		}
		else
		{
			logger.warn("Unknown import source. Defaulting to UNKNOWN");
			importSource = ImporterExporterFactory.IMPORT_SOURCE.UNKNOWN;
		}

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
					logger.info("Skip Directory: " + file.getName());
					continue;
				}
				GenericFile importFile = FileFactory.getExistingFile(file);

				ImportLogger importLogger = ImporterExporterFactory.getImportLogger(importerType);
				try
				{
					importExportService.importDemographic(importerType,
							importSource,
							importLogger,
							importFile,
							importDocumentLocation,
							skipMissingDocs,
							mergeDemographics);

					importCount++;
					moveToCompleted(importFile, importFileDirectory);
				}
				catch(InvalidImportFileException e)
				{
					logger.info("Skip (invalid import file): " + importFile.getName());
				}
				catch(Exception e)
				{
					logger.error("Failed to import: " + importFile.getName(), e);
					failureCount++;
					moveToFailed(importFile, importFileDirectory);
				}
				finally
				{
					importLogger.flush();
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