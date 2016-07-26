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

<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@ include file="/common/webAppContextAndSuperMgr.jsp"%>
<%@page import="org.oscarehr.common.dao.DemographicDao" %>
<%@page import="org.oscarehr.common.dao.OscarAppointmentDao" %>
<%@page import="org.oscarehr.common.model.Appointment" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.model.Provider" %>
<%@page import="org.oscarehr.PMmodule.dao.ProviderDao" %>
<%@page import="org.oscarehr.util.EmailUtils" %>
<%@page import="org.oscarehr.util.MiscUtils" %>
<%@page import="org.oscarehr.util.SpringUtils" %>
<%@page import="org.oscarehr.util.VelocityUtils" %>
<%@page import="oscar.OscarProperties" %>
<%@page import="oscar.util.DateUtils" %>
<%@page import="java.io.InputStream" %>
<%@page import="java.util.Calendar" %>
<%@page import="java.util.Date" %>
<%@page import="org.apache.commons.io.IOUtils" %>
<%@page import="org.apache.velocity.VelocityContext" %>

<%
	OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
  DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
  ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
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
		<bean:message key="appointment.appointmentemailreminder.msgMainLabel" /></font></th>
	</tr>
</table>
<%

  boolean sentEmail = false;
  String emailAddress = "";
  String errorMsg = "";

  try {

    String appointment_no = request.getParameter("appointment_no");
    if(appointment_no == null) {
      // when coming from a newly added appointment, the ID is added as an attribute instead of coming in as a parameter
      appointment_no = (String)request.getAttribute("appointment_no");
    }
    if(appointment_no == null) {
      throw new IllegalArgumentException("Unable to find appointment ID");
    }

    Appointment appt = appointmentDao.find(Integer.parseInt(appointment_no));
    Demographic demo = demographicDao.getDemographic(String.valueOf(appt.getDemographicNo()));
    Provider provider = providerDao.getProvider(appt.getProviderNo());

    emailAddress = demo.getEmail();
    String fullName = demo.getFormattedName();

    if(emailAddress == null || emailAddress.trim().equals("")) {
      throw new IllegalArgumentException("No email address found.");
    }

    if (!EmailUtils.isValidEmailAddress(emailAddress)) {
      throw new IllegalArgumentException("Email Address '" + emailAddress + "' is invalid");
    }

    OscarProperties props = OscarProperties.getInstance();

    String fromEmail = props.getProperty("appointment_reminder_from_email_address");
    String fromName = props.getProperty("appointment_reminder_from_name");
    String subject = props.getProperty("appointment_reminder_subject");
    String dateFormat = props.getProperty("appointment_reminder_appt_date_format_java");

    if(fromEmail == null || fromName == null || subject == null || dateFormat == null) {
      throw new IllegalArgumentException("Application is misconfigured to send email.");
    }

    Calendar apptTime = Calendar.getInstance();
    apptTime.setTime(appt.getStartTime());

    Calendar apptDate = Calendar.getInstance();
    apptDate.setTime(appt.getAppointmentDate());
    apptDate.set(Calendar.HOUR_OF_DAY, apptTime.get(Calendar.HOUR_OF_DAY));
    apptDate.set(Calendar.MINUTE, apptTime.get(Calendar.MINUTE));

    String formattedApptDate = DateUtils.format(dateFormat, apptDate.getTime(), null);

    InputStream templateInputStream = Appointment.class.getResourceAsStream("/appointment_details_email_template.txt");
    String emailTemplate = IOUtils.toString(templateInputStream);

    VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
    velocityContext.put("appointmentDateTime", formattedApptDate);
    velocityContext.put("demographic", demo);
    velocityContext.put("provider", provider);

    String emailBody = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);

    EmailUtils.sendEmail(emailAddress, fullName, fromEmail, fromName, subject, emailBody, null);
    sentEmail = true;
    MiscUtils.getLogger().info("APPOINTMENT REMINDER EMAIL SUCCESSFULLY SENT TO " + emailAddress + " FOR APPOINTMENT #: " + appointment_no);

  } 
  catch (Exception e) {
    MiscUtils.getLogger().error("Unable to email appointment reminder", e);
    errorMsg = e.getMessage();
  }

  if (sentEmail) {
%>
<p>
<h1><bean:message key="appointment.appointmentemailreminder.msgEmailSuccess" /></h1>
<h3><%= emailAddress %></h3>

<%
  } else {
%>

<p>
<h1><bean:message key="appointment.appointmentemailreminder.msgEmailFailure" /></h1>
<h3><%= errorMsg %></h3>

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
