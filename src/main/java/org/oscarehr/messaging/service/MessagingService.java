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

package org.oscarehr.messaging.service;

import org.oscarehr.messaging.model.*;
import org.oscarehr.util.LoggedInInfo;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;

public interface MessagingService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * get a message for the messageable by id.
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) who owns the message.
	 * @param messageId - the message id to fetch.
	 * @return - the message.
	 * @throws org.oscarehr.common.exception.NoSuchRecordException - if a message with the given id cannot be found.
	 */
	public Message getMessage(LoggedInInfo loggedInInfo, Messageable<?> messageable, String messageId);

	/**
	 * get messages for a messageable, filtering by the provided parameters.
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) you want to get messages for.
	 * @param startDateTime - [optional] filter messages to only those that where sent after this time.
	 * @param endDateTime - [optional] filter messages to only those that where sent before this time.
	 * @param group - [optional] filter messages by group. Group ALL is equivalent to null.
	 * @param limit - [optional] limit results to this number (use for paging).
	 * @param offset - [optional] offset results by this number (use for paging).
	 * @param sender - [optional] filter messages to only those sent by this sender.
	 * @param receiver - [optional] filter messages to only those received by this recipient.
	 * @return - a list of messages.
	 * @throws IllegalArgumentException - if the backend does not support one of the provided arguments
	 */
	public List<? extends Message> getMessages(
			LoggedInInfo loggedInInfo,
			Messageable<?> messageable,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable MessageGroup group,
			@Nullable Integer limit,
			@Nullable Integer offset,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver);

	/**
	 * get the total count of messages in a group for the given messageable
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable whose group is being counted.
	 * @param group - the group to count
	 * @return - the count of messages in the group
	 */
	public Number countMessagesInGroup(LoggedInInfo loggedInInfo, Messageable<?> messageable, MessageGroup group);

	/**
	 * get a conversation for the messageable by id.
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) whose conversation is to be pulled.
	 * @param conversationId - the conversation id to fetch.
	 * @return - the conversation.
	 * @throws org.oscarehr.common.exception.NoSuchRecordException - if a conversation with the given id cannot be found.
	 */
	public Conversation getConversation(LoggedInInfo loggedInInfo, Messageable<?> messageable, String conversationId);

	/**
	 * get conversations for messageable filtered by the provided parameters
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable (user) who's conversations are to be searched
	 * @param startDateTime - [optional] filter conversations to only those that where sent after this time.
	 * @param endDateTime - [optional] filter conversations to only those that where sent before this time.
	 * @param group - [optional] filter conversations by group. Group ALL is equivalent to null.
	 * @param limit - [optional] limit results to this number (use for paging).
	 * @param offset - [optional] offset results by this number (use for paging).
	 * @param sender - [optional] filter conversations to only those sent by this sender.
	 * @param receiver - [optional] filter conversations to only those received by this recipient.
	 * @return - list of conversatiosn
	 * @throws IllegalArgumentException - if the backend does not support one of the provided arguments
	 */
	public List<? extends Conversation> getConversations(
			LoggedInInfo loggedInInfo,
			Messageable<?> messageable,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable MessageGroup group,
			@Nullable Integer limit,
			@Nullable Integer offset,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver);

	/**
	 * get the total count of conversations in a group for a messageable
	 * @param loggedInInfo - currently logged in user info
	 * @param messageable - the messageable whose group is to be counted
	 * @param group - the group to count
	 * @return - the count of conversations in the group
	 */
	public Number countConversationsInGroup(LoggedInInfo loggedInInfo, Messageable<?> messageable, MessageGroup group);

	/**
	 * send a message
	 * @param loggedInInfo - currently logged in user info
	 * @param message - the message to send
	 * @return - the message that was just sent.
	 */
	public Message sendMessage(LoggedInInfo loggedInInfo, Message message);

	/**
	 * reply to a conversation
	 * @param loggedInInfo - currently logged in user info
	 * @param message - the reply to the conversation
	 * @param conversation - the conversation being replied to.
	 * @return - the message that was just appended as a reply to a conversation.
	 */
	public Message replyToConversation(LoggedInInfo loggedInInfo, Message message, Conversation conversation);

	/**
	 * get the binary data for the specified attachment
	 * @param loggedInInfo - logged info for the current user
	 * @param messageable - the messageable who owns the attachment
	 * @param attachment - the attachment to get data for
	 * @return - binary attachment data
	 */
	public byte[] getAttachmentData(LoggedInInfo loggedInInfo, Messageable<?> messageable, Attachment attachment);
}
