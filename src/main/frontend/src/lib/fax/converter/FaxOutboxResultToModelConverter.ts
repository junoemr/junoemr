import AbstractConverter from "../../conversion/AbstractConverter";
import {FaxOutboxTransferOutbound} from "../../../../generated";
import moment from "moment";
import FaxOutboxResult from "../model/FaxOutboxResult";
import {FaxStatusCombinedType} from "../model/FaxStatusCombinedType";
import {FaxStatusInternalType} from "../model/FaxStatusInternalType";
import {FaxNotificationStatusType} from "../model/FaxNotificationStatusType";
import {FaxFileType} from "../model/FaxFileType";
import PhoneNumberToModelConverter from "../../common/converter/PhoneNumberToModelConverter";

export default class FaxOutboxResultToModelConverter extends AbstractConverter<FaxOutboxTransferOutbound, FaxOutboxResult>
{
	public convert(from: FaxOutboxTransferOutbound): FaxOutboxResult
	{
		if(!from)
		{
			return null;
		}

		let model = new FaxOutboxResult();
		model.id = from.id;
		model.faxAccountId = from.faxAccountId;
		model.providerId = from.providerId;
		model.providerName = from.providerName;
		model.demographicId = from.demographicId;
		model.toFaxNumber = new PhoneNumberToModelConverter().convert(from.toFaxNumber);
		model.fileType = from.fileType as any as FaxFileType;
		model.systemStatus = from.systemStatus as any as FaxStatusInternalType;
		model.systemStatusMessage = from.systemStatusMessage;
		model.systemSentDateTime = moment(from.systemSentDateTime);
		model.archived = from.archived;
		model.notificationStatus = from.notificationStatus as any as FaxNotificationStatusType;
		model.integrationStatus = from.integrationStatus;
		model.integrationQueuedDateTime = moment(from.integrationQueuedDateTime);
		model.integrationSentDateTime = moment(from.integrationSentDateTime);
		model.combinedStatus = from.combinedStatus as any as FaxStatusCombinedType;

		return model;
	}

}