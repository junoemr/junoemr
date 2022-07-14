import {ProfessionalSpecialistTo1} from "../../../../../generated";

export default class ConsultService
{
	private readonly _id: string;

	private _description: string;
	private _active: boolean;
	private _specialists: ProfessionalSpecialistTo1[]; // needs its own model

	constructor(id)
	{
		this._id = id;
	}

	get id(): string
	{
		return this._id;
	}

	get description(): string
	{
		return this._description;
	}

	set description(value: string)
	{
		this._description = value;
	}

	get active(): boolean
	{
		return this._active;
	}

	set active(value: boolean)
	{
		this._active = value;
	}

	get specialists(): ProfessionalSpecialistTo1[]
	{
		return this._specialists;
	}

	set specialists(value: ProfessionalSpecialistTo1[])
	{
		this._specialists = value;
	}
}