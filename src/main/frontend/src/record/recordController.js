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
import {AppointmentApi} from "../../generated/api/AppointmentApi";
import MhaConfigService from "../lib/integration/myhealthaccess/service/MhaConfigService";
import MhaPatientService from "../lib/integration/myhealthaccess/service/MhaPatientService";
import MessagingServiceFactory from "../lib/messaging/factory/MessagingServiceFactory";
import {MessagingServiceType} from "../lib/messaging/model/MessagingServiceType";
import {MessageGroup} from "../lib/messaging/model/MessageGroup";

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
	'demo',
	'user',
	'noteService',
	'uxService',
	'securityService',
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
		demo,
		user,
		noteService,
		uxService,
		securityService,
		billingService,
		focusService)
	{

		var controller = this;

		const PATIENT_MESSENGER_NAV_ID = 432543;

		controller.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs');

		controller.demographicNo = $stateParams.demographicNo;
		controller.demographic = demo;
		controller.page = {};
		controller.page.assignedCMIssues = [];

		/*
		 * handle concurrent note edit - EditingNoteFlag
		 */
		controller.page.itvSet = null;
		controller.page.itvCheck = null;
		controller.page.editingNoteId = null;
		controller.page.isNoteSaved = false; // Track save state of note TODO-legacy: Potentially add this to the encounterNote object on the backend

		controller.$storage = $localStorage; // Define persistent storage
		controller.recordtabs2 = [];
		controller.working = false;

		controller.displayPhone = null;

		// phone related constants
		controller.phone = {
			cellExtKey: "demo_cell",
			workPhoneExtensionKey: "wPhoneExt",
			homePhoneExtensionKey: "hPhoneExt",
			preferredIndicator: "*",
		};

		controller.init = function init()
		{
			controller.fillMenu();
			controller.loadPreferredPhone(controller.demographic);
		};

		// quick and dirty way to show preferred phone
		controller.loadPreferredPhone = (demographic) =>
		{
			// default is home phone
			controller.displayPhone = controller.formatPhone(
				demographic.phone,
				controller.findExtValue(demographic, controller.phone.homePhoneExtensionKey));

			// check work phone
			if(demographic.alternativePhone && demographic.alternativePhone.endsWith(controller.phone.preferredIndicator))
			{
				controller.displayPhone = controller.formatPhone(
					demographic.alternativePhone,
					controller.findExtValue(demographic, controller.phone.workPhoneExtensionKey));
			}
			else  // check cell
			{
				const cellExtValue = controller.findExtValue(demographic, controller.phone.cellExtKey);
				if(cellExtValue && cellExtValue.endsWith(controller.phone.preferredIndicator))
				{
					controller.displayPhone = controller.formatPhone(cellExtValue);
				}
			}
		}

		controller.formatPhone = (number, extension = null) =>
		{
			let formatted = Juno.Common.Util.toTrimmedString(number).replace(controller.phone.preferredIndicator, "");
			if(!Juno.Common.Util.isBlank(extension))
			{
				formatted += " Ext: " + Juno.Common.Util.toTrimmedString(extension);
			}
			return formatted;
		}

		controller.findExtValue = (demographic, key) =>
		{
			let value = null;
			if(demographic.extras)
			{
				const ext = demographic.extras.find((extra) => extra.key === key);
				if(ext)
				{
					value = ext.value;
				}
			}
			return value;
		}


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

		// Is there a shared location where this could be accessed from any controller? i.e. a utils file
		controller.isNaN = function(num)
		{
			return isNaN(num);
		};

		controller.fillMenu = function fillMenu()
		{
			uxService.menu($stateParams.demographicNo).then(
				function success(results)
				{
					controller.recordtabs2 = results;
					controller.addMessengerToMenu(controller.recordtabs2);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		/**
		 * load messenger nav item & add it to the menu.
		 * Cannot be done on the backend because checking MHA status in a blocking manner (on the backend)
		 * will slow page load.
		 * @param navItems - the nav item array
		 */
		controller.addMessengerToMenu = async (navItems) =>
		{
			const mhaConfigService = new MhaConfigService();
			const mhaPatientService = new MhaPatientService();

			const navItem = {
				id: navItems[navItems.length - 1].id + 1,
				label: "Messenger",
				state: ["record.messaging.view", "record.messaging.view.message"],
				dropdown: true,
				dropdownItems: [],
			};

			// TODO Robert's security check here
			// add patient messenger item only if the patient is verified.
			if (await mhaConfigService.mhaEnabled())
			{
				const mhaProfiles = await mhaPatientService.profilesForDemographic($stateParams.demographicNo);
				const verified = mhaProfiles.reduce((verified, profile) => verified || profile.isVerified, false);

				if (verified)
				{
					const verifiedProfile = mhaProfiles.find((profile) => profile.isVerified);
					const messagingService = MessagingServiceFactory.build(MessagingServiceType.MHA_CLINIC);

					navItem.dropdownItems.push({
						id: PATIENT_MESSENGER_NAV_ID,
						label: "Patient Messenger",
						popup: false,
						openNewWindow: false,
						custom_state: {
							state: "record.messaging.view",
							params: {
								backend: MessagingServiceType.MHA_CLINIC,
								source: (await messagingService.getDefaultMessageSource()).id,
								group: MessageGroup.Received,
								messageableId: verifiedProfile.id,
								recordPageEmbedded: true,
							}
						},
					})
				}
			}

			// TODO Robert's security check here
			navItem.dropdownItems.push({
				id: 1,
				label: "Internal Messenger",
				url: `../oscarMessenger/DisplayDemographicMessages.do?orderby=date&boxType=3&demographic_no=${$stateParams.demographicNo}`
			});

			navItems.push(navItem);
			$scope.$apply();
		}

		controller.changeTab = function changeTab(temp)
		{
			controller.currenttab2 = controller.recordtabs2[temp.id];

			if (Juno.Common.Util.isDefinedAndNotNull(temp.state))
			{
				if(Juno.Common.Util.isDefinedAndNotNull(temp.demoId)){
					$state.go(temp.state[0],
						{
							demographicNo: temp.demoId
						});
				}
				else
				{
					$state.go(temp.state[0]);
				}
			}
			else
			{
				switch (temp.id)
				{
					case PATIENT_MESSENGER_NAV_ID:
						$state.go(temp.custom_state.state, temp.custom_state.params);
						break;
					default:
						if (angular.isDefined(temp.url))
						{
							var win;
							if (temp.label === "Rx")
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
						break;
				}
			}
		};

		controller.isActive = function isActive(tab)
		{
			if(Juno.Common.Util.isDefinedAndNotNull($state.current.name) &&
				Juno.Common.Util.isDefinedAndNotNull(tab.state))
			{
				return (tab.state.includes($state.current.name));
			}

			return false;
		};

		// Check if there have been potential changes to a note, display a warning if needed
		$window.onbeforeunload = function (event) {
			if (Juno.Common.Util.isDefinedAndNotNull(controller.page.encounterNote))
			{
				if(controller.page.encounterNote.note.trim().length !== 0 && controller.page.isNoteSaved === false)
				{
					return 'You have made changes to a note, but you did not save them yet.\nLeaving the page will revert all changes.';
				}
			}
		};

		$scope.$on('$destroy', function() {
			delete $window.onbeforeunload;
		});

		// Warn user about unsaved data before a state change
		$scope.$on("$stateChangeStart", function(event, data)
		{
			// If the encounter note is not null/undefined and the new state is not a child of record, continue
			if (Juno.Common.Util.isDefinedAndNotNull(controller.page.encounterNote) &&
				controller.page.isNoteSaved === false && data.name.indexOf('record.') === -1)
			{
				if(controller.page.encounterNote.note.trim().length !== 0)
				{
					var discard = confirm("You have unsaved note data. Are you sure you want to leave?");
					if (!discard)
					{
						event.preventDefault();
					}
				}
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
		$scope.$watch('recordCtrl.page.encounterNote.note', delayTmpSave);

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

		// TODO-legacy
		controller.cancelNoteEdit = function cancelNoteEdit()
		{
			console.log('CANCELLING EDIT');
			controller.page.encounterNote = null;
			$scope.$broadcast('stopEditingNote');
			skipTmpSave = true;
			controller.getCurrentNote(false);
			controller.removeEditingNoteFlag();
			controller.$storage.hideNote = true;
		};

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
					$scope.$broadcast('noteSaved', results);
					skipTmpSave = true;
					controller.page.encounterNote = results;
					controller.$storage.hideNote = true;
					controller.getCurrentNote(false);
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
			controller.appointmentApi.getAppointment($location.search().appointmentNo).then(
				function success(results)
				{
					controller.page.appointment = results.data.body;
				},
				function error(errors)
				{
					console.log(errors);
				});
		}

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

			window.open(url, "billingWin", "scrollbars=yes, location=no, width=" + screen.width + ", height=" + screen.height, "");
		};

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
					$scope.$broadcast('currentlyEditingNote', controller.page.encounterNote);
					controller.initAppendNoteEditor();
					controller.initObservationDate();
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.getCurrentNote(true);

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
			controller.page.encounterNote = angular.copy(data);
			controller.getIssueNote();

			//Need to check if note has been saved yet.
			controller.$storage.hideNote = false;
			focusService.focusRef(controller.encounterNoteTextAreaRef);
			$scope.$broadcast('currentlyEditingNote', controller.page.encounterNote);

			controller.removeEditingNoteFlag();
		});

		controller.initAppendNoteEditor = function initAppendNoteEditor()
		{
			if ($location.search().noteEditorText != null)
			{
				controller.page.encounterNote.note = controller.page.encounterNote.note + $location.search().noteEditorText;
			}
		};

		// Initialize the observationDate for new notes
		controller.initObservationDate = function initObservationDate()
		{
			if (controller.page.encounterNote.observationDate === null)
			{
				controller.page.encounterNote.observationDate = new Date();
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


		controller.demographic.age = Juno.Common.Util.calcAge(controller.demographic.dobYear, controller.demographic.dobMonth, controller.demographic.dobDay);
		controller.init();
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