import Conversation from "../../../model/Conversation";
import Message from "../../../model/Message";
import MhaMessage from "./MhaMessage";

export class MhaConversation extends Conversation
{
	// ==========================================================================
	// Conversation Field overrides
	// ==========================================================================

	protected _messages: MhaMessage[];

	// ==========================================================================
	// Setters
	// ==========================================================================

	set messages(messages: MhaMessage[])
	{
		this._messages = messages;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get messages(): MhaMessage[]
	{
		return this._messages;
	}
}