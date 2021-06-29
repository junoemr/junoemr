import Attachment from "../model/Attachment";
import moment, {Moment} from "moment";

export default class AttachmentFactory
{
	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static build(name: string, type: string, data: string, createdAt: Moment = moment())
	{
		return new Attachment(null, name, type, createdAt, data);
	}
}