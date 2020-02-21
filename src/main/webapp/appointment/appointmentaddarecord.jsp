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
<%@ page
	import="org.oscarehr.common.dao.OscarAppointmentDao, org.oscarehr.common.dao.WaitingListDao, org.oscarehr.common.model.Appointment,
	org.oscarehr.common.model.Demographic, org.oscarehr.common.model.WaitingList, org.oscarehr.common.model.WaitingListName"
	errorPage="errorpage.jsp"%>
<%@ page import="org.oscarehr.event.EventService,org.oscarehr.managers.DemographicManager, org.oscarehr.util.LoggedInInfo" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="oscar.MyDateFormat" %>
<%@page import="oscar.appt.AppointmentMailer" %>
<%@page import="oscar.log.LogAction" %>
<%@page import="oscar.log.LogConst" %>
<%@page import="oscar.oscarDemographic.data.DemographicData"%>
<%@page import="oscar.util.ConversionUtils" %>
<%@ page import="oscar.util.UtilDateUtilities" %>
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

OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
	DemographicMergedDao demographicMergedDao = SpringUtils.getBean(DemographicMergedDao.class);
WaitingListDao waitingListDao = SpringUtils.getBean(WaitingListDao.class);
Integer appointmentNo = null;

	String[] param = new String[20];

	String headRecord = request.getParameter("demographic_no");
	Demographic demo;

	if (headRecord == null || headRecord.isEmpty())
	{
		headRecord = "0";
		//the keyword(name) must match the demographic_no if it has been changed
		param[4] = request.getParameter("keyword");
	}
	else
	{
		DemographicData demData = new DemographicData();
		demo = demData.getDemographic(loggedInInfo, headRecord);
		param[4] = demo.getLastName() + "," + demo.getFirstName();
	}
	int demographicNo = Integer.parseInt(headRecord);

	DemographicMerged demographicMerged = demographicMergedDao.getCurrentHead(demographicNo);
	// Use parent record if the requested demographic has been merged to someone else
	if (demographicMerged != null)
	{
		demographicNo = demographicMerged.getDemographicNo();
		headRecord = Integer.toString(demographicNo);
	}

	String createDateTime = ConversionUtils.toDateString(new Date(), ConversionUtils.DEFAULT_TS_PATTERN);

param[0]=request.getParameter("provider_no");
param[1]=request.getParameter("appointment_date");
param[2]=MyDateFormat.getTimeXX_XX_XX(request.getParameter("start_time"));
param[3]=MyDateFormat.getTimeXX_XX_XX(request.getParameter("end_time"));
param[16] = headRecord;

param[5]=request.getParameter("notes");
param[6]=request.getParameter("reason");
param[7]=request.getParameter("location");
param[8]=request.getParameter("resources");
param[9]=request.getParameter("type");
param[10]=request.getParameter("style");
param[11]=request.getParameter("billing");
param[12]=request.getParameter("status");
param[13]=createDateTime;
param[14]=request.getParameter("creator");
param[15]=request.getParameter("remarks");
param[17]=(String)request.getSession().getAttribute("programId_oscarView");
param[18]=(request.getParameter("urgency")!=null)?request.getParameter("urgency"):"";
param[19]=request.getParameter("reasonCode");

	Appointment a = new Appointment();
	a.setProviderNo(request.getParameter("provider_no"));
	a.setAppointmentDate(ConversionUtils.fromDateString(request.getParameter("appointment_date")));
	a.setStartTime(ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")));
	a.setEndTime(ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")));
	a.setName(request.getParameter("keyword"));
	a.setNotes(request.getParameter("notes"));
	a.setReason(request.getParameter("reason"));
	a.setLocation(request.getParameter("location"));
	a.setIsVirtual(request.getParameter("isVirtual") != null &&
			request.getParameter("isVirtual").equals("on"));
	a.setResources(request.getParameter("resources"));
	a.setType(request.getParameter("type"));
	a.setStyle(request.getParameter("style"));
	a.setBilling(request.getParameter("billing"));
	a.setStatus(request.getParameter("status"));
	a.setCreateDateTime(ConversionUtils.fromTimestampString(createDateTime));
	a.setCreator(request.getParameter("creator"));
	a.setRemarks(request.getParameter("remarks"));
	a.setReasonCode(Integer.parseInt(request.getParameter("reasonCode")));
	String appointmentName = request.getParameter("keyword");
	a.setDemographicNo(demographicNo);
	//the keyword(name) must match the demographic_no if it has been changed

	// Appointment name column only contains 50 chars
	appointmentName = org.apache.commons.lang3.StringUtils.left(appointmentName, 50);
	a.setName(appointmentName);
	a.setProgramId(Integer.parseInt((String)request.getSession().getAttribute("programId_oscarView")));
	a.setUrgency((request.getParameter("urgency")!=null)?request.getParameter("urgency"):"");
	
	try{
		appointmentDao.persist(a);
		appointmentNo = a.getId();

		LogAction.addLogEntry(loggedInInfo.getLoggedInProviderNo(), a.getDemographicNo(), LogConst.ACTION_ADD, LogConst.CON_APPT,
				LogConst.STATUS_SUCCESS, String.valueOf(a.getId()), request.getRemoteAddr());
	} catch (ConstraintViolationException e)
	{
		MiscUtils.getLogger().error("ConstraintViolation", e);
	}

	if (appointmentNo != null) {

             //email patient appointment record
            if (request.getParameter("emailPt")!= null) {
                try{
                   
                   Appointment aa =  appointmentDao.search_appt_no(request.getParameter("provider_no"), ConversionUtils.fromDateString(request.getParameter("appointment_date")), ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")),
                    			ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")), ConversionUtils.fromTimestampString(createDateTime), request.getParameter("creator"), Integer.parseInt(param[16]));
		   
                    if (aa != null) {
						Integer apptNo = aa.getId();
                        DemographicManager demographicManager =  SpringUtils.getBean(DemographicManager.class);
                        Demographic demographic = demographicManager.getDemographic(loggedInInfo,param[16]);

                        if ((demographic != null) && (apptNo > 0)) {
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
		if (strMWL != null && strMWL.equalsIgnoreCase("yes")){
			;
		} else {
			oscar.oscarWaitingList.WaitingList wL = oscar.oscarWaitingList.WaitingList.getInstance();
			if (wL.getFound()) {
			   if( headRecord != null && !"".equals(headRecord)) {
			    
					List<WaitingList> wl = waitingListDao.findByDemographic(Integer.parseInt(headRecord));
					if(wl.size() > 0) {
						WaitingList wl1 = wl.get(0);
						WaitingListName wln = wl1.getWaitingListName();
					
				%>
				<form name="updateWLFrm"
					action="../oscarWaitingList/RemoveFromWaitingList.jsp"><input
					type="hidden" name="listId"
					value="<%=wl1.getListId()%>" /><input
					type="hidden" name="demographicNo"
					value="<%=request.getParameter("demographic_no")%>" /><script
					LANGUAGE="JavaScript">
						var removeList = confirm("Click OK to remove patient from the waiting list: <%=wln.getName()%>");
						if (removeList) {
							document.forms[0].action = "../oscarWaitingList/RemoveFromWaitingList.jsp?demographicNo=<%=request.getParameter("demographic_no")%>&listID=<%=wl1.getListId()%>";
							document.forms[0].submit();
						}
				</script></form>
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
            Appointment aa =  appointmentDao.search_appt_no(request.getParameter("provider_no"), ConversionUtils.fromDateString(request.getParameter("appointment_date")), ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")),
     			ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")),ConversionUtils.fromTimestampString(createDateTime),  request.getParameter("creator"), Integer.parseInt(param[16]));
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
		 Appointment aa =  appointmentDao.search_appt_no(request.getParameter("provider_no"), ConversionUtils.fromDateString(request.getParameter("appointment_date")), ConversionUtils.fromTimeStringNoSeconds(request.getParameter("start_time")),
     			ConversionUtils.fromTimeStringNoSeconds(request.getParameter("end_time")), ConversionUtils.fromTimestampString(createDateTime), request.getParameter("creator"), Integer.parseInt(param[16]));

		
		
		if (aa != null) {
			Integer apptNo = aa.getId();
			String mcNumber = request.getParameter("appt_mc_number");
			OtherIdManager.saveIdAppointment(apptNo, "appt_mc_number", mcNumber);
			
			EventService eventService = SpringUtils.getBean(EventService.class);
			eventService.appointmentCreated(this,apptNo.toString(), param[0]); // called when adding an appointment
			
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
