import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxAccountTransferOutbound} from "../../../../generated";
import FaxAccount from "../model/FaxAccount";
import {FaxAccountType} from "../model/FaxAccountType";

export default class FaxAccountToModelConverter extends AbstractConverter<FaxAccountTransferOutbound, FaxAccount>
{
	public convert(from: FaxAccountTransferOutbound): FaxAccount
	{
		if(!from)
		{
			return null;
		}

		let model = new FaxAccount();
		model.id = from.id;
		model.accountType = from.accountType as any as FaxAccountType;
		model.accountLogin = from.accountLogin;
		model.accountEmail = from.accountEmail;
		model.enabled = from.enabled;
		model.enableInbound = from.enableInbound;
		model.enableOutbound = from.enableOutbound;
		model.displayName = from.displayName;
		model.faxNumber = from.faxNumber;
		model.coverLetterOption = from.coverLetterOption;

		return model;
	}

}