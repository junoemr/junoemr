import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConsultationServiceTo1} from "../../../../../generated";
import ConsultService from "../model/ConsultService";

export default class ConsultServiceToModelConverter extends AbstractConverter<ConsultationServiceTo1, ConsultService>
{
	convert(from: ConsultationServiceTo1): ConsultService
	{
		if(!from)
		{
			return null;
		}

		let model = new ConsultService(from.serviceId);
		model.description = from.serviceDesc;
		model.active = (from.active !== "0");
		model.specialists = from.specialists;

		return model;
	}
}