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
<%@page import="org.oscarehr.common.dao.ConsultationRequestDao" %>
<%@page import="org.oscarehr.common.dao.ConsultationServiceDao" %>
<%@page import="org.oscarehr.common.model.ConsultationRequest" %>
<%@page import="org.oscarehr.common.model.Demographic" %>
<%@page import="org.oscarehr.common.model.ProfessionalSpecialist" %>
<%@page import="org.oscarehr.common.model.ConsultationServices" %>
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
  ConsultationRequestDao consultationRequestDao = (ConsultationRequestDao)SpringUtils.getBean("consultationRequestDao");
  ConsultationServiceDao consultationServiceDao = (ConsultationServiceDao)SpringUtils.getBean("consultationServiceDao");
  DemographicDao demographicDao = (DemographicDao)SpringUtils.getBean("demographicDao");
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
    <bean:message key="oscarEncounter.oscarConsultationRequest.msgMainLabel" /></font></th>
  </tr>
</table>
<%

  boolean sentEmail = false;
  String emailAddress = "";
  String errorMsg = "";
  String statusMsg = "";
  ConsultationRequest consultRequest = null;

  try {

    String consult_request_id = (String)request.getAttribute("consult_request_id");
    if(consult_request_id == null) {
      throw new IllegalArgumentException("Unable to find consultation request ID");
    }

    consultRequest = consultationRequestDao.find(Integer.parseInt(consult_request_id));
    Demographic demo = demographicDao.getDemographic(String.valueOf(consultRequest.getDemographicId()));
    ProfessionalSpecialist specialist = consultRequest.getProfessionalSpecialist();
    ConsultationServices service = consultationServiceDao.find(consultRequest.getServiceId());

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
    apptTime.setTime(consultRequest.getAppointmentTime());

    Calendar apptDate = Calendar.getInstance();
    apptDate.setTime(consultRequest.getAppointmentDate());
    apptDate.set(Calendar.HOUR_OF_DAY, apptTime.get(Calendar.HOUR_OF_DAY));
    apptDate.set(Calendar.MINUTE, apptTime.get(Calendar.MINUTE));

    String formattedApptDate = DateUtils.format(dateFormat, apptDate.getTime(), null);

    String specialistFullName = "Dr. " + specialist.getFirstName() + " " + specialist.getLastName();
    if(specialist.getProfessionalLetters() != null && specialist.getProfessionalLetters().length() > 0) {
      specialistFullName += " " + specialist.getProfessionalLetters();
    }

    InputStream templateInputStream = ConsultationRequest.class.getResourceAsStream("/consultation_request_details_email_template.txt");
    String emailTemplate = IOUtils.toString(templateInputStream);

    VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
    velocityContext.put("consultRequest", consultRequest);
    velocityContext.put("demographic", demo);
    velocityContext.put("specialist", specialist);
    velocityContext.put("appointmentDateTime", formattedApptDate);
    velocityContext.put("specialistFullName", specialistFullName);
    velocityContext.put("service", service);

    String emailBody = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);

    EmailUtils.sendEmail(emailAddress, fullName, fromEmail, fromName, subject, emailBody, null);
    sentEmail = true;

  } catch (Exception e) {
    MiscUtils.getLogger().error("Unable to email consultation request reminder", e);
    errorMsg = e.getMessage();
  }

  if (sentEmail) {

    try {
      // update the status of the consultation request to 4 (Completed)
      consultRequest.setStatus("4");
      consultationRequestDao.merge(consultRequest);

      statusMsg = "Status updated to 'Completed'";

    } catch (Exception e) {
      MiscUtils.getLogger().error("Unable to update consultation request status", e);
      statusMsg = "Error updating status to 'Completed': " + e.getMessage();
    }

%>
<p>
<h1><bean:message key="oscarEncounter.oscarConsultationRequest.msgEmailSuccess" /></h1>
<h3><%= emailAddress %></h3>
<h3><%= statusMsg %></h3>

<%
  } else {
%>

<p>
<h1><bean:message key="oscarEncounter.oscarConsultationRequest.msgEmailFailure" /></h1>
<h3><%= errorMsg %></h3>

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
