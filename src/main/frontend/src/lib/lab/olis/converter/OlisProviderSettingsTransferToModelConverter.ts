import OlisProviderSettings from "../model/OlisProviderSettings";
import {OLISProviderSettingsTransfer} from "../../../../../generated";
import AbstractConverter from "../../../conversion/AbstractConverter";
import moment from "moment/moment";

export default class OlisProviderSettingsTransferToModelConverter extends AbstractConverter<OLISProviderSettingsTransfer, OlisProviderSettings>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: OLISProviderSettingsTransfer): OlisProviderSettings
	{
		const model = new OlisProviderSettings();
		model.startDateTime = (from.startDateTime) ? moment(from.startDateTime) : null;
		model.isConfigured = from.configured;
		model.provider = from.provider;

		return model;
	}

}