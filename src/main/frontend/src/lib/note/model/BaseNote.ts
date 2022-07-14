import {Moment} from "moment";
import SimpleProvider from "../../provider/model/SimpleProvider";

export abstract class BaseNote
{
	private readonly _id: string;

	private _noteText: string;
	private _revisionId: string;
	private _observationDate: Moment;
	private _provider: SimpleProvider;
	private _signingProvider: SimpleProvider;
	private _editors: SimpleProvider[];

	protected constructor(id: string)
	{
		this._id = id;
	}

	get id(): string
	{
		return this._id;
	}

	get noteText(): string
	{
		return this._noteText;
	}

	set noteText(value: string)
	{
		this._noteText = value;
	}

	get revisionId(): string
	{
		return this._revisionId;
	}

	set revisionId(value: string)
	{
		this._revisionId = value;
	}

	get observationDate(): Moment
	{
		return this._observationDate;
	}

	set observationDate(value: Moment)
	{
		this._observationDate = value;
	}

	get provider(): SimpleProvider
	{
		return this._provider;
	}

	set provider(value: SimpleProvider)
	{
		this._provider = value;
	}

	get signingProvider(): SimpleProvider
	{
		return this._signingProvider;
	}

	set signingProvider(value: SimpleProvider)
	{
		this._signingProvider = value;
	}

	get editors(): SimpleProvider[]
	{
		return this._editors;
	}

	set editors(value: SimpleProvider[])
	{
		this._editors = value;
	}
}