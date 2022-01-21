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
<security:oscarSec roleName="<%=roleName$%>" objectName="_appointment" rights="w" reverse="<%=true%>">
	<%authed=false; %>
	<%response.sendRedirect("../securityError.jsp?type=_appointment");%>
</security:oscarSec>
<%
	if(!authed) {
		return;
	}
%>

<%@page import="org.oscarehr.common.OtherIdManager"%>
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao,
org.oscarehr.common.model.Appointment,
org.oscarehr.event.EventService,
org.oscarehr.util.LoggedInInfo,
org.oscarehr.util.SpringUtils,
oscar.log.LogAction,oscar.log.LogConst"%>
<%@ page import="oscar.oscarDemographic.data.DemographicData"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@page import="oscar.util.ConversionUtils" %>
<%@page import="oscar.util.UtilDateUtilities" %>
<%@ page import="org.oscarehr.util.MiscUtils" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicMergedDao" %>
<%@ page import="org.oscarehr.demographic.entity.DemographicMerged" %>
<%
	OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
    		
    LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
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
		<bean:message key="appointment.addappointment.msgMainLabel" /></font></th>
	</tr>
</table>
<%
	int demographicNo = 0;
	org.oscarehr.common.model.Demographic demo = null;
	String createDateTime = UtilDateUtilities.DateToString(new java.util.Date(), "yyyy-MM-dd HH:mm:ss");
	DemographicMergedDao demographicMergedDao = SpringUtils.getBean(DemographicMergedDao.class);

	try
	{
		demographicNo = Integer.parseInt(request.getParameter("demographic_no"));
	}
	catch (NumberFormatException e)
	{
		MiscUtils.getLogger().error("Error attempting to parse demographicNo when saving appointment: "
				+ request.getParameter("demographic_no"));
	}

	String creator = request.getParameter("creator");

	Appointment a = new Appointment();
	a.setProviderNo(request.getParameter("provider_no"));
	a.setAppointmentDate(ConversionUtils.fromDateString(request.getParameter("appointment_date")));
	a.setStartTime(ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")));
	a.setEndTime(ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")));
	a.setName(request.getParameter("keyword"));
	a.setNotes(request.getParameter("notes"));
	a.setReason(request.getParameter("reason"));
	a.setLocation(request.getParameter("location"));
	a.setResources(request.getParameter("resources"));
	a.setType(request.getParameter("type"));
	a.setStyle(request.getParameter("style"));
	a.setBilling(request.getParameter("billing"));
	a.setStatus(request.getParameter("status"));
	a.setCreateDateTime(ConversionUtils.fromTimestampString(createDateTime));
	a.setCreator(creator);
	a.setLastUpdateUser(creator);
	a.setRemarks(request.getParameter("remarks"));
	a.setReasonCode(Integer.parseInt(request.getParameter("reasonCode")));
	//the keyword(name) must match the demographic_no if it has been changed
	DemographicMerged demographicMerged = demographicMergedDao.getCurrentHead(demographicNo);
	if (demographicMerged != null)
	{
		demographicNo = demographicMerged.getMergedTo();
		a.setDemographicNo(demographicNo);
		DemographicData demData = new DemographicData();
		demo = demData.getDemographic(loggedInInfo, String.valueOf(a.getDemographicNo()));
		a.setName(demo.getLastName() + "," + demo.getFirstName());
	}
	else
	{
		a.setDemographicNo(0);
		a.setName(request.getParameter("keyword"));
	}
	
	a.setProgramId(Integer.parseInt((String)request.getSession().getAttribute("programId_oscarView")));
	a.setUrgency((request.getParameter("urgency")!=null)?request.getParameter("urgency"):"");
	
	appointmentDao.persist(a);

	LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), a.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_APPT,
			LogConst.STATUS_SUCCESS, String.valueOf(a.getId()), request.getRemoteAddr());
    
    int rowsAffected = 1;
	if (rowsAffected == 1) {
%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddSuccess" /></h1>

<%

		Appointment appt1 = appointmentDao.search_appt_no(request.getParameter("provider_no"), ConversionUtils.fromDateString(request.getParameter("appointment_date")), 
				ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")), ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")), 
				ConversionUtils.fromTimestampString(createDateTime), request.getParameter("creator"), demographicNo);
		if (appt1 != null) {
			Integer apptNo = appt1.getId();
			String mcNumber = request.getParameter("appt_mc_number");
			OtherIdManager.saveIdAppointment(apptNo, "appt_mc_number", mcNumber);
			
			EventService eventService = SpringUtils.getBean(EventService.class); //Add Appointment and print preview
			eventService.appointmentCreated(this,apptNo.toString(), request.getParameter("provider_no"));
%>
			<script LANGUAGE="JavaScript">
				self.opener.refresh();
				popupPage(350,750,'../report/reportdaysheet.jsp?dsmode=newappt&provider_no=<%=request.getParameter("provider_no")%>&sdate=<%=request.getParameter("appointment_date")%>&appointment_no=<%=apptNo%>') ;
				self.close();
			</script>
<%
		}
	} else {
%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddFailure" /></h1>

<%
	}
%>
<p></p>
<hr width="90%"/>
<form>
<input type="button" value="<bean:message key="global.btnClose"/>" onClick="window.close();">
</form>
</center>
</body>
</html:html>
