import {MessagingServiceType} from "../model/MessagingServiceType";
import defaultMessagingStrings from "../resources/DefaultMessagingStrings.json";
import mhaMessagingStrings from "../myhealthaccess/resources/MhaMessagingStrings.json"
import JsonStringResourceSet from "../../common/model/JsonStringResourceSet";
import StringResourceSet from "../../common/model/StringResourceSet";

export default class MessagingStringResourceSetFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static build(type: MessagingServiceType): StringResourceSet
	{
		const defaultStringResources = new JsonStringResourceSet(defaultMessagingStrings, "DefaultMessagingStrings.json");

		switch (type)
		{
			case MessagingServiceType.MHA_CLINIC:
				return new JsonStringResourceSet(mhaMessagingStrings, "MhaMessagingStrings.json", defaultStringResources);
			default:
				return defaultStringResources;
		}
	}
}