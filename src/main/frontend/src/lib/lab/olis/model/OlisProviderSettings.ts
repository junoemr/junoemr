import {Moment} from "moment/moment";

export default class OlisProviderSettings
{
	private _startDateTime: Moment;
	private _isConfigured: boolean;
	private _provider: object;

	get startDateTime(): Moment
	{
		return this._startDateTime;
	}

	set startDateTime(value: Moment)
	{
		this._startDateTime = value;
	}

	get isConfigured(): boolean
	{
		return this._isConfigured;
	}

	set isConfigured(value: boolean)
	{
		this._isConfigured = value;
	}

	get provider(): object
	{
		return this._provider;
	}

	set provider(value: object)
	{
		this._provider = value;
	}
}