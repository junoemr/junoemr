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
package org.oscarehr.ws.rest.exceptionMapping;

import org.apache.log4j.Logger;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.filter.exception.RateLimitException;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestResponseRateLimitError;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper for handling exceeded request rate limits.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class RateLimitExceptionMapper implements ExceptionMapper<RateLimitException>
{
	private static final Logger logger = MiscUtils.getLogger();

	public RateLimitExceptionMapper()
	{
	}

	@Override
	public Response toResponse(RateLimitException exception)
	{
		String errorMessage = "The maximum request rate of " + exception.getMaxRequests() + "/" + exception.getResetPeriod() + "ms was exceeded.";

		RestResponseRateLimitError rateLimitError = new RestResponseRateLimitError(errorMessage);
		rateLimitError.setRequestLimit(exception.getMaxRequests());
		rateLimitError.setCurrentRequestCount(exception.getCurrentRequestCount());
		rateLimitError.setResetPeriod(exception.getResetPeriod());
		rateLimitError.setTimeToReset(exception.getTimeToReset());

		RestResponse<String> response = RestResponse.errorResponse(rateLimitError);
		logger.warn(errorMessage);

		// should be status code 429 but this response status doesn't have that value
		return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(response)
				.type(MediaType.APPLICATION_JSON).build();
	}
}
