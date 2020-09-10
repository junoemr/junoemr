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
package org.oscarehr.ws.rest.transfer.providerManagement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.oscarehr.common.model.Security;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SecurityRecordTo1
{
	private Integer securityNo;
	private String userName;
	private String email;
	private String password;
	private String providerNo;
	private String pin;

	public static List<SecurityRecordTo1> fromList(List<Security> securityList)
	{
		ArrayList<SecurityRecordTo1> newSecurityTransfers = new ArrayList<>();
		for (Security security : securityList)
		{
			newSecurityTransfers.add(new SecurityRecordTo1(security));
		}
		return newSecurityTransfers;
	}

	public SecurityRecordTo1(Security security)
	{
		this.securityNo = security.getSecurityNo();
		this.userName 	= security.getUserName();
		this.email 			= security.getEmail();
		this.providerNo = security.getProviderNo();
		// password and pin intentionally omitted
	}

	public SecurityRecordTo1(){};

	/**
	 * convert transfer object to security record
	 * @return current entry as a Security object
	 */
	@JsonIgnore
	public Security getSecurity()
	{
		Security security = new Security();
		security.setSecurityNo(this.getSecurityNo());
		security.setUserName(this.getUserName());
		security.setEmail(this.getEmail());
		security.setPassword(this.getPassword());
		security.setProviderNo(this.getProviderNo());
		security.setPin(this.getPin());
		return security;
	}

	public Integer getSecurityNo()
	{
		return securityNo;
	}

	public void setSecurityNo(Integer securityNo)
	{
		this.securityNo = securityNo;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
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

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public String getPin()
	{
		return pin;
	}

	public void setPin(String pin)
	{
		this.pin = pin;
	}
}
