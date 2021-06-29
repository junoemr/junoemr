import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConversationDto} from "../../../../../generated";
import {MhaConversation} from "../clinic/model/MhaConversation";
import MessageDtoToMhaMessageConverter from "./MessageDtoToMhaMessageConverter";

export default class ConversationDtoToMhaConversationConverter extends AbstractConverter<ConversationDto, MhaConversation>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: ConversationDto): MhaConversation
	{
		return new MhaConversation(from.id, (new MessageDtoToMhaMessageConverter()).convertList(from.messages));
	}

}