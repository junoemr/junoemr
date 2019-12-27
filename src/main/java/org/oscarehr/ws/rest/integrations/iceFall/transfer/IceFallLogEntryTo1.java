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
package org.oscarehr.ws.rest.integrations.iceFall.transfer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.oscarehr.integration.iceFall.model.IceFallLog;
import oscar.util.ConversionUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IceFallLogEntryTo1 implements Serializable
{
	private String status;
	private String message;
	private String providerNo;
	private Integer fdid;
	private Boolean isInstance;
	private Integer demographicNo;
	private LocalDateTime dateSent;

	public IceFallLogEntryTo1() {}

	public IceFallLogEntryTo1(IceFallLog iceFallLog)
	{
		status = iceFallLog.getStatus();
		message = iceFallLog.getMessage();
		providerNo = iceFallLog.getSendingProviderNo();
		fdid = iceFallLog.getFormId();
		dateSent = ConversionUtils.toLocalDateTime(iceFallLog.getCreatedAt());
		demographicNo = iceFallLog.getDemographicNo();
		isInstance = iceFallLog.getFormInstance();
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getProviderNo()
	{
		return providerNo;
	}

	public void setProviderNo(String providerNo)
	{
		this.providerNo = providerNo;
	}

	public Integer getFdid()
	{
		return fdid;
	}

	public void setFdid(Integer fdid)
	{
		this.fdid = fdid;
	}

	public LocalDateTime getDateSent()
	{
		return dateSent;
	}

	public void setDateSent(LocalDateTime dateSent)
	{
		this.dateSent = dateSent;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public Boolean getInstance()
	{
		return isInstance;
	}

	public void setInstance(Boolean instance)
	{
		isInstance = instance;
	}
}
