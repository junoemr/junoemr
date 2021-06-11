import AbstractConverter from "../../../conversion/AbstractConverter";
import {MessageableDto} from "../../../../../generated";
import MhaMessageable from "../clinic/model/MhaMessageable";

export default class MessageableDtoToMhaMessageableConverter extends AbstractConverter<MessageableDto, MhaMessageable>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: MessageableDto): MhaMessageable
	{
		return new MhaMessageable(from.id, from.type, from.name, from.identificationName);
	}

}