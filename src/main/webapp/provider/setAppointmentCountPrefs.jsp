<%--
Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

This software was written for
CloudPractice Inc.
Victoria, British Columbia
Canada
--%>
<%@page contentType="text/html"%>
<%@ include file="/casemgmt/taglibs.jsp"%>
<%
if(session.getValue("user") == null)
{
	response.sendRedirect("../logout.htm");
}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="ctx" value="${pageContext.request.contextPath}"	scope="request" />
<html:html>
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
		<html:base />
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title><bean-el:message key="${providertitle}" /></title>

		<link rel="stylesheet" type="text/css" href="../oscarEncounter/encounterStyles.css">
		<script src="<c:out value="${ctx}"/>/share/javascript/prototype.js"	type="text/javascript"></script>
		<script src="<c:out value="${ctx}"/>/share/javascript/scriptaculous.js"	type="text/javascript"></script>
	</head>

<body class="BodyStyle" vlink="#0000FF">

<table class="MainTable" id="scrollNumber1" name="encounterTable">
	<tr class="MainTableTopRow">
		<td class="MainTableTopRowLeftColumn">
			<bean-el:message key="${providermsgPrefs}" />
		</td>
		<td style="color: white" class="MainTableTopRowRightColumn">
			<bean-el:message key="${providermsgProvider}" />
		</td>
	</tr>
	<tr>
		<td class="MainTableLeftColumn">&nbsp;</td>
		<td class="MainTableRightColumn">
		<%if( request.getAttribute("status") == null ){%>
			<bean-el:message key="${providermsgEdit}" />

            <html:form action="/setAppointmentCountPrefs.do">
				<input type="hidden" name="method" value="<c:out value="${method}"/>">
				<br/>
				Enabled: <html:checkbox property="appointmentCountEnabled.value" />
				<br/>
				Include cancelled appointments: <html:checkbox property="appointmentCountIncludeCancelled.value" />
				<br/>
				Include no-show appointments: <html:checkbox property="appointmentCountIncludeNoShow.value" />
				<br/>
				Include appointments not associated with a patient: <html:checkbox property="appointmentCountIncludeNoPatient.value" />
                <br/>
                <html:submit property="btnApply"/>
			</html:form>

		<%}else {%>
			<bean-el:message key="${providermsgSuccess}" /> <br>
		<%}%>
		</td>
	</tr>
	<tr>
		<td class="MainTableBottomRowLeftColumn"></td>
		<td class="MainTableBottomRowRightColumn"></td>
	</tr>
</table>
<script type="text/javascript">

	var setCheckboxesEnabled = function()
	{
		var disabled = !document.getElementsByName('appointmentCountEnabled.value')[0].checked;
		document.getElementsByName('appointmentCountIncludeCancelled.value')[0].disabled = disabled;
		document.getElementsByName('appointmentCountIncludeNoShow.value')[0].disabled = disabled;
		document.getElementsByName('appointmentCountIncludeNoPatient.value')[0].disabled = disabled;
	};

	document.getElementsByName('appointmentCountEnabled.value')[0].addEventListener('change', setCheckboxesEnabled);
	setCheckboxesEnabled();

</script>
</body>
</html:html>
