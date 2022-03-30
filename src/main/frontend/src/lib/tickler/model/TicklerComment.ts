import {Moment} from "moment";

export default class TicklerComment
{
	private _id: string;
	private _message: string;
	private _providerId: string;
	private _updateDateTime: Moment;

	get id(): string
	{
		return this._id;
	}

	set id(value: string)
	{
		this._id = value;
	}

	get message(): string
	{
		return this._message;
	}

	set message(value: string)
	{
		this._message = value;
	}

	get providerId(): string
	{
		return this._providerId;
	}

	set providerId(value: string)
	{
		this._providerId = value;
	}

	get updateDateTime(): Moment
	{
		return this._updateDateTime;
	}

	set updateDateTime(value: Moment)
	{
		this._updateDateTime = value;
	}
}