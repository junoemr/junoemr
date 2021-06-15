import {IntegrationTo1, MhaIntegrationApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import IntegrationTo1ToMhaIntegrationConverter from "../converter/IntegrationTo1ToMhaIntegrationConverter";
import MhaIntegration from "../model/MhaIntegration";

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
	public async getMhaIntegrations(): Promise<MhaIntegration[]>
	{
		return (new IntegrationTo1ToMhaIntegrationConverter()).convertList((await this._mhaIntegrationApi.searchIntegrations(null, true)).data.body);
	}

	/**
	 * delete the specified integration
	 * @param integration - the integration to delete
	 */
	public async deleteIntegration(integration: MhaIntegration): Promise<void>
	{
		await this._mhaIntegrationApi.deleteMhaIntegration(integration.id);
	}

	/**
	 * test the connection of the specified integration
	 * @param integration - the integration to test
	 * @return promise that resolves to true / false indicating if the integration connection is valid.
	 */
	public async testIntegrationConnection(integration: MhaIntegration): Promise<boolean>
	{
		return (await this._mhaIntegrationApi.testConnection(integration.id)).data.body;
	}
}