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

package org.oscarehr.messaging.backend.myhealthaccess.service;

import org.apache.commons.lang.NotImplementedException;
import org.oscarehr.common.exception.NoSuchRecordException;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.dto.MessageDto;
import org.oscarehr.integration.myhealthaccess.service.ClinicMessagingService;
import org.oscarehr.messaging.backend.myhealthaccess.conversion.ConversationDtoToMhaConversationConverter;
import org.oscarehr.messaging.backend.myhealthaccess.conversion.MessageDtoToMhaMessageConverter;
import org.oscarehr.messaging.backend.myhealthaccess.conversion.MhaMessageToMessageDtoConverter;
import org.oscarehr.messaging.backend.myhealthaccess.model.MhaAttachment;
import org.oscarehr.messaging.backend.myhealthaccess.model.MhaMessage;
import org.oscarehr.messaging.model.*;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;

@Service("mhaMessagingService")
public class MessagingService implements org.oscarehr.messaging.service.MessagingService
{
	private ClinicMessagingService clinicMessagingService;
	private IntegrationDao integrationDao;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public MessagingService(ClinicMessagingService clinicMessagingService, IntegrationDao integrationDao)
	{
		this.clinicMessagingService = clinicMessagingService;
		this.integrationDao = integrationDao;
	}

	// ==========================================================================
	// Messaging Service override methods
	// ==========================================================================

	/**
	 * get a message for the messageable by id.
	 *
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable  - the messageable (user) who owns the message.
	 * @param messageId    - the message id to fetch.
	 * @return - the message.
	 * @throws org.oscarehr.common.exception.NoSuchRecordException - if a message with the given id cannot be found.
	 */
	@Override
	public Message getMessage(LoggedInInfo loggedInInfo, Messageable<?> messageable, String messageId)
	{
		Integration integration = this.getIntegrationFromMessageable(messageable);

		return (new MessageDtoToMhaMessageConverter()).convert(clinicMessagingService.getMessage(integration, loggedInInfo, messageId));
	}

	/**
	 * update attributes of message (save)
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) who owns the message.
	 * @param message - the message to update (save).
	 * @return - a fresh copy of the message after the update.
	 * @throws IllegalArgumentException - if the messaging backend doesn't support the type of message passed in.
	 */
	public Message updateMessage(LoggedInInfo loggedInInfo, Messageable<?> messageable, Message message)
	{
		if (message instanceof MhaMessage)
		{
			MessageDto messageDto = (new MhaMessageToMessageDtoConverter()).convert((MhaMessage) message);

			return (new MessageDtoToMhaMessageConverter()).convert(
					this.clinicMessagingService.updateMessage(getIntegrationFromMessageable(messageable), loggedInInfo, messageDto));
		}
		else
		{
			throw new IllegalArgumentException("MHA messaging service can only update MhaMessage messages");
		}
	}

	/**
	 * get messages for a messageable, filtering by the provided parameters.
	 *
	 * @param loggedInInfo  - currently logged in user info
	 * @param messageable   - the messageable (user) you want to get messages for.
	 * @param startDateTime - [optional] filter messages to only those that where sent after this time.
	 * @param endDateTime   - [optional] filter messages to only those that where sent before this time.
	 * @param group         - [optional] filter messages by group. Group ALL is equivalent to null.
	 * @param limit         - [optional] limit results to this number (use for paging).
	 * @param offset        - [optional] offset results by this number (use for paging).
	 * @param onlyUnread 		- [optional] if true only unread messages will be returned
	 * @param keyword 			- [optional] if not null filter messages to ones containing this keyword in the title / subject
	 * @param sender        - [optional] filter messages to only those sent by this sender.
	 * @param receiver      - [optional] filter messages to only those received by this recipient.
	 * @return - a list of messages.
	 * @throws IllegalArgumentException - if the backend does not support one of the provided arguments
	 */
	@Override
	public List<? extends Message> getMessages(
			LoggedInInfo loggedInInfo,
			Messageable<?> messageable,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable MessageGroup group,
			@Nullable Integer limit,
			@Nullable Integer offset,
			@Nullable Boolean onlyUnread,
			@Nullable String keyword,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver)
	{
		Integration integration = this.getIntegrationFromMessageable(messageable);
		List<MessageDto> messages = clinicMessagingService.getMessages(
				integration,
				loggedInInfo,
				startDateTime,
				endDateTime,
				group,
				limit,
				offset,
				onlyUnread,
				keyword,
				sender,
				receiver);

		return (new MessageDtoToMhaMessageConverter()).convert(messages);
	}

	/**
	 * get the total count of messages in a group for the given messageable
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable whose group is being counted.
	 * @param startDateTime - [optional] filter messages to only those that where sent after this time.
	 * @param endDateTime - [optional] filter messages to only those that where sent before this time.
	 * @param group - the group to count
	 * @param onlyUnread - [optional] if true only unread messages will be returned
	 * @param keyword - [optional] if not null filter messages to ones containing this keyword in the title / subject
	 * @param sender - [optional] filter messages to only those sent by this sender.
	 * @param receiver - [optional] filter messages to only those received by this recipient.
	 * @return - the count of messages in the group
	 */
	@Override
	public Integer countMessagesInGroup(
			LoggedInInfo loggedInInfo,
			Messageable<?> messageable,
			MessageGroup group,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable Boolean onlyUnread,
			@Nullable String keyword,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver
	)
	{
		return this.clinicMessagingService.countMessages(
				getIntegrationFromMessageable(messageable),
				loggedInInfo,
				group,
				startDateTime,
				endDateTime,
				onlyUnread,
				keyword,
				sender,
				receiver);
	}

	/**
	 * get a conversation for the messageable by id.
	 *
	 * @param loggedInInfo   - currently logged in user info
	 * @param messageable    - the messageable (user) whose conversation is to be pulled.
	 * @param conversationId - the conversation id to fetch.
	 * @return - the conversation.
	 * @throws org.oscarehr.common.exception.NoSuchRecordException - if a conversation with the given id cannot be found.
	 */
	@Override
	public Conversation getConversation(LoggedInInfo loggedInInfo, Messageable<?> messageable, String conversationId)
	{
		Integration integration = this.getIntegrationFromMessageable(messageable);

		return (new ConversationDtoToMhaConversationConverter()).convert(clinicMessagingService.getConversation(integration, loggedInInfo, conversationId));
	}

	/**
	 * get conversations for messageable filtered by the provided parameters
	 *
	 * @param loggedInInfo  - currently logged in user info
	 * @param messageable   - the messageable (user) who's conversations are to be searched
	 * @param startDateTime - [optional] filter conversations to only those that where sent after this time.
	 * @param endDateTime   - [optional] filter conversations to only those that where sent before this time.
	 * @param group         - [optional] filter conversations by group. Group ALL is equivalent to null.
	 * @param limit         - [optional] limit results to this number (use for paging).
	 * @param offset        - [optional] offset results by this number (use for paging).
	 * @param sender        - [optional] filter conversations to only those sent by this sender.
	 * @param receiver      - [optional] filter conversations to only those received by this recipient.
	 * @return - list of conversatiosn
	 * @throws IllegalArgumentException - if the backend does not support one of the provided arguments
	 */
	@Override
	public List<? extends Conversation> getConversations(
			LoggedInInfo loggedInInfo,
			Messageable<?> messageable,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable MessageGroup group,
			@Nullable Integer limit,
			@Nullable Integer offset,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver)
	{
		throw new NotImplementedException();
	}

	/**
	 * get the total count of conversations in a group for a messageable
	 *
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable  - the messageable whose group is to be counted
	 * @param group        - the group to count
	 * @return - the count of conversations in the group
	 */
	@Override
	public Number countConversationsInGroup(LoggedInInfo loggedInInfo, Messageable<?> messageable, MessageGroup group)
	{
		throw new NotImplementedException();
	}

	/**
	 * send a message
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) who owns the message.
	 * @param message - the message to send
	 * @return - the message that was just sent.
	 * @throws IllegalArgumentException if the passed in Message is not an MhaMessage
	 */
	@Override
	public Message sendMessage(LoggedInInfo loggedInInfo, Messageable<?> messageable, Message message)
	{
		if (message instanceof MhaMessage)
		{
			MessageDto messageDto = (new MhaMessageToMessageDtoConverter()).convert((MhaMessage) message);

			return (new MessageDtoToMhaMessageConverter()).convert(
					this.clinicMessagingService.sendMessage(getIntegrationFromMessageable(messageable), loggedInInfo, messageDto));
		}
		else
		{
			throw new IllegalArgumentException("MHA messaging service can only send MhaMessage messages");
		}
	}

	/**
	 * reply to a conversation
	 *
	 * @param loggedInInfo - currently logged in user info
	 * @param message      - the reply to the conversation
	 * @param conversation - the conversation being replied to.
	 * @return - the message that was just appended as a reply to a conversation.
	 */
	@Override
	public Message replyToConversation(LoggedInInfo loggedInInfo, Message message, Conversation conversation)
	{
		throw new NotImplementedException();
	}

	/**
	 * get the binary data for the specified attachment
	 *
	 * @param loggedInInfo - logged info for the current user
	 * @param messageable  - the messageable who owns the attachment
	 * @param attachment   - the attachment to get data for
	 * @return - binary attachment data
	 * @throws IllegalArgumentException - if the provided attachment is not an MhaAttachment
	 */
	public byte[] getAttachmentData(LoggedInInfo loggedInInfo, Messageable<?> messageable, Attachment attachment)
	{
		if (attachment instanceof MhaAttachment)
		{
			return this.clinicMessagingService.downloadAttachmentData(getIntegrationFromMessageable(messageable), loggedInInfo, (MhaAttachment) attachment);
		}
		else
		{
			throw new IllegalArgumentException("MHA messaging service can only download attachment data for MhaAttachments!");
		}
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * get MHA integration from messageable
	 * @param messageable - the messageable to get the integration from.
	 * @return - mha integration
	 * @throws UnsupportedOperationException if the messageable type is not supported
	 * @throws NoSuchRecordException if the integration cannot be found
	 */
	protected Integration getIntegrationFromMessageable(Messageable<?> messageable)
	{
		if (messageable.getType().equals(MessageableType.MHA_CLINIC))
		{
			Integration integration = integrationDao.findByIntegrationAndRemoteId(messageable.getId(), Integration.INTEGRATION_TYPE_MHA);
			if (integration == null)
			{
				integration = integrationDao.findByIntegrationAndRemoteId(messageable.getId(), Integration.INTEGRATION_TYPE_CLOUD_MD);
			}

			if (integration == null)
			{
				throw new NoSuchRecordException("No MHA integration with remote_id: " + messageable.getId() + " Could be found.");
			}

			return integration;
		}
		else
		{
			throw new UnsupportedOperationException(
					"MHA Messaging Backend does not support mailbox operations for messageable of type: " + messageable.getType().toString());
		}
	}
}
