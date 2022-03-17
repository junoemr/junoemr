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
package org.oscarehr.integration.ringcentral.api.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.oscarehr.fax.result.FaxInboxResult;
import org.oscarehr.fax.result.FaxStatusResult;
import oscar.util.ConversionUtils;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RingCentralMessageInfoResult implements RingCentralResult, FaxStatusResult, FaxInboxResult
{
	@JsonProperty("id")
	private Long id;

	@JsonProperty("uri")
	private String uri;

	@JsonProperty("extensionId")
	private String extensionId;

	@JsonProperty("attachments")
	private RingCentralAttachment[] attachments;

	@JsonProperty("availability")
	private Availability availability;

	@JsonProperty("conversationId")
	private String conversationId;

	@JsonProperty("conversation")
	private Object[] conversation;

	@JsonProperty("creationTime")
	private String creationTime;

	@JsonProperty("deliveryErrorCode")
	private String deliveryErrorCode;

	@JsonProperty("direction")
	private Direction direction;

	@JsonProperty("faxPageCount")
	private Integer faxPageCount;

	@JsonProperty("faxResolution")
	private FaxResolution faxResolution;

	@JsonProperty("from")
	private RingCentralSenderInformation from;

	@JsonProperty("lastModifiedTime")
	private ZonedDateTime lastModifiedTime;

	@JsonProperty("messageStatus")
	private MessageStatus messageStatus;

	@JsonProperty("pgToDepartment")
	private Boolean pgToDepartment;

	@JsonProperty("priority")
	private Priority priority;

	@JsonProperty("readStatus")
	private ReadStatus readStatus;

	@JsonProperty("smsDeliveryTime")
	private String smsDeliveryTime;

	@JsonProperty("smsSendingAttemptsCount")
	private Integer smsSendingAttemptsCount;

	@JsonProperty("subject")
	private String subject;

	@JsonProperty("to")
	private Object[] to;

	@JsonProperty("type")
	private MessageType type;

	@JsonProperty("vmTranscriptionStatus")
	private String vmTranscriptionStatus;

	@JsonProperty("coverIndex")
	private Integer coverIndex;

	@JsonProperty("coverPageText")
	private String coverPageText;


	@Override
	@JsonIgnore
	public String getRemoteSentStatus()
	{
		return this.getMessageStatus().name();
	}

	@Override
	@JsonIgnore
	public Optional<Date> getRemoteSendTime()
	{
		if(this.getMessageStatus().equals(MessageStatus.Delivered))
		{
			return Optional.of(ConversionUtils.toLegacyDateTime(this.getLastModifiedTime().toLocalDateTime()));//TODO timezone conversion?
		}
		return Optional.empty();
	}

	@Override
	@JsonIgnore
	public Optional<String> getError()
	{
		if(MessageStatus.DeliveryFailed.equals(this.getMessageStatus()))
		{
			return Optional.of(MessageStatus.DeliveryFailed.name());
		}
		else if(MessageStatus.SendingFailed.equals(this.getMessageStatus()))
		{
			return Optional.of(MessageStatus.SendingFailed.name());
		}
		return Optional.empty();
	}

	@Override
	public String getDetailsId()
	{
		return String.valueOf(this.getId());
	}

	@Override
	public String getCallerId()
	{
		return from.getPhoneNumber();
	}
}
