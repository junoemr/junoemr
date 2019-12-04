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

package org.oscarehr.integration.iceFall.service;

import org.oscarehr.integration.iceFall.dao.IceFallCredentialsDao;
import org.oscarehr.integration.iceFall.model.IceFallCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IceFallService
{
	@Autowired
	IceFallCredentialsDao iceFallCredentialsDao;

	/**
	 * get icefall credentials from DB
	 * @return - the icefall credentials
	 */
	public IceFallCredentials getCredentials()
	{
		return iceFallCredentialsDao.getCredentials();
	}

	/**
	 * save icefall credentials to the database
	 * @param creds - the credentials to save
	 * @return - the saved credentials object
	 */
	public IceFallCredentials updateCredentials(IceFallCredentials creds)
	{
		iceFallCredentialsDao.merge(creds);
		return creds;
	}

}
