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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
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

	public List<GenericFile> exportDemographicsToList(String patientSet,
	                                                  ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                                  ExportPreferences preferences) throws Exception
	{
		List<String> demographicIdList = new DemographicSetManager().getDemographicSet(patientSet);
		return exportDemographicsToList(patientSet, exportType, demographicIdList, preferences);
	}

	public List<GenericFile> exportDemographicsToList(List<String> demographicIdList,
	                                                  ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                                  ExportPreferences preferences) throws Exception
	{
		return exportDemographicsToList(null, exportType, demographicIdList, preferences);
	}

	public ZIPFile exportDemographicsToZip(String patientSet,
	                                       ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                       ExportPreferences preferences) throws Exception
	{
		List<String> demographicIdList = new DemographicSetManager().getDemographicSet(patientSet);
		return exportDemographicsToZip(patientSet, demographicIdList, exportType, preferences);
	}

	public ZIPFile exportDemographicsToZip(List<String> demographicIdList,
	                                       ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                       ExportPreferences preferences) throws Exception
	{
		return exportDemographicsToZip(null, demographicIdList, exportType, preferences);
	}



	private List<GenericFile> exportDemographicsToList(String patientSet,
	                                                  ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                                  List<String> demographicIdList,
	                                                  ExportPreferences preferences) throws Exception
	{
		ExportResultHandler<List<GenericFile>> resultHandler = new ExportResultHandler<List<GenericFile>>()
		{
			private List<GenericFile> exportFiles = null;

			@Override
			public void handleExportResults(List<GenericFile> exportFiles, String contextId) throws IOException
			{
				// need to move the files out of the temp directory before context cleanup
				String exportDirectory = patientExportContextService.getContext().getExportPreferences().getExportDirectory();
				for(GenericFile exportFile : exportFiles)
				{
					if(exportDirectory != null)
					{
						exportFile.moveFile(exportDirectory);
					}
					else
					{
						exportFile.moveToLogExport(contextId);
					}
				}
				this.exportFiles = exportFiles;
			}

			@Override
			public String getFilename()
			{
				return null;
			}

			@Override
			public List<GenericFile> getResults()
			{
				return exportFiles;
			}
		};

		exportDemographics(patientSet, demographicIdList, resultHandler, exportType, preferences);
		return resultHandler.getResults();
	}

	private ZIPFile exportDemographicsToZip(String patientSet,
	                                        List<String> demographicIdList,
	                                        ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                        ExportPreferences preferences) throws Exception
	{
		ExportResultHandler<ZIPFile> resultHandler = new ExportResultHandler<ZIPFile>()
		{
			private ZIPFile zipFile = null;

			@Override
			public void handleExportResults(List<GenericFile> exportFiles, String contextId) throws IOException
			{
				zipFile = FileFactory.packageZipFile(exportFiles, true);
				String exportDirectory = patientExportContextService.getContext().getExportPreferences().getExportDirectory();

				if(exportDirectory != null)
				{
					zipFile.moveFile(exportDirectory);
				}
				else
				{
					zipFile.moveToLogExport(contextId);
				}
			}

			@Override
			public String getFilename()
			{
				return zipFile.getName();
			}

			@Override
			public ZIPFile getResults()
			{
				return zipFile;
			}
		};

		exportDemographics(patientSet, demographicIdList, resultHandler, exportType, preferences);
		return resultHandler.getResults();
	}

	private void exportDemographics(String patientSet,
	                                List<String> demographicIdList,
	                                ExportResultHandler<?> resultHandler,
	                                ImporterExporterFactory.EXPORTER_TYPE exportType,
	                                ExportPreferences preferences) throws Exception
	{
		PatientExportContext context = importerExporterFactory.initializeExportContext(exportType, preferences, demographicIdList.size());
		ExportLogger exportLogger = context.getExportLogger();
		String contextId = patientExportContextService.register(context);
		try
		{
			LogDataMigration dataMigration = new LogDataMigration();
			dataMigration.setUuid(contextId);
			dataMigration.setStartDatetime(LocalDateTime.now());
			dataMigration.setTypeExport();

			LogDataMigration.MigrationExportData migrationExportData = new LogDataMigration.MigrationExportData();
			migrationExportData.setPatientSet(patientSet);
			migrationExportData.setLogFiles(Arrays.asList(exportLogger.getSummaryLogFile().getName(), exportLogger.getEventLogFile().getName()));
			dataMigration.setData(migrationExportData);
			logDataMigrationDao.persist(dataMigration);

			resultHandler.handleExportResults(exportDemographics(context, demographicIdList, preferences), contextId);

			dataMigration.setEndDatetime(LocalDateTime.now());
			migrationExportData.setFile(resultHandler.getFilename());
			dataMigration.setData(migrationExportData);
			logDataMigrationDao.merge(dataMigration);
		}
		finally
		{
			context.clean();
			patientExportContextService.unregister(contextId);
		}
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

	private interface ExportResultHandler<T>
	{
		void handleExportResults(List<GenericFile> exportFiles, String contextId) throws IOException;
		String getFilename();
		T getResults();
	}
}
