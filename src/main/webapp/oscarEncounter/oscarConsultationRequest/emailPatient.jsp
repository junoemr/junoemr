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
<%@ page import="java.io.File" %>
<%@ page import="java.io.FileInputStream" %>
<%@ page import="org.apache.log4j.Logger" %>
<%@ page import="oscar.oscarProvider.data.ProviderData" %>
<%@ page import="org.oscarehr.common.model.EmailLog" %>
<%@ page import="org.oscarehr.common.dao.EmailLogDao" %>
<%@ page import="org.oscarehr.util.LoggedInInfo" %>

<%
	ConsultationRequestDao consultationRequestDao = (ConsultationRequestDao) SpringUtils.getBean("consultationRequestDao");
	ConsultationServiceDao consultationServiceDao = (ConsultationServiceDao) SpringUtils.getBean("consultationServiceDao");
	DemographicDao demographicDao = (DemographicDao) SpringUtils.getBean("demographicDao");
%>
<html:html locale="true">
	<head>
		<script type="text/javascript" src="<%= request.getContextPath() %>/js/global.js"></script>
	</head>

	<body background="../images/gray_bg.jpg" bgproperties="fixed">
	<center>
		<%
			Logger logger = MiscUtils.getLogger();

			String consult_request_id = (String) request.getAttribute("consult_request_id");
			String template = (String) request.getAttribute("template");

			boolean sentEmail = false;
			boolean useDetailsTemplate = ("details").equals(template);
			String emailAddress = "";
			String errorMsg = "";
			String statusMsg = "";
			ConsultationRequest consultRequest = null;

			EmailLog logEntry = new EmailLog();
			EmailLogDao emailLogDao = (EmailLogDao) SpringUtils.getBean("emailLogDao");

			try
			{
				if (consult_request_id == null)
				{
					throw new IllegalArgumentException("Unable to find consultation request ID");
				}

				consultRequest = consultationRequestDao.find(Integer.parseInt(consult_request_id));
				Demographic demo = demographicDao.getDemographic(String.valueOf(consultRequest.getDemographicId()));
				ProfessionalSpecialist specialist = consultRequest.getProfessionalSpecialist();
				ConsultationServices service = consultationServiceDao.find(consultRequest.getServiceId());

				emailAddress = demo.getEmail();
				String fullName = demo.getFormattedName();

				if (emailAddress == null || emailAddress.trim().equals(""))
				{
					throw new IllegalArgumentException("No email address found.");
				}

				if (!EmailUtils.isValidEmailAddress(emailAddress))
				{
					throw new IllegalArgumentException("Email Address '" + emailAddress + "' is invalid");
				}

				OscarProperties props = OscarProperties.getInstance();

				String fromEmail = props.getProperty("appointment_reminder_from_email_address");
				String fromName = props.getProperty("appointment_reminder_from_name");
				String subject = props.getProperty("appointment_reminder_subject");
				String dateFormat = props.getProperty("appointment_reminder_appt_date_format_java");

				if (fromEmail == null || fromName == null || subject == null || dateFormat == null)
				{
					throw new IllegalArgumentException("Application is misconfigured to send email.");
				}

				String templateFolder = props.getProperty("template_file_location");

				if (!(("notification").equals(template) || ("details").equals(template)))
				{
					throw new IllegalArgumentException("Unable to find requested email template.");
				}

				String templateFileName = useDetailsTemplate ?
						"email.consult_request_details_template" : "email.consult_request_notification_template";

				String templateTxt = props.getProperty(String.format("%s.txt", templateFileName));
				String templateHtml = props.getProperty(String.format("%s.html", templateFileName));
				if (templateFolder == null || (templateTxt == null && templateHtml == null))
				{
					throw new IllegalArgumentException("Application email templates misconfigured.");
				}

				VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
				velocityContext.put("demographic", demo);
				velocityContext.put("specialist", specialist);
				velocityContext.put("service", service);

				if (useDetailsTemplate)
				{
					Calendar apptTime = Calendar.getInstance();
					apptTime.setTime(consultRequest.getAppointmentTime());

					Calendar apptDate = Calendar.getInstance();
					apptDate.setTime(consultRequest.getAppointmentDate());
					apptDate.set(Calendar.HOUR_OF_DAY, apptTime.get(Calendar.HOUR_OF_DAY));
					apptDate.set(Calendar.MINUTE, apptTime.get(Calendar.MINUTE));

					String formattedApptDate = DateUtils.format(dateFormat, apptDate.getTime(), null);

					String specialistFullName = "Dr. " + specialist.getFirstName() + " " + specialist.getLastName();
					if (specialist.getProfessionalLetters() != null && specialist.getProfessionalLetters().length() > 0)
					{
						specialistFullName += " " + specialist.getProfessionalLetters();
					}

					velocityContext.put("appointmentDateTime", formattedApptDate);
					velocityContext.put("consultRequest", consultRequest);
					velocityContext.put("specialistFullName", specialistFullName);
				}
				else
				{
					ProviderData providerData = new ProviderData(consultRequest.getProviderNo());
					velocityContext.put("referringDoctorName", providerData.getLast_name());
				}

				String emailBodyTxt = null;
				String emailBodyHtml = null;
				if (templateTxt != null)
				{
					File templateFile = new File(templateFolder, templateTxt);
					if (templateFile.exists() && templateFile.isFile())
					{
						InputStream templateInputStream = new FileInputStream(templateFile);
						String emailTemplate = IOUtils.toString(templateInputStream);
						emailBodyTxt = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);
						templateInputStream.close();
					}
					else
					{
						logger.warn("Missing template file: " + templateFile.getPath());
					}
				}
				if (templateHtml != null)
				{
					File templateFile = new File(templateFolder, templateHtml);
					if (templateFile.exists() && templateFile.isFile())
					{
						InputStream templateInputStream = new FileInputStream(templateFile);
						String emailTemplate = IOUtils.toString(templateInputStream);
						emailBodyHtml = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);
						templateInputStream.close();
					}
					else
					{
						logger.warn("Missing template file: " + templateFile.getPath());
					}
				}

				logEntry.setLoggedInProviderNo(LoggedInInfo.loggedInInfo.get().loggedInProvider.getProviderNo());
				logEntry.setReferralDoctorId(specialist != null ? specialist.getId() : null);
				logEntry.setReferringProviderNo(consultRequest.getProviderNo());
				logEntry.setDemographicNo(demo.getDemographicNo());
				logEntry.setEmailAddress(emailAddress);
				logEntry.setEmailContent(emailBodyHtml != null ? emailBodyHtml : emailBodyTxt);

				// don't send blank emails
				if (!(emailBodyTxt == null && emailBodyHtml == null))
				{
					EmailUtils.sendEmail(emailAddress, fullName, fromEmail, fromName, subject, emailBodyTxt, emailBodyHtml);
					sentEmail = true;
				}
				else
				{
					logger.error("Email failed to send: no available templates");
				}

			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error("Unable to email consultation request reminder", e);
				errorMsg = e.getMessage();
			}

			logEntry.setEmailSuccess(sentEmail);
			emailLogDao.persist(logEntry);

		%>
		<table border="0" cellspacing="0" cellpadding="0" width="90%">
			<tr bgcolor="#486ebd">
				<th align="CENTER">
					<font face="Helvetica" color="#FFFFFF">
						<%
							if (useDetailsTemplate)
							{
						%>
						<bean:message
								key="oscarEncounter.oscarConsultationRequest.msgEmailDetailsLabel"/>
						<%
						}
						else
						{
						%>
						<bean:message
								key="oscarEncounter.oscarConsultationRequest.msgEmailNotificationLabel"/>
						<%}%>
					</font>
				</th>
			</tr>
		</table>

		<%
			if (sentEmail)
			{
				consultRequest.setNotificationSent(true);
				consultationRequestDao.merge(consultRequest);
		%>
		<p>
		<h1><bean:message key="oscarEncounter.oscarConsultationRequest.msgEmailSuccess"/></h1>
		<h3><%= emailAddress %>
		</h3>
		<%
			if (useDetailsTemplate)
			{
				try
				{
					// update the status of the consultation request to 4 (Completed)
					consultRequest.setStatus("4");
					consultationRequestDao.merge(consultRequest);

					statusMsg = "Status updated to 'Completed'";

				}
				catch (Exception e)
				{
					MiscUtils.getLogger().error("Unable to update consultation request status", e);
					statusMsg = "Error updating status to 'Completed': " + e.getMessage();
				}
		%>
		<h3><%= statusMsg %>
		</h3>
		<%
			}
		}
		else
		{
		%>

		<p>
		<h1><bean:message key="oscarEncounter.oscarConsultationRequest.msgEmailFailure"/></h1>
		<h3><%= errorMsg %>
		</h3>

		<%
			}
		%>

		<p></p>
		<hr width="90%"/>
		<form>
			<input type="button" value="<bean:message key="global.btnClose"/>"
				   onClick="window.close();">
		</form>
	</center>
	</body>
</html:html>
