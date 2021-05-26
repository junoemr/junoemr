import {LinkStatus} from "./LinkStatus";
import {Province} from "../../../constants/Province";

export default class MhaPatient
{
	protected _id: string;
	protected _firstName: string;
	protected _middleName: string;
	protected _lastName: string;

	protected _healthCareProvinceCode: Province;
	protected _healthNumber: string;
	protected _healthNumberVersion: string;

	protected _email: string;
	protected _cellPhone: string;
	protected _postalCode: string;
	protected _city: string;
	protected _address: string;
	protected _province: Province;

	protected _linkStatus: LinkStatus;
	private _canMessage: boolean;

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

	set linkStatus(value: LinkStatus)
	{
		this._linkStatus = value;
	}

	set canMessage(value: boolean)
	{
		this._canMessage = value;
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

	get linkStatus(): LinkStatus
	{
		return this._linkStatus;
	}

	get isConfirmed(): boolean
	{
		return this._linkStatus === LinkStatus.CONFIRMED || this._linkStatus === LinkStatus.VERIFIED
	}

	get canMessage(): boolean
	{
		return this._canMessage;
	}
}