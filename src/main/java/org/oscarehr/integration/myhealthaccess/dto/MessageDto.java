/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

package org.oscarehr.integration.myhealthaccess.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.oscarehr.messaging.model.MessageGroup;
import org.oscarehr.messaging.model.MessageableType;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class MessageDto implements Serializable
{
	protected String id;
	@JsonProperty("conversation_id")
	protected String conversationId;
	protected String subject;
	protected String message;
	@JsonProperty("is_read")
	protected Boolean read;
	protected MessageGroup group;
	@JsonProperty("created_at")
	protected ZonedDateTime createdAtDateTime;

	@JsonProperty("sender_id")
	protected String senderId;
	@JsonProperty("sender_type")
	protected MessageableType senderType;
	@JsonProperty("sender_name")
	protected String senderName;
	@JsonProperty("meta")
	protected String metaData;

	protected List<MessageParticipantDto> recipients;
}
