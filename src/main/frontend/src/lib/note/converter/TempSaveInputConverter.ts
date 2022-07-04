import AbstractConverter from "../../conversion/AbstractConverter";
import {TempNoteCreateInput} from "../../../../generated";
import TempNoteInput from "../model/TempNoteInput";

export default class TempSaveInputConverter extends AbstractConverter<TempNoteInput, TempNoteCreateInput>
{
	convert(input: TempNoteInput): TempNoteCreateInput
	{
		return {
			noteId: input.noteId,
			note: input.noteText,
			observationDate: this.serializeLocalDateTime(input.observationDate),
			encounterType: input.encounterType,
		} as TempNoteCreateInput;
	}
}