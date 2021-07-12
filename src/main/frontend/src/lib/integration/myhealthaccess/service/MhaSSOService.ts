import {MhaSSOApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import Integration from "../../model/Integration";

export default class MhaSSOService
{
	protected _mhaSSOApi: MhaSSOApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaSSOApi = new MhaSSOApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * get the SSO URL for the clinic admin.
	 * @param integration - the integration to generate this link for
	 */
	public async getClinicAdminSSOLink(integration: Integration): Promise<string>
	{
		return (await this._mhaSSOApi.getClinicAdminSSOLink(integration.id)).data.body;
	}
}