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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class PhoneNumberModelTest
{
	@Test
	public void testStaticConstructorOf_Null()
	{
		assertNull(PhoneNumberModel.of(null));
	}

	@Test
	public void testStaticConstructorOf_Number()
	{
		String number = "2505554567";

		PhoneNumberModel result = PhoneNumberModel.of(number);
		assertEquals(number, result.getNumber());
		assertNull(result.getExtension());
	}

	@Test
	public void testStaticConstructorOf_NumberExtension()
	{
		String number = "2505554567";
		String extension = "123";

		PhoneNumberModel result = PhoneNumberModel.of(number, extension);
		assertEquals(number, result.getNumber());
		assertEquals(extension, result.getExtension());
	}

	@Test
	public void testStaticConstructorOf_NumberExtensionPrimary()
	{
		String number = "2505554567";
		String extension = "123";
		boolean isPrimary = true;

		PhoneNumberModel result = PhoneNumberModel.of(number, extension, isPrimary, null);
		assertEquals(number, result.getNumber());
		assertEquals(extension, result.getExtension());
		assertEquals(isPrimary, result.isPrimaryContactNumber());
	}

	@Test
	public void testStaticConstructorOf_Number_withInvalidChars()
	{
		String number = "(250) 555-4567*";
		String expected = "2505554567";

		PhoneNumberModel result = PhoneNumberModel.of(number);
		assertEquals(expected, result.getNumber());
	}
	@Test
	public void testStaticConstructorOf_Extension_withInvalidChars()
	{
		String number = "2505554567";
		String extension = "(12-3)";
		String expected = "123";

		PhoneNumberModel result = PhoneNumberModel.of(number, extension);
		assertEquals(expected, result.getExtension());
	}

	@Test
	public void testGetNumberFormattedHL7_Null()
	{
		PhoneNumberModel phoneNumber = new PhoneNumberModel();
		assertNull(phoneNumber.getNumberFormattedHL7());
	}

	@Test
	public void testGetNumberFormattedHL7_Invalid()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("1234");
		assertNull(phoneNumber.getNumberFormattedHL7());
	}

	@Test
	public void testGetNumberFormattedHL7_Valid()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("2505557878");
		assertEquals("(250)555-7878", phoneNumber.getNumberFormattedHL7());
	}

	@Test
	public void getNumber11DigitsOnly_Null()
	{
		PhoneNumberModel phoneNumber = new PhoneNumberModel();
		assertFalse(phoneNumber.getNumber11DigitsOnly().isPresent());
	}

	@Test
	public void getNumber11DigitsOnly_shortNumber()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("1234567");
		assertFalse(phoneNumber.getNumber11DigitsOnly().isPresent());
	}

	@Test
	public void getNumber11DigitsOnly_longNumber()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("123456789012");
		assertFalse(phoneNumber.getNumber11DigitsOnly().isPresent());
	}

	@Test
	public void getNumber11DigitsOnly_10digitValid()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("1234567890");
		assertEquals("11234567890", phoneNumber.getNumber11DigitsOnly().orElse(null));
	}

	@Test
	public void getNumber11DigitsOnly_11digitValid()
	{
		PhoneNumberModel phoneNumber = PhoneNumberModel.of("12345678901");
		assertEquals("12345678901", phoneNumber.getNumber11DigitsOnly().orElse(null));
	}
}
