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
	'formService',

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
		securityService,
		formService)
	{

		var controller = this;

		controller.page = {};
		controller.page.columnOne = {};
		controller.page.columnOne.modules = {};

		controller.page.columnThree = {};
		controller.page.columnThree.modules = {};
		controller.page.selectedNoteHash = {};

		controller.index = 0;
		controller.busy = false;

		controller.demographicNo = $stateParams.demographicNo;
		controller.user = user;

		// store the child component refresh function so that this controller can trigger it.
		controller.noteListComponentRefreshFunction = null;

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

		controller.openAddForms = function openForms()
		{
			// open forms tab with "Library" list selected
			$state.go('record.forms.add');
		};

		controller.openCompletedForms = function()
		{
			$state.go('record.forms.completed');
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

		controller.bubbleUpEditNoteCallback = function bubbleUpEditNoteCallback(note, successCallback, dismissCallback)
		{
			//TODO-legacy open record controller note edit without emit?
			$scope.$emit('loadNoteForEdit', note);
		};

		controller.trackerUrl = "";

		controller.getTrackerUrl = function getTrackerUrl(demographicNo)
		{
			controller.trackerUrl = '../oscarEncounter/oscarMeasurements/HealthTrackerPage.jspf?template=tracker&demographic_no=' + demographicNo + '&numEle=4&tracker=slim';
		};

		function getLeftItems()
		{
			summaryService.getSummaryHeaders($stateParams.demographicNo, 'left').then(
				function success(results)
				{
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

		controller.onEditCpp = function(note, successCallback, dismissCallback)
		{
			var obj = controller.findGroupNote(note);
			if (obj === null)
			{
				return;
			}

			var modalInstance = $uibModal.open(
				{
					templateUrl: 'src/record/summary/groupNotes.jsp',
					controller: 'Record.Summary.GroupNotesController as groupNotesCtrl',
					backdrop: 'static',
					windowClass: 'notesModal',
					size: 'lg',
					resolve:
						{
							mod: function()
							{
								return obj.module;
							},
							action: function()
							{
								return 0;
							},
							user: function()
							{
								return controller.user;
							},
							note: function()
							{
								return note;
							}
						}
				});

			modalInstance.result.then(successCallback, dismissCallback);
		};

		controller.editGroupedNotes = function editGroupedNotes(size, mod, action, successCallback, dismissCallback)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/record/summary/groupNotes.jsp',
				controller: 'Record.Summary.GroupNotesController as groupNotesCtrl',
				backdrop: 'static',
				windowClass: 'notesModal',
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
						return controller.user;
					}
				}
			});


			modalInstance.result.then(
				function success(results)
				{
					// trigger the callback
					if(angular.isFunction(successCallback))
					{
						successCallback(results);
					}

					// refresh the main note list
					controller.refreshModel();
				},
				function dismiss(reason)
				{
					if (angular.isFunction(dismissCallback))
					{
						dismissCallback(reason);
					}
				}
			);
		};

		// refresh the data model for the page
		controller.refreshModel = function()
		{
			// refresh the main note list
			if(angular.isFunction(controller.noteListComponentRefreshFunction))
			{
				controller.noteListComponentRefreshFunction();
			}
			getLeftItems();
			getRightItems();
		};

		$scope.$on('summary_page_refresh', function (refresh)
		{
			if (refresh)
			{
				controller.refreshModel();
			}
		});

		//TODO-legacy I would really like to refactor this out
		controller.gotoState = function gotoState(item, mod, successCallback, dismissCallback)
		{
			if (item == "add")
			{
				controller.editGroupedNotes('md', mod, null, successCallback, dismissCallback);

			}
			else if (item.action == 'add' && item.type == 'dx_reg')
			{

				controller.editGroupedNotes('lg', mod, item.id, successCallback, dismissCallback);

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
			else if (item.type === 'eform')
			{
				formService.openEFormInstancePopup($stateParams.demographicNo, item.id).then(function (val)
				{
					controller.refreshModel();
				});
			}
			else if (item.type === 'form')
			{
				formService.openFormInstancePopup(item.displayName, $stateParams.demographicNo, null, item.id).then(function (val)
				{
					controller.refreshModel();
				});
			}
			else if (item.action == 'action')
			{
				controller.editGroupedNotes('lg', mod, item.id, successCallback, dismissCallback);

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

		controller.viewEform = function viewEform(eFormId)
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
		controller.viewDocument = function viewDocument(documentId)
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


		controller.showPrintModal = function showPrintModal()
		{
			console.info(controller.page.selectedNoteHash);

			var selectedNoteList = [];

			Object.keys(controller.page.selectedNoteHash).forEach(function (key) {
				var note = controller.page.selectedNoteHash[key];

				selectedNoteList.push(note.noteId);
				// iteration code
			});

			console.info(selectedNoteList);

			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/record/print.jsp',
				controller: 'Record.Summary.RecordPrintController as recordPrintCtrl',
				backdrop: 'static',
				size: 'lg',
				resolve:
				{
					selectedNoteList: function()
					{
						return selectedNoteList;
					},
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					console.log(results);

				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.onSummaryModAdd = function onSummaryModAdd(module, successCallback, dismissCallback)
		{
			if (module.summaryCode === 'othermeds' ||
				module.summaryCode === 'ongoingconcerns' ||
				module.summaryCode === 'medhx' ||
				module.summaryCode === 'sochx' ||
				module.summaryCode === 'famhx' ||
				module.summaryCode === 'reminders' ||
				module.summaryCode === 'riskfactors')
			{
				controller.editGroupedNotes('md', module, null, successCallback, dismissCallback);
			}
			else if (module.summaryCode === 'meds')
			{
				controller.openRx(controller.demographicNo);
			}
			else if (module.summaryCode === 'allergies')
			{
				controller.openAllergies(controller.demographicNo);
			}
			else if (module.summaryCode === 'forms')
			{
				controller.openAddForms();
			}
			else if (module.summaryCode === 'preventions')
			{
				controller.openPreventions(controller.demographicNo);
			}
		};

		controller.onSummaryModClickTitle = function (module)
		{
			if (module.summaryCode === "forms")
			{
				controller.openCompletedForms()
			}
		};

		controller.isModTitleClickable = function(module)
		{
			return module.summaryCode === "forms";
		};

		// determine if the date should be show for a particular summary module
		controller.hideSummaryModuleDate = function(module)
		{
			switch (module.summaryCode)
			{
				case "meds":
				case "othermeds":
				case "ongoingconcerns":
				case "medhx":
				case "sochx":
				case "famhx":
				case "reminders":
				case "riskfactors":
				case "allergies":
					return true;
				default:
					return false;
			}
		};

		// called when a child component is initialized. this allows the controller to call select child methods
		controller.registerEncNoteListFunctions = function(refresh)
		{
			controller.noteListComponentRefreshFunction = refresh;
		}
	}
]);