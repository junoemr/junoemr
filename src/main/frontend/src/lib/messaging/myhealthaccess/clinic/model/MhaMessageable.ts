import Messageable from "../../../model/Messageable";
import {MessageableLocalType} from "../../../model/MessageableLocalType";
import {MessageableType} from "../../../model/MessageableType";
import MhaPatientService from "../../../../integration/myhealthaccess/service/MhaPatientService";
import MhaPatient from "../../../../integration/myhealthaccess/model/MhaPatient";

export default class MhaMessageable extends Messageable
{
	// ==========================================================================
	// Public Messageable Overrides
	// ==========================================================================

	/**
	 * if this messageable can be mapped to a local entity (provider or demographic).
	 * @return promise - that resolves to true / false
	 */
	public async hasLocalMapping(): Promise<boolean>
	{
		if (this.type === MessageableType.PatientUser)
		{
			const mhaPatientService = new MhaPatientService();
			const profile: MhaPatient = await mhaPatientService.getProfile(this.id);

			return !!profile && profile.isVerified;
		}
		return false;
	}

	/**
	 * local entity id. i.e. (provider or demographic id).
	 * @return promise that resolves to local entity id or null if not available
	 */
	public async localId(): Promise<string>
	{
		if (this.type === MessageableType.PatientUser)
		{
			const mhaPatientService = new MhaPatientService();
			const profile: MhaPatient = await mhaPatientService.getProfile(this.id);

			if (profile.isVerified)
			{
				return profile.id;
			}
		}

		return null;
	}

	/**
	 * local entity type.
	 * @return promise that resolves to the local entity type. Will be NONE if no local mapping
	 */
	public async localType(): Promise<MessageableLocalType>
	{
		switch (this.type)
		{
			case MessageableType.PatientUser:
				return MessageableLocalType.DEMOGRAPHIC;
			case MessageableType.ClinicProfile:
				return MessageableLocalType.PROVIDER;
			default:
				return MessageableLocalType.NONE;
		}
	}
}