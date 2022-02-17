import moment, {Moment} from "moment";
import Address from "../../common/model/Address";
import PhoneNumber from "../../common/model/PhoneNumber";
import {Sex, sexToHuman} from "./Sex";
import {ElectronicMessagingConsentStatus} from "../ElectronicMessagingConsentStatus";
import {TitleType} from "./TitleType";
import {OfficialLanguageType} from "./OfficialLanguageType";
import SimpleProvider from "../../provider/model/SimpleProvider";
import RosterStatusData from "./RosterStatusData";
import {PhoneType} from "../../common/model/PhoneType";
import DemographicWaitingList from "../../waitingList/model/DemographicWaitingList";
import {AddressResidencyStatus} from "../../common/model/AddressResidencyStatus";

export default class Demographic
{
	private _id: number;

	// base info
	private _firstName: string;
	private _middleName: string;
	private _lastName: string;
	private _title: TitleType;
	private _dateOfBirth: Moment;
	private _sex: Sex;
	private _healthNumber: string;
	private _healthNumberVersion: string;
	private _healthNumberProvinceCode: string;
	private _healthNumberCountryCode: string;
	private _healthNumberEffectiveDate: Moment;
	private _healthNumberRenewDate: Moment;
	private _chartNumber: string;
	private _sin: string;
	private _patientStatus: string;
	private _patientStatusDate: Moment;
	private _dateJoined: Moment;
	private _dateEnded: Moment;

	//contact info
	private _addressList: Address[];
	private _email: string;
	private _homePhone: PhoneNumber;
	private _workPhone: PhoneNumber;
	private _cellPhone: PhoneNumber;
	private _phoneComment: string;

	// physician info
	private _mrpProvider: SimpleProvider;
	private _nurseProvider: SimpleProvider;
	private _midwifeProvider: SimpleProvider;
	private _residentProvider: SimpleProvider;
	private _referralDoctor: SimpleProvider;
	private _familyDoctor: SimpleProvider;

	// roster info
	private _rosterData: RosterStatusData;

	// other info
	private _lastUpdateProviderId: string;
	private _lastUpdateDateTime: Moment;

	private _alias: string;
	private _citizenship: string;
	private _spokenLanguage: string;
	private _officialLanguage: OfficialLanguageType;
	private _countryOfOrigin: string;
	private _patientNote: string;
	private _patientAlert: string;

	private _electronicMessagingConsentStatus: ElectronicMessagingConsentStatus;
	private _electronicMessagingConsentGivenAt: Moment;
	private _electronicMessagingConsentRejectedAt: Moment;

	private _aboriginal: boolean;
	private _cytolNum: string;
	private _paperChartArchived: boolean;
	private _paperChartArchivedDate: Moment;
	private _usSigned: string;
	private _privacyConsent: string;
	private _informedConsent: string;
	private _securityQuestion1: string;
	private _securityAnswer1: string;
	private _rxInteractionWarningLevel: string;

	private _waitList: DemographicWaitingList;

	constructor()
	{
		this.addressList = [
			new Address(AddressResidencyStatus.Current),
		];
	}

	public setPrimaryPhone(phoneNumber: PhoneNumber): void
	{
		this.clearPrimaryPhoneType();
		phoneNumber.primaryContactNumber = true;
		switch (phoneNumber.phoneType)
		{
			case PhoneType.Home: this.homePhone = phoneNumber; break;
			case PhoneType.Work: this.workPhone = phoneNumber; break;
			case PhoneType.Cell: this.cellPhone = phoneNumber; break;
		}
	}
	public setPrimaryPhoneType(type: PhoneType): void
	{
		this.clearPrimaryPhoneType();
		switch (type)
		{
			case PhoneType.Home: this.homePhone.primaryContactNumber = true; break;
			case PhoneType.Work: this.workPhone.primaryContactNumber = true; break;
			case PhoneType.Cell: this.cellPhone.primaryContactNumber = true; break;
		}
	}
	public clearPrimaryPhoneType(): void
	{
		if(this.homePhone)
		{
			this.homePhone.primaryContactNumber = false;
		}
		if(this.workPhone)
		{
			this.workPhone.primaryContactNumber = false;
		}
		if(this.cellPhone)
		{
			this.cellPhone.primaryContactNumber = false;
		}
	}

	/**
	 * display and formatting helpers
	 */

	get primaryPhone(): PhoneNumber
	{
		// prioritize the marked primary contact
		if(this.cellPhone && this.cellPhone.primaryContactNumber)
		{
			return this.cellPhone;
		}
		if(this.homePhone && this.homePhone.primaryContactNumber)
		{
			return this.homePhone;
		}
		if(this.workPhone && this.workPhone.primaryContactNumber)
		{
			return this.workPhone;
		}
		// prioritize by existence
		if(this.cellPhone)
		{
			return this.cellPhone;
		}
		if(this.homePhone)
		{
			return this.homePhone;
		}
		if(this.workPhone)
		{
			return this.workPhone;
		}
		return null;
	}

	get primaryPhoneType(): PhoneType
	{
		return this.primaryPhone?.phoneType;
	}

	get displayName(): string
	{
		return this.lastName + ', ' + this.firstName;
	}

	get displayDateOfBirth(): string
	{
		return Juno.Common.Util.formatMomentDate(this.dateOfBirth);
	}

	get displaySex(): string
	{
		return sexToHuman(this.sex);
	}

	get displayAge(): string
	{
		if(isNaN(this.age))
		{
			return "NA";
		}
		return String(this.age);
	}

	get age(): number
	{
		let currDate = moment();
		return currDate.diff(this.dateOfBirth, 'years');
	}

	// helper function for places where only a single address is used/supported
	get address(): Address
	{
		if(this.addressList && this.addressList.length > 0)
		{
			return this.addressList[0];
		}
		return null;
	}

	// helper function for places where a fixed 2nd address is used/supported
	get address2(): Address
	{
		if(this.addressList && this.addressList.length > 1)
		{
			return this.addressList[1];
		}
		return null;
	}

	/**
	 * getters and setters
	 */

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get firstName(): string
	{
		return this._firstName;
	}

	set firstName(value: string)
	{
		this._firstName = value;
	}

	get middleName(): string
	{
		return this._middleName;
	}

	set middleName(value: string)
	{
		this._middleName = value;
	}

	get lastName(): string
	{
		return this._lastName;
	}

	set lastName(value: string)
	{
		this._lastName = value;
	}

	get title(): TitleType
	{
		return this._title;
	}

	set title(value: TitleType)
	{
		this._title = value;
	}

	get dateOfBirth(): Moment
	{
		return this._dateOfBirth;
	}

	set dateOfBirth(value: Moment)
	{
		this._dateOfBirth = value;
	}

	get sex(): Sex
	{
		return this._sex;
	}

	set sex(value: Sex)
	{
		this._sex = value;
	}

	get healthNumber(): string
	{
		return this._healthNumber;
	}

	set healthNumber(value: string)
	{
		this._healthNumber = value;
	}

	get healthNumberVersion(): string
	{
		return this._healthNumberVersion;
	}

	set healthNumberVersion(value: string)
	{
		this._healthNumberVersion = value;
	}

	get healthNumberProvinceCode(): string
	{
		return this._healthNumberProvinceCode;
	}

	set healthNumberProvinceCode(value: string)
	{
		this._healthNumberProvinceCode = value;
	}

	get healthNumberCountryCode(): string
	{
		return this._healthNumberCountryCode;
	}

	set healthNumberCountryCode(value: string)
	{
		this._healthNumberCountryCode = value;
	}

	get healthNumberEffectiveDate(): Moment
	{
		return this._healthNumberEffectiveDate;
	}

	set healthNumberEffectiveDate(value: Moment)
	{
		this._healthNumberEffectiveDate = value;
	}

	get healthNumberRenewDate(): Moment
	{
		return this._healthNumberRenewDate;
	}

	set healthNumberRenewDate(value: Moment)
	{
		this._healthNumberRenewDate = value;
	}

	get chartNumber(): string
	{
		return this._chartNumber;
	}

	set chartNumber(value: string)
	{
		this._chartNumber = value;
	}

	get sin(): string
	{
		return this._sin;
	}

	set sin(value: string)
	{
		this._sin = value;
	}

	get patientStatus(): string
	{
		return this._patientStatus;
	}

	set patientStatus(value: string)
	{
		this._patientStatus = value;
	}

	get patientStatusDate(): Moment
	{
		return this._patientStatusDate;
	}

	set patientStatusDate(value: Moment)
	{
		this._patientStatusDate = value;
	}

	get dateJoined(): Moment
	{
		return this._dateJoined;
	}

	set dateJoined(value: Moment)
	{
		this._dateJoined = value;
	}

	get dateEnded(): Moment
	{
		return this._dateEnded;
	}

	set dateEnded(value: Moment)
	{
		this._dateEnded = value;
	}

	get addressList(): Address[]
	{
		return this._addressList;
	}

	set addressList(value: Address[])
	{
		this._addressList = value;
	}

	get email(): string
	{
		return this._email;
	}

	set email(value: string)
	{
		this._email = value;
	}

	get homePhone(): PhoneNumber
	{
		return this._homePhone;
	}

	set homePhone(value: PhoneNumber)
	{
		this._homePhone = value;
	}

	get workPhone(): PhoneNumber
	{
		return this._workPhone;
	}

	set workPhone(value: PhoneNumber)
	{
		this._workPhone = value;
	}

	get cellPhone(): PhoneNumber
	{
		return this._cellPhone;
	}

	set cellPhone(value: PhoneNumber)
	{
		this._cellPhone = value;
	}

	get phoneComment(): string
	{
		return this._phoneComment;
	}

	set phoneComment(value: string)
	{
		this._phoneComment = value;
	}

	get mrpProvider(): SimpleProvider
	{
		return this._mrpProvider;
	}

	set mrpProvider(value: SimpleProvider)
	{
		this._mrpProvider = value;
	}

	get referralDoctor(): SimpleProvider
	{
		return this._referralDoctor;
	}

	set referralDoctor(value: SimpleProvider)
	{
		this._referralDoctor = value;
	}

	get familyDoctor(): SimpleProvider
	{
		return this._familyDoctor;
	}

	set familyDoctor(value: SimpleProvider)
	{
		this._familyDoctor = value;
	}

	get nurseProvider(): SimpleProvider
	{
		return this._nurseProvider;
	}

	set nurseProvider(value: SimpleProvider)
	{
		this._nurseProvider = value;
	}

	get midwifeProvider(): SimpleProvider
	{
		return this._midwifeProvider;
	}

	set midwifeProvider(value: SimpleProvider)
	{
		this._midwifeProvider = value;
	}

	get residentProvider(): SimpleProvider
	{
		return this._residentProvider;
	}

	set residentProvider(value: SimpleProvider)
	{
		this._residentProvider = value;
	}

	get rosterData(): RosterStatusData
	{
		return this._rosterData;
	}

	set rosterData(value: RosterStatusData)
	{
		this._rosterData = value;
	}

	get lastUpdateProviderId(): string
	{
		return this._lastUpdateProviderId;
	}

	set lastUpdateProviderId(value: string)
	{
		this._lastUpdateProviderId = value;
	}

	get lastUpdateDateTime(): Moment
	{
		return this._lastUpdateDateTime;
	}

	set lastUpdateDateTime(value: Moment)
	{
		this._lastUpdateDateTime = value;
	}

	get alias(): string
	{
		return this._alias;
	}

	set alias(value: string)
	{
		this._alias = value;
	}

	get citizenship(): string
	{
		return this._citizenship;
	}

	set citizenship(value: string)
	{
		this._citizenship = value;
	}

	get spokenLanguage(): string
	{
		return this._spokenLanguage;
	}

	set spokenLanguage(value: string)
	{
		this._spokenLanguage = value;
	}

	get officialLanguage(): OfficialLanguageType
	{
		return this._officialLanguage;
	}

	set officialLanguage(value: OfficialLanguageType)
	{
		this._officialLanguage = value;
	}

	get countryOfOrigin(): string
	{
		return this._countryOfOrigin;
	}

	set countryOfOrigin(value: string)
	{
		this._countryOfOrigin = value;
	}

	get patientNote(): string
	{
		return this._patientNote;
	}

	set patientNote(value: string)
	{
		this._patientNote = value;
	}

	get patientAlert(): string
	{
		return this._patientAlert;
	}

	set patientAlert(value: string)
	{
		this._patientAlert = value;
	}

	get electronicMessagingConsentStatus(): ElectronicMessagingConsentStatus
	{
		return this._electronicMessagingConsentStatus;
	}

	set electronicMessagingConsentStatus(value: ElectronicMessagingConsentStatus)
	{
		this._electronicMessagingConsentStatus = value;
	}

	get electronicMessagingConsentGivenAt(): moment.Moment
	{
		return this._electronicMessagingConsentGivenAt;
	}

	set electronicMessagingConsentGivenAt(value: moment.Moment)
	{
		this._electronicMessagingConsentGivenAt = value;
	}

	get electronicMessagingConsentRejectedAt(): moment.Moment
	{
		return this._electronicMessagingConsentRejectedAt;
	}

	set electronicMessagingConsentRejectedAt(value: moment.Moment)
	{
		this._electronicMessagingConsentRejectedAt = value;
	}

	get aboriginal(): boolean
	{
		return this._aboriginal;
	}

	set aboriginal(value: boolean)
	{
		this._aboriginal = value;
	}

	get cytolNum(): string
	{
		return this._cytolNum;
	}

	set cytolNum(value: string)
	{
		this._cytolNum = value;
	}

	get paperChartArchived(): boolean
	{
		return this._paperChartArchived;
	}

	set paperChartArchived(value: boolean)
	{
		this._paperChartArchived = value;
	}

	get paperChartArchivedDate(): moment.Moment
	{
		return this._paperChartArchivedDate;
	}

	set paperChartArchivedDate(value: moment.Moment)
	{
		this._paperChartArchivedDate = value;
	}

	get usSigned(): string
	{
		return this._usSigned;
	}

	set usSigned(value: string)
	{
		this._usSigned = value;
	}

	get privacyConsent(): string
	{
		return this._privacyConsent;
	}

	set privacyConsent(value: string)
	{
		this._privacyConsent = value;
	}

	get informedConsent(): string
	{
		return this._informedConsent;
	}

	set informedConsent(value: string)
	{
		this._informedConsent = value;
	}

	get securityQuestion1(): string
	{
		return this._securityQuestion1;
	}

	set securityQuestion1(value: string)
	{
		this._securityQuestion1 = value;
	}

	get securityAnswer1(): string
	{
		return this._securityAnswer1;
	}

	set securityAnswer1(value: string)
	{
		this._securityAnswer1 = value;
	}

	get rxInteractionWarningLevel(): string
	{
		return this._rxInteractionWarningLevel;
	}

	set rxInteractionWarningLevel(value: string)
	{
		this._rxInteractionWarningLevel = value;
	}

	get waitList(): DemographicWaitingList
	{
		return this._waitList;
	}

	set waitList(value: DemographicWaitingList)
	{
		this._waitList = value;
	}
}