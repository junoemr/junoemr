angular.module('Consults').controller('Consults.ConsultRequestController', [

	'$scope',
	'$http',
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
		if (consult.letterhead == null)
		{
			for (var i = 0; i < consult.letterheadList.length; i++)
			{
				if (consult.letterheadList[i].id == user.providerNo)
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
		controller.consultChanged = -1;
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

		controller.changeLetterhead = function changeLetterhead(newLetterheadName)
		{
			var index = $("#letterhead")[0].selectedIndex;
			if (index === null) return;

			consult.letterheadAddress = consult.letterheadList[index].address;
			consult.letterheadPhone = consult.letterheadList[index].phone;

		};

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
					if(results.summaryItem.length === 0)
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
					if(results.summaryItem.length === 0)
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
					if(results.summaryItem.length === 0)
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
					if(results.summaryItem.length === 0)
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
					if(results.summaryItem.length === 0)
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
					if(results.summaryItem.length === 0)
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
			console.log('CONSULT: ', consult);

			if (!controller.consultWriteAccess && consult.id == null)
			{
				alert("You don't have right to save new consult");
				return false;
			}
			if (!controller.consultUpdateAccess)
			{
				alert("You don't have right to update consult");
				return false;
			}

			if (controller.invalidData()) return false;

			controller.consultSaving = true; //show saving banner
			controller.setAppointmentTime();

			consultService.saveRequest(consult).then(
				function success(results)
				{
					if (consult.id == null) $location.path("/record/" + consult.demographicId + "/consult/" + results.id);
				},
				function error(errors)
				{
					console.log(errors);
				});
			controller.setESendEnabled();
			controller.consultSaving = false; //hide saving banner
			controller.consultChanged = -1; //reset change count
			return true;
		};

		controller.close = function close()
		{
			if ($location.search().list === "patient") $location.path("/record/" + consult.demographicId + "/consults");
			else $location.path("/consults");
		};

		controller.sendFax = function sendFax()
		{
			var reqId = consult.id;
			var demographicNo = consult.demographicId;
			var letterheadFax = Juno.Common.Util.noNull(consult.letterhead.fax);
			var fax = Juno.Common.Util.noNull(consult.professionalSpecialist.faxNumber);
			//		var faxRecipients = *additional fax recipients (can be >1)*

			window.open("../fax/CoverPage.jsp?reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&fax=" + fax);
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

		controller.printPreview = function printPreview()
		{
			if (controller.invalidData()) return;

			window.open("../oscarEncounter/oscarConsultationRequest/printPdf2.do?reqId=" + consult.id + "&demographicNo=" + consult.demographicId);
			/*
					var printWin = window.open("","consultRequestPrintWin","width=830,height=900,scrollbars=yes,location=no");
					printWin.document.open();

					var replyTo = "Please reply ";
					if (consult.patientWillBook) {
						replyTo = "";
					} else {
						if (noNull(consult.letterheadList[0].name)!="") replyTo += "to " + consult.letterheadList[0].name;
						replyTo += " by fax or by phone with appointment";
					}

					var urgency = noNull(controller.urgencies[$("#urgency").val()].name);
					var referralDate = formatDate(consult.referralDate);
					var letterheadName = noNull(consult.letterheadList[$("#letterhead").val()].name);
					var letterheadAddress = noNull(consult.letterheadAddress);
					var letterheadPhone = noNull(consult.letterheadPhone);
					var letterheadFax = noNull(consult.letterheadFax);
					var serviceName = noNull(consult.serviceList[$("#serviceId").val()].serviceDesc);
					var consultantName = noNull(consult.professionalSpecialist.name);
					var consultantPhone = noNull(consult.professionalSpecialist.phoneNumber);
					var consultantFax = noNull(consult.professionalSpecialist.faxNumber);
					var consultantAddress = noNull(consult.professionalSpecialist.streetAddress);
					var patientName = noNull(consult.demographic.lastName)+", "+noNull(consult.demographic.firstName);
					var patientPhone = noNull(consult.demographic.phone);
					var patientWorkPhone = noNull(consult.demographic.alternativePhone);
					var patientBirthdate = formatDate(consult.demographic.dateOfBirth);
					var patientSex = noNull(consult.demographic.sexDesc);
					var patientHealthCardNo = noNull(consult.demographic.hin)+"-"+noNull(consult.demographic.ver);
					var patientChartNo = noNull(consult.demographic.chartNo);
					var patientAddress = "";
					if (consult.demographic.address!=null) {
						patientAddress = noNull(consult.demographic.address.address)+", "+noNull(consult.demographic.address.city)+", "+noNull(consult.demographic.address.province)+" "+noNull(consult.demographic.address.postal);
					}
					var appointmentDate = formatDate(consult.appointmentDate);
					var appointmentTime = formatTime(consult.appointmentTime);
					var reason = noNull(consult.reasonForReferral);
					var clinicalInfo = noNull(consult.clinicalInfo);
					var concurrentProblems = noNull(consult.concurrentProblems);
					var currentMeds = noNull(consult.currentMeds);
					var allergies = noNull(consult.allergies);
					var referringProvider = noNull(user.lastName)+", "+noNull(user.firstName);
					var mrp = "";
					if (consult.demographic.provider!=null) {
						mrp = noNull(consult.demographic.provider.lastName)+", "+noNull(consult.demographic.provider.firstName);
					}

					var reqId = consult.id;
					var demoId = consult.demographicId;
					var userId = user.providerNo;

					printWin.document.write("<html><style>body {width:800px;font-family:arial,verdana,tahoma,helvetica,sans serif;}div {text-align:center;}table {width:100%;}th {text-align:left;font-weight:bold;width:1;white-space:nowrap}td {vertical-align:top;}label {font-weight:bold;}em {font-size:small;}p {font-size:large;}</style><style media='print'>button {display: none;}</style><script>function printAttachments(){window.location.href='../oscarEncounter/oscarConsultationRequest/attachmentReport.jsp?reqId="+reqId+"&demographicNo="+demoId+"&providerNo="+userId+"';}</script><body><button onclick='window.print();'>Print</button><button onclick='printAttachments()'>Print attachments</button><button onclick='window.close()'>Close</button><div><label>Consultation Request</label><br/><label>"+replyTo+"</label></div><br/><table><tr><td><label>Date: </label>"+referralDate+"</td><td rowspan=6 width=10>&nbsp;</td><td><label>Status: </label>"+urgency+"</td></tr><tr><td colspan=2>&nbsp;</td></tr><tr><th>FROM:</th><th>TO:</th></tr><tr><td><p>"+letterheadName+"</p>"+letterheadAddress+"<br/><label>Tel: </label>"+letterheadPhone+"<br/><label>Fax: </label>"+letterheadFax+"</td><td><table><tr><th>Consultant:</th><td>"+consultantName+"</td></tr><tr><th>Service:</th><td>"+serviceName+"</td></tr><tr><th>Phone:</th><td>"+consultantPhone+"</td></tr><tr><th>Fax:</th><td>"+consultantFax+"</td></tr><tr><th>Address:</th><td>"+consultantAddress+"</td></tr></table></td></tr><tr><td colspan=2>&nbsp;</td></tr><tr><td><table><tr><th>Patient:</th><td>"+patientName+"</td></tr><tr><th>Address:</th><td>"+patientAddress+"</td></tr><tr><th>Phone:</th><td>"+patientPhone+"</td></tr><tr><th>Work Phone:</th><td>"+patientWorkPhone+"</td></tr><tr><th>Birthdate:</th><td>"+patientBirthdate+"</td></tr></table></td><td><table><tr><th>Sex:</th><td>"+patientSex+"</td></tr><tr><th>Health Card No:</th><td>"+patientHealthCardNo+"</td></tr><tr><th>Appointment date:</th><td>"+appointmentDate+"</td></tr><tr><th>Appointment time:</th><td>"+appointmentTime+"</td></tr><tr><th>Chart No:</th><td>"+patientChartNo+"</td></tr></table></td></tr></table><br/><table><tr><th>Reason for consultation:</th></tr><tr><td>"+reason+"<hr></td></tr><tr><th>Pertinent Clinical Information:</th></tr><tr><td>"+clinicalInfo+"<hr></td></tr><tr><th>Significant Concurrent Problems:</th></tr><tr><td>"+concurrentProblems+"<hr></td></tr><tr><th>Current Medications:</th></tr><tr><td>"+currentMeds+"<hr></td></tr><tr><th>Allergies:</th></tr><tr><td>"+allergies+"<hr></td></tr><tr><td><label>Referring Practitioner: </label>"+referringProvider+"</td></tr><tr><td><label>MRP: </label>"+mrp+"</td></tr><tr><td>&nbsp;</td></tr><tr><td><div><em>Created by: OSCAR The open-source EMR www.oscarcanada.org</em></div></td></tr></table></body></html>");
					printWin.document.close();
			*/
		}
		/* html for printPreview, kept here for easy reference
		<html>
		<style>
			body {width:800px;font-family:arial,verdana,tahoma,helvetica,sans serif;}
			div {text-align:center;}
			table {width:100%;}
			th {text-align:left;font-weight:bold;width:1;white-space:nowrap}
			td {vertical-align:top;}
			label {font-weight:bold;}
			em {font-size:small;}
			p {font-size:large;}
		</style>
		<style media='print'>
			button {display: none;}
		</style>
		<script>
			function printAttachments(){
				window.location.href='../oscarEncounter/oscarConsultationRequest/attachmentReport.jsp?reqId="+reqId+"&demographicNo="+demoId+"&providerNo="+userId+"';
			}
		</script>
		<body>
			<button onclick='window.print();'>Print</button>
			<button onclick='printAttachments()'>Print attachments</button>
			<button onclick='window.close()'>Close</button>
			<div>
				<label>Consultation Request</label><br/>
				<label>"+replyTo+"</label>
			</div>
			<br/>
			<table>
				<tr>
					<td>
						<label>Date: </label>"+referralDate+"
					</td>
					<td rowspan=6 width=10>&nbsp;</td>
					<td>
						<label>Status: </label>"+urgency+"
					</td>
				</tr>
				<tr><td colspan=2>&nbsp;</td></tr>
				<tr>
					<th>FROM:</th>
					<th>TO:</th>
				</tr>
				<tr>
					<td>
						<p>"+letterheadName+"</p>
						"+letterheadAddress+"<br/>
						<label>Tel: </label>"+letterheadPhone+"<br/>
						<label>Fax: </label>"+letterheadFax+"
					</td>
					<td>
						<table>
							<tr>
								<th>Consultant:</th>
								<td>"+consultantName+"</td>
							</tr>
							<tr>
								<th>Service:</th>
								<td>"+serviceName+"</td>
							</tr>
							<tr>
								<th>Phone:</th>
								<td>"+consultantPhone+"</td>
							</tr>
							<tr>
								<th>Fax:</th>
								<td>"+consultantFax+"</td>
							</tr>
							<tr>
								<th>Address:</th>
								<td>"+consultantAddress+"</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td colspan=2>&nbsp;</td></tr>
				<tr>
					<td>
						<table>
							<tr>
								<th>Patient:</th>
								<td>"+patientName+"</td>
							</tr>
							<tr>
								<th>Address:</th>
								<td>"+patientAddress+"</td>
							</tr>
							<tr>
								<th>Phone:</th>
								<td>"+patientPhone+"</td>
							</tr>
							<tr>
								<th>Work Phone:</th>
								<td>"+patientWorkPhone+"</td>
							</tr>
							<tr>
								<th>Birthdate:</th>
								<td>"+patientBirthdate+"</td>
							</tr>
						</table>
					</td>
					<td>
						<table>
							<tr>
								<th>Sex:</th>
								<td>"+patientSex+"</td>
							</tr>
							<tr>
								<th>Health Card No:</th>
								<td>"+patientHealthCardNo+"</td>
							</tr>
							<tr>
								<th>Appointment date:</th>
								<td>"+appointmentDate+"</td>
							</tr>
							<tr>
								<th>Appointment time:</th>
								<td>"+appointmentTime+"</td>
							</tr>
							<tr>
								<th>Chart No:</th>
								<td>"+patientChartNo+"</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
			<br/>
			<table>
				<tr><th>Reason for consultation:</th></tr>
				<tr><td>"+reason+"<hr></td></tr>
				<tr><th>Pertinent Clinical Information:</th></tr>
				<tr><td>"+clinicalInfo+"<hr></td></tr>
				<tr><th>Significant Concurrent Problems:</th></tr>
				<tr><td>"+concurrentProblems+"<hr></td></tr>
				<tr><th>Current Medications:</th></tr>
				<tr><td>"+currentMeds+"<hr></td></tr>
				<tr><th>Allergies:</th></tr>
				<tr><td>"+allergies+"<hr></td></tr>
				<tr><td><label>Referring Practitioner: </label>"+referringProvider+"</td></tr>
				<tr><td><label>MRP: </label>"+mrp+"</td></tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td><div><em>Created by: OSCAR The open-source EMR www.oscarcanada.org</em></div></td></tr>
			</table>
		</body>
		</html>
		*/
	}
]);