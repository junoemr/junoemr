import {MhaIntegrationApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";

export default class MhaConfigService
{
	protected _mhaIntegrationApi: MhaIntegrationApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaIntegrationApi = new MhaIntegrationApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	public async MhaEnabled(): Promise<boolean>
	{
		return (await this._mhaIntegrationApi.searchIntegrations(null, true)).data.body.length > 0;
	}
}