import {OLISSystemSettingsUpdateInput} from "../../../../../generated";
import AbstractConverter from "../../../conversion/AbstractConverter";
import OlisSystemSettings from "../model/OlisSystemSettings";

export default class OlisSystemSettingsInputConverter extends AbstractConverter<OlisSystemSettings, OLISSystemSettingsUpdateInput>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: OlisSystemSettings): OLISSystemSettingsUpdateInput
	{
		return {
			startDateTime: from.startDateTime as unknown,
			frequency: from.frequency,
			filterPatients: from.filterPatients,
		} as OLISSystemSettingsUpdateInput;
	}

}