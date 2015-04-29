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
package oscar.eform.actions;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;


import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.EmailUtils;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.oscarehr.casemgmt.dao.CaseManagementNoteLinkDAO;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import oscar.util.UtilDateUtilities;
import oscar.oscarEncounter.data.EctProgram;

import org.oscarehr.util.LoggedInInfo;

import oscar.OscarProperties;

import com.lowagie.text.DocumentException;

public final class EmailAction {

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;
	
	private boolean skipSave = false;
	
	private String fromEmailAddress = null;

	private static CaseManagementNoteLinkDAO cmDao = (CaseManagementNoteLinkDAO) SpringUtils.getBean("CaseManagementNoteLinkDAO");
	private static ProviderDao providerDao = (ProviderDao)SpringUtils.getBean("providerDao");
	
	private static HttpServletRequest servletRequest;


	public EmailAction(HttpServletRequest request) {
		localUri = getEformRequestUrl(request);
		skipSave = "true".equals(request.getParameter("skipSave"));
		
		servletRequest = request;
		
		//LoggedInInfo loggedInfo = LoggedInInfo.loggedInInfo.get();
		//providerNo = loggedInfo.loggedInProvider.getProviderNo();
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
	 * This method will take eforms and send them to a PHR.
	 * @throws DocumentException 
	 * @throws EmailException 
	 */
	public void sendEformToEmail( String toEmailAddress, String toName, String formId) throws DocumentException, EmailException {
		
		File tempFile = null;

		try {
			logger.info("Generating PDF for eform with fdid = " + formId);

			tempFile = File.createTempFile("EForm." + formId, ".pdf");
			//tempFile.deleteOnExit();

			// convert to PDF
			String viewUri = localUri + formId;
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);
			logger.info("Writing pdf to : "+tempFile.getCanonicalPath());

			String tempPath = OscarProperties.getInstance().getProperty("email_file_location");

		    String tempName = "EForm-" + formId + "." + System.currentTimeMillis();
			
			String tempPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
			
			// Copying the pdf.
			FileUtils.copyFile(tempFile, new File(tempPdf));
			logger.debug("Copying pdf to : "+tempPdf);
			

			// A little sanity check to ensure both files exist.
			if (!new File(tempPdf).exists()) {
				throw new DocumentException("Unable to create files for email of eform " + formId + ".");
			}
			
			EFormDataDao eFormDataDao=(EFormDataDao) SpringUtils.getBean("EFormDataDao");
			EFormData eFormData=eFormDataDao.find(Integer.parseInt(formId));

			if (skipSave) {
	        	 eFormData.setCurrent(false);
	        	 eFormDataDao.merge(eFormData);
			}
			
			logger.debug("Emailing PDF from "+tempPdf);
			logger.debug("skipsave: "+skipSave);
			String emailSubject=OscarProperties.getInstance().getProperty("eform_email_subject");
			fromEmailAddress = OscarProperties.getInstance().getProperty("eform_email_from_address");

			emailPdf(tempPdf, emailSubject, toEmailAddress, toName);
			tempFile.delete();			
			
			// write note to echart
			saveEmailNoteOnEChart(formId,Integer.toString(eFormData.getDemographicId()), eFormData.getFormName(), toEmailAddress);
						
		} catch (IOException e) {
			MiscUtils.getLogger().error("Error converting and sending eform. id="+formId, e);
		} 
	}
	
	private void saveEmailNoteOnEChart(String formId, String demographicNo, String formName, String toEmailAddress){
		
		Date now = UtilDateUtilities.now();
		CaseManagementNote cmn = new CaseManagementNote();
		cmn.setUpdate_date(now);
		cmn.setObservation_date(now);
		cmn.setDemographic_no(demographicNo);
		
		LoggedInInfo loggedInfo = LoggedInInfo.loggedInInfo.get();
		String providerNo = loggedInfo.loggedInProvider.getProviderNo();
		
		
		HttpSession se = servletRequest.getSession();
		String prog_no = new EctProgram(se).getProgram(providerNo);
		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(se.getServletContext());
		CaseManagementManager cmm = (CaseManagementManager) ctx.getBean("caseManagementManager");
		
		
		cmn.setProviderNo("-1");// set the provider no to be -1 so the editor appears as 'System'.
		Provider provider = providerDao.getProvider(providerNo);
		String provFirstName = "";
		String provLastName = "";
		if(provider!=null) {
			provFirstName=provider.getFirstName();
			provLastName=provider.getLastName();
		}
		String strNote = "\"" + formName + "\" " + " emailed to " + 
						toEmailAddress + " by " + provFirstName + " " + provLastName + ".";

		// String strNote="Document"+" "+docDesc+" "+ "created at "+now+".";
		cmn.setNote(strNote);
		cmn.setSigned(true);
		cmn.setSigning_provider_no("-1");
		cmn.setProgram_no(prog_no);
		
		SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
		SecRole doctorRole = secRoleDao.findByName("doctor");		
		cmn.setReporter_caisi_role(doctorRole.getId().toString());
		
		cmn.setReporter_program_team("0");
		cmn.setPassword("NULL");
		cmn.setLocked(false);
		cmn.setHistory(strNote);
		cmn.setPosition(0);
		
		Long note_id = cmm.saveNoteSimpleReturnID(cmn);
		
		// Add a noteLink to casemgmt_note_link
		CaseManagementNoteLink cmnl = new CaseManagementNoteLink();
		cmnl.setTableName(CaseManagementNoteLink.EFORMDATA);
		cmnl.setTableId(Long.parseLong(formId));
		cmnl.setNoteId(note_id);
		cmDao.save(cmnl);
	}
	
	private void emailPdf(String pdfPath, String emailSubject, String toEmailAddress, String toName) throws EmailException{
		try {
			logger.debug("Sending email to "+toEmailAddress + " from " + fromEmailAddress);
			EmailUtils.sendEmailWithAttachment(toEmailAddress, toName, fromEmailAddress, null, emailSubject, null, null, pdfPath);
		}catch (EmailException e){
			logger.error("Error sending email to "+toEmailAddress + " from " + fromEmailAddress, e);
		}
	}

}
