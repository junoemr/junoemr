import AbstractConverter from "../../conversion/AbstractConverter";
import {WaitListModel} from "../../../../generated";
import WaitingList from "../model/WaitingList";
import moment from "moment";
import ProviderModelToSimpleProviderConverter from "../../provider/converter/ProviderModelToSimpleProviderConverter";

export default class WaitingListTransferConverter extends AbstractConverter<WaitListModel, WaitingList>
{
	convert(from: WaitListModel): WaitingList
	{
		if(!from)
		{
			return null;
		}
		let model = new WaitingList();
		model.id = from.id;
		model.name = from.name;
		model.groupNumber = from.groupNumber;
		model.provider = new ProviderModelToSimpleProviderConverter().convert(from.provider);
		model.createdDateTime = moment(from.createdDateTime);
		model.archived = from.archived;

		return model;
	}
}