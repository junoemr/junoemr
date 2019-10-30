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

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ClinicUserLoginTo1 implements Serializable
{
	@JsonProperty(value="email", required=true)
	private String email;

	@JsonProperty(value="password", required=true)
	private String password;

	@JsonProperty("remote_id")
	private String remoteId;

	public ClinicUserLoginTo1(String email, String password)
	{
		this.email = email;
		this.password = password;
	}

	public ClinicUserLoginTo1(String email, String password, String remoteId)
	{
		this.email = email;
		this.password = password;
		this.remoteId = remoteId;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}
}
