import MessagingServiceInterface from "../../../service/MessagingServiceInterface";
import MessageSource from "../../../model/MessageSource";
import Message from "../../../model/Message";
import {API_BASE_PATH} from "../../../../constants/ApiConstants";
import MessageDtoToMessageConverter from "../../../converter/MessageDtoToMessageConverter";
import MessagingError from "../../../../error/MessagingError";
import {MhaClinicMessagingApi} from "../../../../../../generated";

export default class ClinicMessagingService implements MessagingServiceInterface
{
	protected _mhaClinicMessagingApi: MhaClinicMessagingApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._mhaClinicMessagingApi = new MhaClinicMessagingApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
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
			throw new MessagingError(`Failed to retrieve message [${messageId}] with error: ${error.toString()}`)
		}
	}

}