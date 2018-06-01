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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.oscarehr.common.model.AppointmentStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RunWith(Parameterized.class)
public class AppointmentStatusListTitleTest
{
	private List<String> testStatusList;
	private Map<String, String> testDescriptionMap;

	private String inputValue;
	private String expectedResult;

	@Before
	public void before() throws Exception
	{
		testStatusList = new ArrayList<>();

		testStatusList.add("a");
		testStatusList.add("b");
		testStatusList.add("c");
		testStatusList.add("d");

		testDescriptionMap = new HashMap<>();

		testDescriptionMap.put("a", "description1");
		testDescriptionMap.put("b", "description2");
		testDescriptionMap.put("c", "description3");
		testDescriptionMap.put("d", "description4");
	}

	public AppointmentStatusListTitleTest(String inputValue, String expectedResult)
	{
		this.inputValue = inputValue;
		this.expectedResult = expectedResult;
	}

	@Parameterized.Parameters
	public static Collection testData()
	{
		return Arrays.asList(new Object[][]
			{
				{"a", "description1"},
				{"aS", "description1/Signed"},
				{"bV", "description2/Verified"},
				{"bV", "description2/Verified"},
				{"B", "Billed"},
				{"BV", "Billed"},
			});
	}

	@Test
	public void testGetTitle()
	{
		List<AppointmentStatus> appointmentStatuses = new ArrayList<>();
		AppointmentStatusList asl = new AppointmentStatusList(testStatusList, testDescriptionMap, appointmentStatuses);
		Locale locale = new Locale.Builder().setLanguage("en").setRegion("CA").build();

		String result = asl.getTitle(inputValue, locale);
		assertEquals(expectedResult, result);
	}
}
