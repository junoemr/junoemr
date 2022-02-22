import {AddressResidencyStatus} from "./AddressResidencyStatus";
import {AddressModel} from "../../../../generated";
import {CountryCode} from "../../constants/CountryCode";
import {Province} from "../../constants/Province";
import {USStateCode} from "../../constants/USStateCode";
import ResidencyStatusEnum = AddressModel.ResidencyStatusEnum;

export default class Address
{
	private _addressLine1: string;
	private _addressLine2: string;
	private _city: string;
	private _postalCode: string;
	private _regionCode: Province | USStateCode; // changes based on country code
	private _countryCode: CountryCode;
	private _residencyStatus: AddressResidencyStatus;

	/**
	 * display and formatting helpers
	 */

	constructor(residencyStatus: ResidencyStatusEnum = AddressResidencyStatus.Past)
	{
		this.countryCode = CountryCode.CA;
		this.residencyStatus = residencyStatus;
	}

	get displayLine1of2(): string
	{
		let line = "";
		if(!Juno.Common.Util.isBlank(this.addressLine1))
		{
			line += this.addressLine1;
		}
		if(!Juno.Common.Util.isBlank(this.addressLine2))
		{
			line += " " + this.addressLine2;
		}
		return line;
	}

	get displayLine2of2(): string
	{
		let line = "";
		if(!Juno.Common.Util.isBlank(this.city))
		{
			line += this.city + ", ";
		}
		if(!Juno.Common.Util.isBlank(this.regionCode))
		{
			line += this.regionCode + " ";
		}
		if(!Juno.Common.Util.isBlank(this.countryCode))
		{
			line += this.countryCode + " ";
		}
		if(!Juno.Common.Util.isBlank(this.postalCode))
		{
			line += this.postalCode;
		}
		return line.trim();
	}

	/**
	 * validation helpers
	 */

	public isValidPostalOrZip(nullable: boolean = true): boolean
	{
		if (!this.postalCode)
		{
			return nullable;
		}
		if(this.countryCode === CountryCode.CA)
		{
			const regex = new RegExp(/^[A-Za-z]\d[A-Za-z][ ]?\d[A-Za-z]\d$/); // Match to Canadian postal code standard
			return (regex.test(this.postalCode));
		}
		else
		{
			// todo US zip code validation?
			return true;
		}
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

	get regionCode(): Province | USStateCode
	{
		return this._regionCode;
	}

	set regionCode(value: Province | USStateCode)
	{
		this._regionCode = value;

		// set the country based on incoming region code for now, as country code not really supported in ui
		if ((<any>Object).values(USStateCode).includes(value))
		{
			this.countryCode = CountryCode.US;
		}
		else
		{
			this.countryCode = CountryCode.CA;
		}
	}

	get countryCode(): CountryCode
	{
		return this._countryCode;
	}

	set countryCode(value: CountryCode)
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