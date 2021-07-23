import {Moment} from "moment";
import JunoFile from "../../documents/model/JunoFile";

export default class EFormInstance implements JunoFile
{
	protected _id: string;
	protected _formInstanceId: string;
	protected _name: string;
	protected _statusMessage: string;
	protected _subject: string;
	protected _demographicNo: string;
	protected _updatedAt: Moment;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, formInstanceId: string, name: string, statusMessage: string, subject: string, demographicNo: string, updatedAt: Moment)
	{
		this._id = id;
		this._formInstanceId = formInstanceId;
		this._name = name;
		this._statusMessage = statusMessage;
		this._subject = subject;
		this._demographicNo = demographicNo;
		this._updatedAt = updatedAt;
	}

	// ==========================================================================
	// JunoFile Implementation
	// ==========================================================================

	public getBase64Data(): Promise<string>
	{
		return Promise.resolve("");
	}

	get type(): string
	{
		return "application/pdf";
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get formInstanceId(): string
	{
		return this._formInstanceId;
	}

	get name(): string
	{
		return this._name;
	}

	get statusMessage(): string
	{
		return this._statusMessage;
	}

	get subject(): string
	{
		return this._subject;
	}

	get demographicNo(): string
	{
		return this._demographicNo;
	}

	get updatedAt(): Moment
	{
		return this._updatedAt;
	}

	get createdAt(): Moment
	{
		// eForm transfer currently doesn't contain created at.
		return this._updatedAt;
	}
}