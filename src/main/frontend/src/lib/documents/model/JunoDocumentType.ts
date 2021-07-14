import {JunoDocumentTypeStatus} from "./JunoDocumentTypeStatus";
import {JunoDocumentTypeModule} from "./JunoDocumentTypeModule";

export default class JunoDocumentType
{
	protected _id: string;
	protected _module: JunoDocumentTypeModule;
	protected _type: string;
	protected _status: JunoDocumentTypeStatus;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, module: JunoDocumentTypeModule, type: string, status: JunoDocumentTypeStatus)
	{
		this._id = id;
		this._module = module;
		this._type = type;
		this._status = status;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get module(): JunoDocumentTypeModule
	{
		return this._module;
	}

	get type(): string
	{
		return this._type;
	}

	get status(): JunoDocumentTypeStatus
	{
		return this._status;
	}

	get isActive(): boolean
	{
		return this._status === JunoDocumentTypeStatus.Active;
	}

	get isInactive(): boolean
	{
		return this._status === JunoDocumentTypeStatus.Inactive;
	}
}