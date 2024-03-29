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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericErrorTo1 implements Serializable
{
	public static final String ERROR_UNKNOWN = "error_unknown";
	public static final String ERROR_AUTHENTICATION = "error_authentication";
	public static final String ERROR_ACCESS = "error_access";
	public static final String ERROR_CLINIC_ACCESS = "error_clinic_access";
	public static final String ERROR_SESSION_EXPIRED = "error_session_expired";
	public static final String ERROR_RECORD_NOT_FOUND = "error_record_not_found";
	public static final String ERROR_VALIDATION = "error_field_validation";
	public static final String ERROR_DUPLICATE_RECORD = "error_duplicate_record";

	@Getter
	@Setter
	@JsonProperty("code")
	private String code;

	@Getter
	@Setter
	@JsonProperty("message")
	private String message;

	@Getter
	@Setter
	@JsonProperty("error_description")
	private String errorDescription;

	@Getter
	@Setter
	@JsonProperty("data")
	private Map<String, String> data;

	@Override
	public String toString()
	{
		String dataString = "";
		if (this.data != null)
		{
			dataString = this.data.entrySet().stream().map((entry) -> entry.getKey() + ": " + entry.getValue())
					.collect(Collectors.joining("\n"));
		}

		return "Error Code: " + this.code +
				"\nError Message: " + this.message +
				"\nError Description: " + this.errorDescription +
				"\nError Data: " + dataString;
	}
}
