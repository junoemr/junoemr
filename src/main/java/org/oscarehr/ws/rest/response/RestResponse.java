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

package org.oscarehr.ws.rest.response;

import java.io.Serializable;

public class RestResponse<B, E> implements Serializable
{
	public enum ResponseStatus
	{
		SUCCESS, ERROR
	}

	private final RestResponseHeaders headers;
	private final B body;
	private final E error;
	private final ResponseStatus status;

	protected RestResponse(RestResponseHeaders headers, B body)
	{
		this(headers, body, null, ResponseStatus.SUCCESS);
	}

	protected RestResponse(RestResponseHeaders headers, B body, E error)
	{
		this(headers, body, error, ResponseStatus.ERROR);
	}

	protected RestResponse(RestResponseHeaders headers, B body, E error, ResponseStatus status)
	{
		this.headers = headers;
		this.body= body;
		this.error = error;
		this.status = status;
	}

	public RestResponseHeaders getHeaders()
	{
		return headers;
	}

	public B getBody()
	{
		return body;
	}

	public E getError()
	{
		return error;
	}

	public ResponseStatus getStatus()
	{
		return status;
	}

	public static <T, E> RestResponse<T, E> successResponse(RestResponseHeaders headers, T body)
	{
		return new RestResponse<>(headers, body, null, ResponseStatus.SUCCESS);
	}

	public static <T, E> RestResponse<T, E> successResponse(T body)
	{
		return successResponse(new RestResponseHeaders(), body);
	}

	public static <T, E> RestResponse<T, E> errorResponse(RestResponseHeaders headers, E error)
	{
		return new RestResponse<>(headers, null, error, ResponseStatus.ERROR);
	}

	public static <T, E> RestResponse<T, E> errorResponse(E error)
	{
		return errorResponse(new RestResponseHeaders(), error);
	}
}