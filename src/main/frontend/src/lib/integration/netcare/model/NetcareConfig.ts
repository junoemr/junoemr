
export default class NetcareConfig
{
	private _conformanceCode: string;
	private _launcherUrl: string;
	private _loginUrl: string;
	private _logoutUrl: string;


	get conformanceCode(): string
	{
		return this._conformanceCode;
	}

	set conformanceCode(value: string)
	{
		this._conformanceCode = value;
	}

	get launcherUrl(): string
	{
		return this._launcherUrl;
	}

	set launcherUrl(value: string)
	{
		this._launcherUrl = value;
	}

	get loginUrl(): string
	{
		return this._loginUrl;
	}

	set loginUrl(value: string)
	{
		this._loginUrl = value;
	}

	get logoutUrl(): string
	{
		return this._logoutUrl;
	}

	set logoutUrl(value: string)
	{
		this._logoutUrl = value;
	}
}