import {MhaSSOApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import Integration from "../../model/Integration";
import MhaIntegration from "../model/MhaIntegration";
import MhaAppointment from "../model/MhaAppointment";

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

	/**
	 * get an SSO link for joining and on demand audio call.
	 * @param integration - the MHA integration for which this link should be generated
	 * @param appointment - the MHA appointment that the audio call is for.
	 * @return promise that resolves to the link.
	 */
	public async getOnDemandAudioCallSSOLink(integration: MhaIntegration, appointment: MhaAppointment): Promise<string>
	{
		return (await this._mhaSSOApi.getTelehealthAudioCallSSOLink(integration.id, appointment.id)).data.body;
	}
}