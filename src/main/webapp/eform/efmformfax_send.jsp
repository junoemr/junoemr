<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@page import="org.oscarehr.fax.exception.FaxException"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<%@page import="org.oscarehr.fax.model.FaxOutbound"%>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound" %>
<%@ page import="oscar.eform.actions.FaxAction" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<html:html locale="true">
	<%

		String formId = (String) request.getAttribute("fdid");
		String[] faxRecipients = request.getParameterValues("faxRecipients");
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerId = loggedInInfo.getLoggedInProviderNo();
		FaxAction bean = new FaxAction(request);
		boolean failed = false;
		List<String> errorMessages = new ArrayList<String>(faxRecipients.length);

		try
		{
			List<FaxOutboxTransferOutbound> faxTransferList = bean.faxForms(faxRecipients, formId, providerId);
			for(FaxOutboxTransferOutbound transfer : faxTransferList)
			{
				if(FaxOutbound.Status.QUEUED.equals(transfer.getSystemStatus()))
				{
					errorMessages.add("Failed to send fax, it has been queued for automatic resend. " +
							"Reason: " + transfer.getSystemStatusMessage());
				}
				else if(FaxOutbound.Status.ERROR.equals(transfer.getSystemStatus()))
				{
					errorMessages.add("Failed to send fax. Check account settings. " +
							"Reason: " + transfer.getSystemStatusMessage());
					failed=true;
				}
			}
		}
		catch(FaxException e)
		{
			MiscUtils.getLogger().error("An error occurred while faxing eForm.", e);
			errorMessages.add(e.getUserFriendlyMessage(request.getLocale()));
			failed = true;
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("An error occurred while faxing eForm.", e);
			errorMessages.add("An error occurred sending the fax, please contact an administrator.");
			failed = true;
		}

	%>
<head>
	<title>Fax</title>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
	<style>
		table {
			font-size: initial;
		}
		.faxError {
			font-size: 1.25em;
			color: red;
		}
	</style>
	<html:base />
</head>
<script language="javascript">
function BackToOscar() {
       window.close();
}

<%
	if (!failed)
	{
%>
window.onload = function()
{
	var seconds = 5;
	setTimeout(window.close, seconds * 500);
};
<%
	}
%>
</script>


<body topmargin="0" leftmargin="0" vlink="#0000FF">
<!--  -->
<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">E Form</td>
		<td class="MainTableTopRowRightColumn"></td>
	</tr>
	<tr style="vertical-align: top">
		<td class="MainTableLeftColumn" width="10%">&nbsp;</td>
		<td class="MainTableRightColumn">
		<table width="100%" height="100%">

			<%
				if(!errorMessages.isEmpty())
				{
			%>
				<h1><bean:message
					key="oscarEncounter.oscarConsultationRequest.ConfirmConsultationRequest.msgEncounteredErrors"/></h1><%
					for(String errorMessage : errorMessages)
					{
					%>
					<tr>
						<td>
							<span class="faxError"><%=errorMessage%></span>
						</td>
					</tr>
					<%
					}
				}
				else
				{
				%>
					<tr>
						<td id='faxSuccess'>
							Fax has been sent successfully.
						</td>
					</tr>
				<%
				}

				if (!failed)
				{
				%>
				<tr>
					<td><bean:message
						key="oscarEncounter.oscarConsultationRequest.ConfirmConsultationRequest.msgClose5Sec" />
					</td>
				</tr>
				<%
				}
			%>
			<tr>
				<td><a href="javascript: BackToOscar();"> <bean:message
						key="global.btnClose" /> </a></td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td class="MainTableBottomRowLeftColumn"></td>
		<td class="MainTableBottomRowRightColumn"></td>
	</tr>
</table>
</body>
</html:html>

