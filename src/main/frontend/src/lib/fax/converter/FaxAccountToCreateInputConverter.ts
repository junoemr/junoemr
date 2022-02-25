import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxAccountCreateInput} from "../../../../generated";
import FaxAccount from "../model/FaxAccount";

export default class FaxAccountToCreateInputConverter extends AbstractConverter<FaxAccount, FaxAccountCreateInput>
{
	public convert(model: FaxAccount): FaxAccountCreateInput
	{
		if(!model)
		{
			return null;
		}

		let input = {} as FaxAccountCreateInput;
		input.accountType = model.accountType as any as FaxAccountCreateInput.AccountTypeEnum;
		input.accountLogin = model.accountLogin;
		input.password = model.password;
		input.accountEmail = model.accountEmail;
		input.enabled = model.enabled;
		input.enableInbound = model.enableInbound;
		input.enableOutbound = model.enableOutbound;
		input.displayName = model.displayName;
		input.faxNumber = model.faxNumber;
		input.coverLetterOption = model.coverLetterOption;

		return input;
	}

}