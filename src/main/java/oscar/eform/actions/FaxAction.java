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
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;

import com.lowagie.text.DocumentException;

import oscar.OscarProperties;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.FaxUtils;

public final class FaxAction {

	private static final Logger logger = MiscUtils.getLogger();
	private static final int MAX_PDF_CONVERSION_ATTEMPTS = 2;

	private String localUri = null;
	private HttpServletRequest request;
	private OscarProperties props;
	
	private boolean skipSave = false;

	public FaxAction(HttpServletRequest request) {
		this.request = request;
		localUri = getEformRequestUrl(request);
		skipSave = "true".equals(request.getParameter("skipSave"));
		props = OscarProperties.getInstance();
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
	 * Prepares eForm fax files and places them in the outgoing faxes location.
	 *
	 * @param numbers     the fax numbers to send to
	 * @param formId      the fdid of the form to send
	 * @param providerId  the provider number
	 * @return true if successful, false otherwise
	 */
	public Boolean faxForms(String[] numbers, String formId, String providerId) {
		
		File tempFile = null;

		try {
			logger.info("Generating PDF for eForm with fdid = " + formId);

			tempFile = File.createTempFile("EForm." + formId, ".pdf");

			// convert to PDF
			String viewUri = localUri + formId;
			int pdfConversionAttempts = 0;
			do
			{
				logger.info("Attempting to convert eForm content to pdf. Target file: " + tempFile.getCanonicalPath());
				WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);
				pdfConversionAttempts++;
			} while (tempFile.length() == 0 && pdfConversionAttempts < MAX_PDF_CONVERSION_ATTEMPTS);

			// failed to generate pdf
			if (tempFile.length() == 0)
			{
				tempFile.delete();
				logger.error("Unable to convert eForm with fdid " + formId +
						" to pdf after " + MAX_PDF_CONVERSION_ATTEMPTS + " attempts.");
				return false;
			}

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
			    if (faxNo.length() < 7)
				{
					logger.error("Document target fax number '" + faxNo + "' is invalid.");
					return false;
				}
				String tempName = "EForm-" + formId + "." + System.currentTimeMillis();
				
				String tempPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
				String tempTxt = String.format("%s%s%s.txt", tempPath, File.separator, tempName);
				
				// Copying the fax pdf.
				FileUtils.copyFile(tempFile, new File(tempPdf));

				// Removing the tmp pdf.
				tempFile.delete();

				// Creating text file with the specialists fax number.
				fos = new FileOutputStream(tempTxt);				
				PrintWriter pw = new PrintWriter(fos);
				pw.println(faxNo);
				pw.close();
				fos.close();
				
				// A little sanity check to ensure both files exist.
				if (!new File(tempPdf).exists() || !new File(tempTxt).exists()) {
					logger.error("Unable to create files for fax of eForm " + formId + ".");
					return false;
				}		
				if (skipSave) {
		        	 EFormDataDao eFormDataDao=(EFormDataDao) SpringUtils.getBean("EFormDataDao");
		        	 EFormData eFormData=eFormDataDao.find(Integer.parseInt(formId));
		        	 eFormData.setCurrent(false);
		        	 eFormDataDao.merge(eFormData);
				}
				
				/* -- OHSUPPORT-2932 -- */
				if(props.isPropertyActive("encounter_notes_add_fax_notes_eform")) {
					String demographic_no = request.getParameter("efmdemographic_no");
					String programNo = new EctProgram(request.getSession()).getProgram(providerId);
					FaxUtils.addFaxEformEncounterNote(demographic_no, providerId, programNo, faxNo, Long.valueOf(formId));
				}
			}
		} catch (IOException e) {
			MiscUtils.getLogger().error("I/O Error occurred while preparing eForm fax files. form id="+formId, e);
			if (tempFile != null)
			{
				tempFile.delete();
			}
			return false;
		}

		return true;
	}
}
