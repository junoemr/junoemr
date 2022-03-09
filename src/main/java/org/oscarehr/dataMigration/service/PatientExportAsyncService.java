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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.dataMigration.converter.out.PatientRecordModelConverter;
import org.oscarehr.dataMigration.model.PatientRecord;
import org.oscarehr.dataMigration.service.context.PatientExportContext;
import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.log.LogAction;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PatientExportAsyncService
{
	private static final Logger logger = Logger.getLogger(PatientExportAsyncService.class);

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private PatientRecordModelConverter patientRecordModelConverter;

	@Autowired
	private PatientExportContextService patientExportContextService;

	@Async
	public CompletableFuture<GenericFile> exportDemographic(DemographicExporter exporter, PatientExportContext patientExportContext, Integer demographicId) throws Exception
	{
		Instant instant = Instant.now();
		GenericFile file;
		patientExportContextService.register(patientExportContext); // need to register this thread with the given context
		try
		{
			patientExportContext.addProcessIdentifier(String.valueOf(demographicId));
			logger.info("Load Demographic " + demographicId);
			org.oscarehr.demographic.entity.Demographic demographic = demographicDao.find(demographicId);

			PatientRecord patientRecord = patientRecordModelConverter.convert(demographic);
			instant = LogAction.printDuration(instant, "[" + demographicId + "] Export Service: load patient model");

			logger.info("Export Demographic " + patientRecord.getDemographic().getId());
			file = exporter.exportDemographic(patientRecord);
			instant = LogAction.printDuration(instant, "[" + demographicId + "] Export Service: export file creation");
			patientExportContext.incrementProcessed();
		}
		finally
		{
			patientExportContextService.unregister();
		}
		return CompletableFuture.completedFuture(file);
	}
}
