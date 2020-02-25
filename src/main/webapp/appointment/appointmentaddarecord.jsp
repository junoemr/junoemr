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
<%@ page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@ page import="org.oscarehr.common.dao.WaitingListDao" %>
<%@ page import="org.oscarehr.common.model.Appointment"%>
<%@ page import="org.oscarehr.common.model.Demographic"%>
<%@ page import="org.oscarehr.common.model.WaitingList"%>
<%@ page import="org.oscarehr.common.model.WaitingListName" errorPage="errorpage.jsp"%>
<%@ page import="org.oscarehr.event.EventService,org.oscarehr.managers.DemographicManager" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="oscar.appt.AppointmentMailer" %>
<%@page import="oscar.log.LogAction" %>
<%@page import="oscar.log.LogConst" %>
<%@page import="oscar.util.ConversionUtils" %>
<%@ page import="javax.validation.ConstraintViolationException" %>
<%@ page import="java.util.List" %>
<%@ page import="org.oscarehr.demographic.dao.DemographicMergedDao" %>
<%@ page import="org.oscarehr.demographic.model.DemographicMerged" %>
<%@ page import="java.util.Date" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
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
	LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
	OscarAppointmentDao appointmentDao = SpringUtils.getBean(OscarAppointmentDao.class);
	DemographicMergedDao demographicMergedDao = SpringUtils.getBean(DemographicMergedDao.class);
	WaitingListDao waitingListDao = SpringUtils.getBean(WaitingListDao.class);

	Integer appointmentNo = null;
	String headRecord = request.getParameter("demographic_no");

	if (headRecord == null || headRecord.isEmpty())
	{
		headRecord = "0";
		//the keyword(name) must match the demographic_no if it has been changed
	}

	int demographicNo = Integer.parseInt(headRecord);

	DemographicMerged demographicMerged = demographicMergedDao.getCurrentHead(demographicNo);
	// Use parent record if the requested demographic has been merged to someone else
	if (demographicMerged != null)
	{
		demographicNo = demographicMerged.getDemographicNo();
		headRecord = Integer.toString(demographicNo);
	}

	// Pulling out and parsing request parameters that are used more than once on this page
	String providerNo = request.getParameter("provider_no");
	String creator = request.getParameter("creator");
	Date appointmentDate = ConversionUtils.fromDateString(request.getParameter("appointment_date"));
	Date startTime = ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time"));
	Date endTime = ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time"));
	Date createDateTime = new Date();

	Appointment appointment = new Appointment();
	appointment.setProviderNo(providerNo);
	appointment.setAppointmentDate(appointmentDate);
	appointment.setStartTime(startTime);
	appointment.setEndTime(endTime);
	appointment.setName(request.getParameter("keyword"));
	appointment.setNotes(request.getParameter("notes"));
	appointment.setReason(request.getParameter("reason"));
	appointment.setLocation(request.getParameter("location"));
	appointment.setIsVirtual(request.getParameter("isVirtual") != null
			&& request.getParameter("isVirtual").equals("on"));
	appointment.setResources(request.getParameter("resources"));
	appointment.setType(request.getParameter("type"));
	appointment.setStyle(request.getParameter("style"));
	appointment.setBilling(request.getParameter("billing"));
	appointment.setStatus(request.getParameter("status"));
	appointment.setCreateDateTime(createDateTime);
	appointment.setCreator(creator);
	appointment.setRemarks(request.getParameter("remarks"));
	appointment.setReasonCode(Integer.parseInt(request.getParameter("reasonCode")));
	appointment.setDemographicNo(demographicNo);
	String appointmentName = request.getParameter("keyword");

	// Appointment name column only contains 50 chars
	appointmentName = org.apache.commons.lang3.StringUtils.left(appointmentName, 50);
	appointment.setName(appointmentName);
	appointment.setProgramId(Integer.parseInt((String)request.getSession().getAttribute("programId_oscarView")));
	appointment.setUrgency((request.getParameter("urgency")!=null)?request.getParameter("urgency"):"");
	
	try
	{
		appointmentDao.persist(appointment);
		appointmentNo = appointment.getId();

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(),
				appointment.getDemographicNo(),
				LogConst.ACTION_ADD,
				LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS,
				String.valueOf(appointment.getId()),
				request.getRemoteAddr());
	}
	catch (ConstraintViolationException e)
	{
		MiscUtils.getLogger().error("ConstraintViolation", e);
	}

	if (appointmentNo != null)
	{
		//email patient appointment record
		if (request.getParameter("emailPt")!= null)
		{
			try
			{
				Appointment alternateAppointment =  appointmentDao.search_appt_no(providerNo,
						appointmentDate,
						startTime,
						endTime,
						createDateTime,
						creator,
						demographicNo);
		   
				if (alternateAppointment != null)
				{
					Integer apptNo = alternateAppointment.getId();
					DemographicManager demographicManager =  SpringUtils.getBean(DemographicManager.class);
					Demographic demographic = demographicManager.getDemographic(loggedInInfo, headRecord);

					if ((demographic != null) && (apptNo > 0))
					{
						AppointmentMailer emailer = new AppointmentMailer(apptNo,demographic);
						emailer.prepareMessage();
						emailer.send();
					}
				}

			}catch(Exception e) {
				out.print(e.getMessage());
			}
		}

		// turn off reminder of "remove patient from the waiting list"
		oscar.OscarProperties pros = oscar.OscarProperties.getInstance();
		String strMWL = pros.getProperty("MANUALLY_CLEANUP_WL");
		if (!(strMWL != null && strMWL.equalsIgnoreCase("yes")))
		{
			oscar.oscarWaitingList.WaitingList wL = oscar.oscarWaitingList.WaitingList.getInstance();
			if (wL.getFound())
			{
			   if(!headRecord.isEmpty())
			   {
					List<WaitingList> wl = waitingListDao.findByDemographic(Integer.parseInt(headRecord));
					if(wl.size() > 0)
					{
						WaitingList wl1 = wl.get(0);
						WaitingListName wln = wl1.getWaitingListName();

	%>
				<form name="updateWLFrm" action="../oscarWaitingList/RemoveFromWaitingList.jsp">
					<input type="hidden" name="listId" value="<%=wl1.getListId()%>" />
					<input type="hidden" name="demographicNo" value="<%=request.getParameter("demographic_no")%>" />
					<script type="text/javascript">
						var removeList = confirm("Click OK to remove patient from the waiting list: <%=wln.getName()%>");
						if (removeList) {
							document.forms[0].action = "../oscarWaitingList/RemoveFromWaitingList.jsp?demographicNo=<%=request.getParameter("demographic_no")%>&listID=<%=wl1.getListId()%>";
							document.forms[0].submit();
						}
					</script>
				</form>
	<%
					}
			   }
			}
		}
	%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddSuccess" /></h1>
	<%
		String postAction = (String) request.getAttribute("postAction");
		if("Email".equals(postAction))
		{
			pageContext.getRequest().setAttribute("appointment_no", String.valueOf(appointmentNo));
			pageContext.forward("appointmentemailreminder.jsp");
		}
		else
		{
	%>

<script LANGUAGE="JavaScript">
    <%
        int apptId=0;
        if(!(request.getParameter("printReceipt")==null) && request.getParameter("printReceipt").equals("1")) { 
            Appointment aa =  appointmentDao.search_appt_no(providerNo, appointmentDate, startTime, endTime, createDateTime, creator, demographicNo);
            if (aa != null) {
                apptId = aa.getId();
            }%>
            popupPage(350,750,'printappointment.jsp?appointment_no=<%=apptId%>') ;
        <%}%>
	self.opener.refresh();
	self.close();
</script>

<%
		}
		Appointment alternateAppointment = appointmentDao.search_appt_no(providerNo,
				appointmentDate,
				startTime,
				endTime,
				createDateTime,
				creator,
				demographicNo);
		
		if (alternateAppointment != null)
		{
			Integer apptNo = alternateAppointment.getId();
			String mcNumber = request.getParameter("appt_mc_number");
			OtherIdManager.saveIdAppointment(apptNo, "appt_mc_number", mcNumber);
			
			EventService eventService = SpringUtils.getBean(EventService.class);
			eventService.appointmentCreated(this,apptNo.toString(), providerNo); // called when adding an appointment
		}

	} else {
%>
<p>
<h1><bean:message key="appointment.addappointment.msgAddFailure" /></h1>

<%
	}
%>
<p></p>
<hr width="90%" />
<form>
<input type="button" value="<bean:message key="global.btnClose"/>" onClick="closeit()">
</form>
</center>
</body>
</html:html>
