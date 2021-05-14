import Message from "./Message";

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
}