import Attachment from "../../../model/Attachment";
import MessagingServiceInterface from "../../../service/MessagingServiceInterface";

export default class MhaAttachment extends Attachment
{
	private _messagingService: MessagingServiceInterface;

	// ==========================================================================
	// Attachment Overrides
	// ==========================================================================

	/**
	 * Get the files data as base64 encoded string.
	 * If data is already in this object return it, else fetch from server.
	 */
	public async getBase64Data(): Promise<string>
	{
		if (this._base64Data)
		{
			return this._base64Data;
		}

		this._base64Data = await this._messagingService.downloadAttachmentData(this);
		return this._base64Data
	}

	// ==========================================================================
	// Setters
	// ==========================================================================

	set messagingService(value: MessagingServiceInterface)
	{
		this._messagingService = value;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get messagingService(): MessagingServiceInterface
	{
		return this._messagingService;
	}
}