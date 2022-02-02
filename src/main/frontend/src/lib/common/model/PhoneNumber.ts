export default class PhoneNumber
{
	private _number: string;
	private _extension: string;
	private _primaryContactNumber: boolean;
	private _phoneType: any; //todo

	get formattedForDisplay(): string
	{
		let display = "";
		if(this.number && this.number.length == 10)
		{
			display = "(" + this.number.substring(0,3) + ") " + this.number.substring(3,6) + "-" + this.number.substring(6);
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

	get phoneType(): any
	{
		return this._phoneType;
	}

	set phoneType(value: any)
	{
		this._phoneType = value;
	}
}