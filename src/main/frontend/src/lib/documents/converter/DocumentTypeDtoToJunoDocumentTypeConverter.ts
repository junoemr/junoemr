import AbstractConverter from "../../conversion/AbstractConverter";
import {DocumentTypeDto} from "../../../../generated";
import JunoDocumentType from "../model/JunoDocumentType";
import {JunoDocumentTypeModule} from "../model/JunoDocumentTypeModule";
import {JunoDocumentTypeStatus} from "../model/JunoDocumentTypeStatus";


export default class DocumentTypeDtoToJunoDocumentTypeConverter extends AbstractConverter<DocumentTypeDto, JunoDocumentType>
{
	// ==========================================================================
	// AbstractConverter Implementation
	// ==========================================================================

	convert(from: DocumentTypeDto): JunoDocumentType
	{
		return new JunoDocumentType(from.id?.toString(), from.module as JunoDocumentTypeModule, from.type, from.status as JunoDocumentTypeStatus);
	}

}