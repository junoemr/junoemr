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
package org.oscarehr.fax.schedulingTasks;

import org.apache.log4j.Logger;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class OutboundFaxStatusUpdateTask
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String cronSchedule = "0 */5 * * * *";

	@Autowired
	private FaxUploadService faxUploadService;

	@PostConstruct
	public void init()
	{
		logger.info("Fax integration status scheduling task initialized.");
	}

	@Scheduled(cron = cronSchedule)
	public void updateFaxStatuses()
	{
		try
		{
			faxUploadService.requestAllPendingStatusUpdates();
		}
		catch(IllegalStateException e)
		{
			logger.warn(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error("Unexpected fax status update error", e);
		}
	}
}