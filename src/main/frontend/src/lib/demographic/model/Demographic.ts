import moment, {Moment} from "moment";
import Address from "../../common/model/Address";
import PhoneNumber from "../../common/model/PhoneNumber";
import {Sex} from "./Sex";

export default class Demographic
{
	private _id: number;

	// base info
	private _firstName: string;
	private _lastName: string;
	private _title: string;
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

	// physician info
	private _mrpProvider: object; //todo
	private _referralDoctor: object; //todo
	private _familyDoctor: object; //todo

	// roster info
	private _rosterHistory: object[]; //todo

	// other info
	private _lastUpdateProviderId: string;
	private _lastUpdateDateTime: Moment;

	private _alias: string;
	private _citizenship: string;
	private _spokenLanguage: string;
	private _officialLanguage: string;
	private _countryOfOrigin: string;
	private _newsletter: string;
	private _nameOfMother: string;
	private _nameOfFather: string;
	private _veteranNumber: string;
	private _patientNote: string;
	private _patientAlert: string;

	constructor()
	{

	}

	/**
	 * functions & helpers
	 */

	get primaryPhone(): PhoneNumber
	{
		if(this.homePhone && this.homePhone.primaryContactNumber)
		{
			return this.homePhone;
		}
		if(this.workPhone && this.workPhone.primaryContactNumber)
		{
			return this.workPhone;
		}
		if(this.cellPhone && this.cellPhone.primaryContactNumber)
		{
			return this.cellPhone;
		}
		return null;
	}

	get displayName(): string
	{
		return this.lastName + ', ' + this.firstName;
	}

	get displayDateOfBirth(): string
	{
		return Juno.Common.Util.formatMomentDate(this.dateOfBirth);
	}

	get age(): number
	{
		let currDate = moment();
		return currDate.diff(this.dateOfBirth, 'years');
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

	get lastName(): string
	{
		return this._lastName;
	}

	set lastName(value: string)
	{
		this._lastName = value;
	}

	get title(): string
	{
		return this._title;
	}

	set title(value: string)
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

	get mrpProvider(): object
	{
		return this._mrpProvider;
	}

	set mrpProvider(value: object)
	{
		this._mrpProvider = value;
	}

	get referralDoctor(): object
	{
		return this._referralDoctor;
	}

	set referralDoctor(value: object)
	{
		this._referralDoctor = value;
	}

	get familyDoctor(): object
	{
		return this._familyDoctor;
	}

	set familyDoctor(value: object)
	{
		this._familyDoctor = value;
	}

	get rosterHistory(): object[]
	{
		return this._rosterHistory;
	}

	set rosterHistory(value: object[])
	{
		this._rosterHistory = value;
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

	get officialLanguage(): string
	{
		return this._officialLanguage;
	}

	set officialLanguage(value: string)
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

	get newsletter(): string
	{
		return this._newsletter;
	}

	set newsletter(value: string)
	{
		this._newsletter = value;
	}

	get nameOfMother(): string
	{
		return this._nameOfMother;
	}

	set nameOfMother(value: string)
	{
		this._nameOfMother = value;
	}

	get nameOfFather(): string
	{
		return this._nameOfFather;
	}

	set nameOfFather(value: string)
	{
		this._nameOfFather = value;
	}

	get veteranNumber(): string
	{
		return this._veteranNumber;
	}

	set veteranNumber(value: string)
	{
		this._veteranNumber = value;
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
}