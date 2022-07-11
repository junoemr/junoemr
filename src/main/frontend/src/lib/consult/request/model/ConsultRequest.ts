import {
	ConsultationAttachmentTo1,
	FaxConfigTo1,
	ProfessionalSpecialistTo1
} from "../../../../../generated";
import {Moment} from "moment";
import Letterhead from "./Letterhead";

export default class ConsultRequest
{
	private readonly _id: number;

	private _referralDate: Moment;
	private _serviceId: number;
	private _professionalSpecialist: ProfessionalSpecialistTo1;
	private _appointmentDateTime: Moment;
	private _reasonForReferral: string;
	private _clinicalInfo: string;
	private _currentMeds: string;
	private _allergies: string;
	private _providerNo: string;
	private _demographicId: number;
	private _status: string;
	private _statusText: string;
	private _sendTo: string;
	private _concurrentProblems: string;
	private _urgency: string;
	private _patientWillBook: boolean;
	private _siteName: string;
	private _followUpDate: Moment;
	private _signatureImg: string;
	private _letterhead: Letterhead;
	private _attachments: ConsultationAttachmentTo1[];

	// todo move lists to their own requests, not attached from model
	public faxList: Array<FaxConfigTo1>;
	public sendToList: Array<string>;

	constructor(id)
	{
		this._id = id;
	}

	get id(): number
	{
		return this._id;
	}

	get referralDate(): Moment
	{
		return this._referralDate;
	}

	set referralDate(value: Moment)
	{
		this._referralDate = value;
	}

	get serviceId(): number
	{
		return this._serviceId;
	}

	set serviceId(value: number)
	{
		this._serviceId = value;
	}

	get professionalSpecialist(): ProfessionalSpecialistTo1
	{
		return this._professionalSpecialist;
	}

	set professionalSpecialist(value: ProfessionalSpecialistTo1)
	{
		this._professionalSpecialist = value;
	}

	get appointmentDateTime(): Moment
	{
		return this._appointmentDateTime;
	}

	set appointmentDateTime(value: Moment)
	{
		this._appointmentDateTime = value;
	}

	get reasonForReferral(): string
	{
		return this._reasonForReferral;
	}

	set reasonForReferral(value: string)
	{
		this._reasonForReferral = value;
	}

	get clinicalInfo(): string
	{
		return this._clinicalInfo;
	}

	set clinicalInfo(value: string)
	{
		this._clinicalInfo = value;
	}

	get currentMeds(): string
	{
		return this._currentMeds;
	}

	set currentMeds(value: string)
	{
		this._currentMeds = value;
	}

	get allergies(): string
	{
		return this._allergies;
	}

	set allergies(value: string)
	{
		this._allergies = value;
	}

	get providerNo(): string
	{
		return this._providerNo;
	}

	set providerNo(value: string)
	{
		this._providerNo = value;
	}

	get demographicId(): number
	{
		return this._demographicId;
	}

	set demographicId(value: number)
	{
		this._demographicId = value;
	}

	get status(): string
	{
		return this._status;
	}

	set status(value: string)
	{
		this._status = value;
	}

	get statusText(): string
	{
		return this._statusText;
	}

	set statusText(value: string)
	{
		this._statusText = value;
	}

	get sendTo(): string
	{
		return this._sendTo;
	}

	set sendTo(value: string)
	{
		this._sendTo = value;
	}

	get concurrentProblems(): string
	{
		return this._concurrentProblems;
	}

	set concurrentProblems(value: string)
	{
		this._concurrentProblems = value;
	}

	get urgency(): string
	{
		return this._urgency;
	}

	set urgency(value: string)
	{
		this._urgency = value;
	}

	get patientWillBook(): boolean
	{
		return this._patientWillBook;
	}

	set patientWillBook(value: boolean)
	{
		this._patientWillBook = value;
	}

	get siteName(): string
	{
		return this._siteName;
	}

	set siteName(value: string)
	{
		this._siteName = value;
	}

	get followUpDate(): Moment
	{
		return this._followUpDate;
	}

	set followUpDate(value: Moment)
	{
		this._followUpDate = value;
	}

	get signatureImg(): string
	{
		return this._signatureImg;
	}

	set signatureImg(value: string)
	{
		this._signatureImg = value;
	}

	get letterhead(): Letterhead
	{
		return this._letterhead;
	}

	set letterhead(value: Letterhead)
	{
		this._letterhead = value;
	}

	get attachments(): ConsultationAttachmentTo1[]
	{
		return this._attachments;
	}

	set attachments(value: ConsultationAttachmentTo1[])
	{
		this._attachments = value;
	}
}