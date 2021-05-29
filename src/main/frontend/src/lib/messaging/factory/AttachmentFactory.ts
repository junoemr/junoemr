import Attachment from "../model/Attachment";
import moment from "moment";

export default class AttachmentFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static build(name: string, type: string, data: string)
	{
		return new Attachment(null, name, type, moment(), data);
	}
}