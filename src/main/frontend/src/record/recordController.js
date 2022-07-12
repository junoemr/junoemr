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
import MhaConfigService from "../lib/integration/myhealthaccess/service/MhaConfigService";
import MhaPatientService from "../lib/integration/myhealthaccess/service/MhaPatientService";
import {SecurityPermissions} from "../common/security/securityConstants";
import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../common/components/junoComponentConstants";
import {MhaCallPanelEvents} from "./components/mhaCallPanel/mhaCallPanelEvents";
import moment from "moment";
import AppointmentService from "../lib/appointment/service/AppointmentService";
import ToastErrorHandler from "../lib/error/handler/ToastErrorHandler";
import {LogLevel} from "../lib/error/handler/LogLevel";
import TempNoteInput from "../lib/note/model/TempNoteInput";

angular.module('Record').controller('Record.RecordController', [

	'$rootScope',
	'$scope',
	'$window',
	'$http',
	'$httpParamSerializer',
	'$localStorage',
	'$location',
	'$state',
	'$stateParams',
	'$timeout',
	'$interval',
	'$uibModal',
	'demographicService',
	'user',
	'noteService',
	'uxService',
	'securityService',
	'securityRolesService',
	'billingService',
	'focusService',

	function(
		$rootScope,
		$scope,
		$window,
		$http,
		$httpParamSerializer,
		$localStorage,
		$location,
		$state,
		$stateParams,
		$timeout,
		$interval,
		$uibModal,
		demographicService,
		user,
		noteService,
		uxService,
		securityService,
		securityRolesService,
		billingService,
		focusService)
	{
		var controller = this;

		const PATIENT_MESSENGER_NAV_ID = 432543;

		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		$scope.LABEL_POSITION = LABEL_POSITION;

		controller.appointmentService = new AppointmentService();

		controller.demographicNo = $stateParams.demographicNo;
		controller.demographic = null;
		controller.page = {};
		controller.page.assignedCMIssues = [];
		controller.SecurityPermissions = SecurityPermissions;
		controller.errorHandler = new ToastErrorHandler(false, LogLevel.WARN);

		/*
		 * handle concurrent note edit - EditingNoteFlag
		 */
		controller.page.itvSet = null;
		controller.page.itvCheck = null;
		controller.page.editingNoteId = null;
		controller.page.isNoteSaved = false; // Track save state of note TODO-legacy: Potentially add this to the encounterNote object on the backend
		controller.page.currentNoteConfig = {};

		controller.$storage = $localStorage; // Define persistent storage
		controller.working = false;
		controller.noteDirty = false;
		controller.draftSavedDate = null;
		controller.displayPhone = null;
		controller.page.cannotChange = true;
		controller.canMHACallPatient = false;
		controller.mhaCallPanelOpen = false;
		controller.netcareModuleEnabled = false;

		controller.encounterTypeOptions = [
			{
				label: "",
				value: "",
			},
			{
				label: "face to face encounter with client",
				value: "face to face encounter with client",
			},
			{
				label: "telephone encounter with client",
				value: "telephone encounter with client",
			},
			{
				label: "email encounter with client",
				value: "email encounter with client",
			},
			{
				label: "video encounter with client",
				value: "video encounter with client",
			},
			{
				label: "encounter without client",
				value: "encounter without client",
			},
		];

		controller.$onInit = async () =>
		{
			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicRead))
			{
				controller.page.cannotChange = !securityRolesService.hasSecurityPrivileges(SecurityPermissions.EncounterNoteCreate);
				controller.demographic = await demographicService.getDemographic(controller.demographicNo);
			}

			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.EncounterNoteCreate))
			{
				//////AutoSave
				const saveIntervalSeconds = 2;
				let timeout = null;
				controller.skipTmpSave = false;
				controller.noteDirty = false;

				if($stateParams.appointmentNo && securityRolesService.hasSecurityPrivileges(SecurityPermissions.AppointmentRead))
				{
					controller.page.appointment = await controller.appointmentService.getAppointment($stateParams.appointmentNo);
				}

				await controller.getCurrentNote(controller.page.appointment);

				let delayTmpSave = function delayTmpSave(newVal, oldVal)
				{
					if (!controller.skipTmpSave)
					{
						if (newVal !== oldVal)
						{
							controller.noteDirty = true;
							controller.draftSavedDate = null;
							if (timeout)
							{
								$timeout.cancel(timeout);
							}
							timeout = $timeout(controller.saveUpdates, saveIntervalSeconds * 1000);
						}
						else
						{
							controller.noteDirty = false;
						}
					}
					controller.skipTmpSave = false; // only skip once
				};

				controller.checkIfMhaCallAvailable();
				$scope.$watchGroup(["recordCtrl.page.encounterNote.note",
					"recordCtrl.page.encounterNote.encounterType",
					"recordCtrl.page.encounterNote.observationDate",
				], delayTmpSave);

				//////

				//////Timer
				controller.currentDate = new Date(); //the start

				controller.totalSeconds = 0;
				controller.intervalVal = setInterval(setTime, 1000);

				$scope.$on('$destroy', function()
				{
					clearInterval(controller.intervalVal);
					delete $window.onbeforeunload;
				});

			}

			try
			{
				let billingResults = await Promise.all([
					billingService.getBillingRegion(),
					billingService.getDefaultView(),
				]);

				controller.page.billregion = billingResults[0].message;
				controller.page.defaultView = billingResults[1].message;
			}
			catch (e)
			{
				controller.errorHandler.handleError(e);
			}

			//TODO re-enable once conformance is passed and prod credentials set
			controller.netcareModuleEnabled = false; //await netcareService.loadEnabledState();
		}

		controller.noteChanged = () =>
		{
			return (controller.page.encounterNote.note !== controller.page.initNote);
		}

		controller.saveUpdates = async () =>
		{
			if (!controller.noteChanged()) return; //user did not input anything, don't save

			try
			{
				let tmpInput = new TempNoteInput();
				tmpInput.noteId = controller.page.encounterNote.noteId;
				tmpInput.noteText = controller.page.encounterNote.note;
				tmpInput.observationDate = moment(controller.page.encounterNote.observationDate);
				tmpInput.encounterType = controller.page.encounterNote.encounterType;

				await noteService.tempSave($stateParams.demographicNo, tmpInput);

				controller.noteDirty = false;
				controller.draftSavedDate = new moment();
			}
			catch (e)
			{
				controller.errorHandler.handleError(e);
			}
		};

		/**
		 * check if patient has the MHA app & they have a strong enough connection to be called from the eChart.
		 */
		controller.checkIfMhaCallAvailable = async () =>
		{
			const mhaConfigService = new MhaConfigService();
			const mhaPatientService = new MhaPatientService();

			if (await mhaConfigService.mhaEnabled())
			{
				let profiles = await mhaPatientService.profilesForDemographic(controller.demographicNo);
				profiles = profiles.filter((profile) => profile.isConfirmed && profile.hasVoipToken);
				controller.canMHACallPatient = profiles.length > 0;
			}
		}

		controller.canSaveIssues = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.EncounterIssueUpdate)
				|| securityRolesService.hasSecurityPrivileges(SecurityPermissions.EncounterIssueCreate);
		}

		//disable click and keypress if user only has read-access
		controller.checkAction = function checkAction(event)
		{
			if (controller.page.cannotChange)
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		// Is there a shared location where this could be accessed from any controller? i.e. a utils file
		controller.isNaN = function(num)
		{
			return isNaN(num);
		};

		// Check if there have been potential changes to a note, display a warning if needed
		$window.onbeforeunload = function (event)
		{
			if (controller.inUnsavedNoteState())
			{
				return 'You have made changes to a note, but you did not save them yet.\nLeaving the page will revert all changes.';
			}
		};

		// Warn user about unsaved data before a state change
		$scope.$on("$stateChangeStart", function(event, data)
		{
			// If the encounter note is not null/undefined and the new state is not a child of record, continue
			if (controller.inUnsavedNoteState() && data.name.indexOf('record.') === -1)
			{
				controller.saveUpdates(); // force temp-save current note
			}
		});

		controller.inUnsavedNoteState = () =>
		{
			if (Juno.Common.Util.isDefinedAndNotNull(controller.page.encounterNote))
			{
				return (controller.noteDirty);
			}
			return false;
		}

		controller.getCurrentTimerToggle = function getCurrentTimerToggle()
		{
			if (angular.isDefined(controller.intervalVal))
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
				clearInterval(controller.intervalVal);
			}
			else
			{
				$("#aToggle").removeClass("fa-play");
				$("#aToggle").addClass("fa-pause");
				controller.intervalVal = setInterval(setTime, 1000);
			}
		};

		controller.pasteTimer = function pasteTimer()
		{
			var ed = new Date();
			controller.page.encounterNote.note += "\n" + document.getElementById("startTag").value + ": " + controller.currentDate.getHours() + ":" + pad(controller.currentDate.getMinutes()) + "\n" + document.getElementById("endTag").value + ": " + ed.getHours() + ":" + pad(ed.getMinutes()) + "\n" + pad(parseInt(controller.totalSeconds / 3600)) + ":" + pad(parseInt((controller.totalSeconds / 60) % 60)) + ":" + pad(controller.totalSeconds % 60);
		};

		function setTime()
		{
			++(controller.totalSeconds);
			document.getElementById("aTimer").innerHTML = pad(parseInt(controller.totalSeconds / 60)) + ":" + pad(controller.totalSeconds % 60);
			if (controller.totalSeconds === 1200)
			{
				$("#aTimer").css("background-color", "#DFF0D8");
			} //1200 sec = 20 min light green
			if (controller.totalSeconds === 3000)
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

		controller.isWorking = function isWorking()
		{
			return controller.working;
		};

		// Note Input Logic
		controller.toggleNote = function toggleNote()
		{
			if (controller.$storage.hideNote)
			{
				controller.$storage.hideNote = false;
				focusService.focusRef(controller.encounterNoteTextAreaRef);
			}
			else
			{
				controller.$storage.hideNote = true;
			}
		};

		controller.cancelNoteEdit = async () =>
		{
			controller.page.encounterNote = null;
			controller.page.assignedCMIssues = [];
			$scope.$broadcast('stopEditingNote');
			controller.skipTmpSave = true;
			controller.$storage.hideNote = true;
			await noteService.clearTempSave($stateParams.demographicNo);
			await controller.getCurrentNote();
			controller.removeEditingNoteFlag();
		}

		// This is a hack wrapper until we figure out a more sane way to check the DOM for updated content
		// Right now this is being called from anywhere that directly manipulates the DOM
		controller.updateCurrentNote = function updateCurrentNote(note)
		{
			controller.page.encounterNote.note = note;
		};

		controller.saveNote = function saveNote()
		{
			if(controller.isWorking())
			{
				return;
			}
			// Don't let users save an empty note
			if (controller.page.encounterNote.note.length === 0)
			{
				alert("Can't save a blank note!"); // Placeholder error handling
				return;
			}

			controller.working = true;

			// Check if this is a new note, if it isn't, we don't want to overwrite the existing observationDate
			// Need to find a better way of preventing this date overwrite
			controller.page.encounterNote.assignedIssues = controller.page.assignedCMIssues;
			controller.page.encounterNote.issueDescriptions = [];
			if(!Juno.Common.Util.isInArray(user.displayName,  controller.page.encounterNote.editorNames))
				controller.page.encounterNote.editorNames.push(user.displayName);

			for (var i = 0; i < controller.page.assignedCMIssues.length; i++)
			{
				controller.page.encounterNote.issueDescriptions.push(controller.page.assignedCMIssues[i].issue.description);
			}

			noteService.saveNote($stateParams.demographicNo, controller.page.encounterNote).then(
				function success(results)
				{
					controller.page.isNoteSaved = true;
					controller.noteDirty = false;
					$scope.$broadcast('noteSaved', results);
					controller.skipTmpSave = true;
					controller.page.encounterNote = results;
					controller.$storage.hideNote = true;
					controller.getCurrentNote();
					controller.page.assignedCMIssues = [];
					controller.working = false;
				},
				function error(errors)
				{
					console.log(errors);
					controller.working = false;
				});
			controller.removeEditingNoteFlag();
		};

		controller.saveSignNote = function saveSignNote()
		{
			if(controller.isWorking())
			{
				return;
			}
			controller.page.encounterNote.isSigned = true;
			controller.saveNote();
		};

		controller.saveSignVerifyNote = function saveSignVerifyNote()
		{
			if(controller.isWorking())
			{
				return;
			}
			controller.page.encounterNote.isVerified = true;
			controller.page.encounterNote.isSigned = true;
			controller.saveNote();
		};

		controller.saveSignBillNote = function saveSignBillNote()
		{
			if(controller.isWorking())
			{
				return;
			}
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
				var dt = moment(controller.page.appointment.appointmentDate).toDate();
				apptDate = dt.getFullYear() + "-" + Juno.Common.Util.pad0(dt.getMonth() + 1) + "-" + Juno.Common.Util.pad0(dt.getDate());
				dt = new Date(controller.page.appointment.startTime);
				apptStartTime = Juno.Common.Util.pad0(dt.getHours()) + ":" + Juno.Common.Util.pad0(dt.getMinutes()) + ":" + Juno.Common.Util.pad0(dt.getSeconds());
			}

			var url = "../billing.do?billRegion=" + encodeURIComponent(controller.page.billregion);
			url += "&billForm=" + encodeURIComponent(controller.page.defaultView);
			url += "&demographic_name=" + encodeURIComponent(controller.demographic.lastName + "," + controller.demographic.firstName);
			url += "&demographic_no=" + controller.demographicNo;
			url += "&providerview=" + user.providerNo + "&user_no=" + user.providerNo;
			url += "&appointment_no=" + apptNo + "&apptProvider_no=" + apptProvider;
			url += "&appointment_date=" + apptDate + "&start_time=" + apptStartTime;
			url += "&hotclick=&status=t&bNewForm=1" + dxCode;

			window.open(url, "billingWin", "scrollbars=yes, location=no, width=" + screen.width + ", height=" + screen.height);
		};

		controller.getIssueNote = function getIssueNote()
		{
			if (controller.page.encounterNote.noteId != null)
			{
				noteService.getIssueNote(controller.page.encounterNote.noteId).then(
					function success(results)
					{
						if (results != null) controller.page.assignedCMIssues = Juno.Common.Util.toArray(results.assignedCMIssues);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.getCurrentNote = async function getCurrentNote(appointment = null)
		{
			let results = await noteService.getCurrentNote($stateParams.demographicNo, $location.search());
			controller.page.encounterNote = results;
			controller.page.encounterNote.encounterType = Juno.Common.Util.noNull(controller.page.encounterNote.encounterType);
			controller.page.initNote = results.note; //compare this with current note content to determine tmpsave or not
			controller.getIssueNote();
			$scope.$broadcast('currentlyEditingNote', controller.page.encounterNote);
			controller.initAppendNoteEditor(appointment);
			controller.initObservationDate(appointment);
			return results.note;
		};

		controller.editNote = function editNote(note)
		{
			$scope.$broadcast('', note);
		};

		$rootScope.$on('loadNoteForEdit', function(event, data)
		{
			// Check if another note is currently being edited
			if (controller.page.editingNoteId !== null)
			{
				console.log('Note is already being edited! Do you want to save changes?');
				controller.displayWarning(data);
				return;
			}
			controller.removeEditingNoteFlag();

			controller.page.encounterNote = angular.copy(data);
			controller.getIssueNote();

			//Need to check if note has been saved yet.
			controller.$storage.hideNote = false;
			focusService.focusRef(controller.encounterNoteTextAreaRef);
			controller.setEditingNoteFlag();
			$scope.$broadcast('currentlyEditingNote', controller.page.encounterNote);
		});

		$scope.$on('appendToCurrentNote', (event, message) =>
		{
			if(!controller.page.encounterNote)
			{
				controller.page.encounterNote = {
					note: "",
				}
			}
			controller.page.encounterNote.note = controller.page.encounterNote.note + "\n" + message;
			controller.getIssueNote();

			controller.$storage.hideNote = false;
			$scope.$broadcast('currentlyEditingNote', controller.page.encounterNote);

			controller.removeEditingNoteFlag();
		})

		controller.initAppendNoteEditor = function initAppendNoteEditor(appointment = null)
		{
			if ($location.search().noteEditorText != null)
			{
				controller.page.encounterNote.note = controller.page.encounterNote.note + $location.search().noteEditorText;
			}
			else if(appointment && appointment.reason && Juno.Common.Util.isBlank(controller.page.encounterNote.note))
			{
				controller.page.encounterNote.note = "Reason: " + appointment.reason;
			}
		};

		// Initialize the observationDate for new notes
		controller.initObservationDate = function initObservationDate(appointment = null)
		{
			if (controller.page.encounterNote.observationDate === null)
			{
				if(appointment && appointment.appointmentDate)
				{
					controller.page.encounterNote.observationDate = moment(appointment.appointmentDate).toDate();
				}
				else
				{
					controller.page.encounterNote.observationDate = new Date();
				}
			}
		};


		$rootScope.$on("$stateChangeStart", function()
		{
			controller.removeEditingNoteFlag();
		});

		controller.doSetEditingNoteFlag = function doSetEditingNoteFlag()
		{
			noteService.setEditingNoteFlag(controller.page.editingNoteId, user.providerNo).then(
				function success(results)
				{
					if (!results.success)
					{
						if (results.message == "Parameter error") alert("Parameter Error: noteUUID[" + controller.page.editingNoteId + "] userId[" + user.providerNo + "]");
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
			controller.page.isNoteSaved = false;
			controller.page.editingNoteId = controller.page.encounterNote.uuid;
			if (controller.page.itvSet == null)
			{
				controller.page.itvSet = $interval(controller.doSetEditingNoteFlag(), 30000); //set flag every 5 min until canceled
			}
			if (controller.page.itvCheck == null)
			{ //warn once only when the 1st time another user tries to edit this note
				controller.page.itvCheck = $interval(function()
				{
					noteService.checkEditNoteNew(controller.page.editingNoteId, user.providerNo).then(
						function success(results)
						{
							if (!results.success)
							{ //someone else wants to edit this note
								alert("Warning! Another user tries to edit this note. Your update may be replaced by later revision(s).");
								$interval.cancel(controller.page.itvCheck);
								controller.page.itvCheck = null;
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
			if (controller.page.editingNoteId != null)
			{
				noteService.removeEditingNoteFlag(controller.page.editingNoteId, user.providerNo);
				$interval.cancel(controller.page.itvSet);
				$interval.cancel(controller.page.itvCheck);
				controller.page.itvSet = null;
				controller.page.itvCheck = null;
				controller.page.editingNoteId = null;
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

		controller.insertTemplate = async function insertTemplate(item, model, label)
		{
			try
			{
				const results = await uxService.getTemplate({name: model});

				if (results.templates !== null)
				{
					const templateValue = results.templates[0].encounterTemplateValue;
					const currentNote = controller.page.encounterNote.note;
					const cursorIndex = controller.encounterNoteTextAreaRef.prop("selectionStart");

					let newNoteValue;
					// attempt to split the current note on the cursor position. the template will be inserted where the cursor is
					if (!Juno.Common.Util.isBlank(cursorIndex) && currentNote.length > cursorIndex)
					{
						newNoteValue = currentNote.substring(0, cursorIndex) + templateValue + currentNote.substring(cursorIndex);
					}
					else // append template to the end of the note normally
					{
						newNoteValue = (currentNote + "\n" + templateValue).trim();
					}
					controller.page.encounterNote.note = newNoteValue;

					controller.options = {
						magicVal: ''
					};
				}
			}
			catch(errors)
			{
				console.error(errors);
			}
		};

		controller.displayWarning = function displayWarning(noteToEdit)
		{
			var modalInstance = $uibModal.open(
			{
				templateUrl: 'src/record/summary/saveWarning.jsp',
				controller: 'Record.Summary.SaveWarningController as saveWarningCtrl',
				backdrop: 'static',
				size: 'md',
				resolve:
				{
					saveSignNote: function()
					{
						return controller.saveSignNote;
					},
					cancelNoteEdit: function()
					{
						return controller.cancelNoteEdit;
					}
				}
			});

			// Might need to keep this to continue the original edit action
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

		controller.searchIssues = function searchIssues(term)
		{
			console.log('SEARCHING FOR ISSUE: ', term);
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

					$scope.$broadcast('noteIssueAdded');
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.removeIssue = function removeIssue(i)
		{
			console.log('removed issue in record ctrl');
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

			$scope.$broadcast('noteIssueRemoved');
		};

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

				return filterValue;
			};
		};

		/**
		 * open the mha audio call panel.
		 */
		controller.openMhaCallPanel = () =>
		{
			controller.mhaCallPanelOpen = true;
		}

		// close the mha audio call panel.
		$scope.$on(MhaCallPanelEvents.Close, () =>
		{
			controller.mhaCallPanelOpen = false;
		});

		controller.draftSavedMessage = () =>
		{
			if(controller.draftSavedDate)
			{
				return "Draft saved";
			}
			else if (controller.page.encounterNote
				&& controller.page.encounterNote.note
				&& !controller.noteChanged()
				&& controller.page.encounterNote.updateDate)
			{
				return "Draft loaded from " + Juno.Common.Util.formatMomentDateTime(
					moment(controller.page.encounterNote.updateDate),
					Juno.Common.Util.DisplaySettings.dateTimeFormat);
			}
			return "";
		}
		controller.draftSavedTooltip = () =>
		{
			if(controller.draftSavedDate)
			{
				return "Draft saved on " + Juno.Common.Util.formatMomentDateTime(
					controller.draftSavedDate,
					Juno.Common.Util.DisplaySettings.dateTimeFormat);
			}
			return "";
		}

		controller.cancelButtonText = () =>
		{
			if(controller.page.editingNoteId)
			{
				return "Cancel";
			}
			return "Delete";
		}

		controller.cancelButtonTooltip = () =>
		{
			if(controller.page.editingNoteId)
			{
				return "Cancel saved draft";
			}
			return "Deletes saved draft, encounter text, and assigned issues.";
		}
	}
]);