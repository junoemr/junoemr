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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ include file="/common/webAppContextAndSuperMgr.jsp" %>
<%@page import="org.oscarehr.email.service.EmailService" %>
<%@page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.util.SpringUtils" %>

<%
	EmailService emailService = SpringUtils.getBean(EmailService.class);
%>
<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	</head>

	<body background="../images/gray_bg.jpg" bgproperties="fixed">
	<center>
		<table border="0" cellspacing="0" cellpadding="0" width="90%">
			<tr bgcolor="#486ebd">
				<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
					<bean:message key="appointment.appointmentemailreminder.msgMainLabel"/></font></th>
			</tr>
		</table>
		<%
			boolean sentEmail = false;

			String appointment_no = request.getParameter("appointment_no");
			if (appointment_no == null)
			{
				// when coming from a newly added appointment, the ID is added as an attribute instead of coming in as a parameter
				appointment_no = (String) request.getAttribute("appointment_no");
			}
			if (appointment_no != null)
			{
				String loggedInProviderNo = LoggedInInfo.getLoggedInInfoFromSession(session).getLoggedInProviderNo();
				sentEmail = emailService.sendAppointmentTemplateEmail(appointment_no, loggedInProviderNo);
			}

			if (sentEmail)
			{
		%>
		<p>
		<h1><bean:message key="appointment.appointmentemailreminder.msgEmailSuccess"/></h1>

		<%
		}
		else
		{
		%>

		<p>
		<h1><bean:message key="appointment.appointmentemailreminder.msgEmailFailure"/></h1>

		<%
			}
		%>

		<p></p>
		<hr width="90%"/>
		<form>
			<input type="button" value="<bean:message key="global.btnClose"/>" onClick="closeit()">
		</form>
	</center>
	</body>
</html:html>
