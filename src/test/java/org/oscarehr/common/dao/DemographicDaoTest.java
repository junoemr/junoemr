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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.Gender;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicDaoTest extends DaoTestFixtures
{
	@Autowired
	protected DemographicDao demographicDao;

	@Autowired
	protected ProviderDataDao providerDataDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"demographic", "lst_gender", "admission", "demographic_merged", "program",
			"health_safety", "provider", "providersite", "site", "program_team","log", "Facility","demographicExt",
			"provider"
		};
	}
	
	
	@Test
	public void testCreate() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		demographicDao.save(entity);
		assertNotNull(entity.getDemographicNo());
	}

	@Test
	public void testGetDemographic() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		demographicDao.save(entity);

		assertNotNull(demographicDao.getDemographic(entity.getDemographicNo().toString()));
		assertNotNull(demographicDao.getDemographicById(entity.getDemographicNo()));
		assertNotNull(demographicDao.getClientByDemographicNo(entity.getDemographicNo()));
	}

	@Test
	public void testGetDemographicByProvider() throws Exception {
		ProviderData provider = new ProviderData();
		EntityDataGenerator.generateTestDataForModelClass(provider);
		provider.setProviderNo(null);
		providerDataDao.saveEntity(provider);


		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setProviderNo(provider.getId());
		entity.setPatientStatus("AC");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getDemographicByProvider(entity.getProviderNo()));
		assertNotNull(demographicDao.getDemographicByProvider(entity.getProviderNo(), false));

		assertEquals(1, demographicDao.getDemographicByProvider(entity.getProviderNo()).size());
		assertEquals(1, demographicDao.getDemographicByProvider(entity.getProviderNo(), false).size());
	}

	@Test
	public void testGetDemographicByMyOscarUserName() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setMyOscarUserName("marc");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getDemographicByMyOscarUserName("marc"));
	}

	@Test
	public void testGetActiveDemosByHealthCardNo() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("2222222222");
		entity.setHcType("Ontario");
		entity.setPatientStatus("AC");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getActiveDemosByHealthCardNo(entity.getHin(), entity.getHcType()));
		assertEquals(1, demographicDao.getActiveDemosByHealthCardNo(entity.getHin(), entity.getHcType()).size());
	}

	@Test
	public void testSearchDemographic() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setLastName("Smith");
		entity.setFirstName("John");
		demographicDao.save(entity);

		assertEquals(1, demographicDao.searchDemographic("Smi").size());
		assertEquals(0, demographicDao.searchDemographic("Doe").size());
		assertEquals(1, demographicDao.searchDemographic("Smi,Jo").size());
		assertEquals(0, demographicDao.searchDemographic("Smi,Ja").size());
	}

	@Test
	public void testGetRosterStatuses() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setRosterStatus("AB");
		demographicDao.save(entity);

		entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setRosterStatus("AB");
		demographicDao.save(entity);

		entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setRosterStatus("AC");
		demographicDao.save(entity);

		assertEquals(2, demographicDao.getRosterStatuses().size());
	}

	@Test
	public void testClientExists() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		demographicDao.save(entity);
		assertNotNull(entity.getDemographicNo());
		assertTrue(demographicDao.clientExists(entity.getDemographicNo()));
	}

	@Test
	public void testGetClientsByChartNo() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setChartNo("000001");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getClientsByChartNo(entity.getChartNo()));
		assertEquals(1, demographicDao.getClientsByChartNo(entity.getChartNo()).size());

	}

	@Test
	public void testGetClientsByHealthCard() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("2222222222");
		entity.setHcType("ontario");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getClientsByHealthCard(entity.getHin(), entity.getHcType()));
		assertEquals(1, demographicDao.getClientsByHealthCard(entity.getHin(), entity.getHcType()).size());

	}

	@Test
	public void testSearchByHealthCard() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("2222222222");
		demographicDao.save(entity);

		assertNotNull(demographicDao.searchByHealthCard(entity.getHin()));
		assertEquals(1, demographicDao.searchByHealthCard(entity.getHin()).size());
	}

	@Test
	public void testGetDemographicByNamePhoneEmail() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setLastName("Smith");
		entity.setFirstName("John");
		entity.setPhone("444-444-4444");
		entity.setPhone2("555-555-5555");
		entity.setEmail("a@b.com");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getDemographicByNamePhoneEmail(entity.getFirstName(), entity.getLastName(), entity.getPhone(), entity.getPhone2(), entity.getEmail()));
		assertEquals(entity.getDemographicNo(), demographicDao.getDemographicByNamePhoneEmail(entity.getFirstName(), entity.getLastName(), entity.getPhone(), entity.getPhone2(), entity.getEmail()).getDemographicNo());
	}

	@Test
	public void testGetDemographicWithLastFirstDOB() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setLastName("Smith");
		entity.setFirstName("John");
		entity.setYearOfBirth("1999");
		entity.setMonthOfBirth("12");
		entity.setDateOfBirth("01");
		demographicDao.save(entity);

		assertNotNull(demographicDao.getDemographicWithLastFirstDOB(entity.getLastName(), entity.getFirstName(), entity.getYearOfBirth(), entity.getMonthOfBirth(), entity.getDateOfBirth()));
		assertEquals(1, demographicDao.getDemographicWithLastFirstDOB(entity.getLastName(), entity.getFirstName(), entity.getYearOfBirth(), entity.getMonthOfBirth(), entity.getDateOfBirth()).size());

	}

	@Test
	public void testFindByCriterion() {
		assertNotNull(demographicDao.findByCriterion(new DemographicDao.DemographicCriterion(null, "", "", "", "", "", "", "")));
		assertNotNull(demographicDao.findByCriterion(new DemographicDao.DemographicCriterion("", "", "", "", "", "", "", "")));
	}

	@Test
	public void testGetAllPatientStatuses() {
		assertNotNull(demographicDao.getAllPatientStatuses());
	}

	@Test
	public void testGetAllRosterStatuses() {
		assertNotNull(demographicDao.getAllRosterStatuses());
	}

	@Test
	public void testGetAllProviderNumers() {
		assertNotNull(demographicDao.getAllProviderNumbers());
	}

    @Test
    public void testFindByField() {
    	assertNotNull(demographicDao.findByField("DemographicNo", "", "DemographicNo", 0));
    	
    	for(String s : new String[] {"LastName", "FirstName", "ChartNo", "Sex", "YearOfBirth", "PatientStatus"}) {    		
    		assertNotNull(demographicDao.findByField(s, "BLAH", "DemographicNo", 0));
    	}
    	
    }

	@Override
    protected List<String> getSimpleExceptionTestExcludes() {
		List<String> result = super.getSimpleExceptionTestExcludes();
		result.add("findByField");
	    return result;
    }
	
	@Test
	public void findByAttributes() throws Exception {
		Demographic entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("7771111");
		entity.setFirstName("bob");
		entity.setLastName("the builder");
		entity.setSex(Gender.M.name());
		entity.setBirthDay(new GregorianCalendar(1990, 1, 4));
		entity.setPhone("5556667777");
		entity.setPhone2("9998884444");
		demographicDao.save(entity);

		entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("7772222");
		entity.setFirstName("bart");
		entity.setLastName("simpson");
		entity.setSex(Gender.M.name());
		entity.setBirthDay(new GregorianCalendar(1980, 2, 5));
		entity.setPhone("2224446666");
		demographicDao.save(entity);

		entity = new Demographic();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setDemographicNo(null);
		entity.setHin("7773333");
		entity.setFirstName("lisa");
		entity.setLastName("simpson");
		entity.setSex(Gender.F.name());
		entity.setBirthDay(new GregorianCalendar(1970, 0, 9));
		entity.setPhone2("6665553333");
		demographicDao.save(entity);

		List<Demographic> results= demographicDao.findByAttributes("777", null, null, null, null, null, null, null, null, null, 0, 99);
		assertEquals(3, results.size());
		
		results= demographicDao.findByAttributes(null, null, "sim", null, null, null, null, null, null, null, 0, 99);
		assertEquals(2, results.size());

		results= demographicDao.findByAttributes(null, "bar", "sim", null, null, null, null, null, null, null, 0, 99);
		assertEquals(1, results.size());

		results= demographicDao.findByAttributes(null, "b", null, null, null, null, null, null, null, null, 0, 99);
		assertEquals(2, results.size());		

		results= demographicDao.findByAttributes(null, "b", null, null, new GregorianCalendar(1980, 2, 5), null, null, null, null, null, 0, 99);
		assertEquals(1, results.size());

		results= demographicDao.findByAttributes(null, "b", null, null, null, null, null, "6665553333", null, null, 0, 99);
		assertEquals(0, results.size());

		results= demographicDao.findByAttributes(null, null, null, null, null, null, null, "6665553333", null, null, 0, 99);
		assertEquals(1, results.size());

		results= demographicDao.findByAttributes(null, "lisa", null, null, null, null, null, "66555333", null, null, 0, 99);
		assertEquals(1, results.size());

		results= demographicDao.findByAttributes(null, null, null, Gender.F, null, null, null, null, null, null, 0, 99);
		assertEquals(1, results.size());

		results= demographicDao.findByAttributes(null, null, null, Gender.F, null, null, null, "66555333", null, null, 0, 99);
		assertEquals(1, results.size());
	}

}
