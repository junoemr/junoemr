import Message from "../../../model/Message";
import {MessageableType} from "../../../model/MessageableType";
import {MessageGroup} from "../../../model/MessageGroup";
import MhaAttachment from "./MhaAttachment";

export default class MhaMessage extends Message
{

	// ==========================================================================
	// Message Field overrides
	// ==========================================================================

	protected _attachments: MhaAttachment[];

	// ==========================================================================
	// Public Methods. Message overrides
	// ==========================================================================


	/**
	 * unarchive the message
	 */
	public unarchive(): void
	{
		if (this.sender.type === MessageableType.ClinicProfile)
		{
			this._group = MessageGroup.sent;
		}
		else
		{
			this._group = MessageGroup.received;
		}
	}

}