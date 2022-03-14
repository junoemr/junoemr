import {Moment} from "moment";
import moment from "moment/moment";

export default class DemographicWaitingList
{
	private _id: number;
	private _position: number;
	private _note: string;
	private _dateAddedToWaitList: Moment;
	private _archived: boolean;
	private _waitListId: number;

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get position(): number
	{
		return this._position;
	}

	set position(value: number)
	{
		this._position = value;
	}

	get note(): string
	{
		return this._note;
	}

	set note(value: string)
	{
		this._note = value;
	}

	get dateAddedToWaitList(): Moment
	{
		return this._dateAddedToWaitList;
	}

	set dateAddedToWaitList(value: Moment)
	{
		this._dateAddedToWaitList = value;
	}

	get archived(): boolean
	{
		return this._archived;
	}

	set archived(value: boolean)
	{
		this._archived = value;
	}

	get waitListId(): number
	{
		return this._waitListId;
	}

	set waitListId(value: number)
	{
		this._waitListId = value;
	}
}