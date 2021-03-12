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
import org.json.JSONArray;
import org.json.JSONObject;
import org.oscarehr.common.dao.SiteDao;
import org.oscarehr.common.exception.InvalidCommandLineArgumentsException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.model.Site;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.demographicImport.converter.in.BaseModelToDbConverter;
import org.oscarehr.demographicImport.converter.out.SiteDbToModelConverter;
import org.oscarehr.demographicImport.exception.DuplicateDemographicException;
import org.oscarehr.demographicImport.exception.InvalidImportFileException;
import org.oscarehr.demographicImport.logger.ImportLogger;
import org.oscarehr.demographicImport.service.context.PatientImportContextService;
import org.oscarehr.demographicImport.transfer.ImportTransferOutbound;
import org.oscarehr.demographicImport.pref.ImportPreferences;
import org.oscarehr.demographicImport.service.context.PatientImportContext;
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
		String contextId = patientImportContextService.register(context);
		ImportLogger importLogger = context.getImportLogger();

		ImportTransferOutbound transferOutbound = new ImportTransferOutbound();

		LogDataMigration dataMigration = new LogDataMigration();
		dataMigration.setUuid(contextId);
		dataMigration.setStartDatetime(LocalDateTime.now());
		dataMigration.setTypeImport();

		JSONArray fileList = new JSONArray();
		fileList.put(importLogger.getSummaryLogFile().getName());
		fileList.put(importLogger.getEventLogFile().getName());

		JSONObject jsonData = new JSONObject();
		jsonData.put(LogDataMigration.DATA_KEY_TOTAL, importFileList.size());
		jsonData.put(LogDataMigration.DATA_KEY_FILES, fileList);
		dataMigration.setJsonData(jsonData);

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

			jsonData = dataMigration.getDataAsJson();
			jsonData.put(LogDataMigration.DATA_KEY_COMPLETE, importCount);
			jsonData.put(LogDataMigration.DATA_KEY_DUPLICATE, duplicateCount);
			jsonData.put(LogDataMigration.DATA_KEY_FAILED, failureCount);

			JSONArray demographicIdArray = new JSONArray();
			for(Integer id : demographicIds)
			{
				demographicIdArray.put(id);
			}
			jsonData.put(LogDataMigration.DATA_KEY_DEMOGRAPHICS, demographicIdArray);

			dataMigration.setJsonData(jsonData);
			dataMigration.setEndDatetime(LocalDateTime.now());
			logDataMigrationDao.merge(dataMigration);
		}
		finally
		{
			// always clear the provider cache after an import to unload resources
			BaseModelToDbConverter.clearProviderCache();
			context.markAsComplete();
			patientImportContextService.unregister(contextId);
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
