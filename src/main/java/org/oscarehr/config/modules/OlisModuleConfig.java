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

import org.apache.log4j.Logger;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.olis.OLISSchedulerJob;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import oscar.OscarProperties;

@Configuration
@Conditional(OlisModuleConfig.Condition.class)
public class OlisModuleConfig
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	protected OLISSchedulerJob olisSchedulerJob;

	@Autowired
	protected SystemPreferenceService systemPreferenceService;

	public OlisModuleConfig()
	{
		logger.info("Loaded Olis module");
	}

	static class Condition extends ModuleConfigCondition
	{
		public OscarProperties.Module getModule()
		{
			return OscarProperties.Module.MODULE_OLIS;
		}
	}

	@Scheduled(fixedDelayString = "${omd.olis.poll_interval_sec}000")
	public void pullOLISReports()
	{
		if(systemPreferenceService.isPreferenceEnabled(UserProperty.OLIS_POLLING_ENABLED, false))
		{
			logger.info("Initialize OLIS scheduler job");
			olisSchedulerJob.run();
		}
	}
}
