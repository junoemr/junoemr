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
import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MeasurementTypeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected MeasurementTypeDao measurementTypeDao;

	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"measurementType"
		};
	}

	@Test
	public void testCreate() throws Exception {
		MeasurementType entity = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		measurementTypeDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testFindAll() throws Exception {

		MeasurementType measurementType1 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType1);
		measurementTypeDao.persist(measurementType1);
		
		MeasurementType measurementType2 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType2);
		measurementTypeDao.persist(measurementType2);
		
		MeasurementType measurementType3 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType3);
		measurementTypeDao.persist(measurementType3);
		
		MeasurementType measurementType4 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType4);
		measurementTypeDao.persist(measurementType4);
		
		List<MeasurementType> expectedResult = new ArrayList<MeasurementType>(Arrays.asList(measurementType1, measurementType2, measurementType3, measurementType4));
		List<MeasurementType> result = measurementTypeDao.findAll();

		Logger logger = MiscUtils.getLogger();
		
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		/*
		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		*/
		assertTrue(true);
	}
	
	@Test 
	public void testFindByType() throws Exception {
		
		String type1 = "typ1";
		String type2 = "typ2";
		
		MeasurementType measurementType1 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType1);
		measurementType1.setType(type1);
		measurementTypeDao.persist(measurementType1);
		
		MeasurementType measurementType2 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType2);
		measurementType2.setType(type1);
		measurementTypeDao.persist(measurementType2);
		
		MeasurementType measurementType3 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType3);
		measurementType3.setType(type2);
		measurementTypeDao.persist(measurementType3);
		
		MeasurementType measurementType4 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType4);
		measurementType4.setType(type1);
		measurementTypeDao.persist(measurementType4);
		
		List<MeasurementType> expectedResult = new ArrayList<MeasurementType>(Arrays.asList(measurementType1, measurementType2, measurementType4));
		List<MeasurementType> result = measurementTypeDao.findByType(type1);

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
	public void testFindByMeasuringInstructionAndTypeDisplayName() throws Exception {
		
		String measuringInstruction1 = "instruction1";
		String measuringInstruction2 = "instruction2";
		
		String typeDisplayName1 = "alpha";
		String typeDisplayName2 = "bravo";
		
		MeasurementType measurementType1 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType1);
		measurementType1.setMeasuringInstruction(measuringInstruction1);
		measurementType1.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType1);
		
		MeasurementType measurementType2 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType2);
		measurementType2.setMeasuringInstruction(measuringInstruction1);
		measurementType2.setTypeDisplayName(typeDisplayName2);
		measurementTypeDao.persist(measurementType2);
		
		MeasurementType measurementType3 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType3);
		measurementType3.setMeasuringInstruction(measuringInstruction2);
		measurementType3.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType3);
		
		MeasurementType measurementType4 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType4);
		measurementType4.setMeasuringInstruction(measuringInstruction1);
		measurementType4.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType4);
		
		List<MeasurementType> expectedResult = new ArrayList<MeasurementType>(Arrays.asList(measurementType1, measurementType4));
		List<MeasurementType> result = measurementTypeDao.findByMeasuringInstructionAndTypeDisplayName(measuringInstruction1, typeDisplayName1);

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
	public void testFindByTypeDisplayName() throws Exception {
		
		String typeDisplayName1 = "alp";
		String typeDisplayName2 = "bra";
		
		MeasurementType measurementType1 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType1);
		measurementType1.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType1);
		
		MeasurementType measurementType2 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType2);
		measurementType2.setTypeDisplayName(typeDisplayName2);
		measurementTypeDao.persist(measurementType2);
		
		MeasurementType measurementType3 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType3);
		measurementType3.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType3);
		
		MeasurementType measurementType4 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType4);
		measurementType4.setTypeDisplayName(typeDisplayName1);
		measurementTypeDao.persist(measurementType4);
		
		List<MeasurementType> expectedResult = new ArrayList<MeasurementType>(Arrays.asList(measurementType1, measurementType3, measurementType4));
		List<MeasurementType> result = measurementTypeDao.findByTypeDisplayName(typeDisplayName1);

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
	public void testFindByTypeAndMeasuringInstruction() throws Exception {
		
		String measuringInstruction1 = "instruction1";
		String measuringInstruction2 = "instruction2";
		
		String type1 = "typ1";
		String type2 = "typ2";
		
		MeasurementType measurementType1 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType1);
		measurementType1.setMeasuringInstruction(measuringInstruction2);
		measurementType1.setType(type1);
		measurementTypeDao.persist(measurementType1);
		
		MeasurementType measurementType2 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType2);
		measurementType2.setMeasuringInstruction(measuringInstruction1);
		measurementType2.setType(type1);
		measurementTypeDao.persist(measurementType2);
		
		MeasurementType measurementType3 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType3);
		measurementType3.setMeasuringInstruction(measuringInstruction2);
		measurementType3.setType(type2);
		measurementTypeDao.persist(measurementType3);
		
		MeasurementType measurementType4 = new MeasurementType();
		EntityDataGenerator.generateTestDataForModelClass(measurementType4);
		measurementType4.setMeasuringInstruction(measuringInstruction1);
		measurementType4.setType(type1);
		measurementTypeDao.persist(measurementType4);
		
		List<MeasurementType> expectedResult = new ArrayList<MeasurementType>(Arrays.asList(measurementType2, measurementType4));
		List<MeasurementType> result = measurementTypeDao.findByTypeAndMeasuringInstruction(type1, measuringInstruction1);

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
