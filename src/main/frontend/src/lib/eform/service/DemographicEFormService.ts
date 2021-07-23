import {EFormApi, FormApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import {FormType} from "../model/FormType";
import FormTo1ToEFormInstanceConverter from "../converter/FormTo1ToEFormInstanceConverter";
import EFormInstance from "../model/EFormInstance";

export default class DemographicEFormService
{

	protected _eFormApi: EFormApi;
	protected _formApi: FormApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._eFormApi = new EFormApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);

		this._formApi = new FormApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * get all eforms (completed) for a given demographic.
	 * @param demographicNo - the demographic to get eforms for.
	 * @return a list of eforms completed eforms.
	 */
	public async getDemographicEForms(demographicNo: string): Promise<EFormInstance[]>
	{
		const forms = (await this._formApi.getAllCompletedForms(Number.parseInt(demographicNo))).data.list;
		const eForms = forms.filter((form) => form.type === FormType.EForm);
		return (new FormTo1ToEFormInstanceConverter()).convertList(eForms);
	}
}