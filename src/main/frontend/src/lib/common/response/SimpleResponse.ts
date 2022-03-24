export default class SimpleResponse<T>
{
	protected _body: T;

	constructor(body: T)
	{
		this._body = body;
	}

	get body(): T
	{
		return this._body;
	}
}