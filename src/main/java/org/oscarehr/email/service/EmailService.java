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
import org.oscarehr.demographic.entity.Demographic;
import org.oscarehr.email.EmailTemplateConfig;
import org.oscarehr.email.dao.EmailLogDao;
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

	@Autowired
	private EmailLogDao emailLogDao;

	public boolean sendConsultationTemplateEmail(String consultRequestId, boolean useDetailsTemplate, String loggedInProviderNo)
	{
		boolean emailSent = false;
		EmailLog logEntry = new EmailLog();

		try
		{
			EmailTemplateConfig templateConfig = new EmailTemplateConfig();

			ConsultationRequest consultRequest = consultationRequestDao.find(Integer.parseInt(consultRequestId));
			ProfessionalSpecialist specialist = consultRequest.getProfessionalSpecialist();
			ConsultationServices service = consultationServiceDao.find(consultRequest.getServiceId());

			// load required demographic info
			Demographic demo = demographicDao.find(consultRequest.getDemographicId());
			templateConfig.setToEmail(demo.getEmail());
			templateConfig.setToName(demo.getFormattedName());

			// prepare log entry values
			logEntry.setLoggedInProviderNo(loggedInProviderNo);
			logEntry.setReferralDoctorId(specialist != null ? specialist.getId() : null);
			logEntry.setReferringProviderNo(consultRequest.getProviderNo());
			logEntry.setDemographicNo(demo.getDemographicId());
			logEntry.setEmailAddress(templateConfig.getToEmail());

			// determine which template to use
			String templateFileNameProp = useDetailsTemplate ?
					"email.consult_request_details_template" : "email.consult_request_notification_template";

			// configure velocity context for template value replacements
			VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
			velocityContext.put("demographic", demo);
			velocityContext.put("specialist", specialist);
			velocityContext.put("service", service);

			if (useDetailsTemplate)
			{
				String formattedApptDate = ConversionUtils.toDateString(consultRequest.getAppointmentDateTime(), templateConfig.getDateFormat());

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

			templateConfig.setVelocityContext(velocityContext);
			templateConfig.setTemplateNameTxt(props.getProperty(String.format("%s.txt", templateFileNameProp)));
			templateConfig.setTemplateNameHtml(props.getProperty(String.format("%s.html", templateFileNameProp)));

			logEntry.setEmailContent((templateConfig.getEmailBodyHtml() != null)? templateConfig.getEmailBodyHtml() : templateConfig.getEmailBodyTxt());

			// send the email
			sendTemplateEmail(templateConfig);
			emailSent = true;

			// update the consultation request info
			if (useDetailsTemplate)
			{
				consultRequest.setStatus(ConsultationRequest.STATUS_COMPLETE);
			}
			consultRequest.setNotificationSent(true);
			consultationRequestDao.merge(consultRequest);
		}
		catch(Exception e)
		{
			logger.error("Email Consultation template error", e);
		}
		logEntry.setEmailSent(emailSent);
		emailLogDao.persist(logEntry);
		return emailSent;
	}

	public boolean sendAppointmentTemplateEmail(String appointmentNo, String loggedInProviderNo)
	{
		boolean emailSent = false;
		EmailLog logEntry = new EmailLog();

		try
		{
			EmailTemplateConfig templateConfig = new EmailTemplateConfig();

			Appointment appt = appointmentDao.find(Integer.parseInt(appointmentNo));
			ProviderData provider = providerDao.find(appt.getProviderNo());

			// load required demographic info
			Demographic demo = demographicDao.find(appt.getDemographicNo());
			templateConfig.setToEmail(demo.getEmail());
			templateConfig.setToName(demo.getFormattedName());

			// prepare log entry values
			logEntry.setLoggedInProviderNo(loggedInProviderNo);
			logEntry.setDemographicNo(demo.getDemographicId());
			logEntry.setEmailAddress(templateConfig.getToEmail());

			// configure velocity context
			String formattedApptDate = ConversionUtils.toDateString(appt.getStartTimeAsFullDate(), templateConfig.getDateFormat());

			VelocityContext velocityContext = VelocityUtils.createVelocityContextWithTools();
			velocityContext.put("appointmentDateTime", formattedApptDate);
			velocityContext.put("demographic", demo);
			velocityContext.put("provider", provider);
			templateConfig.setVelocityContext(velocityContext);

			templateConfig.setTemplateNameTxt(props.getProperty("email.appointment_details_template.txt"));
			templateConfig.setTemplateNameHtml(props.getProperty("email.appointment_details_template.html"));

			logEntry.setEmailContent((templateConfig.getEmailBodyHtml() != null)? templateConfig.getEmailBodyHtml() : templateConfig.getEmailBodyTxt());

			// send the email
			sendTemplateEmail(templateConfig);
			emailSent = true;
		}
		catch(Exception e)
		{
			logger.error("Email Appointment template error", e);
		}
		logEntry.setEmailSent(emailSent);
		emailLogDao.persist(logEntry);
		return emailSent;
	}

	private void sendTemplateEmail(EmailTemplateConfig templateConfig) throws EmailException
	{
		if(templateConfig.isEmailConfigured())
		{
			if(templateConfig.isEmailBodyConfigured())
			{
				EmailUtilsOld.sendEmail(
						templateConfig.getToEmail(),
						templateConfig.getToName(),
						templateConfig.getFromEmail(),
						templateConfig.getFromName(),
						templateConfig.getSubject(),
						templateConfig.getEmailBodyTxt(),
						templateConfig.getEmailBodyHtml());
			}
			else
			{
				throw new IllegalStateException("Missing or invalid email body");
			}
		}
		else
		{
			throw new IllegalArgumentException("Application is misconfigured to send email.");
		}
	}
}
