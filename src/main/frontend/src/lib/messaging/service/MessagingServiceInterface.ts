import MessageSource from "../model/MessageSource";
import Message from "../model/Message";

export default interface MessagingServiceInterface
{
	// ==========================================================================
	// Interface Methods
	// ==========================================================================

	/**
	 * get a message
	 * @param source - the message source to pull from
	 * @param messageId - the message id to get
	 * @return - the message
	 */
	getMessage(source: MessageSource, messageId: string): Promise<Message>;
}