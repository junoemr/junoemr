import AbstractConverter from "../../../conversion/AbstractConverter";
import {AttachmentDto} from "../../../../../generated";
import MhaAttachment from "../clinic/model/MhaAttachment";
import moment from "moment";

export default class AttachmentDtoToMhaAttachmentConverter extends AbstractConverter<AttachmentDto, MhaAttachment>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: AttachmentDto): MhaAttachment
	{
		return new MhaAttachment(from.id, from.name, from.mimeType, moment(from.createdAtDateTime));
	}

}