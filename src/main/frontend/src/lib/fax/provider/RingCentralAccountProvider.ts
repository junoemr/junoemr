import FaxAccountProvider from "./FaxAccountProvider";
import FaxAccount from "../model/FaxAccount";

export default class RingCentralAccountProvider implements FaxAccountProvider
{
	protected faxAccount: FaxAccount;

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

}