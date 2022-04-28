import {JunoSelectOption} from "../../common/junoSelectOption";

export default interface FaxAccountProvider
{
	isOauth(): boolean;
	showPasswordField(): boolean;
	showOutboundEmailField(): boolean;
	showOutboundReturnFaxNoField(): boolean;

	passwordFieldValidation(): any //todo validations typing
	outboundEmailFieldValidation(): any
	outboundReturnFaxNoFieldValidation(): any

	getCoverLetterOptions(): Promise<JunoSelectOption[]>
	getIntegrationName(): string
}