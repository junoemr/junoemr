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

import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.demographic.model.DemographicMerged;
import org.oscarehr.demographic.dao.DemographicMergedDao;
import org.oscarehr.util.SpringUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemographicMergedDaoTest extends DaoTestFixtures
{

	protected DemographicMergedDao dao = SpringUtils.getBean(DemographicMergedDao.class);

	@Before
	public void before() throws Exception
	{
		SchemaUtils.restoreTable("demographic_merged");
	}

	@Test
	public void testCreate() throws Exception
	{
		DemographicMerged entity = new DemographicMerged();
		dao.persist(entity);
		assertNotNull(entity.getId());
	}
	
	@Test 
	public void testFindCurrentByMergedTo()
	{
		final int demographicMergedTo = 12;
		final int demographicBeingMerged = 33;

		DemographicMerged demoMerged = new DemographicMerged();
		demoMerged.setDemographicNo(demographicBeingMerged);
		demoMerged.setMergedTo(demographicMergedTo);
		dao.persist(demoMerged);

		List<DemographicMerged> result = dao.findCurrentByMergedTo(demoMerged.getMergedTo());
		assertEquals(1, result.size());
		assertEquals(demographicMergedTo, result.get(0).getMergedTo());
	}
	
	@Test
	public void testGetCurrentHead()
	{
		final int demographicMergedTo = 333;
		final int myDemographicNo = 322;

		DemographicMerged demoMerged = new DemographicMerged();
		demoMerged.setDemographicNo(myDemographicNo);
		demoMerged.setMergedTo(demographicMergedTo);
		dao.persist(demoMerged);

		DemographicMerged result = dao.getCurrentHead(demoMerged.getDemographicNo());
		assertEquals(demographicMergedTo, result.getMergedTo());
	}

	@Test
	public void testFindByParentAndChildIds()
	{
		final int parentId = 1234;
		final int childId = 5678;

		DemographicMerged demographicMerged = new DemographicMerged();
		demographicMerged.setDemographicNo(childId);
		demographicMerged.setMergedTo(parentId);
		dao.persist(demographicMerged);

		List<DemographicMerged> foundEntities = dao.findByParentAndChildIds(parentId, childId);
		assertEquals(1, foundEntities.size());
		assertEquals(demographicMerged, foundEntities.get(0));
	}

	// Slightly different context than the other find methods, as this will get any entry
	// regardless of whether it's been deleted.
	@Test
	public void testFindByDemographicNo()
	{
		final int demographicNo = 222;
				
		DemographicMerged demoMerged = new DemographicMerged();
		demoMerged.setDemographicNo(demographicNo);
		demoMerged.delete();
		dao.persist(demoMerged);
		
		List<DemographicMerged> result = dao.findByDemographicNo(demographicNo);
		assertEquals(1, result.size());
		assertEquals(demoMerged, result.get(0));
	}

	@Test
	public void testMergeDemographics_success()
	{
		final int demographicNo = 1;
		final int mergedTo = 2;

		assertTrue(dao.mergeDemographics("", demographicNo, mergedTo));
		List<DemographicMerged> demographicMerged = dao.findByDemographicNo(demographicNo);

		assertEquals(1, demographicMerged.size());
		assertEquals(mergedTo, demographicMerged.get(0).getMergedTo());

	}

	// Slightly different from the failure case, as we outright do not want the ability to merge a demographic to itself
	@Test
	public void testMergeDemographic_mergeToSelf()
	{
		final int demographicNo = 1;

		assertFalse(dao.mergeDemographics("", demographicNo, demographicNo));

		List<DemographicMerged> noResults = dao.findByDemographicNo(1);
		assertEquals(0, noResults.size());
	}

	@Test
	public void testMergeDemographic_repeatedMerge()
	{
		final int demographicNo = 1;
		final int mergedTo = 2;

		assertTrue(dao.mergeDemographics("", demographicNo, mergedTo));
		assertFalse(dao.mergeDemographics("", demographicNo, mergedTo));

		List<DemographicMerged> demographicMerged = dao.findByDemographicNo(demographicNo);
		assertEquals(1, demographicMerged.size());
		assertEquals(mergedTo, demographicMerged.get(0).getMergedTo());
	}

	@Test
	public void testUnmergeDemographics_success()
	{
		DemographicMerged demographicMerged = new DemographicMerged();
		demographicMerged.setDemographicNo(1);
		demographicMerged.setMergedTo(2);
		dao.persist(demographicMerged);

		dao.unmergeDemographics("", 1);

		// Only way for us to be sure this worked is finding the original entry and asserting its deleted flag is set
		List<DemographicMerged> expectedUnmerged = dao.findByDemographicNo(1);
		assertEquals(1, expectedUnmerged.size());
		assertTrue(expectedUnmerged.get(0).getDeleted());
	}

	@Test(expected = NoSuchElementException.class)
	public void testUnmergeDemographics_noEntry()
	{
		dao.unmergeDemographics("test", 0);
	}
}
