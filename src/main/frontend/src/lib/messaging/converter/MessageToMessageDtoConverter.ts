import AbstractConverter from "../../conversion/AbstractConverter";
import Message from "../model/Message";
import {MessageDto} from "../../../../generated";
import MessageableToMessageableDtoConverter from "./MessageableToMessageableDtoConverter";
import AttachmentToAttachmentDtoConverter from "./AttachmentToAttachmentDtoConverter";

export default class MessageToMessageDtoConverter extends AbstractConverter<Message, MessageDto>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: Message): MessageDto
	{
		return {
			id: from.id,
			conversationId: from.conversationId,
			subject: from.subject,
			message: from.message,
			createdAtDateTime: from.createdAtDateTime.toDate(),
			metaData: JSON.stringify(from.metaData),
			isRead: from.isRead,
			group: from.group,
			sender: from.sender ? (new MessageableToMessageableDtoConverter()).convert(from.sender) : null,
			recipients: from.recipients ? (new MessageableToMessageableDtoConverter()).convertList(from.recipients) : [],
			attachments: from.attachments ? (new AttachmentToAttachmentDtoConverter()).convertList(from.attachments): [],
		};
	}
}