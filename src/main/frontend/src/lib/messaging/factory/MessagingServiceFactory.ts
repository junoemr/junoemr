import MessagingServiceInterface from "../service/MessagingServiceInterface";
import {MessagingServiceType} from "../model/MessagingServiceType";
import ClinicMessagingService from "../myhealthaccess/clinic/service/ClinicMessagingService";
import MessagingError from "../../error/MessagingError";

export default class MessagingServiceFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	/**
	 * build a new messaging service
	 * @param type - the backend type of the service
	 */
	public static build(type: MessagingServiceType): MessagingServiceInterface
	{
		switch (type)
		{
			case MessagingServiceType.MHA_CLINIC:
				return new ClinicMessagingService();
			default:
				throw new MessagingError(`Failed to build messaging service! Messaging type [${type}] is unknown`)
		}
	}
}