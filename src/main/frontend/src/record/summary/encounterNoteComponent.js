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

import {SecurityPermissions} from "../../common/security/securityConstants";

angular.module('Record.Summary').component('encounterNote', {
	templateUrl: "src/record/summary/encounterNoteTemplate.jsp",
	bindings: {
		note: '<?',
		onEditCpp: '&?',
		onEditNote: '&?',
		onToggleSelect: '&?',

		minimized: '<?',
		selectedForPrint: '<?',
	},
	controller: [
		'$scope',
		'$state',
		'$stateParams',
		'formService',
		'securityRolesService',
		function ($scope,
		          $state,
		          $stateParams,
				  formService,
				  securityRolesService)
	{
		var ctrl = this;

		ctrl.displayDateFormat = Object.freeze(Juno.Common.Util.DisplaySettings.dateFormat);

		ctrl.$onInit = function()
		{
			// initialize internal variables
			ctrl.inOpenEdit = false;

			// set default binding values
			ctrl.note =  ctrl.note || {};
			ctrl.onEditCpp =  ctrl.onEditCpp || null;
			ctrl.onEditNote =  ctrl.onEditNote || null;
			ctrl.onToggleSelect =  ctrl.onToggleSelect || null;

			ctrl.minimized = ctrl.minimized || false;
			ctrl.selectedForPrint = ctrl.selectedForPrint || false;
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
			return (ctrl.note.eformData || ctrl.note.document || ctrl.note.encounterForm);
		};

		ctrl.viewButtonClick = function viewButtonClick()
		{
			if(ctrl.note.eformData && ctrl.note.eformDataId)
			{
				formService.openEFormInstancePopup($stateParams.demographicNo, ctrl.note.eformDataId).then(function () {
					$scope.$emit('summary_page_refresh', true);
				});
			}
			else if(ctrl.note.document && ctrl.note.documentId)
			{
				ctrl.viewDocument(ctrl.note.documentId);
			}
			if (ctrl.note.encounterForm)
			{
				formService.openFormInstancePopup(ctrl.note.note, $stateParams.demographicNo, null, ctrl.note.encounterFormId).then(function () {
					$scope.$emit('summary_page_refresh', true);
				});
			}

		};

		ctrl.showNoteEditButton = function showNoteEditButton()
		{
			return ctrl.note.editable && ((ctrl.isRegularNote()) || (ctrl.note.cpp && !ctrl.note.archived));
		};

		ctrl.editButtonEnabled = () =>
		{
			// require create permissions for now, since the backend has no PUT operations for notes
			return (ctrl.isRegularNote() && securityRolesService.hasSecurityPrivileges(SecurityPermissions.EncounterNoteCreate)
				|| (ctrl.note.cpp && securityRolesService.hasSecurityPrivileges(SecurityPermissions.CppNoteCreate)));
		}

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
							console.debug('callback success', updatedNote);
							// clear the existing properties and replace with the updated ones
							angular.copy(updatedNote, ctrl.note);
							ctrl.note.revision = Number(ctrl.note.revision) + 1;
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
							console.debug('callback success', updatedNote);
							// clear the existing properties and replace with the updated ones
							angular.copy(updatedNote, ctrl.note);
							ctrl.note.revision = Number(ctrl.note.revision) + 1;
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

		ctrl.toggleIsSelectedForPrint = function toggleIsSelectedForPrint()
		{
			ctrl.selectedForPrint = !ctrl.selectedForPrint;
			if(angular.isFunction(ctrl.onToggleSelect))
			{
				ctrl.onToggleSelect({
					note: ctrl.note,
					selected: ctrl.selectedForPrint,
				});
			}
		};
		ctrl.isSelectedForPrint = function isSelectedForPrint()
		{
			return ctrl.selectedForPrint;
		};

		ctrl.getNoteHeader = function firstLine()
		{
			if(ctrl.note && ctrl.note.note)
			{
				return ctrl.note.note.trim().split('\n')[0]; // First line of the note text, split by newline
			}
			return "";
		};

		ctrl.allowNoteExpansion = function allowNoteExpansion()
		{
			return !(ctrl.note.cpp === true || ctrl.note.document === true || ctrl.note.eformData === true || ctrl.note.encounterForm);
		};

		// Returns true if the given note is an unsigned encounter note
		ctrl.isUnsignedEncounterNote = function isUnsignedEncounterNote()
		{
			return (!ctrl.note.isSigned && !ctrl.note.cpp && !ctrl.note.document && !ctrl.note.ticklerNote && !ctrl.note.eformData && !ctrl.note.encounterForm);
		};

		// Check if note regular note
		ctrl.isRegularNote = function isRegularNote()
		{
			return !(ctrl.note.document ||
				ctrl.note.rxAnnotation ||
				ctrl.note.eformData ||
				ctrl.note.encounterForm ||
				ctrl.note.invoice ||
				ctrl.note.cpp);
		};


		ctrl.openRevisionHistory = function openRevisionHistory()
		{
			var win = "revision";
			var url = "../CaseManagementEntry.do?method=notehistory&noteId=" + ctrl.note.noteId;
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};
		ctrl.viewDocument = function viewDocument(documentId)
		{
			var win = "revision";
			var url = "../dms/showDocument.jsp" +
				"?inWindow=true" +
				"&segmentID=" + documentId +
				"&status=A";
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};

	}]
});
