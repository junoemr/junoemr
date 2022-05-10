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
import org.oscarehr.config.JunoProperties;
import org.oscarehr.config.conditions.OntarioInstance;
import org.oscarehr.config.scheduling.FixedPeriodicAdjustableTrigger;
import org.oscarehr.olis.dao.OLISSystemPreferencesDao;
import org.oscarehr.olis.model.OLISSystemPreferences;
import org.oscarehr.olis.service.OLISPollingService;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Duration;

@Configuration
@Conditional(OntarioInstance.class)
public class OlisModuleConfig implements SchedulingConfigurer
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	protected OLISPollingService olisPollingService;

	@Autowired
	protected TaskScheduler scheduler;

	@Autowired
	private OLISSystemPreferencesDao olisSystemPrefDao;

	@Autowired
	protected JunoProperties junoProperties;

	public OlisModuleConfig()
	{
		logger.info("Loaded OLIS module");
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar)
	{
		scheduledTaskRegistrar.setScheduler(scheduler);
		scheduledTaskRegistrar.addTriggerTask(olisPollingService,
				new FixedPeriodicAdjustableTrigger("OLIS Polling Task", () ->
		{
			OLISSystemPreferences olisPrefs = olisSystemPrefDao.getPreferences();
			Integer frequency = olisPrefs.getPollFrequency().orElse(junoProperties.getOlis().getDefaultPollingIntervalMin());
			return Duration.ofMinutes(frequency);
		}));
	}
}
