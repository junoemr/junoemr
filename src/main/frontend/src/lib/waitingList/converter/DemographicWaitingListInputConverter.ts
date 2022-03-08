import AbstractConverter from "../../conversion/AbstractConverter";
import {DemographicWaitListUpdateInput} from "../../../../generated";
import DemographicWaitingList from "../model/DemographicWaitingList";

export default class DemographicWaitingListInputConverter extends AbstractConverter<DemographicWaitingList, DemographicWaitListUpdateInput>
{
	convert(from: DemographicWaitingList): DemographicWaitListUpdateInput
	{
		if(!from)
		{
			return null;
		}
		let transfer = {} as DemographicWaitListUpdateInput;
		transfer.waitListId = from.waitListId;
		transfer.note = from.note;
		transfer.position = from.position;
		transfer.dateAddedToWaitList = this.serializeLocalDateTime(from.dateAddedToWaitList);
		return transfer;
	}
}