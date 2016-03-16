/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.actions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.service.CaseManagementManager;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.SecRole;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;

import com.lowagie.text.DocumentException;

import oscar.OscarProperties;
import oscar.dms.EDocUtil;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.UtilDateUtilities;

public final class FaxAction {

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;
	private HttpServletRequest request;
	private HttpSession session;
	private OscarProperties props;
	
	private boolean skipSave = false;

	public FaxAction(HttpServletRequest request) {
		this.request = request;
		localUri = getEformRequestUrl(request);
		skipSave = "true".equals(request.getParameter("skipSave"));
		props = OscarProperties.getInstance();
		session = request.getSession();
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
		url.append("/EFormViewForPdfGenerationServlet?parentAjaxId=eforms&prepareForFax=true&providerId=");
		url.append(request.getParameter("providerId"));
		url.append("&fdid=");

		return (url.toString());
	}
	
	/**
	 * Add an encounter note when a fax is sent.
	 *  -- OHSUPPORT-2932 -- 
	 */
	private boolean addFaxEncounterNote(String providerId, String demographic_no, String faxNo, Long formId) {
		CaseManagementManager cmm = (CaseManagementManager) SpringUtils.getBean("caseManagementManager");
		if(demographic_no != null && providerId != null) {
			Date now = UtilDateUtilities.now();
			CaseManagementNote cmn = new CaseManagementNote();
			cmn.setUpdate_date(now);
			cmn.setObservation_date(now);
			cmn.setDemographic_no(demographic_no);
			cmn.setPosition(0);
			cmn.setReporter_program_team("0");
			cmn.setPassword("NULL");
			cmn.setLocked(false);
			
			String prog_no = new EctProgram(session).getProgram(providerId);
			cmn.setProgram_no(prog_no);
			
			SecRoleDao secRoleDao = (SecRoleDao) SpringUtils.getBean("secRoleDao");
			SecRole doctorRole = secRoleDao.findByName("doctor");
			cmn.setReporter_caisi_role(doctorRole.getId().toString());
			
			Provider provider = EDocUtil.getProvider(providerId);
			String provFirstName = "";
			String provLastName = "";
			if(provider!=null) {
				provFirstName=provider.getFirstName();
				provLastName=provider.getLastName();
			}
			
			String strNote = "Fax Sent to " + faxNo + " at " + now + " by " + provFirstName + " " + provLastName + ".";

			cmn.setNote(strNote);
			cmn.setHistory(strNote);
			cmn.setProviderNo(providerId);
			cmn.setSigned(true);
			cmn.setSigning_provider_no(providerId);
			
			Long note_id = cmm.saveNoteSimpleReturnID(cmn);
			CaseManagementNoteLink cmLink = new CaseManagementNoteLink(CaseManagementNoteLink.EFORMDATA, formId, note_id);
			EDocUtil.addCaseMgmtNoteLink(cmLink);
			
			logger.info("Saved note id=" + note_id.toString() + " for demographic " + demographic_no);
			return true;
		}
		else {
			logger.error("failed to add fax note to encounter notes. null demographicNo or providerId");
		}
		return false;
	}

	/**
	 * This method will take eforms and send them to a PHR.
	 * @throws DocumentException 
	 */
	public void faxForms(String[] numbers, String formId, String providerId) throws DocumentException {
		
		File tempFile = null;

		try {
			logger.info("Generating PDF for eform with fdid = " + formId);

			tempFile = File.createTempFile("EForm." + formId, ".pdf");
			//tempFile.deleteOnExit();

			// convert to PDF
			String viewUri = localUri + formId;
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);
			logger.info("Writing pdf to : "+tempFile.getCanonicalPath());
			
			// Removing all non digit characters from fax numbers.
			for (int i = 0; i < numbers.length; i++) { 
				numbers[i] = numbers[i].trim().replaceAll("\\D", "");
			}
			ArrayList<String> recipients = new ArrayList<String>(Arrays.asList(numbers));
			
			// Removing duplicate phone numbers.
			recipients = new ArrayList<String>(new HashSet<String>(recipients));
			String tempPath = OscarProperties.getInstance().getProperty("fax_file_location");
			FileOutputStream fos;
			for (int i = 0; i < recipients.size(); i++) {					
			    String faxNo = recipients.get(i).trim().replaceAll("\\D", "");
			    if (faxNo.length() < 7) { throw new DocumentException("Document target fax number '"+faxNo+"' is invalid."); }
			    String tempName = "EForm-" + formId + "." + System.currentTimeMillis();
				
				String tempPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
				String tempTxt = String.format("%s%s%s.txt", tempPath, File.separator, tempName);
				
				// Copying the fax pdf.
				FileUtils.copyFile(tempFile, new File(tempPdf));
				
				// Creating text file with the specialists fax number.
				fos = new FileOutputStream(tempTxt);				
				PrintWriter pw = new PrintWriter(fos);
				pw.println(faxNo);
				pw.close();
				fos.close();
				
				// A little sanity check to ensure both files exist.
				if (!new File(tempPdf).exists() || !new File(tempTxt).exists()) {
					throw new DocumentException("Unable to create files for fax of eform " + formId + ".");
				}		
				if (skipSave) {
		        	 EFormDataDao eFormDataDao=(EFormDataDao) SpringUtils.getBean("EFormDataDao");
		        	 EFormData eFormData=eFormDataDao.find(Integer.parseInt(formId));
		        	 eFormData.setCurrent(false);
		        	 eFormDataDao.merge(eFormData);
				}
				
				/* -- OHSUPPORT-2932 -- */
				if(props.isPropertyActive("encounter_notes_add_fax_notes")) {
					String demographic_no = request.getParameter("efmdemographic_no");
					addFaxEncounterNote(providerId, demographic_no, faxNo, Long.valueOf(formId));
				}
			}
			// Removing the consulation pdf.
			tempFile.delete();
						
		} catch (IOException e) {
			MiscUtils.getLogger().error("Error converting and sending eform. id="+formId, e);
		} 
	}

}
