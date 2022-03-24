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
import org.oscarehr.fax.FaxStatus;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

@Component
public class OutboundFaxSchedulingTask
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final String cronSchedule = "0 */5 * * * *";
	private static CronSequenceGenerator cronTrigger;

	@Autowired
	private FaxUploadService faxUploadService;

	@Autowired
	private FaxStatus faxStatus;

	@PostConstruct
	public void init()
	{
		cronTrigger = new CronSequenceGenerator(cronSchedule, TimeZone.getDefault());
		logger.info("Fax integration outbound scheduling task initialized.");
	}

	@Scheduled(cron = cronSchedule)
	public void sendOutboundFaxes()
	{
		try
		{
			faxUploadService.sendQueuedFaxes();
		}
		catch(IllegalStateException e)
		{
			logger.warn(e.getMessage());
		}
		catch(Exception e)
		{
			logger.error("Unexpected scheduling task error", e);
		}
	}

	public LocalDateTime getNextRunTime()
	{
		if(faxStatus.canSendFaxes())
		{
			return ConversionUtils.toLocalDateTime(cronTrigger.next(new Date()));
		}
		return null;
	}
}