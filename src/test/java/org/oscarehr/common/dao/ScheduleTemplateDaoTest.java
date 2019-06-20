/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.dao;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.schedule.dao.ScheduleTemplateDao;
import org.oscarehr.schedule.model.ScheduleTemplate;
import org.oscarehr.schedule.model.ScheduleTemplatePrimaryKey;
import org.oscarehr.util.SpringUtils;

public class ScheduleTemplateDaoTest extends DaoTestFixtures
{
	protected ScheduleTemplateDao scheduleTemplateDao = SpringUtils.getBean(ScheduleTemplateDao.class);

	public ScheduleTemplateDaoTest() {
	}


	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("scheduletemplate", "scheduledate");
	}

	@Test
	public void testCreate() throws Exception {
		ScheduleTemplate entity = new ScheduleTemplate();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setId(new ScheduleTemplatePrimaryKey("000001","a"));
		scheduleTemplateDao.persist(entity);

		assertNotNull(entity.getId());
	}

    @Test
    public void testFindSchedules() {
	    assertNotNull(scheduleTemplateDao.findSchedules(new Date(), new Date(), "100"));
    }
}
