import AbstractConverter from "../../conversion/AbstractConverter";
import {AttachmentDto} from "../../../../generated";
import Attachment from "../model/Attachment";
import moment from "moment";

export default class AttachmentDtoToAttachmentConverter extends AbstractConverter<AttachmentDto, Attachment>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public convert(from: AttachmentDto): Attachment
	{
		return new Attachment(from.id, from.name, from.mimeType, moment(from.createdAtDateTime));
	}

}