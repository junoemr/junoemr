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
<%@page import="org.oscarehr.phr.PHRAuthentication"%>
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
		demographic = demoData.getDemographic(demoId);
	}

	ProviderData providerData = null;
	if (providerId != null && demographic != null)
	{
		providerData = 
			new oscar.oscarProvider.data.ProviderData(demographic.getProviderNo());
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

%>
<html:html locale="true">
<head>

<title>Email PDF Documents</title>

<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />

<script src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
<script type="text/javascript">

var submitting = false;                                                         

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

function disableButtons()
{
	document.getElementById("email_button").disabled = true;                    

	if(document.getElementById("email_provider_button"))                        
	{                                                                           
		document.getElementById("email_provider_button").disabled = true;       
	}                                                                           

	if(document.getElementById("email_patient_button"))                         
	{                                                                           
		document.getElementById("email_patient_button").disabled = true;        
	}   
}

function emailProvider()
{
	var name = 'provider';
	<% if(providerData != null) { %>
	var email = '<%= StringEscapeUtils.escapeHtml(providerData.getEmail()) %>';
	<% } %>
	_AddHiddenEmail(email) 
	submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');
}

function emailPatient()
{
	var name = 'patient';
	<% if(demographic != null) { %>
	var email = '<%= patientEmail %>';
	<% } %>
	_AddHiddenEmail(email) 
	submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');
}

function AddOtherEmailProvider() 
{
	var selected = jQuery("#otherEmailSelect option:selected");
	_AddOtherEmail(selected.text(),selected.val());
}

function AddOtherEmail() 
{
	var email = jQuery("#otherEmailInput").val();
	_AddOtherEmail(email,email);
}

function _AddHiddenEmail(email)                                                 
{                                                                               
	var html = "<input type='hidden' name='emailAddresses' value='"+email+"'></input>";
	jQuery("#emailAddresses").append(jQuery(html));                             
	updateEmailButton();                                                        
}  

function _AddOtherEmail(name, email) 
{
	var remove = "<a href='javascript:void(0);' onclick='removeRecipient(this)'>remove</a>";
	var html = "<li>"+name+"<b>, Email No: </b>"+email+ " " +remove+"<input type='hidden' name='emailAddresses' value='"+email+"'></input></li>";
	jQuery("#emailAddresses").append(jQuery(html));
	updateEmailButton();
}

function removeRecipient(el) 
{
	var el = jQuery(el);
	if (el) { el.parent().remove(); updateEmailButton(); }
	else { alert("Unable to remove recipient."); }
}

function hasEmailAddress() 
{
	return jQuery("#emailAddresses").children().size() > 0;
}

function updateEmailButton() 
{
	var disabled = !hasEmailAddress();
	document.getElementById("email_button").disabled = disabled;
}

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
</style>



</head>
<body class="bodystyle">
	<html:form action="/dms/sendEmailPDFs">
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
		<% // if there is a demographic, show some info on who we are sending the email to. 
		if(demographic != null) { %>
		<tr><td>
			<label for="patientDispName">Patient Name:</label>
			<input id="patientDispName"type="text" value="<%=patientDispalyName%>" disabled="disabled">
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
							 String  referralNo = ""; // TODO: add referal number to specialists ((String) displayServiceUtil.referralNoList.get(i)).trim();
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
	
	<div class="flexV">
		<label for="emailAddresses">Email To:</label>
		<div>
		<%if(demographic != null) { %>
			<!-- <label for="patientEmailAddr">Patient Email:</label> -->
			<input id="patientEmailAddr" name="patientEmailAddr" type="text" value="<%=patientEmail%>" disabled="disabled">
		<%}%>
			<ul id="emailAddresses"></ul></div>
		
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
	<%} %>
	
	<%if(emailActionType.equals("DOC")) { %>
    <logic:iterate name="docNo" id="doc">
        <input type="hidden" name="docNo" value='<bean:write name="doc" />' />
    </logic:iterate>
    <%} %>

	<!-- Show options for email recipients -->

	<!-- Show free-form phone number box -->

    <input type="button" id="email_button" disabled="disabled" value="<bean:message key="dms.documentReport.btnEmailPDF"/>"
        onclick="return submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do?emailActionType=<%=emailActionType%>/>');" />
	<% if(providerData != null && providerData.getEmail() != null && !providerData.getEmail().equals("")) { %>
    <input type="button" id="email_provider_button" value="Email to Patient's Provider"
        onclick="return emailProvider();" />
	<% } %>
	<% if(demographic != null && demographic.getEmail() != null && !demographic.getEmail().equals("")) { %>
    <input type="button" id="email_patient_button" value="Email to Patient"
        onclick="return emailPatient();" />
	<% } %>

</html:form>
</body>
</html:html>

