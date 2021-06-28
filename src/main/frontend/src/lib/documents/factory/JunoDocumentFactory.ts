import JunoDocument from "../model/JunoDocument";
import {DocumentStatus} from "../model/DocumentStatus";

export class JunoDocumentFactory
{
	protected static readonly DEFAULT_DOC_TYPE = "Document";

	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static build(fileName: string, description: string, contentType: string, base64Data: string): JunoDocument
	{
		const doc = new JunoDocument(fileName, description, base64Data);
		doc.documentType = JunoDocumentFactory.DEFAULT_DOC_TYPE;
		doc.status = DocumentStatus.A;
		doc.contentType = contentType;

		return doc;
	}
}