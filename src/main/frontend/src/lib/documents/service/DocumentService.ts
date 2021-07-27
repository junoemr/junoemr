import {DemographicApi, DocumentApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import JunoDocument from "../model/JunoDocument";
import DocumentTransferOutboundToJunoDocumentConverter
	from "../converter/DocumentTransferOutboundToJunoDocumentConverter";
import DocumentTypeDtoToJunoDocumentTypeConverter from "../converter/DocumentTypeDtoToJunoDocumentTypeConverter";
import {JunoDocumentTypeStatus} from "../model/JunoDocumentTypeStatus";
import {JunoDocumentTypeModule} from "../model/JunoDocumentTypeModule";
import JunoDocumentType from "../model/JunoDocumentType";

export default class DocumentService
{
	protected _documentApi: DocumentApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._documentApi = new DocumentApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	public async getDocument(documentNo: string): Promise<JunoDocument>
	{
		return (new DocumentTransferOutboundToJunoDocumentConverter()).convert((await this._documentApi.getDocument(documentNo)).data.body);
	}

	/**
	 * get a list of all demographic document types.
	 */
	public async getDemographicDocumentTypes(): Promise<JunoDocumentType[]>
	{
		return (new DocumentTypeDtoToJunoDocumentTypeConverter()).convertList((await this._documentApi.getDocumentTypes(JunoDocumentTypeStatus.Active, JunoDocumentTypeModule.Demographic)).data.body);
	}
}