import {TicklerAttachmentType} from "./TicklerAttachmentType";
import ArgumentError from "../../error/ArgumentError";

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

	public getLinkUrl($state: any): string
	{
		switch (this.attachmentType)
		{
			case TicklerAttachmentType.Cml: return "../lab/CA/ON/CMLDisplay.jsp?segmentID=" + this.attachmentId;
			case TicklerAttachmentType.Mds: return "../oscarMDS/SegmentDisplay.jsp?segmentID=" + this.attachmentId;
			case TicklerAttachmentType.Hl7: return "../lab/CA/ALL/labDisplay.jsp?segmentID=" + this.attachmentId;
			case TicklerAttachmentType.Doc: return "../dms/ManageDocument.do?method=display&doc_no=" + this.attachmentId;
			case TicklerAttachmentType.Message:
			{
				const meta = JSON.parse(this.attachmentMeta);
				return $state.href("messaging.view.message", {
					messageId: this.attachmentId,
					backend: meta.messagingBackend,
					source: meta.source,
					group: meta.group,
				});
			}
			default: throw new ArgumentError("Invalid attachment type: " + this.attachmentType);
		}
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