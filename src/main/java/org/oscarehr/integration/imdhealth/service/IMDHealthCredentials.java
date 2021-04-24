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

package org.oscarehr.integration.imdhealth.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.oscarehr.integration.imdhealth.transfer.inbound.BearerToken;

import javax.servlet.http.HttpSession;
import java.io.Serializable;

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
public class IMDHealthCredentials implements Serializable
{
	private static final String IMD_CREDENTIALS_KEY = "INTEGRATION.IMDHEALTH";
	private BearerToken bearerToken;

	/**
	 * Retrieve IMDHealthCredentials stored on the user session
	 * @param session user session
	 * @return IMDHealthCredentials if found, null otherwise
	 */
	static IMDHealthCredentials getFromSession(HttpSession session)
	{
		return (IMDHealthCredentials) session.getAttribute(IMD_CREDENTIALS_KEY);
	}

	/**
	 * Remove any existing IMDHealthCredentials from the session
	 * @param session user session
	 */
	static void removeFromSession(HttpSession session)
	{
		session.removeAttribute(IMD_CREDENTIALS_KEY);
	}

	void saveToSession(HttpSession session)
	{
		// To fully impement multisites this will need to be refactored
		// into a HashMap<siteID, credential> rather than just the credential object
		session.setAttribute(IMD_CREDENTIALS_KEY, this);
	}
}
