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
package org.oscarehr.dataMigration.service;

import org.apache.commons.lang3.EnumUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Site;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.converter.out.SiteDbToModelConverter;
import org.oscarehr.dataMigration.exception.DuplicateDemographicException;
import org.oscarehr.dataMigration.exception.InvalidImportFileException;
import org.oscarehr.dataMigration.logger.ImportLogger;
import org.oscarehr.dataMigration.pref.ImportPreferences;
import org.oscarehr.dataMigration.service.context.PatientImportContext;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.log.dao.LogDataMigrationDao;
import org.oscarehr.log.model.LogDataMigration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional(propagation = Propagation.NEVER)
public class PatientImportWrapperService
{
	private static final OscarProperties properties = OscarProperties.getInstance();
	private static final Logger logger = Logger.getLogger(PatientImportWrapperService.class);

	@Autowired
	private PatientImportService patientImportService;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	@Autowired
	private PatientImportContextService patientImportContextService;

	@Autowired
	private SiteDao siteDao;

	@Autowired
	private SiteDbToModelConverter siteDbToModelConverter;

	@Autowired
	private LogDataMigrationDao logDataMigrationDao;

	public void importDemographics(
			ImporterExporterFactory.IMPORTER_TYPE importerType,
			ImporterExporterFactory.IMPORT_SOURCE importSource,
			DemographicImporter.MERGE_STRATEGY mergeStrategy,
			List<GenericFile> importFileList,
			String documentLocation,
			boolean skipMissingDocs,
			boolean skipInvalidData,
			String defaultSiteName) throws IOException, InterruptedException
	{
		long importCount = 0;
		long duplicateCount = 0;
		long failureCount = 0;

		ImportPreferences importPreferences = new ImportPreferences();
		importPreferences.setExternalDocumentPath(documentLocation);
		importPreferences.setImportSource(importSource);
		importPreferences.setSkipMissingDocs(skipMissingDocs);
		importPreferences.setForceSkipInvalidData(skipInvalidData);
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
		String contextId = patientImportContextService.register(context);
		ImportLogger importLogger = context.getImportLogger();

		LogDataMigration dataMigration = new LogDataMigration();
		dataMigration.setUuid(contextId);
		dataMigration.setStartDatetime(LocalDateTime.now());
		dataMigration.setTypeImport();

		LogDataMigration.MigrationImportData migrationImportData = new LogDataMigration.MigrationImportData();
		migrationImportData.setLogFiles(Arrays.asList(importLogger.getSummaryLogFile().getName(), importLogger.getEventLogFile().getName()));
		migrationImportData.setTotal(importFileList.size());
		dataMigration.setData(migrationImportData);

		try
		{
			logger.info("BEGIN DEMOGRAPHIC IMPORT PROCESS ...");
			logDataMigrationDao.persist(dataMigration);
			importLogger.logSummaryHeader();

			List<Integer> demographicIds = new ArrayList<>(importFileList.size());
			for(GenericFile importFile : importFileList)
			{
				try
				{
					context.addProcessIdentifier(importFile.getName());
					Demographic demographic = patientImportService.importDemographic(
							importFile,
							context,
							mergeStrategy);
					demographicIds.add(demographic.getId());

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
				catch(Error e)
				{
					// Error is a subclass of Throwable that indicates serious problems that a reasonable application should not try to catch.
					// includes things like OutOfMemory errors etc.
					// in this case we don't want to attempt importing the remaining files, and we don't mark them as failed/complete etc.

					logger.fatal("Importer encountered a critical error while processing file: " + importFile.getName(), e);
					throw e;
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

			migrationImportData.setComplete(importCount);
			migrationImportData.setDuplicates(duplicateCount);
			migrationImportData.setFailures(failureCount);
			migrationImportData.setDemographics(demographicIds);
			dataMigration.setData(migrationImportData);
			dataMigration.setEndDatetime(LocalDateTime.now());

			logDataMigrationDao.merge(dataMigration);
		}
		finally
		{
			// always clear the provider cache after an import to unload resources
			BaseModelToDbConverter.clearProviderCache();
			patientImportContextService.unregister(contextId);
			context.clean();
		}

		onImportComplete(importCount, duplicateCount, failureCount);
	}

	public void importDemographics(
			String importerTypeStr,
			String importSourceStr,
			String mergeStrategyStr,
			List<GenericFile> importFileList,
			String documentLocation,
			boolean skipMissingDocs,
			boolean skipInvalidData,
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

		importDemographics(importerType, importSource, mergeStrategy, importFileList, documentLocation, skipMissingDocs, skipInvalidData, defaultSiteName);
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
