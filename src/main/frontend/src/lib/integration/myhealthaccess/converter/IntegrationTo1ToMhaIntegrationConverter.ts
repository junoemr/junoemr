import AbstractConverter from "../../../conversion/AbstractConverter";
import {IntegrationTo1} from "../../../../../generated";
import MhaIntegration from "../model/MhaIntegration";
import {IntegrationType} from "../../model/IntegrationType";

export default class IntegrationTo1ToMhaIntegrationConverter extends AbstractConverter<IntegrationTo1, MhaIntegration>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public convert(from: IntegrationTo1): MhaIntegration
	{
		return new MhaIntegration(from.id.toString(), from.remoteId, from.integrationType as IntegrationType, from.apiKey, from.siteId.toString(), from.siteName);
	}
}