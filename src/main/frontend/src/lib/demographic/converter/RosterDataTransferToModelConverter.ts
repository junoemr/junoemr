import AbstractConverter from "../../conversion/AbstractConverter";
import {RosterData} from "../../../../generated";
import RosterStatusData from "../model/RosterStatusData";
import moment from "moment";
import {RosterTerminationReasonType} from "../model/RosterTerminationReasonType";
import ProviderModelToSimpleProviderConverter from "../../provider/converter/ProviderModelToSimpleProviderConverter";

export default class RosterDataTransferToModelConverter extends AbstractConverter<RosterData, RosterStatusData>
{
	convert(from: RosterData): RosterStatusData
	{
		if(!from)
		{
			return null;
		}

		let model = new RosterStatusData();

		model.id = from.id;
		model.isRostered = from.rostered;
		model.statusCode = from.statusCode;
		model.statusDescription = from.statusDescription;
		model.rosterDateTime = moment(from.rosterDateTime);
		model.terminationDateTime = moment(from.terminationDateTime);
		model.terminationReason = from.terminationReason as any as RosterTerminationReasonType;
		model.rosterProvider = new ProviderModelToSimpleProviderConverter().convert(from.rosterProvider);

		return model;
	}
}