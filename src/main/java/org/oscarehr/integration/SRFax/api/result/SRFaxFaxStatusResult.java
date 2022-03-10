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
package org.oscarehr.integration.SRFax.api.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.oscarehr.fax.result.FaxStatusResult;
import oscar.util.ConversionUtils;
import java.util.Date;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SRFaxFaxStatusResult implements FaxStatusResult
{
	@JsonProperty("FileName")
	private String rawFileName;
	@JsonIgnore
	private String fileName;
	@JsonIgnore
	private String detailsId;
	@JsonProperty("SentStatus")
	private String sentStatus;

	/**
	 * Datetime the fax was queued, format is determined by account preference
	 */
	@JsonProperty("DateQueued")
	private String dateQueued;

	/**
	 * Datetime the fax was sent, format is determined by account preference.
	 * A consistent format for this field found in GetFaxStatusResult.epochTime
	 */
	@JsonProperty("DateSent")
	private String dateSent;

	/**
	 * Datetime the fax was sent, as seconds since the UNIX epoch.
	 */
	@JsonProperty("EpochTime")
	private String epochTime;
	
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

	public String getEpochTime()
	{
		return epochTime;
	}

	public void setEpochTime(String epochTime)
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
	
	/*
	 * Interface Methods
	 */
	@Override
	public String getRemoteSentStatus()
	{
		return this.sentStatus;
	}

	@Override
	public Optional<Date> getRemoteSendTime()
	{
		Date remoteSendTime = null;
		String secondsSinceEpoch = this.getEpochTime();

		if (ConversionUtils.hasContent(secondsSinceEpoch))
		{
			remoteSendTime = ConversionUtils.fromEpochStringSeconds(secondsSinceEpoch);
		}

		return Optional.ofNullable(remoteSendTime);
	}

	@Override
	public Optional<String> getError()
	{
		return Optional.ofNullable(this.errorCode);
	}
}