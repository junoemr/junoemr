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

package org.oscarehr.integration.myhealthaccess.service;

import org.oscarehr.common.model.Site;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.myhealthaccess.client.RestClientBase;
import org.oscarehr.integration.myhealthaccess.client.RestClientFactory;
import org.oscarehr.integration.myhealthaccess.dto.ConversationDto;
import org.oscarehr.integration.myhealthaccess.dto.MessageCountDto;
import org.oscarehr.integration.myhealthaccess.dto.MessageDto;
import org.oscarehr.integration.myhealthaccess.dto.PatientSingleSearchResponseTo1;
import org.oscarehr.messaging.backend.myhealthaccess.model.MhaAttachment;
import org.oscarehr.messaging.model.MessageGroup;
import org.oscarehr.messaging.model.Messageable;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Do not use this service directly rather use the oscarehr.messaging.service.MessagingService interface.
@Service
public class ClinicMessagingService extends BaseService
{
	@Autowired
	ClinicService clinicService;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * Get a message from the clinic mailbox by id.
	 * @param integration - the integration (Mha clinic) you wish to fetch the message from
	 * @param loggedInInfo - the logged in info of the user performing the action
	 * @param messageId - the id of the message you want to fetch
	 * @return the message.
	 */
	public MessageDto getMessage(Integration integration, LoggedInInfo loggedInInfo, String messageId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/message/%s", messageId);

		return this.postProcessMessage(restClient.doGetWithToken(url, getLoginToken(integration, loggedInInfo), MessageDto.class), restClient);
	}

	/**
	 * update the message according to the provided message dto.
	 * @param integration - the integration (Mha clinic) in which the message resides that you are updating
	 * @param loggedInInfo - the logged in info of the user performing the action
	 * @param messageDto - message date to update. Only fields that have an effect are id, subject, message, metaData, read, group.
	 *                   other attributes are ignored / cannot be updated.
	 * @return - a fresh copy of the message pulled from the MHA server.
	 */
	public MessageDto updateMessage(Integration integration, LoggedInInfo loggedInInfo, MessageDto messageDto)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/message/%s", messageDto.getId());

		return this.postProcessMessage(restClient.doPutWithToken(url, getLoginToken(integration, loggedInInfo), messageDto, MessageDto.class), restClient);
	}

	/**
	 * send a message.
	 * @param integration - integration in which to send the message (which MHA clinic to send the message as)
	 * @param loggedInInfo - logged in user info
	 * @param messageDto - the message to send. If message contains a conversation_id it is appended to the conversation (reply) instead of sent stand alone.
	 * @return - a fresh copy of the message that was just sent, returned by the MHA server.
	 */
	public MessageDto sendMessage(Integration integration, LoggedInInfo loggedInInfo, MessageDto messageDto)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/message/");

		return this.postProcessMessage(restClient.doPostWithToken(url, getLoginToken(integration, loggedInInfo), messageDto, MessageDto.class), restClient);
	}

	/**
	 *
	 * @param integration - the integration (mha clinic) you which to fetch messages from.
	 * @param loggedInInfo - the logged in info for the user performing the action
	 * @param startDateTime - [optional] filter messages to only those that where sent after this time.
	 * @param endDateTime - [optional] filter messages to only those that where sent before this time.
	 * @param group - [optional] filter messages by group. Group ALL is equivalent to null.
	 * @param limit - [optional] limit results to this number (use for paging).
	 * @param offset - [optional] offset results by this number (use for paging).
	 * @param sender - [optional] filter messages to only those sent by this sender.
	 * @param receiver - [optional] filter messages to only those received by this recipient.
	 * @return - a list of messages.
	 */
	public List<MessageDto> getMessages(
			Integration integration,
			LoggedInInfo loggedInInfo,
			@Nullable ZonedDateTime startDateTime,
			@Nullable ZonedDateTime endDateTime,
			@Nullable MessageGroup group,
			@Nullable Integer limit,
			@Nullable Integer offset,
			@Nullable Messageable<?> sender,
			@Nullable Messageable<?> receiver)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		// fill out query params
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		Optional.ofNullable(startDateTime).ifPresent((startDate) -> queryParams.add("start_datetime", startDate.toString()));
		Optional.ofNullable(endDateTime).ifPresent((endDate) -> queryParams.add("end_datetime", endDate.toString()));
		Optional.ofNullable(group).ifPresent((messageGroup -> queryParams.add("group", messageGroup.getName())));
		Optional.ofNullable(limit).ifPresent((lim) -> queryParams.add("limit", lim.toString()));
		Optional.ofNullable(offset).ifPresent((off) -> queryParams.add("offset", off.toString()));
		if (sender != null)
		{
			queryParams.add("sender_id", sender.getId());
			queryParams.add("sender_type", sender.getType().getName());
		}
		if (receiver != null)
		{
			queryParams.add("recipient_id", receiver.getId());
			queryParams.add("recipient_type", receiver.getType().getName());
		}

		String url = restClient.formatEndpointFull("/clinic_user/self/clinic/messages", null, queryParams);

		return this.postProcessMessage(Arrays.asList(restClient.doGetWithToken(url, getLoginToken(integration, loggedInInfo), MessageDto[].class)), restClient);
	}

	/**
	 * count the number of messages in the provided integration / group combo
	 * @param integration - the integration (mha clinic) you which to count messages in.
	 * @param loggedInInfo - the logged in info for the user performing the action.
	 * @param group - the group you want to count messages in.
	 * @param onlyUnread - if true only unread messages are counted.
	 * @return - the count of messages
	 */
	public Integer countMessages(Integration integration,	LoggedInInfo loggedInInfo, MessageGroup group, Boolean onlyUnread)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("only_unread", onlyUnread.toString());
		queryParams.add("group", group.getName());

		String url = restClient.formatEndpointFull("/clinic_user/self/clinic/messages/count", null, queryParams);

		return restClient.doGetWithToken(url, getLoginToken(integration, loggedInInfo), MessageCountDto.class).getCount();
	}

	/**
	 * get a conversation from the clinic mailbox by id
	 * @param integration - integration to fetch conversation from
	 * @param loggedInInfo - logged in info
 	 * @param conversationId - the conversation id to fetch.
	 * @return - the conversation
	 */
	public ConversationDto getConversation(Integration integration, LoggedInInfo loggedInInfo, String conversationId)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);
		String url = restClient.formatEndpoint("/clinic_user/self/clinic/conversation/%s", conversationId);

		ConversationDto conversationDto = restClient.doGetWithToken(url, getLoginToken(integration, loggedInInfo), ConversationDto.class);
		conversationDto.setMessages(this.postProcessMessage(conversationDto.getMessages(), restClient));

		return conversationDto;
	}

	/**
	 * get the data for an attachment
	 * @param integration - integration to fetch attachment from
	 * @param loggedInInfo - logged in user info
	 * @param attachment - the attachment who's data is to be downloaded.
	 * @return - the attachment binary data.
	 */
	public byte[] downloadAttachmentData(Integration integration, LoggedInInfo loggedInInfo, MhaAttachment attachment)
	{
		RestClientBase restClient = RestClientFactory.getRestClient(integration);

		return restClient.doGetWithToken(
				attachment.getDocumentUrl().orElseThrow(() -> new IllegalArgumentException("Cannot download attachment without url")).toString(),
				getLoginToken(integration, loggedInInfo),
				byte[].class);
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * get a clinic_user login token
	 * @param integration - the MHA integration
	 * @param loggedInInfo - the provider's logged in info
	 * @return - the providers clinic_user login token
	 */
	protected String getLoginToken(Integration integration, LoggedInInfo loggedInInfo)
	{
		return clinicService.loginOrCreateClinicUser(
				loggedInInfo,
				Optional.ofNullable(integration.getSite()).map(Site::getName).orElse(null))
				.getToken();
	}

	/**
	 * apply post processing to the incoming MessageDto.
	 * @param messageDto - the message to process
	 * @param restClient - the rest client that fetched the message
	 * @return - the processed message
	 */
	protected MessageDto postProcessMessage(MessageDto messageDto, RestClientBase restClient)
	{
		// attachment url's are relative to the server they where fetched from. Make them an absolute URL.
		messageDto.getAttachments().forEach((attachment) ->
		{
			attachment.setUrl(restClient.getRootURI().toString() + attachment.getUrl());
		});

		return messageDto;
	}

	/**
	 * just like postProcessMessage but takes a list of messages
	 * @param messageDtos - messages to process
	 * @param restClient - the rest client used to fetch the messages
	 * @return - list of processed messages
	 */
	protected List<MessageDto> postProcessMessage(List<MessageDto> messageDtos, RestClientBase restClient)
	{
		return messageDtos.stream().map((message) -> this.postProcessMessage(message, restClient)).collect(Collectors.toList());
	}
}
