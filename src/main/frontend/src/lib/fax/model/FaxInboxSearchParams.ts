import {Moment} from "moment";
import FaxAccount from "./FaxAccount";

export default class FaxInboxSearchParams
{
	private _faxAccount: FaxAccount;
	private _page: number;
	private _perPage: number;
	private _startDate: Moment;
	private _endDate: Moment;


	get faxAccount(): FaxAccount
	{
		return this._faxAccount;
	}

	set faxAccount(value: FaxAccount)
	{
		this._faxAccount = value;
	}

	get page(): number
	{
		return this._page;
	}

	set page(value: number)
	{
		this._page = value;
	}

	get perPage(): number
	{
		return this._perPage;
	}

	set perPage(value: number)
	{
		this._perPage = value;
	}

	get startDate(): moment.Moment
	{
		return this._startDate;
	}

	set startDate(value: moment.Moment)
	{
		this._startDate = value;
	}

	get endDate(): moment.Moment
	{
		return this._endDate;
	}

	set endDate(value: moment.Moment)
	{
		this._endDate = value;
	}
}