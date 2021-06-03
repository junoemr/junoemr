import {IntegrationTo1, MhaIntegrationApi} from "../../../../../generated";
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

	/**
	 * @return promise that resolves to, true / false indicating if MHA is enabled.
	 */
	public async MhaEnabled(): Promise<boolean>
	{
		return (await this.getMhaIntegrations()).length > 0;
	}

	/**
	 * get a list of all MHA integrations on this Juno server.
	 * @return promise that resolves to list of integrations.
	 */
	public async getMhaIntegrations(): Promise<IntegrationTo1[]>
	{
		return (await this._mhaIntegrationApi.searchIntegrations(null, true)).data.body;
	}
}