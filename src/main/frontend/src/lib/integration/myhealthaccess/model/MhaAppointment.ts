import {Moment} from "moment";
import {MhaAppointmentType} from "./MhaAppointmentType";

export default class MhaAppointment
{
	private _id: string;
	private _cancelled: boolean;
	private _virtual: boolean;
	private _startDateTime: Moment;
	private _endDateTime: Moment;
	private _appointmentNo: string;
	private _demographicNo: string;
	private _providerNo: string;
	private _appName: string;
	private _appointmentType: MhaAppointmentType;

	// ==========================================================================
	// Public Methods
	// ==========================================================================

	constructor(id: string, startDateTime: Moment, endDateTime: Moment, appointmentNo: string,
		demographicNo: string, providerNo: string, virtual: boolean, appName: string, appointmentType: MhaAppointmentType, cancelled = false)
	{
		this._id = id;
		this._cancelled = cancelled;
		this._virtual = virtual;
		this._startDateTime = startDateTime;
		this._endDateTime = endDateTime;
		this._appointmentNo = appointmentNo;
		this._providerNo = providerNo;
		this._appName = appName;
		this._appointmentType = appointmentType;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get id(): string
	{
		return this._id;
	}

	get cancelled(): boolean
	{
		return this._cancelled;
	}

	get virtual(): boolean
	{
		return this._virtual;
	}

	get startDateTime(): Moment
	{
		return this._startDateTime;
	}

	get endDateTime(): Moment
	{
		return this._endDateTime;
	}

	get appointmentNo(): string
	{
		return this._appointmentNo;
	}

	get demographicNo(): string
	{
		return this._demographicNo;
	}

	get providerNo(): string
	{
		return this._providerNo;
	}

	get appName(): string
	{
		return this._appName;
	}

	get appointmentType(): MhaAppointmentType
	{
		return this._appointmentType;
	}
}