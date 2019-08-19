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
<%
  if (session.getAttribute("user") == null)    response.sendRedirect("../logout.jsp");
%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@page import="org.oscarehr.common.dao.AppointmentArchiveDao" %>
<%@page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@page import="org.oscarehr.common.model.Appointment" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%
	AppointmentArchiveDao appointmentArchiveDao = (AppointmentArchiveDao)SpringUtils.getBean("appointmentArchiveDao");
	OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
</head>
<body onload="start()">
<center>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		<bean:message key="appointment.appointmentdeletearecord.msgLabel" /></font></th>
	</tr>
</table>
<%
	Appointment appt = appointmentDao.find(Integer.parseInt(request.getParameter("appointment_no")));
	appointmentArchiveDao.archiveAppointment(appt);
	int rowsAffected=0;
	if(appt != null) {
		LogAction.addLogEntry(LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo(),
				appt.getDemographicNo(), LogConst.ACTION_DELETE, LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS, String.valueOf(appt.getId()), request.getRemoteAddr());
		appointmentDao.remove(appt.getId());
		rowsAffected=1;
	}
	if (rowsAffected == 1) {
%>
<p>
<h1><bean:message
	key="appointment.appointmentdeletearecord.msgDeleteSuccess" /></h1>

<script LANGUAGE="JavaScript">
	self.opener.refresh();
	self.close();
</script> <%
	} else {
%>
<p>
<h1><bean:message
	key="appointment.appointmentdeletearecord.msgDeleteFailure" /></h1>

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
