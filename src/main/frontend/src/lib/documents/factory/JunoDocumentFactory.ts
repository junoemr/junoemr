import JunoDocument from "../model/JunoDocument";
import {DocumentStatus} from "../model/DocumentStatus";
import JunoDocumentType from "../model/JunoDocumentType";

export class JunoDocumentFactory
{
	protected static readonly DEFAULT_DOC_TYPE = "Document";

	// ==========================================================================
	// Class Methods
	// ==========================================================================

	public static build(fileName: string, description: string, documentType: JunoDocumentType, contentType: string, base64Data: string): JunoDocument
	{
		const doc = new JunoDocument(fileName, description, base64Data);
		doc.documentType = documentType.type;
		doc.status = DocumentStatus.A;
		doc.contentType = contentType;

		return doc;
	}
}