import Message from "./Message";
import Messageable from "./Messageable";

export default class Conversation
{
	protected _id: string;
	protected _messages: Message[];

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, messages: Message[])
	{
		this._id = id;
		this._messages = messages;
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set messages(messages: Message[])
	{
		this._messages = messages;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get messages(): Message[]
	{
		return this._messages;
	}

	/**
	 * get all participants in this conversation
	 */
	get participants(): Messageable[]
	{
		const recipients: Messageable[] = this.messages.map((msg) => msg.recipients).flat();
		const senders: Messageable[] = this.messages.map((msg) => msg.sender);

		// @ts-ignore
		return Juno.Common.Util.arrayDistinct(senders.concat(recipients), "id");
	}
}