import {Moment} from "moment";
import MessageSource from "./MessageSource";
import Message from "./Message";
import JunoFile from "../../documents/model/JunoFile";

export default class Attachment implements JunoFile
{
	protected _id: string;
	protected _name: string;
	protected _type: string;
	protected _createdAtDateTime: Moment;
	protected _source: MessageSource;
	protected _message: Message;
	protected _base64Data: string;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, name: string, type: string, createdAtDateTime: Moment, base64Data = null)
	{
		this._id = id;
		this._name = name;
		this._type = type;
		this._createdAtDateTime = createdAtDateTime;
		this._base64Data = base64Data;
	}

	/**
	 * get the files data as base64 encoded string
	 */
	public async getBase64Data(): Promise<string>
	{
		return this._base64Data;
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

	set base64Data(data: string)
	{
		this._base64Data = data;
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

	get createdAt(): Moment
	{
		return this._createdAtDateTime;
	}

	get updatedAt(): Moment
	{
		// attachments cannot be updated.
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