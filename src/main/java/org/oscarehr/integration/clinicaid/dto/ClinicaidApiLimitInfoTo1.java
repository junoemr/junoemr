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

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClinicaidApiLimitInfoTo1 implements Serializable
{
	@JsonProperty("current_call_count")
	private int currentCallCount;

	@JsonProperty("call_limit")
	private int callLimit;

	@JsonProperty("reset_interval")
	private int resetInterval;

	@JsonProperty("interval_expire_seconds")
	private int intervalExpireSeconds;

	@JsonProperty("limit_reached")
	private boolean limitReached;

	public int getCurrentCallCount()
	{
		return currentCallCount;
	}

	public int getCallLimit()
	{
		return callLimit;
	}

	public int getResetInterval()
	{
		return resetInterval;
	}

	public int getIntervalExpireSeconds()
	{
		return intervalExpireSeconds;
	}

	public boolean isLimitReached()
	{
		return limitReached;
	}
}
