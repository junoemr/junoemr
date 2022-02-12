import {AddressResidencyStatus} from "./AddressResidencyStatus";

export default class Address
{
	private _addressLine1: string;
	private _addressLine2: string;
	private _city: string;
	private _postalCode: string;
	private _regionCode: string;
	private _countryCode: string;
	private _residencyStatus: AddressResidencyStatus;

	/**
	 * display and formatting helpers
	 */

	constructor()
	{
		this.countryCode = "CA";
		this.residencyStatus = AddressResidencyStatus.Past;
	}

	get displayLine1of2(): string
	{
		return this.addressLine1 + " " + this.addressLine2;
	}

	get displayLine2of2(): string
	{
		return this.city + ", " + this.regionCode + " " + this.countryCode + " " + this.postalCode;
	}

	/**
	 * getters and setters
	 */

	get addressLine1(): string
	{
		return this._addressLine1;
	}

	set addressLine1(value: string)
	{
		this._addressLine1 = value;
	}

	get addressLine2(): string
	{
		return this._addressLine2;
	}

	set addressLine2(value: string)
	{
		this._addressLine2 = value;
	}

	get city(): string
	{
		return this._city;
	}

	set city(value: string)
	{
		this._city = value;
	}

	get postalCode(): string
	{
		return this._postalCode;
	}

	set postalCode(value: string)
	{
		this._postalCode = value;
	}

	get regionCode(): string
	{
		return this._regionCode;
	}

	set regionCode(value: string)
	{
		this._regionCode = value;
	}

	get countryCode(): string
	{
		return this._countryCode;
	}

	set countryCode(value: string)
	{
		this._countryCode = value;
	}

	get residencyStatus(): AddressResidencyStatus
	{
		return this._residencyStatus;
	}

	set residencyStatus(value: AddressResidencyStatus)
	{
		this._residencyStatus = value;
	}
}