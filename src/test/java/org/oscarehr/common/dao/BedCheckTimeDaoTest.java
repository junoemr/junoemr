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

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.BedCheckTime;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BedCheckTimeDaoTest extends DaoTestFixtures
{
	@Autowired
	protected BedCheckTimeDao bedCheckTimeDao;

	DateFormat dfm = new SimpleDateFormat("yyyyMMdd");

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"bed_check_time"
		};
	}
	
	@Test
	public void testCreate() throws Exception {
		BedCheckTime entity = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		bedCheckTimeDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test
	public void testBedCheckTimeExists() throws Exception {
		
		int programId1 = 101;
		int programId2 = 202;
		
		Date date1 = new Date(dfm.parse("20120101").getTime());
		Date date2 = new Date(dfm.parse("20091101").getTime());
		
		BedCheckTime bct1 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct1);
		bct1.setProgramId(programId1);
		bct1.setTime(date1);
		bedCheckTimeDao.persist(bct1);
		
		BedCheckTime bct2 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct2);
		bct2.setProgramId(programId2);
		bct2.setTime(date2);
		bedCheckTimeDao.persist(bct2);
		
		boolean result = bedCheckTimeDao.bedCheckTimeExists(programId1, date1);
		boolean expectedResult = true;
		
		assertEquals(expectedResult, result);
	}
	
	@Test
	public void testGetBedCheckTimes() throws Exception {
		
		int programId1 = 1111;
		int programId2 = 2222;
		int programId3 = 3333;
		
		BedCheckTime bct1 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct1);
		bct1.setProgramId(programId1);
		bedCheckTimeDao.persist(bct1);
		
		BedCheckTime bct2 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct2);
		bct2.setProgramId(programId2);
		bedCheckTimeDao.persist(bct2);
		
		BedCheckTime bct3 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct3);
		bct3.setProgramId(programId3);
		bedCheckTimeDao.persist(bct3);
		
		BedCheckTime result[] = bedCheckTimeDao.getBedCheckTimes(programId1);
		BedCheckTime expectedResult[] = {bct1};
		
		Logger logger = MiscUtils.getLogger();
		
		if (result.length != expectedResult.length) {
			logger.warn("Array sizes do not match.");
			fail("Array sizes do not match.");
		}
		for (int i = 0; i < expectedResult.length; i++) {
			if (!expectedResult[i].equals(result[i])){
				logger.warn("Items  do not match.");
				fail("Items  do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Ignore ///Returns the query instead of the result...check dao file
	@Test
	public void testGetBedCheckTimesQuery() throws Exception {
		
		int programId1 = 101;
		int programId2 = 202;
		
		Date date1 = new Date(dfm.parse("20110101").getTime());
		Date date2 = new Date(dfm.parse("20101101").getTime());
		Date date3 = new Date(dfm.parse("20091012").getTime());
		
		BedCheckTime bct1 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct1);
		bct1.setProgramId(programId1);
		bct1.setTime(date1);
		bedCheckTimeDao.persist(bct1);
		
		BedCheckTime bct2 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct2);
		bct2.setProgramId(programId2);
		bct2.setTime(date2);
		bedCheckTimeDao.persist(bct2);
		
		BedCheckTime bct3 = new BedCheckTime();
		EntityDataGenerator.generateTestDataForModelClass(bct3);
		bct3.setProgramId(303);
		bct3.setTime(date3);
		bedCheckTimeDao.persist(bct3);
		
		String result = bedCheckTimeDao.getBedCheckTimesQuery(programId1);
		Logger logger = MiscUtils.getLogger();
		logger.warn(result);
		String expectedResult = date3.toString();
		
		assertEquals(expectedResult, result);
	}	
}