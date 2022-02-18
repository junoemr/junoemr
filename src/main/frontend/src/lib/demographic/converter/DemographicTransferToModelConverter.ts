import AbstractConverter from "../../conversion/AbstractConverter";
import Demographic from "../model/Demographic";
import {DemographicModel} from "../../../../generated";
import moment from "moment";
import AddressToModelConverter from "../../common/converter/AddressToModelConverter";
import {Sex} from "../model/Sex";
import {TitleType} from "../model/TitleType";
import {OfficialLanguageType} from "../model/OfficialLanguageType";
import {ElectronicMessagingConsentStatus} from "../ElectronicMessagingConsentStatus";
import ProviderModelToSimpleProviderConverter from "../../provider/converter/ProviderModelToSimpleProviderConverter";
import PhoneNumberToModelConverter from "../../common/converter/PhoneNumberToModelConverter";
import RosterDataTransferToModelConverter from "./RosterDataTransferToModelConverter";
import {Province} from "../../constants/Province";
import {CountryCode} from "../../constants/CountryCode";
import {USStateCode} from "../../constants/USStateCode";

export default class DemographicTransferToModelConverter extends AbstractConverter<DemographicModel, Demographic>
{
	convert(from: DemographicModel): Demographic
	{
		let model = new Demographic();

		model.id = from.id;
		model.firstName = from.firstName;
		model.middleName = from.middleName;
		model.lastName = from.lastName;
		model.alias = from.alias;
		model.title = from.title as any as TitleType;
		model.dateOfBirth = moment(from.dateOfBirth);
		model.sex = from.sex as any as Sex;
		model.chartNumber = from.chartNumber;
		model.healthNumber = from.healthNumber;
		model.healthNumberVersion = from.healthNumberVersion;

		model.healthNumberCountryCode = from.healthNumberCountryCode as any as CountryCode;
		if(model.healthNumberCountryCode === CountryCode.US)
		{
			model.healthNumberProvinceCode = from.healthNumberProvinceCode as any as USStateCode;
		}
		else
		{
			model.healthNumberProvinceCode = Province[from.healthNumberProvinceCode];
		}
		model.healthNumberEffectiveDate = moment(from.healthNumberEffectiveDate);
		model.healthNumberRenewDate = moment(from.healthNumberRenewDate);
		model.patientStatus = from.patientStatus;
		model.patientStatusDate = moment(from.patientStatusDate);
		model.dateJoined = moment(from.dateJoined);
		model.dateEnded = moment(from.dateEnded);
		model.lastUpdateDateTime = moment(from.lastUpdateDateTime);

		model.addressList = new AddressToModelConverter().convertList(from.addressList);
		model.email = from.email;
		const phoneNumberConverter = new PhoneNumberToModelConverter();
		model.homePhone = phoneNumberConverter.convert(from.homePhone);
		model.workPhone = phoneNumberConverter.convert(from.workPhone);
		model.cellPhone = phoneNumberConverter.convert(from.cellPhone);
		model.phoneComment = from.phoneComment;

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
		model.electronicMessagingConsentStatus = from.electronicMessagingConsentStatus as any as ElectronicMessagingConsentStatus;
		model.electronicMessagingConsentGivenAt = moment(from.electronicMessagingConsentGivenAt);
		model.electronicMessagingConsentRejectedAt = moment(from.electronicMessagingConsentGivenAt);

		const simpleProviderConverter = new ProviderModelToSimpleProviderConverter();
		model.mrpProvider = simpleProviderConverter.convert(from.mrpProvider);
		model.nurseProvider = simpleProviderConverter.convert(from.nurseProvider);
		model.midwifeProvider = simpleProviderConverter.convert(from.midwifeProvider);
		model.residentProvider = simpleProviderConverter.convert(from.residentProvider);
		model.familyDoctor = simpleProviderConverter.convert(from.familyDoctor);
		model.referralDoctor = simpleProviderConverter.convert(from.referralDoctor);

		if(from.rosterHistory && from.rosterHistory.length > 0)
		{
			const rosterConverter = new RosterDataTransferToModelConverter();
			// roster history is sorted with latest at the end of the list
			model.rosterData = rosterConverter.convert(from.rosterHistory[from.rosterHistory.length-1]);
		}

		return model;
	}
}