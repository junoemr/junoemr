/**
 * Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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
 * OscarHost, a Division of Cloud Practice Inc.
 */

package org.oscarehr.web.eform;

import java.io.File;
import java.io.FileInputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.util.EmailUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;

import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.dms.actions.AddEditDocumentAction;
import oscar.eform.data.EForm;

public final class EfmEmailPdfAction {
	private static final Logger logger = MiscUtils.getLogger();
	private String localUri = null;
	private String clientId = null;
	private String providerNo = null;
	
	private String toEmailAddress = null;
	private String toName = null;
	
	private String fromEmailAddress = null;
	private String fromName = null;
	private static EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
	
	public EfmEmailPdfAction(HttpServletRequest request) {
		localUri = getEformRequestUrl(request);

		clientId = request.getParameter("clientId");
		toEmailAddress = request.getParameter("toEmail");
		toName = request.getParameter("toName");

		LoggedInInfo loggedInfo = LoggedInInfo.loggedInInfo.get();
		providerNo = loggedInfo.loggedInProvider.getProviderNo();
		
		fromEmailAddress = loggedInfo.loggedInProvider.getEmail();
		fromName = loggedInfo.loggedInProvider.getFullName();

		logger.debug(ReflectionToStringBuilder.toString(this));
	}
	
	/**
	 * This method is a copy of Apache Tomcat's ApplicationHttpRequest getRequestURL method with the exception that the uri is removed and replaced with our eform viewing uri. Note that this requires that the remote url is valid for local access. i.e. the
	 * host name from outside needs to resolve inside as well. The result needs to look something like this : https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms
	 */
	private String getEformRequestUrl(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		if (port < 0) port = 80; // Work around java.net.URL bug

		url.append(scheme);
		url.append("://");
		
		// IMPORTANT : do not change the serverName to 127.0.0.1
		// you can not do that because on virtual hosts or named hosts 127.0.0.1 may
		// not resolve to the same webapp. You must use the serverName that maps properly
		// as per the server.xml (in tomcat). Admittedly 95% of the time 127.0.0.1 would
		// work because most people don't do virtual hosting with tomcat on an oscar
		// system (but some caisi systems have in the past), but by keeping the hostName
		// this code would then work with everyone - although everyone needs to ensure
		// the serverName now resolves properly from localhost, i.e. usually this means
		// make a /etc/hosts entry if you're using NAT.
		url.append(request.getServerName());
		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443))) {
			url.append(':');
			url.append(port);
		}
		url.append(request.getContextPath());
		url.append("/EFormViewForPdfGenerationServlet?parentAjaxId=eforms&fdid=");

		return (url.toString());
	}
	
	/**
	 * @return the new document id
	 */
	public String sendEformToEmail(int eFormId) throws Exception {
		File tempFile = null;

		try {
			logger.debug("Send eform to email address. id=" + eFormId);

			tempFile = File.createTempFile("eform.", ".pdf");
			tempFile.deleteOnExit();

			// convert to PDF
			String viewUri = localUri + eFormId;
			logger.debug(localUri+eFormId);
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);
			logger.debug("Writing pdf to : " + tempFile.getCanonicalPath());

			// upload pdf to oscar docs
			String docId = uploadToOscarDocuments(tempFile, "eform", "eform");
			
			EDoc eDoc = EDocUtil.getDoc(docId);
			logger.debug("Sending pdf from : "+eDoc.getFilePath());
			
			EForm eForm = new EForm(Integer.toString(eFormId));
			
			String emailSubject = eForm.getFormName();
			
			emailPdf(eDoc.getFilePath(), emailSubject);
			
			return docId;
		} finally {
			// we'll be nice and if debugging is enabled we'll leave the file lying around so you can see it.
			if (tempFile != null && !logger.isDebugEnabled()) tempFile.delete();
		}
	}
	
	/**
	 * @return the new documentId
	 */
	private String uploadToOscarDocuments(File file, String description, String type) throws Exception {

		String originalFileName = file.getName();
		EDoc newDoc = new EDoc(description, type, originalFileName, "", providerNo, "", "", 'A', oscar.util.UtilDateUtilities.getToday("yyyy-MM-dd"), "", "", "demographic", clientId);
		newDoc.setContentType("application/pdf");
		String newFileName = newDoc.getFileName();

		FileInputStream fis = new FileInputStream(file);
		try {
			logger.debug(newFileName);
			AddEditDocumentAction.writeLocalFile(fis, newFileName);
		} finally {
			fis.close();
		}

		return(EDocUtil.addDocumentSQL(newDoc));
	}
	
	private void emailPdf(String pdfPath, String emailSubject) throws EmailException{
		logger.debug("Sending email to "+toEmailAddress + " from " + fromEmailAddress);
		EmailUtils.sendEmailWithAttachment(toEmailAddress, toName, fromEmailAddress, fromName, emailSubject, null, null, pdfPath);
		//return "";
	}
	
}
