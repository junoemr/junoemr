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
package org.oscarehr.fax.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true) // Ignore properties that are not defined in this class
public abstract class FaxAccountTransferBasic implements Serializable
{
	private Boolean enabled;
	private Boolean enableInbound;
	private Boolean enableOutbound;
	private String accountEmail;
	private String displayName;
	private String faxNumber;
	private String coverLetterOption;

	public Boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public Boolean isEnableInbound()
	{
		return enableInbound;
	}

	public void setEnableInbound(Boolean enableInbound)
	{
		this.enableInbound = enableInbound;
	}

	public Boolean isEnableOutbound()
	{
		return enableOutbound;
	}

	public void setEnableOutbound(Boolean enableOutbound)
	{
		this.enableOutbound = enableOutbound;
	}

	public String getAccountEmail()
	{
		return accountEmail;
	}

	public void setAccountEmail(String accountEmail)
	{
		this.accountEmail = accountEmail;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getFaxNumber()
	{
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber)
	{
		this.faxNumber = faxNumber;
	}

	public String getCoverLetterOption()
	{
		return coverLetterOption;
	}

	public void setCoverLetterOption(String coverLetterOption)
	{
		this.coverLetterOption = coverLetterOption;
	}
}
