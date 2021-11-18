import {SecurityPermissions} from "../common/security/securityConstants";
import {LABEL_POSITION} from "../common/components/junoComponentConstants";
import {ProvidersServiceApi} from "../../generated";
import LoadingQueue from "../lib/util/LoadingQueue";
import ToastService from "../lib/alerts/service/ToastService";

angular.module('Consults').controller('Consults.ConsultRequestController', [

	'$scope',
	'$http',
	"$httpParamSerializer",
	'$q',
	'$resource',
	'$location',
	'$uibModal',
	'consultService',
	'demographicService',
	'securityRolesService',
	'summaryService',
	'staticDataService',
	'consult',
	'user',

	function(
		$scope,
		$http,
		$httpParamSerializer,
		$q,
		$resource,
		$location,
		$uibModal,
		consultService,
		demographicService,
		securityRolesService,
		summaryService,
		staticDataService,
		consult,
		user)
	{
		const controller = this;
		controller.SecurityPermissions = SecurityPermissions;

		let providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer, "../ws/rs");

		controller.consult = consult;

		consult.faxList = Juno.Common.Util.toArray(consult.faxList);
		consult.serviceList = Juno.Common.Util.toArray(consult.serviceList);
		consult.sendToList = Juno.Common.Util.toArray(consult.sendToList);

		controller.urgencies = staticDataService.getConsultUrgencies();
		controller.statuses = staticDataService.getConsultRequestStatuses();
		controller.hours = staticDataService.getHours();
		controller.minutes = staticDataService.getMinutes();

		controller.loadingQueue = new LoadingQueue();
		controller.toastService = new ToastService();

		controller.parseTime = function parseTime(time)
		{
			var tArray = time.split(":");
			consult.appointmentHour = tArray[0];
			consult.appointmentMinute = tArray[1];
		};
		/* If appointment time is present, we must parse the hours and minutes in order to
		   populate the hour and minute selectors */
		if (consult.appointmentTime !== null)
		{
			controller.parseTime(consult.appointmentTime);

		}

		controller.initialize = function()
		{
			controller.labelPosition = LABEL_POSITION;

			//set demographic info
			controller.loadingQueue.pushLoadingState();
			demographicService.getDemographic(consult.demographicId).then(
				function success(results)
				{
					consult.demographic = results;

					//set cell phone
					consult.demographic.extras = Juno.Common.Util.toArray(consult.demographic.extras);
					for (var i = 0; i < consult.demographic.extras.length; i++)
					{
						if (consult.demographic.extras[i].key == "demo_cell")
						{
							consult.demographic.cellPhone = consult.demographic.extras[i].value;
							break;
						}
					}
				},
				function error(errors)
				{
					console.error(errors);
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});

			controller.loadingQueue.pushLoadingState();
			consultService.getLetterheadList().then(
				function success(results)
				{
					consult.letterheadList = Juno.Common.Util.toArray(results.data);
					if(consult.letterhead === null)
					{
						controller.changeLetterhead(consult.letterheadList[0]);
					}
					else
					{
						for (var i = 0; i < consult.letterheadList.length; i++)
						{
							if (consult.letterheadList[i].id === consult.letterheadName)
							{
								controller.changeLetterhead(consult.letterheadList[i]);
								break;
							}
						}
					}
				},
				function error(errors)
				{
					console.error(errors);
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});

			//set specialist list
			for (var i = 0; i < consult.serviceList.length; i++)
			{
				if (consult.serviceList[i].serviceId == consult.serviceId)
				{
					controller.specialists = Juno.Common.Util.toArray(consult.serviceList[i].specialists);
					break;
				}
			}
			angular.forEach(controller.specialists, function(spec)
			{
				if (consult.professionalSpecialist && spec.id == consult.professionalSpecialist.id)
				{
					consult.professionalSpecialist = spec;
				}
			});

			//set attachments
			consult.attachments = Juno.Common.Util.toArray(consult.attachments);
			Juno.Consults.Common.sortAttachmentDocs(consult.attachments);

			controller.loadingQueue.pushLoadingState();
			providersServiceApi.getActive().then(
				function success(results)
				{
					controller.providers = [];
					for (let provider of results.data.body)
					{
						controller.providers.push({
							label: provider.name,
							value: provider.providerNo
						})
					}
				},
				function error(results)
				{
					console.error("Failed to get provider list with error: " + results);
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});
		};
		controller.initialize();

		// providerNo is what is used as the referral provider for saving
		controller.onReferralPractitionerSelected = (provider) =>
		{
			controller.consult.providerNo = provider;
		};

		controller.changeLetterhead = function changeLetterhead(letterhead)
		{
			consult.letterhead = letterhead;

			// these are required for current print functionality
			consult.letterheadName = consult.letterhead.id;
			consult.letterheadAddress = consult.letterhead.address;
			consult.letterheadPhone = consult.letterhead.phone;
			consult.letterheadFax = consult.letterhead.fax;
		};

		//monitor data changed
		controller.consultChanged = 0;
		$scope.$watchCollection(function()
			{
				return controller.consult;
			},
			function(newVal, oldVal)
			{
				controller.consultChanged++;
			});

		//remind user of unsaved data
		$scope.$on("$stateChangeStart", function(event)
		{
			if (controller.consultChanged > 0)
			{
				var discard = confirm("You may have unsaved data. Are you sure to leave?");
				if (!discard) event.preventDefault();
			}
		});

		controller.changeService = function changeService(id)
		{
			var index = $("#serviceId")[0].selectedIndex;
			if (index === null)
			{
				$scope.specialists = null;
				return;
			}
			controller.specialists = Juno.Common.Util.toArray(consult.serviceList[index].specialists);
		};

		controller.writeToBox = function writeToBox(results, boxId)
		{
			var items = Juno.Common.Util.toArray(results.summaryItem);
			var boxData = null;
			for (var i = 0; i < items.length; i++)
			{
				boxData = Juno.Common.Util.addNewLine(items[i].displayName, boxData);
			}
			if (boxId == "clinicalInfo") consult.clinicalInfo = Juno.Common.Util.addNewLine(boxData, consult.clinicalInfo);
			else if (boxId == "concurrentProblems") consult.concurrentProblems = Juno.Common.Util.addNewLine(boxData, consult.concurrentProblems);
			else if (boxId == "currentMeds") consult.currentMeds = Juno.Common.Util.addNewLine(boxData, consult.currentMeds);
		};


		controller.getFamilyHistory = function getFamilyHistory(boxId)
		{
			summaryService.getFamilyHistory(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No family history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing family history!", true);
					console.error(errors);
				});
		};

		controller.getMedicalHistory = function getMedicalHistory(boxId)
		{
			summaryService.getMedicalHistory(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No medical history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing medical history!", true);
					console.error(errors);
				});
		};

		controller.getSocialHistory = function getSocialHistory(boxId)
		{
			summaryService.getSocialHistory(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No social history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing social history!", true);
					console.error(errors);
				});
		};

		controller.getOngoingConcerns = function getOngoingConcerns(boxId)
		{
			summaryService.getOngoingConcerns(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No ongoing concerns");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing ongoing concerns!", true);
					console.error(errors);
				});
		};

		controller.getDxRegistry = function getDxRegistry(boxId)
		{
			summaryService.getDiseaseRegistry(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No Dx codes registered");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing Dx codes!", true);
					console.error(errors);
				});
		};

		controller.getOtherMeds = function getOtherMeds(boxId)
		{
			summaryService.getOtherMeds(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No other meds");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing other meds!", true);
					console.error(errors);
				});
		};

		controller.getReminders = function getReminders(boxId)
		{
			summaryService.getReminders(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No reminders");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing reminders!", true);
					console.error(errors);
				});
		};

		// New function, doesn't work
		controller.getAllergies = function getAllergies(boxId)
		{
			summaryService.getAllergies(consult.demographicId).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.error(errors);
				});
		};
		
		controller.invalidData = function invalidData()
		{
			if (!controller.consult.urgency)
			{
				alert("Please select an Urgency");
				return true;
			}
			if (!controller.consult.letterheadName)
			{
				alert("Please select a Letterhead");
				return true;
			}
			if (!controller.consult.serviceId)
			{
				alert("Please select a Service");
				return true;
			}
			if (controller.consult.demographic == null || controller.consult.demographic == "")
			{
				alert("Error! Invalid patient!");
				return true;
			}
			return false;
		};

		controller.setAppointmentTime = function setAppointmentTime()
		{
			if (consult.appointmentHour != null && consult.appointmentMinute != null && !consult.patientWillBook)
			{
				let apptTime = moment(Date.now());
				apptTime.set('hours', consult.appointmentHour);
				apptTime.set('minute', consult.appointmentMinute);
				consult.appointmentTime = apptTime;
			}
			else
			{
				consult.appointmentTime = null;
			}
		};

		controller.openAttach = function openAttach(attachment)
		{
			window.open("../" + attachment.url);
		};

		controller.attachFiles = function attachFiles()
		{
			var modalInstance = $uibModal.open(
				{
					templateUrl: "src/consults/consultAttachment.jsp",
					controller: 'Consults.ConsultRequestAttachmentController as consultAttachmentCtrl',
					windowClass: "attachment-modal-window",
					size: 'lg',
					resolve:
						{
							consult: function()
							{
								return consult;
							}
						}
				});

			modalInstance.result.then(
				function success()
				{
					if (consult.attachmentsChanged)
					{
						controller.consultChanged++;
						consult.attachmentsChanged = false;
					}
				},
				function error(errors)
				{
					console.error(errors);
				});
		};

		//show/hide e-send button
		controller.setESendEnabled = function setESendEnabled()
		{
			controller.eSendEnabled = consult.professionalSpecialist != null && consult.professionalSpecialist.eDataUrl != null && consult.professionalSpecialist.eDataUrl.trim() != "";
		};

		controller.setESendEnabled(); //execute once on form open

		controller.save = function save()
		{
			var deferred = $q.defer();
			var valid = true;

			if (consult.id == null && !securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationCreate))
			{
				controller.toastService.errorToast("You don't have right to save new consult");
				valid = false;
			}
			else if (!securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationUpdate))
			{
				controller.toastService.errorToast("You don't have right to update consult");
				valid = false;
			}
			if (controller.invalidData())
			{
				valid = false;
			}

			if(valid)
			{
				controller.loadingQueue.pushLoadingState();
				controller.consultSaving = true; //show saving banner
				controller.setAppointmentTime();

				consultService.saveRequest(controller.consult).then(
					function success(results)
					{
						if (controller.consult.id == null)
						{
							controller.consult.id = results.id; // assign id to prevent possible double save
							$location.path("/record/" + consult.demographicId + "/consult/" + results.id);
						}
						deferred.resolve(results.id);
					},
					function error(errors)
					{
						console.error(errors);
						deferred.reject(errors);
					})
					.finally(
						function()
						{
							controller.setESendEnabled();
							controller.consultSaving = false; //hide saving banner
							controller.consultChanged = 0; //reset change count
							controller.loadingQueue.popLoadingState();
						}
					);
			}
			else
			{
				deferred.reject("Invalid");
			}
			return deferred.promise;
		};

		controller.close = function close()
		{
			if ($location.search().list === "patient")
			{
				$location.path("/record/" + consult.demographicId + "/consults");
			}
			else
			{
				$location.path("/consults");
			}
		};

		controller.saveAndFax = function saveAndFax()
		{
			controller.loadingQueue.pushLoadingState();
			controller.save().then(
				function success(reqId)
				{
					var demographicNo = consult.demographicId;
					var letterheadFax = Juno.Common.Util.noNull(consult.letterhead.fax);
					var fax = Juno.Common.Util.noNull(consult.professionalSpecialist.faxNumber);

					window.open("../fax/CoverPage.jsp?reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&faxRecipients=" + fax);
				},
				function failure(error)
				{
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});
		};

		controller.eSend = function eSend()
		{
			if (controller.eSendEnabled)
			{
				controller.loadingQueue.pushLoadingState();
				consultService.eSendRequest(consult.id).then(
					function success(results)
					{
						alert(results.message);
					},
					function error(errors)
					{
						console.error(errors);
					}).finally(() =>
				{
					controller.loadingQueue.popLoadingState();
				});
			}
		};

		controller.saveAndPrint = function saveAndPrint()
		{
			controller.loadingQueue.pushLoadingState();
			controller.save().then(
				function success(reqId)
				{
					controller.print(reqId);
				},
				function failure(error)
				{
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});
		};

		controller.print = function print(reqId)
		{
			window.open("../oscarEncounter/oscarConsultationRequest/printPdf2.do?reqId=" + reqId + "&demographicNo=" + consult.demographicId);
		};
	}
]);