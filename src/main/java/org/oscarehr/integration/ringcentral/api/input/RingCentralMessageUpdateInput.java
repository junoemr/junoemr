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
package org.oscarehr.integration.ringcentral.api.input;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import oscar.util.ConversionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class RingCentralMessageUpdateInput
{
	@JsonProperty("readStatus")
	private String readStatus;

	@JsonIgnore
	private ZonedDateTime dateFrom;

	@JsonIgnore
	private String messageType;

	@JsonIgnore
	public Map<String, Object> getParameterMap()
	{
		Map<String, Object> parameters = new HashMap<>();
		putOptionalParam(parameters, "dateFrom", ConversionUtils.toNullableDateTimeString(dateFrom, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		putOptionalParam(parameters, "type", messageType);
		return parameters;
	}

	@JsonIgnore
	private void putOptionalParam(Map<String, Object> parameters, String key, Object value)
	{
		if(value != null)
		{
			parameters.put(key, value);
		}
	}
}
