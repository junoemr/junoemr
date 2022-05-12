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

import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.oscarehr.config.JunoProperties;
import org.oscarehr.hospitalReportManager.service.HRMScheduleService;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class HrmModuleConfig
{
	@Autowired
	private HRMScheduleService hrmScheduleService;

	@Autowired
	private JunoProperties junoProperties;

	@Autowired
	private SystemPreferenceService systemPreferences;

	private static final Logger logger = MiscUtils.getLogger();
	
	public HrmModuleConfig()
	{
		logger.info("Loaded HRM module");
	}

	@PostConstruct
	public void startSchedule()
	{
		JunoProperties.Hrm hrmConfig = junoProperties.getHrm();

		String userInterval = systemPreferences.getPreferenceValue("omd.hrm.polling_interval_sec", null);
		int defaultInterval = hrmConfig.getDefaultPollingIntervalSeconds();
		int interval = NumberUtils.toInt(userInterval, defaultInterval);

		hrmScheduleService.startSchedule(Math.max(interval, hrmConfig.getMinPollingIntervalSeconds()));
	}
}