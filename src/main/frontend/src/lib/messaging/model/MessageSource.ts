
export default class MessageSource
{
	protected _id: string;
	protected _name: string;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * a source of messages.
	 * @param id - the id of this source.
	 * @param name - the name of the source
	 */
	constructor(id: string, name: string)
	{
		this._id = id;
		this._name = name;
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
}