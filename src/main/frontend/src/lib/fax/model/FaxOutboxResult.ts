import {Moment} from "moment";
import {FaxStatusInternalType} from "./FaxStatusInternalType";
import {FaxStatusCombinedType} from "./FaxStatusCombinedType";
import {FaxNotificationStatusType} from "./FaxNotificationStatusType";
import {FaxFileType} from "./FaxFileType";

export default class FaxOutboxResult
{
	id: number;
	faxAccountId: number;
	providerId: string;
	providerName: string;
	demographicId: number;
	toFaxNumber: string;
	fileType: FaxFileType;
	systemStatus: FaxStatusInternalType;
	systemStatusMessage: string;
	systemSentDateTime: Moment;
	archived: boolean;
	notificationStatus: FaxNotificationStatusType;
	integrationStatus: string;
	integrationQueuedDateTime: Moment;
	integrationSentDateTime: Moment;
	combinedStatus: FaxStatusCombinedType;

	public isInternalStatusError = (): boolean =>
	{
		return (this.systemStatus === FaxStatusInternalType.Error);
	}

	public isInternalStatusSent = (): boolean =>
	{
		return (this.systemStatus === FaxStatusInternalType.Sent);
	}

	public isInternalStatusQueued = (): boolean =>
	{
		return (this.systemStatus === FaxStatusInternalType.Queued);
	}

	public isCombinedStatusSent = (): boolean =>
	{
		return (this.combinedStatus === FaxStatusCombinedType.IntegrationSuccess);
	}

	public isCombinedStatusError = (): boolean =>
	{
		return (this.combinedStatus === FaxStatusCombinedType.Error);
	}

	public isCombinedStatusIntegrationFailed = (): boolean =>
	{
		return (this.combinedStatus === FaxStatusCombinedType.IntegrationFailed);
	}

	public isCombinedStatusInProgress = (): boolean =>
	{
		return (this.combinedStatus === FaxStatusCombinedType.InProgress);
	}

	public isCombinedStatusInQueued = (): boolean =>
	{
		return (this.combinedStatus === FaxStatusCombinedType.Queued);
	}

	public isNotificationStatusNotify = (): boolean =>
	{
		return (this.notificationStatus === FaxNotificationStatusType.Notify);
	}

	public isNotificationStatusSilent = (): boolean =>
	{
		return (this.notificationStatus === FaxNotificationStatusType.Silent);
	}
}