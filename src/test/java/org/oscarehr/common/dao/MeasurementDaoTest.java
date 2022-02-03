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
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.MeasurementDao.SearchCriteria;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Measurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeasurementDaoTest extends DaoTestFixtures
{
	@Autowired
	protected MeasurementDao measurementDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"measurements", "measurementType", "measurementsExt", "measurementMap", "provider"
		};
	}

	@Test
	public void testFind() {
		Measurement m = populate();

		SearchCriteria c = new SearchCriteria();
		c.setComments(m.getComments());
		c.setDataField(m.getDataField());
		c.setDateObserved(m.getDateObserved());
		c.setDemographicNo(m.getDemographicId());
		c.setMeasuringInstrc(m.getMeasuringInstruction());
		c.setType(m.getType());

		List<Measurement> ms = measurementDao.find(c);
		assertEquals(1, ms.size());

		c = new SearchCriteria();
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setComments(m.getComments());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setDataField(m.getDataField());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setDateObserved(m.getDateObserved());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setDemographicNo(m.getDemographicId());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setMeasuringInstrc(m.getMeasuringInstruction());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setType(m.getType());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setDataField(m.getDataField());
		c.setDateObserved(m.getDateObserved());
		c.setMeasuringInstrc(m.getMeasuringInstruction());
		c.setType(m.getType());
		ms = measurementDao.find(c);
		assertNotNull(ms);

		c = new SearchCriteria();
		c.setComments(m.getComments());
		c.setDataField(m.getDataField());
		c.setDemographicNo(m.getDemographicId());
		c.setMeasuringInstrc(m.getMeasuringInstruction());
		ms = measurementDao.find(c);
		assertNotNull(ms);
	}

	protected Measurement populate() {
		Measurement m = new Measurement();
		m.setDemographicId(999);
		m.setAppointmentNo(100);
		m.setComments("NUIOBLAHA");
		m.setDataField("DTATAHEROVATA");
		try
		{
			m.setDateObserved((new SimpleDateFormat("yyyy-MM-dd")).parse("2020-01-01"));
		}
		catch (ParseException e)
		{

		}
		m.setMeasuringInstruction("MSRNIGINSRCTIONS");
		m.setProviderNo("PRVDRE");
		m.setType("TIPPITIP");
		measurementDao.persist(m);
		return m;
	}

	@Test
	public void testFindById() {
		populate();
		populate();

		List<Measurement> m = measurementDao.findByIdTypeAndInstruction(999, "TIPPITIP", "MSRNIGINSRCTIONS");
		assertFalse(m.isEmpty());
	}

	@Test
	public void testFindByDemographicIdUpdatedAfterDate() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);

		Measurement m = new Measurement();
		m.setDemographicId(1);
		m.setAppointmentNo(100);
		m.setComments("NUIOBLAHA");
		m.setDataField("DTATAHEROVATA");
		m.setDateObserved(cal.getTime());
		m.setCreateDate(cal.getTime());
		m.setMeasuringInstruction("MSRNIGINSRCTIONS");
		m.setProviderNo("PRVDRE");
		m.setType("TIPPITIP");
		measurementDao.persist(m);

		m = new Measurement();
		m.setDemographicId(1);
		m.setAppointmentNo(100);
		m.setComments("NUIOBLAHA");
		m.setDataField("DTATAHEROVATA");
		m.setDateObserved(new Date());
		m.setMeasuringInstruction("MSRNIGINSRCTIONS");
		m.setProviderNo("PRVDRE");
		m.setType("TIPPITIP");
		measurementDao.persist(m);

		cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);

		assertEquals(1, measurementDao.findByDemographicIdUpdatedAfterDate(1, cal.getTime()).size());
		
		cal=new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, -1);
		List<Measurement> results= measurementDao.findByCreateDate(cal.getTime(), 99);
		assertTrue(results.size()>0);

		cal.add(Calendar.DAY_OF_YEAR, 2);
		results= measurementDao.findByCreateDate(cal.getTime(), 99);
		assertEquals(0, results.size());

	}

	@Test
	public void testFindMeasurementsByDemographicIdAndLocationCode() {
		assertNotNull(measurementDao.findMeasurementsByDemographicIdAndLocationCode(100, "CDE"));
	}

	@Test
	public void testFindMeasurementsWithIdentifiersByDemographicIdAndLocationCode() {
		assertNotNull(measurementDao.findMeasurementsWithIdentifiersByDemographicIdAndLocationCode(100, "CDE"));
	}

	@Test
	public void testFindLabNumbers() {
		assertNotNull(measurementDao.findLabNumbers(100, "CDE"));
	}
	
	@Test
	public void testFindLastEntered() {
		measurementDao.findLastEntered(100, "CDE");
	}

	@Test
	public void testFindMeasurementsAndProviders() {
		assertNotNull(measurementDao.findMeasurementsAndProviders(100));
	}

	@Test
	public void testFindMeasurementsAndProvidersByType() {
		assertNotNull(measurementDao.findMeasurementsAndProvidersByType("TYPE", 100));
	}

	@Test
	public void testFindMeasurementsAndProvidersByDemoAndType() {
		measurementDao.findMeasurementsAndProvidersByDemoAndType(100, "TYPE", 1);
	}
	
	@Test
	public void testFindByValue() {
		assertNotNull(measurementDao.findByValue("ZPA", "ZPA"));
	}

    @Test
    public void testFindObservationDatesByDemographicNoTypeAndMeasuringInstruction() {
	    assertNotNull(measurementDao.findObservationDatesByDemographicNoTypeAndMeasuringInstruction(100, "TYPE", "INSTR"));
    }

    @Test
    public void testFindByDemographicNoTypeAndDate() {
	    measurementDao.findByDemographicNoTypeAndDate(100, "TUY", new Date());
	    
    }
}
