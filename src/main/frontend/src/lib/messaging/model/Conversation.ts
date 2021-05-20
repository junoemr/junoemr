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
		// @ts-ignore
		return Juno.Common.Util.arrayDistinct(this.messages
				.map((msg) => msg.recipients)
				.flat(), "id");
	}
}