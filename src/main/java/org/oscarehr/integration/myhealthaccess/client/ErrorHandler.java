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
package org.oscarehr.integration.myhealthaccess.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.oscarehr.integration.myhealthaccess.dto.GenericErrorTo1;
import org.oscarehr.integration.myhealthaccess.exception.CommunicationException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;

public class ErrorHandler implements ResponseErrorHandler
{
	@Override
	public boolean hasError(ClientHttpResponse clientHttpResponse) throws IOException
	{
		return clientHttpResponse.getRawStatusCode() != 200;
	}

	@Override
	public void handleError(ClientHttpResponse clientHttpResponse) throws IOException
	{
		InputStream inputStream = clientHttpResponse.getBody();
		String body = IOUtils.toString(inputStream);

		try
		{
			GenericErrorTo1 genericError = new ObjectMapper().readValue(body, GenericErrorTo1.class);
			throw new CommunicationException("MHA server responded with error.", genericError);
		}
		catch(IOException e)
		{
			throw new CommunicationException(
					"Failed to deserialize MHA error response with error:\n" + e + "\n" + clientHttpResponse.getBody().toString(),
					null,
					e);
		}
	}
}
