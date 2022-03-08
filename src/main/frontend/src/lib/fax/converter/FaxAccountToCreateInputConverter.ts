import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxAccountCreateInput, PhoneNumberModel} from "../../../../generated";
import FaxAccount from "../model/FaxAccount";
import PhoneNumber from "../../common/model/PhoneNumber";
import PhoneNumberToInputConverter from "../../common/converter/PhoneNumberToInputConverter";

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
		if(model.faxNumber)
		{
			let modelNumber = new PhoneNumber(model.faxNumber, null, PhoneNumberModel.PhoneTypeEnum.Fax);
			input.faxNumber = new PhoneNumberToInputConverter().convert(modelNumber);
		}
		input.coverLetterOption = model.coverLetterOption;

		return input;
	}

}