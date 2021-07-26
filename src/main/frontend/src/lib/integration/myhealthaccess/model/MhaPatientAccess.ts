import {LinkStatus} from "./LinkStatus";
import {Moment} from "moment";
import {MhaUserType} from "./MhaUserType";

export default class MhaPatientAccess
{
	// core
	protected _patientId: string;
	protected _clinicId: string;
	protected _linkStatus: LinkStatus;

	// user permissions
	protected _canMessage: boolean;
	protected _canCancelAppointments: boolean;

	// confirmation
	protected _confirmedAt: Moment;
	protected _confirmedById: string;
	protected _confirmedByType: MhaUserType;
	protected _confirmingUserName: string;

	// verification
	protected _verifiedAt: Moment;
	protected _verifiedById: string;
	protected _verifiedByType: MhaUserType;
	protected _verifierUserName: string;

	// ==========================================================================
	// Setters
	// ==========================================================================

	set patientId(value: string)
	{
		this._patientId = value;
	}

	set clinicId(value: string)
	{
		this._clinicId = value;
	}

	set linkStatus(value: LinkStatus)
	{
		this._linkStatus = value;
	}

	set canMessage(value: boolean)
	{
		this._canMessage = value;
	}

	set canCancelAppointments(value: boolean)
	{
		this._canCancelAppointments = value;
	}

	set confirmedAt(value: Moment)
	{
		this._confirmedAt = value;
	}

	set confirmedById(value: string)
	{
		this._confirmedById = value;
	}

	set confirmedByType(value: MhaUserType)
	{
		this._confirmedByType = value;
	}

	set confirmingUserName(value: string)
	{
		this._confirmingUserName = value;
	}

	set verifiedAt(value: Moment)
	{
		this._verifiedAt = value;
	}

	set verifiedById(value: string)
	{
		this._verifiedById = value;
	}

	set verifiedByType(value: MhaUserType)
	{
		this._verifiedByType = value;
	}

	set verifierUserName(value: string)
	{
		this._verifierUserName = value;
	}

	// ==========================================================================
	// Getters
	// ==========================================================================

	get patientId(): string
	{
		return this._patientId;
	}

	get clinicId(): string
	{
		return this._clinicId;
	}

	get linkStatus(): LinkStatus
	{
		return this._linkStatus;
	}

	get canMessage(): boolean
	{
		return this._canMessage;
	}

	get canCancelAppointments(): boolean
	{
		return this._canCancelAppointments;
	}

	get confirmedAt(): Moment
	{
		return this._confirmedAt;
	}

	get confirmedById(): string
	{
		return this._confirmedById;
	}

	get confirmedByType(): MhaUserType
	{
		return this._confirmedByType;
	}

	get confirmingUserName(): string
	{
		return this._confirmingUserName;
	}

	get verifiedAt(): Moment
	{
		return this._verifiedAt;
	}

	get verifiedById(): string
	{
		return this._verifiedById;
	}

	get verifiedByType(): MhaUserType
	{
		return this._verifiedByType;
	}

	get verifierUserName(): string
	{
		return this._verifierUserName;
	}
}