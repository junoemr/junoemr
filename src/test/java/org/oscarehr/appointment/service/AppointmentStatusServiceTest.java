package org.oscarehr.appointment.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.oscarehr.common.dao.DaoTestFixtures;
import org.oscarehr.common.dao.utils.SchemaUtils;
import org.oscarehr.common.model.AppointmentStatus;
import org.oscarehr.util.SpringUtils;

import java.util.ArrayList;
import java.util.List;

public class AppointmentStatusServiceTest
{
	protected AppointmentStatusService appointmentStatusService = SpringUtils.getBean(AppointmentStatusService.class);

	@BeforeClass
	public static void init() throws Exception
	{
		DaoTestFixtures.setupBeanFactory();
	}

	@Before
	public void before() throws Exception {
		SchemaUtils.restoreTable("appointment_status");
	}
	
	/**
	 * Test that the correct appointment status code (the next one alphabetically) is assigned.
	 */
	public void createAppointmentStatuses()
	{
/*		// General case
		Mockito.when(appointmentStatusDao.findAll()).thenReturn(generateAppointmentStatusList("ABCabc".toCharArray()));
		AppointmentStatus status0 = new AppointmentStatus();
		status0 = appointmentStatusService.createAppointmentStatus(status0);
		Assert.assertEquals("D", status0.getStatus());
		
		// Upper case assigned before lower case
		Mockito.when(appointmentStatusDao.findAll()).thenReturn(generateAppointmentStatusList("ABCDEFabcdefghijklmnopqrstuvwxyz".toCharArray()));
		AppointmentStatus status1 = new AppointmentStatus();
		status1 = appointmentStatusService.createAppointmentStatus(status1);
		Assert.assertEquals("G", status1.getStatus());
		
		// Lower case assigned after upper case
		Mockito.when(appointmentStatusDao.findAll()).thenReturn(generateAppointmentStatusList("ABCDEFGHIJKLMNOPQRSTUVWXYZabcd".toCharArray()));
		AppointmentStatus status2 = new AppointmentStatus();
		status2 = appointmentStatusService.createAppointmentStatus(status2);
		Assert.assertEquals("e", status2.getStatus());
		
		// The order of the returned appointment statuses doesn't matter
		Mockito.when(appointmentStatusDao.findAll()).thenReturn(generateAppointmentStatusList("CcDdBbAa".toCharArray()));
		AppointmentStatus status3 = new AppointmentStatus();
		status3 = appointmentStatusService.createAppointmentStatus(status2);
		Assert.assertEquals("E", status3.getStatus());
		
		// Picks the first available status
		Mockito.when(appointmentStatusDao.findAll()).thenReturn(generateAppointmentStatusList("ACDEGacdeg".toCharArray()));
		AppointmentStatus status4 = new AppointmentStatus();
		status4 = appointmentStatusService.createAppointmentStatus(status2);
		Assert.assertEquals("B", status4.getStatus());*/
	}

	@Test
	public void testSwapDown1()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(1);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(2);

		appointmentStatusService.swapDown(topBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(1);
		AppointmentStatus bottomAfterSwap = afterSwap.get(2);

		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
	}

	@Test
	public void testSwapDown2()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(4);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(5);

		appointmentStatusService.swapDown(topBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(4);
		AppointmentStatus bottomAfterSwap = afterSwap.get(5);

		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
	}

	@Test
	public void testSwapUp1()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(1);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(2);

		appointmentStatusService.swapUp(bottomBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topAfterSwap = afterSwap.get(1);
		AppointmentStatus bottomAfterSwap = afterSwap.get(2);

		Assert.assertEquals(bottomBeforeSwap.getStatus(), topAfterSwap.getStatus());
		Assert.assertEquals(topBeforeSwap.getStatus(), bottomAfterSwap.getStatus());
	}

	@Test
	public void testSwapUp2()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus topBeforeSwap = allStatuses.get(4);
		AppointmentStatus bottomBeforeSwap = allStatuses.get(5);

		appointmentStatusService.swapUp(bottomBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
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
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapUp(firstBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
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
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapUp(secondBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
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
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus firstBeforeSwap = allStatuses.get(0);
		AppointmentStatus secondBeforeSwap = allStatuses.get(1);

		appointmentStatusService.swapDown(firstBeforeSwap);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus firstAfterSwap = afterSwap.get(0);
		AppointmentStatus secondAfterSwap = afterSwap.get(1);

		Assert.assertEquals(firstBeforeSwap.getStatus(), firstAfterSwap.getStatus());
		Assert.assertEquals(firstBeforeSwap.getId(), firstAfterSwap.getId());

		Assert.assertEquals(secondBeforeSwap.getStatus(), secondAfterSwap.getStatus());
		Assert.assertEquals(secondBeforeSwap.getId(), secondAfterSwap.getId());
	}

	@Test
	/**
	 * The last status should not be able to be swapped down because it is already last.
	 * Attempting to swap it should do nothing.
	 */
	public void testSwapDownPastBottom()
	{
		List<AppointmentStatus> allStatuses = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus last = allStatuses.get(allStatuses.size() - 1);
		AppointmentStatus secondLast = allStatuses.get(allStatuses.size() - 2);

		appointmentStatusService.swapDown(last);

		List<AppointmentStatus> afterSwap = appointmentStatusService.getAppointmentStatuses();
		AppointmentStatus newLast = afterSwap.get(afterSwap.size() - 1);
		AppointmentStatus newSecondLast = afterSwap.get(afterSwap.size() - 2);

		Assert.assertEquals(last.getStatus(), newLast.getStatus());
		Assert.assertEquals(last.getId(), newLast.getId());

		Assert.assertEquals(secondLast.getStatus(), newSecondLast.getStatus());
		Assert.assertEquals(secondLast.getId(), secondLast.getId());
	}

	/**
	 * Create a list of appointment statuses, using all the status in the given array
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
