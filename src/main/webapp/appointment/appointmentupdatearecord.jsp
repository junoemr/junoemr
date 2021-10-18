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

<%@ taglib uri="/WEB-INF/security.tld" prefix="security"%>
<%
    String roleName$ = (String)session.getAttribute("userrole") + "," + (String) session.getAttribute("user");
    boolean authed=true;
%>
<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment" rights="u" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_appointment");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@ page import="org.oscarehr.common.OtherIdManager"%>
<%@ page import="org.oscarehr.common.dao.AppointmentArchiveDao"%>
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao"%>
<%@ page import="org.oscarehr.common.model.Appointment"%>
<%@ page import="org.oscarehr.event.EventService"%>
<%@ page import="org.oscarehr.telehealth.service.MyHealthAccessService"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ page import="org.oscarehr.util.SpringUtils" %>
<%@ page import="oscar.log.LogAction" %>
<%@ page import="oscar.log.LogConst" %>
<%@ page import="oscar.util.ConversionUtils" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="java.util.Date" %>
<%@ page import="oscar.util.StringUtils" %>
<%
	AppointmentArchiveDao appointmentArchiveDao = SpringUtils.getBean(AppointmentArchiveDao.class);
	OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
	MyHealthAccessService myHealthAccessService = SpringUtils.getBean(MyHealthAccessService.class);
%>
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
</head>

<body>
<center>
<table border="0" cellspacing="0" cellpadding="0" width="90%">
	<tr bgcolor="#486ebd">
		<th align="CENTER"><font face="Helvetica" color="#FFFFFF">
		<bean:message key="appointment.appointmentupdatearecord.msgMainLabel" /></font></th>
	</tr>
</table>
	<%
 		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String updateuser = loggedInInfo.getLoggedInProviderNo();
		final String cancelledAppointment = "Cancel Appt";

		String apptNo = request.getParameter("appointment_no");
		Appointment appt = appointmentDao.find(Integer.parseInt(apptNo));
		appointmentArchiveDao.archiveAppointment(appt);

		// Try to read all the request parameters and use default (previous) values where appropriate
		String buttonCancel = request.getParameter("buttoncancel");

		String status = ConversionUtils.getStringOrDefaultValue(request.getParameter("status"), appt.getAppointmentStatus());
		String changedStatus = null;
		String headRecord = ConversionUtils.getStringOrDefaultValue(request.getParameter("demographic_no"), "0");
		Date appointmentDate = ConversionUtils.fromDateString(request.getParameter("appointment_date"));
		Date startDate = ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time"));
		Date endDate = ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time"));

		String appointmentName = ConversionUtils.getStringOrDefaultValue(request.getParameter("keyword"), appt.getName());
		String notes = StringUtils.transformNullInEmptyString(request.getParameter("notes"));
		String reason = StringUtils.transformNullInEmptyString(request.getParameter("reason"));
		String location = ConversionUtils.getStringOrDefaultValue(request.getParameter("location"), appt.getLocation());
		String isVirtual = ConversionUtils.getStringOrDefaultValue(request.getParameter("isVirtual"), "off");
		String resources = StringUtils.transformNullInEmptyString(request.getParameter("resources"));
		String type = StringUtils.transformNullInEmptyString(request.getParameter("type"));
		String style = ConversionUtils.getStringOrDefaultValue(request.getParameter("style"), appt.getStyle());
		String billing = ConversionUtils.getStringOrDefaultValue(request.getParameter("billing"), appt.getBilling());
		String remarks = ConversionUtils.getStringOrDefaultValue(request.getParameter("remarks"), appt.getRemarks());
		String urgency = StringUtils.transformNullInEmptyString(request.getParameter("urgency"));

		// Virtual appointments can not have their locations changed
		if (appt.getIsVirtual() && !location.equals(appt.getLocation()))
		{
			throw new IllegalArgumentException("Appointment ID: " + appt.getId() + " can't be updated. Virtual appointments can not change location."); 
		}

		Integer reasonCode = appt.getReasonCode();
		int demographicNo = appt.getDemographicNo();
		try
		{
			if (request.getParameter("reasonCode") != null)
			{
				reasonCode = Integer.parseInt(request.getParameter("reasonCode"));
			}
			demographicNo = Integer.parseInt(headRecord);
		}
		catch (NumberFormatException e)
		{
			MiscUtils.getLogger().error("Error attempting to parse a number when saving appointment", e);
		}

		int rowsAffected = 0;

		//Did the appt status change ?
		if(appt.getStatus() != null && !appt.getStatus().equals(status))
		{
			changedStatus = status;
		}

		if(buttonCancel != null && (buttonCancel.equals(cancelledAppointment) || buttonCancel.equals("No Show")))
		{
			changedStatus = buttonCancel.equals(cancelledAppointment) ? Appointment.CANCELLED : Appointment.NO_SHOW;
			appt.setStatus(changedStatus);
			appt.setLastUpdateUser(updateuser);
			appointmentDao.merge(appt);
			rowsAffected = 1;

			if(appt.getIsVirtual())
			{
				myHealthAccessService.queueAppointmentCacheUpdate(appt);
			}

			LogAction.addLogEntry(updateuser, appt.getDemographicNo(), LogConst.ACTION_UPDATE, LogConst.CON_APPT,
					LogConst.STATUS_SUCCESS, String.valueOf(appt.getId()), request.getRemoteAddr());

		}
		else
		{
			appt.setDemographicNo(demographicNo);

			appt.setAppointmentDate(appointmentDate);
			appt.setStartTime(startDate);
			appt.setEndTime(endDate);
			appt.setName(appointmentName);
			appt.setNotes(notes);
			appt.setReason(reason);
			appt.setLocation(location);
			appt.setIsVirtual(isVirtual.equals("on"));
			appt.setResources(resources);
			appt.setType(type);
			appt.setStyle(style);
			appt.setBilling(billing);
			appt.setStatus(status);
			appt.setLastUpdateUser(updateuser);
			appt.setRemarks(remarks);
			appt.setUpdateDateTime(new java.util.Date());
			appt.setUrgency(urgency);
			appt.setReasonCode(reasonCode);

			appointmentDao.merge(appt);

			LogAction.addLogEntry(updateuser, appt.getDemographicNo(), LogConst.ACTION_UPDATE, LogConst.CON_APPT,
					LogConst.STATUS_SUCCESS, String.valueOf(appt.getId()), request.getRemoteAddr());
			rowsAffected = 1;

			if(appt.getIsVirtual())
			{
				myHealthAccessService.queueAppointmentCacheUpdate(appt);
			}
		}
		if(rowsAffected == 1)
		{
	%>
<p>
<h1><bean:message
	key="appointment.appointmentupdatearecord.msgUpdateSuccess" /></h1>

<script LANGUAGE="JavaScript">
    <% 
        if(!(request.getParameter("printReceipt")==null) && request.getParameter("printReceipt").equals("1")) {
    %>
            popupPage(350,750,'printappointment.jsp?appointment_no=<%=request.getParameter("appointment_no")%>') ;
    <%}%>
	self.opener.refresh();
	self.close();
</script>
<%
	String mcNumber = request.getParameter("appt_mc_number");
	OtherIdManager.saveIdAppointment(apptNo, "appt_mc_number", mcNumber);
	
	if(changedStatus != null){
		EventService eventService = SpringUtils.getBean(EventService.class); //updating an appt from the appt update screen delete doesn't work
		eventService.appointmentStatusChanged(this,apptNo.toString(), appt.getProviderNo(), changedStatus);
	}
	// End External Prescriber 
  } else {
%>
<p>
<h1><bean:message
	key="appointment.appointmentupdatearecord.msgUpdateFailure" /></h1>

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
