import AbstractConverter from "../../conversion/AbstractConverter";
import {ConversationDto} from "../../../../generated";
import Conversation from "../model/Conversation";
import MessageDtoToMessageConverter from "./MessageDtoToMessageConverter";

export default class ConversationDtoToConversationConverter extends AbstractConverter<ConversationDto, Conversation>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: ConversationDto): Conversation
	{
		return new Conversation(from.id, (new MessageDtoToMessageConverter()).convertList(from.messages));
	}
}