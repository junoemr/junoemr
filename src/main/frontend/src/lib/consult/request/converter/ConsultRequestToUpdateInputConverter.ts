import ConsultRequest from "../model/ConsultRequest";
import AbstractConverter from "../../../conversion/AbstractConverter";
import {ConsultationRequestTo1} from "../../../../../generated";

export default class ConsultRequestToUpdateInputConverter extends AbstractConverter<ConsultRequest, ConsultationRequestTo1>
{
	convert(model: ConsultRequest): any
	{
		if(!model)
		{
			return null;
		}

		let input = {} as ConsultationRequestTo1;

		input.id = model.id;
		input.referralDate = this.serializeLocalDateTime(model.referralDate);
		input.serviceId = model.serviceId;
		input.professionalSpecialist = model.professionalSpecialist;
		input.appointmentDateTime = this.serializeZonedDateTime(model.appointmentDateTime);
		input.reasonForReferral = model.reasonForReferral;
		input.clinicalInfo = model.clinicalInfo;
		input.currentMeds = model.currentMeds;
		input.allergies = model.allergies;
		input.providerNo = model.providerNo;
		input.demographicId = model.demographicId;
		input.status = model.status;
		input.statusText = model.statusText;
		input.sendTo = model.sendTo;
		input.concurrentProblems = model.concurrentProblems;
		input.urgency = model.urgency;
		input.patientWillBook = model.patientWillBook;
		input.siteName = model.siteName;
		input.followUpDate = this.serializeLocalDateTime(model.followUpDate);
		input.signatureImg = model.signatureImg;

		if(model.letterhead)
		{
			const letterhead = model.letterhead;
			input.letterheadName = letterhead.id; // why does id map to name?
			if(letterhead.address)
			{
				input.letterheadAddress = letterhead.address.addressLine1;
			}
			if(letterhead.phone)
			{
				input.letterheadPhone = letterhead.phone.number;
			}
			if(letterhead.fax)
			{
				input.letterheadFax = letterhead.fax.number;
			}
		}

		input.attachments = model.attachments;
		return input;
	}
}