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

package org.oscarehr.ws.rest.conversion.messaging;

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.messaging.model.Message;
import org.oscarehr.ws.rest.transfer.messaging.MessageDto;
import org.springframework.beans.BeanUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MessageToMessageDtoConverter extends AbstractModelConverter<Message, MessageDto>
{
	// ==========================================================================
	// AbstractModelConverter Overrides
	// ==========================================================================

	@Override
	public MessageDto convert(Message input)
	{
		MessageDto messageDto = new MessageDto();
		BeanUtils.copyProperties(input, messageDto, "sender", "recipients", "attachments", "isRead");

		messageDto.setIsRead(input.isRead());

		// convert sender and recipients
		MessageableToMessageableDtoConverter messageableConverter = new MessageableToMessageableDtoConverter();
		messageDto.setSender(messageableConverter.convert(input.getSender()));
		messageDto.setRecipients(messageableConverter.convert(input.getRecipients()));

		// convert attachments
		messageDto.setAttachments((new AttachmentToAttachmentDtoConverter()).convert(input.getAttachments()));

		return messageDto;
	}
}
