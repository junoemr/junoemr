import FaxAccountProvider from "./FaxAccountProvider";
import FaxAccount from "../model/FaxAccount";
import FaxAccountService from "../service/FaxAccountService";

export default class RingCentralAccountProvider implements FaxAccountProvider
{
	protected faxAccount: FaxAccount;
	protected faxAccountService = new FaxAccountService();

	constructor(faxAccount: FaxAccount)
	{
		this.faxAccount = faxAccount;
	}

	isOauth(): boolean
	{
		return true;
	}

	showOutboundEmailField(): boolean
	{
		return false;
	}

	showOutboundReturnFaxNoField(): boolean
	{
		return false;
	}

	showPasswordField(): boolean
	{
		return false;
	}

	outboundEmailFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	outboundReturnFaxNoFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	passwordFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	getCoverLetterOptions(): Promise<object[]>
	{
		return new Promise(async (resolve, reject) =>
		{
			if (!this.faxAccount.id)
			{
				reject("RingCentral account id is required to fetch cover letter options");
			}

			try
			{
				let response = await this.faxAccountService.getCoverLetterOptions(this.faxAccount.id)
				let options = response.map((value: string) =>
				{
					return {
						label: value,
						value: value,
					};
				});
				resolve(options);
			}
			catch (error)
			{
				reject(error);
			}
		});
	}

}