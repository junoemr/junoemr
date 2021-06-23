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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Appointment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OscarAppointmentDaoTest extends DaoTestFixtures
{
	@Autowired
	protected OscarAppointmentDao oscarAppointmentDao;

	@Before
	public void before() throws Exception {
		//nothing here yet. should clean up the appointment table to a known state
		SchemaUtils.restoreTable("appointment", "demographic", "lst_gender", "admission", 
				"demographic_merged", "program", "health_safety", "provider", "providersite", "site", "program_team", "log", "Facility", 
				"mygroup", "drugs","appointmentArchive");
	}

	@Test
	public void test() {
		Calendar cal = Calendar.getInstance();

		Appointment a = new Appointment();
		a.setAppointmentDate(cal.getTime());
		a.setStartTime(cal.getTime());
		cal.add(Calendar.MINUTE, 60);
		a.setEndTime(cal.getTime());
		a.setCreateDateTime(new Date());
		a.setCreator("999998");
		a.setDemographicNo(1);
		a.setProviderNo("999998");
		a.setStatus("t");
		a.setUpdateDateTime(new Date());
		oscarAppointmentDao.persist(a);

		assertNotNull(a.getId());
		assertNotNull(oscarAppointmentDao.find(a.getId()));

		oscarAppointmentDao.remove(a.getId());
		assertNull(oscarAppointmentDao.find(a.getId()));
	}

	@Test
	public void testGetByProviderAndDay() throws Exception {
		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setProviderNo("000001");
		appt.setAppointmentDate(new Date());
		oscarAppointmentDao.persist(appt);

		Integer apptId = appt.getId();
		assertNotNull(apptId);

		List<Appointment> appts = oscarAppointmentDao.getByProviderAndDay(appt.getAppointmentDate(), appt.getProviderNo());
		assertEquals(appts.size(), 1);
		assertEquals(appts.get(0).getId(), apptId);
	}

	@Test
	public void testFindByProviderDayAndStatus() throws Exception {
		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setProviderNo("000001");
		appt.setAppointmentDate(new Date());
		appt.setStatus("t");
		oscarAppointmentDao.persist(appt);

		Integer apptId = appt.getId();
		assertNotNull(apptId);

		List<Appointment> appts = oscarAppointmentDao.findByProviderDayAndStatus(appt.getProviderNo(), appt.getAppointmentDate(), "t");
		assertEquals(appts.size(), 1);
		assertEquals(appts.get(0).getId(), apptId);

	}

	@Test
	public void testFindByDayAndStatus() throws Exception {
		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setProviderNo("000001");
		appt.setAppointmentDate(new Date());
		appt.setStatus("t");
		oscarAppointmentDao.persist(appt);

		Integer apptId = appt.getId();
		assertNotNull(apptId);

		List<Appointment> appts = oscarAppointmentDao.findByDayAndStatus(appt.getAppointmentDate(), "t");
		assertEquals(appts.size(), 1);
		assertEquals(appts.get(0).getId(), apptId);

	}

	@Test
	public void testFind() throws Exception {
		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setProviderNo("000001");
		try
		{
			appt.setAppointmentDate((new SimpleDateFormat("yyyy-MM-dd")).parse("2020-01-01"));
			appt.setCreateDateTime((new SimpleDateFormat("yyyy-MM-dd")).parse("2020-01-01"));
			appt.setStartTime((new SimpleDateFormat("yyyy-MM-dd")).parse("2020-01-01"));
			appt.setEndTime((new SimpleDateFormat("yyyy-MM-dd")).parse("2020-01-01"));
		}
		catch (ParseException e)
		{

		}
		appt.setStatus("t");
		oscarAppointmentDao.persist(appt);

		Integer apptId = appt.getId();
		assertNotNull(apptId);

		List<Appointment> appts = oscarAppointmentDao.find(appt.getAppointmentDate(), appt.getProviderNo(), appt.getStartTime(), appt.getEndTime(), appt.getName(), appt.getNotes(), appt.getReason(), appt.getCreateDateTime(), appt.getCreator(), appt.getDemographicNo());

		assertEquals(appts.size(), 1);
		assertEquals(appts.get(0).getId(), apptId);

		
		Calendar cal=new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		List<Appointment> results= oscarAppointmentDao.findByUpdateDate(cal.getTime(), 99);
		assertTrue(results.size()>0);

		cal.add(Calendar.DAY_OF_YEAR, 2);
		results= oscarAppointmentDao.findByUpdateDate(cal.getTime(), 99);
		assertEquals(0, results.size());
	}

	@Test
	public void testFindNonCancelledFutureAppointments() throws Exception {
		Integer demographicId = 999999;

		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setDemographicNo(demographicId);
		appt.setAppointmentDate(new Date(System.currentTimeMillis() + 1000000000));
		appt.setStatus("C");
		oscarAppointmentDao.persist(appt);

		List<Appointment> appts = oscarAppointmentDao.findNonCancelledFutureAppointments(demographicId);
		assertTrue("Expected to find no appt's for " + demographicId, appts.isEmpty());

		appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setDemographicNo(demographicId);
		appt.setAppointmentDate(new Date(System.currentTimeMillis() + 1000000000));
		appt.setStatus("t");
		oscarAppointmentDao.persist(appt);

		appts = oscarAppointmentDao.findNonCancelledFutureAppointments(demographicId);
		assertFalse("Expected to find an appt's for " + demographicId, appts.isEmpty());
	}

	@Test
	@Ignore
	public void testFindNextAppointment() throws Exception {
		Integer demographicId = 999999;

		Appointment appt = new Appointment();
		EntityDataGenerator.generateTestDataForModelClass(appt);
		appt.setDemographicNo(demographicId);

		appt.setAppointmentDate(new Date());
		GregorianCalendar cal = new GregorianCalendar();
		cal.add(Calendar.MINUTE, 5);
		appt.setStartTime(cal.getTime());
		appt.setStatus("A");
		oscarAppointmentDao.persist(appt);

		Appointment apptCheck = oscarAppointmentDao.findNextAppointment(demographicId);
		assertNotNull(apptCheck);
		assertEquals(appt, apptCheck);
	}

	@Test
	public void testFindByProviderAndDate() {
		assertNotNull(oscarAppointmentDao.findByProviderAndDate("100", new Date()));
	}

	@Test
	public void testFindAppointments() {
		assertNotNull(oscarAppointmentDao.findAppointments(null, null));
		assertNotNull(oscarAppointmentDao.findAppointments(new Date(), new Date()));
	}

	@Test
	public void testFindPatientAppointments() {
		assertNotNull(oscarAppointmentDao.findPatientAppointments("10", null, null));
		assertNotNull(oscarAppointmentDao.findPatientAppointments("10", null, new Date()));
		assertNotNull(oscarAppointmentDao.findPatientAppointments("10", new Date(), null));
		assertNotNull(oscarAppointmentDao.findPatientAppointments("10", new Date(), new Date()));
	}

	@Test
	public void testFindByDateAndProvider() {
		assertNotNull(oscarAppointmentDao.findByDateAndProvider(new Date(), "100"));
	}

	/*
	public Appointment makePersistedAppointment()
	{
		Appointment a=new Appointment();
		a.setProviderNo("1111");
		a.setAppointmentDate(new Date());
		a.setStartTime(new Date());
		a.setEndTime(new Date());
		oscarAppointmentDao.persist(a);
		a= oscarAppointmentDao.find(a.getId()); // must select back out to get the proper update date (with ms zero-ed from the db etc...)
		return(a);
	}

	 */
}
