/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.ws.rest.util;

import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.response.RestResponseError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.List;


@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

	public ValidationExceptionMapper()
	{
	}

	@Override
	public Response toResponse(ConstraintViolationException exception)
	{
		List<ValidationError> errors = new ArrayList<>();
		for(ConstraintViolation constraintViolation: exception.getConstraintViolations())
		{
			errors.add(toValidationError(constraintViolation));
		}

		RestResponse<List<ValidationError>> response = RestResponse.errorResponse(errors, new RestResponseError("Validation Error"));

		return Response.status(Response.Status.BAD_REQUEST).entity(response)
			.type(MediaType.APPLICATION_JSON).build();
	}

	private ValidationError toValidationError(ConstraintViolation constraintViolation)
	{
		ValidationError error = new ValidationError();
		error.setPath(constraintViolation.getPropertyPath().toString());
		error.setMessage(constraintViolation.getMessage());
		return error;
	}

	private class ValidationError
	{
		private String path;
		private String message;

		public String getPath()
		{
			return path;
		}

		public void setPath(String path)
		{
			this.path = path;
		}

		public String getMessage()
		{
			return message;
		}

		public void setMessage(String message)
		{
			this.message = message;
		}
	}
}
