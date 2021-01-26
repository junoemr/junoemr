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
package org.oscarehr.util.task;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.service.DemographicImporter;
import org.oscarehr.demographicImport.service.ImportExportWrapperService;
import org.oscarehr.demographicImport.service.ImporterExporterFactory;
import org.oscarehr.demographicImport.util.ImportCallback;
import org.oscarehr.util.task.args.BooleanArg;
import org.oscarehr.util.task.args.CommandLineArg;
import org.oscarehr.util.task.args.StringArg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class CommandLineImporter implements CommandLineTask, ImportCallback
{
	private static final Logger logger = Logger.getLogger(CommandLineImporter.class);

	@Autowired
	private ImportExportWrapperService importExportWrapperService;

	public String taskName()
	{
		return "import";
	}
	public List<CommandLineArg<?>> argsList()
	{
		return Arrays.asList(
				new StringArg("type", null, true),
				new StringArg("file-directory", null, true),
				new StringArg("document-directory", null, false),
				new StringArg("source-type", null, false),
				new StringArg("mergeStrategy", "SKIP", false),
				new BooleanArg("skipMissingDocs", false, false)
				);
	}

	public void run(Map<String, CommandLineArg<?>> args)
	{
		logger.info("init importer");

		String importType = (String) args.get("type").getValue();
		String importFileLocation = (String) args.get("file-directory").getValue();
		String importDocumentLocation = (String) args.get("document-directory").getValue();
		String importSourceStr = (String) args.get("source-type").getValue();
		Boolean skipMissingDocs = (Boolean) args.get("skipMissingDocs").getValue();
		String mergeStrategyStr = (String) args.get("mergeStrategy").getValue();

		File importFileDirectory = new File(importFileLocation);
		if(!(importFileDirectory.exists() && importFileDirectory.isDirectory()))
		{
			throw new InvalidCommandLineArgumentsException("Invalid import directory: " + importFileLocation);
		}

		// default docs directory to the file directory if missing
		if(importDocumentLocation == null)
		{
			importDocumentLocation = importFileLocation;
		}
		File importDocumentDirectory = new File(importDocumentLocation);
		if(!(importDocumentDirectory.exists() && importDocumentDirectory.isDirectory()))
		{
			throw new InvalidCommandLineArgumentsException("Invalid document directory: " + importDocumentLocation);
		}

		if(!EnumUtils.isValidEnum(DemographicImporter.MERGE_STRATEGY.class, mergeStrategyStr))
		{
			throw new InvalidCommandLineArgumentsException(importType + " is not a valid MERGE_STRATEGY enum. must be one of " +
					java.util.Arrays.asList(DemographicImporter.MERGE_STRATEGY.values()));
		}
		DemographicImporter.MERGE_STRATEGY mergeStrategy = DemographicImporter.MERGE_STRATEGY.valueOf(mergeStrategyStr);

		if(!EnumUtils.isValidEnum(ImporterExporterFactory.IMPORTER_TYPE.class, importType))
		{
			throw new InvalidCommandLineArgumentsException(importType + " is not a valid IMPORTER_TYPE enum. must be one of " +
					java.util.Arrays.asList(ImporterExporterFactory.IMPORTER_TYPE.values()));
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

		try
		{
			List<GenericFile> genericFileList = new ArrayList<>();

			File[] fileList = importFileDirectory.listFiles();
			for(File file : fileList)
			{
				// skip sub directories
				if(file.isDirectory())
				{
					logger.info("Skip Directory: " + file.getName());
					continue;
				}
				genericFileList.add(FileFactory.getExistingFile(file));
			}

			importExportWrapperService.importDemographics(
					importerType,
					importSource,
					genericFileList,
					importDocumentLocation,
					skipMissingDocs,
					mergeStrategy,
					this);
		}
		catch(Exception e)
		{
			logger.error("Error", e);
		}
	}

	private static void moveTo(GenericFile genericFile, String baseDir, String subfolder)
	{
		File failedDir = new File(baseDir, subfolder);
		try
		{
			genericFile.moveFile(failedDir);
		}
		catch(IOException e)
		{
			logger.error("IO ERROR: Failed to move " + genericFile.getName() + " to " + subfolder + " folder!", e);
		}
	}

	@Override
	public void onFileImportSuccess(GenericFile genericFile)
	{
		moveTo(genericFile, genericFile.getDirectory(), "completed");
	}

	@Override
	public void onFileImportFailure(GenericFile genericFile)
	{
		moveTo(genericFile, genericFile.getDirectory(), "failed");
	}

	@Override
	public void onImportComplete(long successCount, long failureCount)
	{
		logger.info("IMPORT PROCESS COMPLETE (" + successCount + " files imported. " + failureCount + " failures)");
	}
}