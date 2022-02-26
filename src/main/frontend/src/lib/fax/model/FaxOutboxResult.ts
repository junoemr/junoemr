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
}