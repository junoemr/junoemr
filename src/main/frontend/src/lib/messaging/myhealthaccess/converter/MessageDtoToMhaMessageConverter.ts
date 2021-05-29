import AbstractConverter from "../../../conversion/AbstractConverter";
import {MessageDto} from "../../../../../generated";
import MhaMessage from "../clinic/model/MhaMessage";
import MessageableDtoToMessageableConverter from "../../converter/MessageableDtoToMessageableConverter";
import moment from "moment";
import AttachmentDtoToMhaAttachmentConverter from "./AttachmentDtoToMhaAttachmentConverter";

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
			(new MessageableDtoToMessageableConverter()).convert(from.sender),
			(new MessageableDtoToMessageableConverter()).convertList(from.recipients),
			JSON.parse(from.metaData),
			moment(from.createdAtDateTime),
			(new AttachmentDtoToMhaAttachmentConverter()).convertList(from.attachments),
		);

		message.attachments.forEach((attach) => attach.message = message);

		return message;
	}

}