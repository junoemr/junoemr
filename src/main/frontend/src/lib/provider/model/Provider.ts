/**
 * a representation of a provider record
 */
import {JunoSelectOption} from "../../common/junoSelectOption";
import {Sex} from "../../demographic/model/Sex";
import {Moment} from "moment";
import PhoneNumber from "../../common/model/PhoneNumber";
import Address from "../../common/model/Address";
import {ProviderTitleType} from "./ProviderTitleType";

export default class Provider implements JunoSelectOption
{
	private readonly _id: string;
	private _firstName: string;
	private _lastName: string;
	private _ohipNumber: string;

	private _providerType: string;
	private _sex: Sex;
	private _dateOfBirth: Moment;
	private _title: ProviderTitleType;
	private _email: string;
	private _homePhone: PhoneNumber;
	private _workPhone: PhoneNumber;
	private _cellPhone: PhoneNumber;
	private _address: Address;
	private _active: boolean;
	private _specialty: string;
	private _team: string;
	private _rmaNumber: string;
	private _billingNumber: string;
	private _hsoNumber: string;
	private _practitionerNumber: string;
	private _jobTitle: string;
	private readonly _lastUpdateUserId: string;
	private readonly _lastUpdateDateTime: Moment;
	private _signedConfidentialityDateTime: Moment;
	private _supervisor: string;

	constructor(id: string = null, lastUpdateUserId: string = null, lastUpdateDateTime: Moment = null)
	{
		this._id = id;
		this._lastUpdateUserId = lastUpdateUserId;
		this._lastUpdateDateTime = lastUpdateDateTime;
	}

	get displayName(): string
	{
		return this.lastName + ', ' + this.firstName;
	}

	/**
	 * getters and setters
	 */

	get id(): string
	{
		return this._id;
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

	get ohipNumber(): string
	{
		return this._ohipNumber;
	}

	set ohipNumber(value: string)
	{
		this._ohipNumber = value;
	}

	get providerType(): string
	{
		return this._providerType;
	}

	set providerType(value: string)
	{
		this._providerType = value;
	}

	get sex(): Sex
	{
		return this._sex;
	}

	set sex(value: Sex)
	{
		this._sex = value;
	}

	get dateOfBirth(): Moment
	{
		return this._dateOfBirth;
	}

	set dateOfBirth(value: Moment)
	{
		this._dateOfBirth = value;
	}

	get title(): ProviderTitleType
	{
		return this._title;
	}

	set title(value: ProviderTitleType)
	{
		this._title = value;
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

	get address(): Address
	{
		return this._address;
	}

	set address(value: Address)
	{
		this._address = value;
	}

	get active(): boolean
	{
		return this._active;
	}

	set active(value: boolean)
	{
		this._active = value;
	}

	get specialty(): string
	{
		return this._specialty;
	}

	set specialty(value: string)
	{
		this._specialty = value;
	}

	get team(): string
	{
		return this._team;
	}

	set team(value: string)
	{
		this._team = value;
	}

	get rmaNumber(): string
	{
		return this._rmaNumber;
	}

	set rmaNumber(value: string)
	{
		this._rmaNumber = value;
	}

	get billingNumber(): string
	{
		return this._billingNumber;
	}

	set billingNumber(value: string)
	{
		this._billingNumber = value;
	}

	get hsoNumber(): string
	{
		return this._hsoNumber;
	}

	set hsoNumber(value: string)
	{
		this._hsoNumber = value;
	}

	get practitionerNumber(): string
	{
		return this._practitionerNumber;
	}

	set practitionerNumber(value: string)
	{
		this._practitionerNumber = value;
	}

	get jobTitle(): string
	{
		return this._jobTitle;
	}

	set jobTitle(value: string)
	{
		this._jobTitle = value;
	}

	get lastUpdateUserId(): string
	{
		return this._lastUpdateUserId;
	}

	get lastUpdateDateTime(): Moment
	{
		return this._lastUpdateDateTime;
	}

	get signedConfidentialityDateTime(): Moment
	{
		return this._signedConfidentialityDateTime;
	}

	set signedConfidentialityDateTime(value: Moment)
	{
		this._signedConfidentialityDateTime = value;
	}

	get supervisor(): string
	{
		return this._supervisor;
	}

	set supervisor(value: string)
	{
		this._supervisor = value;
	}

	// ========= JunoSelectOption interface methods ============

	get value(): any
	{
		return this.id;
	}

	get label(): string
	{
		return `${this.displayName} (${this.id})`;
	}

	get data(): any
	{
		return this;
	}
}