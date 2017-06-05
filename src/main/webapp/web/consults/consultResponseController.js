angular.module('Consults').controller('Consults.ConsultResponseController', [

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

		controller.consult = consult;

		consult.letterheadList = Juno.Common.Util.toArray(consult.letterheadList);
		consult.referringDoctorList = Juno.Common.Util.toArray(consult.referringDoctorList);
		consult.faxList = Juno.Common.Util.toArray(consult.faxList);
		consult.sendToList = Juno.Common.Util.toArray(consult.sendToList);
		if (consult.referringDoctor == null) consult.referringDoctor = {};

		//set attachments
		consult.attachments = Juno.Common.Util.toArray(consult.attachments);
		Juno.Consults.Common.sortAttachmentDocs(consult.attachments);

		//set default letterhead
		if (consult.letterheadName == null)
		{
			for (var i = 0; i < consult.letterheadList.length; i++)
			{
				if (consult.letterheadList[i].id == user.providerNo)
				{
					consult.letterheadName = consult.letterheadList[i].id;
					consult.letterheadAddress = consult.letterheadList[i].address;
					consult.letterheadPhone = consult.letterheadList[i].phone;
					break;
				}
			}
		}

		//set default fax if there's only 1
		if (consult.letterheadFax == null && consult.faxList.length == 1)
		{
			consult.letterheadFax = consult.faxList[0].faxNumber;
		}

		//show referringDoctor in list
		angular.forEach(consult.referringDoctorList, function(referringDoc)
		{
			if (referringDoc.id == consult.referringDoctor.id)
			{
				consult.referringDoctor = referringDoc;
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

		//set appointment time
		if (consult.appointmentTime != null)
		{
			var apptTime = new Date(consult.appointmentTime);
			consult.appointmentHour = Juno.Common.Util.pad0(apptTime.getHours());
			consult.appointmentMinute = Juno.Common.Util.pad0(apptTime.getMinutes());
		}

		controller.urgencies = staticDataService.getConsultUrgencies();
		controller.statuses = staticDataService.getConsultResponseStatuses();
		controller.hours = staticDataService.getHours();
		controller.minutes = staticDataService.getMinutes();

		//monitor data changed
		controller.consultChanged = -1;
		$scope.$watchCollection("consult", function()
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

		controller.changeLetterhead = function changeLetterhead()
		{
			var index = $("#letterhead").val();
			if (index == null) return;

			consult.letterheadAddress = consult.letterheadList[index].address;
			consult.letterheadPhone = consult.letterheadList[index].phone;
		};

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
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		controller.getMedicalHistory = function getMedicalHistory(boxId)
		{
			summaryService.getMedicalHistory(consult.demographic.demographicNo).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		controller.getOngoingConcerns = function getOngoingConcerns(boxId)
		{
			summaryService.getOngoingConcerns(consult.demographic.demographicNo).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		controller.getOtherMeds = function getOtherMeds(boxId)
		{
			summaryService.getOtherMeds(consult.demographic.demographicNo).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		controller.getReminders = function getReminders(boxId)
		{
			summaryService.getReminders(consult.demographic.demographicNo).then(
				function success(results)
				{
					controller.writeToBox(results, boxId);
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.invalidData = function invalidData()
		{
			if (controller.urgencies[$("#urgency").val()] == null)
			{
				alert("Please select an Urgency");
				return true;
			}
			if (consult.letterheadList[$("#letterhead").val()] == null)
			{
				alert("Please select a Letterhead");
				return true;
			}
			if (consult.referringDoctor == null)
			{
				alert("Please select a Referring Doctor");
				return true;
			}
			if (consult.demographic == null || consult.demographic == "")
			{
				alert("Error! Invalid patient!");
				return true;
			}
			return false;
		};

		controller.setAppointmentTime = function setAppointmentTime()
		{
			if (consult.appointmentHour != null && consult.appointmentMinute != null)
			{
				var apptTime = new Date();
				if (consult.appointmentTime != null) apptTime = new Date(consult.appointmentTime);
				apptTime.setHours(consult.appointmentHour);
				apptTime.setMinutes(consult.appointmentMinute);
				apptTime.setSeconds(0);
				consult.appointmentTime = apptTime;
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
				controller: AttachmentCtrl,
				windowClass: "attachment-modal-window"
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

		//attachment modal controller
		function AttachmentCtrl($scope, $uibModalInstance)
		{
			controller.atth = {};
			controller.atth.patientName = consult.demographic.lastName + ", " + consult.demographic.firstName;

			controller.atth.attachedDocs = consult.attachments;
			if (controller.atth.attachedDocs[0] != null) controller.atth.selectedAttachedDoc = controller.atth.attachedDocs[0];

			var consultId = 0;
			if (consult.id != null) consultId = consult.id;
			consultService.getResponseAttachments(consultId, consult.demographic.demographicNo).then(
				function success(results)
				{
					if (consult.availableDocs == null) consult.availableDocs = Juno.Common.Util.toArray(results);
					controller.atth.availableDocs = consult.availableDocs;
					Juno.Consults.Common.sortAttachmentDocs(controller.atth.availableDocs);
					if (controller.atth.availableDocs[0] != null) controller.atth.selectedAvailableDoc = controller.atth.availableDocs[0];
				},
				function error(errors)
				{
					console.log(errors);
				});

			controller.openDoc = function openDoc(doc)
			{
				window.open("../" + doc.url);
			};

			controller.attach = function attach()
			{
				if (controller.atth.selectedAvailableDoc == null) return;

				controller.atth.attachedDocs.push(controller.atth.selectedAvailableDoc);
				controller.atth.selectedAttachedDoc = controller.atth.selectedAvailableDoc;
				controller.atth.selectedAttachedDoc.attached = true;
				Juno.Consults.Common.sortAttachmentDocs(controller.atth.attachedDocs);

				var x = $("#selAvailDoc").val();
				controller.atth.availableDocs.splice(x, 1);
				if (x >= controller.atth.availableDocs.length) x = controller.atth.availableDocs.length - 1;
				controller.atth.selectedAvailableDoc = controller.atth.availableDocs[x];

				consult.attachmentsChanged = true;
			};

			controller.detach = function detach()
			{
				if (controller.atth.selectedAttachedDoc == null) return;

				controller.atth.availableDocs.push(controller.atth.selectedAttachedDoc);
				controller.atth.selectedAvailableDoc = controller.atth.selectedAttachedDoc;
				controller.atth.selectedAvailableDoc.attached = false;
				Juno.Consults.Common.sortAttachmentDocs(controller.atth.availableDocs);

				var x = $("#selAttachDoc").val();
				controller.atth.attachedDocs.splice(x, 1);
				if (x >= controller.atth.attachedDocs.length) x = controller.atth.attachedDocs.length - 1;
				controller.atth.selectedAttachedDoc = controller.atth.attachedDocs[x];

				consult.attachmentsChanged = true;
			};

			controller.done = function done()
			{
				$uibModalInstance.close();
			};
		}
		//end modal controller


		controller.save = function save()
		{
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

			consultService.saveResponse(consult).then(
				function success(results)
				{
					//update url for new consultation
					if (consult.id == null) $location.path("/record/" + consult.demographic.demographicNo + "/consultResponse/" + results.id);
				},
				function error(errors)
				{
					console.log(errors);
				});
			controller.consultSaving = false; //hide saving banner
			controller.consultChanged = -1; //reset change count
			return true;
		};

		controller.close = function close()
		{
			if ($location.search().list == "patient") $location.path("/record/" + consult.demographic.demographicNo + "/consultResponses");
			else $location.path("/consultResponses");
		};

		//fax & print functions
		var p_page1 = "<html><style>body{width:800px;font-family:arial,verdana,tahoma,helvetica,sans serif}table{width:100%}th{text-align:left;font-weight:bold;width:1;white-space:nowrap}td{vertical-align:top}label{font-weight:bold}em{font-size:small}.large{font-size:large}.center{text-align:center}</style><style media='print'>button{display:none}.noprint{display:none}</style><script>function printAttachments(url){window.open('../'+url);}</script><body>";

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

			window.open("../fax/CoverPage.jsp?consultResponsePage=" + consultResponsePage + "&reqId=" + reqId + "&demographicNo=" + demographicNo + "&letterheadFax=" + letterheadFax + "&fax=" + fax);
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

/* html for fax & print, kept here for easy reference
<html>
<style>
	body{width:800px;font-family:arial,verdana,tahoma,helvetica,sans serif}
	table{width:100%}
	th{text-align:left;font-weight:bold;width:1;white-space:nowrap}
	td{vertical-align:top}
	label{font-weight:bold}
	em{font-size:small}
	.large{font-size:large}
	.center{text-align:center}
</style>
<style media='print'>
	button{display:none}
	.noprint{display:none}
</style>
<script>
	function printAttachments(url){
		window.open('../'+url);
	}
</script>
<body>

<!-- Print preview page exclusive -->
	<!-- p_buttons -->
	<button onclick='window.print()'>Print</button>
	<button onclick='window.close()'>Close</button>
	<!-- p_buttons -->

	<!-- p_attachments, 1 or more -->
	<div class='noprint'>
		<button onclick=printAttachments('"+consult.attachments[i].url+"')>Print attachment</button> "+consult.attachments[i].displayName+"
	</div>
	<!-- p_attachments -->
<!-- Print preview page exclusive -->

	<div class='center'>
		<label class='large'>"+p_clinicName+"</label><br/>
		<label>Consultation Response</label><br/>
	</div>
	<br/>
	<table>
		<tr>
			<td>
				<label>Date: </label>"+p_responseDate+"
			</td>
			<td rowspan=6 width=10></td>
			<td>
				<label>Status: </label>"+p_urgency+"
			</td>
		</tr>
		<tr><td colspan=2></td></tr>
		<tr>
			<th>FROM:</th>
			<th>TO:</th>
		</tr>
		<tr>
			<td>
				<p class='large'>"+p_letterheadName+"</p>
				"+p_letterheadAddress+"<br/>
				<label>Tel: </label>"+p_letterheadPhone+"<br/>
				<label>Fax: </label>"+p_letterheadFax+"
			</td>
			<td>
				<table>
					<tr>
						<th>Referring Doctor:</th>
						<td>"+p_consultantName+"</td>
					</tr>
					<tr>
						<th>Phone:</th>
						<td>"+p_consultantPhone+"</td>
					</tr>
					<tr>
						<th>Fax:</th>
						<td>"+p_consultantFax+"</td>
					</tr>
					<tr>
						<th>Address:</th>
						<td>"+p_consultantAddress+"</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr><td colspan=2></td></tr>
		<tr>
			<td>
				<table>
					<tr>
						<th>Patient:</th>
						<td>"+p_patientName+"</td>
					</tr>
					<tr>
						<th>Address:</th>
						<td>"+p_patientAddress+"</td>
					</tr>
					<tr>
						<th>Phone:</th>
						<td>"+p_patientPhone+"</td>
					</tr>
					<tr>
						<th>Work Phone:</th>
						<td>"+p_patientWorkPhone+"</td>
					</tr>
					<tr>
						<th>Birthdate:</th>
						<td>"+p_patientBirthdate+"</td>
					</tr>
				</table>
			</td>
			<td>
				<table>
					<tr>
						<th>Sex:</th>
						<td>"+p_patientSex+"</td>
					</tr>
					<tr>
						<th>Health Card No:</th>
						<td>"+p_patientHealthCardNo+"</td>
					</tr>
					<tr>
						<th>Appointment date:</th>
						<td>"+p_appointmentDate+"</td>
					</tr>
					<tr>
						<th>Appointment time:</th>
						<td>"+p_appointmentTime+"</td>
					</tr>
					<tr>
						<th>Chart No:</th>
						<td>"+p_patientChartNo+"</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<br/>
	<table>
		<tr><th>Examination:</th></tr>
		<tr><td>"+p_examination+"<hr></td></tr>
		<tr><th>Impression:</th></tr>
		<tr><td>"+p_impression+"<hr></td></tr>
		<tr><th>Plan:</th></tr>
		<tr><td>"+p_plan+"<hr></td></tr>
		<tr><td></td></tr>
		<tr><th>Reason for consultation: (Date: "+p_referralDate+")</th></tr>
		<tr><td>"+p_reason+"<hr></td></tr>
		<tr><th>Pertinent Clinical Information:</th></tr>
		<tr><td>"+p_clinicalInfo+"<hr></td></tr>
		<tr><th>Significant Concurrent Problems:</th></tr>
		<tr><td>"+p_concurrentProblems+"<hr></td></tr>
		<tr><th>Current Medications:</th></tr>
		<tr><td>"+p_currentMeds+"<hr></td></tr>
		<tr><th>Allergies:</th></tr>
		<tr><td>"+p_allergies+"<hr></td></tr>
		<tr><td><label>Consultant: </label>"+p_provider+"</td></tr>
		<tr><td></td></tr>
		<tr><td><div class='center'><em>Created by: OSCAR The open-source EMR www.oscarcanada.org</em></div></td></tr>
	</table>
</body>
</html>
*/