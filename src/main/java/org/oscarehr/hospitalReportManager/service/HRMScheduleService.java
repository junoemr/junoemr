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
import org.oscarehr.hospitalReportManager.model.HRMFetchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class HRMScheduleService
{
	@Autowired
	TaskScheduler scheduler;
	
	@Autowired
	HRMSftpService hrmSftpService;
	
	private HRMFetchResults lastFetchResults = null;
	
	public void scheduleRegularFetch(int intervalSeconds)
	{
		PeriodicTrigger fetchSchedule = new PeriodicTrigger(intervalSeconds, TimeUnit.SECONDS);
		fetchSchedule.setInitialDelay(0L);
		
		scheduler.schedule(() -> hrmSftpService.pullHRMFromSource() , fetchSchedule);
	}
	
	@Synchronized
	public HRMFetchResults scheduleFetchNow() throws InterruptedException, ExecutionException, TimeoutException
	{
		hrmSftpService.pullHRMFromSource();
		return this.lastFetchResults;
	}
	
	@Synchronized
	protected void setLastFetchResults(HRMFetchResults results)
	{
		this.lastFetchResults = results;
	}
	
	@Synchronized
	public HRMFetchResults getLastFetchResults()
	{
		return this.lastFetchResults;
	}
}
