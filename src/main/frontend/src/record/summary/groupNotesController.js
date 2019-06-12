angular.module('Record.Summary').controller('Record.Summary.GroupNotesController', [

	'$scope',
	'$uibModal',
	'$uibModalInstance',
	'$stateParams',
	'$state',
	'$interval',
	'mod',
	'action',
	'user',
	'noteService',
	'securityService',
	'diseaseRegistryService',

	function(
		$scope,
		$uibModal,
		$uibModalInstance,
		$stateParams,
		$state,
		$interval,
		mod,
		action,
		user,
		noteService,
		securityService,
		diseaseRegistryService)
	{

		var controller = this;


		controller.page = {};
		controller.page.title = mod.displayName;
		controller.page.items = mod.summaryItem;
		controller.page.quickLists = [];
		
		//controller.action = action;
		controller.page.code = mod.summaryCode;

		controller.groupNotesForm = {
			assignedCMIssues: []
		};
		controller.groupNotesForm.encounterNote = {
			position: 1
		};


		//set hidden which can can move out of hidden to $scope values
		var now = new Date();
		controller.groupNotesForm.annotation_attrib = "anno" + now.getTime();


		//get access rights
		securityService.hasRight("_eChart", "u", $stateParams.demographicNo).then(
			function success(results)
			{
				controller.page.cannotChange = !results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		diseaseRegistryService.getIssueQuickLists().then(
			function success(results)
			{
				console.log(results);
				controller.page.quickLists = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		controller.addDxItem = function addDxItem(item)
		{
			for (var x = 0; x < controller.groupNotesForm.assignedCMIssues.length; x++)
			{
				if (controller.groupNotesForm.assignedCMIssues[x].issue.code === item.code && controller.groupNotesForm.assignedCMIssues[x].issue.type === item.codingSystem)
				{
					return;
				}
			}

			diseaseRegistryService.findDxIssue(item.code, item.codingSystem).then(
				function success(results)
				{
					var cmIssue = {
						acute: false,
						certain: false,
						issue: results,
						issue_id: results.id,
						major: false,
						resolved: false,
						unsaved: true
					};
					controller.groupNotesForm.assignedCMIssues.push(cmIssue);
				},
				function error(errors)
				{
					console.log(errors);
				});


		};

		//disable click and keypress if user only has read-access
		controller.checkAction = function checkAction(event)
		{
			if (controller.page.cannotChange)
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		displayIssueId = function displayIssueId(issueCode)
		{
			noteService.getIssueId(issueCode).then(
				function success(results)
				{
					controller.page.issueId = results.id;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		displayIssueId(controller.page.code);

		displayGroupNote = function displayGroupNote(item, itemId)
		{
			if (controller.page.items[itemId].noteId != null)
			{
				noteService.getIssueNote(controller.page.items[itemId].noteId).then(
					function success(results)
					{
						//controller.master = angular.copy( "iNote----" +  JSON.stringify(iNote) );
						controller.groupNotesForm.encounterNote = results.encounterNote;
						controller.groupNotesForm.encounterNote.editorNames = mod.editorNames; // Get editor names. TODO: Add editor names to the object on the back end
						controller.groupNotesForm.groupNoteExt = results.groupNoteExt;
						controller.groupNotesForm.assignedCMIssues = results.assignedCMIssues;

						controller.groupNotesForm.assignedCMIssues = [];

						if (results.assignedCMIssues instanceof Array)
						{
							controller.groupNotesForm.assignedCMIssues = results.assignedCMIssues;
						}
						else
						{
							if (results.assignedCMIssues != null)
							{
								controller.groupNotesForm.assignedCMIssues.push(results.assignedCMIssues);
							}
						}

						action = itemId;
						controller.setAvailablePositions();

						controller.removeEditingNoteFlag();

						if (controller.groupNotesForm.encounterNote.position < 1)
						{
							controller.groupNotesForm.encounterNote.position = 1;
						}

					},
					function error(errors)
					{
						console.log(errors);
					});
			}
			else if (controller.page.items[itemId].type === "dx_reg")
			{
				controller.groupNotesForm.assignedCMIssues = [];
				var itemExtra = controller.page.items[itemId].extra;
				diseaseRegistryService.findDxIssue(itemExtra.code, itemExtra.codingSystem).then(
					function success(results)
					{
						var cmIssue = {
							acute: false,
							certain: false,
							issue: results,
							issue_id: results.issueId,
							major: false,
							resolved: false,
							unsaved: true
						};
						console.log("find like issue ", cmIssue, results);
						controller.groupNotesForm.assignedCMIssues.push(cmIssue);
						controller.groupNotesForm.encounterNote = {};
						controller.groupNotesForm.groupNoteExt = {};
						controller.groupNotesForm.encounterNote = {
							position: 1
						};
						action = itemId;
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		//action is NULL when new , action is some id when not
		if (action != null)
		{
			displayGroupNote(controller.page.items, action);
		}
		else
		{
			//new entry
		}

		controller.setAvailablePositions = function setAvailablePositions()
		{
			controller.availablePositions = [];
			if (controller.page.items == null || controller.page.items.length == 0)
			{
				controller.availablePositions.push(1);
			}
			else
			{
				var x = 0;
				for (x = 0; x < controller.page.items.length; x++)
				{
					controller.availablePositions.push(x + 1);
				}
				if (action == null)
				{
					controller.availablePositions.push(x + 1);
				}
			}
		};

		controller.setAvailablePositions();

		controller.changeNote = function changeNote(item, itemId)
		{
			return displayGroupNote(item, itemId);
		};

		controller.saveGroupNotes = function saveGroupNotes()
		{
			if (controller.groupNotesForm.encounterNote.noteId == null)
			{
				controller.groupNotesForm.encounterNote.noteId = 0;
			}

			controller.groupNotesForm.encounterNote.cpp = true;
			controller.groupNotesForm.encounterNote.editable = true;
			controller.groupNotesForm.encounterNote.isSigned = true;
			controller.groupNotesForm.encounterNote.observationDate = new Date();
			controller.groupNotesForm.encounterNote.appointmentNo = $stateParams.appointmentNo; //TODO: make this dynamic so it changes on edit
			controller.groupNotesForm.encounterNote.encounterType = "";
			controller.groupNotesForm.encounterNote.encounterTime = "";
			controller.groupNotesForm.encounterNote.assignedIssues = controller.groupNotesForm.assignedCMIssues;
			controller.groupNotesForm.encounterNote.summaryCode = controller.page.code; //'ongoingconcerns';

			noteService.saveIssueNote($stateParams.demographicNo, controller.groupNotesForm).then(
				function success(results)
				{
					$uibModalInstance.dismiss('cancel');
					$state.transitionTo($state.current, $stateParams, {
						reload: false,
						inherit: false,
						notify: true
					});

				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.removeGroupNoteIssue = function removeGroupNoteIssue(i)
		{
			console.log('removing issue');
			i.unchecked = true;
			var newList = [];
			for (var x = 0; x < controller.groupNotesForm.assignedCMIssues.length; x++)
			{
				if (controller.groupNotesForm.assignedCMIssues[x].issue_id != i.issue_id)
				{
					newList.push(controller.groupNotesForm.assignedCMIssues[x]);
				}
			}
			controller.groupNotesForm.assignedCMIssues = newList;
			console.log('NEW LIST: ', newList);
		};

		/*
		 * handle concurrent note edit - EditingNoteFlag
		 */
		controller.doSetEditingNoteFlag = function doSetEditingNoteFlag()
		{
			noteService.setEditingNoteFlag(editingNoteId, user.providerNo).then(
				function success(results)
				{
					if (!results.success)
					{
						if (results.message == "Parameter error") alert("Parameter Error: noteUUID[" + editingNoteId + "] userId[" + user.providerNo + "]");
						else alert("Warning! Another user is editing this note now.");
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.setEditingNoteFlag = function setEditingNoteFlag()
		{
			if (controller.groupNotesForm.encounterNote.uuid == null) return;

			controller.removeEditingNoteFlag(); //remove any previous flag actions
			editingNoteId = controller.groupNotesForm.encounterNote.uuid;

			itvSet = $interval(controller.doSetEditingNoteFlag(), 30000); //set flag every 5 min
			itvCheck = $interval(function()
			{
				noteService.checkEditNoteNew(editingNoteId, user.providerNo).then(
					function success(results)
					{
						if (!results.success)
						{ //someone else wants to edit this note
							alert("Warning! Another user tries to edit this note. Your update may be replaced by later revision(s).");

							//cancel 10sec check after 1st time warning when another user tries to edit this note
							$interval.cancel(itvCheck);
							itvCheck = null;
						}
					},
					function error(errors)
					{
						console.log(errors);
					});
			}, 10000); //check for new edit every 10 sec
		};

		controller.removeEditingNoteFlag = function removeEditingNoteFlag()
		{
			if (editingNoteId != null)
			{
				noteService.removeEditingNoteFlag(editingNoteId, user.providerNo);
				$interval.cancel(itvSet);
				$interval.cancel(itvCheck);
				itvSet = null;
				itvCheck = null;
				editingNoteId = null;
			}
		};


		controller.removeIssue = function removeIssue(i)
		{
			i.unchecked = true;
		};
		controller.restoreIssue = function restoreIssue(i)
		{
			i.unchecked = false;
		};

		controller.archiveGroupNotes = function archiveGroupNotes()
		{
			//controller.master = angular.copy(controller.groupNotesForm);
			controller.groupNotesForm.encounterNote.archived = true;
			controller.saveGroupNotes();
		};

		controller.cancel = function cancel()
		{
			$uibModalInstance.dismiss('cancel');
		};

		//temp load into pop-up
		controller.openRevisionHistory = function openRevisionHistory(encounterNote)
		{
			console.log(controller.groupNotesForm);
			var rnd = Math.round(Math.random() * 1000);
			win = "win" + rnd;
			var url = "../CaseManagementEntry.do?method=notehistory&noteId=" + encounterNote.noteId;
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};

		controller.searchIssues = function searchIssues(term)
		{
			var search = {
				'term': term
			};
			return noteService.searchIssues(search, 0, 100).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.content.length; x++)
					{
						resp.push({
							issueId: results.content[x].id,
							code: results.content[x].description + '(' + results.content[x].code + ')'
						});
					}
					if (results.total > results.content.length)
					{
						//warn user there's more results somehow?
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.assignIssue = function assignIssue(item, model, label)
		{
			for (var x = 0; x < controller.groupNotesForm.assignedCMIssues.length; x++)
			{
				if (controller.groupNotesForm.assignedCMIssues[x].issue.id == model)
				{
					return;
				}
			}

			noteService.getIssue(model).then(
				function success(results)
				{
					var cmIssue = {
						acute: false,
						certain: false,
						issue: results,
						issue_id: item.issueId,
						major: false,
						resolved: false,
						unsaved: true
					};
					controller.groupNotesForm.assignedCMIssues.push(cmIssue);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.isSelected = function isSelected(item)
		{
			if (item.id == action)
			{
				return "group-note-selected";
			}
		};

		controller.addToDxRegistry = function addToDxRegistry(issue)
		{
			diseaseRegistryService.addToDxRegistry($stateParams.demographicNo, issue).then(
				function success(results)
				{
					console.log(results);
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

	}
]);