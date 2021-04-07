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

package org.oscarehr.integration.imdhealth.transfer.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SSOSessionCredentials implements Serializable
{
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * The full SSO link, containing the components below.
	 * It's recommended in the documentation that this field be used to connect, rather than assembling it ourselves.
	 */
	@JsonProperty("imd_url")
	private String imdUrl;

	@JsonProperty("membership_id")
	private String membershipId;

	@JsonProperty("organization_id")
	private String organizationId;

	@JsonProperty("patient_session_id")
	private String patientSessionId;

	// Implementation TBD below this line

	@JsonProperty("resources")
	private Map<String, String> resources;

	@JsonProperty("topic_id")
	private String topicId;
}
