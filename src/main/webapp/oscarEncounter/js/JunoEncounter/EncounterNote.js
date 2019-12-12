'use strict';

if (!window.Juno) window.Juno = {};
if (!Juno.OscarEncounter) Juno.OscarEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter) Juno.OscarEncounter.JunoEncounter = {};
if (!Juno.OscarEncounter.JunoEncounter.EncounterNote) Juno.OscarEncounter.JunoEncounter.EncounterNote = {};

var me = Juno.OscarEncounter.JunoEncounter.EncounterNote;

me.pasteToEncounterNote = function pasteToEncounterNote(txt)
{
	var currentlyEditedNoteId = jQuery('input#editNoteId').val();
	var currentTextAreaId = "caseNote_note" + currentlyEditedNoteId;

	$(currentTextAreaId).value += "\n" + txt;
	adjustCaseNote();
	setCaretPosition($(currentTextAreaId), $(currentTextAreaId).value.length);

};

me.updateNoteInPageState = function updateNoteInPageState(noteData, assignedIssueArray)
{
	pageState.currentNoteData = jQuery.extend(true, {}, noteData);
	pageState.currentAssignedCMIssues = jQuery.extend(true, [], assignedIssueArray);
};
