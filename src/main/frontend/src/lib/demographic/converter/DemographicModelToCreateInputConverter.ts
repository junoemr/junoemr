import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicCreateInput} from "../../../../generated";

export default class DemographicModelToCreateInputConverter extends AbstractConverter<Demographic, DemographicCreateInput>
{
	convert(from: Demographic, args: any): DemographicCreateInput
	{
		let createInput = {} as DemographicCreateInput;
		createInput.firstName = from.firstName;
		createInput.lastName = from.lastName;
		createInput.dateOfBirth = this.serializeDateTime(from.dateOfBirth);
		createInput.sex = DemographicCreateInput.SexEnum[from.sex];
		createInput.healthNumber = from.healthNumber;
		createInput.healthNumberVersion = from.healthNumberVersion;
		createInput.healthNumberProvinceCode = from.healthNumberProvinceCode;
		createInput.healthNumberCountryCode = from.healthNumberCountryCode;
		createInput.healthNumberEffectiveDate = this.serializeDateTime(from.healthNumberEffectiveDate);
		createInput.healthNumberRenewDate = this.serializeDateTime(from.healthNumberRenewDate);
		createInput.dateJoined = this.serializeDateTime(from.dateJoined);

		console.info("debug transfer conversion", createInput);
		return createInput;
	}
}