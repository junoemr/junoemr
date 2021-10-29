import LoadingQueue from "../lib/util/LoadingQueue";
import ToastService from "../lib/alerts/service/ToastService";
import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Consults').controller('Consults.ConsultResponseController', [

	'$scope',
	'$http',
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

		controller.loadingQueue = new LoadingQueue();
		controller.toastService = new ToastService();

		controller.initialize = function()
		{
			controller.consult = consult;
			consult.referringDoctorList = Juno.Common.Util.toArray(consult.referringDoctorList);
			consult.faxList = Juno.Common.Util.toArray(consult.faxList);
			consult.sendToList = Juno.Common.Util.toArray(consult.sendToList);

			//set attachments
			consult.attachments = Juno.Common.Util.toArray(consult.attachments);
			Juno.Consults.Common.sortAttachmentDocs(consult.attachments);


			controller.loadingQueue.pushLoadingState();
			consultService.getLetterheadList().then(
				function success(results)
				{
					consult.letterheadList = Juno.Common.Util.toArray(results.data);

					//set default letterhead
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
					console.log(errors);
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});

			//show referringDoctor in list
			angular.forEach(consult.referringDoctorList, function(referringDoc)
			{
				if(consult.referringDoctor !== null)
				{
					if (referringDoc.id === consult.referringDoctor.id)
					{
						consult.referringDoctor = referringDoc;
					}
				}
			});

			//set patient cell phone
			consult.demographic.extras = Juno.Common.Util.toArray(consult.demographic.extras);
			for (var i = 0; i < consult.demographic.extras.length; i++)
			{
				if (consult.demographic.extras[i].key == "demo_cell")
				{
					consult.demographic.cellPhone = consult.demographic.extras[i].value;
					break;
				}
			}
		};
		controller.initialize();

		controller.changeLetterhead = function changeLetterhead(letterhead)
		{
			consult.letterhead = letterhead;

			// these are required for current print functionality
			consult.letterheadName = consult.letterhead.id;
			consult.letterheadAddress = consult.letterhead.address;
			consult.letterheadPhone = consult.letterhead.phone;
			consult.letterheadFax = consult.letterhead.fax;
		};


		controller.urgencies = staticDataService.getConsultUrgencies();
		controller.statuses = staticDataService.getConsultResponseStatuses();
		controller.hours = staticDataService.getHours();
		controller.minutes = staticDataService.getMinutes();

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

		//monitor data changed
		controller.consultChanged = -1;
		$scope.$watchCollection(function()
			{
				return controller.consult;
			}, function()
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

		controller.writeToBox = function writeToBox(data, boxId)
		{
			var items = Juno.Common.Util.toArray(data.summaryItem);
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
			summaryService.getFamilyHistory(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No family history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing family history!", true);
					console.log(errors);
				});
		};
		controller.getMedicalHistory = function getMedicalHistory(boxId)
		{
			summaryService.getMedicalHistory(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No medical history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing medical history!", true);
					console.log(errors);
				});
		};
		controller.getSocialHistory = function getSocialHistory(boxId)
		{
			summaryService.getSocialHistory(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No social history");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing social history!", true);
					console.log(errors);
				});
		};
		controller.getOngoingConcerns = function getOngoingConcerns(boxId)
		{
			summaryService.getOngoingConcerns(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No ongoing concerns");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing ongoing concerns!", true);
					console.log(errors);
				});
		};

		controller.getDxRegistry = function getDxRegistry(boxId)
		{
			summaryService.getDiseaseRegistry(consult.demographic.demographicNo).then(
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
					console.log(errors);
				});
		};

		controller.getOtherMeds = function getOtherMeds(boxId)
		{
			summaryService.getOtherMeds(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No other meds");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					controller.toastService.errorToast("Error grabbing other meds!", true);
					console.log(errors);
				});
		};
		controller.getReminders = function getReminders(boxId)
		{
			summaryService.getReminders(consult.demographic.demographicNo).then(
				function success(results)
				{
					if(results.summaryItem.length === 0)
					{
						controller.toastService.notificationToast("No reminders");
					}
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing reminders!");
					console.log(errors);
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
			if (Juno.Common.Util.isUndefinedOrNull(controller.consult.referringDoctor))
			{
				alert("Please select a Referring Doctor");
				return true;
			}
			if (!controller.consult.demographic || controller.consult.demographic === "")
			{
				alert("Error! Invalid patient!");
				return true;
			}
			return false;
		};

		controller.setAppointmentTime = function setAppointmentTime()
		{
			if (consult.appointmentHour !== null && consult.appointmentMinute !== null)
			{
				const apptTime = moment(Date.now());
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
				controller: "Consults.ConsultResponseAttachmentController as consultAttachmentCtrl",
				windowClass: "attachment-modal-window",
				backdrop: "static",
				resolve:
				{
					consult: function()
					{
						return consult;
					}
				}
			});

			modalInstance.result.then(
				function success(results)
				{
					if (consult.attachmentsChanged)
					{
						controller.consultChanged++;
						consult.attachmentsChanged = false;
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.save = function save()
		{
			if (consult.id == null && !securityRolesService.hasSecurityPrivileges(SecurityPermissions.CONSULTATION_CREATE))
			{
				alert("You don't have right to save new consult");
				return false;
			}
			else if (!securityRolesService.hasSecurityPrivileges(SecurityPermissions.CONSULTATION_UPDATE))
			{
				alert("You don't have right to update consult");
				return false;
			}

			if (controller.invalidData()) return false;

			controller.consultSaving = true; //show saving banner
			controller.setAppointmentTime();

			controller.loadingQueue.pushLoadingState();
			consultService.saveResponse(consult).then(
				function success(results)
				{
					//update url for new consultation
					if (consult.id === null) {
						$location.path("/record/" + consult.demographic.demographicNo + "/consultResponse/" + results.body.id);
					}
				},
				function error(errors)
				{
					console.log(errors);
				}
			).finally(() =>
			{
				controller.loadingQueue.popLoadingState();
			});
			controller.consultSaving = false; //hide saving banner
			controller.consultChanged = -1; //reset change count
			return true;
		};

		controller.close = function close()
		{
			if ($location.search().list === "patient") $location.path("/record/" + consult.demographic.demographicNo + "/consultResponses");
			else $location.path("/consultResponses");
		};

		//fax & print functions
		var p_page1 = "<html><style>body{width:800px;font-family:arial,verdana,tahoma,helvetica,sans serif}table{width:100%}th{text-align:left;font-weight:bold;width:1px;white-space:nowrap}td{vertical-align:top}label{font-weight:bold}em{font-size:small}.large{font-size:large}.center{text-align:center}</style><style media='print'>button{display:none}.noprint{display:none}</style><script>function printAttachments(url){window.open('../'+url);}</script><body>";

		controller.sendFax = function sendFax()
		{
			var p_urgency = Juno.Common.Util.toTrimmedString(controller.urgencies[$("#urgency").val()].name);
			var p_letterheadName = Juno.Common.Util.toTrimmedString(consult.letterheadList[$("#letterhead").val()].name);
			var p_page2 = Juno.Common.Util.getPrintPage2(p_urgency, p_letterheadName, consult, user);

			var consultResponsePage = encodeURIComponent(p_page1 + p_page2);
			var reqId = consult.id;
			var demographicNo = consult.demographic.demographicNo;
			var letterheadFax = Juno.Common.Util.toTrimmedString(consult.letterheadFax);
			var fax = Juno.Common.Util.toTrimmedString(consult.referringDoctor.faxNumber);

			window.open("../fax/CoverPage.jsp?consultResponsePage=" + consultResponsePage + "&reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&faxRecipients=" + fax);
		};

		controller.printPreview = function printPreview()
		{
			if (controller.invalidData()) return;

			var printWin = window.open("", "consultResponsePrintWin", "width=830,height=900,scrollbars=yes,location=no");
			printWin.document.open();

			var p_buttons = "<button onclick='window.print()'>Print</button><button onclick='window.close()'>Close</button>";
			var p_attachments = "";
			for (var i = 0; i < consult.attachments.length; i++)
			{
				p_attachments += "<div class='noprint'><button onclick=printAttachments('" + consult.attachments[i].url + "')>Print attachment</button> " + consult.attachments[i].displayName + "</div>";
			}

			var p_urgency = Juno.Common.Util.toTrimmedString(controller.urgencies[$("#urgency").val()].name);
			var p_letterheadName = Juno.Common.Util.toTrimmedString(consult.letterheadList[$("#letterhead").val()].name);
			var p_page2 = controller.getPrintPage2(p_urgency, p_letterheadName, consult, user);

			printWin.document.write(p_page1 + p_buttons + p_attachments + p_page2);
			printWin.document.close();
		};

		controller.getPrintPage2 = function getPrintPage2(p_urgency, p_letterheadName, consult, user)
		{
			var p_clinicName = Juno.Common.Util.toTrimmedString(consult.letterheadList[0].name);
			var p_responseDate = Juno.Common.Util.formatDate(consult.responseDate);
			var p_referralDate = Juno.Common.Util.formatDate(consult.referralDate);
			var p_letterheadAddress = Juno.Common.Util.toTrimmedString(consult.letterheadAddress);
			var p_letterheadPhone = Juno.Common.Util.toTrimmedString(consult.letterheadPhone);
			var p_letterheadFax = Juno.Common.Util.toTrimmedString(consult.letterheadFax);
			var p_consultantName = Juno.Common.Util.toTrimmedString(consult.referringDoctor.name);
			var p_consultantPhone = Juno.Common.Util.toTrimmedString(consult.referringDoctor.phoneNumber);
			var p_consultantFax = Juno.Common.Util.toTrimmedString(consult.referringDoctor.faxNumber);
			var p_consultantAddress = Juno.Common.Util.toTrimmedString(consult.referringDoctor.streetAddress);
			var p_patientName = Juno.Common.Util.toTrimmedString(consult.demographic.lastName) + ", " + Juno.Common.Util.toTrimmedString(consult.demographic.firstName);
			var p_patientPhone = Juno.Common.Util.toTrimmedString(consult.demographic.phone);
			var p_patientWorkPhone = Juno.Common.Util.toTrimmedString(consult.demographic.alternativePhone);
			var p_patientBirthdate = Juno.Common.Util.formatDate(consult.demographic.dateOfBirth);
			var p_patientSex = Juno.Common.Util.toTrimmedString(consult.demographic.sexDesc);
			var p_patientHealthCardNo = Juno.Common.Util.toTrimmedString(consult.demographic.hin) + "-" + Juno.Common.Util.toTrimmedString(consult.demographic.ver);
			var p_patientChartNo = Juno.Common.Util.toTrimmedString(consult.demographic.chartNo);
			var p_patientAddress = "";
			if (consult.demographic.address != null)
			{
				p_patientAddress = Juno.Common.Util.toTrimmedString(consult.demographic.address.address) + ", " +
					Juno.Common.Util.toTrimmedString(consult.demographic.address.city) + ", " +
					Juno.Common.Util.toTrimmedString(consult.demographic.address.province) + " " +
					Juno.Common.Util.toTrimmedString(consult.demographic.address.postal);
			}
			var p_appointmentDate = Juno.Common.Util.formatDate(consult.appointmentDate);
			var p_appointmentTime = Juno.Common.Util.formatTime(consult.appointmentTime);
			var p_reason = Juno.Common.Util.toTrimmedString(consult.reasonForReferral);
			var p_examination = Juno.Common.Util.toTrimmedString(consult.examination);
			var p_impression = Juno.Common.Util.toTrimmedString(consult.impression);
			var p_plan = Juno.Common.Util.toTrimmedString(consult.plan);
			var p_clinicalInfo = Juno.Common.Util.toTrimmedString(consult.clinicalInfo);
			var p_concurrentProblems = Juno.Common.Util.toTrimmedString(consult.concurrentProblems);
			var p_currentMeds = Juno.Common.Util.toTrimmedString(consult.currentMeds);
			var p_allergies = Juno.Common.Util.toTrimmedString(consult.allergies);
			var p_provider = Juno.Common.Util.toTrimmedString(user.lastName) + ", " + Juno.Common.Util.toTrimmedString(user.firstName);

			return "<div class='center'><label class='large'>" + p_clinicName + "</label><br/><label>Consultation Response</label><br/></div><br/><table><tr><td><label>Date: </label>" + p_responseDate + "</td><td rowspan=6 width=10></td><td><label>Status: </label>" + p_urgency + "</td></tr><tr><td colspan=2></td></tr><tr><th>FROM:</th><th>TO:</th></tr><tr><td><p class='large'>" + p_letterheadName + "</p>" + p_letterheadAddress + "<br/><label>Tel: </label>" + p_letterheadPhone + "<br/><label>Fax: </label>" + p_letterheadFax + "</td><td><table><tr><th>Referring Doctor:</th><td>" + p_consultantName + "</td></tr><tr><th>Phone:</th><td>" + p_consultantPhone + "</td></tr><tr><th>Fax:</th><td>" + p_consultantFax + "</td></tr><tr><th>Address:</th><td>" + p_consultantAddress + "</td></tr></table></td></tr><tr><td colspan=2></td></tr><tr><td><table><tr><th>Patient:</th><td>" + p_patientName + "</td></tr><tr><th>Address:</th><td>" + p_patientAddress + "</td></tr><tr><th>Phone:</th><td>" + p_patientPhone + "</td></tr><tr><th>Work Phone:</th><td>" + p_patientWorkPhone + "</td></tr><tr><th>Birthdate:</th><td>" + p_patientBirthdate + "</td></tr></table></td><td><table><tr><th>Sex:</th><td>" + p_patientSex + "</td></tr><tr><th>Health Card No:</th><td>" + p_patientHealthCardNo + "</td></tr><tr><th>Appointment date:</th><td>" + p_appointmentDate + "</td></tr><tr><th>Appointment time:</th><td>" + p_appointmentTime + "</td></tr><tr><th>Chart No:</th><td>" + p_patientChartNo + "</td></tr></table></td></tr></table><br/><table><tr><th>Examination:</th></tr><tr><td>" + p_examination + "<hr></td></tr><tr><th>Impression:</th></tr><tr><td>" + p_impression + "<hr></td></tr><tr><th>Plan:</th></tr><tr><td>" + p_plan + "<hr></td></tr><tr><td></td></tr><tr><th>Reason for consultation: (Date: " + p_referralDate + ")</th></tr><tr><td>" + p_reason + "<hr></td></tr><tr><th>Pertinent Clinical Information:</th></tr><tr><td>" + p_clinicalInfo + "<hr></td></tr><tr><th>Significant Concurrent Problems:</th></tr><tr><td>" + p_concurrentProblems + "<hr></td></tr><tr><th>Current Medications:</th></tr><tr><td>" + p_currentMeds + "<hr></td></tr><tr><th>Allergies:</th></tr><tr><td>" + p_allergies + "<hr></td></tr><tr><td><label>Consultant: </label>" + p_provider + "</td></tr><tr><td></td></tr><tr><td><div class='center'><em>Created by: OSCAR The open-source EMR www.oscarcanada.org</em></div></td></tr></table></body></html>";
		};
	}
]);