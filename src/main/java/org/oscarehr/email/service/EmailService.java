/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.email.service;

import org.apache.commons.io.IOUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.oscarehr.common.dao.ConsultationRequestDao;
import org.oscarehr.common.dao.ConsultationServiceDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.ConsultationRequest;
import org.oscarehr.common.model.ConsultationServices;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.demographic.dao.DemographicDao;
import org.oscarehr.demographic.model.Demographic;
import org.oscarehr.email.model.EmailLog;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.EmailUtilsOld;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.VelocityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;
import oscar.util.ConversionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class EmailService
{
	private static final OscarProperties props = OscarProperties.getInstance();
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private OscarAppointmentDao appointmentDao;

	@Autowired
	private ConsultationRequestDao consultationRequestDao;

	@Autowired
	private ConsultationServiceDao consultationServiceDao;

	@Autowired
	private DemographicDao demographicDao;

	@Autowired
	private ProviderDataDao providerDao;

	public void sendConsultationTemplateEmail(String consultRequestId, String template, String loggedInProviderNo) throws IOException, EmailException
	{
		EmailLog logEntry = new EmailLog();
		if (consultRequestId == null)
		{
			throw new IllegalArgumentException("Unable to find consultation request ID");
		}
		boolean useDetailsTemplate = ("details").equals(template);

		ConsultationRequest consultRequest = consultationRequestDao.find(Integer.parseInt(consultRequestId));
		Demographic demo = demographicDao.find(consultRequest.getDemographicId());
		ProfessionalSpecialist specialist = consultRequest.getProfessionalSpecialist();
		ConsultationServices service = consultationServiceDao.find(consultRequest.getServiceId());

		String emailAddress = demo.getEmail();
		String fullName = demo.getFormattedName();

		if (emailAddress == null || emailAddress.trim().equals(""))
		{
			throw new IllegalArgumentException("No email address found.");
		}

		if (!EmailUtilsOld.isValidEmailAddress(emailAddress))
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

			String formattedApptDate = ConversionUtils.toDateString(apptDate.getTime(), dateFormat);

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
			ProviderData providerData = providerDao.find(consultRequest.getProviderNo());
			velocityContext.put("referringDoctorName", providerData.getLastName());
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

		logEntry.setLoggedInProviderNo(loggedInProviderNo);
		logEntry.setReferralDoctorId(specialist != null ? specialist.getId() : null);
		logEntry.setReferringProviderNo(consultRequest.getProviderNo());
		logEntry.setDemographicNo(demo.getDemographicId());
		logEntry.setEmailAddress(emailAddress);
		logEntry.setEmailContent(emailBodyHtml != null ? emailBodyHtml : emailBodyTxt);

		// don't send blank emails
		if (!(emailBodyTxt == null && emailBodyHtml == null))
		{
			EmailUtilsOld.sendEmail(emailAddress, fullName, fromEmail, fromName, subject, emailBodyTxt, emailBodyHtml);
			boolean sentEmail = true;
		}
		else
		{
			logger.error("Email failed to send: no available templates");
		}
	}

	public void sendAppointmentTemplateEmail(String appointmentNo) throws IOException, EmailException
	{
		Appointment appt = appointmentDao.find(Integer.parseInt(appointmentNo));
		Demographic demo = demographicDao.find(appt.getDemographicNo());
		ProviderData provider = providerDao.find(appt.getProviderNo());

		String emailAddress = demo.getEmail();
		String fullName = demo.getFormattedName();

		if (emailAddress == null || emailAddress.trim().equals(""))
		{
			throw new IllegalArgumentException("No email address found.");
		}

		if (!EmailUtilsOld.isValidEmailAddress(emailAddress))
		{
			throw new IllegalArgumentException("Email Address '" + emailAddress + "' is invalid");
		}

		String fromEmail = props.getProperty("appointment_reminder_from_email_address");
		String fromName = props.getProperty("appointment_reminder_from_name");
		String subject = props.getProperty("appointment_reminder_subject");
		String dateFormat = props.getProperty("appointment_reminder_appt_date_format_java");

		if (fromEmail == null || fromName == null || subject == null || dateFormat == null)
		{
			throw new IllegalArgumentException("Application is misconfigured to send email.");
		}

		String templateFolder = props.getProperty("template_file_location");
		String detailsTemplateTxt = props.getProperty("email.appointment_details_template.txt");
		String detailsTemplateHtml = props.getProperty("email.appointment_details_template.html");
		if (templateFolder == null || (detailsTemplateTxt == null && detailsTemplateHtml == null))
		{
			throw new IllegalArgumentException("Application email templates misconfigured.");
		}

		Calendar apptTime = Calendar.getInstance();
		apptTime.setTime(appt.getStartTime());

		Calendar apptDate = Calendar.getInstance();
		apptDate.setTime(appt.getAppointmentDate());
		apptDate.set(Calendar.HOUR_OF_DAY, apptTime.get(Calendar.HOUR_OF_DAY));
		apptDate.set(Calendar.MINUTE, apptTime.get(Calendar.MINUTE));

		String formattedApptDate = ConversionUtils.toDateString(apptDate.getTime(), dateFormat);

		VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
		velocityContext.put("appointmentDateTime", formattedApptDate);
		velocityContext.put("demographic", demo);
		velocityContext.put("provider", provider);

		String emailBodyTxt = null;
		String emailBodyHtml = null;
		if (detailsTemplateTxt != null)
		{
			File templateFile = new File(templateFolder, detailsTemplateTxt);
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
		if (detailsTemplateHtml != null)
		{
			File templateFile = new File(templateFolder, detailsTemplateHtml);
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
		// don't send blank emails
		if (!(emailBodyTxt == null && emailBodyHtml == null))
		{
			EmailUtilsOld.sendEmail(emailAddress, fullName, fromEmail, fromName, subject, emailBodyTxt, emailBodyHtml);
			boolean sentEmail = true;
			logger.info("APPOINTMENT REMINDER EMAIL SUCCESSFULLY SENT TO " + emailAddress + " FOR APPOINTMENT #: " + appointmentNo);
		}
		else {
			logger.error("Email failed to send: no available templates");
			throw new RuntimeException("Email failed to send: no available templates");
		}
	}
}
