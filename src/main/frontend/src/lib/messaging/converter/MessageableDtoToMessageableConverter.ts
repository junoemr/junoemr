import AbstractConverter from "../../conversion/AbstractConverter";
import {MessageableDto} from "../../../../generated";
import Messageable from "../model/Messageable";

export default class MessageableDtoToMessageableConverter extends AbstractConverter<MessageableDto, Messageable>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: MessageableDto): Messageable
	{
		return new Messageable(from.id, from.type, from.name);
	}

}