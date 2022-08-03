import {Moment} from "moment";

export default class ProviderSearchParams
{
	private _active: boolean;
	private _firstName: string;
	private _lastName: string;
	private _type: string;
	private _practitionerNo: string;
	private _siteId: number;
	private _page: number;
	private _perPage: number;

	get active(): boolean
	{
		return this._active;
	}

	set active(value: boolean)
	{
		this._active = value;
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

	get type(): string
	{
		return this._type;
	}

	set type(value: string)
	{
		this._type = value;
	}

	get practitionerNo(): string
	{
		return this._practitionerNo;
	}

	set practitionerNo(value: string)
	{
		this._practitionerNo = value;
	}

	get siteId(): number
	{
		return this._siteId;
	}

	set siteId(value: number)
	{
		this._siteId = value;
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
}