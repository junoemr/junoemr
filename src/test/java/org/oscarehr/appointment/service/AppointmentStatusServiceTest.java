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
package org.oscarehr.appointment.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.oscarehr.appointment.dao.AppointmentStatusDao;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;

public class AppointmentStatusServiceTest
{
	/**
	 * Used when the unit test is highly coupled to the DAO
	 */
	protected AppointmentStatusService appointmentStatusService = SpringUtils.getBean(AppointmentStatusService.class);
	
	/**
	 * Used when the unit test doesn't require explicit modification of the DAO
	 */
	@Autowired
	@InjectMocks
	private AppointmentStatusService appointmentStatusServiceMockedDao;
	
	@Mock
	AppointmentStatusDao mockDao;
	
	@BeforeClass
	public static void init() throws Exception
	{
		DaoTestFixtures.setupBeanFactory();
	}

	@Before
	public void before() throws Exception
	{
		SchemaUtils.restoreTable("appointment_status");
		MockitoAnnotations.initMocks(this);
	}
	
	/**
	 * Test that the correct appointment status code (the next one alphabetically) is assigned.
	 */
	@Test
	public synchronized void createAppointmentStatuses()
	{
		// General case
		when(mockDao.findAll()).thenReturn(generateAppointmentStatusList("ABCabc".toCharArray()));
		AppointmentStatus status0 = new AppointmentStatus();
		status0 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status0);
		Assert.assertEquals("status0", "D", status0.getStatus());
		
		// Upper case assigned before lower case
		when(mockDao.findAll()).thenReturn(generateAppointmentStatusList("ABCDEFabcdefghijklmnopqrstuvwxyz".toCharArray()));
		AppointmentStatus status1 = new AppointmentStatus();
		status1 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status1);
		Assert.assertEquals("status1", "G", status1.getStatus());
		
		// Lower case assigned after upper case
		when(mockDao.findAll()).thenReturn(generateAppointmentStatusList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcd".toCharArray()));
		AppointmentStatus status2 = new AppointmentStatus();
		status2 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status2);
		Assert.assertEquals("status2", "e", status2.getStatus());
		
		// The order of the returned appointment statuses doesn't matter
		when(mockDao.findAll()).thenReturn(generateAppointmentStatusList("CcDdBbAa".toCharArray()));
		AppointmentStatus status3 = new AppointmentStatus();
		status3 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status3);
		Assert.assertEquals("status3", "E", status3.getStatus());
		
		// Picks the first available status if multiple gaps are provided
		when(mockDao.findAll()).thenReturn(generateAppointmentStatusList("ACDEGacdeg".toCharArray()));
		AppointmentStatus status4 = new AppointmentStatus();
		status4 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status4);
		Assert.assertEquals("status4", "B", status4.getStatus());
		
		// Inactive status codes are not reused
		List<AppointmentStatus> statuses = generateAppointmentStatusList("ABC".toCharArray());
		AppointmentStatus inactiveStatus = statuses.get(2);
		inactiveStatus.setActive(0);
		String inactiveStatusCode = inactiveStatus.getStatus();
		when(mockDao.findAll()).thenReturn(statuses);
		AppointmentStatus status5 = new AppointmentStatus();
		status5 = appointmentStatusServiceMockedDao.assignStatusCodeAndSave(status5);
		Assert.assertFalse("status5.1", inactiveStatusCode.equals(status5.getStatus()));
		Assert.assertEquals("status5.2", "D", status5.getStatus());
	}
	
	/**
	 * Test that an exception is thrown if there are no statuses left to assign
	 */
	@Test(expected=NoSuchElementException.class)
	public void testNoMoreAppointmentStatuses()
	{
		List<AppointmentStatus> theEntireAlphabet = generateAppointmentStatusList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray());
		when(mockDao.findAll()).thenReturn(theEntireAlphabet);
		
		AppointmentStatus expectedToFail = new AppointmentStatus();
		appointmentStatusServiceMockedDao.assignStatusCodeAndSave(expectedToFail);
	}
	
	/**
	 * General swap down test, at the first permitted set of swappable statuses
	 */
	@Test
	public void testSwapDown1()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(1);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(2);

		appointmentStatusService.swapDown(topBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(1);
		AppointmentStatus bottomAfterSwap = afterSwap.get(2);

		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
	}
	
	/**
	 * General swap down test, in the middle of the set
	 */
	@Test
	public void testSwapDown2()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(4);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(5);

		appointmentStatusService.swapDown(topBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(4);
		AppointmentStatus bottomAfterSwap = afterSwap.get(5);

		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
	}
	
	/**
	 * General swap up test, at the first permitted set of swappable statuses
	 */
	@Test
	public void testSwapUp1()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(1);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(2);

		appointmentStatusService.swapUp(bottomBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(1);
		AppointmentStatus bottomAfterSwap = afterSwap.get(2);

		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
	}
	
	/**
	 * General swap down test, in the middle of the swappable set.
	 */
	@Test
	public void testSwapUp2()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(4);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(5);

		appointmentStatusService.swapUp(bottomBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(4);
		AppointmentStatus bottomAfterSwap = afterSwap.get(5);

		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
	}

	/**
	 * The first status (sorted by id) should not be able to be swapped up because it is already first.
	 * Attempting to swap it should do nothing.
	 */
	@Test
	public void testSwapUpPastTop()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapUp(firstBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstAfterSwap = afterSwap.get(0);
		AppointmentStatus secondAfterSwap = afterSwap.get(1);

		Assert.assertEquals(firstBeforeSwap.getStatus(), firstAfterSwap.getStatus());
		Assert.assertEquals(firstBeforeSwap.getId(), firstAfterSwap.getId());

		Assert.assertEquals(secondBeforeSwap.getStatus(), secondAfterSwap.getStatus());
		Assert.assertEquals(secondBeforeSwap.getId(), secondAfterSwap.getId());
	}

	/**
	 * The first status (sorted by id) is reserved, the second status should not be able to be swapped up with it
	 * Attempting to swap the second status up should do nothing.
	 */
	@Test
	public void testSwapUpToReservedFirstSpot()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapUp(secondBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstAfterSwap = afterSwap.get(0);
		AppointmentStatus secondAfterSwap = afterSwap.get(1);

		Assert.assertEquals(firstBeforeSwap.getStatus(), firstAfterSwap.getStatus());
		Assert.assertEquals(firstBeforeSwap.getId(), firstAfterSwap.getId());

		Assert.assertEquals(secondBeforeSwap.getStatus(), secondAfterSwap.getStatus());
		Assert.assertEquals(secondBeforeSwap.getId(), secondAfterSwap.getId());
	}

	/**
	 * The first status (sorted by id) is reserved, the first status should be able to be swapped down.
	 * Attempting to swap it should do nothing.
	 */
	@Test
	public void testSwapDownReservedFirstSpot()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapDown(firstBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus firstAfterSwap = afterSwap.get(0);
		AppointmentStatus secondAfterSwap = afterSwap.get(1);

		Assert.assertEquals(firstBeforeSwap.getStatus(), firstAfterSwap.getStatus());
		Assert.assertEquals(firstBeforeSwap.getId(), firstAfterSwap.getId());

		Assert.assertEquals(secondBeforeSwap.getStatus(), secondAfterSwap.getStatus());
		Assert.assertEquals(secondBeforeSwap.getId(), secondAfterSwap.getId());
	}

	
	/**
	 * The last status should not be able to be swapped down because it is already last.
	 * Attempting to swap it should do nothing.
	 */
	@Test
	public void testSwapDownPastBottom()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus last = allStatuses.get(allStatuses.size() - 1);
		AppointmentStatus secondLast = allStatuses.get(allStatuses.size() - 2);

		appointmentStatusService.swapDown(last);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAllAppointmentStatuses();
		AppointmentStatus newLast = afterSwap.get(afterSwap.size() - 1);
		AppointmentStatus newSecondLast = afterSwap.get(afterSwap.size() - 2);

		Assert.assertEquals(last.getStatus(), newLast.getStatus());
		Assert.assertEquals(last.getId(), newLast.getId());

		Assert.assertEquals(secondLast.getStatus(), newSecondLast.getStatus());
		Assert.assertEquals(secondLast.getId(), secondLast.getId());
	}
	
	/**
	 * Test if used statuses are detected properly
	 */
	@Test
	public void testStatusUsage()
	{
		// Test without modifiers
		List<AppointmentStatus> basicCheck = generateAppointmentStatusList("ABCDE".toCharArray());
		when(mockDao.getStatusesInUse()).thenReturn(Arrays.asList("A", "B", "D", "Z"));
		
		List<String> basicStatusesInUse = appointmentStatusServiceMockedDao.checkStatusUsage(basicCheck);
		
		Assert.assertEquals(3, basicStatusesInUse.size());
		Assert.assertTrue(basicStatusesInUse.contains("A"));
		Assert.assertTrue(basicStatusesInUse.contains("B"));
		Assert.assertTrue(basicStatusesInUse.contains("D"));

		// Test casing
		List<AppointmentStatus> caseCheck = generateAppointmentStatusList("Aa".toCharArray());
		when(mockDao.getStatusesInUse()).thenReturn(Arrays.asList("a"));
		List<String> casedStatusesInUse = appointmentStatusServiceMockedDao.checkStatusUsage(caseCheck);
		
		Assert.assertEquals(1, casedStatusesInUse.size());
		Assert.assertTrue(casedStatusesInUse.contains("a"));
		
		// Test modifiers, such as S, V for signed and verified appointments
		List<AppointmentStatus> modifierCheck = generateAppointmentStatusList("ABcd".toCharArray());
		when(mockDao.getStatusesInUse()).thenReturn(Arrays.asList("AV", "BS", "cv", "ds", "Y", "YV", "yv"));
		List<String> modifierStatusesInUse = appointmentStatusServiceMockedDao.checkStatusUsage(modifierCheck);
		
		Assert.assertEquals(4, modifierStatusesInUse.size());
		Assert.assertTrue(modifierStatusesInUse.contains("A"));
		Assert.assertTrue(modifierStatusesInUse.contains("B"));
		Assert.assertTrue(modifierStatusesInUse.contains("c"));
		Assert.assertTrue(modifierStatusesInUse.contains("d"));
		
		// Test asking for nothing, it should also return nothing
		List<AppointmentStatus> nothingIn = new ArrayList<>();
		List<String> emptyResult = new ArrayList<>();
		when(mockDao.getStatusesInUse()).thenReturn(emptyResult);
		
		List<String> nothingOut = appointmentStatusServiceMockedDao.checkStatusUsage(nothingIn);
		Assert.assertTrue(nothingOut.isEmpty());
	}

	/**
	 * Create a list of appointment statuses using all the statuses in the supplied array.
	 *
	 * @param takenStatuses AppointmentStatus status codes
	 * @return List of appointment statuses containing all the codes in takenStatuses.
	 */
	private List<AppointmentStatus> generateAppointmentStatusList(char[] takenStatuses)
	{
		List<AppointmentStatus> results = new ArrayList<>();
		for (int i=0; i < takenStatuses.length; i++)
		{
			char c = takenStatuses[i];
			
			AppointmentStatus status = new AppointmentStatus();
			status.setId(i);
			status.setStatus(Character.toString(c));
			status.setDescription(Character.toString(c));
			status.setEditable(1);
			status.setActive(1);
			status.setColor("#FFFFFF");
			status.setJunoColor("#111111");
			status.setIcon("starbill.gif");
			
			results.add(status);
		}
		
		return results;
	}
}
