
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
 
package org.oscarehr.ws.rest.myhealthaccess.clinic.message;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.oscarehr.integration.dao.IntegrationDao;
import org.oscarehr.integration.myhealthaccess.exception.RecordNotFoundException;
import org.oscarehr.messaging.factory.MessagingServiceFactory;
import org.oscarehr.messaging.model.Attachment;
import org.oscarehr.messaging.model.Message;
import org.oscarehr.messaging.model.MessagingBackendType;
import org.oscarehr.ws.common.annotation.SkipContentLoggingOutbound;
import org.oscarehr.ws.rest.myhealthaccess.clinic.MessagingBaseWebService;
import org.oscarehr.ws.rest.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Base64;
import java.util.Optional;

@Path("myhealthaccess/integration/{integrationId}/clinic/message/{messageId}/attachment/")
@Component("mhaAttachmentWebService")
@Tag(name = "mhaClinicMessaging")
public class AttachmentWebService extends MessagingBaseWebService
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	@Autowired
	public AttachmentWebService(MessagingServiceFactory messagingServiceFactory, IntegrationDao integrationDao)
	{
		super(messagingServiceFactory.build(MessagingBackendType.MHA), integrationDao);
	}

	// ==========================================================================
	// Endpoints
	// ==========================================================================

	@GET
	@Path("/{attachmentId}/")
	@Produces(MediaType.APPLICATION_JSON)
	@SkipContentLoggingOutbound
	public RestResponse<String> getFileDataBase64(
			@PathParam("integrationId") String integrationId,
			@PathParam("messageId") String messageId,
			@PathParam("attachmentId") String attachmentId
	)
	{
		Message message = this.messagingService.getMessage(getLoggedInInfo(), this.messageableFromIntegrationId(integrationId), messageId);
		Optional<? extends Attachment> attachmentOptional = message.getAttachments().stream().filter((attachment) -> attachment.getId().equals(attachmentId)).findFirst();

		if (attachmentOptional.isPresent())
		{
			byte[] fileData = this.messagingService.getAttachmentData(
					getLoggedInInfo(),
					this.messageableFromIntegrationId(integrationId),
					attachmentOptional.get());

			return RestResponse.successResponse(Base64.getEncoder().encodeToString(fileData));
		}
		else
		{
			throw new RecordNotFoundException("Message [" + messageId + "] does not contain an attachment with id [" + attachmentId + "]");
		}
	}
}