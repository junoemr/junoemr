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

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.oscarehr.common.exception.EncryptionException;
import oscar.OscarProperties;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Optional;

public class StringEncryptor
{
	private static final Logger logger = Logger.getLogger(StringEncryptor.class);
	private static final OscarProperties props = OscarProperties.getInstance();
	public static final String KEY_PROPERTY_NAME = "STRING_ENCRYPTION_KEY";

	private static final String TRANSFORM = "AES/CBC/PKCS5Padding";
	private static final String AES = "AES";

	public static String encrypt(String plaintextString)
	{
		byte[] encryptedWithIV = encrypt(plaintextString.getBytes());
		return Base64.encodeBase64String(encryptedWithIV);
	}

	public static String decrypt(String encryptedStringWithIV)
	{
		byte[] original = decrypt(Base64.decodeBase64(encryptedStringWithIV));
		return new String(original);
	}

	public static SecretKey generateKey() throws NoSuchAlgorithmException
	{
		KeyGenerator keyGenerator = KeyGenerator.getInstance(AES);
		keyGenerator.init(128); // 128 default; 192 and 256 also possible
		return keyGenerator.generateKey();
	}

	private static byte[] encrypt(byte[] plaintext)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(TRANSFORM);
			final SecureRandom rng = new SecureRandom();

			SecretKeySpec sKeySpec = getSecretKeySpec(getAESKey());
			IvParameterSpec iv = createIV(cipher.getBlockSize(), rng);

			cipher.init(Cipher.ENCRYPT_MODE, sKeySpec, iv);

			byte[] encrypted = cipher.doFinal(plaintext);
			return concat(iv.getIV(), encrypted);
		}
		catch(IOException | GeneralSecurityException e)
		{
			logger.error("Encryption Error", e);
			throw new EncryptionException(e);
		}
	}

	private static byte[] decrypt(byte[] encryptedWithIV)
	{
		try
		{
			Cipher cipher = Cipher.getInstance(TRANSFORM);

			SecretKeySpec skeySpec = getSecretKeySpec(getAESKey());
			IvParameterSpec iv = readIV(cipher.getBlockSize(), new ByteArrayInputStream(encryptedWithIV));

			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] encrypted = Arrays.copyOfRange(encryptedWithIV, iv.getIV().length, encryptedWithIV.length);
			return cipher.doFinal(encrypted);
		}
		catch(IOException | GeneralSecurityException e)
		{
			logger.error("Decryption Error", e);
			throw new EncryptionException(e);
		}
	}

	private static String getAESKey()
	{
		return props.getProperty(KEY_PROPERTY_NAME);
	}

	private static SecretKeySpec getSecretKeySpec(String secretKey) throws UnsupportedEncodingException
	{
		if(secretKey == null)
		{
			throw new EncryptionException("Invalid encryption key");
		}
		return new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), AES);
	}

	private static IvParameterSpec createIV(final int ivSizeBytes, final SecureRandom rng)
	{
		final byte[] iv = new byte[ivSizeBytes];
		final SecureRandom theRNG = Optional.ofNullable(rng).orElse(new SecureRandom());
		theRNG.nextBytes(iv);
		return new IvParameterSpec(iv);
	}

	private static IvParameterSpec readIV(final int ivSizeBytes, final InputStream is) throws IOException
	{
		final byte[] iv = new byte[ivSizeBytes];
		int offset = 0;
		while(offset < ivSizeBytes)
		{
			final int read = is.read(iv, offset, ivSizeBytes - offset);
			if(read == -1)
			{
				throw new IOException("Too few bytes for IV in input stream");
			}
			offset += read;
		}
		return new IvParameterSpec(iv);
	}
	private static byte[] concat(byte[] a, byte[] b)
	{
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}
}
