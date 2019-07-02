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

package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public class BaseErrorTo1 implements Serializable
{
	@JsonProperty("generic_errors")
	private List<GenericErrorTo1> genericErrors;

	@JsonProperty("auth_error")
	private GenericErrorTo1 authError;

	public List<GenericErrorTo1> getGenericErrors()
	{
		return genericErrors;
	}

	public void setGenericErrors(List<GenericErrorTo1> genericErrors)
	{
		this.genericErrors = genericErrors;
	}

	public void addGenericError(String code, String message)
	{
		this.genericErrors = new ArrayList<GenericErrorTo1>();
		GenericErrorTo1 genericError = new GenericErrorTo1();
		genericError.setCode(code);
		genericError.setMessage(message);
		this.genericErrors.add(genericError);
	}

	public GenericErrorTo1 getAuthError()
	{
		return authError;
	}

	public void setAuthError(GenericErrorTo1 authError)
	{
		this.authError = authError;
	}

	public boolean hasGenericErrors()
	{
		return genericErrors != null && genericErrors.size() > 0;
	}

	public boolean hasAuthError()
	{
		return authError != null;
	}
}
