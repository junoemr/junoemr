import AbstractConverter from "../../conversion/AbstractConverter";
import {ProviderModel} from "../../../../generated";
import Provider from "../model/Provider";
import moment from "moment";
import AddressToModelConverter from "../../common/converter/AddressToModelConverter";
import PhoneNumberToModelConverter from "../../common/converter/PhoneNumberToModelConverter";
import {Sex} from "../../demographic/model/Sex";
import {ProviderTitleType} from "../model/ProviderTitleType";

export default class ProviderToModelConverter extends AbstractConverter<ProviderModel, Provider>
{
	convert(from: ProviderModel): Provider
	{
		if(!from)
		{
			return null;
		}

		let model = new Provider(from.id, from.lastUpdateUserId, moment(from.lastUpdateDateTime));
		model.firstName = from.firstName;
		model.lastName = from.lastName;
		model.ohipNumber = from.ohipNumber;

		model.providerType = from.providerType;
		model.sex = from.sex as any as Sex;
		model.dateOfBirth = from.dateOfBirth ? moment(from.dateOfBirth) : null;
		model.title = from.title as any as ProviderTitleType;
		model.email = from.email;

		let phoneConverter = new PhoneNumberToModelConverter();
		model.homePhone = phoneConverter.convert(from.homePhone);
		model.workPhone = phoneConverter.convert(from.workPhone);
		model.cellPhone = phoneConverter.convert(from.cellPhone);

		model.address = new AddressToModelConverter().convert(from.address);

		model.active = from.status === "1";
		model.specialty = from.specialty;
		model.team = from.team;
		model.rmaNumber = from.rmaNumber;
		model.billingNumber = from.billingNumber;
		model.hsoNumber = from.hsoNumber;
		model.practitionerNumber = from.practitionerNumber;
		model.jobTitle = from.jobTitle;
		model.signedConfidentialityDateTime = from.signedConfidentialityDateTime ? moment(from.signedConfidentialityDateTime) : null;
		model.supervisor = from.supervisor?.id;

		return model;
	}
}