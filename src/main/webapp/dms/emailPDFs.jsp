<%--

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

--%>

<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.oscarehr.util.LocaleUtils"%>
<%@page import="org.oscarehr.phr.util.MyOscarUtils"%>
<%@page import="org.oscarehr.util.MiscUtils"%>
<%@page import="org.oscarehr.util.LoggedInInfo"%>
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, oscar.OscarProperties, org.oscarehr.common.model.UserProperty, org.springframework.web.context.support.WebApplicationContextUtils" %>
<%@page import="org.oscarehr.common.model.Demographic"%>
<%@page import="oscar.oscarDemographic.data.DemographicData"%>
<%@page import="org.oscarehr.common.model.Provider"%>
<%@page import="oscar.oscarProvider.data.ProviderData"%>
<%@page import="oscar.SxmlMisc"%>

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>
<%@ taglib uri="/WEB-INF/rewrite-tag.tld" prefix="rewrite"%>
<%@ taglib uri="/WEB-INF/oscarProperties-tag.tld" prefix="oscarProp"%>
<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<jsp:useBean id="displayServiceUtil" scope="request" class="oscar.oscarEncounter.oscarConsultationRequest.config.pageUtil.EctConDisplayServiceUtil" />
<%
    LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

	// Load the specialists
	displayServiceUtil.estSpecialist();
	OscarProperties props = OscarProperties.getInstance();

	String providerId = (String) session.getAttribute("user");
	String demoId = request.getParameter("demoId");
	String emailActionType = request.getParameter("emailActionType");

	DemographicData demoData = null;
	Demographic demographic = null;
	if (demoId != null)
	{
		demoData = new oscar.oscarDemographic.data.DemographicData();
		demographic = demoData.getDemographic(loggedInInfo, demoId);
	}

	ProviderData providerData = null;
	if (providerId != null && demographic != null)
	{
		providerData = new oscar.oscarProvider.data.ProviderData(demographic.getProviderNo());
	}
	
	String emailSubject = OscarProperties.getInstance().getProperty("document_email_subject", "");
	String emailBody = OscarProperties.getInstance().getProperty("document_email_body", "");
	String emailName = OscarProperties.getInstance().getProperty("document_email_name", "");
	
	String patientEmail = null;
	String patientDispalyName = null;
	if(demographic != null) {
		patientEmail = StringEscapeUtils.escapeHtml(demographic.getEmail());
		patientDispalyName = StringEscapeUtils.escapeHtml(demographic.getDisplayName());
	}
	String providerEmail = null;
	String providerDispalyName = null;
	if(providerData != null) {
		providerEmail = providerData.getEmail();
		providerDispalyName = providerData.getDisplayName();
	}
	
	String[] printHpList = request.getParameterValues("printHP");

%>
<html:html locale="true">
<head>

<title>Email PDF Documents</title>

<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />

<script src="../share/javascript/jquery/jquery-2.2.4.min.js"></script>
<script>

var submitting = false;
var demoNo = '<%=demoId%>';
var demoEmail = '<%= patientEmail%>';
var providerEmail = '<%= providerEmail%>';

function submitForm(actionPath)
{
	if(submitting)
	{
		return false;
	}
	submitting = true;

	disableButtons();                                                           
	var form = document.forms[0];
	form.action = actionPath;
	form.submit();

	return true;
}
function emailProvider(actionPath) {
	clearEmailList();
	appendEmail(providerEmail);
	submitForm(actionPath);
}
function emailPatient(actionPath) {
	clearEmailList();
	appendEmail(demoEmail);
	submitForm(actionPath);
}
function disableButtons()
{
	document.getElementById("email_button").disabled = true;
	document.getElementById("email_patient_button").disabled = true;
	document.getElementById("email_provider_button").disabled = true;
}
function AddOtherEmailProvider() 
{
	var selected = jQuery("#otherEmailSelect option:selected");
	appendEmail(selected.val());
}
function AddOtherEmail() 
{
	var email = jQuery("#otherEmailInput").val();
	appendEmail(email);
}
function hasEmailAddress() 
{
	return jQuery("#emailAddresses").children().size() > 0;
}
function updateEmailButton()
{
	var disableEmail = !hasEmailAddress();
	document.getElementById("email_button").disabled = disableEmail;
}
function appendEmail(email) {
	var $div = $("<div>", {class: "flexH"});
	var $input = $("<input>", {
		type:"text",
		readonly: true,
		name: "emailAddresses",
		value: email,
		class: "flexInput"
	});
	var $remove = $("<button>", {
		type:"button",
		text: "remove",
		click: function() {
			$div.remove();
			updateEmailButton();
			return false;
		}
	});
	
	$div.append($input).append($remove);
	
	$("#emailAddresses").append($div);
	
	updateEmailButton();
}
function clearEmailList() {
	$("#emailAddresses").empty();
}

$(function() {
	
	var disablePatientEmail = (demoEmail == null || demoEmail === "" || demoEmail === "null");
	document.getElementById("email_patient_button").disabled = disablePatientEmail;
	
	var disableProviderEmail = (providerEmail == null || providerEmail === "" || providerEmail === "null");
	document.getElementById("email_provider_button").disabled = disableProviderEmail;
	
	updateEmailButton();
	
	var form = document.forms[0];
	
	<%
	// have to generate the form parameters because oscar hates simple things
	if(emailActionType.equals("PREV") && printHpList != null) {
		for(String printHp : printHpList) {
			String pHdr = "preventionHeader" + printHp;
			%>
			var header = "<%=request.getParameter(pHdr)%>";
			$(form).append($("<input>", {
				type:"hidden",
				name:"printHP",
				value:"<%=printHp%>"
			}));
			$(form).append($("<input>", {
				type:"hidden",
				name:"<%=pHdr%>",
				value: header
			}));
			
			<%
			int i=0;
			while(request.getParameter("preventProcedureAge" + printHp + "-"+ Integer.toString(i)) != null ) {
				String ppAge = "preventProcedureAge" + printHp + "-"+ Integer.toString(i);
				String ppDate = "preventProcedureDate" + printHp + "-"+ Integer.toString(i);
				i++;
			%>
				var age = "<%=request.getParameter(ppAge)%>";
				var date = "<%=request.getParameter(ppDate)%>";
				$(form).append($("<input>", {
					type:"hidden",
					name:"<%=ppAge%>",
					value: age
				}));
				$(form).append($("<input>", {
					type:"hidden",
					name:"<%=ppDate%>",
					value: date
				}));
			<%
			}
		}
	}%>
	
});
</script>

<style>
	.flexH {
	display: flex;
	flex-direction: row;
	}
	.flexV {
	display: flex;
	flex-direction: column;
	}
	.flexInput {
	flex: 1;
	max-width: 500px;
	}
	#patientDispName, #providerDispName {
	float: right;
	width: 80%;
	}
	#emailAddresses {
	min-height: 15px;
	}
</style>

</head>
<body class="bodystyle">
<table class="MainTable" id="scrollNumber1" name="encounterTable" style="margin: 0px;">
	<tr class="MainTableRowTop">
		<td class="MainTableTopRowLeftColumn" width="60px">eDocs</td>
		<td class="MainTableTopRowRightColumn">
			<table class="TopStatusBar">
				<tr>
					<td>Send PDF Documents By Email</td>
				</tr>
			</table>
		</td>
	</tr>
</table>

<table>
	<% // if there is a demographic, show some info.
	if(demographic != null) { %>
	<tr><td>
		<label for="patientDispName">Patient Name:</label>
		<input id="patientDispName" type="text" value="<%=patientDispalyName%>" disabled="disabled">
	</td></tr>
<% }
	// if there is a provider, show some info.
	if(providerData != null) { %>
	<tr><td>
		<label for="providerDispName">Provider:</label>
		<input id="providerDispName" type="text" value="<%=providerDispalyName%>" disabled="disabled">
	</td></tr>
<% } %>
	<tr><td class="tite4">Add Email Addresses:</td></tr>
	<tr>
		<td>
			<%
				String rdohip = "";
					if (demographic != null) {
						String famDoc = demographic.getFamilyDoctor();
						if (famDoc != null && famDoc.trim().length() > 0) {
							rdohip = SxmlMisc.getXmlContent(famDoc, "rdohip");
							rdohip = rdohip == null ? "" : rdohip.trim();
						}
					}
			%>
			<table width="100%">
			<tr>
				<td class="tite4" width="10%">  Providers: </td>
				<td class="tite3" width="20%">
					<select id="otherEmailSelect" style="width: 100%;">
					<%
					String rdName = "";
					String rdEmailAddress = "";
					for (int i=0;i < displayServiceUtil.specIdVec.size(); i++) 
					{
						 String  specId     = (String) displayServiceUtil.specIdVec.elementAt(i);
						 String  fName      = (String) displayServiceUtil.fNameVec.elementAt(i);
						 String  lName      = (String) displayServiceUtil.lNameVec.elementAt(i);
						 String  proLetters = (String) displayServiceUtil.proLettersVec.elementAt(i);
						 String  address    = (String) displayServiceUtil.addressVec.elementAt(i);
						 String  phone      = (String) displayServiceUtil.phoneVec.elementAt(i);
						 String  fax        = (String) displayServiceUtil.faxVec.elementAt(i);
						 String  email      = (String) displayServiceUtil.emailVec.elementAt(i);
						 String  referralNo = ""; // TODO-legacy: add referal number to specialists ((String) displayServiceUtil.referralNoList.get(i)).trim();
						 if (rdohip != null && !"".equals(rdohip) && rdohip.equals(referralNo)) 
						 {
							 rdName = String.format("%s, %s", lName, fName);
							 rdEmailAddress = email;
						 }
						 if (!"".equals(email)) 
						 {
						 %>

						 <option value="<%= email %>"> <%= String.format("%s, %s", lName, fName) %> </option>
						 <%
						 }
					}
					%>
					</select>
				</td>
				<td class="tite3">
					<button onclick="AddOtherEmailProvider(); return false;">Add Provider</button>
				</td>
			</tr>
			<tr>
				<td class="tite4" width="20%"> Other Email Address: </td>
				<td class="tite3" width="32%">
					<input type="text" id="otherEmailInput"></input>
				</td>
				<td class="tite3">
					<button onclick="AddOtherEmail(); return false;">Add Other Address</button>
				</td>
			</tr>
			</table>
		</td>
	</tr>
</table>
	
<html:form action="/dms/sendEmailPDFs">
	<div class="flexV">
		<label for="emailAddresses">Email To:</label>
		<div id="emailAddresses"></div>
		
		<label for="emailSubject">Subject:</label>
		<input id="emailSubject" name="emailSubject" type="text" value="<%=emailSubject%>">
		<label for="emailBody">Message:</label>
		<textarea id="emailBody" name="emailBody" rows=10><%=emailBody%></textarea>
	</div>

	<input type="hidden" name="demoId" value="<%=demoId%>">
	<input type="hidden" name="providerId" value="<%=providerId%>">
	<input type="hidden" name="emailActionType"id="emailActionType" value="<%=emailActionType%>">
	<%
	// add a bunch of hidden input because they are required by the rx pdf generator
	if(emailActionType.equals("RX")) { %>
	<input type="hidden" id="__title" name="__title" value="<%=request.getParameter("__title")%>">
	<input type="hidden" id="imgFile" name="imgFile" value="<%=request.getParameter("imgFile")%>">
	<input type="hidden" id="sigDoctorName" name="sigDoctorName" value="<%=request.getParameter("sigDoctorName")%>">
	<input type="hidden" id="pracNo" name="pracNo" value="<%=request.getParameter("pracNo")%>">
	<input type="hidden" id="useSC" name="useSC" value="<%=request.getParameter("useSC")%>">
	<input type="hidden" id="scAddress" name="scAddress" value="<%=request.getParameter("scAddress")%>">
	<input type="hidden" id="clinicName" name="clinicName" value="<%=request.getParameter("clinicName")%>">
	<input type="hidden" id="clinicPhone" name="clinicPhone" value="<%=request.getParameter("clinicPhone")%>">
	<input type="hidden" id="clinicFax" name="clinicFax" value="<%=request.getParameter("clinicFax")%>">
	<input type="hidden" id="patientPhone" name="patientPhone" value="<%=request.getParameter("patientPhone")%>">
	<input type="hidden" id="patientCityPostal" name="patientCityPostal" value="<%=request.getParameter("patientCityPostal")%>">
	<input type="hidden" id="patientAddress" name="patientAddress" value="<%=request.getParameter("patientAddress")%>">
	<input type="hidden" id="patientName" name="patientName" value="<%=request.getParameter("patientName")%>">
	<input type="hidden" id="patientHIN" name="patientHIN" value="<%=request.getParameter("patientHIN")%>">
	<input type="hidden" id="patientChartNo" name="patientChartNo" value="<%=request.getParameter("patientChartNo")%>">
	<input type="hidden" id="showPatientDOB" name="showPatientDOB" value="<%=request.getParameter("showPatientDOB")%>">
	<input type="hidden" id="patientDOB" name="patientDOB" value="<%=request.getParameter("patientDOB")%>">
	<input type="hidden" id="rxDate" name="rxDate" value="<%=request.getParameter("rxDate")%>">
	<input type="hidden" id="additNotes" name="additNotes" value="<%=request.getParameter("additNotes")%>">
	<input type="hidden" id="rx" name="rx" value="<%=request.getParameter("rx")%>">
	<input type="hidden" id="scriptId" name="scriptId" value="<%=request.getParameter("scriptId")%>">
	<input type="hidden" id="__method" name="__method" value="<%=request.getParameter("__method")%>">
	<input type="hidden" id="origPrintDate" name="origPrintDate" value="<%=request.getParameter("origPrintDate")%>">
	<input type="hidden" id="numPrints" name="numPrints" value="<%=request.getParameter("numPrints")%>">
	<input type="hidden" id="sigDoctorName" name="sigDoctorName" value="<%=request.getParameter("sigDoctorName")%>">	
	<%} 
	// most of the inputs for preventions are generated on document load.
	if(emailActionType.equals("PREV")) {%>
	<input type="hidden" id="nameAge" name="nameAge" value="<%=request.getParameter("nameAge")%>">
	<input type="hidden" id="hin" name="hin" value="<%=request.getParameter("hin")%>">
	<input type="hidden" id="mrp" name="mrp" value="<%=request.getParameter("mrp")%>">
	<%}
	if(emailActionType.equals("DOC")) { %>
    <logic:iterate name="docNo" id="doc">
        <input type="hidden" name="docNo" value='<bean:write name="doc" />' />
    </logic:iterate>
    <%} %>
    <input type="button" id="email_button" value="<bean:message key="dms.documentReport.btnEmailPDF"/>"
        onclick="return submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');" />
    <input type="button" id="email_patient_button" value="Email to Patient Only"
        onclick="return emailPatient('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');" />
    <input type="button" id="email_provider_button" value="Email to Provider Only"
        onclick="return emailProvider('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');" />

</html:form>
</body>
</html:html>

