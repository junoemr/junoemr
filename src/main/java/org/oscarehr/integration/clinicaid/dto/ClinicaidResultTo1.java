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

package org.oscarehr.integration.clinicaid.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicaidResultTo1 implements Serializable
{
	private final String ERROR_STRING = "error";


	private String result;

	@JsonProperty("data")
	private ClinicaidResultDataTo1 data;

	private ClinicaidErrorResultTo1 errors;

	private ClinicaidApiLimitInfoTo1 apiLimitInfo;

	private String nonce;

	private boolean hasError = false;

	@JsonProperty("meta")
	private void unpackApiLimitInfo(Map<String, Object> metaInfo) throws IOException
	{
		this.apiLimitInfo = new ObjectMapper().convertValue(metaInfo.get("api_rate_limit_info"), ClinicaidApiLimitInfoTo1.class);
	}

	public ClinicaidApiLimitInfoTo1 getApiLimitInfo()
	{
		return apiLimitInfo;
	}

	public String getResult()
	{
		return this.result;
	}

	public void setResult(String result)
	{
		this.result = result;
		if (this.result.equals(ERROR_STRING))
		{
			this.hasError = true;
		}
	}

	public String getNonce()
	{
		return this.nonce;
	}

	public ClinicaidResultDataTo1 getData()
	{
		return this.data;
	}

	public ClinicaidErrorResultTo1 getErrors()
	{
		return this.errors;
	}

	public void setErrors(ClinicaidErrorResultTo1 errors)
	{
		this.errors = errors;
	}

	public boolean hasError()
	{
		return this.hasError;
	}
}
