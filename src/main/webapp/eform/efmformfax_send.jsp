<%--

    Copyright (c) 2008-2012 Indivica Inc.

    This software is made available under the terms of the
    GNU General Public License, Version 2, 1991 (GPLv2).
    License details are available via "indivica.ca/gplv2"
    and "gnu.org/licenses/gpl-2.0.html".

--%>
<%@page import="oscar.eform.actions.FaxAction"%>
<%@ page language="java"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic"%>

<%@page import="org.oscarehr.util.MiscUtils"%>
<html:html locale="true">
	<%

		String formId = (String) request.getAttribute("fdid");
		String[] faxRecipients = request.getParameterValues("faxRecipients");
		String providerId = request.getParameter("efmprovider_no");
		FaxAction bean = new FaxAction(request);
		boolean failed = false;
		String responseMsg;

		try
		{
			bean.faxForms(faxRecipients, formId, providerId);
			responseMsg = "Fax has been sent successfully.";

		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("An error occurred while faxing eForm.", e);
			responseMsg = "An error occurred sending the fax, please contact an administrator.";
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
		#faxError {
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
		<td class="MainTableTopRowLeftColumn">E From</td>
		<td class="MainTableTopRowRightColumn"></td>
	</tr>
	<tr style="vertical-align: top">
		<td class="MainTableLeftColumn" width="10%">&nbsp;</td>
		<td class="MainTableRightColumn">
		<table width="100%" height="100%">
			<tr>
				<td id=<%=failed ? "faxError" : "faxSuccess"%>>
					<%=responseMsg%>
				</td>
			</tr>
			<%
				if (!failed)
				{
			%>
			<tr>
				<td><bean:message
					key="oscarEncounter.oscarConsultationRequest.ConfirmConsultationRequest.msgClose5Sec" />
				</td>
			</tr>
			<tr>
				<td><a href="javascript: BackToOscar();"> <bean:message
					key="global.btnClose" /> </a></td>
			</tr>
			<%
				}
			%>
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

