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


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.util.EmailUtils;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;
import oscar.dms.EDocUtil;
import oscar.oscarRx.templates.RxPdfTemplate;
import oscar.oscarRx.templates.RxPdfTemplateCustom1;

/**
 * @author jay
 * revised Robert 2016
 */
public class SendEmailPDFAction extends Action {
	
	OscarProperties props = OscarProperties.getInstance();
	Logger logger = MiscUtils.getLogger();

    public ActionForward execute(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) {
    	
    	logger.info("EMAILING PDF DOCUMENTS");
    	
    	String emailActionType = request.getParameter("emailActionType");
    	ArrayList<String> attachments = new ArrayList<String>();
    	ArrayList<Object> errorList = new ArrayList<Object>();
    	
    	if(emailActionType.equals("DOC")) {
    		attachments = getDocAttachments(mapping, form, request, response);
    	}
    	else if (emailActionType.equals("RX")) {
    		attachments = getRxAttachments(mapping, form, request, response);
    	}
    	
    	if(attachments==null || attachments.size() <= 0) {
    		logger.error("No pdf attachments to email. Aborting");
    		return mapping.findForward("failure");
    	}
    	
		String demoNo = request.getParameter("demoId");
		String providerNo = request.getParameter("providerId");
    	
		String[] recipients = request.getParameterValues("emailAddresses");
		
		String fromAddress = props.getProperty("document_email_from_address");
		
		String subject = request.getParameter("emailSubject");
		String body = request.getParameter("emailBody");
		String name = props.getProperty("document_email_name");
		
		for (int i=0; i < recipients.length; i++) {
			for(String pdf: attachments) {
				try {
					sendEmail(pdf, recipients[i], fromAddress, subject, body, name);
					logger.info("Email Sent to " + recipients[i]);
					logger.info("file:" + pdf);//TODO remove
				}
				catch(Exception e) {
					logger.error("Error emailing pdf", e);
					request.setAttribute("printError", new Boolean(true));
					errorList.add(e.getMessage());
				}
			}
		}
		request.setAttribute("errors", errorList);
		logger.info("EMAILING PDF DOCUMENTS COMPLETE");
        return mapping.findForward("success");
    }
    
    @SuppressWarnings("unused")
    private ArrayList<String> getDocAttachments(ActionMapping mapping, ActionForm form, 
    		HttpServletRequest request, HttpServletResponse response) {
    	
    	ArrayList<String> attachments = new ArrayList<String>();
    	
    	String[] docNoArray = request.getParameterValues("docNo");
    	EDocUtil docData = new EDocUtil();
    	
    	for (int i=0; docNoArray!= null && i < docNoArray.length; i++) {
    		String docNo = docNoArray[i];
    		String path = props.getProperty("DOCUMENT_DIR");
    		String filename =  docData.getDocumentName(docNo);
    		String emailPdf = path + filename;
    		
    		attachments.add(emailPdf);
    	}
    	return attachments;
    }
    
    @SuppressWarnings("unused")
    private ArrayList<String> getRxAttachments(ActionMapping mapping, ActionForm form, 
    		HttpServletRequest request, HttpServletResponse response) {
    	ArrayList<String> attachments = new ArrayList<String>();
    	
    	try {
	    	RxPdfTemplate template = new RxPdfTemplateCustom1(request, null);
	    	ByteArrayOutputStream stream = template.getOutputStream();
	    	
	    	String providerNo = request.getParameter("providerId");
	    	
	    	// write to file
			String path = props.getProperty("email_file_location");
			String tempName = "Prescription-" + providerNo + "." + System.currentTimeMillis();
			String tempPdf = String.format("%s%s%s.pdf", path, File.separator, tempName);
			FileOutputStream fos = new FileOutputStream(tempPdf);
			stream.writeTo(fos);
			fos.close();
			
			attachments.add(tempPdf);
    	}
    	catch(Exception e) {
    		logger.error("Error creating Rx PDF for email", e);
    	}

    	return attachments;
    }
	private void sendEmail(String emailPdf, String toAddress, String fromAddress, String subject, String body, String name) throws EmailException {
		if(!EmailUtils.isValidEmailAddress(toAddress))
		{
			throw new EmailException("Invalid email address (" + StringEscapeUtils.escapeHtml(toAddress) + ")");
		}
		EmailUtils.sendEmailWithAttachment(toAddress, toAddress, fromAddress, name, subject, body, emailPdf);
	}

    /** Creates a new instance of CombinePDFAction */
    public SendEmailPDFAction() 
	{
    }

}
