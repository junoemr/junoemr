import AbstractConverter from "../../conversion/AbstractConverter";
import {IntegrationTo1} from "../../../../generated";
import MessageSource from "../model/MessageSource";

export default class IntegrationTo1ToMessageSourceConverter extends AbstractConverter<IntegrationTo1, MessageSource>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: IntegrationTo1): MessageSource
	{
		return new MessageSource(from.id.toString(), from.siteName);
	}

}