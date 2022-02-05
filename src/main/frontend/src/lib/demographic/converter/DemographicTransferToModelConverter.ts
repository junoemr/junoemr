import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicModel} from "../../../../generated";
import moment from "moment";
import AddressToModelConverter from "../../common/converter/AddressToModelConverter";
import PhoneNumber from "../../common/model/PhoneNumber";
import {Sex} from "../model/Sex";

export default class DemographicTransferToModelConverter extends AbstractConverter<DemographicModel, Demographic>
{
	convert(from: DemographicModel, args: any): Demographic
	{
		let model = new Demographic();

		model.id = from.id;
		model.firstName = from.firstName;
		model.lastName = from.lastName;
		model.alias = from.alias;
		model.title = String(from.title);
		model.dateOfBirth = moment(from.dateOfBirth);
		model.sex = Sex[String(from.sex)];
		model.chartNumber = from.chartNumber;
		model.healthNumber = from.healthNumber;
		model.healthNumberVersion = from.healthNumberVersion;
		model.healthNumberProvinceCode = from.healthNumberProvinceCode;
		model.healthNumberEffectiveDate = moment(from.healthNumberEffectiveDate);
		model.healthNumberRenewDate = moment(from.healthNumberRenewDate);
		model.patientStatusDate = moment(from.patientStatusDate);
		model.dateJoined = moment(from.dateJoined);
		model.dateEnded = moment(from.dateEnded);
		model.lastUpdateDateTime = moment(from.lastUpdateDateTime);

		model.addressList = new AddressToModelConverter().convertList(from.addressList);
		model.email = from.email;
		model.homePhone = from.homePhone ? new PhoneNumber(from.homePhone.number, from.homePhone.extension, from.homePhone.phoneType, from.homePhone.primaryContactNumber) : null;
		model.workPhone = from.workPhone ? new PhoneNumber(from.workPhone.number, from.workPhone.extension, from.workPhone.phoneType, from.workPhone.primaryContactNumber) : null;
		model.cellPhone = from.cellPhone ? new PhoneNumber(from.cellPhone.number, from.cellPhone.extension, from.cellPhone.phoneType, from.cellPhone.primaryContactNumber) : null;

		model.patientNote = from.patientNote;
		model.patientAlert = from.patientAlert;

		return model;
	}
}