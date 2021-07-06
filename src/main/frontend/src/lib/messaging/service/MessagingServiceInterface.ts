import MessageSource from "../model/MessageSource";
import Message from "../model/Message";
import {Moment} from "moment";
import {MessageGroup} from "../model/MessageGroup";
import Messageable from "../model/Messageable";
import StreamingList from "../../util/StreamingList";
import Conversation from "../model/Conversation";
import Attachment from "../model/Attachment";
import {MessagingServiceType} from "../model/MessagingServiceType";

export default interface MessagingServiceInterface
{
	// ==========================================================================
	// Interface Methods
	// ==========================================================================

	/**
	 * @return the type of this messaging service.
	 */
	getType(): MessagingServiceType

	/**
	 * get a message
	 * @param source - the message source to pull from
	 * @param messageId - the message id to get
	 * @return - the message
	 */
	getMessage(source: MessageSource, messageId: string): Promise<Message>;

	/**
	 * update the message attributes
	 * @param message - the message to update.
	 * @return message - a freshly loaded copy of the message from the server.
	 */
	updateMessage(message: Message): Promise<Message>;

	/**
	 * send a message
	 * @param source - the source to send the message through
	 * @param message - the message to send.
	 */
	sendMessage(source: MessageSource, message: Message): Promise<Message>;

	/**
	 * search messages from the specified message source.
	 * @param source - the source to search in.
	 * @param searchOptions - filters to narrow the search.
	 * @return search result. list of messages.
	 */
	searchMessages(source: MessageSource, searchOptions: MessageSearchParams): Promise<Message[]>;

	/**
	 * like searchMessages but returns a streamingList such that all results are not fetched at once but can be fetched piece by piece.
	 * @param source - the source to search in.
	 * @param searchOptions - filters to narrow the search.
	 * @return search results as a StreamingList. Can be used the same as an array however it will not contain all results. You must call load() to get
	 * additional results.
	 */
	searchMessagesAsStream(source: MessageSource, searchOptions: MessageSearchParams): Promise<StreamingList<Message>>;

	/**
	 * count messages.
	 * @param source - source to count messages in
	 * @param group - [optional] group to count messages in
	 * @param onlyUnread - [optional] if true only count unread messages
	 * @return the count of messages
	 */
	countMessages(source: MessageSource, group?: MessageGroup, onlyUnread?: boolean): Promise<number>;

	/**
	 * get a conversation
	 * @param source - source to get conversation from
	 * @param conversationId - the conversation id to get
	 */
	getConversation(source: MessageSource, conversationId: string): Promise<Conversation>;

	/**
	 * download base64 attachment data
	 * @param attachment - that data who's data is to be downloaded
	 * @return promise - that resolves to base 64 attachment data.
 	 */
	downloadAttachmentData(attachment: Attachment): Promise<string>;

	/**
	 * get a list of all available message sources.
	 * @return list of message sources
	 */
	getMessageSources(): Promise<MessageSource[]>;

	/**
	 * get the default source
	 * @return the default source
	 */
	getDefaultMessageSource(): Promise<MessageSource>;

	/**
	 * get a message source by it's id.
	 * @param id
	 * @return the message source
	 */
	getMessageSourceById(id: string): Promise<MessageSource>;

	/**
	 * get a list of allowed message groups for this messaging service
	 * @return list of allowed groups
	 */
	getMessageGroups(): Promise<MessageGroup[]>;

	/**
	 * get a messageable by id.
	 * @param messageSource - the source to fetch the messageable from
	 * @param messageableId - the id of the messageable to fetch
	 * @return the messageable
	 */
	getMessageable(messageSource: MessageSource, messageableId: string): Promise<Messageable>;

	/**
	 * search messageables by keyword
	 * @param messageSource - the source in which to perform the search
	 * @param keyword - the keyword to search by
	 * @return a list of matching messageables
	 */
	searchMessageables(messageSource: MessageSource, keyword: string): Promise<Messageable[]>;
}

/**
 * Search parameter hash for search interface methods.
 */
export interface MessageSearchParams
{
	startDateTime?: Moment, // items before this time will be filtered
	endDateTime?: Moment, // items after this time will be filtered
	group?: MessageGroup, // items not in this message group will be filtered
	limit?: number, // limit the returned results to this number
	offset?: number, // offset the returned results by this number
	sender?: Messageable, // limit to only items sent by this sender
	recipient?: Messageable, // limit to only items received by this recipient
}