import AbstractConverter from "../../../conversion/AbstractConverter";
import {AppointmentTo1} from "../../../../../generated";
import MhaAppointment from "../model/MhaAppointment";
import {MhaAppointmentType} from "../model/MhaAppointmentType";
import moment from "moment";

export default class AppointmentTo1ToMhaAppointmentConverter extends AbstractConverter<AppointmentTo1, MhaAppointment>
{
	convert(from: AppointmentTo1): MhaAppointment
	{
		return new MhaAppointment(
			from.id,
			moment(from.startDateTime),
			moment(from.endDateTime),
			from.appointmentNo?.toString(),
			from.demographicNo?.toString(),
			from.providerNo?.toString(),
			from.virtual,
			from.appName,
			from.appointmentType as MhaAppointmentType,
			from.cancelled);
	}
}