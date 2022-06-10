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
package org.oscarehr.integration.aqs.exception;

import ca.cloudpractice.aqs.client.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.oscarehr.integration.aqs.model.AqsErrorResponse;
import org.oscarehr.util.MiscUtils;

public class AqsCommunicationException extends RuntimeException
{
	@Getter
	protected AqsErrorResponse errorResponse;

	public AqsCommunicationException(String msg)
	{
		super(msg);
	}

	public AqsCommunicationException(String msg, ApiException cause)
	{
		super(msg, cause);

		try
		{
			this.errorResponse = (new ObjectMapper()).readValue(cause.getResponseBody(), AqsErrorResponse.class);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error(
				"Failed to deserialize error response from AQS server with error: " +
				e.toString() +
				" While handling error: " +
				cause.toString());
		}
	}
}
