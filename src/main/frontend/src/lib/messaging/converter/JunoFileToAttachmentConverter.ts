import AbstractConverter from "../../conversion/AbstractConverter";
import JunoFile from "../../documents/model/JunoFile";
import Attachment from "../model/Attachment";
import AttachmentFactory from "../factory/AttachmentFactory";

export default class JunoFileToAttachmentConverter extends AbstractConverter<JunoFile, Promise<Attachment>>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public async convert(from: JunoFile): Promise<Attachment>
	{
		return AttachmentFactory.build(from.name, from.type, await from.getBase64Data(), from.createdAt);
	}
}