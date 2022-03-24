import {Moment} from "moment";
import PhoneNumber from "../../common/model/PhoneNumber";
import FaxAccount from "./FaxAccount";

export default class FaxInboxResult
{
	private _id: number;
	private _faxAccount: FaxAccount;
	private _systemDateReceived: Moment;
	private _documentId: number;
	private _sentFrom: PhoneNumber;
	private _externalReferenceId: number;


	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get faxAccount(): FaxAccount
	{
		return this._faxAccount;
	}

	set faxAccount(value: FaxAccount)
	{
		this._faxAccount = value;
	}

	get systemDateReceived(): Moment
	{
		return this._systemDateReceived;
	}

	set systemDateReceived(value: Moment)
	{
		this._systemDateReceived = value;
	}

	get documentId(): number
	{
		return this._documentId;
	}

	set documentId(value: number)
	{
		this._documentId = value;
	}

	get sentFrom(): PhoneNumber
	{
		return this._sentFrom;
	}

	set sentFrom(value: PhoneNumber)
	{
		this._sentFrom = value;
	}

	get externalReferenceId(): number
	{
		return this._externalReferenceId;
	}

	set externalReferenceId(value: number)
	{
		this._externalReferenceId = value;
	}
}