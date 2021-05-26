import AbstractConverter from "../../../conversion/AbstractConverter";
import {PatientTo1} from "../../../../../generated";
import MhaPatient from "../model/MhaPatient";
import {map} from "angular-ui-router";
import {Province} from "../../../constants/Province";
import {LinkStatus} from "../model/LinkStatus";

export default class PatientTo1ToMhaPatientConverter extends AbstractConverter<PatientTo1, MhaPatient>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: PatientTo1): MhaPatient
	{
		const patientTo1 = from as any;
		const mhaPatient = new MhaPatient();

		mhaPatient.id = patientTo1.id;
		mhaPatient.firstName = patientTo1.first_name;
		mhaPatient.middleName = patientTo1.middle_name;
		mhaPatient.lastName = patientTo1.last_name;

		mhaPatient.healthCareProvinceCode = Province[patientTo1.health_care_province_code];
		mhaPatient.healthNumber = patientTo1.health_number;
		mhaPatient.healthNumberVersion = patientTo1.health_number_version;

		mhaPatient.email = patientTo1.email;
		mhaPatient.cellPhone = patientTo1.cell_phone;
		mhaPatient.postalCode = patientTo1.postal_code;
		mhaPatient.city = patientTo1.city;
		mhaPatient.address = patientTo1.address_1;
		mhaPatient.province = Province[patientTo1.address_province_code];

		mhaPatient.linkStatus = LinkStatus[patientTo1.link_status];
		mhaPatient.canMessage = patientTo1.can_message_clinic;

		return mhaPatient;
	}

}