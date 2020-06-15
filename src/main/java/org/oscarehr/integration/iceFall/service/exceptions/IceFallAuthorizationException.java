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
package org.oscarehr.integration.iceFall.service.exceptions;

import org.oscarehr.integration.iceFall.service.transfer.IceFallErrorTo1;

public class IceFallAuthorizationException extends IceFallRESTException
{
	public static final String MISSING_AUTH_CREDENTIALS = "__MISSING_AUTH_CREDENTIALS__";

	public IceFallAuthorizationException(String msg)
	{
		super(msg);
	}

	public IceFallAuthorizationException(String msg, IceFallErrorTo1 errorTo1)
	{
		super(msg, errorTo1);
	}

	public static void throwIfAuthorizationException(IceFallErrorTo1 errorTo1)
	{
		if (MISSING_AUTH_CREDENTIALS.equals(errorTo1.getErrorDetail()))
		{
			throw new IceFallAuthorizationException("Authorization Failure", errorTo1);
		}
	}
}
