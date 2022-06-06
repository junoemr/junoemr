import {OLISSystemSettingsTransfer} from "../../../../../generated";
import AbstractConverter from "../../../conversion/AbstractConverter";
import OlisSystemSettings from "../model/OlisSystemSettings";
import moment from "moment/moment";

export default class OlisSystemSettingsTransferToModelConverter extends AbstractConverter<OLISSystemSettingsTransfer, OlisSystemSettings>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: OLISSystemSettingsTransfer): OlisSystemSettings
	{
		const model = new OlisSystemSettings();
		model.startDateTime = (from.startDateTime) ? moment(from.startDateTime) : null;
		model.lastRunDateTime = (from.lastRunDateTime) ? moment(from.lastRunDateTime) : null;
		model.frequency = from.frequency;
		model.filterPatients = from.filterPatients;
		model.warnings = from.warnings;
		model.vendorId = from.vendorId;

		return model;
	}

}