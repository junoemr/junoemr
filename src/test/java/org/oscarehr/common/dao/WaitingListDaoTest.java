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
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.WaitingList;
import org.oscarehr.common.model.WaitingListName;
import org.oscarehr.util.SpringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import oscar.util.ConversionUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WaitingListDaoTest extends DaoTestFixtures
{

	@Autowired
	protected WaitingListDao dao;

	@Autowired
	protected DemographicDao demographicDao;

	private WaitingListName wn;
	private Demographic demographic;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"Facility","lst_gender","demographic_merged","admission","health_safety","program",
			"waitingList", "waitingListName", "demographic","appointment"
		};
	}

	@Before
	public void before() throws Exception
	{
		wn = new WaitingListName();
		wn.setCreateDate(new Date());
		wn.setName("NAHBLIAYH");
		wn.setGroupNo("1");
		wn.setIsHistory("N");
		wn.setProviderNo("1");
		dao.persist(wn);

		demographic = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(demographic);
		demographic.setDemographicNo(null);
		demographicDao.save(demographic);
	}

	@Test
	public void testCreate() throws Exception {
		WaitingList entity = new WaitingList();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographic(demographic);
		entity.setWaitingListName(wn);
		dao.persist(entity);

		assertNotNull(entity.getId());
	}

	@Test
	public void testFindByDemographic() {
		WaitingList w = new WaitingList();
		w.setDemographic(new Demographic(1));
		w.setWaitingListName(wn);
		w.setOnListSince(new Date());
		w.setPosition(1);
		w.setIsHistory("N");
		dao.persist(w);

		List<WaitingList> lists = dao.findByDemographic(ConversionUtils.fromIntString(demographic.getDemographicNo().toString()));
		assertNotNull(lists);
		assertTrue(lists.size() == 1);
	}

	@Test
	public void testFindAppts() {
		WaitingList w = new WaitingList();
		w.setDemographic(demographic);
		w.setOnListSince(new Date());
		w.setWaitingListName(wn);
		List<Appointment> appts = dao.findAppointmentFor(w);
		assertNotNull(appts);
	}

	@Test
	public void testFBWLIADI() {
		List<WaitingList> wls = dao.findByWaitingListIdAndDemographicId(1,1);
		assertNotNull(wls);
	}
	
	@Test
	public void testMaxPosition() {
		Long i = dao.getMaxPosition(1);
		assertNotNull(i);
	}
	
}
