import AbstractConverter from "../../conversion/AbstractConverter";
import TicklerAttachment from "../model/TicklerAttachment";
import TicklerLinkDto from "../transfer/TicklerLinkDto";

export default class TicklerAttachmentToTicklerLinkDtoConverter extends AbstractConverter<TicklerAttachment, TicklerLinkDto>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: TicklerAttachment): TicklerLinkDto
	{
		return {
			ticklerNo: from.ticklerNo,
			type: from.attachmentType,
			linkId: from.attachmentId,
			jsonMeta: JSON.stringify(from.attachmentMeta),
		}
	}

}