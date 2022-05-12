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

package org.oscarehr.hospitalReportManager.service;

import lombok.Synchronized;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.hospitalReportManager.model.HrmFetchResultsModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Component
public class HRMScheduleService
{
	@Autowired
	TaskScheduler scheduler;
	
	@Autowired
	HRMService hrmService;

	@Autowired
	JunoProperties junoProps;

	/**
	 * Schedule remote fetch every intervalSeconds.  Location will not be overridden by local override.
	 * @param intervalSeconds seconds between HRM report queries
	 */
	public void startSchedule(int intervalSeconds)
	{
		PeriodicTrigger fetchSchedule = new PeriodicTrigger(intervalSeconds, TimeUnit.SECONDS);
		fetchSchedule.setInitialDelay(0L);

		scheduler.schedule(this::fetchOnSchedule, fetchSchedule);
	}

	public void fetchOnSchedule()
	{
		if (hrmService.isHRMEnabled() && hrmService.isHRMFetchEnabled())
		{
			hrmService.consumeRemoteHRMDocuments();
		}
	}

	/**
	 * Fetch HRM documents now.
	 * If a local override is enabled, will read from it's location instead of using sftp connection.
	 */
	@Synchronized
	public HrmFetchResultsModel fetchNow()
	{
		HrmFetchResultsModel results;

		JunoProperties.Hrm hrmConfig = junoProps.getHrm();
		if (hrmConfig.isLocalOverrideEnabled())
		{
			String localHrmDocs = hrmConfig.getLocalOverrideDirectory();
			results = hrmService.consumeLocalHRMDocuments(Paths.get(localHrmDocs));
		}
		else
		{
			results = hrmService.consumeRemoteHRMDocuments();
		}
		
		return results;
	}
}