import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxInboxTransferOutbound} from "../../../../generated";
import FaxInboxResult from "../model/FaxInboxResult";
import moment from "moment";

export default class FaxInboxResultToModelConverter extends AbstractConverter<FaxInboxTransferOutbound, FaxInboxResult>
{
	public convert(from: FaxInboxTransferOutbound): FaxInboxResult
	{
		if(!from)
		{
			return null;
		}

		let model = new FaxInboxResult();
		model.id = from.id;
		model.faxAccountId = from.faxAccountId;
		model.documentId = from.documentId;
		model.sentFrom = from.sentFrom;
		model.systemDateReceived = moment(from.systemDateReceived);
		model.externalReferenceId = from.externalReferenceId;

		return model;
	}

}