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

import org.oscarehr.dataMigration.service.context.PatientExportContextService;
import org.oscarehr.dataMigration.service.context.PatientImportContextService;
import org.oscarehr.dataMigration.service.context.PollableContext;
import org.oscarehr.log.dao.LogDataMigrationDao;
import org.oscarehr.log.model.LogDataMigration;
import org.oscarehr.ws.rest.transfer.common.ProgressBarPollingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DataMigrationService
{
	@Autowired
	private LogDataMigrationDao logDataMigrationDao;

	@Autowired
	private PatientExportContextService patientExportContextService;

	@Autowired
	private PatientImportContextService patientImportContextService;

	public ProgressBarPollingData getImportStatus(String processId) throws IOException
	{
		return getMigrationStatus(processId, patientImportContextService.getContext(processId));
	}

	public ProgressBarPollingData getExportStatus(String processId) throws IOException
	{
		return getMigrationStatus(processId, patientExportContextService.getContext(processId));
	}

	public LogDataMigration getMigrationResult(String uuid) throws IOException
	{
		LogDataMigration dataMigration = logDataMigrationDao.findByUuid(uuid);
		if(dataMigration == null)
		{
			throw new RuntimeException("Data Migration Entry (" + uuid + ") does not exist");
		}
		if(dataMigration.getEndDatetime() == null)
		{
			throw new RuntimeException("Data Migration Entry (" + uuid + ") is not complete");
		}
		return dataMigration;
	}

	private ProgressBarPollingData getMigrationStatus(String processId, PollableContext context) throws IOException
	{
		if(context != null)
		{
			return context.getProgress();
		}
		else
		{
			LogDataMigration dataMigration = getMigrationResult(processId);

			if(dataMigration != null)
			{
				ProgressBarPollingData pollingData = new ProgressBarPollingData();
				pollingData.setMessage("Finalizing...");
				pollingData.setComplete(true);
				return pollingData;
			}
			else
			{
				throw new RuntimeException("Invalid process id: " + processId);
			}
		}
	}
}
