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
package org.oscarehr.common.encryption;

import org.junit.Test;
import org.oscarehr.common.exception.EncryptionException;
import oscar.OscarProperties;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StringEncryptorTest
{
	@Test
	public void testMissingKey()
	{
		setNullKey();
		try
		{
			StringEncryptor.encrypt("to encrypt");
			fail("Expected invalid key exception");
		}
		catch(EncryptionException e)
		{
			assertTrue(true);
		}
	}

	@Test
	public void testInvalidKey()
	{
		setInvalidKey();
		try
		{
			StringEncryptor.encrypt("to encrypt");
			fail("Expected invalid key exception");
		}
		catch(EncryptionException e)
		{
			assertTrue(true);
		}
	}

	@Test
	public void testEncryptString()
	{
		setValidKey();
		String original = "to encrypt";
		String encrypted = StringEncryptor.encrypt(original);
		assertThat(encrypted, not(equalTo(original)));
	}

	@Test
	public void testEncryptDecryptString()
	{
		setValidKey();
		String original = "to encrypt and decrypt";
		String encrypted = StringEncryptor.encrypt(original);
		String decrypted = StringEncryptor.decrypt(encrypted);
		assertThat(decrypted, equalTo(original));
	}

	/**
	 * ensure that two identical strings don't result in the same encryption value
	 */
	@Test
	public void testRandomIV()
	{
		setValidKey();
		String original = "to encrypt";
		String encrypted1 = StringEncryptor.encrypt(original);
		String encrypted2 = StringEncryptor.encrypt(original);
		assertThat(encrypted1, not(equalTo(encrypted2)));
	}

	private void setValidKey()
	{
		OscarProperties.getInstance().setProperty(StringEncryptor.KEY_PROPERTY_NAME, "eb693ec8252cd630");
	}
	private void setNullKey()
	{
		OscarProperties.getInstance().remove(StringEncryptor.KEY_PROPERTY_NAME);
	}
	private void setInvalidKey()
	{
		OscarProperties.getInstance().setProperty(StringEncryptor.KEY_PROPERTY_NAME, "1234567890");
	}
}
