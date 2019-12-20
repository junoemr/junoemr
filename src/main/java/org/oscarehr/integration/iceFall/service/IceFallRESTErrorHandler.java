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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallAuthenticationException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallAuthorizationException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallDoctorPrivilegeException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallEmailExistsException;
import org.oscarehr.integration.iceFall.service.exceptions.IceFallRESTException;
import org.oscarehr.integration.iceFall.service.transfer.IceFallErrorTo1;
import org.oscarehr.util.MiscUtils;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;

public class IceFallRESTErrorHandler extends DefaultResponseErrorHandler
{

	@Override
	public void handleError(ClientHttpResponse response)
	{
		try
		{
			InputStream inputStream = response.getBody();
			String body = IOUtils.toString(inputStream);
			IceFallErrorTo1 iceFallError = new ObjectMapper().readValue(body, IceFallErrorTo1.class);

			// throw exception based on response type
			IceFallAuthenticationException.throwIfAuthenticationException(iceFallError);
			IceFallAuthorizationException.throwIfAuthorizationException((iceFallError));
			IceFallDoctorPrivilegeException.throwIfPermissionError((iceFallError));
			IceFallEmailExistsException.throwIfEmailExistsException(iceFallError);

			// if no other exception thrown, throw default
			throw new IceFallRESTException(body, iceFallError);
		}
		catch (IOException e)
		{
			MiscUtils.getLogger().error("Error parsing: ", e);
			IceFallErrorTo1 iceFallErrorTo1 = new IceFallErrorTo1();
			iceFallErrorTo1.setJunoInternalError(true);
			IceFallRESTException iceFallException = new IceFallRESTException("Failed to process error response");
			iceFallException.setErrorObject(iceFallErrorTo1);
			throw iceFallException;
		}
	}

}
