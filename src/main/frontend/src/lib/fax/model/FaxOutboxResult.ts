import {Moment} from "moment";
import {FaxStatusInternalType} from "./FaxStatusInternalType";
import {FaxStatusCombinedType} from "./FaxStatusCombinedType";
import {FaxNotificationStatusType} from "./FaxNotificationStatusType";
import {FaxFileType} from "./FaxFileType";
import PhoneNumber from "../../common/model/PhoneNumber";
import FaxAccount from "./FaxAccount";

export default class FaxOutboxResult
{
	private _id: number;
	private _faxAccount: FaxAccount;
	private _providerId: string;
	private _providerName: string;
	private _demographicId: number;
	private _toFaxNumber: PhoneNumber;
	private _fileType: FaxFileType;
	private _systemStatus: FaxStatusInternalType;
	private _systemStatusMessage: string;
	private _systemSentDateTime: Moment;
	private _archived: boolean;
	private _notificationStatus: FaxNotificationStatusType;
	private _integrationStatus: string;
	private _integrationQueuedDateTime: Moment;
	private _integrationSentDateTime: Moment;
	private _combinedStatus: FaxStatusCombinedType;

	/**
	 * helpers
	 */

	get isInternalStatusError(): boolean
	{
		return (this.systemStatus === FaxStatusInternalType.Error);
	}

	get isInternalStatusSent(): boolean
	{
		return (this.systemStatus === FaxStatusInternalType.Sent);
	}

	get isInternalStatusQueued(): boolean
	{
		return (this.systemStatus === FaxStatusInternalType.Queued);
	}

	get isCombinedStatusSent(): boolean
	{
		return (this.combinedStatus === FaxStatusCombinedType.IntegrationSuccess);
	}

	get isCombinedStatusError(): boolean
	{
		return (this.combinedStatus === FaxStatusCombinedType.Error);
	}

	get isCombinedStatusIntegrationFailed(): boolean
	{
		return (this.combinedStatus === FaxStatusCombinedType.IntegrationFailed);
	}

	get isCombinedStatusInProgress(): boolean
	{
		return (this.combinedStatus === FaxStatusCombinedType.InProgress);
	}

	get isCombinedStatusInQueued(): boolean
	{
		return (this.combinedStatus === FaxStatusCombinedType.Queued);
	}

	get isNotificationStatusNotify(): boolean
	{
		return (this.notificationStatus === FaxNotificationStatusType.Notify);
	}

	get isNotificationStatusSilent(): boolean
	{
		return (this.notificationStatus === FaxNotificationStatusType.Silent);
	}

	/**
	 * direct getters and setters
	 */
	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get faxAccount(): FaxAccount
	{
		return this._faxAccount;
	}

	set faxAccount(value: FaxAccount)
	{
		this._faxAccount = value;
	}

	get providerId(): string
	{
		return this._providerId;
	}

	set providerId(value: string)
	{
		this._providerId = value;
	}

	get providerName(): string
	{
		return this._providerName;
	}

	set providerName(value: string)
	{
		this._providerName = value;
	}

	get demographicId(): number
	{
		return this._demographicId;
	}

	set demographicId(value: number)
	{
		this._demographicId = value;
	}

	get toFaxNumber(): PhoneNumber
	{
		return this._toFaxNumber;
	}

	set toFaxNumber(value: PhoneNumber)
	{
		this._toFaxNumber = value;
	}

	get fileType(): FaxFileType
	{
		return this._fileType;
	}

	set fileType(value: FaxFileType)
	{
		this._fileType = value;
	}

	get systemStatus(): FaxStatusInternalType
	{
		return this._systemStatus;
	}

	set systemStatus(value: FaxStatusInternalType)
	{
		this._systemStatus = value;
	}

	get systemStatusMessage(): string
	{
		return this._systemStatusMessage;
	}

	set systemStatusMessage(value: string)
	{
		this._systemStatusMessage = value;
	}

	get systemSentDateTime(): Moment
	{
		return this._systemSentDateTime;
	}

	set systemSentDateTime(value: Moment)
	{
		this._systemSentDateTime = value;
	}

	get archived(): boolean
	{
		return this._archived;
	}

	set archived(value: boolean)
	{
		this._archived = value;
	}

	get notificationStatus(): FaxNotificationStatusType
	{
		return this._notificationStatus;
	}

	set notificationStatus(value: FaxNotificationStatusType)
	{
		this._notificationStatus = value;
	}

	get integrationStatus(): string
	{
		return this._integrationStatus;
	}

	set integrationStatus(value: string)
	{
		this._integrationStatus = value;
	}

	get integrationQueuedDateTime(): Moment
	{
		return this._integrationQueuedDateTime;
	}

	set integrationQueuedDateTime(value: Moment)
	{
		this._integrationQueuedDateTime = value;
	}

	get integrationSentDateTime(): Moment
	{
		return this._integrationSentDateTime;
	}

	set integrationSentDateTime(value: Moment)
	{
		this._integrationSentDateTime = value;
	}

	get combinedStatus(): FaxStatusCombinedType
	{
		return this._combinedStatus;
	}

	set combinedStatus(value: FaxStatusCombinedType)
	{
		this._combinedStatus = value;
	}

}