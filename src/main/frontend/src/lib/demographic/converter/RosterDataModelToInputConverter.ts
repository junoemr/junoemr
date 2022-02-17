import AbstractConverter from "../../conversion/AbstractConverter";
import {RosterData} from "../../../../generated";
import RosterStatusData from "../model/RosterStatusData";
import SimpleProviderToInputConverter from "../../provider/converter/SimpleProviderToInputConverter";

export default class RosterDataModelToInputConverter extends AbstractConverter<RosterStatusData, RosterData>
{
	convert(from: RosterStatusData): RosterData
	{
		if(!from)
		{
			return null;
		}

		let input = {} as RosterData;

		input.id = from.id;
		input.rostered = from.isRostered;
		input.statusCode = from.statusCode;
		input.statusDescription = from.statusDescription;
		input.rosterDateTime = this.serializeLocalDateTime(from.rosterDateTime);
		input.terminationDateTime = this.serializeLocalDateTime(from.terminationDateTime);
		input.terminationReason = from.terminationReason as any as RosterData.TerminationReasonEnum;
		input.rosterProvider = new SimpleProviderToInputConverter().convert(from.rosterProvider);

		return input;
	}
}