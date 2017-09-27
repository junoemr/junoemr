/*

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

*/
angular.module('Record.Summary').controller('Record.Summary.SummaryController', [

	'$rootScope',
	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'$filter',
	'$uibModal',
	'$interval',
	'user',
	'noteService',
	'summaryService',
	'securityService',

	function(
		$rootScope,
		$scope,
		$http,
		$location,
		$stateParams,
		$state,
		$filter,
		$uibModal,
		$interval,
		user,
		noteService,
		summaryService,
		securityService)
	{

		var controller = this;

		console.log("in summary Ctrl ", $stateParams);

		controller.page = {};
		controller.page.columnOne = {};
		controller.page.columnOne.modules = {};

		controller.page.columnThree = {};
		controller.page.columnThree.modules = {};
		controller.page.selectedNotes = [];

		controller.page.notes = {};
		controller.index = 0;
		controller.page.notes = {};
		controller.page.notes.notelist = [];
		controller.busy = false;
		controller.page.noteFilter = {};
		controller.page.currentFilter = 'none';
		controller.page.onlyNotes = false; // Filter for only showing encounter notes
		controller.page.onlyMine = false; // Filter for only showing notes the current user has created/edited

		controller.demographicNo = $stateParams.demographicNo;

		//get access rights
		securityService.hasRight("_eChart", "r", $stateParams.demographicNo).then(
			function success(results)
			{
				controller.page.canRead = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_eChart", "u", $stateParams.demographicNo).then(
			function success(results)
			{
				controller.page.cannotChange = !results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_eChart", "w", $stateParams.demographicNo).then(
			function success(results)
			{
				controller.page.cannotAdd = !results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//disable click and keypress if user only has read-access
		controller.checkAction = function checkAction(event)
		{
			if (controller.page.cannotChange)
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		controller.openRevisionHistory = function openRevisionHistory(note)
		{
			//var rnd = Math.round(Math.random() * 1000);
			win = "revision";
			var url = "../CaseManagementEntry.do?method=notehistory&noteId=" + note.noteId;
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};

		controller.openRx = function openRx(demoNo)
		{
			win = "Rx" + demoNo;
			var url = "../oscarRx/choosePatient.do?demographicNo=" + demoNo;
			window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
		};

		controller.openAllergies = function openAllergies(demoNo)
		{
			win = "Allergy" + demoNo;
			var url = "../oscarRx/showAllergy.do?demographicNo=" + demoNo;
			window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
			return false;
		};

		controller.openPreventions = function openPreventions(demoNo)
		{
			win = "prevention" + demoNo;
			var url = "../oscarPrevention/index.jsp?demographic_no=" + demoNo;
			window.open(url, win, "scrollbars=yes, location=no, width=900, height=600", "");
			return false;
		};

		//Note display functions
		controller.addMoreItems = function addMoreItems()
		{
			console.log(controller.busy);
			if (controller.busy) return;

			controller.busy = true;

			noteService.getNotesFrom($stateParams.demographicNo, controller.index, 20, controller.page.noteFilter).then(
				function success(results)
				{
					console.log('whats the data', angular.isUndefined(results.notelist), results.notelist);
					if (angular.isDefined(results.notelist))
					{
						//controller.page.notes = data;
						if (results.notelist instanceof Array)
						{
							console.log("ok its in an array", controller.busy);
							for (var i = 0; i < results.notelist.length; i++)
							{
								controller.page.notes.notelist.push(results.notelist[i]);
							}
						}
						else
						{
							controller.page.notes.notelist.push(results.notelist);
						}
						controller.index = controller.page.notes.notelist.length;
					}
					controller.busy = false;
				},
				function error(errors)
				{
					console.log(errors);
					controller.error = errors;
					controller.busy = false;
				}
			);

		};

		controller.addMoreItems();

		controller.editNote = function editNote(note)
		{
			$rootScope.$emit('loadNoteForEdit', note);
		};

		// Call the findGroupNote function and search for the given note, if found, open the groupNote editor
		controller.editGroupNote = function editGroupNote(note)
		{
			var obj = controller.findGroupNote(note);

			if (obj !== null)
			{
				obj.module.editorNames = note.editorNames;
				controller.gotoState(obj.note, obj.module, obj.note.id);
				return;
			}
		};

		// There is probably a better way of doing this
		controller.findGroupNote = function findGroupNote(note)
		{
			var moduleList = controller.page.columnOne.modules;
			for (var i = 0; i < moduleList.length; i++)
			{

				var summaryItems = moduleList[i].summaryItem;
				for (var k = 0; k < summaryItems.length; k++)
				{
					if (summaryItems[k].noteId === note.noteId)
					{
						return {
							note: summaryItems[k],
							module: moduleList[i]
						};
					}
				}
			}
			return null;
		};

		controller.page.currentEditNote = {};

		controller.isNoteBeingEdited = function isNoteBeingEdited(note)
		{

			if (note.uuid === controller.page.currentEditNote.uuid && note.uuid !== null)
			{
				return true;
			}

			return false;
		};

		$rootScope.$on('currentlyEditingNote', function(event, data)
		{
			controller.page.currentEditNote = data;
		});

		// TODO
		$rootScope.$on('stopEditingNote', function()
		{
			controller.page.currentEditNote = {};
		});

		$rootScope.$on('noteSaved', function(event, data)
		{
			console.log('new data coming in', data);
			var noteFound = false;
			for (var notecount = 0; notecount < controller.page.notes.notelist.length; notecount++)
			{
				if (data.uuid == controller.page.notes.notelist[notecount].uuid)
				{
					console.log('uuid ' + data.uuid + ' notecount ' + notecount, data, controller.page.notes.notelist[notecount]);
					controller.page.notes.notelist[notecount] = data;
					noteFound = true;
					break;
				}
			}

			if (noteFound == false)
			{
				controller.page.notes.notelist.unshift(data);
			}
			controller.index = controller.page.notes.notelist.length;
		});

		// Check if note regular note, if not, we must either display the group note edit window or have no edit option
		controller.isRegularNote = function isRegularNote(note)
		{
			if (note.document || note.rxAnnotation || note.eformData || note.encounterForm || note.invoice || note.ticklerNote || note.cpp)
			{
				return false;
			}

			return true;
		};

		//Note display functions
		controller.setColor = function setColor(note)
		{
			if (note.eformData)
			{
				return {
					'border-left-color': '#DFF0D8',
				};
			}
			else if (note.document)
			{
				return {
					'border-left-color': '#617CB2',
				};
			}
			else if (note.rxAnnotation)
			{
				return {
					'border-left-color': '#D3D3D3',
				};
			}
			else if (note.encounterForm)
			{
				return {
					'border-left-color': '#BCAD75',
				};
			}
			else if (note.invoice)
			{
				return {
					'border-left-color': '##FF7272',
				};
			}
			else if (note.ticklerNote)
			{
				return {
					'border-left-color': '#FFA96F',
				};
			}
			else if (note.cpp)
			{
				return {
					'border-left-color': '#9B8166',
				};
			}
		};

		controller.showNoteHeader = function showNoteHeader(note)
		{
			if (controller.page.onlyNotes)
			{
				if (note.document || note.rxAnnotation || note.eformData || note.encounterForm || note.invoice || note.ticklerNote || note.cpp)
				{
					return false;
				}
			}
			return true;
		};

		controller.showNote = function showNote(note)
		{
			if (controller.page.onlyNotes)
			{
				if (note.document || note.rxAnnotation || note.eformData || note.encounterForm || note.invoice || note.ticklerNote || note.cpp)
				{
					return false;
				}
			}

			if(controller.page.onlyMine)
			{
				// Hide note if the current user is not in the list of editors.
				// TODO: Decide later if we want to filter based on this rather than the author alone
				// if (!Juno.Common.Util.isInArray(user.formattedName, note.editorNames))
				// 	return false;

				// Hide the note if the current user's provder number does not match that of the note author
				if (user.providerNo !== note.providerNo)
					return false;
			}

			return true;
		};

		controller.getNoteHeader = function firstLine(noteObj)
		{
			return  noteObj.note.trim().split('\n')[0]; // First line of the note text, split by newline
		};

		controller.trackerUrl = "";

		controller.getTrackerUrl = function getTrackerUrl(demographicNo)
		{
			controller.trackerUrl = '../oscarEncounter/oscarMeasurements/HealthTrackerPage.jspf?template=tracker&demographic_no=' + demographicNo + '&numEle=4&tracker=slim';
		};

		controller.toggleList = function toggleList(mod)
		{

			// If all the items are displayed, reset displaySize to 5 (min), else, show all the items
			if (mod.displaySize >= mod.summaryItem.length)
			{
				mod.displaySize = 5;
			}
			else
			{
				mod.displaySize = mod.summaryItem.length;
			}
		};

		controller.showMoreItems = function showMoreItems(mod)
		{

			if (!angular.isDefined(mod.summaryItem))
			{
				return false;
			}

			if (mod.summaryItem.length == 0)
			{
				return false;
			}

			return true;
		};

		// Return true if a given section is expanded, otherwise return false
		controller.isSectionExpanded = function isSectionExpanded(mod)
		{
			if (mod.displaySize > 5)
			{
				return true;
			}

			return false;
		};

		// Return true if a given section is empty, otherwise return false
		controller.isSectionEmpty = function isSectionEmpty(mod)
		{
			if (mod.summaryItem.length <= 5)
			{
				return true;
			}

			return false;
		};

		// controller.showMoreItemsSymbol = function(mod)
		// {
		// 	if (!angular.isDefined(mod.summaryItem))
		// 	{
		// 		return "";
		// 	}

		// 	if ((mod.displaySize < mod.summaryItem.length) && mod.displaySize == initialDisplayLimit)
		// 	{
		// 		return "glyphicon glyphicon-chevron-down hand-hover pull-right";
		// 	}
		// 	else if ((mod.displaySize == mod.summaryItem.length) && mod.displaySize != initialDisplayLimit)
		// 	{
		// 		return "glyphicon glyphicon-chevron-up hand-hover pull-right";
		// 	}
		// 	else if (mod.summaryItem.length <= initialDisplayLimit)
		// 	{
		// 		return "glyphicon glyphicon-chevron-down glyphicon-chevron-down-disabled pull-right";
		// 	}
		// 	else
		// 	{
		// 		return "";
		// 	}

		// 	if (controller.isSectionExpanded(mod))
		// 	{
		// 		return "glyphicon glyphicon-chevron-up hand-hover pull-right";
		// 	}

		// 	return "glyphicon glyphicon-chevron-down hand-hover pull-right";

		// };

		function getLeftItems()
		{
			summaryService.getSummaryHeaders($stateParams.demographicNo, 'left').then(
				function success(results)
				{
					console.log("left", results);
					controller.page.columnOne.modules = results;
					fillItems(controller.page.columnOne.modules);
				},
				function error(errors)
				{
					console.log(errors);
					controller.error = errors;
				});
		}

		getLeftItems();


		function getRightItems()
		{
			summaryService.getSummaryHeaders($stateParams.demographicNo, 'right').then(
				function success(results)
				{
					console.log("right", results);
					controller.page.columnThree.modules = results;
					fillItems(controller.page.columnThree.modules);
				},
				function error(errors)
				{
					console.log(errors);
					controller.error = errors;
				});
		}

		getRightItems();

		var summaryLists = {};

		function fillItems(itemsToFill)
		{

			for (var i = 0; i < itemsToFill.length; i++)
			{
				summaryLists[itemsToFill[i].summaryCode] = itemsToFill[i];

				summaryService.getFullSummary($stateParams.demographicNo, itemsToFill[i].summaryCode).then(
					function success(results)
					{
						if (angular.isDefined(results.summaryItem))
						{
							if (results.summaryItem instanceof Array)
							{
								summaryLists[results.summaryCode].summaryItem = results.summaryItem;
							}
							else
							{
								summaryLists[results.summaryCode].summaryItem = [results.summaryItem];
							}
						}
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		}


		controller.editGroupedNotes = function editGroupedNotes(size, mod, action)
		{

			var modalInstance = $uibModal.open(
			{
				templateUrl: 'record/summary/groupNotes.jsp',
				controller: 'Record.Summary.GroupNotesController as groupNotesCtrl',
				backdrop: 'static',
				size: size,
				resolve:
				{
					mod: function()
					{
						return mod;
					},
					action: function()
					{
						return action;
					},
					user: function()
					{
						return user;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					console.log(results);
				},
				function error(errors)
				{
					if (editingNoteId != null)
					{
						noteService.removeEditingNoteFlag(editingNoteId, user.providerNo);
						$interval.cancel(itvSet);
						itvSet = null;
						$interval.cancel(itvCheck);
						itvCheck = null;
						editingNoteId = null;
					}
					console.log('Modal dismissed at: ' + new Date());
					console.log(errors);
				});

			console.log($('#myModal'));
		};


		controller.gotoState = function gotoState(item, mod, itemId)
		{
			console.log('ITEM: ', item);
			console.log('MOD: ', mod);

			if (item == "add")
			{
				controller.editGroupedNotes('lg', mod, null);

			}
			else if (item.action == 'add' && item.type == 'dx_reg')
			{

				controller.editGroupedNotes('lg', mod, itemId);

			}
			else if (item.type == 'lab' || item.type == 'document' || item.type == 'rx' || item.type == 'allergy' || item.type == 'prevention' || item.type == 'dsguideline')
			{

				if (item.type == 'rx')
				{
					win = "Rx" + $stateParams.demographicNo;
				}
				else if (item.type == 'allergy')
				{
					win = "Allergy" + $stateParams.demographicNo;
				}
				else if (item.type == 'prevention')
				{
					win = "prevention" + $stateParams.demographicNo;
				}
				else
				{
					//item.type == 'lab' || item.type == 'document'
					//var rnd = Math.round(Math.random() * 1000);
					win = "win_item.type_";
				}

				window.open(item.action, win, "scrollbars=yes, location=no, width=900, height=600", "");
				return false;
			}
			else if (item.action == 'action')
			{
				controller.editGroupedNotes('lg', mod, itemId);

			}
			else
			{
				$state.transitionTo(item.action,
				{
					demographicNo: $stateParams.demographicNo,
					type: item.type,
					id: item.id
				},
				{
					location: 'replace',
					notify: true
				});
			}

		};

		controller.viewEform = function viewEform(eForm)
		{
			$state.transitionTo('record.forms.existing',
				{
					demographicNo: $stateParams.demographicNo,
					type: 'eform',
					id: eForm.noteId
				},
				{
					location: 'replace',
					notify: true
				});
		};


		controller.showPrintModal = function showPrintModal(mod, action)
		{
			var size = 'lg';
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'record/print.jsp',
				controller: 'Record.Summary.RecordPrintController as recordPrintCtrl',
				backdrop: 'static',
				size: size,
				resolve:
				{
					mod: function()
					{
						return mod;
					},

					action: function()
					{
						return action;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					console.log(results);

				},
				function error(errors)
				{
					console.log('Modal dismissed at: ' + new Date());
					console.log(errors);
				});
		};

		// Toggle whether the note is selected for printing
		controller.toggleIsSelectedForPrint = function toggleIsSelectedForPrint(note)
		{
			note.isSelected = !note.isSelected;
		};

	}
]);


var itvSet = null;
var itvCheck = null;
var editingNoteId = null;