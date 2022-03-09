import AbstractConverter from "../../conversion/AbstractConverter";
import {DemographicWaitListModel} from "../../../../generated";
import moment from "moment";
import DemographicWaitingList from "../model/DemographicWaitingList";

export default class DemographicWaitingListTransferConverter extends AbstractConverter<DemographicWaitListModel, DemographicWaitingList>
{
	convert(from: DemographicWaitListModel): DemographicWaitingList
	{
		if(!from)
		{
			return null;
		}
		let model = new DemographicWaitingList();
		model.id = from.id;
		model.note = from.note;
		model.position = from.position;
		model.dateAddedToWaitList = moment(from.dateAddedToWaitList);
		model.archived = from.archived;
		model.waitListId = from.waitListModel?.id;

		return model;
	}
}