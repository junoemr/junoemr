import AbstractConverter from "../../../conversion/AbstractConverter";
import {MessageDto} from "../../../../../generated";
import MhaMessage from "../clinic/model/MhaMessage";
import moment from "moment";
import AttachmentDtoToMhaAttachmentConverter from "./AttachmentDtoToMhaAttachmentConverter";
import MessageableDtoToMhaMessageableConverter from "./MessageableDtoToMhaMessageableConverter";

export default class MessageDtoToMhaMessageConverter extends AbstractConverter<MessageDto, MhaMessage>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: MessageDto): MhaMessage
	{
		const message = new MhaMessage(
			from.id,
			from.conversationId,
			from.subject,
			from.message,
			from.group,
			from.isRead,
			(new MessageableDtoToMhaMessageableConverter()).convert(from.sender),
			(new MessageableDtoToMhaMessageableConverter()).convertList(from.recipients),
			JSON.parse(from.metaData),
			moment(from.createdAtDateTime),
			(new AttachmentDtoToMhaAttachmentConverter()).convertList(from.attachments),
		);

		message.attachments.forEach((attach) => attach.message = message);

		return message;
	}

}