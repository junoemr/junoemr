import ConsultRequest from "../model/ConsultRequest";
import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConsultationRequestTo1} from "../../../../../generated";
import moment from "moment";
import PhoneNumber from "../../../common/model/PhoneNumber";
import {PhoneType} from "../../../common/model/PhoneType";
import Address from "../../../common/model/Address";
import {AddressResidencyStatus} from "../../../common/model/AddressResidencyStatus";
import Letterhead from "../model/Letterhead";

export default class ConsultRequestModelConverter extends AbstractConverter<ConsultationRequestTo1, ConsultRequest>
{
	public convert(from: ConsultationRequestTo1): ConsultRequest
	{
		if(!from)
		{
			return null;
		}

		let model = new ConsultRequest(from.id);

		model.referralDate = from.referralDate ? moment(from.referralDate).startOf("day") : null;
		model.serviceId = from.serviceId;
		model.professionalSpecialist = from.professionalSpecialist; // should have own model

		if(from.appointmentDate && from.appointmentTime)
		{
			model.appointmentDateTime = Juno.Common.Util.getDateAndTimeMoment(from.appointmentDate, from.appointmentTime);
		}

		model.reasonForReferral = from.reasonForReferral;
		model.clinicalInfo = from.clinicalInfo;
		model.currentMeds = from.currentMeds;
		model.allergies = from.allergies;
		model.providerNo = from.providerNo;
		model.demographicId = from.demographicId;
		model.status = from.status;
		model.statusText = from.statusText;
		model.sendTo = from.sendTo;
		model.concurrentProblems = from.concurrentProblems;
		model.urgency = from.urgency;
		model.patientWillBook = from.patientWillBook;
		model.siteName = from.siteName;
		model.followUpDate = from.followUpDate ? moment(from.followUpDate).startOf("day") : null;
		model.signatureImg = from.signatureImg;

		let letterhead = new Letterhead(from.letterheadName);
		if(from.letterheadAddress)
		{
			let address = new Address(AddressResidencyStatus.Current);
			address.addressLine1 = from.letterheadAddress;
			letterhead.address = address;
		}
		if(from.letterheadPhone)
		{
			letterhead.phone = new PhoneNumber(from.letterheadPhone, null, PhoneType.Work, true);
		}
		if(from.letterheadFax)
		{
			letterhead.fax = new PhoneNumber(from.letterheadFax, null, PhoneType.Fax, false);
		}
		model.letterhead = letterhead;

		model.attachments = Juno.Common.Util.toArray(from.attachments); // should have own models

		model.faxList = Juno.Common.Util.toArray(from.faxList);
		model.sendToList = Juno.Common.Util.toArray(from.sendToList);

		return model;
	}
}