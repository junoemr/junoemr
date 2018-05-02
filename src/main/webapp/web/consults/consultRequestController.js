angular.module('Consults').controller('Consults.ConsultRequestController', [

	'$scope',
	'$http',
	'$q',
	'$resource',
	'$location',
	'$uibModal',
	'consultService',
	'demographicService',
	'securityService',
	'summaryService',
	'staticDataService',
	'consult',
	'user',

	function(
		$scope,
		$http,
		$q,
		$resource,
		$location,
		$uibModal,
		consultService,
		demographicService,
		securityService,
		summaryService,
		staticDataService,
		consult,
		user)
	{

		var controller = this;

		controller.consult = consult;

		consult.letterheadList = Juno.Common.Util.toArray(consult.letterheadList);
		consult.faxList = Juno.Common.Util.toArray(consult.faxList);
		consult.serviceList = Juno.Common.Util.toArray(consult.serviceList);
		consult.sendToList = Juno.Common.Util.toArray(consult.sendToList);

		controller.urgencies = staticDataService.getConsultUrgencies();
		controller.statuses = staticDataService.getConsultRequestStatuses();
		controller.hours = staticDataService.getHours();
		controller.minutes = staticDataService.getMinutes();

		//set appointment time
		console.log('initial appointment time: ', angular.copy(consult.appointmentTime));

		controller.changeLetterhead = function changeLetterhead()
		{
			if (consult.letterhead === null) return;

			consult.letterheadName = consult.letterhead.id;
			consult.letterheadAddress = consult.letterhead.address;
			consult.letterheadPhone = consult.letterhead.phone;
		};

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

		//get access rights
		securityService.hasRight("_con", "r").then(
			function success(results)
			{
				controller.consultReadAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "u").then(
			function success(results)
			{
				controller.consultUpdateAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});
		securityService.hasRight("_con", "w").then(
			function success(results)
			{
				controller.consultWriteAccess = results;
			},
			function error(errors)
			{
				console.log(errors);
			});

		//set demographic info
		demographicService.getDemographic(consult.demographicId).then(
			function success(results)
			{
				console.log('SUCCESS, DEMO: ', results);
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
				console.log(errors);
			});

		//set default letterhead
		if (consult.letterheadName == null)
		{
			consult.letterhead = consult.letterheadList[0];
			controller.changeLetterhead();
		}
		else
		{
			for (var i = 0; i < consult.letterheadList.length; i++)
			{
				if (consult.letterheadList[i].id === consult.letterheadName)
				{
					consult.letterhead = consult.letterheadList[i];
					break;
				}
			}
		}

		//set default fax if there's only 1
		if (consult.letterhead.fax == null && consult.faxList.length == 1)
		{
			consult.letterhead.fax = consult.faxList[0].faxNumber;
		}

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
			controller.specialists = toArray(consult.serviceList[index].specialists);
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
						alert("No family history");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing family history!");
					console.log(errors);
				});
		};

		controller.getMedicalHistory = function getMedicalHistory(boxId)
		{
			summaryService.getMedicalHistory(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
						alert("No medical history");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing medical history!");
					console.log(errors);
				});
		};

		controller.getSocialHistory = function getSocialHistory(boxId)
		{
			summaryService.getSocialHistory(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
						alert("No social history");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing social history!");
					console.log(errors);
				});
		};

		controller.getOngoingConcerns = function getOngoingConcerns(boxId)
		{
			summaryService.getOngoingConcerns(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
						alert("No ongoing concerns");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing ongoing concerns!");
					console.log(errors);
				});
		};

		controller.getOtherMeds = function getOtherMeds(boxId)
		{
			summaryService.getOtherMeds(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
						alert("No other meds");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing other meds!");
					console.log(errors);
				});
		};

		controller.getReminders = function getReminders(boxId)
		{
			summaryService.getReminders(consult.demographicId).then(
				function success(results)
				{
					if (results.summaryItem.length === 0)
						alert("No reminders");
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					alert("Error grabbing reminders!");
					console.log(errors);
				});
		};

		// New function, doesn't work
		controller.getAllergies = function getAllergies(boxId)
		{
			console.log('CONSULT: ', consult);

			summaryService.getAllergies(consult.demographicId).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		// controller.invalidData = function invalidData()
		// {
		// 	if (controller.urgencies[$("#urgency").val()] == null)
		// 	{
		// 		alert("Please select an Urgency");
		// 		return true;
		// 	}
		// 	if (consult.letterheadList[$("#letterhead").val()] == null)
		// 	{
		// 		alert("Please select a Letterhead");
		// 		return true;
		// 	}
		// 	if (consult.serviceList[$("#serviceId").val()] == null)
		// 	{
		// 		alert("Please select a Specialist Service");
		// 		return true;
		// 	}
		// 	if (consult.professionalSpecialist == null)
		// 	{
		// 		alert("Please select a Specialist");
		// 		return true;
		// 	}
		// 	if (consult.demographic == null || consult.demographic == "")
		// 	{
		// 		alert("Error! Invalid patient!");
		// 		return true;
		// 	}
		// 	return false;
		// };

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
			// if (!controller.consult.prof)
			// {
			// 	alert("Please select a Specialist Service");
			// 	return true;
			// }
			if (!controller.consult.professionalSpecialist)
			{
				alert("Please select a Specialist");
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
				apptTime = moment(Date.now());
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
					templateUrl: "consults/consultAttachment.jsp",
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
					console.log(errors);
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

			if (!controller.consultWriteAccess && consult.id == null)
			{
				alert("You don't have right to save new consult");
				deferred.reject();
			}
			if (!controller.consultUpdateAccess)
			{
				alert("You don't have right to update consult");
				deferred.reject();
			}

			if (controller.invalidData()) deferred.reject();

			controller.consultSaving = true; //show saving banner
			controller.setAppointmentTime();

			deferred = consultService.saveRequest(consult)
				.then(
					function success(results)
					{
						if (consult.id == null) $location.path("/record/" + consult.demographicId + "/consult/" + results.id);
						return results.id;
					},
					function error(errors)
					{
						console.log(errors);
					})
				.finally(
					function()
					{
						console.log("finally happened")
						controller.setESendEnabled();
						controller.consultSaving = false; //hide saving banner
						controller.consultChanged = 0; //reset change count
					}
				);

			return deferred;
		};

		controller.close = function close()
		{
			if ($location.search().list === "patient") $location.path("/record/" + consult.demographicId + "/consults");
			else $location.path("/consults");
		};

		controller.saveAndFax = function saveAndPrint()
		{
			controller.save().then(
				function success(reqId)
				{
					var demographicNo = consult.demographicId;
					var letterheadFax = Juno.Common.Util.noNull(consult.letterhead.fax);
					var fax = Juno.Common.Util.noNull(consult.professionalSpecialist.faxNumber);

					window.open("../fax/CoverPage.jsp?reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&fax=" + fax);
				}
			);
		};

		controller.eSend = function eSend()
		{
			if (controller.eSendEnabled)
			{
				consultService.eSendRequest(consult.id).then(
					function success(results)
					{
						alert(results.message);
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		controller.saveAndPrint = function saveAndPrint()
		{
			controller.save().then(
				function success(reqId)
				{
					if (controller.invalidData()) return;

					window.open("../oscarEncounter/oscarConsultationRequest/printPdf2.do?reqId=" + reqId + "&demographicNo=" + consult.demographicId);
				}
			);
		}
	}
]);