import MessageSource from "../model/MessageSource";
import Message from "../model/Message";
import {Moment} from "moment";
import {MessageGroup} from "../model/MessageGroup";
import Messageable from "../model/Messageable";

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

	/**
	 * search messages from the specified message source.
	 * @param source - the source to search in.
	 * @param searchOptions - filters to narrow the search.
	 * @return search result. list of messages.
	 */
	searchMessages(source: MessageSource, searchOptions: MessageSearchParams): Promise<Message[]>;
}

/**
 * Search parameter hash for search interface methods.
 */
export interface MessageSearchParams
{
	startDateTime: Moment, // items before this time will be filtered
	endDateTime: Moment, // items after this time will be filtered
	group: MessageGroup, // items not in this message group will be filtered
	limit: number, // limit the returned results to this number
	offset: number, // offset the returned results by this number
	sender: Messageable, // limit to only items sent by this sender
	recipient: Messageable, // limit to only items received by this recipient
}