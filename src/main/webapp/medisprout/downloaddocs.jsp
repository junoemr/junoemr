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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@page import="org.oscarehr.integration.medisprout.MediSprout"%>
<%@page import="org.oscarehr.common.model.MediSproutAppointment"%>
<%
	if (session.getAttribute("user") == null)    response.sendRedirect("../logout.jsp");

  String curProvider_no = request.getParameter("provider_no");
  String appointment_no = request.getParameter("appointment_no");
  String curUser_no = (String) session.getAttribute("user");
  String userfirstname = (String) session.getAttribute("userfirstname");
  String userlastname = (String) session.getAttribute("userlastname");
  String deepcolor = "#CCCCFF", weakcolor = "#EEEEFF";
  String origDate = null;

  boolean bFirstDisp = true; //this is the first time to display the window
  if (request.getParameter("bFirstDisp")!=null) bFirstDisp = (request.getParameter("bFirstDisp")).equals("true");
%>
<%@ include file="/common/webAppContextAndSuperMgr.jsp"%>
<%@page import="oscar.oscarDemographic.data.*,java.util.*,java.sql.*,oscar.appt.*,oscar.*,java.text.*,java.net.*,org.oscarehr.common.OtherIdManager"%>
<%@ page import="oscar.appt.status.service.AppointmentStatusMgr"%>
<%@ page import="oscar.appt.status.model.AppointmentStatus"%>
<%@ page import="org.oscarehr.common.dao.DemographicDao,org.oscarehr.common.model.Demographic,org.oscarehr.util.SpringUtils"%>
<%@ page import="oscar.oscarEncounter.data.EctFormData"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ taglib uri="/WEB-INF/oscar-tag.tld" prefix="oscar" %>
<jsp:useBean id="providerBean" class="java.util.Properties" scope="session" />
<%@page import="org.oscarehr.common.model.DemographicCust" %>
<%@page import="org.oscarehr.common.dao.DemographicCustDao" %>
<%
	DemographicCustDao demographicCustDao = (DemographicCustDao)SpringUtils.getBean("demographicCustDao");
	org.oscarehr.PMmodule.dao.ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
%>
<%
	ApptData apptObj = ApptUtil.getAppointmentFromSession(request);

  oscar.OscarProperties pros = oscar.OscarProperties.getInstance();
  String strEditable = pros.getProperty("ENABLE_EDIT_APPT_STATUS");

  AppointmentStatusMgr apptStatusMgr = (AppointmentStatusMgr)webApplicationContext.getBean("AppointmentStatusMgr");
  List allStatus = apptStatusMgr.getAllActiveStatus();

  Boolean isMobileOptimized = session.getAttribute("mobileOptimized") != null;

  DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
%>
<%@page import="org.oscarehr.common.dao.SiteDao"%>
<%@page import="org.oscarehr.common.model.Site"%><html:html locale="true">
<head>
<%
	if (isMobileOptimized) {
%>
    <meta name="viewport" content="initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, width=device-width" />
    
<%
	} else {
%>
    <style type="text/css">
        .deep { background-color: <%=deepcolor%>; }
        .weak { background-color: <%=weakcolor%>; }
    </style>
<%
	}
%>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/global.js"></script>
<title>Downloading all MediSprout Appt Docs</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
   <script>
     jQuery.noConflict();
   </script>

</head>
<body onload="setfocus()" bgproperties="fixed"
      topmargin="0" leftmargin="0" rightmargin="0" bottommargin="0">
<!-- The mobile optimized page is split into two sections: viewing and editing an appointment
     In the mobile version, we only display the edit section first if we are returning from a search -->


<%
MediSprout mediSprout = new MediSprout();

mediSprout.downloadDocs();


%>

Done.


</body>

</html:html>
