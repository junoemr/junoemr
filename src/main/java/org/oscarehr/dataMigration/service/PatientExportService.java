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

import org.json.JSONObject;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.ZIPFile;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.logger.ExportLogger;
import org.oscarehr.dataMigration.pref.ExportPreferences;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.oscarehr.log.dao.LogDataMigrationDao;
import org.oscarehr.log.model.LogDataMigration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.oscarReport.data.DemographicSetManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PatientExportService
{
	@Autowired
	private AppointmentStatusCache appointmentStatusCache;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	@Autowired
	private PatientExportAsyncService patientExportService;

	@Autowired
	private LogDataMigrationDao logDataMigrationDao;

	@Autowired
	private PatientExportContextService patientExportContextService;

	@Deprecated
	/**
	 * @deprecated for legacy UI use only
	 */
	public List<GenericFile> exportDemographics(ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                            List<String> demographicIdList,
	                                            ExportPreferences preferences) throws Exception
	{
		PatientExportContext context = importerExporterFactory.initializeExportContext(exportType, preferences, demographicIdList.size());
		String contextId = patientExportContextService.register(context);
		List<GenericFile> exportFiles;
		try
		{
			LogDataMigration dataMigration = new LogDataMigration();
			dataMigration.setUuid(contextId);
			dataMigration.setStartDatetime(LocalDateTime.now());
			dataMigration.setTypeExport();
			logDataMigrationDao.persist(dataMigration);

			exportFiles = exportDemographics(context, demographicIdList, preferences);

			dataMigration.setEndDatetime(LocalDateTime.now());
			logDataMigrationDao.merge(dataMigration);
		}
		finally
		{
			patientExportContextService.unregister(contextId);
		}
		return exportFiles;
	}

	public ZIPFile exportDemographicsToZip(String patientSet,
	                                       ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                       ExportPreferences preferences) throws Exception
	{
		List<String> demographicIdList = new DemographicSetManager().getDemographicSet(patientSet);
		PatientExportContext context = importerExporterFactory.initializeExportContext(exportType, preferences, demographicIdList.size());
		String contextId = patientExportContextService.register(context);
		ZIPFile zipFile;
		try
		{
			LogDataMigration dataMigration = new LogDataMigration();
			dataMigration.setUuid(contextId);
			dataMigration.setStartDatetime(LocalDateTime.now());
			dataMigration.setTypeExport();
			dataMigration.setJsonData(new JSONObject().put(LogDataMigration.DATA_KEY_PATIENT_SET, patientSet));
			logDataMigrationDao.persist(dataMigration);

			List<GenericFile> exportFiles = exportDemographics(context, demographicIdList, preferences);
			zipFile = FileFactory.packageZipFile(exportFiles, true);
			zipFile.moveToLogExport(contextId);

			dataMigration.setEndDatetime(LocalDateTime.now());
			dataMigration.setJsonData(dataMigration.getDataAsJson().put(LogDataMigration.DATA_KEY_FILE, zipFile.getName()));
			logDataMigrationDao.merge(dataMigration);
		}
		finally
		{
			patientExportContextService.unregister(contextId);
		}
		return zipFile;
	}

	private List<GenericFile> exportDemographics(PatientExportContext context,
	                                             List<String> demographicIdList,
	                                             ExportPreferences preferences) throws Exception
	{
		ExportLogger exportLogger = context.getExportLogger();
		DemographicExporter exporter = context.getExporter();

		exportLogger.logSummaryHeader();
		List<GenericFile> fileList = new ArrayList<>();

		try
		{
			int threadCount = preferences.getThreadCount();
			if(threadCount < 1)
			{
				threadCount = 1;
			}

			// break export tasks into threads (one thread per demographic)
			for(int i = 0; i < demographicIdList.size(); i += threadCount)
			{
				ArrayList<CompletableFuture<GenericFile>> threads = new ArrayList<>(threadCount);
				for(int j = 0; j < threadCount && i+j < demographicIdList.size(); j++)
				{
					Integer demographicId = Integer.parseInt(demographicIdList.get(i + j));
					threads.add(patientExportService.exportDemographic(exporter, context, demographicId));
				}
				CompletableFuture.allOf(threads.toArray(new CompletableFuture<?>[0])).join();

				for(CompletableFuture<?> thread : threads)
				{
					fileList.add((GenericFile) thread.get());
				}
			}
			exportLogger.logSummaryFooter();
			fileList.addAll(exporter.getAdditionalFiles(preferences));
		}
		finally
		{
			BaseDbToModelConverter.clearProviderCache();
			appointmentStatusCache.clear();
		}

		return fileList;
	}
}
