import Messageable from "../model/Messageable";
import Attachment from "../model/Attachment";
import Message from "../model/Message";
import moment from "moment";
import Conversation from "../model/Conversation";

export default class MessageFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	/**
	 * create a new message
	 * @param subject - subject of the message
	 * @param message - message body
	 * @param recipients - recipients of the message
	 * @param attachments - attachments of the message
	 * @param conversation - [optional] the conversation to attach the message to.
	 * @param metaData - [optional] meta data of the message
	 */
	public static build(subject: string, message: string, recipients: Messageable[], attachments: Attachment[], conversation: Conversation = null, metaData: any = null)
	{
		return new Message(null, conversation?.id, subject, message, null, false, null, recipients, metaData, moment(), attachments);
	}
}