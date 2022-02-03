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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.ticklers.dao.TicklerDao;
import org.oscarehr.ticklers.dao.TicklerLinkDao;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.ticklers.entity.TicklerLink;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TicklerLinkDaoTest extends DaoTestFixtures
{
	@Autowired
	protected TicklerLinkDao ticklerLinkDao;

	@Autowired
	TicklerDao ticklerDao;

	@Override
	protected String[] getTablesToRestore()
	{
		return new String[]{
			"tickler_link", "tickler"
		};
	}

	@Test
	public void testCreate() throws Exception {
		Tickler tickler1 = new Tickler();
		ticklerDao.persist(tickler1);
		TicklerLink entity = new TicklerLink();
		EntityDataGenerator.generateTestDataForModelClass(entity);
		entity.setTickler(tickler1);
		entity.setMeta(null);
		ticklerLinkDao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test 
	public void testGetLinkByTableId() throws Exception {
		
		String tableName1 = "alp";
		String tableName2 = "brv";
		
		String tableId1 = "101";
		String tableId2 = "202";

		Tickler tickler1 = new Tickler();
		ticklerDao.persist(tickler1);

		TicklerLink ticklerLink1 = new TicklerLink();
		EntityDataGenerator.generateTestDataForModelClass(ticklerLink1);
		ticklerLink1.setTableName(tableName1);
		ticklerLink1.setTableId(tableId1);
		ticklerLink1.setTickler(tickler1);
		ticklerLink1.setMeta(null);
		ticklerLinkDao.persist(ticklerLink1);
		
		TicklerLink ticklerLink2 = new TicklerLink();
		EntityDataGenerator.generateTestDataForModelClass(ticklerLink2);
		ticklerLink2.setTableName(tableName2);
		ticklerLink2.setTableId(tableId1);
		ticklerLink2.setTickler(tickler1);
		ticklerLink2.setMeta(null);
		ticklerLinkDao.persist(ticklerLink2);
		
		TicklerLink ticklerLink3 = new TicklerLink();
		EntityDataGenerator.generateTestDataForModelClass(ticklerLink3);
		ticklerLink3.setTableName(tableName1);
		ticklerLink3.setTableId(tableId1);
		ticklerLink3.setTickler(tickler1);
		ticklerLink3.setMeta(null);
		ticklerLinkDao.persist(ticklerLink3);
		
		TicklerLink ticklerLink4 = new TicklerLink();
		EntityDataGenerator.generateTestDataForModelClass(ticklerLink4);
		ticklerLink4.setTableName(tableName1);
		ticklerLink4.setTableId(tableId2);
		ticklerLink4.setTickler(tickler1);
		ticklerLink4.setMeta(null);
		ticklerLinkDao.persist(ticklerLink4);

		List<TicklerLink> expectedResult = new ArrayList<TicklerLink>(Arrays.asList(ticklerLink1, ticklerLink3));
		List<TicklerLink> result = ticklerLinkDao.getLinkByTableId(tableName1, tableId1);
		
		Logger logger = MiscUtils.getLogger();
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match. Result: "+result.size());
			fail("Array sizes do not match.");
		}

		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items do not match.");
				fail("Items do not match.");
			}
		}
		assertTrue(true);
	}
	
	@Test
	public void testGetLinkByTickler() throws Exception {
		Tickler tickler1 = new Tickler();
		ticklerDao.persist(tickler1);
		Tickler tickler2 = new Tickler();
		ticklerDao.persist(tickler2);

		TicklerLink ticklerLink1 = new TicklerLink();
		fillTicklerLink(ticklerLink1);
		ticklerLink1.setTickler(tickler1);
		ticklerLinkDao.persist(ticklerLink1);

		TicklerLink ticklerLink2 = new TicklerLink();
		fillTicklerLink(ticklerLink2);
		ticklerLink2.setTickler(tickler2);
		ticklerLinkDao.persist(ticklerLink2);

		TicklerLink ticklerLink3 = new TicklerLink();
		fillTicklerLink(ticklerLink3);
		ticklerLink3.setTickler(tickler1);
		ticklerLinkDao.persist(ticklerLink3);

		TicklerLink ticklerLink4 = new TicklerLink();
		fillTicklerLink(ticklerLink4);
		ticklerLink4.setTickler(tickler1);
		ticklerLinkDao.persist(ticklerLink4);

		List<TicklerLink> expectedResult = new ArrayList<TicklerLink>(Arrays.asList(ticklerLink1, ticklerLink3, ticklerLink4));
		List<TicklerLink> result = ticklerLinkDao.getLinkByTickler(tickler1.getId());

		Logger logger = MiscUtils.getLogger();
		if (result.size() != expectedResult.size()) {
			logger.warn("Array sizes do not match. Result: "+result.size());
			fail("Array sizes do not match.");
		}

		for (int i = 0; i < expectedResult.size(); i++) {
			if (!expectedResult.get(i).equals(result.get(i))){
				logger.warn("Items do not match.");
				fail("Items do not match.");
			}
		}
		assertTrue(true);
	}


	protected TicklerLink fillTicklerLink(TicklerLink ticklerLink)
	{
		ticklerLink.setTableId("34");
		ticklerLink.setTableName("foobar");
		return ticklerLink;
	}
}
