/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.schedule.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.oscarehr.common.dao.MyGroupAccessRestrictionDao;
import org.oscarehr.common.dao.MyGroupDao;
import org.oscarehr.common.model.MyGroupAccessRestriction;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.MyGroupPrimaryKey;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.schedule.dto.ScheduleGroup;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ScheduleGroupServiceTest
{
	@Autowired
	@InjectMocks
	private ScheduleGroupService scheduleGroupService;

	@Mock
	private MyGroupDao myGroupDao;

	@Mock
	private ProviderDataDao providerDataDao;

	@Mock
	private MyGroupAccessRestrictionDao myGroupAccessRestrictionDao;


	@Before
	public void initMocks()
	{
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void getScheduleGroupsEmptyTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		List<ScheduleGroup> expectedResult = new ArrayList<>();

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertEquals(expectedResult, result);
	}

	@Test
	public void getScheduleGroupsOnlyProvidersOneTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		ProviderData provider1 = new ProviderData();

		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyProvidersTwoTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(2);

		expectedResult.add(new ScheduleGroup("2", ScheduleGroup.IdentifierType.PROVIDER, "last2, first2", providerList2));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsOneTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name", "1"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		expectedResult.add(new ScheduleGroup("name", ScheduleGroup.IdentifierType.GROUP, "name", providerList));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoSameTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "2"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(2);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoFirstGroupedTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "3"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(3);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoGroupedTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "3"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(2);
		providerList2.add(3);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsThreeThreesTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(4);
		providerList2.add(5);
		providerList2.add(6);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsComboTest()
	{
		List<ProviderData> providerDataResults = new ArrayList<>();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(4);
		providerList2.add(5);
		providerList2.add(6);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		List<Integer> providerList4 = new ArrayList<>();
		providerList4.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList4));

		List<Integer> providerList5 = new ArrayList<>();
		providerList5.add(2);

		expectedResult.add(new ScheduleGroup("2", ScheduleGroup.IdentifierType.PROVIDER, "last2, first2", providerList5));

		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyOneProviderOneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();

		ProviderData provider1 = new ProviderData();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>();
		MyGroupAccessRestriction restriction1 = new MyGroupAccessRestriction();
		List<ScheduleGroup> expectedResult = new ArrayList<>(0);

		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		restriction1.setProviderNo(provider1.getProviderNo().toString());
		restriction1.setMyGroupNo(provider1.getProviderNo().toString());
		myGroupAccessRestrictionList.add(restriction1);

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo(provider1.getProviderNo().toString())).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyOneProviderNoneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		ProviderData provider1 = new ProviderData();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);
		List<ScheduleGroup> expectedResult = new ArrayList<>(0);

		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList));

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo(provider1.getProviderNo().toString())).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyTwoProvidersOneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(1);
		MyGroupAccessRestriction restriction = new MyGroupAccessRestriction();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		restriction.setProviderNo(provider1.getProviderNo().toString());
		restriction.setMyGroupNo(provider2.getProviderNo().toString());
		myGroupAccessRestrictionList.add(restriction);

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		List<ScheduleGroup> expectedResult = new ArrayList<>();
		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList));


		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo(provider1.getProviderNo().toString())).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyOneGroupOneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name", "1"), "", "", null));
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(1);
		MyGroupAccessRestriction restriction = new MyGroupAccessRestriction();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		restriction.setProviderNo("1");
		restriction.setMyGroupNo("name");
		myGroupAccessRestrictionList.add(restriction);

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyOneGroupNoneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name", "1"), "", "", null));
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);

		expectedResult.add(new ScheduleGroup("name", ScheduleGroup.IdentifierType.GROUP, "name", providerList));

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoGroupsOneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(1);
		MyGroupAccessRestriction restriction = new MyGroupAccessRestriction();

		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "2"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		ProviderData provider1 = new ProviderData();
		provider1.set("1");

		List<Integer> providerList = new ArrayList<>();
		providerList.add(1);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList));

		restriction.setProviderNo("1");
		restriction.setMyGroupNo("name2");
		myGroupAccessRestrictionList.add(restriction);

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo(provider1.getProviderNo().toString())).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsOnlyGroupsTwoGroupsNoneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroup> myGroupResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);

		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "2"), "", "", null));

		ProviderData provider1 = new ProviderData();
		provider1.set("1");

		ProviderData provider2 = new ProviderData();
		provider2.set("2");

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(2);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsComboNoneRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(4);
		providerList2.add(5);
		providerList2.add(6);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		List<Integer> providerList4 = new ArrayList<>();
		providerList4.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList4));

		List<Integer> providerList5 = new ArrayList<>();
		providerList5.add(2);

		expectedResult.add(new ScheduleGroup("2", ScheduleGroup.IdentifierType.PROVIDER, "last2, first2", providerList5));

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsComboOneProviderRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);
		MyGroupAccessRestriction restriction = new MyGroupAccessRestriction();

		restriction.setProviderNo("1");
		restriction.setMyGroupNo("2");
		myGroupAccessRestrictionList.add(restriction);

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList2 = new ArrayList<>();
		providerList2.add(4);
		providerList2.add(5);
		providerList2.add(6);
		expectedResult.add(new ScheduleGroup("name2", ScheduleGroup.IdentifierType.GROUP, "name2", providerList2));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		List<Integer> providerList4 = new ArrayList<>();
		providerList4.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList4));

		List<Integer> providerList5 = new ArrayList<>();
		providerList5.add(2);

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsComboOneGroupRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);
		MyGroupAccessRestriction restriction = new MyGroupAccessRestriction();

		restriction.setProviderNo("1");
		restriction.setMyGroupNo("name2");
		myGroupAccessRestrictionList.add(restriction);

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		List<Integer> providerList4 = new ArrayList<>();
		providerList4.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList4));

		List<Integer> providerList5 = new ArrayList<>();
		providerList5.add(2);

		expectedResult.add(new ScheduleGroup("2", ScheduleGroup.IdentifierType.PROVIDER, "last2, first2", providerList5));

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getScheduleGroupsComboOneProviderOneGroupRestrictedTest()
	{
		// Prep
		List<ProviderData> providerDataResults = new ArrayList<>();
		List<MyGroupAccessRestriction> myGroupAccessRestrictionList = new ArrayList<>(0);
		MyGroupAccessRestriction restriction1 = new MyGroupAccessRestriction();
		MyGroupAccessRestriction restriction2 = new MyGroupAccessRestriction();

		restriction1.setProviderNo("1");
		restriction1.setMyGroupNo("2");
		myGroupAccessRestrictionList.add(restriction1);

		restriction2.setProviderNo("1");
		restriction2.setMyGroupNo("name2");
		myGroupAccessRestrictionList.add(restriction2);

		ProviderData provider1 = new ProviderData();
		provider1.set("1");
		provider1.setFirstName("first1");
		provider1.setLastName("last1");
		providerDataResults.add(provider1);

		ProviderData provider2 = new ProviderData();
		provider2.set("2");
		provider2.setFirstName("first2");
		provider2.setLastName("last2");
		providerDataResults.add(provider2);

		List<MyGroup> myGroupResults = new ArrayList<>();
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "1"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "2"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name1", "3"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "4"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "5"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name2", "6"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "7"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "8"), "", "", null));
		myGroupResults.add(new MyGroup(new MyGroupPrimaryKey("name3", "9"), "", "", null));

		List<ScheduleGroup> expectedResult = new ArrayList<>();

		List<Integer> providerList1 = new ArrayList<>();
		providerList1.add(1);
		providerList1.add(2);
		providerList1.add(3);
		expectedResult.add(new ScheduleGroup("name1", ScheduleGroup.IdentifierType.GROUP, "name1", providerList1));

		List<Integer> providerList3 = new ArrayList<>();
		providerList3.add(7);
		providerList3.add(8);
		providerList3.add(9);
		expectedResult.add(new ScheduleGroup("name3", ScheduleGroup.IdentifierType.GROUP, "name3", providerList3));

		List<Integer> providerList4 = new ArrayList<>();
		providerList4.add(1);

		expectedResult.add(new ScheduleGroup("1", ScheduleGroup.IdentifierType.PROVIDER, "last1, first1", providerList4));

		List<Integer> providerList5 = new ArrayList<>();
		providerList5.add(2);

		// Mocks
		Mockito.when(providerDataDao.findAll(false)).thenReturn(providerDataResults);
		Mockito.when(myGroupDao.findAllOrdered()).thenReturn(myGroupResults);
		Mockito.when(myGroupAccessRestrictionDao.findByProviderNo("1")).thenReturn(myGroupAccessRestrictionList);

		// Test
		List<ScheduleGroup> result = scheduleGroupService.getScheduleGroups(provider1.getProviderNo().toString());
		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
