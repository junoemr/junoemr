import {MessageableType} from "./MessageableType";
import {MessageableLocalType} from "./MessageableLocalType";

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

	/**
	 * if this messageable can be mapped to a local entity (provider or demographic).
	 * @return promise - that resolves to true / false
	 */
	public async hasLocalMapping(): Promise<boolean>
	{
		return false;
	}

	/**
	 * local entity id. i.e. (provider or demographic id).
	 * @return promise that resolves to local entity id or null if not available
	 */
	public async localId(): Promise<string>
	{
		return null;
	}

	/**
	 * local entity type.
	 * @return promise that resolves to the local entity type. Will be NONE if no local mapping
	 */
	public async localType(): Promise<MessageableLocalType>
	{
		return MessageableLocalType.NONE;
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