import {MessageableType} from "./MessageableType";
import {MessageableLocalType} from "./MessageableLocalType";
import {MessageableMappingConfidence} from "./MessageableMappingConfidence";

export default class Messageable
{
	protected _id: string;
	protected _type: MessageableType;
	protected _name: string;
	protected _identificationName: string;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build new messageable
	 * @param id - id of the messageble
	 * @param type - type of the messageable
	 * @param name - name of the messageable. ex: "Smith, Jon".
	 * @param identificationName - a more descriptive name used to identify the messageable
	 */
	constructor(id: string, type: MessageableType, name: string, identificationName = null)
	{
		this._id = id;
		this._type = type;
		this._name = name;
		this._identificationName = identificationName;
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
	 * get the confidence level of the local mapping. This indicates how certain the mapping to the local entity is.
	 * For example if you match to a demogrpahic by HIN you would select MEDIUM. However if the mapping is based on a direct
	 * id match you would return HIGH. Certain features will be restricted depending on confidence.
	 * @return a promise that resolves to the confidence level of this messageable's mapping to a local entity.
	 */
	public async localMappingConfidenceLevel(): Promise<MessageableMappingConfidence>
	{
		return MessageableMappingConfidence.NONE;
	}

	/**
	 * a string explaining to the user why this messageable is at the current confidence level.
	 * An example might be, "Patient Chart Connection Active" for HIGH confidence.
	 * or "Patient chart unavailable please do X, Y, & Z to connect..."
	 * @return promise that resolves to an explanatory string.
	 */
	public async localMappingConfidenceExplanationString(): Promise<string>
	{
		return null;
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

	get identificationName(): string
	{
		return this._identificationName;
	}
}