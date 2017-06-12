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

		var controller = this;

		console.log("in patient Ctrl ", demo);
		console.log("in RecordCtrl state params ", $stateParams, $location.search());

		controller.demographicNo = $stateParams.demographicNo;
		controller.demographic = demo;
		controller.page = {};
		controller.page.assignedCMIssues = [];

		controller.hideNote = false;

		//this doesn't actually work, hideNote is note showing up in the $stateParams
		if ($stateParams.hideNote != null)
		{
			controller.hideNote = $stateParams.hideNote;
		}
		/*
		controller.recordtabs2 = [ 
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
		controller.recordtabs2 = [];
		/*
		                 	 {id : 0,displayName : 'Details'  ,path : 'record.details'},
		                 	 {id : 1,displayName : 'Summary'  ,path : 'record.summary'},
		                 	 {id : 2,displayName : 'Forms'    ,path : 'record.forms'},
		                 	 {id : 3,displayName : 'Labs/Docs',path : 'partials/eform.jsp'},
		                 	 {id : 4,displayName : 'Rx'       ,path : 'partials/eform.jsp'}];
		*/

		//get access rights
		securityService.hasRight("_eChart", "w", controller.demographicNo).then(
			function success(results)
			{
				controller.page.cannotChange = !results;
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

		controller.fillMenu = function fillMenu()
		{
			uxService.menu($stateParams.demographicNo).then(
				function success(results)
				{
					controller.recordtabs2 = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.fillMenu();

		//var transitionP = $state.transitionTo(controller.recordtabs2[0].path,$stateParams,{location:'replace',notify:true});
		//console.log("transition ",transitionP);

		controller.changeTab = function changeTab(temp)
		{
			controller.currenttab2 = controller.recordtabs2[temp.id];

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
					win = temp.label + controller.demographicNo;
				}
				else
				{
					var rnd = Math.round(Math.random() * 1000);
					win = "win" + rnd;
				}
				window.open(temp.url, win, "scrollbars=yes, location=no, width=1000, height=600", "");
			}
			//console.log(controller.recordtabs2[temp].path);


		};

		controller.isTabActive = function isTabActive(tab)
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
			console.log("save the last note!!", controller.page.encounterNote, noteDirty);
			if (noteDirty)
			{
				noteService.tmpSave($stateParams.demographicNo, controller.page.encounterNote);
			}

		});

		//////AutoSave
		var saveIntervalSeconds = 2;

		var timeout = null;
		var saveUpdates = function saveUpdates()
		{
			if (controller.page.encounterNote.note == controller.page.initNote) return; //user did not input anything, don't save

			console.log("save", controller.page.encounterNote);
			noteService.tmpSave($stateParams.demographicNo, controller.page.encounterNote);
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

		controller.getCurrentTimerToggle = function getCurrentTimerToggle()
		{
			if (angular.isDefined(myVar))
			{
				return "fa-pause";
			}
			return "fa-play";
		};

		controller.toggleTimer = function toggleTimer()
		{
			if ($("#aToggle").hasClass("fa-pause"))
			{
				$("#aToggle").removeClass("fa-pause");
				$("#aToggle").addClass("fa-play");
				clearInterval(myVar);
			}
			else
			{
				$("#aToggle").removeClass("fa-play");
				$("#aToggle").addClass("fa-pause");
				myVar = setInterval(setTime, 1000);
			}
		};

		controller.pasteTimer = function pasteTimer()
		{
			var ed = new Date();
			controller.page.encounterNote.note += "\n" + document.getElementById("startTag").value + ": " + d.getHours() + ":" + pad(d.getMinutes()) + "\n" + document.getElementById("endTag").value + ": " + ed.getHours() + ":" + pad(ed.getMinutes()) + "\n" + pad(parseInt(totalSeconds / 3600)) + ":" + pad(parseInt((totalSeconds / 60) % 60)) + ":" + pad(totalSeconds % 60);
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
		controller.toggleNote = function()
		{
			if (controller.hideNote == true)
			{
				controller.hideNote = false;
			}
			else
			{
				controller.hideNote = true;
			}
		};

		controller.moveNote = function(p)
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

		controller.saveNote = function()
		{
			console.log("This is the note" + controller.page.encounterNote);
			controller.page.encounterNote.observationDate = new Date();
			controller.page.encounterNote.assignedIssues = controller.page.assignedCMIssues;
			controller.page.encounterNote.issueDescriptions = null;
			for (var i = 0; i < controller.page.assignedCMIssues.length; i++)
			{
				if (controller.page.encounterNote.issueDescriptions == null)
				{
					controller.page.encounterNote.issueDescriptions = controller.page.assignedCMIssues[i].issue.description;
				}
				else
				{
					controller.page.encounterNote.issueDescriptions += "," + controller.page.assignedCMIssues[i].issue.description;
				}
			}
			noteService.saveNote($stateParams.demographicNo, controller.page.encounterNote).then(
				function success(results)
				{
					$rootScope.$emit('noteSaved', results);
					skipTmpSave = true;
					controller.page.encounterNote = results;
					console.debug('whats the index', results);
					if (controller.page.encounterNote.isSigned)
					{
						controller.hideNote = false;
						controller.getCurrentNote(false);
						controller.page.assignedCMIssues = [];
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
			controller.removeEditingNoteFlag();
		};

		controller.saveSignNote = function saveSignNote()
		{
			controller.page.encounterNote.isSigned = true;
			controller.saveNote();
		};

		controller.saveSignVerifyNote = function saveSignVerifyNote()
		{
			controller.page.encounterNote.isVerified = true;
			controller.page.encounterNote.isSigned = true;
			controller.saveNote();
		};

		billingService.getBillingRegion().then(
			function success(results)
			{
				controller.page.billregion = results.message;
			},
			function error(errors)
			{
				console.log(errors);
			});
		billingService.getDefaultView().then(
			function success(results)
			{
				controller.page.defaultView = results.message;
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
					controller.page.appointment = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		}

		controller.saveSignBillNote = function saveSignBillNote()
		{
			controller.page.encounterNote.isSigned = true;
			controller.saveNote();

			var dxCode = "";
			for (var i = 0; i < controller.page.assignedCMIssues.length; i++)
			{
				dxCode += "&dxCode=" + controller.page.assignedCMIssues[i].issue.code.substring(0, 3);
			}

			var apptNo = "",
				apptProvider = "",
				apptDate = "",
				apptStartTime = "";
			if (controller.page.appointment != null)
			{
				apptNo = controller.page.appointment.id;
				apptProvider = controller.page.appointment.providerNo;

				var dt = new Date(controller.page.appointment.appointmentDate);
				apptDate = dt.getFullYear() + "-" + zero(dt.getMonth() + 1) + "-" + zero(dt.getDate());
				dt = new Date(controller.page.appointment.startTime);
				apptStartTime = zero(dt.getHours()) + ":" + zero(dt.getMinutes()) + ":" + zero(dt.getSeconds());
			}

			var url = "../billing.do?billRegion=" + encodeURIComponent(controller.page.billregion);
			url += "&billForm=" + encodeURIComponent(controller.page.defaultView);
			url += "&demographic_name=" + encodeURIComponent(demo.lastName + "," + demo.firstName);
			url += "&demographic_no=" + demo.demographicNo;
			url += "&providerview=" + user.providerNo + "&user_no=" + user.providerNo;
			url += "&appointment_no=" + apptNo + "&apptProvider_no=" + apptProvider;
			url += "&appointment_date=" + apptDate + "&start_time=" + apptStartTime;
			url += "&hotclick=&status=t&bNewForm=1" + dxCode;

			window.open(url, "billingWin", "scrollbars=yes, location=no, width=1000, height=600", "");
		};


		console.log('RecordCtrlEnd', $state);

		controller.page.currentNoteConfig = {};


		controller.getIssueNote = function getIssueNote()
		{
			if (controller.page.encounterNote.noteId != null)
			{
				noteService.getIssueNote(controller.page.encounterNote.noteId).then(
					function success(results)
					{
						if (results != null) controller.page.assignedCMIssues = toArray(results.assignedCMIssues);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.getCurrentNote = function getCurrentNote(showNoteAfterLoadingFlag)
		{
			noteService.getCurrentNote($stateParams.demographicNo, $location.search()).then(
				function success(results)
				{
					controller.page.encounterNote = results;
					controller.page.initNote = results.note; //compare this with current note content to determine tmpsave or not
					controller.getIssueNote();
					console.log(controller.page.encounterNote);
					controller.hideNote = showNoteAfterLoadingFlag;
					$rootScope.$emit('currentlyEditingNote', controller.page.encounterNote);
					initAppendNoteEditor();
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.getCurrentNote(true);



		controller.editNote = function editNote(note)
		{
			$rootScope.$emit('', note);
		};

		$rootScope.$on('loadNoteForEdit', function(event, data)
		{
			console.log('loadNoteForEdit ', data);
			controller.page.encounterNote = data;
			controller.getIssueNote();

			//Need to check if note has been saved yet.
			controller.hideNote = true;
			$rootScope.$emit('currentlyEditingNote', controller.page.encounterNote);

			controller.removeEditingNoteFlag();
		});


		var initAppendNoteEditor = function initAppendNoteEditor()
		{
			if ($location.search().noteEditorText != null)
			{
				controller.page.encounterNote.note = controller.page.encounterNote.note + $location.search().noteEditorText;
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
			controller.removeEditingNoteFlag();
		});

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
			if (controller.page.encounterNote.uuid == null) return;

			editingNoteId = controller.page.encounterNote.uuid;
			if (itvSet == null)
			{
				itvSet = $interval(controller.doSetEditingNoteFlag(), 30000); //set flag every 5 min until canceled
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


		controller.searchTemplates = function searchTemplates(term)
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

		controller.insertTemplate = function insertTemplate(item, model, label)
		{

			uxService.getTemplate(
			{
				name: model
			}).then(
				function success(results)
				{
					if (results.templates != null)
					{
						//	controller.page.encounterNote.note = controller.page.encounterNote.note + "\n\n" + results.templates.encounterTemplateValue;
						controller.page.encounterNote.note = controller.page.encounterNote.note + results.templates.encounterTemplateValue;
						controller.options = {
							magicVal: ''
						};
					}

				},
				function error(errors)
				{
					console.log(errors);
				});
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

		controller.assignIssue = function assignIssue(item, model, label)
		{
			for (var x = 0; x < controller.page.assignedCMIssues.length; x++)
			{
				if (controller.page.assignedCMIssues[x].issue.id == model)
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
					controller.page.assignedCMIssues.push(cmIssue);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.removeIssue = function removeIssue(i)
		{
			i.unchecked = true;
			var newList = [];
			for (var x = 0; x < controller.page.assignedCMIssues.length; x++)
			{
				if (controller.page.assignedCMIssues[x].issue_id != i.issue_id)
				{
					newList.push(controller.page.assignedCMIssues[x]);
				}
			}
			controller.page.assignedCMIssues = newList;
		};

		// For some reason Angular does not allow for the evaluation of the inverse of custom filters, thus, we have the the following masterpiece
		// If inverse === false, return true if the given item is supposed to be shown outisde the 'more' dropdown on the medium view
		// If inverse === true, return the inverse of the above statement,
		controller.mediumNavItemFilter = function mediumNavItemFilter(inverse)
		{
			return function(item)
			{
				var labelsToShow = ['Details', 'Summary', 'Forms', 'Tickler', 'Health Tracker', 'Rx', 'Consultations'];
				var filterValue = $.inArray(item.label, labelsToShow) != -1;

				if (inverse === true)
				{
					return !filterValue;
				}
				else
				{
					return filterValue;
				}
			};
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