import {DocumentStatus} from "./DocumentStatus";
import {Moment} from "moment";
import JunoFile from "./JunoFile";
import DocumentService from "../service/DocumentService";

export default class JunoDocument implements JunoFile
{
	protected _documentNo: string;
	protected _fileName: string;
	protected _status: DocumentStatus;
	protected _documentDescription: string;
	protected _base64Data: string;
	private _contentType: string;
	protected _documentXml: string;
	protected _publicDocument: boolean;
	protected _documentType: string;
	protected _documentClass: string;
	protected _documentSubClass: string;
	protected _documentSource: string;
	protected _documentSourceFacility: string;

	protected _documentCreatorProviderNo: string;
	protected _responsibleProviderNo: string;
	protected _reviewerProviderNo: string;

	protected _appointmentNo: string;

	protected _reviewedAt: Moment;
	protected _observedAt: Moment;
	protected _createdAt: Moment;
	private _updatedAt: Moment;

	// ==========================================================================
	// Public
	// ==========================================================================

	constructor(fileName: string, description: string, base64Data: string = null)
	{
		this.fileName = fileName;
		this.documentDescription = description;
		this.base64Data = base64Data;
	}

	public async getBase64Data(): Promise<string>
	{
		if (!this._base64Data)
		{
			// if we do not have the file data load it from the server.
			this._base64Data = await (await (new DocumentService()).getDocument(this.documentNo)).getBase64Data();
		}

		return this._base64Data;
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set fileName(value: string)
	{
		this._fileName = value;
	}

	set documentNo(value: string)
	{
		this._documentNo = value;
	}

	set status(value: DocumentStatus)
	{
		this._status = value;
	}

	set documentDescription(value: string)
	{
		this._documentDescription = value;
	}

	set base64Data(value: string)
	{
		this._base64Data = value;
	}

	set documentXml(value: string)
	{
		this._documentXml = value;
	}

	set publicDocument(value: boolean)
	{
		this._publicDocument = value;
	}

	set documentType(value: string)
	{
		this._documentType = value;
	}

	set documentClass(value: string)
	{
		this._documentClass = value;
	}

	set documentSubClass(value: string)
	{
		this._documentSubClass = value;
	}

	set documentSource(value: string)
	{
		this._documentSource = value;
	}

	set documentSourceFacility(value: string)
	{
		this._documentSourceFacility = value;
	}

	set documentCreatorProviderNo(value: string)
	{
		this._documentCreatorProviderNo = value;
	}

	set responsibleProviderNo(value: string)
	{
		this._responsibleProviderNo = value;
	}

	set reviewerProviderNo(value: string)
	{
		this._reviewerProviderNo = value;
	}

	set appointmentNo(value: string)
	{
		this._appointmentNo = value;
	}

	set reviewedAt(value: Moment)
	{
		this._reviewedAt = value;
	}

	set observedAt(value: Moment)
	{
		this._observedAt = value;
	}

	set createdAt(value: Moment)
	{
		this._createdAt = value;
	}

	set updatedAt(value: Moment)
	{
		this._updatedAt = value;
	}

	set contentType(value: string)
	{
		this._contentType = value;
	}

// ==========================================================================
	// Getters
	// ==========================================================================

	get fileName(): string
	{
		return this._fileName;
	}

	get name(): string
	{
		// if already looks like file name
		if (this.documentDescription?.match(/.*\.[\w]+$/))
		{
			return this.documentDescription;
		}
		return `${this.documentDescription}${this.fileName.replace(/.*(\.[\w]+)$/g, "$1")}`;
	}

	get documentNo(): string
	{
		return this._documentNo;
	}

	get status(): DocumentStatus
	{
		return this._status;
	}

	get documentDescription(): string
	{
		return this._documentDescription;
	}

	get documentXml(): string
	{
		return this._documentXml;
	}

	get publicDocument(): boolean
	{
		return this._publicDocument;
	}

	get documentType(): string
	{
		return this._documentType;
	}

	get documentClass(): string
	{
		return this._documentClass;
	}

	get documentSubClass(): string
	{
		return this._documentSubClass;
	}

	get documentSource(): string
	{
		return this._documentSource;
	}

	get documentSourceFacility(): string
	{
		return this._documentSourceFacility;
	}

	get documentCreatorProviderNo(): string
	{
		return this._documentCreatorProviderNo;
	}

	get responsibleProviderNo(): string
	{
		return this._responsibleProviderNo;
	}

	get reviewerProviderNo(): string
	{
		return this._reviewerProviderNo;
	}

	get appointmentNo(): string
	{
		return this._appointmentNo;
	}

	get reviewedAt(): Moment
	{
		return this._reviewedAt;
	}

	get observedAt(): Moment
	{
		return this._observedAt;
	}

	get createdAt(): Moment
	{
		return this._createdAt;
	}

	get updatedAt(): Moment
	{
		return this._updatedAt;
	}

	get contentType(): string
	{
		return this._contentType;
	}

	get type(): string
	{
		return this.contentType;
	}
}