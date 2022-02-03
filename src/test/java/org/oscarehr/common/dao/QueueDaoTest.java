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
/**
 * @author Shazib
 */

package org.oscarehr.common.dao;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.EntityDataGenerator;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.Queue;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class QueueDaoTest extends DaoTestFixtures
{
	@Autowired
	protected QueueDao queueDao;
	
	@Override
	@Test
	public void doSimpleExceptionTest() {
		MiscUtils.getLogger().error("Unable to run doSimpleExceptionTest on this DAO");
	}
	
	@Override
	protected String[] getTablesToClear()
	{
		return new String[]{
			"queue"
		};
	}

	@Test
	public void testGetHashMapOfQueues() throws Exception {
		Queue queue1 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue1);
		queue1.setName("alpha");
		queueDao.persist(queue1);
		
		HashMap<Integer,String> expectedResult = new HashMap<Integer,String>();
		expectedResult.put(queue1.getId(), "alpha");
		HashMap<Integer,String> result = queueDao.getHashMapOfQueues();
		
		assertEquals(expectedResult, result);
	}

	@Test
	public void testGetQueues() throws Exception {
		
		Queue queue1 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue1);
		queueDao.persist(queue1);
		
		Queue queue2 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue2);
		queueDao.persist(queue2);
		
		//Hashtable htQueue1=new Hashtable();
       // htQueue1.put("id", queue1.getId());
       // htQueue1.put("queue", queue1.getName());
        
       // Hashtable htQueue2=new Hashtable();
      //  htQueue2.put("id", queue2.getId());
      //  htQueue2.put("queue", queue2.getName());
		
		List<Hashtable> result = queueDao.getQueues();
		//List<Hashtable> expectedResult = new ArrayList<Hashtable>(Arrays.asList(htQueue1, htQueue2));
		
		assertEquals(2, result.size());
	}

	@Test
	public void testGetLastId() throws Exception {
		
		Queue queue1 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue1);
		queueDao.persist(queue1);
		
		Queue queue2 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue2);
		queueDao.persist(queue2);
		
		Queue queue3 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue3);
		queueDao.persist(queue3);
		
		int latestId = 0;
		int id1 =queue1.getId();
		int id2 =queue2.getId();
		int id3 =queue3.getId();
		if ( id1 > id2 && id1 > id3 )
			latestId = id1;
	      else if ( id2 > id1 && id2 > id3 )
	    	  latestId = id2;
	      else if ( id3 > id1 && id3 > id2 )
	    	  latestId = id3;  
	         
		String result = queueDao.getLastId();
		String expectedResult = String.valueOf(latestId);
		
		assertEquals(expectedResult, result);
	}

	@Test
	public void testGetQueueName() throws Exception {
		
		String name1 = "alpha";
		String name2 = "bravo";
		String name3 = "charlie";
		
		Queue queue1 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue1);
		queue1.setName(name1);
		queueDao.persist(queue1);
		
		Queue queue2 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue2);
		queue2.setName(name2);
		queueDao.persist(queue2);
		
		Queue queue3 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue3);
		queue3.setName(name3);
		queueDao.persist(queue3);
		
		String expectedResult = name2;
		String result = queueDao.getQueueName(queue2.getId());
		
		assertEquals(expectedResult, result);
	}

	@Test
	public void testGetQueueid() throws Exception {
		
		String name1 = "alpha";
		String name2 = "10001";
		String name3 = "charlie";
		
		Queue queue1 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue1);
		queue1.setName(name1);
		queueDao.persist(queue1);
		
		Queue queue2 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue2);
		queue2.setName(name2);
		queueDao.persist(queue2);
		
		Queue queue3 = new Queue();
		EntityDataGenerator.generateTestDataForModelClass(queue3);
		queue3.setName(name3);
		queueDao.persist(queue3);
		
		String expectedResult = queue2.getId().toString();
		String result = queueDao.getQueueid(name2);
		
		assertEquals(expectedResult, result);
	}

	@Test
	public void testAddNewQueue() {
		
		String name1 = "Sigma";
		
		boolean expectedResult = true;
		boolean result = queueDao.addNewQueue(name1);
		
		assertEquals(expectedResult, result);
	}
}
