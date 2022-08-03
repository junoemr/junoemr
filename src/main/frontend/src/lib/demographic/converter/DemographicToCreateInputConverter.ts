import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicCreateInput} from "../../../../generated";
import AddressToInputConverter from "../../common/converter/AddressToInputConverter";
import PhoneNumberToInputConverter from "../../common/converter/PhoneNumberToInputConverter";

export default class DemographicToCreateInputConverter extends AbstractConverter<Demographic, DemographicCreateInput>
{
	convert(from: Demographic, args: any): DemographicCreateInput
	{
		let createInput = {} as DemographicCreateInput;
		createInput.firstName = from.firstName;
		createInput.middleName = from.middleName;
		createInput.lastName = from.lastName;
		createInput.dateOfBirth = this.serializeLocalDateTime(from.dateOfBirth);
		createInput.sex = DemographicCreateInput.SexEnum[from.sex];
		createInput.healthNumber = from.healthNumber;
		createInput.healthNumberVersion = from.healthNumberVersion;
		createInput.healthNumberProvinceCode = from.healthNumberProvinceCode;
		createInput.healthNumberCountryCode = from.healthNumberCountryCode;
		createInput.healthNumberEffectiveDate = this.serializeLocalDateTime(from.healthNumberEffectiveDate);
		createInput.healthNumberRenewDate = this.serializeLocalDateTime(from.healthNumberRenewDate);
		createInput.addressList = new AddressToInputConverter().convertList(from.addressList);
		createInput.email = from.email;

		const phoneConverter = new PhoneNumberToInputConverter();
		createInput.cellPhone = phoneConverter.convert(from.cellPhone);
		createInput.homePhone = phoneConverter.convert(from.homePhone);
		createInput.workPhone = phoneConverter.convert(from.workPhone);

		// physician info
		createInput.mrpProviderId = from.mrpProvider?.id;

		return createInput;
	}
}