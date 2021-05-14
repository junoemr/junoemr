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

package org.oscarehr.ws.rest.myhealthaccess.clinic;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.messaging.factory.MessagingServiceFactory;
import org.oscarehr.messaging.model.Message;
import org.oscarehr.messaging.model.MessageGroup;
import org.oscarehr.messaging.model.MessageableType;
import org.oscarehr.messaging.model.MessagingBackendType;
import org.oscarehr.ws.rest.conversion.messaging.MessageToMessageDtoConverter;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.messaging.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.ZonedDateTime;
import java.util.List;

@Path("myhealthaccess/integration/{integrationId}/clinic/messages")
@Component("mhaClinicMessagesWebService")
@Tag(name = "mhaClinicMessaging")
public class MessagesWebService extends MessagingBaseWebService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public MessagesWebService(MessagingServiceFactory messagingServiceFactory, IntegrationDao integrationDao)
	{
		super(messagingServiceFactory.build(MessagingBackendType.MHA), integrationDao);
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	/**
	 * search clinic messages
	 * @param integrationId - integration id path param
	 * @param startDateTime - [optional] filter conversations to only those that where sent after this time.
	 * @param endDateTime - [optional] filter conversations to only those that where sent before this time.
	 * @param group - [optional] filter conversations by group. Group ALL is equivalent to null.
	 * @param limit - [optional] limit results to this number (use for paging).
	 * @param offset - [optional] offset results by this number (use for paging).
	 * @param senderId - [optional] filter conversations to only those sent by this sender.
	 * @param senderType - you must provide this if you provide senderId
	 * @param recipientId - [optional] filter conversations to only those received by this recipient.
	 * @param recipientType - you must provide this if you provide recipientType
	 * @return - message search results
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<List<MessageDto>> getMessages(
			@PathParam("integrationId") String integrationId,

			@QueryParam("startDateTime") ZonedDateTime startDateTime,
			@QueryParam("endDateTime") ZonedDateTime endDateTime,
			@QueryParam("group") String group,
			@QueryParam("limit") Integer limit,
			@QueryParam("offset") Integer offset,
			@QueryParam("senderId") String senderId,
			@QueryParam("senderType") String senderType,
			@QueryParam("recipientId") String recipientId,
			@QueryParam("recipientType") String recipientType)
	{
		List<? extends Message> messages = this.messagingService.getMessages(
				getLoggedInInfo(),
				this.messageableFromIntegrationId(integrationId),
				startDateTime,
				endDateTime,
				group == null ? null : MessageGroup.valueOf(group),
				limit,
				offset,
				senderId == null ? null : this.messageableFromIdType(senderId, MessageableType.valueOf(senderType)),
				recipientId == null ? null : this.messageableFromIdType(recipientId, MessageableType.valueOf(recipientType)));

		return RestResponse.successResponse((new MessageToMessageDtoConverter()).convert(messages));
	}

}
