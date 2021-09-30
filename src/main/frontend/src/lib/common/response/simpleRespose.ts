export default class SimpleResponse<T>
{
	protected _body: T;

	constructor(body)
	{
		this._body = body;
	}

	get body()
	{
		return this._body;
	}
}