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
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographicImport.converter.in.BaseModelToDbConverter;
import org.oscarehr.demographicImport.converter.out.SiteDbToModelConverter;
import org.oscarehr.demographicImport.exception.DuplicateDemographicException;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.logger.ImportLogger;
import org.oscarehr.demographicImport.transfer.ImportTransferOutbound;
import org.oscarehr.demographicImport.pref.ImportPreferences;
import org.oscarehr.demographicImport.service.context.PatientImportContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import java.io.IOException;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NEVER)
public class ImportWrapperService
{
	private static final OscarProperties properties = OscarProperties.getInstance();
	private static final Logger logger = Logger.getLogger(ImportWrapperService.class);

	@Autowired
	private PatientImportService patientImportService;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	@Autowired
	private SiteDao siteDao;

	@Autowired
	private SiteDbToModelConverter siteDbToModelConverter;

	public ImportTransferOutbound importDemographics(
			ImporterExporterFactory.IMPORTER_TYPE importerType,
			ImporterExporterFactory.IMPORT_SOURCE importSource,
			DemographicImporter.MERGE_STRATEGY mergeStrategy,
			List<GenericFile> importFileList,
			String documentLocation,
			boolean skipMissingDocs,
			String defaultSiteName) throws IOException, InterruptedException
	{
		long importCount = 0;
		long duplicateCount = 0;
		long failureCount = 0;

		ImportPreferences importPreferences = new ImportPreferences();
		importPreferences.setExternalDocumentPath(documentLocation);
		importPreferences.setImportSource(importSource);
		importPreferences.setSkipMissingDocs(skipMissingDocs);
		importPreferences.setThreadCount(1);
		if(properties.isMultisiteEnabled())
		{
			Site site = siteDao.findByName(defaultSiteName);
			if(site == null)
			{
				throw new RuntimeException("No site exists with name '" + defaultSiteName + "'");
			}
			importPreferences.setDefaultSite(siteDbToModelConverter.convert(site));
		}

		PatientImportContext context = importerExporterFactory.initializeImportContext(importerType, importPreferences, importFileList.size());
		ImportLogger importLogger = context.getImportLogger();

		ImportTransferOutbound transferOutbound = new ImportTransferOutbound();

		try
		{
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");
			importLogger.logSummaryHeader();

			for(GenericFile importFile : importFileList)
			{
				try
				{
					context.addProcessIdentifier(importFile.getName());
					patientImportService.importDemographic(
							importFile,
							context,
							mergeStrategy);

					importCount++;
					onSuccess(importFile);
				}
				catch(InvalidImportFileException e)
				{
					importLogger.logEvent(importFile.getName() + ": Skipped (invalid import file)");
				}
				catch(DuplicateDemographicException e)
				{
					importLogger.logEvent(importFile.getName() + ": Skipped (" + e.getMessage() + ")");
					duplicateCount++;
					onDuplicate(importFile);
				}
				catch(Exception e)
				{
					importLogger.logEvent(importFile.getName() + ": Failed to import");
					logger.error("Failed to import: " + importFile.getName(), e);

					// clear the provider cache on failures for now so that un-persisted providers are not referenced by future lookups
					BaseModelToDbConverter.clearProviderCache();
					failureCount++;
					onError(importFile);
				}
				finally
				{
					importLogger.flush();
					context.incrementProcessed();
				}
			}
			importLogger.logSummaryFooter();
		}
		finally
		{
			// always clear the provider cache after an import to unload resources
			BaseModelToDbConverter.clearProviderCache();
			context.markAsComplete();
		}

		onImportComplete(importCount, duplicateCount, failureCount);

		transferOutbound.setSuccessCount(importCount);
		transferOutbound.setDuplicateCount(duplicateCount);
		transferOutbound.setFailureCount(failureCount);
		transferOutbound.setMessages(importLogger.getMessages());
		transferOutbound.setLogFiles(importLogger.getEventLogFile(), importLogger.getSummaryLogFile());

		return transferOutbound;
	}

	public ImportTransferOutbound importDemographics(
			String importerTypeStr,
			String importSourceStr,
			String mergeStrategyStr,
			List<GenericFile> importFileList,
			String documentLocation,
			boolean skipMissingDocs,
			String defaultSiteName) throws IOException, InterruptedException
	{
		if(!EnumUtils.isValidEnum(ImporterExporterFactory.IMPORTER_TYPE.class, importerTypeStr))
		{
			throw new InvalidCommandLineArgumentsException(importerTypeStr + " is not a valid IMPORTER_TYPE enum. must be one of " +
					java.util.Arrays.asList(ImporterExporterFactory.IMPORTER_TYPE.values()));
		}
		ImporterExporterFactory.IMPORTER_TYPE importerType = ImporterExporterFactory.IMPORTER_TYPE.valueOf(importerTypeStr);

		if(!EnumUtils.isValidEnum(DemographicImporter.MERGE_STRATEGY.class, mergeStrategyStr))
		{
			throw new InvalidCommandLineArgumentsException(mergeStrategyStr + " is not a valid MERGE_STRATEGY enum. must be one of " +
					java.util.Arrays.asList(DemographicImporter.MERGE_STRATEGY.values()));
		}
		DemographicImporter.MERGE_STRATEGY mergeStrategy = DemographicImporter.MERGE_STRATEGY.valueOf(mergeStrategyStr);

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

		return importDemographics(importerType, importSource, mergeStrategy, importFileList, documentLocation, skipMissingDocs, defaultSiteName);
	}

	protected void onSuccess(GenericFile genericFile)
	{
		// no-op
	}

	protected void onDuplicate(GenericFile genericFile)
	{
		// no-op
	}

	protected void onError(GenericFile genericFile)
	{
		// no-op
	}

	protected void onImportComplete(long importCount, long duplicateCount, long failureCount)
	{
		logger.info("IMPORT PROCESS COMPLETE (" + importCount + " files imported. " + failureCount + " failures. " + duplicateCount + " duplicates)");
	}
}
