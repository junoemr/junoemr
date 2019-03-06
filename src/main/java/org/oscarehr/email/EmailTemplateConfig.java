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
package org.oscarehr.email;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.VelocityContext;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.util.EmailUtilsOld;
import org.oscarehr.util.VelocityUtils;
import oscar.OscarProperties;

import java.io.IOException;
import java.io.InputStream;

import static org.oscarehr.common.io.GenericFile.EMAIL_TEMPLATE_DIRECTORY;

public class EmailTemplateConfig
{
	private static final OscarProperties props = OscarProperties.getInstance();

	private String fromEmail;
	private String fromName;
	private String subject;
	private String dateFormat;

	private String toName;
	private String toEmail;

	private String templateNameTxt;
	private String templateNameHtml;
	private String emailBodyTxt;
	private String emailBodyHtml;

	private VelocityContext velocityContext;


	public EmailTemplateConfig()
	{
		setFromEmail(props.getProperty("appointment_reminder_from_email_address"));
		setFromName(props.getProperty("appointment_reminder_from_name"));
		setSubject(props.getProperty("appointment_reminder_subject"));
		setDateFormat(props.getProperty("appointment_reminder_appt_date_format_java"));

		templateNameTxt = null;
		templateNameHtml = null;
		emailBodyTxt = null;
		emailBodyHtml = null;

		velocityContext = null;
	}

	public boolean isEmailConfigured()
	{
		return (fromEmail != null && fromName != null && subject != null && dateFormat != null);
	}
	public boolean isTemplateConfigured()
	{
		return (templateNameTxt != null || templateNameHtml != null);
	}
	public boolean isEmailBodyConfigured()
	{
		return (emailBodyTxt != null || emailBodyHtml != null);
	}

	public String getFromEmail()
	{
		return fromEmail;
	}

	public void setFromEmail(String fromEmail)
	{
		validateEmail(fromEmail);
		this.fromEmail = fromEmail;
	}

	public String getFromName()
	{
		return fromName;
	}

	public void setFromName(String fromName)
	{
		this.fromName = fromName;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getDateFormat()
	{
		return dateFormat;
	}

	public void setDateFormat(String dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public String getToName()
	{
		return toName;
	}

	public void setToName(String toName)
	{
		this.toName = toName;
	}

	public String getToEmail()
	{
		return toEmail;
	}

	public void setToEmail(String toEmail)
	{
		validateEmail(toEmail);
		this.toEmail = toEmail;
	}

	public String getTemplateNameTxt()
	{
		return templateNameTxt;
	}

	public void setTemplateNameTxt(String templateNameTxt) throws IOException
	{
		this.templateNameTxt = templateNameTxt;
		this.emailBodyTxt = loadTemplateAndEvaluate(this.templateNameTxt);
	}

	public String getTemplateNameHtml()
	{
		return templateNameHtml;
	}

	public void setTemplateNameHtml(String templateNameHtml) throws IOException
	{
		this.templateNameHtml = templateNameHtml;
		this.emailBodyHtml = loadTemplateAndEvaluate(this.templateNameHtml);
	}

	public String getEmailBodyTxt()
	{
		return emailBodyTxt;
	}

	public void setEmailBodyTxt(String emailBodyTxt)
	{
		this.emailBodyTxt = emailBodyTxt;
	}

	public String getEmailBodyHtml()
	{
		return emailBodyHtml;
	}

	public void setEmailBodyHtml(String emailBodyHtml)
	{
		this.emailBodyHtml = emailBodyHtml;
	}

	public VelocityContext getVelocityContext()
	{
		return velocityContext;
	}

	public void setVelocityContext(VelocityContext velocityContext)
	{
		this.velocityContext = velocityContext;
	}

	private String loadTemplateAndEvaluate(String templateFilename) throws IOException
	{
		if(velocityContext == null)
		{
			throw new IllegalStateException("VelocityContext must be set");
		}
		String emailBody = null;
		if(templateFilename != null)
		{
			GenericFile genericFile = FileFactory.getExistingFile(EMAIL_TEMPLATE_DIRECTORY, templateFilename);
			InputStream templateInputStream = genericFile.asFileInputStream();
			String emailTemplate = IOUtils.toString(templateInputStream);

			emailBody = VelocityUtils.velocityEvaluate(velocityContext, emailTemplate);
			templateInputStream.close();
		}
		return emailBody;
	}
	private void validateEmail(String emailAddress)
	{
		if (emailAddress == null || emailAddress.trim().isEmpty())
		{
			throw new IllegalArgumentException("No email address found.");
		}

		if (!EmailUtilsOld.isValidEmailAddress(emailAddress))
		{
			throw new IllegalArgumentException("Email Address '" + emailAddress + "' is invalid");
		}
	}
}
