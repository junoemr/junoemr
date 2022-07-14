import Address from "../../../common/model/Address";
import PhoneNumber from "../../../common/model/PhoneNumber";

export default class Letterhead
{
	private readonly _id: string;

	private _name: string;
	private _address: Address;
	private _phone: PhoneNumber;
	private _fax: PhoneNumber;

	constructor(id)
	{
		this._id = id;
	}

	get id(): string
	{
		return this._id;
	}

	get name(): string
	{
		return this._name;
	}

	set name(value: string)
	{
		this._name = value;
	}

	get address(): Address
	{
		return this._address;
	}

	set address(value: Address)
	{
		this._address = value;
	}

	get phone(): PhoneNumber
	{
		return this._phone;
	}

	set phone(value: PhoneNumber)
	{
		this._phone = value;
	}

	get fax(): PhoneNumber
	{
		return this._fax;
	}

	set fax(value: PhoneNumber)
	{
		this._fax = value;
	}
}