import {Moment} from "moment";
import {MessageGroup} from "./MessageGroup";
import Messageable from "./Messageable";

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
		createdAtDateTime: Moment
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
	}

}