/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.dms.actions;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.mail.EmailException;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import org.oscarehr.util.EmailUtils;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;

import oscar.dms.EDocUtil;

/**
 *
 * @author jay
 */
public class SendEmailPDFAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) 
	{
		if(!OscarProperties.getInstance().isPropertyActive("document_email_enabled")) 
		{
			return mapping.findForward("failed");
		}

		String demoNo = request.getParameter("demoId");
		String providerNo = request.getParameter("providerId");

        String[] docNoArray = request.getParameterValues("docNo");
		String[] recipients = request.getParameterValues("emailAddresses");
	
		request.setAttribute("docNo", docNoArray);
		request.setAttribute("emailAddresses", recipients);

        String ContentDisposition=request.getParameter("ContentDisposition");
        ArrayList<Object> errorList = new ArrayList<Object>();
        if (docNoArray != null)
		{
			EDocUtil docData = new EDocUtil();
            for (int i =0 ; i < docNoArray.length ; i++)
			{
				String docNo = docNoArray[i];
				String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
				String filename =  docData.getDocumentName(docNo);
				String emailPdf = path + filename;

				for (int j = 0; j < recipients.length; j++)
				{
					String emailAddress = recipients[j];

					String emailNo = recipients[j].replaceAll("\\D", "");
					String error = "";
					String message = "";
					Exception exception = null;
					try
					{
						sendEmail(emailAddress, path, emailPdf);
					}
					catch (EmailException ee) 
					{
						error = "EmailException";
						message = ee.getMessage();
						exception = ee;
					} 

					if (!error.equals("")) 
					{
						MiscUtils.getLogger().error(
							error + " occured insided SendEmailPDFAction", exception);
						errorList.add(message);
						request.setAttribute("printError", new Boolean(true));
						continue;
					}
				}
			}
        }
		
		request.setAttribute("errors", errorList);

        return mapping.findForward("success");
    }

	private void sendEmail(String emailAddress, String path, String emailPdf)
		throws EmailException
	{
		if(!EmailUtils.isValidEmailAddress(emailAddress))
		{
			throw new EmailException("Invalid email address (" + 
				StringEscapeUtils.escapeHtml(emailAddress) + ")");
		}

		String subject = OscarProperties.getInstance().getProperty("document_email_subject");
		String body = OscarProperties.getInstance().getProperty("document_email_body");
		String name = OscarProperties.getInstance().getProperty("document_email_name");
		String from_address = 
			OscarProperties.getInstance().getProperty("document_email_from_address");

		EmailUtils.sendEmailWithAttachment(emailAddress, emailAddress, 
			from_address, name, subject, body, emailPdf);
	}

    /** Creates a new instance of CombinePDFAction */
    public SendEmailPDFAction() 
	{
    }

}
