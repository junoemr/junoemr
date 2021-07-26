import AbstractConverter from "../../../conversion/AbstractConverter";
import {PatientAccessDto} from "../../../../../generated";
import MhaPatientAccess from "../model/MhaPatientAccess";
import {LinkStatus} from "../model/LinkStatus";
import moment from "moment";
import {MhaUserType} from "../model/MhaUserType";

export default class PatientAccessDtoToMhaPatientAccessConverter extends AbstractConverter<PatientAccessDto, MhaPatientAccess>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: PatientAccessDto): MhaPatientAccess
	{
		if (!from)
		{
			return null;
		}

		const patientAccess = new MhaPatientAccess();

		patientAccess.patientId = from.patientId;
		patientAccess.clinicId = from.clinicId;
		patientAccess.linkStatus = LinkStatus[from.linkStatus];

		patientAccess.canMessage = from.canMessage;
		patientAccess.canCancelAppointments = from.canCancelAppointments;

		patientAccess.confirmedAt = moment(from.confirmedAt);
		patientAccess.confirmedById = from.confirmedById;
		patientAccess.confirmedByType = from.confirmedByType;
		patientAccess.confirmingUserName = from.confirmingUserName;

		patientAccess.verifiedAt = moment(from.verifiedAt);
		patientAccess.verifiedById = from.verifiedById;
		patientAccess.verifiedByType = from.verifiedByType as any as MhaUserType;
		patientAccess.verifierUserName = from.verifierUserName;

		return patientAccess;
	}

}