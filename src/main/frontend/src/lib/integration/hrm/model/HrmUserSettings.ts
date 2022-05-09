export default class HrmUserSettings
{
	private _userName: string;
	private _mailBoxAddress: string;
	private _remotePath: string;
	private _port: string;

	public constructor()
	{
		this._userName = null;
		this._mailBoxAddress = null;
		this._remotePath = null;
		this._port = null;
	}

	get userName(): string
	{
		return this._userName;
	}

	set userName(value: string)
	{
		this._userName = value;
	}

	get remotePath(): string
	{
		return this._remotePath;
	}

	set remotePath(value: string)
	{
		this._remotePath = value;
	}

	get port(): string
	{
		return this._port;
	}

	set port(value: string)
	{
		this._port = value;
	}

	get mailBoxAddress(): string
	{
		return this._mailBoxAddress;
	}

	set mailBoxAddress(value: string)
	{
		this._mailBoxAddress = value;
	}
}