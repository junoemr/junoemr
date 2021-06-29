import {DemographicApi, DocumentApi} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import JunoDocument from "../model/JunoDocument";
import DocumentTransferOutboundToJunoDocumentConverter
	from "../converter/DocumentTransferOutboundToJunoDocumentConverter";

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
}