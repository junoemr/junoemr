/*

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
	This software is published under the GPL GNU General Public License.
	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

 */

angular.module('Record.Summary').component('encounterNoteList', {
	bindings: {
		userId: '<', // current user provider number
		onEditCpp: '&',
		onEditNote: '&',
	},
	templateUrl: "src/record/summary/encounterNoteListTemplate.jsp",
	controller: [
		'$stateParams',
		'noteService',
		function ($stateParams,
		          noteService)
	{
		var ctrl = this;

		ctrl.$onInit = function()
		{
			// initialize internal variables
			ctrl.filter = {
				onlyNotes: false,
				onlyMine: false,
				textFilter: null,
			};

			ctrl.noteList = [];

			ctrl.page = {
				currentEditNote: {},
			};

			// set default binding values
			ctrl.userId =  ctrl.userId || null;
			ctrl.onEditCpp =  ctrl.onEditCpp || null;
			ctrl.onEditNote =  ctrl.onEditNote || null;

			ctrl.addMoreItems();

		};

		ctrl.$onChanges = function(bindingHash)
		{
			// bindingsHash only has data for changed bindings, so check for object existance
			if(Juno.Common.Util.exists(bindingHash.noteList))
			{
				// ctrl.noteList = bindingHash.noteList.currentValue;
			}
		};

		ctrl.bubbleUpEditCppCallback = function bubbleUpEditCppCallback(note, successCallback, dismissCallback)
		{
			ctrl.onEditCpp({
				note: note,
				successCallback: successCallback,
				dismissCallback: dismissCallback
			});
		};
		ctrl.bubbleUpEditNoteCallback = function bubbleUpEditNoteCallback(note, successCallback, dismissCallback)
		{
			ctrl.onEditNote({
				note: note,
				successCallback: successCallback,
				dismissCallback: dismissCallback
			});
		};

		// -----------------------------------------------------------------------------------------------------

		ctrl.showNote = function showNote(note)
		{
			if (ctrl.filter.onlyNotes)
			{
				if (note.document || note.rxAnnotation || note.eformData || note.encounterForm || note.invoice || note.ticklerNote || note.cpp)
				{
					return false;
				}
			}

			if(ctrl.filter.onlyMine)
			{
				// Hide note if the current user is not in the list of editors.
				// TODO: Decide later if we want to filter based on this rather than the author alone
				// if (!Juno.Common.Util.isInArray(user.formattedName, note.editorNames))
				// 	return false;

				// Hide the note if the current user's provder number does not match that of the note author
				if (ctrl.userId !== note.providerNo)
					return false;
			}
			return !note.deleted;
		};

		//Note display functions
		ctrl.addMoreItems = function addMoreItems()
		{
			if (ctrl.busy) return;

			ctrl.busy = true;

			noteService.getNotesFrom($stateParams.demographicNo, ctrl.index, 20, ctrl.page.noteFilter).then(
				function success(results)
				{
					if (angular.isDefined(results.notelist))
					{
						//controller.page.notes = data;
						if (results.notelist instanceof Array)
						{
							for (var i = 0; i < results.notelist.length; i++)
							{
								ctrl.noteList.push(results.notelist[i]);
							}
						}
						else
						{
							ctrl.noteList.push(results.notelist);
						}
						ctrl.index = ctrl.noteList.length;
					}
					ctrl.busy = false;
				},
				function error(errors)
				{
					console.log(errors);
					ctrl.error = errors;
					ctrl.busy = false;
				}
			);

		};

	}]
});