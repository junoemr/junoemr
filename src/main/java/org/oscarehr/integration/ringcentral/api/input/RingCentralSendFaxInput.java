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

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RingCentralSendFaxInput
{
	@JsonProperty("attachment")
	private String attachment; // required

	/* Resolution of Fax (High, Low) */
	@JsonProperty("faxResolution")
	private String faxResolution;

	/* To Phone Number(s) */
	@JsonProperty("to")
	private String[] to; // required

	/* Timestamp to send fax at. If not specified (current or the past), the fax is sent immediately */
	@JsonProperty("sendTime")
	private String sendTime;

	/* ISO Code. e.g UK */
	@JsonProperty("isoCode")
	private String isoCode;

	/*
	Cover page identifier. If coverIndex is set to '0' (zero) cover page is not attached.
	For the list of available cover page identifiers (1-13) please call the Fax Cover Pages method.
	If not specified, the default cover page is attached (which is configured in 'Outbound Fax Settings')
	 */
	@JsonProperty("coverIndex")
	private Integer coverIndex;

	/* Cover page text, entered by the fax sender and printed on the cover page. Maximum length is limited to 1024 symbols */
	@JsonProperty("coverPageText")
	private String coverPageText;
}
