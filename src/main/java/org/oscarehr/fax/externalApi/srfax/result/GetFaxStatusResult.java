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
package org.oscarehr.fax.externalApi.srfax.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetFaxStatusResult
{
	@JsonProperty("FileName")
	private String rawFileName;
	@JsonIgnore
	private String fileName;
	@JsonIgnore
	private String detailsId;
	@JsonProperty("SentStatus")
	private String sentStatus;
	@JsonProperty("DateQueued")
	private String dateQueued;
	@JsonProperty("DateSent")
	private String dateSent;
	@JsonProperty("EpochTime")
	private Long epochTime;
	@JsonProperty("ToFaxNumber")
	private String toFaxNumber;
	@JsonProperty("Pages")
	private String pages;
	@JsonProperty("Duration")
	private String duration;
	@JsonProperty("RemoteID")
	private String remoteId;
	@JsonProperty("ErrorCode")
	private String errorCode;
	@JsonProperty("Size")
	private String size;
	@JsonProperty("AccountCode")
	private String accountCode;


	public String getRawFileName()
	{
		return rawFileName;
	}

	public void setRawFileName(String rawFileName)
	{
		this.rawFileName = rawFileName;
		this.fileName = rawFileName.split("\\|")[0];
		this.detailsId = rawFileName.split("\\|")[1];
	}

	public String getFileName()
	{
		return fileName;
	}

	public String getDetailsId()
	{
		return detailsId;
	}

	public String getSentStatus()
	{
		return sentStatus;
	}

	public void setSentStatus(String sentStatus)
	{
		this.sentStatus = sentStatus;
	}

	public String getDateQueued()
	{
		return dateQueued;
	}

	public void setDateQueued(String dateQueued)
	{
		this.dateQueued = dateQueued;
	}

	public String getDateSent()
	{
		return dateSent;
	}

	public void setDateSent(String dateSent)
	{
		this.dateSent = dateSent;
	}

	public Long getEpochTime()
	{
		return epochTime;
	}

	public void setEpochTime(Long epochTime)
	{
		this.epochTime = epochTime;
	}

	public String getToFaxNumber()
	{
		return toFaxNumber;
	}

	public void setToFaxNumber(String toFaxNumber)
	{
		this.toFaxNumber = toFaxNumber;
	}

	public String getPages()
	{
		return pages;
	}

	public void setPages(String pages)
	{
		this.pages = pages;
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public String getRemoteId()
	{
		return remoteId;
	}

	public void setRemoteId(String remoteId)
	{
		this.remoteId = remoteId;
	}

	public String getErrorCode()
	{
		return errorCode;
	}

	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getAccountCode()
	{
		return accountCode;
	}

	public void setAccountCode(String accountCode)
	{
		this.accountCode = accountCode;
	}

	@Override
	public String toString()
	{
		return new ReflectionToStringBuilder(this).toString();
	}
}
