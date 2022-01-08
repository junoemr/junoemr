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

package org.oscarehr.config.modules;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.oscarehr.hospitalReportManager.service.HRMScheduleService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import oscar.OscarProperties;

import javax.annotation.PostConstruct;

@Configuration
@Conditional(HrmModuleConfig.Condition.class)
public class HrmModuleConfig
{
	@Autowired
	private HRMScheduleService hrmScheduleService;

	private static final Logger logger = MiscUtils.getLogger();
	
	public HrmModuleConfig()
	{
		logger.info("Loaded HRM module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_HRM;
		}
	}
	
	@PostConstruct
	public void startSchedule()
	{
		boolean pollingEnabled = OscarProperties.getInstance().isPropertyActive("omd.hrm.polling_enabled");

		if (pollingEnabled)
		{
			String interval = OscarProperties.getInstance().getProperty("omd.hrm.poll_interval_sec");
			hrmScheduleService.scheduleRegularFetch(Math.max(NumberUtils.toInt(interval), HRMScheduleService.HRM_MINIMUM_POLL_TIME_SEC));
		}
	}
}