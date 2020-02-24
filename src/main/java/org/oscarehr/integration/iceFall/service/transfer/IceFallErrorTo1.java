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
package org.oscarehr.integration.iceFall.service.transfer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import oscar.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallErrorTo1 implements Serializable
{
	@JsonProperty("non_field_errors")
	private List<String> nonFieldErrors = new ArrayList<>();

	@JsonProperty("detail")
	private String errorDetail;

	@JsonIgnore
	private boolean junoInternalError = false;

	public List<String> getNonFieldErrors()
	{
		return nonFieldErrors;
	}

	public void setNonFieldErrors(List<String> nonFieldErrors)
	{
		this.nonFieldErrors = nonFieldErrors;
	}

	public boolean isJunoInternalError()
	{
		return junoInternalError;
	}

	public String getErrorDetail()
	{
		return errorDetail;
	}

	public void setErrorDetail(String errorDetail)
	{
		this.errorDetail = errorDetail;
	}

	public void setJunoInternalError(boolean junoInternalError)
	{
		this.junoInternalError = junoInternalError;
	}

	@Override
	public String toString()
	{
		if (nonFieldErrors != null)
		{
			return StringUtils.join(nonFieldErrors, "\n");
		}
		return "";
	}
}
