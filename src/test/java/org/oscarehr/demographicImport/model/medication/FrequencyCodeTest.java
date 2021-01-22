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
package org.oscarehr.demographicImport.model.medication;

import org.junit.Test;
import org.oscarehr.demographicImport.exception.InvalidFrequencyCodeException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FrequencyCodeTest
{
	private static final String INVALID_ENUM_MAPPING = "Enum mapping does not return correct value";

	@Test
	public void testStaticConstructor_Null()
	{
		assertNull(FrequencyCode.from(null));
	}

	@Test(expected = InvalidFrequencyCodeException.class)
	public void testToScaler_Null()
	{
		new FrequencyCode(null).toScaler();
	}

	@Test(expected = InvalidFrequencyCodeException.class)
	public void testToScaler_Empty()
	{
		new FrequencyCode("").toScaler();
	}

	@Test
	public void testToScaler_Enum_OneTimeDaily()
	{
		Double expectedFrequency = 1.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QD").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("OD").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QAM").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QPM").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QNOON").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QHS").toScaler());
	}

	@Test
	public void testToScaler_Enum_TwoTimesDaily()
	{
		Double expectedFrequency = 2.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("BID").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q12H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q8_12H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryOneHour()
	{
		Double expectedFrequency = 24.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q1H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryTwoHours()
	{
		Double expectedFrequency = 12.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q2H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q1_2H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryThreeHours()
	{
		Double expectedFrequency = 8.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q3H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q2_3H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryFourHours()
	{
		Double expectedFrequency = 6.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q4H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q3_4H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EverySixHours()
	{
		Double expectedFrequency = 4.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("QID").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q6H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q4_6H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryEightHours()
	{
		Double expectedFrequency = 3.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("TID").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q8H").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q6_8H").toScaler());
	}

	@Test
	public void testToScaler_Enum_EveryOtherDay()
	{
		Double expectedFrequency = 0.5;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("Q2D").toScaler());
	}

	@Test
	public void testToScaler_Enum_Once()
	{
		Double expectedFrequency = -1.0;
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("STAT").toScaler());
		assertEquals(INVALID_ENUM_MAPPING, expectedFrequency, FrequencyCode.from("ONCE").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryOtherDay()
	{
		Double expectedFrequency = 0.5;
		assertEquals(expectedFrequency, FrequencyCode.from("2D").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryThirdDay()
	{
		Double expectedFrequency = (1.0/3.0);
		assertEquals(expectedFrequency, FrequencyCode.from("3D").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("Q3D").toScaler());
	}

	@Test
	public void testToScaler_Parse_OneTimeDaily()
	{
		Double expectedFrequency = 1.0;
		assertEquals(expectedFrequency, FrequencyCode.from("1 time daily").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("1 times daily").toScaler());
	}

	@Test
	public void testToScaler_Parse_TwoTimeDaily()
	{
		Double expectedFrequency = 2.0;
		assertEquals(expectedFrequency, FrequencyCode.from("12H").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("2 time daily").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("2 times daily").toScaler());
	}

	@Test
	public void testToScaler_Parse_ThreeTimeDaily()
	{
		Double expectedFrequency = 3.0;
		assertEquals(expectedFrequency, FrequencyCode.from("8H").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("3 time daily").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("3 times daily").toScaler());
	}

	@Test
	public void testToScaler_Parse_FourTimeDaily()
	{
		Double expectedFrequency = 4.0;
		assertEquals(expectedFrequency, FrequencyCode.from("6H").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("4 time daily").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("4 times daily").toScaler());
	}

	@Test
	public void testToScaler_Parse_OnceWeekly()
	{
		Double expectedFrequency = (1.0/7.0);
		assertEquals(expectedFrequency, FrequencyCode.from("1W").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("1 time weekly").toScaler());
	}

	@Test
	public void testToScaler_Parse_TwiceWeekly()
	{
		Double expectedFrequency = (2.0/7.0);
		assertEquals(expectedFrequency, FrequencyCode.from("2 times weekly").toScaler());
	}

	@Test
	public void testToScaler_Parse_OnceMonthly()
	{
		Double expectedFrequency = (1.0/30.0);
		assertEquals(expectedFrequency, FrequencyCode.from("1L").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("Q1L").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("1 time monthly").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryOneHours()
	{
		Double expectedFrequency = 24.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every 1 hours").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryTwoHours()
	{
		Double expectedFrequency = 12.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every 2 hours").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryFourHours()
	{
		Double expectedFrequency = 6.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every 4 hours").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryFourToSixHours()
	{
		Double expectedFrequency = 4.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every 4-6 hours").toScaler());
	}

	@Test
	public void testToScaler_Parse_Now()
	{
		Double expectedFrequency = -1.0;
		assertEquals(expectedFrequency, FrequencyCode.from("now").toScaler());
		assertEquals(expectedFrequency, FrequencyCode.from("one time only").toScaler());
	}

	@Test
	public void testToScaler_Parse_AtBedtime()
	{
		Double expectedFrequency = 1.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every day at bedtime").toScaler());
	}

	@Test
	public void testToScaler_Parse_EveryMorning()
	{
		Double expectedFrequency = 1.0;
		assertEquals(expectedFrequency, FrequencyCode.from("every morning").toScaler());
	}

}
