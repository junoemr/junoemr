import {MessageableType} from "./MessageableType";

export default class Messageable
{
	protected _id: string;
	protected _type: MessageableType;
	protected _name: string;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build new messageable
	 * @param id - id of the messageble
	 * @param type - type of the messageable
	 * @param name - name of the messageable. ex: "Smith, Jon".
	 */
	constructor(id: string, type: MessageableType, name: string)
	{
		this._id = id;
		this._type = type;
		this._name = name;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get type(): MessageableType
	{
		return this._type;
	}

	get name(): string
	{
		return this._name;
	}
}