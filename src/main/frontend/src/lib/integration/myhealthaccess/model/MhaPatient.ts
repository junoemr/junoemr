import {LinkStatus} from "./LinkStatus";
import {Province} from "../../../constants/Province";
import moment, {Moment} from "moment";
import {Sex} from "../../../demographic/model/Sex";
import MhaPatientAccess from "./MhaPatientAccess";
import MhaPatientAccessService from "../service/MhaPatientAccessService";
import MhaIntegration from "./MhaIntegration";

export default class MhaPatient
{
	protected _id: string;
	protected _firstName: string;
	protected _middleName: string;
	protected _lastName: string;
	protected _birthDate: Moment;
	protected _sex: Sex;

	protected _healthCareProvinceCode: Province;
	protected _healthNumber: string;
	protected _healthNumberVersion: string;

	protected _email: string;
	protected _cellPhone: string;
	protected _postalCode: string;
	protected _city: string;
	protected _address: string;
	protected _province: Province;
	protected _hasVoipToken: boolean;

	protected _linkStatus: LinkStatus;
	protected _canMessage: boolean;
	protected _demographicNo: string;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * get all patient access records for this patient across all MHA integrations
	 * @return promise that resolves to a list of access records
	 */
	public async getPatientAccessRecords(): Promise<MhaPatientAccess[]>
	{
		const accessService = new MhaPatientAccessService();
		return await accessService.getPatientAccesses(this.id);
	}

	/**
	 * get a specific access record for this patient
	 * @param integration - the integration to get the record from
	 * @return promise that resolves to an access record or null
	 */
	public async getPatientAccessRecord(integration: MhaIntegration): Promise<MhaPatientAccess>
	{
		const accessService = new MhaPatientAccessService();
		return await accessService.getPatientAccess(integration.id, this.id);
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set id(value: string)
	{
		this._id = value;
	}

	set firstName(value: string)
	{
		this._firstName = value;
	}

	set middleName(value: string)
	{
		this._middleName = value;
	}

	set lastName(value: string)
	{
		this._lastName = value;
	}

	set birthDate(value: Moment)
	{
		this._birthDate = value;
	}

	set sex(value: Sex)
	{
		this._sex = value;
	}

	set healthCareProvinceCode(value: Province)
	{
		this._healthCareProvinceCode = value;
	}

	set healthNumber(value: string)
	{
		this._healthNumber = value;
	}

	set healthNumberVersion(value: string)
	{
		this._healthNumberVersion = value;
	}

	set email(value: string)
	{
		this._email = value;
	}

	set cellPhone(value: string)
	{
		this._cellPhone = value;
	}

	set postalCode(value: string)
	{
		this._postalCode = value;
	}

	set city(value: string)
	{
		this._city = value;
	}

	set address(value: string)
	{
		this._address = value;
	}

	set province(value: Province)
	{
		this._province = value;
	}

	set hasVoipToken(value: boolean)
	{
		this._hasVoipToken = value;
	}

	set linkStatus(value: LinkStatus)
	{
		this._linkStatus = value;
	}

	set canMessage(value: boolean)
	{
		this._canMessage = value;
	}

	set demographicNo(value: string)
	{
		this._demographicNo = value;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get firstName(): string
	{
		return this._firstName;
	}

	get middleName(): string
	{
		return this._middleName;
	}

	get lastName(): string
	{
		return this._lastName;
	}

	get displayName(): string
	{
		return `${this.firstName} ${this.lastName}`;
	}

	get birthDate(): Moment
	{
		return this._birthDate;
	}

	get age(): number
	{
		let now = moment();
		let age = moment.duration(now.diff(this.birthDate)).years();
		if (isNaN(age))
		{
			return null;
		}
		return age;
	}

	get sex(): Sex
	{
		return this._sex;
	}

	get healthCareProvinceCode(): Province
	{
		return this._healthCareProvinceCode;
	}

	get healthNumber(): string
	{
		return this._healthNumber;
	}

	get healthNumberVersion(): string
	{
		return this._healthNumberVersion;
	}

	get email(): string
	{
		return this._email;
	}

	get cellPhone(): string
	{
		return this._cellPhone;
	}

	get postalCode(): string
	{
		return this._postalCode;
	}

	get city(): string
	{
		return this._city;
	}

	get address(): string
	{
		return this._address;
	}

	get province(): Province
	{
		return this._province;
	}

	get fullAddress(): string
	{
		return `${this.address ? this._address : ''} ${this.city ? this.city : ''}, ${this.province ? this.province : ''}`
	}

	get hasVoipToken(): boolean
	{
		return this._hasVoipToken;
	}

	get linkStatus(): LinkStatus
	{
		return this._linkStatus;
	}

	get isConfirmed(): boolean
	{
		return this._linkStatus === LinkStatus.CONFIRMED || this._linkStatus === LinkStatus.VERIFIED;
	}

	get isVerified(): boolean
	{
		return this._linkStatus === LinkStatus.VERIFIED;
	}

	get isRejected(): boolean
	{
		return this._linkStatus === LinkStatus.CLINIC_REJECTED ||
			this._linkStatus === LinkStatus.PATIENT_REJECTED;
	}

	get canMessage(): boolean
	{
		return this._canMessage;
	}

	get demographicNo(): string
	{
		return this._demographicNo;
	}
}