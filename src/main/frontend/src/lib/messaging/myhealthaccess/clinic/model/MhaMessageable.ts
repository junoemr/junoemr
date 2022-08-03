import Messageable from "../../../model/Messageable";
import {MessageableLocalType} from "../../../model/MessageableLocalType";
import {MessageableType} from "../../../model/MessageableType";
import MhaPatientService from "../../../../integration/myhealthaccess/service/MhaPatientService";
import MhaPatient from "../../../../integration/myhealthaccess/model/MhaPatient";
import {MessageableMappingConfidence} from "../../../model/MessageableMappingConfidence";
import {LinkStatus, linkStatusToVerificationLevel} from "../../../../integration/myhealthaccess/model/LinkStatus";

export default class MhaMessageable extends Messageable
{
	// ==========================================================================
	// Cache attributes
	// ==========================================================================

	// Checking with MHA is expensive.
	// Cache profiles here after first lookup from MHA.
	protected profiles: MhaPatient[] = null;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * clear any caches in this messageable. The next time the data is needed
	 * it will be pulled from the MHA server.
	 */
	public clearCache(): void
	{
		this.profiles = null;
	}

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
			if (!this.profiles)
			{
				const mhaPatientService = new MhaPatientService();
				this.profiles = (await mhaPatientService.getProfiles(this.id));
			}

			const profile: MhaPatient = this.profiles.find((profile) => profile.isConfirmed && profile.demographicNo != null);
			return !!profile;
		}
		return false;
	}

	/**
	 * get the confidence level of the local mapping. This indicates how certain the mapping to the local entity is.
	 * For example if you match to a demogrpahic by HIN you would select MEDIUM. However if the mapping is based on a direct
	 * id match you would return HIGH. Certain features will be restricted depending on confidence.
	 * @return a promise that resolves to the confidence level of this messageable's mapping to a local entity.
	 */
	public async localMappingConfidenceLevel(): Promise<MessageableMappingConfidence>
	{
		if (this.type === MessageableType.PatientUser)
		{
			if (!this.profiles)
			{
				const mhaPatientService = new MhaPatientService();
				this.profiles = (await mhaPatientService.getProfiles(this.id));
			}

			const linkStatus = this.profiles.reduce((linkStatus, profile) => {
				return linkStatusToVerificationLevel(linkStatus) < linkStatusToVerificationLevel(profile.linkStatus) ? profile.linkStatus : linkStatus
			}, LinkStatus.NO_LINK);

			switch (linkStatus)
			{
				case LinkStatus.VERIFIED:
					return MessageableMappingConfidence.HIGH;
				case LinkStatus.CONFIRMED:
					return MessageableMappingConfidence.MEDIUM;
				default:
					return MessageableMappingConfidence.NONE;
			}
		}
		return MessageableMappingConfidence.NONE;
	}

	/**
	 * a string explaining to the user why this messageable is at the current confidence level.
	 * An example might be, "Patient Chart Connection Active" for HIGH confidence.
	 * or "Patient chart unavailable please do X, Y, & Z to connect..."
	 * @return promise that resolves to an explanatory string.
	 */
	public async localMappingConfidenceExplanationString(): Promise<string>
	{
		switch(await this.localMappingConfidenceLevel())
		{
			case MessageableMappingConfidence.NONE:
			case MessageableMappingConfidence.LOW:
				return "MHA patient is unconfirmed. To access their eChart you must verify the connection";
			case MessageableMappingConfidence.MEDIUM:
				return "MHA patient is confirmed. To access their eChart you must verify the connection";
			case MessageableMappingConfidence.HIGH:
				return "MHA patient is verified";
		}
	}

	/**
	 * local entity id. i.e. (provider or demographic id).
	 * @return promise that resolves to local entity id or null if not available
	 */
	public async localId(): Promise<string>
	{
		if (this.type === MessageableType.PatientUser)
		{
			if (!this.profiles)
			{
				const mhaPatientService = new MhaPatientService();
				this.profiles = (await mhaPatientService.getProfiles(this.id));
			}

			const profile: MhaPatient = this.profiles.find((profile) => profile.isConfirmed);
			if (profile)
			{
				return profile.demographicNo;
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