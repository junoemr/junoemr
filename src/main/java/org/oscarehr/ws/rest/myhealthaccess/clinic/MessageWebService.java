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
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.messaging.factory.MessagingServiceFactory;
import org.oscarehr.messaging.model.Message;
import org.oscarehr.messaging.model.MessagingBackendType;
import org.oscarehr.ws.rest.conversion.messaging.MessageToMessageDtoConverter;
import org.oscarehr.ws.rest.conversion.messaging.myhealthaccess.MessageDtoToMhaMessageConverter;
import org.oscarehr.ws.rest.response.GenericRestResponse;
import org.oscarehr.ws.rest.response.RestResponse;
import org.oscarehr.ws.rest.transfer.messaging.MessageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("myhealthaccess/integration/{integrationId}/clinic/message/")
@Component("mhaClinicMessageWebService")
@Tag(name = "mhaClinicMessaging")
public class MessageWebService extends MessagingBaseWebService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public MessageWebService(MessagingServiceFactory messagingServiceFactory, IntegrationDao integrationDao)
	{
		super(messagingServiceFactory.build(MessagingBackendType.MHA), integrationDao);
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<MessageDto> sendMessage(
			@PathParam("integrationId") String integrationId,
			MessageDto messageDto)
	{
		Message message = this.messagingService.sendMessage(
				getLoggedInInfo(),
				messageableFromIntegrationId(integrationId),
				(new MessageDtoToMhaMessageConverter()).convert(messageDto));

		return RestResponse.successResponse((new MessageToMessageDtoConverter()).convert(message));
	}

	@GET
	@Path("/{messageId}/")
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<MessageDto> getMessage(
			@PathParam("integrationId") String integrationId,
			@PathParam("messageId") String messageId
	)
	{
		try
		{
			Message message = this.messagingService.getMessage(getLoggedInInfo(), messageableFromIntegrationId(integrationId), messageId);
			return RestResponse.successResponse((new MessageToMessageDtoConverter()).convert(message));
		}
		catch (RecordNotFoundException e)
		{
			return null;
		}
	}


	@PUT
	@Path("/{messageId}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public RestResponse<MessageDto> updateMessage(
			@PathParam("integrationId") String integrationId,
			@PathParam("messageId") String messageId,
			MessageDto messageDto
	)
	{
		messageDto.setId(messageId);
		Message message = this.messagingService.updateMessage(
				getLoggedInInfo(),
				messageableFromIntegrationId(integrationId),
				(new MessageDtoToMhaMessageConverter()).convert(messageDto));

		return RestResponse.successResponse((new MessageToMessageDtoConverter()).convert(message));
	}

}
