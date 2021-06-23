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
package org.oscarehr.dataMigration.model.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PhoneNumberTest
{
	@Test
	public void testStaticConstructorOf_Null()
	{
		assertNull(PhoneNumber.of(null));
	}

	@Test
	public void testStaticConstructorOf_Number()
	{
		String number = "2505554567";

		PhoneNumber result = PhoneNumber.of(number);
		assertEquals(number, result.getNumber());
		assertNull(result.getExtension());
	}

	@Test
	public void testStaticConstructorOf_NumberExtension()
	{
		String number = "2505554567";
		String extension = "123";

		PhoneNumber result = PhoneNumber.of(number, extension);
		assertEquals(number, result.getNumber());
		assertEquals(extension, result.getExtension());
	}

	@Test
	public void testStaticConstructorOf_NumberExtensionPrimary()
	{
		String number = "2505554567";
		String extension = "123";
		boolean isPrimary = true;

		PhoneNumber result = PhoneNumber.of(number, extension, isPrimary);
		assertEquals(number, result.getNumber());
		assertEquals(extension, result.getExtension());
		assertEquals(isPrimary, result.isPrimaryContactNumber());
	}

	@Test
	public void testStaticConstructorOf_Number_withInvalidChars()
	{
		String number = "(250) 555-4567*";
		String expected = "2505554567";

		PhoneNumber result = PhoneNumber.of(number);
		assertEquals(expected, result.getNumber());
	}
	@Test
	public void testStaticConstructorOf_Extension_withInvalidChars()
	{
		String number = "2505554567";
		String extension = "(12-3)";
		String expected = "123";

		PhoneNumber result = PhoneNumber.of(number, extension);
		assertEquals(expected, result.getExtension());
	}

	@Test
	public void testGetNumberFormattedHL7_Null()
	{
		PhoneNumber phoneNumber = new PhoneNumber();
		assertNull(phoneNumber.getNumberFormattedHL7());
	}

	@Test
	public void testGetNumberFormattedHL7_Invalid()
	{
		PhoneNumber phoneNumber = PhoneNumber.of("1234");
		assertNull(phoneNumber.getNumberFormattedHL7());
	}

	@Test
	public void testGetNumberFormattedHL7_Valid()
	{
		PhoneNumber phoneNumber = PhoneNumber.of("2505557878");
		assertEquals("(250)555-7878", phoneNumber.getNumberFormattedHL7());
	}
}
