import FaxAccountProvider from "./FaxAccountProvider";
import FaxAccount from "../model/FaxAccount";
import {JunoSelectOption} from "../../common/junoSelectOption";

export default class SRFaxAccountProvider implements FaxAccountProvider
{
	protected faxAccount: FaxAccount;

	constructor(faxAccount: FaxAccount)
	{
		this.faxAccount = faxAccount;
	}

	public isOauth(): boolean
	{
		return false;
	}

	public showOutboundEmailField(): boolean
	{
		return true;
	}

	public showOutboundReturnFaxNoField(): boolean
	{
		return true;
	}

	public showPasswordField(): boolean
	{
		return true;
	}

	public passwordFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => Boolean(this.faxAccount.id)),
			Juno.Validations.validationFieldRequired(this.faxAccount, "password"));
	}

	public outboundEmailFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => !this.faxAccount.enableOutbound),
			Juno.Validations.validationFieldRequired(this.faxAccount, "accountEmail"));
	}

	public outboundReturnFaxNoFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => !this.faxAccount.enableOutbound),
			Juno.Validations.validationFieldRequired(this.faxAccount, "faxNumber"));
	}

	public getCoverLetterOptions(): Promise<JunoSelectOption[]>
	{
		return new Promise((resolve, reject) =>
		{
			resolve([
				{
					label: "None",
					value: "None",
				},
				{
					label: "Basic",
					value: "Basic",
				},
				{
					label: "Standard",
					value: "Standard",
				},
				{
					label: "Company",
					value: "Company",
				},
				{
					label: "Personal",
					value: "Personal",
				},
			]);
		});
	}

	public getIntegrationName(): string
	{
		return "SRFax";
	}


}