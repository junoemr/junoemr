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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.converter.out.BaseDbToModelConverter;
import org.oscarehr.demographicImport.logger.ExportLogger;
import org.oscarehr.demographicImport.util.ExportPreferences;
import org.oscarehr.demographicImport.util.PatientExportContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class PatientExportService
{
	private static final Logger logger = Logger.getLogger(PatientExportService.class);

	@Autowired
	private AppointmentStatusCache appointmentStatusCache;

	@Autowired
	private ImporterExporterFactory importerExporterFactory;

	@Autowired
	private PatientExportAsyncService patientExportService;

	public List<GenericFile> exportDemographics(ImporterExporterFactory.EXPORTER_TYPE importType,
	                                            List<String> demographicIdList,
	                                            ExportPreferences preferences) throws Exception
	{
		PatientExportContext context = importerExporterFactory.initializeExportContext(importType, preferences, demographicIdList.size());
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
					threads.add(patientExportService.exportDemographic(exporter, demographicId));
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
			context.setComplete(true);
			BaseDbToModelConverter.clearProviderCache();
			appointmentStatusCache.clear();
		}

		return fileList;
	}
}
