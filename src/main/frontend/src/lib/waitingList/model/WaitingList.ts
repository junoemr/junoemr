import SimpleProvider from "../../provider/model/SimpleProvider";
import {Moment} from "moment";

export default class WaitingList
{
	private _id: number;
	private _name: string;
	private _groupNumber: string;
	private _provider: SimpleProvider;
	private _createdDateTime: Moment;
	private _archived: boolean;

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get name(): string
	{
		return this._name;
	}

	set name(value: string)
	{
		this._name = value;
	}

	get groupNumber(): string
	{
		return this._groupNumber;
	}

	set groupNumber(value: string)
	{
		this._groupNumber = value;
	}

	get provider(): SimpleProvider
	{
		return this._provider;
	}

	set provider(value: SimpleProvider)
	{
		this._provider = value;
	}

	get createdDateTime(): moment.Moment
	{
		return this._createdDateTime;
	}

	set createdDateTime(value: moment.Moment)
	{
		this._createdDateTime = value;
	}

	get archived(): boolean
	{
		return this._archived;
	}

	set archived(value: boolean)
	{
		this._archived = value;
	}
}