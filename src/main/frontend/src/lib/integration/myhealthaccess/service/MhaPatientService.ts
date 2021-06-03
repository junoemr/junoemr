import {MhaDemographicApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import MhaPatient from "../model/MhaPatient";
import MhaConfigService from "./MhaConfigService";
import PatientTo1ToMhaPatientConverter from "../converter/PatientTo1ToMhaPatientConverter";

export default class MhaPatientService
{
	protected _mhaConfigService: MhaConfigService;
	protected _mhaDemographicApi: MhaDemographicApi;

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
		let patientTransfers = await Promise.all((await this._mhaConfigService.getMhaIntegrations()).map(async (integration) =>
		{
			return this.profileForDemographic(integration.id, demographicNo);
		}));

		return patientTransfers.filter((patientDto) => patientDto != null);
	}

}