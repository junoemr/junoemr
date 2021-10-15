import AbstractConverter from "../../conversion/AbstractConverter";
import JunoDocument from "../model/JunoDocument";
import {DocumentTransferInbound} from "../../../../generated";

export default class JunoDocumentToDocumentTransferInboundConverter extends AbstractConverter<JunoDocument, Promise<DocumentTransferInbound>>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	public async convert(from: JunoDocument): Promise<DocumentTransferInbound>
	{
		return {
			documentNo: parseInt(from.documentNo),
			status: from.status,
			// @ts-ignore
			createdDateTime: from.createdAt?.toDate(),
			observationDate: from.observedAt?.toString(),
			publicDocument: from.publicDocument,
			base64EncodedFile: await from.getBase64Data(),
			documentType: from.documentType,
			documentClass: from.documentClass,
			documentSubClass: from.documentSubClass,
			documentDescription: from.documentDescription,
			documentXml: from.documentXml,
			source: from.documentSource,
			sourceFacility: from.documentSourceFacility,
			documentCreator: from.documentCreatorProviderNo,
			responsible: from.responsibleProviderNo,
			reviewer: from.reviewerProviderNo,
			// @ts-ignore
			reviewDateTime: from.reviewedAt?.toDate(),
			appointmentNo: parseInt(from.appointmentNo),
			fileName: from.fileName,
		}
	}

}