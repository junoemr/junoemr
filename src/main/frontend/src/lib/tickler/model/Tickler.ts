import {Moment} from "moment";
import TicklerComment from "./TicklerComment";
import TicklerAttachment from "./TicklerAttachment";

export default class Tickler
{
	private _id: number;
	private _demographicId: number;
	private _message: string;
	private _status: string; //todo enum
	private _updateDateTime: Moment;
	private _serviceDateTime: Moment;
	private _creatorId: string;
	private _priority: string; //todo enum
	private _assignedProviderId: string;
	private _attachments: TicklerAttachment[];
	private _comments: TicklerComment[];


	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get demographicId(): number
	{
		return this._demographicId;
	}

	set demographicId(value: number)
	{
		this._demographicId = value;
	}

	get message(): string
	{
		return this._message;
	}

	set message(value: string)
	{
		this._message = value;
	}

	get status(): string
	{
		return this._status;
	}

	set status(value: string)
	{
		this._status = value;
	}

	get updateDateTime(): Moment
	{
		return this._updateDateTime;
	}

	set updateDateTime(value: Moment)
	{
		this._updateDateTime = value;
	}

	get serviceDateTime(): Moment
	{
		return this._serviceDateTime;
	}

	set serviceDateTime(value: Moment)
	{
		this._serviceDateTime = value;
	}

	get creatorId(): string
	{
		return this._creatorId;
	}

	set creatorId(value: string)
	{
		this._creatorId = value;
	}

	get priority(): string
	{
		return this._priority;
	}

	set priority(value: string)
	{
		this._priority = value;
	}

	get assignedProviderId(): string
	{
		return this._assignedProviderId;
	}

	set assignedProviderId(value: string)
	{
		this._assignedProviderId = value;
	}

	get attachments(): TicklerAttachment[]
	{
		return this._attachments;
	}

	set attachments(value: TicklerAttachment[])
	{
		this._attachments = value;
	}

	get comments(): TicklerComment[]
	{
		return this._comments;
	}

	set comments(value: TicklerComment[])
	{
		this._comments = value;
	}
}