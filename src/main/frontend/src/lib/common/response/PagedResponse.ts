import SimpleResponse from "./SimpleResponse";

export default class PagedResponse<T> extends SimpleResponse<Array<T>>
{
	protected _page: number;
	protected _perPage: number;
	protected _total: number;

	constructor(body: T[], headers?)
	{
		super(body);
		if (headers)
		{
			this._page = headers.page;
			this._perPage = headers.perPage;
			this._total = headers.total;
		}
	}

	get body(): T[]
	{
		return this._body;
	}

	get page()
	{
		return this._page;
	}

	get perPage()
	{
		return this._perPage;
	}

	get total()
	{
		return this._total;
	}
}