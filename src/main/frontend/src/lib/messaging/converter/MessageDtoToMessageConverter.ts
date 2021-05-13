import AbstractConverter from "../../conversion/AbstractConverter";
import Message from "../model/Message";
import {MessageDto} from "../../../../generated";
import MessageableDtoToMessageableConverter from "./MessageableDtoToMessageableConverter";
import moment from "moment";

export default class MessageDtoToMessageConverter extends AbstractConverter<MessageDto, Message>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: MessageDto): Message
	{
		return new Message(
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
		);
	}

}