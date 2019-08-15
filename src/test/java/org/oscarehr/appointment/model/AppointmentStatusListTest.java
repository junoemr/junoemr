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
package org.oscarehr.appointment.model;

import org.junit.Assert;
import org.junit.Test;
import org.oscarehr.appointment.dto.CalendarAppointmentStatus;
import org.oscarehr.common.model.AppointmentStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentStatusListTest
{

	@Test
	public void getCalendarAppointmentStatusListEmptyTest()
	{
		List<String> testStatusList = new ArrayList<>();
		Map<String, String> testDescriptionMap = new HashMap<>();
		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();

		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);

		List<CalendarAppointmentStatus> result = asl.getCalendarAppointmentStatusList();

		List<CalendarAppointmentStatus> expectedResult = new ArrayList<>();

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarAppointmentStatusListOneTest()
	{
		List<String> testStatusList = new ArrayList<>();
		Map<String, String> testDescriptionMap = new HashMap<>();
		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();

		AppointmentStatus appointmentStatus = new AppointmentStatus();
		appointmentStatus.setJunoColor("color1");
		appointmentStatus.setStatus("a");
		appointmentStatus.setId(1);
		appointmentStatus.setDescription("description1");
		appointmentStatus.setIcon("icon1");

		appointmentStatuses.add(appointmentStatus);

		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);

		List<CalendarAppointmentStatus> result = asl.getCalendarAppointmentStatusList();

		List<CalendarAppointmentStatus> expectedResult = new ArrayList<>();

		expectedResult.add(new CalendarAppointmentStatus(
			"color1",
			"a",
			"",
			true,
			1,
			null,
			"icon1"
		));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarAppointmentStatusListOneDescriptionTest()
	{
		List<String> testStatusList = new ArrayList<>();
		Map<String, String> testDescriptionMap = new HashMap<>();
		testDescriptionMap.put("a", "descriptionManual");

		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();

		AppointmentStatus appointmentStatus = new AppointmentStatus();
		appointmentStatus.setJunoColor("color1");
		appointmentStatus.setStatus("a");
		appointmentStatus.setId(1);
		appointmentStatus.setDescription("description1");
		appointmentStatus.setIcon("icon1");

		appointmentStatuses.add(appointmentStatus);

		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);

		List<CalendarAppointmentStatus> result = asl.getCalendarAppointmentStatusList();

		List<CalendarAppointmentStatus> expectedResult = new ArrayList<>();

		expectedResult.add(new CalendarAppointmentStatus(
			"color1",
			"a",
			"descriptionManual",
			true,
			1,
			null,
			"icon1"
		));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarAppointmentStatusListOneBilledTest()
	{
		List<String> testStatusList = new ArrayList<>();
		Map<String, String> testDescriptionMap = new HashMap<>();
		testDescriptionMap.put("B", "descriptionManual");

		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();

		AppointmentStatus appointmentStatus = new AppointmentStatus();
		appointmentStatus.setJunoColor("color1");
		appointmentStatus.setStatus("B");
		appointmentStatus.setId(1);
		appointmentStatus.setDescription("description1");
		appointmentStatus.setIcon("icon1");

		appointmentStatuses.add(appointmentStatus);

		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);

		List<CalendarAppointmentStatus> result = asl.getCalendarAppointmentStatusList();

		List<CalendarAppointmentStatus> expectedResult = new ArrayList<>();

		expectedResult.add(new CalendarAppointmentStatus(
			"color1",
			"B",
			"Billed",
			false,
			1,
			"billed",
			"icon1"
		));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}

	@Test
	public void getCalendarAppointmentStatusListTwoTest()
	{
		List<String> testStatusList = new ArrayList<>();
		Map<String, String> testDescriptionMap = new HashMap<>();
		testDescriptionMap.put("a", "descriptionManual");

		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();

		AppointmentStatus appointmentStatus1 = new AppointmentStatus();
		appointmentStatus1.setJunoColor("color1");
		appointmentStatus1.setStatus("a");
		appointmentStatus1.setId(1);
		appointmentStatus1.setDescription("description1");
		appointmentStatus1.setIcon("icon1");

		appointmentStatuses.add(appointmentStatus1);

		AppointmentStatus appointmentStatus2 = new AppointmentStatus();
		appointmentStatus2.setJunoColor("color1");
		appointmentStatus2.setStatus("B");
		appointmentStatus2.setId(1);
		appointmentStatus2.setDescription("description1");
		appointmentStatus2.setIcon("icon1");

		appointmentStatuses.add(appointmentStatus2);

		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);

		List<CalendarAppointmentStatus> result = asl.getCalendarAppointmentStatusList();

		List<CalendarAppointmentStatus> expectedResult = new ArrayList<>();

		expectedResult.add(new CalendarAppointmentStatus(
			"color1",
			"a",
			"descriptionManual",
			true,
			1,
			null,
			"icon1"
		));

		expectedResult.add(new CalendarAppointmentStatus(
			"color1",
			"B",
			"Billed",
			false,
			1,
		"billed",
			"icon1"
		));

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
