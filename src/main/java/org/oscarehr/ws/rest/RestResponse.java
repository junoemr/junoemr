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

package org.oscarehr.ws.rest;

import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;

public class RestResponse<T, E> extends HttpEntity<T> {

	public enum ResponseStatus {SUCCESS, ERROR}

	private final E error;
	private final ResponseStatus status;

	public RestResponse(MultiValueMap headers, T body) {
		this(headers, body, null, ResponseStatus.SUCCESS);
	}

	public RestResponse(MultiValueMap headers, T body, E error) {
		this(headers, body, error, ResponseStatus.ERROR);
	}

	public RestResponse(MultiValueMap headers, T body, E error, ResponseStatus status) {
		super(body, headers);
		this.error = error;
		this.status = status;
	}

	public E getError() {
		return error;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public static<T, E> RestResponse<T,E> successResponse(MultiValueMap headers, T body) {
		return new RestResponse<T, E>(headers, body, null, ResponseStatus.SUCCESS);
	}
	public static<T, E> RestResponse<T,E> errorResponse(MultiValueMap headers, E error) {
		return new RestResponse<T, E>(headers, null, error, ResponseStatus.ERROR);
	}
}