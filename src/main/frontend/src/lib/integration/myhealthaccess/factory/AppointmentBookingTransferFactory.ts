import {AppointmentBookingTransfer} from "../../../../../generated";
import {MhaAppointmentType} from "../model/MhaAppointmentType";
import moment from "moment";

export default class AppointmentBookingTransferFactory
{
	public static readonly OD_AUDIO_BOOKING_DEFAULT_DURATION = 5; // min

	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static buildOnDemandAudioAppointmentBookingTransfer(site: string, remotePatientId: string): AppointmentBookingTransfer
	{
    // @ts-ignore
		return {
			site,
			type: MhaAppointmentType.OdAudioCall as any,
			notifyPatient: false,
			remotePatientId,
      // @ts-ignore
			startDateTime: new Date(),
      // @ts-ignore
			endDateTime: moment().add(AppointmentBookingTransferFactory.OD_AUDIO_BOOKING_DEFAULT_DURATION, "m").toDate(),
			virtual: true,
		}
	}
}