import {MhaAppointmentApi} from "../../../../../generated";
import {getAngular$http, getAngular$httpParamSerializer} from "../../../util/AngularUtil";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import MhaAppointment from "../model/MhaAppointment";
import MhaIntegration from "../model/MhaIntegration";
import MhaPatient from "../model/MhaPatient";
import AppointmentBookingTransferFactory from "../factory/AppointmentBookingTransferFactory";
import AppointmentTo1ToMhaAppointmentConverter from "../converter/AppointmentTo1ToMhaAppointmentConverter";

export default class MhaAppointmentService
{
	protected _mhaAppointmentApi: MhaAppointmentApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaAppointmentApi = new MhaAppointmentApi(getAngular$http(), getAngular$httpParamSerializer, API_BASE_PATH);
	}

	/**
	 * book a new on demand audio appointment.
	 * @return promise that resolves to the newly booked on demand audio appointment.
	 */
	public async bookOnDemandAudioAppointment(integration: MhaIntegration, patient: MhaPatient): Promise<MhaAppointment>
	{
		return (new AppointmentTo1ToMhaAppointmentConverter()).convert(
			(await this._mhaAppointmentApi.bookMhaAppointment(parseInt(integration.id), AppointmentBookingTransferFactory.buildOnDemandAudioAppointmentBookingTransfer(integration.siteName, patient.id))).data.body);
	}

}
