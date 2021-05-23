import Message from "../../../model/Message";
import {MessageableType} from "../../../model/MessageableType";
import {MessageGroup} from "../../../model/MessageGroup";

export default class MhaMessage extends Message
{
	// ==========================================================================
	// Public Methods
	// ==========================================================================


	/**
	 * unarchive the message
	 */
	public unarchive(): void
	{
		if (this.sender.type === MessageableType.ClinicProfile)
		{
			this._group = MessageGroup.Sent;
		}
		else
		{
			this._group = MessageGroup.Received;
		}
	}

}