import FaxAccountProvider from "./FaxAccountProvider";
import FaxAccount from "../model/FaxAccount";

export default class SRFaxAccountProvider implements FaxAccountProvider
{
	protected faxAccount: FaxAccount;

	constructor(faxAccount: FaxAccount)
	{
		this.faxAccount = faxAccount;
	}

	isOauth(): boolean
	{
		return false;
	}

	showOutboundEmailField(): boolean
	{
		return true;
	}

	showOutboundReturnFaxNoField(): boolean
	{
		return true;
	}

	showPasswordField(): boolean
	{
		return true;
	}

	passwordFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => Boolean(this.faxAccount.id)),
			Juno.Validations.validationFieldRequired(this.faxAccount, "password"));
	}

	outboundEmailFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => !this.faxAccount.enableOutbound),
			Juno.Validations.validationFieldRequired(this.faxAccount, "accountEmail"));
	}

	outboundReturnFaxNoFieldValidation(): any
	{
		return Juno.Validations.validationFieldOr(
			Juno.Validations.validationCustom(() => !this.faxAccount.enableOutbound),
			Juno.Validations.validationFieldRequired(this.faxAccount, "faxNumber"));
	}

	getCoverLetterOptions(): Promise<object[]>
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


}