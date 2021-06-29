import AbstractConverter from "../../conversion/AbstractConverter";
import Message from "../model/Message";
import {MessageDto} from "../../../../generated";
import MessageableToMessageableDtoConverter from "./MessageableToMessageableDtoConverter";
import AttachmentToAttachmentDtoConverter from "./AttachmentToAttachmentDtoConverter";

export default class MessageToMessageDtoConverter extends AbstractConverter<Message, Promise<MessageDto>>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public async convert(from: Message, includeAttachmentData = false): Promise<MessageDto>
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
			attachments: from.attachments ? await Promise.all((new AttachmentToAttachmentDtoConverter()).convertList(from.attachments, includeAttachmentData)) : [],
		};
	}
}