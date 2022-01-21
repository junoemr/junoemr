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
	 * get mha profiles from all integrations matching this id.
	 * @param remoteId - the MHA patient id
	 * @return promise that resolves to a list of MHA profiles.
	 */
	public async getProfiles(remoteId: string): Promise<MhaPatient[]>
	{
		let profiles = await Promise.all((await this._mhaConfigService.getMhaIntegrations()).map(async (integration) =>
		{
			return this.getProfile(integration.id, remoteId);
		}));

		return profiles.filter((profile) => !!profile);
	}

	/**
	 * get an MHA profile by id from the specified integration.
	 * @param integrationId - integration to get the profile from
	 * @param remoteId - the remote id of the profile
	 * @return mha profile or null if profile cannot be found.
	 */
	public async getProfile(integrationId: string, remoteId: string): Promise<MhaPatient>
	{
		return (new PatientTo1ToMhaPatientConverter()).convert((await this._mhaPatientApi.getRemotePatient(integrationId, remoteId)).data.body);
	}

	/**
	 * get an MHA profile by account id code (a short lived code used for account verification)
	 * from the specified integration.
	 * @param integrationId - integration to get the profile from
	 * @param idCode - the account id to get the profile for
	 * @return mha profile or null if profile cannot be found.
	 */
	public async getProfileByAccountIdCode(integrationId: string, idCode: string): Promise<MhaPatient>
	{
		try
		{
			const profileTransfers = (await this._mhaPatientApi.searchPatients(integrationId, null, idCode)).data.body;

			if (profileTransfers && profileTransfers.length == 1)
			{
				return (new PatientTo1ToMhaPatientConverter()).convert(profileTransfers[0]);
			}
		}
		catch(error)
		{
			console.warn(error.data?.error?.message);
		}

		return null;
	}

	/**
	 * get the MHA profile for the demographic.
	 * @param integrationId - the integration to perform the lookup in
	 * @param demographicNo - the demographicNo who's MHA profile is to be fetched.
	 * @return promise that resolves to a MHAPatient profile or null if none found.
	 */
	public async profileForDemographic(integrationId: string, demographicNo: string): Promise<MhaPatient>
	{
		return (new PatientTo1ToMhaPatientConverter()).convert((await this._mhaDemographicApi.getMHAPatient(parseInt(integrationId), demographicNo)).data.body);
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