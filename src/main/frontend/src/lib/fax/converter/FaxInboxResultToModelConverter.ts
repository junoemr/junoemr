import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxInboxTransferOutbound} from "../../../../generated";
import FaxInboxResult from "../model/FaxInboxResult";
import moment from "moment";
import PhoneNumberToModelConverter from "../../common/converter/PhoneNumberToModelConverter";
import FaxAccountToModelConverter from "./FaxAccountToModelConverter";

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
		model.faxAccount = new FaxAccountToModelConverter().convert(from.faxAccount);
		model.documentId = from.documentId;
		model.sentFrom = new PhoneNumberToModelConverter().convert(from.sentFrom);
		model.systemDateReceived = moment(from.systemDateReceived);
		model.externalReferenceId = from.externalReferenceId;

		return model;
	}

}