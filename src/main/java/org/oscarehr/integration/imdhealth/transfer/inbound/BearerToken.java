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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import oscar.util.Jackson.UnixTimeDeserializer;

import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BearerToken implements Serializable
{
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("token_type")
	private String tokenType;
	@JsonProperty("created_at")
	@JsonDeserialize(using = UnixTimeDeserializer.class)
	private Date createdAt;

	private static final int EXPIRY_TIME_HOURS = 24;
	
	/**
	 * Check if the bearer token is expired.  According to the latest documentation, a bearer token is valid
	 * for 24 hours after the created_at date.
	 * 
	 * @return true if expired, false otherwise
	 */
	public boolean isExpired()
	{
		if (createdAt == null)
		{
			return true;
		}

		Instant expiryTime = createdAt.toInstant().plus(EXPIRY_TIME_HOURS, ChronoUnit.HOURS);
		return Instant.now().isAfter(expiryTime);
	}
}
