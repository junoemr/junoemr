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
	String userName = (String) session.getAttribute("userlastname") + "," + (String) session.getAttribute("userfirstname");
%>
<%@ page import="org.oscarehr.schedule.service.Schedule,
                 org.oscarehr.util.SpringUtils,
                 oscar.HScheduleDate,
                 oscar.MyDateFormat,
                 java.util.Date"
	errorPage="../appointment/errorpage.jsp"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="org.oscarehr.schedule.model.ScheduleDate" %>
<%@ page import="org.oscarehr.schedule.dao.ScheduleDateDao" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<jsp:useBean id="scheduleDateBean" class="java.util.Hashtable" scope="session" />
<jsp:useBean id="scheduleRscheduleBean" class="oscar.RscheduleBean"	scope="session" />
<html:html locale="true">
<head>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
<title><bean:message key="schedule.scheduledatesave.title" /></title>
</head>
	<%
		Schedule scheduleService = SpringUtils.getBean(Schedule.class);
		ScheduleDateDao scheduleDateDao = SpringUtils.getBean(ScheduleDateDao.class);

		String providerNo = request.getParameter("provider_no");
		String providerName = request.getParameter("provider_name");
		boolean available = ScheduleDate.AVAILABLE.equals(request.getParameter("available"));
		String priority = "c";
		String reason = request.getParameter("reason");
		String hour = request.getParameter("hour");
		String dateStr = request.getParameter("date");
		Date date = MyDateFormat.getSysDate(dateStr);

		ScheduleDate scheduleDate = scheduleDateDao.findByProviderNoAndDate(providerNo, MyDateFormat.getSysDate(request.getParameter("date")));
		if(scheduleDate != null) {
			scheduleDate.setStatus(ScheduleDate.STATUS_DELETED);
			scheduleDateDao.merge(scheduleDate);
		}

		//save the record first, change holidaybean next

		//add R schedule date if it is available
		if(" Delete ".equals(request.getParameter("Submit")))
		{
			if(scheduleRscheduleBean.getDateAvail(dateStr))
			{
				String availHour = scheduleRscheduleBean.getDateAvailHour(dateStr);
				scheduleService.saveScheduleByDate(providerNo, date, true, "b", "", availHour, userName, scheduleRscheduleBean.active);
			}
			scheduleDateBean.remove(dateStr);
		}
		if(" Save ".equals(request.getParameter("Submit")))
		{
			scheduleService.saveScheduleByDate(providerNo, date, available, priority, reason, hour, userName, scheduleRscheduleBean.active);
			scheduleDateBean.put(dateStr, new HScheduleDate(available, priority, reason, hour, userName));
		}
	%>

<script language="JavaScript">
<!--
  opener.location.href=opener.location.href+"?provider_no=<%=providerNo%>&provider_name=<%=URLEncoder.encode(providerName, "UTF-8")%>";
  opener.location.reload(true);
  self.close();
//-->
</script>
<body>
</body>
</html:html>
