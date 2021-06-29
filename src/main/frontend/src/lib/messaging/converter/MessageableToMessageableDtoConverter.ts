import AbstractConverter from "../../conversion/AbstractConverter";
import Messageable from "../model/Messageable";
import {MessageableDto} from "../../../../generated";

export default class MessageableToMessageableDtoConverter extends AbstractConverter<Messageable, MessageableDto>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: Messageable): MessageableDto
	{
		return {
			id: from.id,
			type: from.type,
			name: from.name,
			identificationName: from.identificationName,
		};
	}

}