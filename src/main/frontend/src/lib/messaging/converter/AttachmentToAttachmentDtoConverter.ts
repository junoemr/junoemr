import AbstractConverter from "../../conversion/AbstractConverter";
import Attachment from "../model/Attachment";
import {AttachmentDto} from "../../../../generated";

export default class AttachmentToAttachmentDtoConverter extends AbstractConverter<Attachment, AttachmentDto>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: Attachment): AttachmentDto
	{
		return {
			id: from.id,
			name: from.name,
			mimeType: from.type,
			createdAtDateTime: from.createdAtDateTime.toDate(),
		};
	}

}