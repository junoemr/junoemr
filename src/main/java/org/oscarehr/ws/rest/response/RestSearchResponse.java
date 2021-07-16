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
package org.oscarehr.ws.rest.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response wrapper object for list results")
public class RestSearchResponse<T> extends GenericRestResponse<RestSearchResponseHeaders, List<T>, RestResponseError>
{
	protected RestSearchResponse(RestSearchResponseHeaders headers, List<T> body, RestResponseError error, ResponseStatus status)
	{
		super(headers, body, error, status);
	}

	public static <T> RestSearchResponse<T> successResponse(RestSearchResponseHeaders headers, List<T> body, int page, int perPage, int total)
	{
		headers.setPage(page);
		headers.setPerPage(perPage);
		headers.setTotal(total);
		return new RestSearchResponse<>(headers, body, null, ResponseStatus.SUCCESS);
	}
	public static <T> RestSearchResponse<T> successResponse(List<T> body)
	{
		return new RestSearchResponse<>(new RestSearchResponseHeaders(0, 0, 0, false), body, null, ResponseStatus.SUCCESS);
	}

	public static <T> RestSearchResponse<T> successResponse(List<T> body, int page, int perPage, int total)
	{
		return successResponse(new RestSearchResponseHeaders(), body, page, perPage, total);
	}
	public static <T> RestSearchResponse<T> successResponseOnePage(List<T> body)
	{
		return successResponse(new RestSearchResponseHeaders(), body, 1, body.size(), body.size());
	}

	public static <T> RestSearchResponse<T> errorResponse(RestSearchResponseHeaders headers, RestResponseError error)
	{
		return new RestSearchResponse<>(headers, null, error, ResponseStatus.ERROR);
	}

	public static <T> RestSearchResponse<T> errorResponse(RestResponseError error)
	{
		return errorResponse(new RestSearchResponseHeaders(), error);
	}
	public static <T> RestSearchResponse<T> errorResponse(String errorMessage)
	{
		return errorResponse(new RestResponseError(errorMessage));
	}
}
