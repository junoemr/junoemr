import FaxAccountProvider from "./FaxAccountProvider";
import FaxAccount from "../model/FaxAccount";
import FaxAccountService from "../service/FaxAccountService";
import {JunoSelectOption} from "../../common/junoSelectOption";

export default class RingCentralAccountProvider implements FaxAccountProvider
{
	protected faxAccount: FaxAccount;
	protected faxAccountService = new FaxAccountService();

	constructor(faxAccount: FaxAccount)
	{
		this.faxAccount = faxAccount;
	}

	public isOauth(): boolean
	{
		return true;
	}

	public showOutboundEmailField(): boolean
	{
		return false;
	}

	public showOutboundReturnFaxNoField(): boolean
	{
		return false;
	}

	public showPasswordField(): boolean
	{
		return false;
	}

	public outboundEmailFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	public outboundReturnFaxNoFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	public passwordFieldValidation(): any
	{
		return Juno.Validations.validationCustom(() => true)
	}

	public getCoverLetterOptions(): Promise<JunoSelectOption[]>
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

	public getIntegrationName(): string
	{
		return "RingCentral";
	}
}