import {MhaPatientApi} from "../../../../../generated";
import {API_BASE_PATH} from "../../../constants/ApiConstants";
import PatientAccessDtoToMhaPatientAccessConverter from "../converter/PatientAccessDtoToMhaPatientAccessConverter";
import MhaPatientAccess from "../model/MhaPatientAccess";
import MhaConfigService from "./MhaConfigService";
import MhaPatient from "../model/MhaPatient";
import {LinkStatus} from "../model/LinkStatus";

export default class MhaPatientAccessService
{
	protected _mhaPatientApi: MhaPatientApi;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	public constructor()
	{
		this._mhaPatientApi = new MhaPatientApi(
			angular.injector(["ng"]).get("$http"),
			angular.injector(["ng"]).get("$httpParamSerializer"),
			API_BASE_PATH);
	}

	/**
	 * get all patient access records for the given remote id
	 * @param remoteId - the remote id to get the access records for
	 * @return promise that resolves to an array of access records
	 */
	public async getPatientAccesses(remoteId: string): Promise<MhaPatientAccess[]>
	{
		const mhaConfigService = new MhaConfigService();

		return await Promise.all((await mhaConfigService.getMhaIntegrations()).map( async (integration) =>
		{
			return await this.getPatientAccess(integration.id, remoteId);
		}));
	}

	/**
	 * get a patients access record
	 * @param integrationId - the integration to get the record from
	 * @param remoteId - the remote patient id whose record is to be fetched
	 * @return promise that resolves to an access record
	 */
	public async getPatientAccess(integrationId: string, remoteId: string): Promise<MhaPatientAccess>
	{
		return (new PatientAccessDtoToMhaPatientAccessConverter()).convert((await this._mhaPatientApi.getPatientAccess(integrationId, remoteId)).data.body);
	}

	/**
	 * verify a patient by account Id Code (temporary verification code).
	 * The patient will also be connected to the clinic if not already, and
	 * linked to the EMR record if not already.
	 * @param integrationId - the integration in which to verify the patient
	 * @param patient - the patient to verify
	 * @param demographicNo - the demographicNo in the EMR that this patient record should map to.
	 * @param idCode - the account id code used in the verification process.
	 */
	public async verifyPatientByAccountIdCode(integrationId: string, patient: MhaPatient, demographicNo: string, idCode: string): Promise<void>
	{
		if (patient.linkStatus === LinkStatus.NO_LINK || patient.linkStatus === LinkStatus.PATIENT_REJECTED)
		{
			// patient is not connected to use at all! Connect!
			await this._mhaPatientApi.connectPatientByVerificationCode(integrationId, patient.id, {verificationCode: idCode});
		}

		await this._mhaPatientApi.linkPatientToEmrPatient(integrationId, patient.id, {demographicNo: demographicNo});
		await this._mhaPatientApi.verifyPatient(integrationId, patient.id);
	}

	/**
	 * cancel patients verification
	 * @param integrationId - the integration to cancel the patient verification in
	 * @param remoteId - the patient to cancel the verification for
	 */
	public async cancelPatientVerification(integrationId: string, remoteId: string): Promise<void>
	{
		await this._mhaPatientApi.cancelPatientVerification(integrationId, remoteId);
	}

	/**
	 * confirm a patients connection. Also links them to the specified EMR record.
	 * @param integrationId - the clinic to confirm the patients connection in.
	 * @param demographicNo - the local EMR record that the remote patient should be linked against.
	 * @param remoteId - the patients remote id
	 */
	public async confirmPatient(integrationId: string, remoteId: string, demographicNo: string): Promise<void>
	{
		await this._mhaPatientApi.linkPatientToEmrPatient(integrationId, remoteId, {demographicNo: demographicNo});
		await this._mhaPatientApi.confirmPatient(integrationId, remoteId);
	}

	/**
	 * cancel a patients confirmation
	 * @param integrationId - the clinic to cancel the patients confirmation in
	 * @param remoteId - the patients remote id
	 */
	public async cancelPatientConfirmation(integrationId: string, remoteId: string): Promise<void>
	{
		await this._mhaPatientApi.cancelPatientConfirmation(integrationId, remoteId);
	}

	/**
	 * reject a patient from the cinic
	 * @param integrationId - the clinic to reject from
	 * @param remoteId - the patient to reject
	 */
	public async rejectPatient(integrationId: string, remoteId: string): Promise<void>
	{
		await this._mhaPatientApi.rejectPatient(integrationId, remoteId);
	}

	/**
	 * cancel the rejection of a patient
	 * @param integrationId
	 * @param remoteId
	 */
	public async cancelPatientRejection(integrationId: string, remoteId: string): Promise<void>
	{
		await this._mhaPatientApi.cancelRejectPatient(integrationId, remoteId);
	}
}