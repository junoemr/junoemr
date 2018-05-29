/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */


package org.oscarehr.schedule.service;

import org.oscarehr.schedule.dao.ScheduleTemplateCodeDao;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.schedule.model.ScheduleTemplateCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ScheduleTemplateService
{
	@Autowired
	ScheduleTemplateDao scheduleTemplateDao;
	@Autowired
	ScheduleTemplateCodeDao scheduleTemplateCodeDao;

	/**
	 * Find the public and private templates available to the provider.
	 * @param providerNo provider id
	 * @return List of public and private templates.
	 */
	public List<ScheduleTemplate> getPublicAndPrivateTemplates(String providerNo)
	{
		List<ScheduleTemplate> templateList = scheduleTemplateDao.findByProviderNo("Public");
		List<ScheduleTemplate> providerTemplates = scheduleTemplateDao.findByProviderNo(providerNo);

		templateList.addAll(providerTemplates);
		return templateList;
	}

	public List<ScheduleTemplateCode> getScheduleTemplateCodes()
	{
		return scheduleTemplateCodeDao.findAll();
	}
}
