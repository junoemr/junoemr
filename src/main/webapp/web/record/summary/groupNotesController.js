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


		$scope.page = {};
		$scope.page.title = mod.displayName;
		$scope.page.items = mod.summaryItem;
		$scope.page.quickLists = [];

		//$scope.action = action;
		$scope.page.code = mod.summaryCode;

		$scope.groupNotesForm = {
			assignedCMIssues: []
		};
		$scope.groupNotesForm.encounterNote = {
			position: 1
		};


		//set hidden which can can move out of hidden to $scope values
		var now = new Date();
		$scope.groupNotesForm.annotation_attrib = "anno" + now.getTime();


		//get access rights
		securityService.hasRight("_eChart", "u", $stateParams.demographicNo).then(function(data)
		{
			$scope.page.cannotChange = !data;
		});

		diseaseRegistryService.getQuickLists().then(function(data)
		{
			console.log(data);
			$scope.page.quickLists = data;
		});

		$scope.addDxItem = function(item)
		{
			for (var x = 0; x < $scope.groupNotesForm.assignedCMIssues.length; x++)
			{
				if ($scope.groupNotesForm.assignedCMIssues[x].issue.code === item.code && $scope.groupNotesForm.assignedCMIssues[x].issue.type === item.codingSystem)
				{
					return;
				}
			}

			diseaseRegistryService.findLikeIssue(item).then(function(response)
			{
				var cmIssue = {
					acute: false,
					certain: false,
					issue: response,
					issue_id: response.issueId,
					major: false,
					resolved: false,
					unsaved: true
				};
				$scope.groupNotesForm.assignedCMIssues.push(cmIssue);
			});


		};

		//disable click and keypress if user only has read-access
		$scope.checkAction = function(event)
		{
			if ($scope.page.cannotChange)
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		displayIssueId = function(issueCode)
		{
			noteService.getIssueId(issueCode).then(function(data)
			{
				$scope.page.issueId = data.id;
			}, function(reason)
			{
				alert(reason);
			});
		};

		displayIssueId($scope.page.code);

		displayGroupNote = function(item, itemId)
		{
			console.log('Display note: ', $scope.page.items[itemId].noteId)
			if ($scope.page.items[itemId].noteId != null)
			{
				noteService.getIssueNote($scope.page.items[itemId].noteId).then(function(iNote)
				{
					//$scope.master = angular.copy( "iNote----" +  JSON.stringify(iNote) );
					$scope.groupNotesForm.encounterNote = iNote.encounterNote;
					$scope.groupNotesForm.groupNoteExt = iNote.groupNoteExt;
					$scope.groupNotesForm.assignedCMIssues = iNote.assignedCMIssues;

					$scope.groupNotesForm.assignedCMIssues = [];

					if (iNote.assignedCMIssues instanceof Array)
					{
						$scope.groupNotesForm.assignedCMIssues = iNote.assignedCMIssues;
					}
					else
					{
						if (iNote.assignedCMIssues != null)
						{
							$scope.groupNotesForm.assignedCMIssues.push(iNote.assignedCMIssues);
						}
					}

					action = itemId;
					$scope.setAvailablePositions();

					$scope.removeEditingNoteFlag();

					if ($scope.groupNotesForm.encounterNote.position < 1)
					{
						$scope.groupNotesForm.encounterNote.position = 1;
					}

				}, function(reason)
				{
					alert(reason);
				});
			}
			else if ($scope.page.items[itemId].type === "dx_reg")
			{
				$scope.groupNotesForm.assignedCMIssues = [];
				diseaseRegistryService.findLikeIssue($scope.page.items[itemId].extra).then(function(response)
				{
					var cmIssue = {
						acute: false,
						certain: false,
						issue: response,
						issue_id: response.issueId,
						major: false,
						resolved: false,
						unsaved: true
					};
					console.log("find like issue ", cmIssue, response);
					$scope.groupNotesForm.assignedCMIssues.push(cmIssue);
					$scope.groupNotesForm.encounterNote = {};
					$scope.groupNotesForm.groupNoteExt = {};
					$scope.groupNotesForm.encounterNote = {
						position: 1
					};
					action = itemId;
				});
			}
		};

		//action is NULL when new , action is some id when not
		if (action != null)
		{
			displayGroupNote($scope.page.items, action);
		}
		else
		{
			//new entry
		}

		$scope.setAvailablePositions = function()
		{
			$scope.availablePositions = [];
			if ($scope.page.items == null || $scope.page.items.length == 0)
			{
				$scope.availablePositions.push(1);
			}
			else
			{
				var x = 0;
				for (x = 0; x < $scope.page.items.length; x++)
				{
					$scope.availablePositions.push(x + 1);
				}
				if (action == null)
				{
					$scope.availablePositions.push(x + 1);
				}
			}
		};

		$scope.setAvailablePositions();

		$scope.changeNote = function(item, itemId)
		{
			return displayGroupNote(item, itemId);
		};

		$scope.saveGroupNotes = function()
		{
			if ($scope.groupNotesForm.encounterNote.noteId == null)
			{
				$scope.groupNotesForm.encounterNote.noteId = 0;
			}

			$scope.groupNotesForm.encounterNote.noteId = $scope.groupNotesForm.encounterNote.noteId; //tmp crap
			$scope.groupNotesForm.encounterNote.cpp = true;
			$scope.groupNotesForm.encounterNote.editable = true;
			$scope.groupNotesForm.encounterNote.isSigned = true;
			$scope.groupNotesForm.encounterNote.observationDate = new Date();
			$scope.groupNotesForm.encounterNote.appointmentNo = $stateParams.appointmentNo; //TODO: make this dynamic so it changes on edit
			$scope.groupNotesForm.encounterNote.encounterType = "";
			$scope.groupNotesForm.encounterNote.encounterTime = "";

			$scope.groupNotesForm.encounterNote.summaryCode = $scope.page.code; //'ongoingconcerns';

			$scope.groupNotesForm.assignedIssues = [];

			noteService.saveIssueNote($stateParams.demographicNo, $scope.groupNotesForm).then(function(data)
			{
				$uibModalInstance.dismiss('cancel');
				$state.transitionTo($state.current, $stateParams,
				{
					reload: true,
					inherit: false,
					notify: true
				});

			}, function(reason)
			{
				alert(reason);
			});
		};

		/*
		 * handle concurrent note edit - EditingNoteFlag
		 */
		$scope.doSetEditingNoteFlag = function()
		{
			noteService.setEditingNoteFlag(editingNoteId, user.providerNo).then(function(resp)
			{
				if (!resp.success)
				{
					if (resp.message == "Parameter error") alert("Parameter Error: noteUUID[" + editingNoteId + "] userId[" + user.providerNo + "]");
					else alert("Warning! Another user is editing this note now.");
				}
			});
		};

		$scope.setEditingNoteFlag = function()
		{
			if ($scope.groupNotesForm.encounterNote.uuid == null) return;

			$scope.removeEditingNoteFlag(); //remove any previous flag actions
			editingNoteId = $scope.groupNotesForm.encounterNote.uuid;

			itvSet = $interval($scope.doSetEditingNoteFlag(), 30000); //set flag every 5 min
			itvCheck = $interval(function()
			{
				noteService.checkEditNoteNew(editingNoteId, user.providerNo).then(function(resp)
				{
					if (!resp.success)
					{ //someone else wants to edit this note
						alert("Warning! Another user tries to edit this note. Your update may be replaced by later revision(s).");

						//cancel 10sec check after 1st time warning when another user tries to edit this note
						$interval.cancel(itvCheck);
						itvCheck = null;
					}
				});
			}, 10000); //check for new edit every 10 sec
		};

		$scope.removeEditingNoteFlag = function()
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


		$scope.removeIssue = function(i)
		{
			i.unchecked = true;
		};
		$scope.restoreIssue = function(i)
		{
			i.unchecked = false;
		};

		$scope.archiveGroupNotes = function()
		{
			//$scope.master = angular.copy($scope.groupNotesForm);
			$scope.groupNotesForm.encounterNote.archived = true;
			$scope.saveGroupNotes();
		};

		$scope.cancel = function()
		{
			$uibModalInstance.dismiss('cancel');
		};

		//temp load into pop-up
		$scope.openRevisionHistory = function(note)
		{
			var rnd = Math.round(Math.random() * 1000);
			win = "win" + rnd;
			var url = "../CaseManagementEntry.do?method=notehistory&noteId=" + note.noteId;
			window.open(url, win, "scrollbars=yes, location=no, width=647, height=600", "");
		};

		$scope.searchIssues = function(term)
		{
			var search = {
				'term': term
			};
			return noteService.searchIssues(search, 0, 100).then(function(response)
			{
				var resp = [];
				for (var x = 0; x < response.content.length; x++)
				{
					resp.push(
					{
						issueId: response.content[x].id,
						code: response.content[x].description + '(' + response.content[x].code + ')'
					});
				}
				if (response.total > response.content.length)
				{
					//warn user there's more results somehow?
				}
				return resp;
			});
		};

		$scope.assignIssue = function(item, model, label)
		{
			for (var x = 0; x < $scope.groupNotesForm.assignedCMIssues.length; x++)
			{
				if ($scope.groupNotesForm.assignedCMIssues[x].issue.id == model)
				{
					return;
				}
			}

			noteService.getIssue(model).then(function(response)
			{
				var cmIssue = {
					acute: false,
					certain: false,
					issue: response,
					issue_id: item.issueId,
					major: false,
					resolved: false,
					unsaved: true
				};
				$scope.groupNotesForm.assignedCMIssues.push(cmIssue);
			});
		};

		$scope.isSelected = function(item)
		{
			if (item.id == action)
			{
				return "group-note-selected";
			}
		};

		$scope.addToDxRegistry = function(issue)
		{
			diseaseRegistryService.addToDxRegistry($stateParams.demographicNo, issue).then(function(data)
			{
				console.log(data);
			});

		};

	}
]);