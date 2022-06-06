import {Moment} from "moment/moment";

export default class OlisSystemSettings
{
	private _startDateTime: Moment;
	private _lastRunDateTime: Moment;
	private _filterPatients: boolean;
	private _frequency: number;
	private _warnings: string[];
	private _vendorId: string;


	get startDateTime(): Moment
	{
		return this._startDateTime;
	}

	set startDateTime(value: Moment)
	{
		this._startDateTime = value;
	}

	get lastRunDateTime(): Moment
	{
		return this._lastRunDateTime;
	}

	set lastRunDateTime(value: Moment)
	{
		this._lastRunDateTime = value;
	}

	get filterPatients(): boolean
	{
		return this._filterPatients;
	}

	set filterPatients(value: boolean)
	{
		this._filterPatients = value;
	}

	get frequency(): number
	{
		return this._frequency;
	}

	set frequency(value: number)
	{
		this._frequency = value;
	}

	get warnings(): string[]
	{
		return this._warnings;
	}

	set warnings(value: string[])
	{
		this._warnings = value;
	}

	get vendorId(): string
	{
		return this._vendorId;
	}

	set vendorId(value: string)
	{
		this._vendorId = value;
	}
}