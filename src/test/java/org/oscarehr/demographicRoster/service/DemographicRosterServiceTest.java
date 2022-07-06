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
package org.oscarehr.demographicRoster.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.demographicRoster.dao.DemographicRosterDao;
import org.oscarehr.demographicRoster.entity.DemographicRoster;
import org.oscarehr.demographicRoster.entity.RosterTerminationReason;
import org.oscarehr.rosterStatus.dao.RosterStatusDao;
import org.oscarehr.rosterStatus.entity.RosterStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

public class DemographicRosterServiceTest
{
	@Autowired
	@InjectMocks
	@Spy
	protected DemographicRosterService demographicRosterService;

	@Mock
	protected DemographicRosterDao demographicRosterDao;

	@Mock
	protected RosterStatusDao rosterStatusDao;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);

		doNothing().when(demographicRosterDao).persist(Mockito.any(DemographicRoster.class));
	}

	@Test
	public void testShouldSaveRosterEntry_noPreviousDemographic_noRosterStatus()
	{
		Demographic newDemographic = buildRosterDemographic(null, null, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, null);

		String errorMessage = "roster status entry should not be created when roster status is not set";
		Assert.assertFalse(errorMessage, result);
	}

	@Test
	public void testShouldSaveRosterEntry_noPreviousDemographic_hasRosterStatus()
	{
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, null);

		String errorMessage = "roster status entry should always be created when roster status is set on new demographic";
		Assert.assertTrue(errorMessage, result);
	}

	@Test
	public void testShouldSaveRosterEntry_noRosterStatus()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		Demographic newDemographic = buildRosterDemographic(null, null, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertFalse("roster status entry can not be unset", result);
	}

	@Test
	public void testShouldSaveRosterEntry_sameRosterStatus()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertFalse("roster status has not changed", result);
	}

	@Test
	public void testShouldSaveRosterEntry_sameRosterStatus_dateChange()
	{
		Date date = new Date();
		date.setTime(0);
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, date, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertTrue("roster status entry should be added on date change", result);
	}

	@Test
	public void testShouldSaveRosterEntry_statusChange()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_NOT_ROSTERED, new Date(), null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertTrue("roster status entry should be added on status change", result);
	}

	@Test
	public void testShouldSaveRosterEntry_familyDocChange()
	{
		Date date = new Date();
		String physicianName = "test, famdoc";
		String physicianOhip = "12345";
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, date, null, null,
				"<fd></fd><fdname></fdname>");
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, date, null, null,
				MessageFormat.format("<fd>{0}</fd><fdname>{1}</fdname>", physicianOhip, physicianName));
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertTrue("roster status entry should be added on family doctor change", result);
	}

	@Test
	public void testShouldSaveRosterEntry_sameRosterStatus_terminationDateChange()
	{
		Date date = new Date();
		date.setTime(0);
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, date, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, null, null, null, null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertTrue("roster status entry should be added on termination date change", result);
	}

	@Test
	public void testShouldSaveRosterEntry_terminationReasonChange()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, "12", null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, "24", null);
		boolean result = demographicRosterService.shouldSaveRosterEntry(newDemographic, oldDemographic);
		Assert.assertTrue("roster status entry should be added on termination reason change", result);
	}

	@Test
	public void testSaveRosterHistory_shouldNotSave()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, null, null);

		doReturn(false).when(demographicRosterService).shouldSaveRosterEntry(any(Demographic.class), any(Demographic.class));

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic, oldDemographic);
		Assert.assertNull("demographic roster should not be created if shouldSave is false", result);
	}

	@Test
	public void testSaveRosterHistory_shouldSave()
	{
		Demographic oldDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, null, null);
		Demographic newDemographic = buildRosterDemographic(RosterStatus.ROSTER_STATUS_ROSTERED, new Date(), null, null, null);

		DemographicRoster expectedResult = Mockito.mock(DemographicRoster.class);
		doReturn(true).when(demographicRosterService).shouldSaveRosterEntry(any(Demographic.class), any(Demographic.class));
		doReturn(expectedResult).when(demographicRosterService).saveRosterHistory(any(Demographic.class));

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic, oldDemographic);
		Assert.assertEquals("demographic roster should not be created if shouldSave is false", expectedResult, result);
	}

	@Test
	public void testSaveRosterHistory_rosteredState_officalRosteredStatus()
	{
		String officialRosteredStatus = RosterStatus.ROSTER_STATUS_ROSTERED;
		Date rosterDate = new Date();
		Date terminationDate = new Date();
		String physicianName = "test, famdoc";
		String physicianOhip = "12345";
		Demographic newDemographic = buildRosterDemographic(officialRosteredStatus,
				rosterDate,
				terminationDate,
				"12",
				MessageFormat.format("<fd>{0}</fd><fdname>{1}</fdname>", physicianOhip, physicianName));
		RosterStatus rosterStatusMock = mockRosterStatusLookup(officialRosteredStatus, true);

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic);

		Assert.assertEquals("roster status should match", rosterStatusMock, result.getRosterStatus());
		Assert.assertEquals("roster date should be present for rostered status",
				LocalDateTime.ofInstant(rosterDate.toInstant(), ZoneId.systemDefault()), result.getRosterDate());
		Assert.assertNull("no termination date when status is actively rostered", result.getRosterTerminationDate());
		Assert.assertNull("no termination reason when status is actively rostered", result.getRosterTerminationReason());
		Assert.assertEquals("physician name should be present", physicianName, result.getRosteredPhysician());
		Assert.assertEquals("physician ohip number should be present", physicianOhip, result.getOhipNo());
	}

	@Test
	public void testSaveRosterHistory_rosteredState_customRosteredStatus()
	{
		String customRosteredStatus = "XX";
		Date rosterDate = new Date();
		Date terminationDate = new Date();
		String physicianName = "test, famdoc";
		String physicianOhip = "12345";
		Demographic newDemographic = buildRosterDemographic(customRosteredStatus,
				rosterDate,
				terminationDate,
				"12",
				MessageFormat.format("<fd>{0}</fd><fdname>{1}</fdname>", physicianOhip, physicianName));
		RosterStatus rosterStatusMock = mockRosterStatusLookup(customRosteredStatus, true);

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic);

		Assert.assertEquals("roster status should match", rosterStatusMock, result.getRosterStatus());
		Assert.assertEquals("roster date should be present for rostered status",
				LocalDateTime.ofInstant(rosterDate.toInstant(), ZoneId.systemDefault()), result.getRosterDate());
		Assert.assertNull("no termination date when status is actively rostered", result.getRosterTerminationDate());
		Assert.assertNull("no termination reason when status is actively rostered", result.getRosterTerminationReason());
		Assert.assertEquals("physician name should be present", physicianName, result.getRosteredPhysician());
		Assert.assertEquals("physician ohip number should be present", physicianOhip, result.getOhipNo());
	}

	@Test
	public void testSaveRosterHistory_rosteredState_officialTerminatedStatus()
	{
		String statusTerminated = RosterStatus.ROSTER_STATUS_TERMINATED;
		Date rosterDate = new Date();
		Date terminationDate = new Date();
		String terminationReason = "12";
		String physicianName = "test, famdoc";
		String physicianOhip = "12345";
		Demographic newDemographic = buildRosterDemographic(statusTerminated,
				rosterDate,
				terminationDate,
				terminationReason,
				MessageFormat.format("<fd>{0}</fd><fdname>{1}</fdname>", physicianOhip, physicianName));
		RosterStatus rosterStatusMock = mockRosterStatusLookup(statusTerminated, false);

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic);

		Assert.assertEquals("roster status should match", rosterStatusMock, result.getRosterStatus());
		Assert.assertEquals("roster date should be present for non-rostered status",
				LocalDateTime.ofInstant(rosterDate.toInstant(), ZoneId.systemDefault()), result.getRosterDate());
		Assert.assertEquals("termination date should be present for non-rostered status",
				LocalDateTime.ofInstant(terminationDate.toInstant(), ZoneId.systemDefault()), result.getRosterTerminationDate());
		Assert.assertEquals("termination reason should be present for non-rostered status",
				RosterTerminationReason.getByCode(12), result.getRosterTerminationReason());
		Assert.assertEquals("physician name should be present", physicianName, result.getRosteredPhysician());
		Assert.assertEquals("physician ohip number should be present", physicianOhip, result.getOhipNo());	}

	@Test
	public void testSaveRosterHistory_rosteredState_customTerminatedStatus()
	{
		String statusTerminated = "ZZ";
		Date rosterDate = new Date();
		Date terminationDate = new Date();
		String terminationReason = "12";
		String physicianName = "test, famdoc";
		String physicianOhip = "12345";
		Demographic newDemographic = buildRosterDemographic(statusTerminated,
				rosterDate,
				terminationDate,
				terminationReason,
				MessageFormat.format("<fd>{0}</fd><fdname>{1}</fdname>", physicianOhip, physicianName));
		RosterStatus rosterStatusMock = mockRosterStatusLookup(statusTerminated, false);

		DemographicRoster result = demographicRosterService.saveRosterHistory(newDemographic);

		Assert.assertEquals("roster status should match", rosterStatusMock, result.getRosterStatus());
		Assert.assertEquals("roster date should be present for non-rostered status",
				LocalDateTime.ofInstant(rosterDate.toInstant(), ZoneId.systemDefault()), result.getRosterDate());
		Assert.assertEquals("termination date should be present for non-rostered status",
				LocalDateTime.ofInstant(terminationDate.toInstant(), ZoneId.systemDefault()), result.getRosterTerminationDate());
		Assert.assertEquals("termination reason should be present for non-rostered status",
				RosterTerminationReason.getByCode(12), result.getRosterTerminationReason());
		Assert.assertEquals("physician name should be present", physicianName, result.getRosteredPhysician());
		Assert.assertEquals("physician ohip number should be present", physicianOhip, result.getOhipNo());
	}

	private RosterStatus mockRosterStatusLookup(String status, boolean isRostered)
	{
		RosterStatus rosterStatusMock = Mockito.mock(RosterStatus.class);
		doReturn(isRostered).when(rosterStatusMock).isRostered();
		doReturn(status).when(rosterStatusMock).getRosterStatus();
		doReturn(rosterStatusMock).when(rosterStatusDao).findByStatus(Mockito.anyString());
		return rosterStatusMock;
	}

	private Demographic buildRosterDemographic(String status,
	                                           Date rosterDate,
	                                           Date terminationDate,
	                                           String terminationReason,
	                                           String familyDoctor)
	{
		Demographic demographic = new Demographic();
		demographic.setRosterStatus(status);
		demographic.setRosterDate(rosterDate);
		demographic.setRosterTerminationDate(terminationDate);
		demographic.setRosterTerminationReason(terminationReason);
		demographic.setFamilyDoctor(familyDoctor);

		return demographic;
	}
}