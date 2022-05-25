import ConsultRequest from "../model/ConsultRequest";
import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConsultationRequestTo1} from "../../../../../generated";

export default class ConsultRequestToUpdateInputConverter extends AbstractConverter<ConsultRequest, ConsultationRequestTo1>
{
	convert(from: ConsultRequest): any
	{
		if(!from)
		{
			return null;
		}

		let input = {};

		return input;
	}
}