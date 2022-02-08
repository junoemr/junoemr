import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicUpdateInput} from "../../../../generated";
import AddressToInputConverter from "../../common/converter/AddressToInputConverter";
import PhoneNumberToInputConverter from "../../common/converter/PhoneNumberToInputConverter";

export default class DemographicToUpdateInputConverter extends AbstractConverter<Demographic, DemographicUpdateInput>
{
	convert(from: Demographic, args: any): DemographicUpdateInput
	{
		let updateInput = {} as DemographicUpdateInput;
		updateInput.id = from.id;
		updateInput.firstName = from.firstName;
		updateInput.lastName = from.lastName;
		updateInput.alias = from.alias;
		updateInput.title = DemographicUpdateInput.TitleEnum[from.title];
		updateInput.dateOfBirth = this.serializeDateTime(from.dateOfBirth);
		updateInput.sex = DemographicUpdateInput.SexEnum[from.sex];
		updateInput.chartNumber = from.chartNumber;
		updateInput.healthNumber = from.healthNumber;
		updateInput.healthNumber = from.healthNumber;
		updateInput.healthNumberVersion = from.healthNumberVersion;
		updateInput.healthNumberProvinceCode = from.healthNumberProvinceCode;
		updateInput.healthNumberCountryCode = from.healthNumberCountryCode;
		updateInput.healthNumberEffectiveDate = this.serializeDateTime(from.healthNumberEffectiveDate);
		updateInput.healthNumberRenewDate = this.serializeDateTime(from.healthNumberRenewDate);
		updateInput.dateJoined = this.serializeDateTime(from.dateJoined);
		updateInput.addressList = new AddressToInputConverter().convertList(from.addressList);
		updateInput.email = from.email;

		const phoneConverter = new PhoneNumberToInputConverter();
		updateInput.cellPhone = phoneConverter.convert(from.cellPhone);
		updateInput.homePhone = phoneConverter.convert(from.homePhone);
		updateInput.workPhone = phoneConverter.convert(from.workPhone);

		updateInput.patientStatus = from.patientStatus;

		updateInput.aboriginal = from.aboriginal;
		updateInput.cytolNum = from.cytolNum;
		updateInput.paperChartArchived = from.paperChartArchived;
		updateInput.paperChartArchivedDate = this.serializeDateTime(from.paperChartArchivedDate);
		updateInput.usSigned = from.usSigned;
		updateInput.privacyConsent = from.privacyConsent;
		updateInput.informedConsent = from.informedConsent;
		updateInput.securityQuestion1 = from.securityQuestion1;
		updateInput.securityAnswer1 = from.securityAnswer1;
		updateInput.rxInteractionWarningLevel = from.rxInteractionWarningLevel;

		return updateInput;
	}
}