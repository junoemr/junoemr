import {TicklerAttachmentType} from "./TicklerAttachmentType";

export default class TicklerAttachment
{
	protected _ticklerNo: string;
	protected _attachmentType: TicklerAttachmentType;
	protected _attachmentId: string;
	protected _attachmentMeta: any;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	/**
	 * build a new tickler attachment
	 * @param attachmentType - the type of thing attached
	 * @param attachmentId - the id of the thing attached
	 * @param meta - [optional] attachment meta data hash
	 */
	constructor(attachmentType: TicklerAttachmentType, attachmentId: string, meta: any = null)
	{
		this._ticklerNo = null;
		this._attachmentType = attachmentType;
		this._attachmentId = attachmentId;
		this._attachmentMeta = meta;
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set ticklerNo(ticklerNo: string)
	{
		this._ticklerNo = ticklerNo;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get ticklerNo(): string
	{
		return this._ticklerNo;
	}

	get attachmentType(): TicklerAttachmentType
	{
		return this._attachmentType;
	}

	get attachmentId(): string
	{
		return this._attachmentId;
	}

	get attachmentMeta(): any
	{
		return this._attachmentMeta;
	}
}