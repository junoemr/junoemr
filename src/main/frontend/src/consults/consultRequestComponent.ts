import {SecurityPermissions} from "../common/security/securityConstants";
import {LABEL_POSITION} from "../common/components/junoComponentConstants";
import {ProfessionalSpecialistTo1, ProvidersServiceApi} from "../../generated";
import LoadingQueue from "../lib/util/LoadingQueue";
import ToastService from "../lib/alerts/service/ToastService";
import moment from "moment";
import {JunoSelectOption} from "../lib/common/junoSelectOption";
import Letterhead from "../lib/consult/request/model/Letterhead";
import ConsultService from "../lib/consult/request/model/ConsultService";
import ConsultRequest from "../lib/consult/request/model/ConsultRequest";
import {API_BASE_PATH} from "../lib/constants/ApiConstants";

angular.module('Consults').component('consultRequest',
	{
		templateUrl: 'src/consults/consultRequest.jsp',
		bindings: {
			consult: "<",
		},
		controller: [
			'$scope',
			'$http',
			"$httpParamSerializer",
			'$q',
			'$resource',
			'$location',
			'$uibModal',
			'$timeout',
			'consultService',
			'demographicService',
			'providerService',
			'securityRolesService',
			'summaryService',
			'staticDataService',

			function (
				$scope,
				$http,
				$httpParamSerializer,
				$q,
				$resource,
				$location,
				$uibModal,
				$timeout,
				consultService,
				demographicService,
				providerService,
				securityRolesService,
				summaryService,
				staticDataService)
			{
				const ctrl = this;
				ctrl.labelPosition = LABEL_POSITION;
				ctrl.SecurityPermissions = SecurityPermissions;

				let providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer, API_BASE_PATH);

				ctrl.urgencyOptions = staticDataService.getConsultUrgencies();
				ctrl.statusOptions = staticDataService.getConsultRequestStatuses();
				ctrl.hours = staticDataService.getHours();
				ctrl.minutes = staticDataService.getMinutes();
				//monitor data changed
				ctrl.consultChanged = false;
				ctrl.initialized = false;
				ctrl.editMode = false;

				ctrl.loadingQueue = new LoadingQueue();
				ctrl.toastService = new ToastService();
				ctrl.serviceOptions = [];
				ctrl.demographic = null;
				ctrl.selectedLetterhead = null;
				ctrl.selectedSpecialistId = null;

				ctrl.specilistOptions = [];
				ctrl.serviceSpecialistMap = new Map();

				ctrl.sendOptions = [];

				ctrl.$onInit = async () =>
				{
					ctrl.editMode = (ctrl.consult.id !== null);

					const results = await Promise.all([
						demographicService.getDemographic(ctrl.consult.demographicId),
						providerService.getActiveProviders(),
						consultService.getLetterheadList(),
						consultService.getServiceList(),
					]);

					ctrl.demographic = results[0];
					ctrl.providers =  results[1].map((provider: any) =>
					{
						return {
							label: provider.name,
							value: provider.providerNo
						};
					});

					ctrl.letterheadOptions = results[2].map((letterhead: Letterhead) =>
					{
						return {
							label: letterhead.name,
							value: letterhead.id,
							data: letterhead,
						};
					});

					if(!ctrl.consult.letterhead)
					{
						ctrl.changeLetterhead(ctrl.letterheadOptions[0].data);
					}
					ctrl.selectedLetterhead = ctrl.consult.letterhead.id;

					ctrl.sendOptions = ctrl.consult.sendToList.map((sendTo: string) =>
					{
						return {
							label: sendTo,
							value: sendTo,
						};
					});

					ctrl.serviceOptions = results[3].map((service: ConsultService) =>
					{
						return {
							label: service.description,
							value: service.id,
						};
					});

					// map specialist options to each service
					results[3].forEach((service: ConsultService) =>
					{
						ctrl.serviceSpecialistMap.set(service.id, service.specialists.map(
							(specialist: ProfessionalSpecialistTo1) =>
							{
								return {
									label: specialist.name,
									value: specialist.id,
									data: specialist,
								};
							}));
					});

					if(!ctrl.consult.serviceId)
					{
						ctrl.consult.serviceId = results[3][0].id;
					}
					if(ctrl.consult.professionalSpecialist)
					{
						ctrl.selectedSpecialistId = ctrl.consult.professionalSpecialist.id;
					}

					ctrl.changeService(ctrl.consult.serviceId)
					ctrl.setESendEnabled(); //execute once on form open

					//set attachments
					Juno.Consults.Common.sortAttachmentDocs(ctrl.consult.attachments);

					ctrl.flagUnsaved(false);
				};

				ctrl.$postLink = () =>
				{
					// hack to prevent a change event on load flagging triggering the watch
					$timeout(() =>
					{
						ctrl.loadWatches(); // done once everything initialized
						ctrl.initialized = true;
					}, 2000);
				}

				// providerNo is what is used as the referral provider for saving
				ctrl.onReferralPractitionerSelected = (provider) =>
				{
					ctrl.consult.providerNo = provider;
				};

				ctrl.parseTime = function parseTime(time)
				{
					var tArray = time.split(":");
					ctrl.consult.appointmentHour = tArray[0];
					ctrl.consult.appointmentMinute = tArray[1];
				};

				ctrl.changeLetterhead = (letterhead: Letterhead): void =>
				{
					ctrl.consult.letterhead = letterhead;
				};

				ctrl.loadWatches = () =>
				{
					$scope.$watchCollection(function()
						{
							return ctrl.consult;
						},
						function(newVal, oldVal)
						{
							if(newVal !== oldVal)
							{
								ctrl.flagUnsaved(true);
							}
						});
				}

				//remind user of unsaved data
				$scope.$on("$stateChangeStart", function(event)
				{
					if (ctrl.isUnsaved())
					{
						var discard = confirm("You may have unsaved data. Are you sure to leave?");
						if (!discard) event.preventDefault();
					}
				});

				ctrl.flagUnsaved = (dirty) =>
				{
					ctrl.consultChanged = dirty;
				}

				ctrl.isUnsaved = () =>
				{
					return ctrl.consultChanged;
				}

				ctrl.changeService = (serviceId: string): void =>
				{
					ctrl.specilistOptions = ctrl.serviceSpecialistMap.get(serviceId);

					// clear current specialist on change if they are not in the new options
					if(ctrl.consult.professionalSpecialist
						&& !(ctrl.specilistOptions.map(
							(option: JunoSelectOption) => option.value).includes(ctrl.consult.professionalSpecialist.id)))
					{
						ctrl.consult.professionalSpecialist = null;
					}
				};

				ctrl.changeSpecialist = (specialist): void =>
				{
					ctrl.consult.professionalSpecialist = specialist;
				}

				ctrl.writeToBox = function writeToBox(results, boxId)
				{
					var items = Juno.Common.Util.toArray(results.summaryItem);
					var boxData = null;
					for (var i = 0; i < items.length; i++)
					{
						boxData = Juno.Common.Util.addNewLine(items[i].displayName, boxData);
					}
					if (boxId == "clinicalInfo") ctrl.consult.clinicalInfo = Juno.Common.Util.addNewLine(boxData, ctrl.consult.clinicalInfo);
					else if (boxId == "concurrentProblems") ctrl.consult.concurrentProblems = Juno.Common.Util.addNewLine(boxData, ctrl.consult.concurrentProblems);
					else if (boxId == "currentMeds") ctrl.consult.currentMeds = Juno.Common.Util.addNewLine(boxData, ctrl.consult.currentMeds);
				};


				ctrl.getFamilyHistory = function getFamilyHistory(boxId)
				{
					summaryService.getFamilyHistory(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No family history");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing family history!", true);
							console.error(errors);
						});
				};

				ctrl.getMedicalHistory = function getMedicalHistory(boxId)
				{
					summaryService.getMedicalHistory(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No medical history");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing medical history!", true);
							console.error(errors);
						});
				};

				ctrl.getSocialHistory = function getSocialHistory(boxId)
				{
					summaryService.getSocialHistory(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No social history");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing social history!", true);
							console.error(errors);
						});
				};

				ctrl.getOngoingConcerns = function getOngoingConcerns(boxId)
				{
					summaryService.getOngoingConcerns(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No ongoing concerns");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing ongoing concerns!", true);
							console.error(errors);
						});
				};

				ctrl.getDxRegistry = function getDxRegistry(boxId)
				{
					summaryService.getDiseaseRegistry(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No Dx codes registered");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing Dx codes!", true);
							console.error(errors);
						});
				};

				ctrl.getOtherMeds = function getOtherMeds(boxId)
				{
					summaryService.getOtherMeds(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No other meds");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing other meds!", true);
							console.error(errors);
						});
				};

				ctrl.getReminders = function getReminders(boxId)
				{
					summaryService.getReminders(ctrl.consult.demographicId).then(
						function success(results)
						{
							if (results.summaryItem.length === 0)
							{
								ctrl.toastService.notificationToast("No reminders");
							}
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							ctrl.toastService.errorToast("Error grabbing reminders!", true);
							console.error(errors);
						});
				};

				// New function, doesn't work
				ctrl.getAllergies = function getAllergies(boxId)
				{
					summaryService.getAllergies(ctrl.consult.demographicId).then(
						function success(results)
						{
							ctrl.writeToBox(results, boxId);
						},
						function error(errors)
						{
							console.error(errors);
						});
				};

				ctrl.invalidData = function invalidData()
				{
					if (!ctrl.consult.urgency)
					{
						ctrl.toastService.errorToast("Please select an Urgency");
						return true;
					}
					if (!ctrl.consult.letterhead)
					{
						ctrl.toastService.errorToast("Please select a Letterhead");
						return true;
					}
					if (!ctrl.consult.serviceId)
					{
						ctrl.toastService.errorToast("Please select a Service");
						return true;
					}
					if (ctrl.consult.demographicId == null || ctrl.consult.demographicId == "")
					{
						ctrl.toastService.errorToast("Error! Invalid patient!");
						return true;
					}
					return false;
				};

				ctrl.setAppointmentTime = function setAppointmentTime()
				{
					if (ctrl.consult.appointmentHour != null && ctrl.consult.appointmentMinute != null && !ctrl.consult.patientWillBook)
					{
						let apptTime = moment(Date.now());
						apptTime.set('hours', ctrl.consult.appointmentHour);
						apptTime.set('minute', ctrl.consult.appointmentMinute);
						ctrl.consult.appointmentTime = apptTime;
					}
					else
					{
						ctrl.consult.appointmentTime = null;
					}
				};

				ctrl.openAttach = function openAttach(attachment)
				{
					window.open("../" + attachment.url);
				};

				ctrl.attachFiles = function attachFiles()
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
										return ctrl.consult;
									}
								}
						});

					modalInstance.result.then(
						function success()
						{
							if (ctrl.consult.attachmentsChanged)
							{
								ctrl.flagUnsaved(true);
								ctrl.consult.attachmentsChanged = false;
							}
						},
						function error(errors)
						{
							console.error(errors);
						});
				};

				//show/hide e-send button
				ctrl.setESendEnabled = function setESendEnabled()
				{
					ctrl.eSendEnabled = ctrl.consult.professionalSpecialist != null && ctrl.consult.professionalSpecialist.eDataUrl != null && ctrl.consult.professionalSpecialist.eDataUrl.trim() != "";
				};

				ctrl.save = async () =>
				{
					var valid = true;

					if (ctrl.consult.id == null && !securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationCreate))
					{
						ctrl.toastService.errorToast("You don't have right to save new consult");
						valid = false;
					}
					else if (!securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationUpdate))
					{
						ctrl.toastService.errorToast("You don't have right to update consult");
						valid = false;
					}
					if (ctrl.invalidData())
					{
						valid = false;
					}

					if(valid)
					{
						ctrl.loadingQueue.pushLoadingState();
						ctrl.consultSaving = true; //show saving banner
						ctrl.flagUnsaved(false); //reset change count
						ctrl.setAppointmentTime();

						let response: ConsultRequest;
						try
						{
							if(ctrl.editMode)
							{
								response = await consultService.updateRequest(ctrl.consult);
							}
							else
							{
								response = await consultService.createRequest(ctrl.consult);
								$location.path("/record/" + ctrl.consult.demographicId + "/consult/" + response.id);
							}
							ctrl.consult = response;
						}
						catch(error)
						{

						}
						finally
						{
							ctrl.setESendEnabled();
							ctrl.consultSaving = false; //hide saving banner
							ctrl.loadingQueue.popLoadingState();
						}
					}
				}

				ctrl.close = function close()
				{
					if ($location.search().list === "patient")
					{
						$location.path("/record/" + ctrl.consult.demographicId + "/consults");
					}
					else
					{
						$location.path("/consults");
					}
				};

				ctrl.saveAndFax = function saveAndFax()
				{
					ctrl.loadingQueue.pushLoadingState();
					ctrl.save().then(
						function success(reqId)
						{
							var demographicNo = ctrl.consult.demographicId;
							var letterheadFax = Juno.Common.Util.noNull(ctrl.consult.letterhead.fax);
							var fax = Juno.Common.Util.noNull(ctrl.consult.professionalSpecialist.faxNumber);

							window.open("../fax/CoverPage.jsp?reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&faxRecipients=" + fax);
						},
						function failure(error)
						{
						}
					).finally(() =>
					{
						ctrl.loadingQueue.popLoadingState();
					});
				};

				ctrl.eSend = function eSend()
				{
					if (ctrl.eSendEnabled)
					{
						ctrl.loadingQueue.pushLoadingState();
						consultService.eSendRequest(ctrl.consult.id).then(
							function success(results)
							{
								alert(results.message);
							},
							function error(errors)
							{
								console.error(errors);
							}).finally(() =>
						{
							ctrl.loadingQueue.popLoadingState();
						});
					}
				};

				ctrl.saveAndPrint = function saveAndPrint()
				{
					ctrl.loadingQueue.pushLoadingState();
					ctrl.save().then(
						function success(reqId)
						{
							ctrl.print(reqId);
						},
						function failure(error)
						{
						}
					).finally(() =>
					{
						ctrl.loadingQueue.popLoadingState();
					});
				};

				ctrl.print = function print(reqId): void
				{
					window.open("../oscarEncounter/oscarConsultationRequest/printPdf2.do?reqId=" + reqId + "&demographicNo=" + ctrl.consult.demographicId);
				};
			}
		]
});