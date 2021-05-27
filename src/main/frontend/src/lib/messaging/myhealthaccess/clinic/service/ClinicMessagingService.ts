import MessagingServiceInterface, {MessageSearchParams} from "../../../service/MessagingServiceInterface";
import MessageSource from "../../../model/MessageSource";
import Message from "../../../model/Message";
import {API_BASE_PATH} from "../../../../constants/ApiConstants";
import MessagingError from "../../../../error/MessagingError";
import {MessageDto, MhaClinicMessagingApi, MhaIntegrationApi, MhaPatientApi} from "../../../../../../generated";
import StreamingList, {StreamSource} from "../../../../util/StreamingList";
import ClinicMailboxStreamSource from "../model/ClinicMailboxStreamSource";
import Conversation from "../../../model/Conversation";
import IntegrationTo1ToMessageSourceConverter from "../../../converter/IntegrationTo1ToMessageSourceConverter";
import {MessageSourceType} from "../../../model/MessageSourceType";
import {MessageGroup} from "../../../model/MessageGroup";
import Attachment from "../../../model/Attachment";
import MessageToMessageDtoConverter from "../../../converter/MessageToMessageDtoConverter";
import {message} from "gulp-typescript/release/utils";
import MessageDtoToMhaMessageConverter from "../../converter/MessageDtoToMhaMessageConverter";
import ConversationDtoToMhaConversationConverter from "../../converter/ConversationDtoToMhaConversationConverter";
import Messageable from "../../../model/Messageable";
import PatientTo1ToMhaPatientConverter
	from "../../../../integration/myhealthaccess/converter/PatientTo1ToMhaPatientConverter";
import MhaPatientToMessageableConverter from "../../converter/MhaPatientToMessageableConverter";

export default class ClinicMessagingService implements MessagingServiceInterface
{
	protected readonly VIRTUAL_SOURCE_ID = "all";
	protected readonly STREAM_INITIAL_LOAD_COUNT = 10;
	protected _mhaClinicMessagingApi: MhaClinicMessagingApi;
	protected _mhaIntegrationApi: MhaIntegrationApi;
	protected _mhaPatientApi: MhaPatientApi;
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

		this._mhaPatientApi = new MhaPatientApi(
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
		if (source.isVirtual)
		{
			// try grabbing message from all sources.
			const searchResults = await Promise.all(this.getPhysicalMessagingSources().map(async (physicalSource) =>
			{
				try
				{
					return await this.getMessage(physicalSource, messageId)
				}
				catch (error)
				{
					return null;
				}
			}));

			const message = (await Promise.all(searchResults)).filter((msg) => msg != null)[0];
			if (message)
			{
				return message;
			}
			else
			{
				throw new MessagingError(`Failed to retrieve message [${messageId}] from source [${source.id}]`);
			}
		}
		else
		{
			try
			{
				const messageDto = (await this._mhaClinicMessagingApi.getMessage(source.id, messageId)).data.body;
				return this.postProcessMessage((new MessageDtoToMhaMessageConverter()).convert(messageDto), source);
			}
			catch(error)
			{
				throw new MessagingError(`Failed to retrieve message [${messageId}] from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
			}
		}
	}

	/**
	 * update the message attributes
	 * @param message - the message to update.
	 * @return message - a freshly loaded copy of the message from the server.
	 */
	public async updateMessage(message: Message): Promise<Message>
	{
		try
		{
			const messageDto: MessageDto = (new MessageToMessageDtoConverter()).convert(message);
			const updatedMessageDto = (await this._mhaClinicMessagingApi.updateMessage(message.source.id, messageDto.id, messageDto)).data.body;

			return this.postProcessMessage((new MessageDtoToMhaMessageConverter()).convert(updatedMessageDto), message.source);
		}
		catch(error)
		{
			throw new MessagingError(`Failed to update message [${message.id}] in source [${message.source.id}] with error: ${error.toString()} - ${error.status}`)
		}
	}

	/**
	 * send a message
	 * @param source - the source to send the message through
	 * @param message - the message to send.
	 */
	public async sendMessage(source: MessageSource, message: Message): Promise<Message>
	{
		if (source.isVirtual)
		{
			throw new MessagingError("Cannot send message through virtual message source");
		}

		try
		{
			const sentMessageDto: MessageDto = (await this._mhaClinicMessagingApi.sendMessage(source.id, (new MessageToMessageDtoConverter()).convert(message))).data.body;
			return this.postProcessMessage((new MessageDtoToMhaMessageConverter()).convert(sentMessageDto), source);
		}
		catch(error)
		{
			throw new MessagingError(`Failed to send message through source [${source.id}] with error: ${error.toString()} - ${error.status}`)
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
		if (source.isVirtual)
		{
			const searchResults = await Promise.all(
				this.getPhysicalMessagingSources().map((physicalSource) => this.searchMessages(physicalSource, searchOptions)));

			let messages = searchResults.flat().sort(this.messageSortFunction);
			if (searchOptions.limit)
			{
				messages = messages.slice(0, searchOptions.limit);
			}

			return messages;
		}
		else
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

				return this.postProcessMessageList((new MessageDtoToMhaMessageConverter()).convertList(messages), source);
			}
			catch (error)
			{
				throw new MessagingError(`Failed to search messages from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
			}
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
		const streamSources: StreamSource<Message>[] = [];
		if (source.isVirtual)
		{
			this.getPhysicalMessagingSources().forEach((msgSource) =>
			{
				streamSources.push(new ClinicMailboxStreamSource(this, msgSource, searchOptions))
			})
		}
		else
		{
			streamSources.push(new ClinicMailboxStreamSource(this, source, searchOptions));
		}

		const stream = new StreamingList<Message>(streamSources, this.messageSortFunction);
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
		if (source.isVirtual)
		{
			// try grabbing conversation from all sources.
			const searchResults = await Promise.all(this.getPhysicalMessagingSources().map(async (physicalSource) =>
			{
				try
				{
					return await this.getConversation(physicalSource, conversationId)
				}
				catch (error)
				{
					return null;
				}
			}));

			const conversation = (await Promise.all(searchResults)).filter((convo) => convo != null)[0];
			if (conversation)
			{
				return conversation;
			}
			else
			{
				throw new MessagingError(`Failed to retrieve conversation [${conversationId}] from source [${source.id}]`);
			}
		}
		else
		{
			try
			{
				const conversationDto = (await this._mhaClinicMessagingApi.getConversation(source.id, conversationId)).data.body;
				const conversation = (new ConversationDtoToMhaConversationConverter()).convert(conversationDto);

				conversation.messages = this.postProcessMessageList(conversation.messages, source);
				return conversation
			}
			catch (error)
			{
				throw new MessagingError(`Failed to retrieve conversation [${conversationId}] from source [${source.id}] with error: ${error.toString()} - ${error.status}`)
			}
		}
	}

	/**
	 * download base64 attachment data
	 * @param attachment - that data who's data is to be downloaded
	 * @return promise - that resolves to base 64 attachment data.
	 */
	public async downloadAttachmentData(attachment: Attachment): Promise<string>
	{
		return (await this._mhaClinicMessagingApi.getFileDataBase64(attachment.source.id, attachment.message.id, attachment.id)).data.body;
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
	 * get a message source by it's id.
	 * @param id
	 * @return the message source
	 */
	public async getMessageSourceById(id: string): Promise<MessageSource>
	{
		const source = (await this.getMessageSources()).find((source) => source.id === id);

		if (source)
		{
			return source;
		}
		else
		{
			throw new MessagingError(`Messaging source with id [${id}] could not be found`);
		}
	}

	/**
	 * get a list of allowed message groups for this messaging service
	 * @return list of allowed groups
	 */
	public async getMessageGroups(): Promise<MessageGroup[]>
	{
		return [MessageGroup.Received, MessageGroup.Sent, MessageGroup.Archived];
	}

	/**
	 * search messageables by keyword
	 * @param messageSource - the source in which to perform the search
	 * @param keyword - the keyword to search by
	 * @return a list of matching messageables
	 */
	public searchMessageables(messageSource: MessageSource, keyword: string): Promise<Messageable[]>
	{
		if (messageSource.isVirtual)
		{
			return this.searchMessageablesVirtual(keyword);
		}
		return this.searchMessageablesPhysical(messageSource, keyword);
	}

	// ==========================================================================
	// Protected Methods
	// ==========================================================================

	/**
	 * searchMessageables impl for physical sources
	 * @see searchMessageables
	 * @protected
	 */
	protected async searchMessageablesPhysical(messageSource: MessageSource, keyword: string): Promise<Messageable[]>
	{
		try
		{
			const patients = (await this._mhaPatientApi.searchPatients(messageSource.id, keyword)).data.body;

			if (patients)
			{
				const mhaPatients = (new PatientTo1ToMhaPatientConverter()).convertList(patients);
				return (new MhaPatientToMessageableConverter()).convertList(mhaPatients.filter((patient) => patient.canMessage));
			}
		}
		catch(error)
		{
			throw new MessagingError(`Failed to search messageables in source ${messageSource.id} with error: ${error.toString()} - ${error.status}`)
		}
		return [];
	}

	/**
	 * searchMessageables impl for virtual sources
	 * @see searchMessageables
	 * @protected
	 */
	protected async searchMessageablesVirtual(keyword: string): Promise<Messageable[]>
	{
		let messageables: Messageable[] = [];

		for (let source of this.getPhysicalMessagingSources())
		{
			messageables = messageables.concat(await this.searchMessageablesPhysical(source, keyword));
		}

		// @ts-ignore
		messageables = Juno.Common.Util.arrayDistinct(messageables, "id");

		return messageables;
	}

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

	/**
	 * gets a list of messaging sources excluding any virtual ones.
	 * @protected
	 */
	protected getPhysicalMessagingSources(): MessageSource[]
	{
		return this._messageSources.filter((messageSource) => messageSource.type != MessageSourceType.VIRTUAL);
	}

	/**
	 * function used for sorting messages via (Array.sort)
	 * @param m1 - first message
	 * @param m2 - second message
	 * @return number - sort order according to sort spec
	 * @protected
	 */
	protected messageSortFunction(m1: Message, m2: Message): number
	{
		return m2.createdAtDateTime.diff(m1.createdAtDateTime);
	}

	/**
	 * apply post processing to message
	 * @param message - message to post process
	 * @param messageSource - the source form which the message was fetched.
	 * @return the processed message
	 * @protected
	 */
	protected postProcessMessage(message: Message, messageSource: MessageSource): Message
	{
		message.source = messageSource;
		message.attachments.forEach((attachment) => attachment.source = messageSource);
		return message;
	}

	/**
	 * apply postProcessMessage() to every message in the list
	 * @param messages - list of messages
	 * @param messageSource - the source form which the message was fetched.
	 * @return the process list of messages
	 * @protected
	 */
	protected postProcessMessageList(messages: Message[], messageSource: MessageSource): Message[]
	{
		return messages.map((msg) => this.postProcessMessage(msg, messageSource));
	}
}