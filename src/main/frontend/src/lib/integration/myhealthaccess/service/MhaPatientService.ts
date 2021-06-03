import {MhaDemographicApi, MhaPatientApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import MhaPatient from "../model/MhaPatient";
import MhaConfigService from "./MhaConfigService";
import PatientTo1ToMhaPatientConverter from "../converter/PatientTo1ToMhaPatientConverter";

export default class MhaPatientService
{
	protected _mhaConfigService: MhaConfigService;
	protected _mhaDemographicApi: MhaDemographicApi;
	protected _mhaPatientApi: MhaPatientApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaConfigService = new MhaConfigService();

		this._mhaDemographicApi = new MhaDemographicApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);

		this._mhaPatientApi = new MhaPatientApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * get an MHA profile by id. Searching all integrations.
	 * @param remoteId - the
	 */
	public async getProfile(remoteId: string): Promise<MhaPatient>
	{
		let profiles = await Promise.all((await this._mhaConfigService.getMhaIntegrations()).map(async (integration) =>
		{
			return this.getProfileFromIntegration(integration.id, remoteId);
		}));

		// even if multiple profiles are returned they will all be the same.
		return profiles[0];
	}

	/**
	 * get an MHA profile by id from the specified integration.
	 * @param integrationId - integration to get the profile from
	 * @param remoteId - the remote id of the profile
	 * @return mha profile or null if profile cannot be found.
	 */
	public async getProfileFromIntegration(integrationId: number, remoteId: string): Promise<MhaPatient>
	{
		return (new PatientTo1ToMhaPatientConverter()).convert((await this._mhaPatientApi.getRemotePatient(integrationId.toString(), remoteId)).data.body);
	}

	/**
	 * get the MHA profile for the demographic.
	 * @param integrationId - the integration to perform the lookup in
	 * @param demographicNo - the demographicNo who's MHA profile is to be fetched.
	 * @return promise that resolves to a MHAPatient profile or null if none found.
	 */
	public async profileForDemographic(integrationId: number, demographicNo: string): Promise<MhaPatient>
	{
		return (new PatientTo1ToMhaPatientConverter()).convert((await this._mhaDemographicApi.getMHAPatient(integrationId, demographicNo)).data.body);
	}

	/**
	 * get MHA profiles for a patient. This can result in none, one or multiple profiles.
	 * In most cases they will all match however they do not have to.
	 * @param demographicNo - the demographicNo who's MHA profiles are to be searched.
	 * @return promise that resolves to a list of MHA profiles.
	 */
	public async profilesForDemographic(demographicNo: string): Promise<MhaPatient[]>
	{
		let profiles = await Promise.all((await this._mhaConfigService.getMhaIntegrations()).map(async (integration) =>
		{
			return this.profileForDemographic(integration.id, demographicNo);
		}));

		return profiles.filter((patientDto) => patientDto != null);
	}

}