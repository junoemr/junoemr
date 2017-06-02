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

angular.module('Record').controller('Record.RecordController', [

	'$rootScope',
	'$scope',
	'$http',
	'$location',
	'$state',
	'$stateParams',
	'$timeout',
	'$interval',
	'demographicService',
	'demo',
	'user',
	'noteService',
	'uxService',
	'securityService',
	'scheduleService',
	'billingService',

	function(
		$rootScope,
		$scope,
		$http,
		$location,
		$state,
		$stateParams,
		$timeout,
		$interval,
		demographicService,
		demo,
		user,
		noteService,
		uxService,
		securityService,
		scheduleService,
		billingService)
	{


		console.log("in patient Ctrl ", demo);
		console.log("in RecordCtrl state params ", $stateParams, $location.search());

		$scope.demographicNo = $stateParams.demographicNo;
		$scope.demographic = demo;
		$scope.page = {};
		$scope.page.assignedCMIssues = [];

		$scope.hideNote = false;

		//this doesn't actually work, hideNote is note showing up in the $stateParams
		if ($stateParams.hideNote != null)
		{
			$scope.hideNote = $stateParams.hideNote;
		}
		/*
		$scope.recordtabs2 = [ 
		 {id : 0,name : 'Master',url : 'partials/master.html'},
		 {id : 1,name : 'Summary',url : 'partials/summary.html'},
		 {id : 2,name : 'Rx',url : 'partials/rx.jsp'},
		 {id : 3,name : 'Msg',url : 'partials/summary.html'},
		 {id : 4,name : 'Trackers',url : 'partials/tracker.jsp'},
		 {id : 5,name : 'Consults',url : 'partials/summary.html'},
		 {id : 6,name : 'Forms',url : 'partials/formview.html'},
		 {id : 7,name : 'Prevs/Measurements',url : 'partials/summary.html'},
		 {id : 8,name : 'Ticklers',url : 'partials/summary.html'},
		 {id : 9,name : 'MyOscar',url : 'partials/blank.jsp'},
		 {id : 10,name : 'Allergies',url : 'partials/summary.html'},
		 {id : 11,name : 'CPP',url : 'partials/cpp.html'},
		 {id : 12,name : 'Labs/Docs',url : 'partials/labview.html'},
		 {id : 13,name : 'Billing',url : 'partials/billing.jsp'}	
		*/
		$scope.recordtabs2 = [];
		/*
		                 	 {id : 0,displayName : 'Details'  ,path : 'record.details'},
		                 	 {id : 1,displayName : 'Summary'  ,path : 'record.summary'},
		                 	 {id : 2,displayName : 'Forms'    ,path : 'record.forms'},
		                 	 {id : 3,displayName : 'Labs/Docs',path : 'partials/eform.jsp'},
		                 	 {id : 4,displayName : 'Rx'       ,path : 'partials/eform.jsp'}];
		*/

		//get access rights
		securityService.hasRight("_eChart", "w", $scope.demographicNo).then(
			function success(results)
			{
				$scope.page.cannotChange = !results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//disable click and keypress if user only has read-access
		$scope.checkAction = function checkAction(event)
		{
			if ($scope.page.cannotChange)
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		$scope.fillMenu = function fillMenu()
		{
			uxService.menu($stateParams.demographicNo).then(
				function success(results)
				{
					$scope.recordtabs2 = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.fillMenu();

		//var transitionP = $state.transitionTo($scope.recordtabs2[0].path,$stateParams,{location:'replace',notify:true});
		//console.log("transition ",transitionP);

		$scope.changeTab = function changeTab(temp)
		{
			$scope.currenttab2 = $scope.recordtabs2[temp.id];

			if (angular.isDefined(temp.state) && temp.state != null)
			{
				if (/^record.consultRequests.[0-9]+$/.test(temp.state) || /^record.consultResponses.[0-9]+$/.test(temp.state))
				{
					var recIdPos = temp.state.lastIndexOf(".");
					$state.go(temp.state.substring(0, recIdPos),
					{
						demographicNo: temp.state.substring(recIdPos + 1)
					});
				}
				else
				{
					$state.go(temp.state);
				}
			}
			else if (angular.isDefined(temp.url))
			{
				if (temp.label == "Rx")
				{
					win = temp.label + $scope.demographicNo;
				}
				else
				{
					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;
				}
				window.open(temp.url, win, "scrollbars=yes, location=no, width=1000, height=600", "");
			}
			//console.log($scope.recordtabs2[temp].path);


		};

		$scope.isTabActive = function isTabActive(tab)
		{
			//console.log('current state '+$state.current.name.substring(0,tab.path.length)+" -- "+($state.current.name.substring(0,tab.path.length) == tab.path),$state.current.name,tab);
			//console.log('ddd '+$state.current.name.length+"  eee "+tab.path.length);
			//if($state.current.name.length < tab.path.length) return "";

			if (tab.dropdown)
			{
				return "dropdown";
			}

			if (tab.state != null && ($state.current.name.substring(0, tab.state.length) == tab.state))
			{
				return "active";
			}

		};

		$scope.$on('$destroy', function()
		{
			console.log("save the last note!!", $scope.page.encounterNote, noteDirty);
			if (noteDirty)
			{
				noteService.tmpSave($stateParams.demographicNo, $scope.page.encounterNote);
			}

		});

		//////AutoSave
		var saveIntervalSeconds = 2;

		var timeout = null;
		var saveUpdates = function saveUpdates()
		{
			if ($scope.page.encounterNote.note == $scope.page.initNote) return; //user did not input anything, don't save

			console.log("save", $scope.page.encounterNote);
			noteService.tmpSave($stateParams.demographicNo, $scope.page.encounterNote);
		};
		var skipTmpSave = false;
		var noteDirty = false;

		var delayTmpSave = function delayTmpSave(newVal, oldVal)
		{
			console.log("whats the val ", (newVal != oldVal));
			if (!skipTmpSave)
			{
				if (newVal != oldVal)
				{
					noteDirty = true;
					if (timeout)
					{
						$timeout.cancel(timeout);
					}
					timeout = $timeout(saveUpdates, saveIntervalSeconds * 1000);
				}
				else
				{
					noteDirty = false;
				}
			}
			skipTmpSave = false; // only skip once
		};
		$scope.$watch('page.encounterNote.note', delayTmpSave);

		//////

		//////Timer
		var d = new Date(); //the start

		var totalSeconds = 0;
		var myVar = setInterval(setTime, 1000);

		$scope.getCurrentTimerToggle = function getCurrentTimerToggle()
		{
			if (angular.isDefined(myVar))
			{
				return "glyphicon-pause"
			}
			return "glyphicon-play";
		};

		$scope.toggleTimer = function toggleTimer()
		{
			if ($("#aToggle").hasClass("glyphicon-pause"))
			{
				$("#aToggle").removeClass("glyphicon-pause");
				$("#aToggle").addClass("glyphicon-play");
				clearInterval(myVar);
			}
			else
			{
				$("#aToggle").removeClass("glyphicon-play");
				$("#aToggle").addClass("glyphicon-pause");
				myVar = setInterval(setTime, 1000);
			}
		};

		$scope.pasteTimer = function pasteTimer()
		{
			var ed = new Date();
			$scope.page.encounterNote.note += "\n" + document.getElementById("startTag").value + ": " + d.getHours() + ":" + pad(d.getMinutes()) + "\n" + document.getElementById("endTag").value + ": " + ed.getHours() + ":" + pad(ed.getMinutes()) + "\n" + pad(parseInt(totalSeconds / 3600)) + ":" + pad(parseInt((totalSeconds / 60) % 60)) + ":" + pad(totalSeconds % 60);
		};

		function setTime()
		{
			++totalSeconds;
			document.getElementById("aTimer").innerHTML = pad(parseInt(totalSeconds / 60)) + ":" + pad(totalSeconds % 60);
			if (totalSeconds == 1200)
			{
				$("#aTimer").css("background-color", "#DFF0D8");
			} //1200 sec = 20 min light green
			if (totalSeconds == 3000)
			{
				$("#aTimer").css("background-color", "#FDFEC7");
			} //3600 sec = 50 min light yellow
		}

		function pad(val)
		{
			var valString = val + "";
			if (valString.length < 2)
			{
				return "0" + valString;
			}
			else
			{
				return valString;
			}
		}
		$scope.$on('$destroy', function()
		{
			clearInterval(myVar);
		});
		//////		


		// Note Input Logic
		$scope.toggleNote = function()
		{
			if ($scope.hideNote == true)
			{
				$scope.hideNote = false;
			}
			else
			{
				$scope.hideNote = true;
			}
		};

		$scope.moveNote = function(p)
		{
			noteEditor = $("[id^=noteInput]");

			if (p == "l")
			{
				$(noteEditor).removeClass('col-md-offset-3');
				$(noteEditor).removeClass('absolute-right');

				$(noteEditor).addClass('absolute-left');
			}
			else if (p == "r")
			{
				$(noteEditor).removeClass('col-md-offset-3');
				$(noteEditor).removeClass('absolute-left');

				$(noteEditor).addClass('absolute-right');
			}
			else
			{
				$(noteEditor).removeClass('absolute-left');
				$(noteEditor).removeClass('absolute-right');

				$(noteEditor).addClass('col-md-offset-3');
			}
		};

		$scope.saveNote = function()
		{
			console.log("This is the note" + $scope.page.encounterNote);
			$scope.page.encounterNote.observationDate = new Date();
			$scope.page.encounterNote.assignedIssues = $scope.page.assignedCMIssues;
			$scope.page.encounterNote.issueDescriptions = null;
			for (var i = 0; i < $scope.page.assignedCMIssues.length; i++)
			{
				if ($scope.page.encounterNote.issueDescriptions == null)
				{
					$scope.page.encounterNote.issueDescriptions = $scope.page.assignedCMIssues[i].issue.description;
				}
				else
				{
					$scope.page.encounterNote.issueDescriptions += "," + $scope.page.assignedCMIssues[i].issue.description;
				}
			}
			noteService.saveNote($stateParams.demographicNo, $scope.page.encounterNote).then(
				function success(results)
				{
					$rootScope.$emit('noteSaved', results);
					skipTmpSave = true;
					$scope.page.encounterNote = results;
					console.debug('whats the index', results);
					if ($scope.page.encounterNote.isSigned)
					{
						$scope.hideNote = false;
						$scope.getCurrentNote(false);
						$scope.page.assignedCMIssues = [];
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
			$scope.removeEditingNoteFlag();
		};

		$scope.saveSignNote = function saveSignNote()
		{
			$scope.page.encounterNote.isSigned = true;
			$scope.saveNote();
		};

		$scope.saveSignVerifyNote = function saveSignVerifyNote()
		{
			$scope.page.encounterNote.isVerified = true;
			$scope.page.encounterNote.isSigned = true;
			$scope.saveNote();
		};

		billingService.getBillingRegion().then(
			function success(results)
			{
				$scope.page.billregion = results.message;
			},
			function error(errors)
			{
				console.log(errors);
			});
		billingService.getDefaultView().then(
			function success(results)
			{
				$scope.page.defaultView = results.message;
			},
			function error(errors)
			{
				console.log(errors);
			});
		if ($location.search().appointmentNo != null)
		{
			scheduleService.getAppointment($location.search().appointmentNo).then(
				function success(results)
				{
					$scope.page.appointment = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		}

		$scope.saveSignBillNote = function saveSignBillNote()
		{
			$scope.page.encounterNote.isSigned = true;
			$scope.saveNote();

			var dxCode = "";
			for (var i = 0; i < $scope.page.assignedCMIssues.length; i++)
			{
				dxCode += "&dxCode=" + $scope.page.assignedCMIssues[i].issue.code.substring(0, 3);
			}

			var apptNo = "",
				apptProvider = "",
				apptDate = "",
				apptStartTime = "";
			if ($scope.page.appointment != null)
			{
				apptNo = $scope.page.appointment.id;
				apptProvider = $scope.page.appointment.providerNo;

				var dt = new Date($scope.page.appointment.appointmentDate);
				apptDate = dt.getFullYear() + "-" + zero(dt.getMonth() + 1) + "-" + zero(dt.getDate());
				dt = new Date($scope.page.appointment.startTime);
				apptStartTime = zero(dt.getHours()) + ":" + zero(dt.getMinutes()) + ":" + zero(dt.getSeconds());
			}

			var url = "../billing.do?billRegion=" + encodeURIComponent($scope.page.billregion);
			url += "&billForm=" + encodeURIComponent($scope.page.defaultView);
			url += "&demographic_name=" + encodeURIComponent(demo.lastName + "," + demo.firstName);
			url += "&demographic_no=" + demo.demographicNo;
			url += "&providerview=" + user.providerNo + "&user_no=" + user.providerNo;
			url += "&appointment_no=" + apptNo + "&apptProvider_no=" + apptProvider;
			url += "&appointment_date=" + apptDate + "&start_time=" + apptStartTime;
			url += "&hotclick=&status=t&bNewForm=1" + dxCode;

			window.open(url, "billingWin", "scrollbars=yes, location=no, width=1000, height=600", "");
		};


		console.log('RecordCtrlEnd', $state);

		$scope.page.currentNoteConfig = {};


		$scope.getIssueNote = function getIssueNote()
		{
			if ($scope.page.encounterNote.noteId != null)
			{
				noteService.getIssueNote($scope.page.encounterNote.noteId).then(
					function success(results)
					{
						if (results != null) $scope.page.assignedCMIssues = toArray(results.assignedCMIssues);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		$scope.getCurrentNote = function getCurrentNote(showNoteAfterLoadingFlag)
		{
			noteService.getCurrentNote($stateParams.demographicNo, $location.search()).then(
				function success(results)
				{
					$scope.page.encounterNote = results;
					$scope.page.initNote = results.note; //compare this with current note content to determine tmpsave or not
					$scope.getIssueNote();
					console.log($scope.page.encounterNote);
					$scope.hideNote = showNoteAfterLoadingFlag;
					$rootScope.$emit('currentlyEditingNote', $scope.page.encounterNote);
					initAppendNoteEditor();
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.getCurrentNote(true);



		$scope.editNote = function editNote(note)
		{
			$rootScope.$emit('', note);
		};

		$rootScope.$on('loadNoteForEdit', function(event, data)
		{
			console.log('loadNoteForEdit ', data);
			$scope.page.encounterNote = data;
			$scope.getIssueNote();

			//Need to check if note has been saved yet.
			$scope.hideNote = true;
			$rootScope.$emit('currentlyEditingNote', $scope.page.encounterNote);

			$scope.removeEditingNoteFlag();
		});


		var initAppendNoteEditor = function initAppendNoteEditor()
		{
			if ($location.search().noteEditorText != null)
			{
				$scope.page.encounterNote.note = $scope.page.encounterNote.note + $location.search().noteEditorText;
			}
		};

		/*
		 * handle concurrent note edit - EditingNoteFlag
		 */
		var itvSet = null;
		var itvCheck = null;
		var editingNoteId = null;

		$rootScope.$on("$stateChangeStart", function()
		{
			$scope.removeEditingNoteFlag();
		});

		$scope.doSetEditingNoteFlag = function doSetEditingNoteFlag()
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

		$scope.setEditingNoteFlag = function setEditingNoteFlag()
		{
			if ($scope.page.encounterNote.uuid == null) return;

			editingNoteId = $scope.page.encounterNote.uuid;
			if (itvSet == null)
			{
				itvSet = $interval($scope.doSetEditingNoteFlag(), 30000); //set flag every 5 min until canceled
			}
			if (itvCheck == null)
			{ //warn once only when the 1st time another user tries to edit this note
				itvCheck = $interval(function()
				{
					noteService.checkEditNoteNew(editingNoteId, user.providerNo).then(
						function success(results)
						{
							if (!results.success)
							{ //someone else wants to edit this note
								alert("Warning! Another user tries to edit this note. Your update may be replaced by later revision(s).");
								$interval.cancel(itvCheck);
								itvCheck = null;
							}
						},
						function error(errors)
						{
							console.log(errors);
						});
				}, 10000); //check for new edit every 10 seconds
			}
		};

		$scope.removeEditingNoteFlag = function removeEditingNoteFlag()
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


		$scope.searchTemplates = function searchTemplates(term)
		{
			var search = {
				name: term
			};

			return uxService.searchTemplates(search, 0, 25).then(
				function success(results)
				{
					var resp = [];
					for (var x = 0; x < results.templates.length; x++)
					{
						resp.push(
						{
							encounterTemplateName: results.templates[x].encounterTemplateName
						});
					}
					return resp;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.insertTemplate = function insertTemplate(item, model, label)
		{

			uxService.getTemplate(
			{
				name: model
			}).then(
				function success(results)
				{
					if (results.templates != null)
					{
						//	$scope.page.encounterNote.note = $scope.page.encounterNote.note + "\n\n" + results.templates.encounterTemplateValue;
						$scope.page.encounterNote.note = $scope.page.encounterNote.note + results.templates.encounterTemplateValue;
						$scope.options = {
							magicVal: ''
						};
					}

				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.searchIssues = function searchIssues(term)
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
						resp.push(
						{
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

		$scope.assignIssue = function assignIssue(item, model, label)
		{
			for (var x = 0; x < $scope.page.assignedCMIssues.length; x++)
			{
				if ($scope.page.assignedCMIssues[x].issue.id == model)
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
					$scope.page.assignedCMIssues.push(cmIssue);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		$scope.removeIssue = function removeIssue(i)
		{
			i.unchecked = true;
			var newList = [];
			for (var x = 0; x < $scope.page.assignedCMIssues.length; x++)
			{
				if ($scope.page.assignedCMIssues[x].issue_id != i.issue_id)
				{
					newList.push($scope.page.assignedCMIssues[x]);
				}
			}
			$scope.page.assignedCMIssues = newList;
		};


	}
]);

function toArray(obj)
{ //convert single object to array
	if (obj instanceof Array) return obj;
	if (obj == null) return [];
	return [obj];
}

function zero(n)
{
	if (n < 10) n = "0" + n;
	return n;
}