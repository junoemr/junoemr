import SimpleProvider from "../../provider/model/SimpleProvider";
import {Moment} from "moment";
import AppointmentStatus from "./AppointmentStatus";

export default class Appointment
{
	private readonly _id: number;

	private _provider: SimpleProvider;
	private _appointmentStartDateTime: Moment;
	private _appointmentEndDateTime: Moment;
	private _name: string;
	private _demographicId: number;

	private _notes: string;
	private _reason: string;
	private _resources: string;
	private _type: string;
	private _style: string;
	private _status: AppointmentStatus;
	private _location: string;
	private _siteId: number;

	constructor(id)
	{
		this._id = id;
	}

	get id(): number
	{
		return this._id;
	}

	get provider(): SimpleProvider
	{
		return this._provider;
	}

	set provider(value: SimpleProvider)
	{
		this._provider = value;
	}

	get appointmentStartDateTime(): Moment
	{
		return this._appointmentStartDateTime;
	}

	set appointmentStartDateTime(value: Moment)
	{
		this._appointmentStartDateTime = value;
	}

	get appointmentEndDateTime(): Moment
	{
		return this._appointmentEndDateTime;
	}

	set appointmentEndDateTime(value: Moment)
	{
		this._appointmentEndDateTime = value;
	}

	get name(): string
	{
		return this._name;
	}

	set name(value: string)
	{
		this._name = value;
	}

	get demographicId(): number
	{
		return this._demographicId;
	}

	set demographicId(value: number)
	{
		this._demographicId = value;
	}

	get notes(): string
	{
		return this._notes;
	}

	set notes(value: string)
	{
		this._notes = value;
	}

	get reason(): string
	{
		return this._reason;
	}

	set reason(value: string)
	{
		this._reason = value;
	}

	get resources(): string
	{
		return this._resources;
	}

	set resources(value: string)
	{
		this._resources = value;
	}

	get type(): string
	{
		return this._type;
	}

	set type(value: string)
	{
		this._type = value;
	}

	get style(): string
	{
		return this._style;
	}

	set style(value: string)
	{
		this._style = value;
	}

	get status(): AppointmentStatus
	{
		return this._status;
	}

	set status(value: AppointmentStatus)
	{
		this._status = value;
	}

	get location(): string
	{
		return this._location;
	}

	set location(value: string)
	{
		this._location = value;
	}

	get siteId(): number
	{
		return this._siteId;
	}

	set siteId(value: number)
	{
		this._siteId = value;
	}
}