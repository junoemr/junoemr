import {PhoneType} from "./PhoneType";

export default class PhoneNumber
{
	private _number: string;
	private _extension: string;
	private _primaryContactNumber: boolean;
	private _phoneType: PhoneType;

	constructor(number: string, extension: string, type: any, primaryContactNumber: boolean = true)
	{
		this.number = number;
		this.extension = extension;
		this.phoneType = type;
		this.primaryContactNumber = primaryContactNumber;
	}

	get formattedForDisplay(): string
	{
		let display = "";
		if(this.number)
		{
			if(this.number.length > 3 && this.number.length <= 7)
			{
				display = this.number.substring(0,3) + "-" + this.number.substring(3);
			}
			else if(this.number.length > 7 && this.number.length <= 10)
			{
				display = "(" + this.number.substring(0,3) + ") " + this.number.substring(3,6) + "-" + this.number.substring(6);
			}
			else
			{
				display = this.number;
			}
		}

		if(this.extension && this.extension.length > 0)
		{
			display += " Ext: " + this.extension;
		}
		return display.trim();
	}

	get number(): string
	{
		return this._number;
	}

	set number(value: string)
	{
		this._number = value;
	}

	get extension(): string
	{
		return this._extension;
	}

	set extension(value: string)
	{
		this._extension = value;
	}

	get primaryContactNumber(): boolean
	{
		return this._primaryContactNumber;
	}

	set primaryContactNumber(value: boolean)
	{
		this._primaryContactNumber = value;
	}

	get phoneType(): PhoneType
	{
		return this._phoneType;
	}

	set phoneType(value: PhoneType)
	{
		this._phoneType = value;
	}
}