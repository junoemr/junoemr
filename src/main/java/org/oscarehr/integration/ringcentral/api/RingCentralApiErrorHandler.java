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
package org.oscarehr.integration.ringcentral.api;

import org.oscarehr.fax.exception.FaxApiConnectionException;
import org.oscarehr.fax.exception.FaxApiValidationException;
import org.oscarehr.fax.exception.FaxIntegrationException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RingCentralApiErrorHandler implements ResponseErrorHandler
{
	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException
	{
		return response.getStatusCode().isError();
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException
	{
		String errorString = new BufferedReader(
				new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
				.lines()
				.collect(Collectors.joining("\n"));

		String errorMessage = "[" + response.getStatusCode().value() + "] "
				+ response.getStatusCode().getReasonPhrase() + ":\n" + errorString;

		if(response.getStatusCode().is5xxServerError())
		{
			// handle server errors
			throw new FaxApiConnectionException(errorMessage);
		}
		else if(response.getStatusCode().is4xxClientError())
		{
			// handle client errors
			switch(response.getStatusCode())
			{
				case NOT_FOUND:
				{
					// something wrong with the system, this is a real error
					throw new FaxIntegrationException(errorMessage);
				}
				case BAD_REQUEST:
				{
					// validation errors etc.
					throw new FaxApiValidationException(errorMessage);
				}
				default:
				{
					// connection issue, can be rectified. faxes probably can be tried again
					throw new FaxApiConnectionException(errorMessage);
				}
			}
		}
	}
}
