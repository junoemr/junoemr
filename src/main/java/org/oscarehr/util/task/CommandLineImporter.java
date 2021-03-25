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

import org.apache.log4j.Logger;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.service.DemographicImporter;
import org.oscarehr.dataMigration.service.PatientImportWrapperService;
import org.oscarehr.util.task.args.BooleanArg;
import org.oscarehr.util.task.args.CommandLineArg;
import org.oscarehr.util.task.args.StringArg;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class CommandLineImporter extends PatientImportWrapperService implements CommandLineTask
{
	private static final Logger logger = Logger.getLogger(CommandLineImporter.class);

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
				new StringArg("merge-strategy", DemographicImporter.MERGE_STRATEGY.SKIP.name(), false),
				new BooleanArg("skip-missing-docs", false, false),
				new BooleanArg("force-skip-invalid-data", false, false),
				new StringArg("site-name", null, false)
				);
	}

	public void run(Map<String, CommandLineArg<?>> args)
	{
		logger.info("init importer");

		String importType = (String) args.get("type").getValue();
		String importFileLocation = (String) args.get("file-directory").getValue();
		String importDocumentLocation = (String) args.get("document-directory").getValue();
		String importSourceStr = (String) args.get("source-type").getValue();
		Boolean skipMissingDocs = (Boolean) args.get("skip-missing-docs").getValue();
		Boolean skipInvalidData = (Boolean) args.get("force-skip-invalid-data").getValue();
		String mergeStrategyStr = (String) args.get("merge-strategy").getValue();
		String defaultSiteName = (String) args.get("site-name").getValue();

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

		try
		{
			String processId = UUID.randomUUID().toString();
			Thread.currentThread().setName(processId);

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

			importDemographics(
					importType,
					importSourceStr,
					mergeStrategyStr,
					genericFileList,
					importDocumentLocation,
					skipMissingDocs,
					skipInvalidData,
					defaultSiteName);
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
	protected void onSuccess(GenericFile genericFile)
	{
		moveTo(genericFile, genericFile.getDirectory(), "completed");
	}

	@Override
	protected void onDuplicate(GenericFile genericFile)
	{
		moveTo(genericFile, genericFile.getDirectory(), "duplicate");
	}

	@Override
	protected void onError(GenericFile genericFile)
	{
		moveTo(genericFile, genericFile.getDirectory(), "failed");
	}
}