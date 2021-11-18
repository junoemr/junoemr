import EFormInstance from "../model/EFormInstance";
import {EFormApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";

export default class EFormInstanceService
{
	protected _eFormApi: EFormApi;
	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._eFormApi = new EFormApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}


	/**
	 * print an eform to pdf
	 * @param eFormInstance - the eform instance to print
	 * @return promise that resolves to base64 encoded pdf data.
	 */
	public async printEForm(eFormInstance: EFormInstance): Promise<string>
	{
		return (await this._eFormApi.getEFormPDFBase64(eFormInstance.formId, eFormInstance.formInstanceId)).data.body;
	}
}