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

angular.module('Record.Summary').component('encounterNote', {
	bindings: {
		note: '<',
		onEditCpp: '&',
		onEditNote: '&',

		minimized: '<',
	},
	templateUrl: "src/record/summary/encounterNoteTemplate.jsp",
	controller: [
		'$scope',
		'$state',
		'$stateParams',
		function ($scope,
		          $state,
		          $stateParams)
	{
		var ctrl = this;

		ctrl.$onInit = function()
		{
			// initialize internal variables
			ctrl.inOpenEdit = false;

			// set default binding values
			ctrl.note =  ctrl.note || {};
			ctrl.onEditCpp =  ctrl.onEditCpp || null;
			ctrl.onEditNote =  ctrl.onEditNote || null;

			ctrl.minimized = ctrl.minimized || false;
		};

		ctrl.$onChanges = function(bindingHash)
		{
			// bindingsHash only has data for changed bindings, so check for object existance
			if(Juno.Common.Util.exists(bindingHash.note))
			{
				ctrl.note = bindingHash.note.currentValue;
			}
			if(Juno.Common.Util.exists(bindingHash.minimized))
			{
				ctrl.minimized = bindingHash.minimized.currentValue;
			}
		};


		ctrl.showNoteViewButton = function showNoteViewButton()
		{
			return (ctrl.note.eformData || ctrl.note.document);
		};

		ctrl.viewButtonClick = function viewButtonClick()
		{
			console.info(ctrl.note.eformData, ctrl.note.document, ctrl.note);
			if(ctrl.note.eformData)
			{
				ctrl.viewEform();
			}
			else if(ctrl.note.document)
			{
				ctrl.viewDocument();
			}
		};

		ctrl.showNoteEditButton = function showNoteEditButton()
		{
			return ctrl.note.editable && ((ctrl.isRegularNote()) || (ctrl.note.cpp && !ctrl.note.archived && !ctrl.note.ticklerNote));
		};

		ctrl.editButtonClick = function editButtonClick()
		{
			if(ctrl.isRegularNote())
			{
				// edit note
				if(angular.isFunction(ctrl.onEditNote))
				{
					ctrl.inOpenEdit = true;
					ctrl.onEditNote({
						note: angular.copy(ctrl.note),
						successCallback: function successCallback(updatedNote)
						{
							ctrl.inOpenEdit = false;
							console.info('callback success', updatedNote);
							// clear the existing properties and replace with the updated ones
							angular.copy(updatedNote, ctrl.note);
						},
						dismissCallback: function dismissCallback(reason)
						{
							ctrl.inOpenEdit = false;
						}
					});
				}
			}
			else if (ctrl.note.cpp)
			{
				// edit group note
				if(angular.isFunction(ctrl.onEditCpp))
				{
					ctrl.inOpenEdit = true;
					ctrl.onEditCpp({
						note: angular.copy(ctrl.note),
						successCallback: function successCallback(updatedNote)
						{
							ctrl.inOpenEdit = false;
							console.info('callback success', updatedNote);
							// clear the existing properties and replace with the updated ones
							angular.copy(updatedNote, ctrl.note);
						},
						dismissCallback: function dismissCallback(reason)
						{
							ctrl.inOpenEdit = false;
						}
					});
				}
			}
		};



		// -----------------------------------------------------------------------------------------------------

		ctrl.toggleMinimizeNote = function toggleMinimizeNote()
		{
			ctrl.minimized = !ctrl.minimized;
		};
		ctrl.isNoteMinimized = function isNoteMinimized()
		{
			return ctrl.minimized;
		};
		ctrl.isNoteExpanded = function isNoteExpanded()
		{
			return !ctrl.isNoteMinimized();
		};

		ctrl.getNoteHeader = function firstLine()
		{
			return  ctrl.note.note.trim().split('\n')[0]; // First line of the note text, split by newline
		};

		ctrl.allowNoteExpansion = function allowNoteExpansion()
		{
			return !(ctrl.note.cpp === true || ctrl.note.document === true || ctrl.note.eformData === true);
		};

		// Returns true if the given note is an unsigned encounter note
		ctrl.isUnsignedEncounterNote = function isUnsignedEncounterNote()
		{
			return (!ctrl.note.isSigned && !ctrl.note.cpp && !ctrl.note.document && !ctrl.note.ticklerNote && !ctrl.note.eformData);
		};

		// Check if note regular note
		ctrl.isRegularNote = function isRegularNote()
		{
			return !(ctrl.note.document ||
				ctrl.note.rxAnnotation ||
				ctrl.note.eformData ||
				ctrl.note.encounterForm ||
				ctrl.note.invoice ||
				ctrl.note.ticklerNote ||
				ctrl.note.cpp);
		};


		ctrl.openRevisionHistory = function openRevisionHistory()
		{
			var win = "revision";
			var url = "../CaseManagementEntry.do?method=notehistory&noteId=" + ctrl.note.noteId;
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};
		ctrl.viewEform = function viewEform(eFormId)
		{
			$state.transitionTo('record.forms.view',
				{
					demographicNo: $stateParams.demographicNo,
					type: 'eform',
					id: eFormId
				},
				{
					location: 'replace',
					notify: true
				});
		};
		ctrl.viewDocument = function viewDocument(documentId)
		{
			// get only document summary items
			let itemArray = summaryLists['incoming'].summaryItem;
			let item = null;

			// find the summary item that matches the document id
			for (let i=0; i < itemArray.length; i++)
			{
				if(itemArray[i].id === documentId) {
					item = itemArray[i];
					break;
				}
			}

			// if we found a matching document, open it
			if(item != null) {
				controller.gotoState(item);
			}
			else
			{
				console.error("item not linked to valid document id:" + documentId);
			}
		};

	}]
});
