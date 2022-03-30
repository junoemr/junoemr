import {FaxAccountType} from "./FaxAccountType";
import {FaxAccountConnectionStatus} from "./FaxAccountConnectionStatus";

export default class FaxAccount
{
	private _id: number;
	private _accountType: FaxAccountType;
	private _accountLogin: string;
	private _password: string;
	private _accountEmail: string;
	private _enabled: boolean;
	private _enableInbound: boolean;
	private _enableOutbound: boolean;
	private _displayName: string;
	private _faxNumber: string;
	private _coverLetterOption: string;

	// transient properties
	private _connectionStatus: FaxAccountConnectionStatus;

	constructor(type: FaxAccountType)
	{
		this.accountType = type;
		this.enabled = true;
		this.enableInbound = false;
		this.enableOutbound = false;
		this.accountLogin = null;
		this.accountEmail = null;
		this.password = null;
		this.displayName = null;
		this.coverLetterOption = null;
		this.faxNumber = null;
		this.connectionStatus = FaxAccountConnectionStatus.Unknown;
	}

	get connectionStatusUnknown(): boolean
	{
		return this.connectionStatus === FaxAccountConnectionStatus.Unknown;
	}

	get connectionStatusSuccess(): boolean
	{
		return this.connectionStatus === FaxAccountConnectionStatus.Success;
	}

	get connectionStatusFailure(): boolean
	{
		return this.connectionStatus === FaxAccountConnectionStatus.Failure;
	}

	public equals(otherAccount: FaxAccount): boolean
	{
		if(!otherAccount)
		{
			return false;
		}
		return this.id === otherAccount.id;
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

	get accountType(): FaxAccountType
	{
		return this._accountType;
	}

	set accountType(value: FaxAccountType)
	{
		this._accountType = value;
	}

	get accountLogin(): string
	{
		return this._accountLogin;
	}

	set accountLogin(value: string)
	{
		this._accountLogin = value;
	}

	get password(): string
	{
		return this._password;
	}

	set password(value: string)
	{
		this._password = value;
	}

	get accountEmail(): string
	{
		return this._accountEmail;
	}

	set accountEmail(value: string)
	{
		this._accountEmail = value;
	}

	get enabled(): boolean
	{
		return this._enabled;
	}

	set enabled(value: boolean)
	{
		this._enabled = value;
	}

	get enableInbound(): boolean
	{
		return this._enableInbound;
	}

	set enableInbound(value: boolean)
	{
		this._enableInbound = value;
	}

	get enableOutbound(): boolean
	{
		return this._enableOutbound;
	}

	set enableOutbound(value: boolean)
	{
		this._enableOutbound = value;
	}

	get displayName(): string
	{
		return this._displayName;
	}

	set displayName(value: string)
	{
		this._displayName = value;
	}

	get faxNumber(): string
	{
		return this._faxNumber;
	}

	set faxNumber(value: string)
	{
		this._faxNumber = value;
	}

	get coverLetterOption(): string
	{
		return this._coverLetterOption;
	}

	set coverLetterOption(value: string)
	{
		this._coverLetterOption = value;
	}

	get connectionStatus(): FaxAccountConnectionStatus
	{
		return this._connectionStatus;
	}

	set connectionStatus(value: FaxAccountConnectionStatus)
	{
		this._connectionStatus = value;
	}
}