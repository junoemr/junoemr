import MessagingServiceInterface, {MessageSearchParams} from "../../../service/MessagingServiceInterface";
import MessageSource from "../../../model/MessageSource";
import Message from "../../../model/Message";
import {API_BASE_PATH} from "../../../../constants/ApiConstants";
import MessageDtoToMessageConverter from "../../../converter/MessageDtoToMessageConverter";
import MessagingError from "../../../../error/MessagingError";
import {MhaClinicMessagingApi, MhaIntegrationApi} from "../../../../../../generated";
import StreamingList from "../../../../util/StreamingList";
import ClinicMailboxStreamSource from "../model/ClinicMailboxStreamSource";
import Conversation from "../../../model/Conversation";
import ConversationDtoToConversationConverter from "../../../converter/ConversationDtoToConversationConverter";
import IntegrationTo1ToMessageSourceConverter from "../../../converter/IntegrationTo1ToMessageSourceConverter";
import {MessageSourceType} from "../../../model/MessageSourceType";
import {MessageGroup} from "../../../model/MessageGroup";

export default class ClinicMessagingService implements MessagingServiceInterface
{
	protected readonly VIRTUAL_SOURCE_ID = "all";
	protected readonly STREAM_INITIAL_LOAD_COUNT = 10;
	protected _mhaClinicMessagingApi: MhaClinicMessagingApi;
	protected _mhaIntegrationApi: MhaIntegrationApi;
	protected _messageSources: MessageSource[];

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaClinicMessagingApi = new MhaClinicMessagingApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);

		this._mhaIntegrationApi = new MhaIntegrationApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);

		this._messageSources = [];
	}

	// ==========================================================================
	// MessagingServiceInterface Implementation
	// ==========================================================================

	/**
	 * get a message
	 * @param source - the message source to pull from
	 * @param messageId - the message id to get
	 * @return - the message
	 */
	public async getMessage(source: MessageSource, messageId: string): Promise<Message>
	{
		try
		{
			const messageDto = (await this._mhaClinicMessagingApi.getMessage(source.id, messageId)).data.body;
			return (new MessageDtoToMessageConverter()).convert(messageDto);
		}
		catch(error)
		{
			throw new MessagingError(`Failed to retrieve message [${messageId}] from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
		}
	}

	/**
	 * search messages from the specified message source.
	 * @param source - the source to search in.
	 * @param searchOptions - filters to narrow the search.
	 * @return search result. list of messages.
	 */
	public async searchMessages(source: MessageSource, searchOptions: MessageSearchParams): Promise<Message[]>
	{
		try
		{
			const messages = (await this._mhaClinicMessagingApi.getMessages(
				source.id,
				searchOptions.startDateTime?.toDate(),
				searchOptions.endDateTime?.toDate(),
				searchOptions.group?.toString(),
				searchOptions.limit,
				searchOptions.offset,
				searchOptions.sender?.id,
				searchOptions.sender?.type.toString(),
				searchOptions.recipient?.id,
				searchOptions.recipient?.type.toString())).data.body;

			return (new MessageDtoToMessageConverter()).convertList(messages);
		}
		catch(error)
		{
			throw new MessagingError(`Failed to search messages from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
		}
	}

	/**
	 * like searchMessages but returns a streamingList such that all results are not fetched at once but can be fetched piece by piece.
	 * @param source - the source to search in.
	 * @param searchOptions - filters to narrow the search.
	 * @return search results as a StreamingList. Can be used the same as an array however it will not contain all results. You must call load() to get
	 * additional results.
	 */
	public async searchMessagesAsStream(source: MessageSource, searchOptions: MessageSearchParams): Promise<StreamingList<Message>>
	{
		const streamSource = new ClinicMailboxStreamSource(this, source, searchOptions);
		const stream = new StreamingList<Message>([streamSource], (t1, t2) => t1.createdAtDateTime.diff(t2.createdAtDateTime));
		await stream.load(this.STREAM_INITIAL_LOAD_COUNT);
		return stream;
	}

	/**
	 * get a conversation
	 * @param source - source to get conversation from
	 * @param conversationId - the conversation id to get
	 */
	public async getConversation(source: MessageSource, conversationId: string): Promise<Conversation>
	{
		try
		{
			const conversationDto = (await this._mhaClinicMessagingApi.getConversation(source.id, conversationId)).data.body;
			return (new ConversationDtoToConversationConverter()).convert(conversationDto);
		}
		catch(error)
		{
			throw new MessagingError(`Failed to retrieve conversation [${conversationId}] from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
		}
	}

	/**
	 * get a list of all available message sources.
	 * @return list of message sources
	 */
	public async getMessageSources(): Promise<MessageSource[]>
	{
		if (this._messageSources.length === 0)
		{
			await this.loadMessagingSources();
		}

		return this._messageSources;
	}

	/**
	 * get a list of allowed message groups for this messaging service
	 * @return list of allowed groups
	 */
	public async getMessageGroups(): Promise<MessageGroup[]>
	{
		return [MessageGroup.RECEIVED, MessageGroup.SENT, MessageGroup.ARCHIVED];
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * load all messaging sources
	 * @protected
	 */
	protected async loadMessagingSources(): Promise<void>
	{
		const integrations = (await this._mhaIntegrationApi.searchIntegrations(null, true)).data.body;
		this._messageSources = [];
		this._messageSources.push(new MessageSource(this.VIRTUAL_SOURCE_ID, "All", MessageSourceType.VIRTUAL));
		this._messageSources = this._messageSources.concat((new IntegrationTo1ToMessageSourceConverter()).convertList(integrations));
	}

}