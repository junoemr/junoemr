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

import {SecurityPermissions} from "../../../../common/security/securityConstants";
import LoadingQueue from "../../../../lib/util/LoadingQueue";
import ToastService from "../../../../lib/alerts/service/ToastService";

angular.module('Record.Summary').component('encounterNoteList', {
	templateUrl: "src/record/summary/components/encounterNoteList/encounterNoteListTemplate.jsp",
	bindings: {
		userId: '<', // current user provider number
		selectedNoteHash: '=',
		onEditCpp: '&',
		onEditNote: '&',
		registerFunctions: '&',
	},
	controller: [
		'$scope',
		'$stateParams',
		'$timeout',
		'noteService',
		'providerService',
		function ($scope,
		          $stateParams,
		          $timeout,
		          noteService,
		          providerService)
	{
		const ctrl = this;

		ctrl.SecurityPermissions = SecurityPermissions;
		ctrl.loadingQueue = new LoadingQueue();
		ctrl.notesPerRequest = 20;

		ctrl.$onInit = async function()
		{
			ctrl.loadingQueue.pushLoadingState();
			// initialize internal variables
			ctrl.filter = {
				onlyNotes: false,
				onlyMine: false,
				textFilter: null,
			};
			ctrl.enableFilterHeader = false; // temporarily remove the filters as they are still being developed

			ctrl.noteList = [];
			ctrl.openNote = {};


			ctrl.index = 0;
			ctrl.moreNotes = true;

			// set default binding values
			ctrl.userId = ctrl.userId || null;
			ctrl.selectedNoteHash = ctrl.selectedNoteHash || {};
			ctrl.onEditCpp = ctrl.onEditCpp || null;
			ctrl.onEditNote = ctrl.onEditNote || null;
			ctrl.registerFunctions = ctrl.registerFunctions || null;

			ctrl.providerSettings = await providerService.getSettings();

			// call this method with functions that the parent is allowed to call.
			if (angular.isFunction(ctrl.registerFunctions))
			{
				ctrl.registerFunctions({
					refresh: ctrl.refresh
				});
			}
			ctrl.loadingQueue.popLoadingState();
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
			//This is a temporary way to hook emits into the old record controller note editing.
			//TODO remove this once the edit note is refactored
			ctrl.openNote = {
				noteId: note.noteId,
				successCallback: successCallback,
				dismissCallback: dismissCallback,
			};
			ctrl.onEditNote({
				note: note,
				successCallback: successCallback,
				dismissCallback: dismissCallback
			});
		};

		$scope.$on('noteSaved', function(event, updatedNote)
		{
			var updateExisting = Juno.Common.Util.exists(ctrl.openNote.noteId) && Number(ctrl.openNote.noteId) > 0;

			if(updateExisting) //Edit note
			{
				if (angular.isFunction(ctrl.openNote.successCallback))
				{
					ctrl.openNote.successCallback(updatedNote);
					ctrl.openNote = {};
				}
			}
			else if (updatedNote.noteId && updatedNote.noteId > 0)
			{
				// force update a note without reference. happens after loading edit from tempsave etc.
				// WARNING: could fail if the note is not already loaded by infinite-scroll
				let noteIndex = ctrl.noteList.findIndex((entry) => entry.noteId === updatedNote.noteId);
				if(noteIndex >= 0) // -1 if not found
				{
					updatedNote.revision = Number(ctrl.noteList[noteIndex].revision) + 1;
					ctrl.noteList[noteIndex] = updatedNote;
				}
			}
			else // add new note
			{
				updatedNote.revision = 1;
				ctrl.noteList.unshift(updatedNote);
			}
		});

		$scope.$on('stopEditingNote', function()
		{
			if(angular.isFunction(ctrl.openNote.dismissCallback))
			{
				ctrl.openNote.dismissCallback();
				ctrl.openNote = {};
			}
		});

		ctrl.onSelectionStateChange = function onSelectionStateChange(note, selected)
		{
			if(selected)
			{
				ctrl.selectedNoteHash[note.noteId] = note;
			}
			else
			{
				delete ctrl.selectedNoteHash[note.noteId];
			}
		};

		// -----------------------------------------------------------------------------------------------------

		ctrl.clearFilters = function clearFilters()
		{
			ctrl.filter.onlyMine = false;
			ctrl.filter.onlyNotes = false;
			ctrl.filter.textFilter = null;
		};

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
			if(!Juno.Common.Util.isBlank(ctrl.filter.textFilter) &&
				(!note.note.toLowerCase().includes(ctrl.filter.textFilter.toLowerCase())))
			{
				return false;
			}
			return !note.deleted;
		};

		ctrl.setNoteMinimized = function setNoteMinimized(note)
		{
			const cmeNoteDate = ctrl.providerSettings.cmeNoteDate;
			let minimizeNote = false;

			// if the note observation date is before the cutoff, minimize the note
			if(Juno.Common.Util.exists(cmeNoteDate) && Juno.Common.Util.isIntegerString(cmeNoteDate))
			{
				// the property stores a negative number, so add to get a past date
				const cutoffDate = moment().add(cmeNoteDate, 'months');
				const noteDate = moment(note.observationDate);
				minimizeNote = (cutoffDate.isAfter(noteDate, 'days'));
			}
			return minimizeNote;
		};

		//Note display functions
		ctrl.addMoreItems = function addMoreItems()
		{
			ctrl.loadingQueue.pushLoadingState();
			ctrl.addMoreItemsAsync().catch((error) =>
			{
				console.error(error);
				new ToastService().errorToast("Error loading additional notes", true);
			}).finally(() =>
			{
				ctrl.loadingQueue.popLoadingState();
			});
		};

		ctrl.addMoreItemsAsync = async () =>
		{
			let results = await noteService.getNotesFrom($stateParams.demographicNo, ctrl.index, ctrl.notesPerRequest, {});

			if (angular.isDefined(results.notelist))
			{
				if (results.notelist instanceof Array)
				{
					for (let i = 0; i < results.notelist.length; i++)
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
			if(angular.isDefined(results.moreNotes))
			{
				ctrl.moreNotes = results.moreNotes;
			}
		}

		ctrl.refresh = function refresh()
		{
			ctrl.index = 0;
			ctrl.noteList = [];
			ctrl.moreNotes = true;
			ctrl.addMoreItems();
		}

	}]
});