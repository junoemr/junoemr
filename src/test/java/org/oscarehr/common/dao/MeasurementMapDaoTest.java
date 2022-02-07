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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.MeasurementMap;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeasurementMapDaoTest extends DaoTestFixtures
{
	@Autowired
	protected MeasurementMapDao measurementMapDao;

	public MeasurementMapDaoTest() {
	}

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"measurementType"
		};
	}

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"measurementMap"
		};
	}

        @Test
        public void testCreate() throws Exception {
                MeasurementMap entity = new MeasurementMap();
                EntityDataGenerator.generateTestDataForModelClass(entity);
                measurementMapDao.persist(entity);

                assertNotNull(entity.getId());
        }

	@Test
	public void testGetAllMaps() throws Exception {
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMapDao.persist(measurementMap3);
		
		MeasurementMap measurementMap4 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap4);
		measurementMapDao.persist(measurementMap4);
		
		List<MeasurementMap> expectedResult = new ArrayList<MeasurementMap>(Arrays.asList(measurementMap1, measurementMap2, measurementMap3, measurementMap4));
		List<MeasurementMap> result = measurementMapDao.getAllMaps();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match. Result:" + result.size());
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetMapsByIdent() throws Exception {
		
		String identCode1 = "101";
		String identCode2 = "202";
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMap1.setIdentCode(identCode1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMap2.setIdentCode(identCode2);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMap3.setIdentCode(identCode1);
		measurementMapDao.persist(measurementMap3);
		
		List<MeasurementMap> expectedResult = new ArrayList<MeasurementMap>(Arrays.asList(measurementMap1, measurementMap3));
		List<MeasurementMap> result = measurementMapDao.getMapsByIdent(identCode1);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testFindByLoincCode() throws Exception {
		
		String loincCode1 = "alpha";
		String loincCode2 = "bravo";
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMap1.setLoincCode(loincCode1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMap2.setLoincCode(loincCode2);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMap3.setLoincCode(loincCode1);
		measurementMapDao.persist(measurementMap3);
		
		List<MeasurementMap> expectedResult = new ArrayList<MeasurementMap>(Arrays.asList(measurementMap1, measurementMap3));
		List<MeasurementMap> result = measurementMapDao.findByLoincCode(loincCode1);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testFindByLoincCodeAndLabType() throws Exception {
		
		String loincCode1 = "alpha";
		String loincCode2 = "bravo";
		
		String labType1 = "sigma";
		String labType2 = "charlie";
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMap1.setLoincCode(loincCode1);
		measurementMap1.setLabType(labType1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMap2.setLoincCode(loincCode2);
		measurementMap2.setLabType(labType1);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMap3.setLoincCode(loincCode1);
		measurementMap3.setLabType(labType1);
		measurementMapDao.persist(measurementMap3);
		
		MeasurementMap measurementMap4 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap4);
		measurementMap4.setLoincCode(loincCode1);
		measurementMap4.setLabType(labType2);
		measurementMapDao.persist(measurementMap4);
		
		List<MeasurementMap> expectedResult = new ArrayList<MeasurementMap>(Arrays.asList(measurementMap1, measurementMap3));
		List<MeasurementMap> result = measurementMapDao.findByLoincCodeAndLabType(loincCode1, labType1);

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testFindDistinctLabTypes() throws Exception {
		
		String labType1 = "sigma";
		String labType2 = "charlie";
		String labType3 = "bravo";
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMap1.setLabType(labType1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMap2.setLabType(labType1);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMap3.setLabType(labType3);
		measurementMapDao.persist(measurementMap3);
		
		MeasurementMap measurementMap4 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap4);
		measurementMap4.setLabType(labType2);
		measurementMapDao.persist(measurementMap4);
		
		List<String> expectedResult = new ArrayList<String>(Arrays.asList(labType1, labType3, labType2));
		List<String> result = measurementMapDao.findDistinctLabTypes();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testFindDistinctLoincCodes() throws Exception {
		
		String loincCode1 = "alpha";
		String loincCode2 = "bravo";
		
		MeasurementMap measurementMap1 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap1);
		measurementMap1.setLoincCode(loincCode1);
		measurementMapDao.persist(measurementMap1);
		
		MeasurementMap measurementMap2 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap2);
		measurementMap2.setLoincCode(loincCode2);
		measurementMapDao.persist(measurementMap2);
		
		MeasurementMap measurementMap3 = new MeasurementMap();
		EntityDataGenerator.generateTestDataForModelClass(measurementMap3);
		measurementMap3.setLoincCode(loincCode1);
		measurementMapDao.persist(measurementMap3);
		
		List<String> expectedResult = new ArrayList<String>(Arrays.asList(loincCode1, loincCode2));
		List<String> result = measurementMapDao.findDistinctLoincCodes();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
}
