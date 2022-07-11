export default class AppointmentStatus
{
	private readonly _id: number;

	private _statusCode: string;
	private _description: string;
	private _active: boolean;
	private _editable: boolean;

	constructor(id)
	{
		this._id = id;
	}

	get id(): number
	{
		return this._id;
	}

	get statusCode(): string
	{
		return this._statusCode;
	}

	set statusCode(value: string)
	{
		this._statusCode = value;
	}

	get description(): string
	{
		return this._description;
	}

	set description(value: string)
	{
		this._description = value;
	}

	get active(): boolean
	{
		return this._active;
	}

	set active(value: boolean)
	{
		this._active = value;
	}

	get editable(): boolean
	{
		return this._editable;
	}

	set editable(value: boolean)
	{
		this._editable = value;
	}
}