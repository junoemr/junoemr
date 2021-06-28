import {Moment} from "moment";
import {MessageGroup} from "./MessageGroup";
import Messageable from "./Messageable";
import Attachment from "./Attachment";
import MessageSource from "./MessageSource";
import MessagingError from "../../error/MessagingError";

export default class Message
{
	protected _id: string;
	protected _conversationId: string;
	protected _subject: string;
	protected _message: string;
	protected _group: MessageGroup;
	protected _read: boolean;
	protected _sender: Messageable;
	protected _recipients: Messageable[];
	protected _metaData: any;
	protected _createdAtDateTime: Moment;
	protected _attachments: Attachment[];
	protected _source: MessageSource;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build new message
	 * @param id - message id
	 * @param conversationId - conversation id to which the message is associated
	 * @param subject - subject of the message
	 * @param message - actual message of the message
	 * @param group - group of the message
	 * @param read - if the message is read or not
	 * @param sender - the sender of the message
	 * @param recipients - recipient list of the message
	 * @param metaData - meta data for the message
	 * @param createdAtDateTime - time at which the message was created.
	 * @param attachments - attachments for this message
	 */
	constructor(
		id: string,
		conversationId: string,
		subject: string,
		message: string,
		group: MessageGroup,
		read: boolean,
		sender: Messageable,
		recipients: Messageable[],
		metaData: any,
		createdAtDateTime: Moment,
		attachments: Attachment[]
	)
	{
		this._id = id;
		this._conversationId = conversationId;
		this._subject = subject;
		this._message = message;
		this._group = group;
		this._read = read;
		this._sender = sender;
		this._recipients = recipients;
		this._metaData = metaData;
		this._createdAtDateTime = createdAtDateTime;
		this._attachments = attachments;
	}

	/**
	 * archive the message
	 */
	public archive(): void
	{
		this._group = MessageGroup.Archived;
	}

	/**
	 * unarchive the message
	 */
	public unarchive(): void
	{
		throw new MessagingError("Base message cannot be unarchived");
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set source(messageSource: MessageSource)
	{
		this._source = messageSource;
	}

	set read(read: boolean)
	{
		this._read = read;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get conversationId(): string
	{
		return this._conversationId;
	}

	get subject(): string
	{
		return this._subject;
	}

	get message(): string
	{
		return this._message;
	}

	get group(): MessageGroup
	{
		return this._group;
	}

	get read(): boolean
	{
		return  this._read;
	}

	get isRead(): boolean
	{
		return this._read;
	}

	get sender(): Messageable
	{
		return this._sender;
	}

	get recipients(): Messageable[]
	{
		return this._recipients;
	}

	get metaData(): any
	{
		return this._metaData;
	}

	get createdAtDateTime(): Moment
	{
		return this._createdAtDateTime;
	}

	get attachments(): Attachment[]
	{
		return this._attachments;
	}
	
	get hasAttachments(): boolean
	{
		return this._attachments && this._attachments.length > 0;
	}

	get source(): MessageSource
	{
		return this._source;
	}
}