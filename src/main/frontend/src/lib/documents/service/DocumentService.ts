import JunoDocument from "../model/JunoDocument";
import {DemographicApi, DocumentTransferOutbound} from "../../../../generated";
import {API_BASE_PATH} from "../../constants/ApiConstants";
import DocumentUploadError from "../../error/document/DocumentUploadError";
import JunoDocumentToDocumentTransferInboundConverter
	from "../converter/JunoDocumentToDocumentTransferInboundConverter";
import DocumentTransferOutboundToJunoDocumentConverter
	from "../converter/DocumentTransferOutboundToJunoDocumentConverter";

export default class DocumentService
{
	protected _demogrpahicApi: DemographicApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor()
	{
		this._demogrpahicApi = new DemographicApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * upload the specified document to the patients chart
	 * @param document - the document to upload
	 * @param demographicNo - the patient who's chart the document is to be stored in.
	 * @return a fresh copy of the document from the Juno server.
	 * @throws DocumentUploadError - if an error occurs uploading the document to the demographic's chart
	 */
	public async uploadDocumentToDemographicChart(document: JunoDocument, demographicNo: string): Promise<JunoDocument>
	{
		try
		{
			const docTransfer: DocumentTransferOutbound =
				(await this._demogrpahicApi.createDocument(demographicNo, (new JunoDocumentToDocumentTransferInboundConverter()).convert(document))).data.body;

			return (new DocumentTransferOutboundToJunoDocumentConverter()).convert(docTransfer);
		}
		catch(error)
		{
			console.error(error);
			throw new DocumentUploadError(`Failed to upload document to demographic [${demographicNo}] chart with error: ` + error.toString());
		}
	}

}
