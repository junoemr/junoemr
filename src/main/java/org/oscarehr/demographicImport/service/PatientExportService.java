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
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographicImport.converter.out.PatientRecordModelConverter;
import org.oscarehr.demographicImport.model.PatientRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class PatientExportService
{
	private static final Logger logger = Logger.getLogger(PatientExportService.class);

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private PatientRecordModelConverter patientRecordModelConverter;

	@Async
	public CompletableFuture<GenericFile> exportDemographic(DemographicExporter exporter, Integer demographicId) throws Exception
	{
		Instant instant = Instant.now();
		logger.info("Load Demographic " + demographicId);
		org.oscarehr.demographic.model.Demographic demographic = demographicDao.find(demographicId);

		PatientRecord patientRecord = patientRecordModelConverter.convert(demographic);
		instant = printDuration(instant, "Export Service: load patient model");

		logger.info("Export Demographic " + patientRecord.getDemographic().getId());
		GenericFile file = exporter.exportDemographic(patientRecord);
		instant = printDuration(instant, "Export Service: export file creation");

		return CompletableFuture.completedFuture(file);
	}

	public static Instant printDuration(Instant start, String what)
	{
		Instant now = Instant.now();
		logger.info("[DURATION] " + what + " took " + Duration.between(start, now));
		return now;
	}
}
