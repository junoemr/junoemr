import {Moment} from "moment";

export default class TempNoteInput
{
	private _noteText: string;
	private _observationDate: Moment;
	private _noteId: number;
	private _encounterType: string;

	protected constructor()
	{
	}

	get noteText(): string
	{
		return this._noteText;
	}

	set noteText(value: string)
	{
		this._noteText = value;
	}

	get observationDate(): Moment
	{
		return this._observationDate;
	}

	set observationDate(value: Moment)
	{
		this._observationDate = value;
	}

	get noteId(): number
	{
		return this._noteId;
	}

	set noteId(value: number)
	{
		this._noteId = value;
	}

	get encounterType(): string
	{
		return this._encounterType;
	}

	set encounterType(value: string)
	{
		this._encounterType = value;
	}
}