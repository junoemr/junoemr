import {Moment} from "moment";
import JunoFile from "../../documents/model/JunoFile";
import EFormInstanceService from "../service/EFormInstanceService";

export default class EFormInstance implements JunoFile
{
	protected _formId: string;
	protected _formInstanceId: string;
	protected _name: string;
	protected _statusMessage: string;
	protected _subject: string;
	protected _demographicNo: string;
	protected _updatedAt: Moment;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(formId: string, formInstanceId: string, name: string, statusMessage: string, subject: string, demographicNo: string, updatedAt: Moment)
	{
		this._formId = formId;
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

	public async getBase64Data(): Promise<string>
	{
		return await (new EFormInstanceService()).printEForm(this);
	}

	get type(): string
	{
		return "application/pdf";
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get formId(): string
	{
		return this._formId;
	}

	get formInstanceId(): string
	{
		return this._formInstanceId;
	}

	get name(): string
	{
		return `${this.subject} (${this.formName})`;
	}

	get formName(): string {
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