import AbstractConverter from "../../conversion/AbstractConverter";
import Attachment from "../model/Attachment";
import {AttachmentDto} from "../../../../generated";

export default class AttachmentToAttachmentDtoConverter extends AbstractConverter<Attachment, Promise<AttachmentDto>>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public async convert(from: Attachment, includeAttachmentData= false): Promise<AttachmentDto>
	{
		return {
			id: from.id,
			name: from.name,
			mimeType: from.type,
			base64Data: includeAttachmentData ? await from.getBase64Data() : null,
			createdAtDateTime: from.createdAtDateTime.toDate(),
		};
	}

}