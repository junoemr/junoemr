import {Moment} from "moment";
import {FaxStatusInternalType} from "./FaxStatusInternalType";
import {FaxStatusCombinedType} from "./FaxStatusCombinedType";
import {FaxNotificationStatusType} from "./FaxNotificationStatusType";
import {FaxFileType} from "./FaxFileType";
import PhoneNumber from "../../common/model/PhoneNumber";

export default class FaxOutboxResult
{
	private _id: number;
	private _faxAccountId: number;
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

	get id(): number
	{
		return this._id;
	}

	set id(value: number)
	{
		this._id = value;
	}

	get faxAccountId(): number
	{
		return this._faxAccountId;
	}

	set faxAccountId(value: number)
	{
		this._faxAccountId = value;
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

	get systemSentDateTime(): moment.Moment
	{
		return this._systemSentDateTime;
	}

	set systemSentDateTime(value: moment.Moment)
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

	get integrationQueuedDateTime(): moment.Moment
	{
		return this._integrationQueuedDateTime;
	}

	set integrationQueuedDateTime(value: moment.Moment)
	{
		this._integrationQueuedDateTime = value;
	}

	get integrationSentDateTime(): moment.Moment
	{
		return this._integrationSentDateTime;
	}

	set integrationSentDateTime(value: moment.Moment)
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

	get isInternalStatusError(): () => boolean
	{
		return this._isInternalStatusError;
	}

	set isInternalStatusError(value: () => boolean)
	{
		this._isInternalStatusError = value;
	}

	get isInternalStatusSent(): () => boolean
	{
		return this._isInternalStatusSent;
	}

	set isInternalStatusSent(value: () => boolean)
	{
		this._isInternalStatusSent = value;
	}

	get isInternalStatusQueued(): () => boolean
	{
		return this._isInternalStatusQueued;
	}

	set isInternalStatusQueued(value: () => boolean)
	{
		this._isInternalStatusQueued = value;
	}

	get isCombinedStatusSent(): () => boolean
	{
		return this._isCombinedStatusSent;
	}

	set isCombinedStatusSent(value: () => boolean)
	{
		this._isCombinedStatusSent = value;
	}

	get isCombinedStatusError(): () => boolean
	{
		return this._isCombinedStatusError;
	}

	set isCombinedStatusError(value: () => boolean)
	{
		this._isCombinedStatusError = value;
	}

	get isCombinedStatusIntegrationFailed(): () => boolean
	{
		return this._isCombinedStatusIntegrationFailed;
	}

	set isCombinedStatusIntegrationFailed(value: () => boolean)
	{
		this._isCombinedStatusIntegrationFailed = value;
	}

	get isCombinedStatusInProgress(): () => boolean
	{
		return this._isCombinedStatusInProgress;
	}

	set isCombinedStatusInProgress(value: () => boolean)
	{
		this._isCombinedStatusInProgress = value;
	}

	get isCombinedStatusInQueued(): () => boolean
	{
		return this._isCombinedStatusInQueued;
	}

	set isCombinedStatusInQueued(value: () => boolean)
	{
		this._isCombinedStatusInQueued = value;
	}

	get isNotificationStatusNotify(): () => boolean
	{
		return this._isNotificationStatusNotify;
	}

	set isNotificationStatusNotify(value: () => boolean)
	{
		this._isNotificationStatusNotify = value;
	}

	get isNotificationStatusSilent(): () => boolean
	{
		return this._isNotificationStatusSilent;
	}

	set isNotificationStatusSilent(value: () => boolean)
	{
		this._isNotificationStatusSilent = value;
	}

	private _isInternalStatusError = (): boolean =>
	{
		return (this._systemStatus === FaxStatusInternalType.Error);
	}

	private _isInternalStatusSent = (): boolean =>
	{
		return (this._systemStatus === FaxStatusInternalType.Sent);
	}

	private _isInternalStatusQueued = (): boolean =>
	{
		return (this._systemStatus === FaxStatusInternalType.Queued);
	}

	private _isCombinedStatusSent = (): boolean =>
	{
		return (this._combinedStatus === FaxStatusCombinedType.IntegrationSuccess);
	}

	private _isCombinedStatusError = (): boolean =>
	{
		return (this._combinedStatus === FaxStatusCombinedType.Error);
	}

	private _isCombinedStatusIntegrationFailed = (): boolean =>
	{
		return (this._combinedStatus === FaxStatusCombinedType.IntegrationFailed);
	}

	private _isCombinedStatusInProgress = (): boolean =>
	{
		return (this._combinedStatus === FaxStatusCombinedType.InProgress);
	}

	private _isCombinedStatusInQueued = (): boolean =>
	{
		return (this._combinedStatus === FaxStatusCombinedType.Queued);
	}

	private _isNotificationStatusNotify = (): boolean =>
	{
		return (this._notificationStatus === FaxNotificationStatusType.Notify);
	}

	private _isNotificationStatusSilent = (): boolean =>
	{
		return (this._notificationStatus === FaxNotificationStatusType.Silent);
	}
}