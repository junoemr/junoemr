import {MhaPatientApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import PatientAccessDtoToMhaPatientAccessConverter from "../converter/PatientAccessDtoToMhaPatientAccessConverter";
import MhaPatientAccess from "../model/MhaPatientAccess";
import MhaConfigService from "./MhaConfigService";

export default class MhaPatientAccessService
{
	protected _mhaPatientApi: MhaPatientApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public constructor()
	{
		this._mhaPatientApi = new MhaPatientApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * get all patient access records for the given remote id
	 * @param remoteId - the remote id to get the access records for
	 * @return promise that resolves to an array of access records
	 */
	public async getPatientAccesses(remoteId: string): Promise<MhaPatientAccess[]>
	{
		const mhaConfigService = new MhaConfigService();

		return await Promise.all((await mhaConfigService.getMhaIntegrations()).map( async (integration) =>
		{
			return await this.getPatientAccess(integration.id, remoteId);
		}));
	}

	/**
	 * get a patients access record
	 * @param integrationId - the integration to get the record from
	 * @param remoteId - the remote patient id whose record is to be fetched
	 * @return promise that resolves to an access record
	 */
	public async getPatientAccess(integrationId: string, remoteId: string): Promise<MhaPatientAccess>
	{
		return (new PatientAccessDtoToMhaPatientAccessConverter()).convert((await this._mhaPatientApi.getPatientAccess(integrationId, remoteId)).data.body);
	}
}