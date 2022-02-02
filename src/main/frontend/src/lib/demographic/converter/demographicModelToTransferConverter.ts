import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/demographic";
import {DemographicModel} from "../../../../generated";

export default class DemographicModelToTransferConverter extends AbstractConverter<Demographic, DemographicModel>
{
	convert(from: Demographic, args: any): DemographicModel
	{
		return from as DemographicModel;
	}
}