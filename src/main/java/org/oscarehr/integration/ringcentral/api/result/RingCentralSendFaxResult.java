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
package org.oscarehr.integration.ringcentral.api.result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RingCentralSendFaxResult implements RingCentralResult
{
	enum FaxResolution {
		High,
		Low,
	}

	@JsonProperty("id")
	private Long id;

	@JsonProperty("uri")
	private String uri;

	@JsonProperty("type")
	private String type;

	@JsonProperty("from")
	private Object from;

	@JsonProperty("to")
	private Object[] to; //todo custom objects

	@JsonProperty("creationTime")
	private String creationTime;

	@JsonProperty("readStatus")
	private ReadStatus readStatus;

	@JsonProperty("priority")
	private Priority priority;

	@JsonProperty("attachments")
	private Object[] attachments;

	@JsonProperty("direction")
	private Direction direction;

	@JsonProperty("availability")
	private String availability;

	@JsonProperty("messageStatus")
	private MessageStatus messageStatus;

	@JsonProperty("faxResolution")
	private FaxResolution faxResolution;

	@JsonProperty("faxPageCount")
	private Integer faxPageCount;

	@JsonProperty("lastModifiedTime")
	private String lastModifiedTime;

	@JsonProperty("coverIndex")
	private Integer coverIndex;

	@JsonProperty("coverPageText")
	private String coverPageText;
}
