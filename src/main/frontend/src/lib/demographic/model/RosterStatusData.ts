import {Moment} from "moment";
import SimpleProvider from "../../provider/model/SimpleProvider";
import {RosterTerminationReasonType} from "./RosterTerminationReasonType";

export default class RosterStatusData
{
	private _id: number;
	private _statusCode: string;
	private _statusDescription: string;
	private _isRostered: boolean;
	private _rosterDateTime: Moment;
	private _terminationDateTime: Moment;
	private _terminationReason: RosterTerminationReasonType;
	private _rosterProvider: SimpleProvider;

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get statusCode(): string
	{
		return this._statusCode;
	}

	set statusCode(value: string)
	{
		this._statusCode = value;
	}

	get statusDescription(): string
	{
		return this._statusDescription;
	}

	set statusDescription(value: string)
	{
		this._statusDescription = value;
	}

	get isRostered(): boolean
	{
		return this._isRostered;
	}

	set isRostered(value: boolean)
	{
		this._isRostered = value;
	}

	get rosterDateTime(): Moment
	{
		return this._rosterDateTime;
	}

	set rosterDateTime(value: Moment)
	{
		this._rosterDateTime = value;
	}

	get terminationDateTime(): Moment
	{
		return this._terminationDateTime;
	}

	set terminationDateTime(value: Moment)
	{
		this._terminationDateTime = value;
	}

	get terminationReason(): RosterTerminationReasonType
	{
		return this._terminationReason;
	}

	set terminationReason(value: RosterTerminationReasonType)
	{
		this._terminationReason = value;
	}

	get rosterProvider(): SimpleProvider
	{
		return this._rosterProvider;
	}

	set rosterProvider(value: SimpleProvider)
	{
		this._rosterProvider = value;
	}
}