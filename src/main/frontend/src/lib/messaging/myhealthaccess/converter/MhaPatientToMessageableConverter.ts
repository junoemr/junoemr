import AbstractConverter from "../../../conversion/AbstractConverter";
import Messageable from "../../model/Messageable";
import {MessageableType} from "../../model/MessageableType";
import MhaPatient from "../../../integration/myhealthaccess/model/MhaPatient";

export default class MhaPatientToMessageableConverter extends AbstractConverter<MhaPatient, Messageable>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: MhaPatient): Messageable
	{
		return new Messageable(from.id, MessageableType.PatientUser, `${from.firstName}, ${from.lastName}`);
	}

}