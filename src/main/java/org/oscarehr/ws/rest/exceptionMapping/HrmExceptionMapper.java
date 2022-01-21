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
import org.oscarehr.hospitalReportManager.exception.HrmException;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.response.RestResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HrmExceptionMapper implements ExceptionMapper<HrmException>
{
	private static final Logger logger = MiscUtils.getLogger();

	@Override
	public Response toResponse(HrmException exception)
	{
		String message = exception.toResponse().name();
		logger.error(message, exception);

		RestResponse<String> response = RestResponse.errorResponse(message);

		return Response.status(Response.Status.BAD_REQUEST).entity(response)
			.type(MediaType.APPLICATION_JSON).build();
	}
}
