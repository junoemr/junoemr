import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicModel} from "../../../../generated";
import moment from "moment";
import AddressToModelConverter from "../../common/converter/AddressToModelConverter";
import PhoneNumber from "../../common/model/PhoneNumber";
import {Sex} from "../model/Sex";
import {TitleType} from "../model/TitleType";
import {OfficialLanguageType} from "../model/OfficialLanguageType";

export default class DemographicTransferToModelConverter extends AbstractConverter<DemographicModel, Demographic>
{
	convert(from: DemographicModel, args: any): Demographic
	{
		let model = new Demographic();

		model.id = from.id;
		model.firstName = from.firstName;
		model.lastName = from.lastName;
		model.alias = from.alias;
		model.title = from.title as any as TitleType;
		model.dateOfBirth = moment(from.dateOfBirth);
		model.sex = from.sex as any as Sex;
		model.chartNumber = from.chartNumber;
		model.healthNumber = from.healthNumber;
		model.healthNumberVersion = from.healthNumberVersion;
		model.healthNumberProvinceCode = from.healthNumberProvinceCode;
		model.healthNumberEffectiveDate = moment(from.healthNumberEffectiveDate);
		model.healthNumberRenewDate = moment(from.healthNumberRenewDate);
		model.patientStatus = from.patientStatus;
		model.patientStatusDate = moment(from.patientStatusDate);
		model.dateJoined = moment(from.dateJoined);
		model.dateEnded = moment(from.dateEnded);
		model.lastUpdateDateTime = moment(from.lastUpdateDateTime);

		model.addressList = new AddressToModelConverter().convertList(from.addressList);
		model.email = from.email;
		model.homePhone = from.homePhone ? new PhoneNumber(from.homePhone.number, from.homePhone.extension, from.homePhone.phoneType, from.homePhone.primaryContactNumber) : null;
		model.workPhone = from.workPhone ? new PhoneNumber(from.workPhone.number, from.workPhone.extension, from.workPhone.phoneType, from.workPhone.primaryContactNumber) : null;
		model.cellPhone = from.cellPhone ? new PhoneNumber(from.cellPhone.number, from.cellPhone.extension, from.cellPhone.phoneType, from.cellPhone.primaryContactNumber) : null;

		model.officialLanguage = from.officialLanguage as any as OfficialLanguageType;
		model.spokenLanguage = from.spokenLanguage;
		model.countryOfOrigin = from.countryOfOrigin;
		model.patientNote = from.patientNote;
		model.patientAlert = from.patientAlert;
		model.aboriginal = from.aboriginal;
		model.cytolNum = from.cytolNum;
		model.paperChartArchived = from.paperChartArchived;
		model.paperChartArchivedDate = moment(from.paperChartArchivedDate);
		model.usSigned = from.usSigned;
		model.privacyConsent = from.privacyConsent;
		model.informedConsent = from.informedConsent;
		model.securityQuestion1 = from.securityQuestion1;
		model.securityAnswer1 = from.securityAnswer1;
		model.rxInteractionWarningLevel = from.rxInteractionWarningLevel;

		return model;
	}
}