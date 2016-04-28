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
<%@page import="org.oscarehr.common.dao.UserPropertyDAO, org.oscarehr.common.model.UserProperty, org.springframework.web.context.support.WebApplicationContextUtils" %>
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

	String providerId = (String) session.getAttribute("user");
	String demoId = request.getParameter("demoId");

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

%>
<html:html locale="true">
<head>

<title>Email Documents</title>

<link rel="stylesheet" type="text/css" href="../share/css/OscarStandardLayout.css" />

<script src="//code.jquery.com/jquery-latest.js"></script>
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
	var email = '<%= StringEscapeUtils.escapeHtml(providerData.getEmail()) %>';
	_AddHiddenEmail(email) 
	submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');
}

function emailPatient()
{
	var name = 'patient';
	var email = '<%= StringEscapeUtils.escapeHtml(demographic.getEmail()) %>';
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



</head>
<body class="bodystyle">

<table class="MainTable" id="scrollNumber1" name="encounterTable"               
	style="margin: 0px;">                                                       
	<tr class="MainTableRowTop">                                                
		<td class="MainTableTopRowLeftColumn" width="60px">eDocs</td>           
		<td class="MainTableTopRowRightColumn">                                 
			<table class="TopStatusBar">                                            
				<tr>                                                                
					<td>Send Documents By Email</td>                                  
				</tr>                                                               
			</table>                                                                
		</td>                                                                   
	</tr>                                                                       
</table>    


	<table>
		<tr><td class="tite4">Add Email Addresses:</td></tr>
		<tr>
			<td>
				<%
				String rdohip = "";
				if (demographic!=null) 
				{
					String famDoc = demographic.getFamilyDoctor();
					if (famDoc != null && famDoc.trim().length() > 0) 
					{ 
						rdohip = SxmlMisc.getXmlContent(famDoc,"rdohip"); 
						rdohip = rdohip == null ? "" : rdohip.trim(); 
					}
				}
				%>
				<table width="100%">
				<tr>

					<td class="tite4" width="10%">  Providers: </td>
					<td class="tite3" width="20%">
						<select id="otherEmailSelect">
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

<html:form action="/dms/sendEmailPDFs">

	<ul id="emailAddresses">
	</ul>

	<input type="hidden" name="demoId" value="<%=demoId%>">
	<input type="hidden" name="providerId" value="<%=providerId%>">
    <logic:iterate name="docNo" id="doc">
        <input type="hidden" name="docNo" value='<bean:write name="doc" />' \>
    </logic:iterate>

	<!-- Show options for email recipients -->

	<!-- Show free-form phone number box -->


    <input type="button" id="email_button" disabled="disabled"
        value="<bean:message key="dms.documentReport.btnEmailPDF"/>"
        onclick="return submitForm('<rewrite:reWrite jspPage="sendEmailPDFs.do"/>');" />
	<% if(providerData.getEmail() != null && !providerData.getEmail().equals("")) { %>
    <input type="button" id="email_provider_button" value="Email to Patient's Provider"
        onclick="return emailProvider();" />
	<% } %>
	<% if(demographic.getEmail() != null && !demographic.getEmail().equals("")) { %>
    <input type="button" id="email_patient_button" value="Email to Patient"
        onclick="return emailPatient();" />
	<% } %>

</html:form>
</body>
</html:html>

