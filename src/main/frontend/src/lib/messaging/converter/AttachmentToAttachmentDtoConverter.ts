import AbstractConverter from "../../conversion/AbstractConverter";
import Attachment from "../model/Attachment";
import {AttachmentDto} from "../../../../generated";

export default class AttachmentToAttachmentDtoConverter extends AbstractConverter<Attachment, Promise<AttachmentDto>>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public async convert(from: Attachment): Promise<AttachmentDto>
	{
		return {
			id: from.id,
			name: from.name,
			mimeType: from.type,
			base64Data: await from.getBase64Data(),
			createdAtDateTime: from.createdAtDateTime.toDate(),
		};
	}

}