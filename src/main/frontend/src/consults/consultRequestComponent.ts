import {SecurityPermissions} from "../common/security/securityConstants";
import {LABEL_POSITION} from "../common/components/junoComponentConstants";
import {ProfessionalSpecialistTo1} from "../../generated";
import LoadingQueue from "../lib/util/LoadingQueue";
import ToastService from "../lib/alerts/service/ToastService";
import {JunoSelectOption} from "../lib/common/junoSelectOption";
import Letterhead from "../lib/consult/request/model/Letterhead";
import ConsultService from "../lib/consult/request/model/ConsultService";
import ConsultRequest from "../lib/consult/request/model/ConsultRequest";
import Demographic from "../lib/demographic/model/Demographic";
import ArgumentError from "../lib/error/ArgumentError";
import SecurityError from "../lib/error/SecurtyError";
import ToastErrorHandler from "../lib/error/handler/ToastErrorHandler";
import {LogLevel} from "../lib/error/handler/LogLevel";

angular.module('Consults').component('consultRequest',
	{
		templateUrl: 'src/consults/consultRequest.jsp',
		bindings: {
			consult: "<",
		},
		controller: [
			'$scope',
			'$state',
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
				$state,
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
				ctrl.toastErrorHandler = new ToastErrorHandler(false, LogLevel.WARN);

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
				ctrl.demographic = null;
				ctrl.selectedLetterhead = null;
				ctrl.selectedSpecialistId = null;

				ctrl.serviceOptions = [];
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

					ctrl.serviceOptions = results[3].map((service: ConsultService): JunoSelectOption =>
					{
						return {
							label: service.description,
							value: service.id,
						};
					});
					ctrl.serviceOptions.unshift({
						label: "",
						value: null,
					});

					ctrl.serviceSpecialistMap.set(null, []); // add empty option
					// map specialist options to each service
					results[3].forEach((service: ConsultService) =>
					{
						ctrl.serviceSpecialistMap.set(service.id, service.specialists.map(
							(specialist: ProfessionalSpecialistTo1): JunoSelectOption =>
							{
								return {
									label: specialist.name,
									value: specialist.id,
									data: specialist,
								};
							}));
					});

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

				ctrl.flagUnsaved = (dirty): void =>
				{
					ctrl.consultChanged = dirty;
				}

				ctrl.isUnsaved = (): boolean =>
				{
					return ctrl.consultChanged;
				}

				ctrl.changeService = (serviceId: string): void =>
				{
					ctrl.specilistOptions = ctrl.serviceSpecialistMap.get(serviceId);

					// clear current specialist on change if they are not in the new options
					if(ctrl.consult.professionalSpecialist
						&& ctrl.specilistOptions
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

				ctrl.checkInvalidData = (): void =>
				{
					if (!ctrl.consult.urgency)
					{
						throw new ArgumentError("Please select an Urgency");
					}
					if (!ctrl.consult.letterhead)
					{
						throw new ArgumentError("Please select a Letterhead");
					}
					if (!ctrl.consult.serviceId)
					{
						throw new ArgumentError("Please select a Service");
					}
					if (ctrl.consult.demographicId == null || ctrl.consult.demographicId == "")
					{
						throw new ArgumentError("Error! Invalid patient!");
					}
				};

				ctrl.openAttach = function openAttach(attachment): void
				{
					window.open("../" + attachment.url);
				};

				ctrl.attachFiles = function attachFiles(): void
				{
					const modalInstance = $uibModal.open(
						{
							component: "consultAttachmentModalComponent",
							windowClass: "attachment-modal-window",
							size: 'lg',
							resolve:
								{
									consult: (): ConsultRequest =>
									{
										return ctrl.consult;
									},
									demographic: (): Demographic =>
									{
										return ctrl.demographic;
									},
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
					ctrl.eSendEnabled = !Juno.Common.Util.isBlank(ctrl.consult.professionalSpecialist)
						&& !Juno.Common.Util.isBlank(ctrl.consult.professionalSpecialist.eDataUrl);
				};

				ctrl.save = async (): Promise<number> =>
				{
					if (ctrl.editMode && !securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationUpdate))
					{
						throw new SecurityError(SecurityPermissions.ConsultationUpdate);
					}
					else if (!securityRolesService.hasSecurityPrivileges(SecurityPermissions.ConsultationCreate))
					{
						throw new SecurityError(SecurityPermissions.ConsultationCreate);
					}
					ctrl.checkInvalidData();

					ctrl.loadingQueue.pushLoadingState();
					ctrl.consultSaving = true; //show saving banner
					ctrl.flagUnsaved(false); //reset change count

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
							$state.go("record.consultRequest",
								{
									demographicNo: ctrl.consult.demographicId,
									requestId: response.id,
								});
						}
					}
					finally
					{
						ctrl.setESendEnabled();
						ctrl.consultSaving = false; //hide saving banner
						ctrl.flagUnsaved(false); //reset change after having updated the object
						ctrl.loadingQueue.popLoadingState();
					}
					return response.id;
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

				ctrl.onSave = async (): Promise<void> =>
				{
					try
					{
						await ctrl.save();
					}
					catch (error)
					{
						ctrl.toastErrorHandler.handleError(error);
					}
				}

				ctrl.onSaveAndFax = (): void =>
				{
					ctrl.loadingQueue.pushLoadingState();
					ctrl.save().then((reqId: number) =>
						{
							if (!ctrl.consult.professionalSpecialist)
							{
								throw new ArgumentError("A specialist must be selected for faxing");
							}
							if (!ctrl.consult.professionalSpecialist.faxNumber)
							{
								throw new ArgumentError("Selected specialist is missing a fax number");
							}
							var demographicNo = ctrl.consult.demographicId;
							var letterheadFax = Juno.Common.Util.noNull(ctrl.consult.letterhead?.fax?.number);
							var fax = Juno.Common.Util.noNull(ctrl.consult.professionalSpecialist.faxNumber);

							window.open("../fax/CoverPage.jsp?reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&faxRecipients=" + fax);
						}
					).catch((error) =>
						{
							ctrl.toastErrorHandler.handleError(error);
						}
					).finally(() =>
						{
							ctrl.loadingQueue.popLoadingState();
						}
					);
				};

				ctrl.eSend = function eSend(): void
				{
					if (ctrl.eSendEnabled)
					{
						ctrl.loadingQueue.pushLoadingState();
						consultService.eSendRequest(ctrl.consult.id).then(
							function success(results)
							{
								ctrl.toastService.successToast(results.message);
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

				ctrl.onSaveAndPrint = (): void =>
				{
					ctrl.loadingQueue.pushLoadingState();
					ctrl.save().then(
						function success(reqId)
						{
							ctrl.print(reqId);
						},
						function failure(error)
						{
							ctrl.toastErrorHandler.handleError(error);
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