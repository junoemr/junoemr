import {MessageSourceType} from "./MessageSourceType";

export default class MessageSource
{
	protected _id: string;
	protected _name: string;
	protected _type: MessageSourceType;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * a source of messages.
	 * @param id - the id of this source.
	 * @param name - the name of the source
	 * @param type - [optional] source type. meaning is implementation specific.
	 */
	constructor(id: string, name: string, type = MessageSourceType.PHYSICAL)
	{
		this._id = id;
		this._name = name;
		this._type = type;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get name(): string
	{
		return this._name;
	}

	get type(): MessageSourceType
	{
		return this._type;
	}

	get isVirtual(): boolean
	{
		return this._type === MessageSourceType.VIRTUAL;
	}
}