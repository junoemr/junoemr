import {ProviderPreferenceApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import {ProviderPreferences} from "./ProviderPreferenceServiceConstants";

export default class ProviderPreferenceService
{
	protected providerPreferenceApi;

	constructor()
	{
		const $http = angular.injector(["ng"]).get("$http");
		const $httpParamSerializer = angular.injector(["ng"]).get("$httpParamSerializer");

		this.providerPreferenceApi = new ProviderPreferenceApi($http, $httpParamSerializer, API_BASE_PATH);
	}

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public async getPreference(key: ProviderPreferences): Promise<string>
	{
		return (await this.providerPreferenceApi.getProviderSetting(key)).data.body;
	}
}
// service is meant to be a singleton
export const providerPreferenceService = new ProviderPreferenceService();