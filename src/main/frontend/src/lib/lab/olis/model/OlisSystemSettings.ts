import {Moment} from "moment/moment";

export default class OlisSystemSettings
{
	private _startDateTime: Moment;
	private _filterPatients: boolean;
	private _frequency: number;
	private _warnings: string[];


	get startDateTime(): Moment
	{
		return this._startDateTime;
	}

	set startDateTime(value: Moment)
	{
		this._startDateTime = value;
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
}