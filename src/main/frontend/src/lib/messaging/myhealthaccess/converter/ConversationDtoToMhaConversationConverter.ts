import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConversationDto} from "../../../../../generated";
import {MhaConversation} from "../clinic/model/MhaConversation";
import Conversation from "../../model/Conversation";
import MessageDtoToMessageConverter from "../../converter/MessageDtoToMessageConverter";

export default class ConversationDtoToMhaConversationConverter extends AbstractConverter<ConversationDto, MhaConversation>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: ConversationDto): MhaConversation
	{
		return new MhaConversation(from.id, (new MessageDtoToMessageConverter()).convertList(from.messages));
	}

}