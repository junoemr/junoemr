import {Moment} from "moment";
import MessageSource from "./MessageSource";
import Message from "./Message";

export default class Attachment
{
	protected _id: string;
	protected _name: string;
	protected _type: string;
	protected _createdAtDateTime: Moment;
	protected _source: MessageSource;
	protected _message: Message;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, name: string, type: string, createdAtDateTime: Moment)
	{
		this._id = id;
		this._name = name;
		this._type = type;
		this._createdAtDateTime = createdAtDateTime;
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set source(messageSource: MessageSource)
	{
		this._source = messageSource;
	}

	set message(message: Message)
	{
		this._message = message;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get name(): string
	{
		return this._name;
	}

	get type(): string
	{
		return this._type;
	}

	get createdAtDateTime(): Moment
	{
		return this._createdAtDateTime;
	}

	get source(): MessageSource
	{
		return this._source;
	}

	get message(): Message
	{
		return this._message;
	}
}