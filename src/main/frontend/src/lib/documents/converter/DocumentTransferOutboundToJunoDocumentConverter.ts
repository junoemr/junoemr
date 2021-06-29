import AbstractConverter from "../../conversion/AbstractConverter";
import {DocumentTransferOutbound} from "../../../../generated";
import JunoDocument from "../model/JunoDocument";
import moment from "moment";

export default class DocumentTransferOutboundToJunoDocumentConverter extends AbstractConverter<DocumentTransferOutbound, JunoDocument>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: DocumentTransferOutbound): JunoDocument
	{

		const doc = new JunoDocument(from.fileName, from.documentDescription, from.base64EncodedFile);
		doc.documentNo = from.documentNo.toString();
		doc.status = from.status;
		doc.createdAt = moment(from.createdDateTime);
		doc.updatedAt = moment(from.updateDateTime);
		doc.observedAt = moment(from.observationDate);
		doc.publicDocument = from.publicDocument;
		doc.documentType = from.documentType;
		doc.documentClass = from.documentClass;
		doc.documentSubClass = from.documentSubClass;
		doc.documentXml = from.documentXml;
		doc.documentSource = from.source;
		doc.documentSourceFacility = from.sourceFacility;
		doc.documentCreatorProviderNo = from.documentCreator;
		doc.responsibleProviderNo = from.responsible;
		doc.reviewerProviderNo = from.reviewer;
		doc.reviewedAt = moment(from.reviewDateTime);
		doc.contentType = from.contentType;

		return doc;
	}

}