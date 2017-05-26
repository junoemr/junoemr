'use strict';

window.Juno = window.Juno ||
{};
Juno.Common = Juno.Common ||
{};

Juno.Common.Util = {};

Juno.Common.Util.exists = function exists(object)
{
	// not undefined and not null
	return angular.isDefined(object) && object !== null;
};

Juno.Common.Util.isBlank = function isBlank(object)
{
	// undefined or null or empty string
	return !Juno.Common.Util.exists(object) || object === "";
};

Juno.Common.Util.toArray = function toArray(obj)
{ //convert single object to array
	if (obj instanceof Array) return obj;
	else if (obj == null) return [];
	else return [obj];
};

Juno.Common.Util.pad0 = function pad0(n)
{
	var s = n.toString();
	if (s.length == 1) s = "0" + s;
	return s;
};

Juno.Common.Util.noNull = function noNull(s)
{
	if (s == null) s = "";
	if (s instanceof String) s = s.trim();
	return s;
};

Juno.Common.Util.formatDate = function formatDate(d)
{
	d = Juno.Common.Util.noNull(d);
	if (d)
	{
		if (!(d instanceof Date)) d = new Date(d);
		d = d.getFullYear() + "-" + Juno.Common.Util.pad0(d.getMonth() + 1) + "-" + Juno.Common.Util.pad0(d.getDate());
	}
	return d;
};

Juno.Common.Util.formatTime = function formatTime(d)
{
	d = Juno.Common.Util.noNull(d);
	if (d)
	{
		if (!(d instanceof Date)) d = new Date(d);
		d = Juno.Common.Util.pad0(d.getHours()) + ":" + Juno.Common.Util.pad0(d.getMinutes());
	}
	return d;
};

Juno.Common.Util.sortAttachmentDocs = function sortAttachmentDocs(arrayOfDocs)
{
	arrayOfDocs.sort(function(doc1, doc2)
	{
		if (doc1.documentType < doc2.documentType) return -1;
		else if (doc1.documentType > doc2.documentType) return 1;
		else
		{
			if (doc1.displayName < doc2.displayName) return -1;
			else if (doc1.displayName > doc2.displayName) return 1;
		}
		return 0;
	});
};

Juno.Common.Util.addNewLine = function addNewLine(line, mssg)
{
	if (line == null || line.trim() == "") return mssg;

	if (mssg == null || mssg.trim() == "") mssg = line.trim();
	else mssg += "\n" + line.trim();

	return mssg;
};

Juno.Common.Util.getPrintPage2 = function getPrintPage2(p_urgency, p_letterheadName, consult, user)
{
	var p_clinicName = Juno.Common.Util.noNull(consult.letterheadList[0].name);
	var p_responseDate = Juno.Common.Util.formatDate(consult.responseDate);
	var p_referralDate = Juno.Common.Util.formatDate(consult.referralDate);
	var p_letterheadAddress = Juno.Common.Util.noNull(consult.letterheadAddress);
	var p_letterheadPhone = Juno.Common.Util.noNull(consult.letterheadPhone);
	var p_letterheadFax = Juno.Common.Util.noNull(consult.letterheadFax);
	var p_consultantName = Juno.Common.Util.noNull(consult.referringDoctor.name);
	var p_consultantPhone = Juno.Common.Util.noNull(consult.referringDoctor.phoneNumber);
	var p_consultantFax = Juno.Common.Util.noNull(consult.referringDoctor.faxNumber);
	var p_consultantAddress = Juno.Common.Util.noNull(consult.referringDoctor.streetAddress);
	var p_patientName = Juno.Common.Util.noNull(consult.demographic.lastName) + ", " + Juno.Common.Util.noNull(consult.demographic.firstName);
	var p_patientPhone = Juno.Common.Util.noNull(consult.demographic.phone);
	var p_patientWorkPhone = Juno.Common.Util.noNull(consult.demographic.alternativePhone);
	var p_patientBirthdate = Juno.Common.Util.formatDate(consult.demographic.dateOfBirth);
	var p_patientSex = Juno.Common.Util.noNull(consult.demographic.sexDesc);
	var p_patientHealthCardNo = Juno.Common.Util.noNull(consult.demographic.hin) + "-" + Juno.Common.Util.noNull(consult.demographic.ver);
	var p_patientChartNo = Juno.Common.Util.noNull(consult.demographic.chartNo);
	var p_patientAddress = "";
	if (consult.demographic.address != null)
	{
		p_patientAddress = Juno.Common.Util.noNull(consult.demographic.address.address) + ", " +
			Juno.Common.Util.noNull(consult.demographic.address.city) + ", " +
			Juno.Common.Util.noNull(consult.demographic.address.province) + " " +
			Juno.Common.Util.noNull(consult.demographic.address.postal);
	}
	var p_appointmentDate = Juno.Common.Util.formatDate(consult.appointmentDate);
	var p_appointmentTime = Juno.Common.Util.formatTime(consult.appointmentTime);
	var p_reason = Juno.Common.Util.noNull(consult.reasonForReferral);
	var p_examination = Juno.Common.Util.noNull(consult.examination);
	var p_impression = Juno.Common.Util.noNull(consult.impression);
	var p_plan = Juno.Common.Util.noNull(consult.plan);
	var p_clinicalInfo = Juno.Common.Util.noNull(consult.clinicalInfo);
	var p_concurrentProblems = Juno.Common.Util.noNull(consult.concurrentProblems);
	var p_currentMeds = Juno.Common.Util.noNull(consult.currentMeds);
	var p_allergies = Juno.Common.Util.noNull(consult.allergies);
	var p_provider = Juno.Common.Util.noNull(user.lastName) + ", " + Juno.Common.Util.noNull(user.firstName);

	return "<div class='center'><label class='large'>" + p_clinicName + "</label><br/><label>Consultation Response</label><br/></div><br/><table><tr><td><label>Date: </label>" + p_responseDate + "</td><td rowspan=6 width=10></td><td><label>Status: </label>" + p_urgency + "</td></tr><tr><td colspan=2></td></tr><tr><th>FROM:</th><th>TO:</th></tr><tr><td><p class='large'>" + p_letterheadName + "</p>" + p_letterheadAddress + "<br/><label>Tel: </label>" + p_letterheadPhone + "<br/><label>Fax: </label>" + p_letterheadFax + "</td><td><table><tr><th>Referring Doctor:</th><td>" + p_consultantName + "</td></tr><tr><th>Phone:</th><td>" + p_consultantPhone + "</td></tr><tr><th>Fax:</th><td>" + p_consultantFax + "</td></tr><tr><th>Address:</th><td>" + p_consultantAddress + "</td></tr></table></td></tr><tr><td colspan=2></td></tr><tr><td><table><tr><th>Patient:</th><td>" + p_patientName + "</td></tr><tr><th>Address:</th><td>" + p_patientAddress + "</td></tr><tr><th>Phone:</th><td>" + p_patientPhone + "</td></tr><tr><th>Work Phone:</th><td>" + p_patientWorkPhone + "</td></tr><tr><th>Birthdate:</th><td>" + p_patientBirthdate + "</td></tr></table></td><td><table><tr><th>Sex:</th><td>" + p_patientSex + "</td></tr><tr><th>Health Card No:</th><td>" + p_patientHealthCardNo + "</td></tr><tr><th>Appointment date:</th><td>" + p_appointmentDate + "</td></tr><tr><th>Appointment time:</th><td>" + p_appointmentTime + "</td></tr><tr><th>Chart No:</th><td>" + p_patientChartNo + "</td></tr></table></td></tr></table><br/><table><tr><th>Examination:</th></tr><tr><td>" + p_examination + "<hr></td></tr><tr><th>Impression:</th></tr><tr><td>" + p_impression + "<hr></td></tr><tr><th>Plan:</th></tr><tr><td>" + p_plan + "<hr></td></tr><tr><td></td></tr><tr><th>Reason for consultation: (Date: " + p_referralDate + ")</th></tr><tr><td>" + p_reason + "<hr></td></tr><tr><th>Pertinent Clinical Information:</th></tr><tr><td>" + p_clinicalInfo + "<hr></td></tr><tr><th>Significant Concurrent Problems:</th></tr><tr><td>" + p_concurrentProblems + "<hr></td></tr><tr><th>Current Medications:</th></tr><tr><td>" + p_currentMeds + "<hr></td></tr><tr><th>Allergies:</th></tr><tr><td>" + p_allergies + "<hr></td></tr><tr><td><label>Consultant: </label>" + p_provider + "</td></tr><tr><td></td></tr><tr><td><div class='center'><em>Created by: OSCAR The open-source EMR www.oscarcanada.org</em></div></td></tr></table></body></html>";
};