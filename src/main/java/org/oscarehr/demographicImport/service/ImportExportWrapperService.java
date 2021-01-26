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
package org.oscarehr.demographicImport.service;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.converter.in.BaseModelToDbConverter;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.logger.ImportLogger;
import org.oscarehr.demographicImport.util.ImportCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.NEVER)
public class ImportExportWrapperService
{
	private static final Logger logger = Logger.getLogger(ImportExportWrapperService.class);

	@Autowired
	private ImportExportService importExportService;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	public void importDemographics(ImporterExporterFactory.IMPORTER_TYPE importerType,
	                               ImporterExporterFactory.IMPORT_SOURCE importSource,
	                               List<GenericFile> importFileList,
	                               String documentLocation,
	                               boolean skipMissingDocs,
	                               DemographicImporter.MERGE_STRATEGY mergeStrategy,
	                               ImportCallback importCallback)
	{

		long importCount = 0;
		long failureCount = 0;
		long fileCounter = 0;

		try
		{
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");

			for(GenericFile importFile : importFileList)
			{
				fileCounter++;
				ImportLogger importLogger = importerExporterFactory.getImportLogger(importerType);
				try
				{
					importExportService.importDemographic(importerType,
							importSource,
							importLogger,
							importFile,
							documentLocation,
							skipMissingDocs,
							mergeStrategy);

					importCount++;

					if(importCallback != null)
					{
						importCallback.onFileImportSuccess(importFile);
					}
				}
				catch(InvalidImportFileException e)
				{
					logger.info("Skip (invalid import file): " + importFile.getName());
				}
				catch(Exception e)
				{
					logger.error("Failed to import: " + importFile.getName(), e);

					// clear the provider cache on failures for now so that un-persisted providers are not referenced by future lookups
					BaseModelToDbConverter.clearProviderCache();
					failureCount++;
					if(importCallback != null)
					{
						importCallback.onFileImportFailure(importFile);
					}
				}
				finally
				{
					importLogger.flush();
				}
			}
		}
		finally
		{
			// always clear the provider cache after an import to unload resources
			BaseModelToDbConverter.clearProviderCache();
		}

		if(importCallback != null)
		{
			importCallback.onImportComplete(importCount, failureCount);
		}
	}

	public void importDemographics(String importerTypeStr,
	                               String importSourceStr,
	                               List<GenericFile> importFileList,
	                               String documentLocation,
	                               boolean skipMissingDocs,
	                               DemographicImporter.MERGE_STRATEGY mergeStrategy,
	                               ImportCallback importCallback)
	{
		if(!EnumUtils.isValidEnum(ImporterExporterFactory.IMPORTER_TYPE.class, importerTypeStr))
		{
			throw new InvalidCommandLineArgumentsException(importerTypeStr + " is not a valid IMPORTER_TYPE enum. must be one of " +
					java.util.Arrays.asList(ImporterExporterFactory.IMPORTER_TYPE.values()));
		}
		ImporterExporterFactory.IMPORTER_TYPE importerType = ImporterExporterFactory.IMPORTER_TYPE.valueOf(importerTypeStr);

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

		importDemographics(importerType, importSource, importFileList, documentLocation, skipMissingDocs, mergeStrategy, importCallback);
	}
}
