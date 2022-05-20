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

package org.oscarehr.hospitalReportManager.service;

import org.oscarehr.common.encryption.StringEncryptor;
import org.oscarehr.preferences.service.SystemPreferenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.oscarehr.preferences.SystemPreferenceConstants.HRM_DECRYPTION_KEY;

@Service
public class HRMAccountService
{
	@Autowired
	private SystemPreferenceService systemPreferences;

	public void saveDecryptionKey(String key)
	{
		String encryptedKey = StringEncryptor.encrypt(key);
		systemPreferences.setPreferenceValue(HRM_DECRYPTION_KEY, encryptedKey);
	}

	public boolean hasDecryptionKey()
	{
		return systemPreferences.getOptionalPreferenceValue(HRM_DECRYPTION_KEY).isPresent();
	}

	public Optional<String> retrieveDecryptionKey()
	{
		String decryptionKey = systemPreferences.getOptionalPreferenceValue(HRM_DECRYPTION_KEY)
				.map(StringEncryptor::decrypt)
				.orElse(null);
		return Optional.ofNullable(decryptionKey);
	}
}