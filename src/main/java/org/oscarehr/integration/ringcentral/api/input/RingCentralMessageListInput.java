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

import lombok.Data;
import oscar.util.ConversionUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Data
public class RingCentralMessageListInput
{
	private String[] availability;
	private Long conversationId;
	private ZonedDateTime dateTo;
	private ZonedDateTime dateFrom;
	private String[] direction;
	private Boolean distinctConversations;
	private String[] messageType;
	private String[] readStatus;
	private Integer page;
	private Integer perPage;
	private String phoneNumber;

	public Map<String, Object> toParameterMap()
	{
		Map<String, Object> parameters = new HashMap<>();
		putOptionalParam(parameters, "availability", availability);
		putOptionalParam(parameters, "conversationId", conversationId);
		putOptionalParam(parameters, "dateFrom", ConversionUtils.toNullableDateTimeString(dateFrom, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		putOptionalParam(parameters, "dateTo", ConversionUtils.toNullableDateTimeString(dateTo, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
		putOptionalParam(parameters, "direction", direction);
		putOptionalParam(parameters, "distinctConversations", distinctConversations);
		putOptionalParam(parameters, "messageType", messageType);
		putOptionalParam(parameters, "readStatus", readStatus);
		putOptionalParam(parameters, "page", page);
		putOptionalParam(parameters, "perPage", perPage);
		putOptionalParam(parameters, "phoneNumber", phoneNumber);

		return parameters;
	}

	private void putOptionalParam(Map<String, Object> parameters, String key, Object value)
	{
		if(value != null)
		{
			parameters.put(key, value);
		}
	}
}
