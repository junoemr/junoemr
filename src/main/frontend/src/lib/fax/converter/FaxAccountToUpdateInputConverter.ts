import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxAccountUpdateInput} from "../../../../generated";
import FaxAccount from "../model/FaxAccount";

export default class FaxAccountToUpdateInputConverter extends AbstractConverter<FaxAccount, FaxAccountUpdateInput>
{
	public convert(model: FaxAccount): FaxAccountUpdateInput
	{
		if(!model)
		{
			return null;
		}

		let input = {} as FaxAccountUpdateInput;
		input.id = model.id;
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