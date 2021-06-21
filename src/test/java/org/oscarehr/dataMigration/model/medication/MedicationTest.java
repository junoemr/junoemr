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
package org.oscarehr.dataMigration.model.medication;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MedicationTest
{
	@Test
	public void testCalculateEndDate_NullStartDate()
	{
		assertNull(Medication.calculateEndDate(null, new FrequencyCode("OD"), 1, 1));
	}

	@Test
	public void testCalculateEndDate_NullFrequency()
	{
		assertNull(Medication.calculateEndDate(LocalDate.of(2021, 1, 1), null, 1, 1));
	}

	@Test
	public void testCalculateEndDate_FrequencyCode1()
	{
		LocalDate startDate = LocalDate.of(2021, 1, 1);
		FrequencyCode frequency = new FrequencyCode("OD"); // once daily
		double dosage = 1.0; // ex 1 pill at a time
		double amount = 10.0; // ex 10 pills

		double expectedDuration = Math.round(amount / (dosage * frequency.toScalar()));
		LocalDate expectedEndDate = LocalDate.of(2021, 1, 11);

		LocalDate result = Medication.calculateEndDate(startDate, frequency, amount, dosage);
		assertEquals("Expected to add " + expectedDuration + " days", expectedEndDate, result);
	}

	@Test
	public void testCalculateEndDate_FrequencyCode2()
	{
		LocalDate startDate = LocalDate.of(2021, 1, 1);
		FrequencyCode frequency = new FrequencyCode("BID"); // twice daily
		double dosage = 2.0; // ex 2 pill at a time
		double amount = 16.0; // ex 16 pills

		double expectedDuration = Math.round(amount / (dosage * frequency.toScalar()));
		LocalDate expectedEndDate = LocalDate.of(2021, 1, 5);

		LocalDate result = Medication.calculateEndDate(startDate, frequency, amount, dosage);
		assertEquals("Expected to add " + expectedDuration + " days", expectedEndDate, result);
	}

}
