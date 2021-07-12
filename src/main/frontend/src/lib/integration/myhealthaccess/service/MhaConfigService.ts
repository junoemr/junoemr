import {IntegrationTo1, MhaIntegrationApi, SystemPreferenceApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import IntegrationTo1ToMhaIntegrationConverter from "../converter/IntegrationTo1ToMhaIntegrationConverter";
import MhaIntegration from "../model/MhaIntegration";
import angular from "angular";

export default class MhaConfigService
{
	public static readonly MHA_ENABLE_PROPERTY = "myhealthaccess_telehealth_enabled";

	protected _mhaIntegrationApi: MhaIntegrationApi;
	protected _systemPreferencesApi: SystemPreferenceApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaIntegrationApi = new MhaIntegrationApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);

		this._systemPreferencesApi = new SystemPreferenceApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * @return promise that resolves to, true / false indicating if MHA is enabled.
	 */
	public async mhaEnabled(): Promise<boolean>
	{
		return (await this.getMhaIntegrations()).length > 0 &&
			(await this._systemPreferencesApi.getPropertyEnabled(MhaConfigService.MHA_ENABLE_PROPERTY)).data.body;
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