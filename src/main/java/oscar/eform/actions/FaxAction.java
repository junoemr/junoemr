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
import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;

import oscar.OscarProperties;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.FaxUtils;

public final class FaxAction
{

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;
	private HttpServletRequest request;
	private OscarProperties props;

	private boolean skipSave = false;

	public FaxAction(HttpServletRequest request)
	{
		this.request = request;
		localUri = getEformRequestUrl(request);
		skipSave = "true".equals(request.getParameter("skipSave"));
		props = OscarProperties.getInstance();
	}

	/**
	 * This method is a copy of Apache Tomcat's ApplicationHttpRequest getRequestURL method with the exception that the uri is removed and replaced with our eform viewing uri. Note that this requires that the remote url is valid for local access. i.e. the
	 * host name from outside needs to resolve inside as well. The result needs to look something like this : https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms
	 */
	private String getEformRequestUrl(HttpServletRequest request)
	{
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

		if ((scheme.equals("http") && (port != 80)) || (scheme.equals("https") && (port != 443)))
		{
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
	 * @param numbers    the fax numbers to send to
	 * @param formId     the fdid of the form to send
	 * @param providerId the provider number needed if encounter_notes_add_fax_notes_eform is on
	 * @throws IOException                  if an I/O exception occurs
	 * @throws HtmlToPdfConversionException if an error occurs converting the form to pdf
	 */
	public void faxForms(String[] numbers, String formId, String providerId) throws IOException, HtmlToPdfConversionException
	{
		String faxFileLocation = OscarProperties.getInstance().getProperty("fax_file_location");
		File tempFile = null;

		try
		{
			logger.info("Generating PDF for eForm with fdid = " + formId);

			tempFile = File.createTempFile("EForm." + formId, ".pdf");

			// convert to PDF
			String viewUri = localUri + formId;
			logger.info("Attempting to convert eForm content to pdf. Target file: " + tempFile.getCanonicalPath());
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);

			// Removing all non digit characters from fax numbers.
			for (int i = 0; i < numbers.length; i++)
			{
				numbers[i] = numbers[i].trim().replaceAll("\\D", "");
			}

			// Removing duplicate phone numbers.
			HashSet<String> recipients = new HashSet<String>(Arrays.asList(numbers));

			FileOutputStream fos;
			for (String recipient : recipients)
			{
				String fileNameFormat = "EForm-" + formId + "." + System.currentTimeMillis();
				String pdfFileName = String.format("%s%s%s.pdf", faxFileLocation, File.separator, fileNameFormat);
				String txtFileName = String.format("%s%s%s.txt", faxFileLocation, File.separator, fileNameFormat);

				// Copying the fax pdf.
				FileUtils.copyFile(tempFile, new File(pdfFileName));

				// Creating text file with the specialists fax number.
				fos = new FileOutputStream(txtFileName);
				PrintWriter pw = new PrintWriter(fos);
				pw.println(recipient);
				pw.close();
				fos.close();

				// A little sanity check to ensure file exists.
				if (!new File(txtFileName).exists())
				{
					throw new IOException("Unable to create fax file for eForm " + formId + ".");
				}

				/* -- OHSUPPORT-2932 -- */
				if (props.isPropertyActive("encounter_notes_add_fax_notes_eform"))
				{
					String demographic_no = request.getParameter("efmdemographic_no");
					String programNo = new EctProgram(request.getSession()).getProgram(providerId);
					FaxUtils.addFaxEformEncounterNote(demographic_no, providerId, programNo, recipient, Long.valueOf(formId));
				}
			}

			if (skipSave)
			{
				EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
				EFormData eFormData = eFormDataDao.find(Integer.parseInt(formId));
				eFormData.setCurrent(false);
				eFormDataDao.merge(eFormData);
			}
		}
		finally
		{
			if (tempFile != null)
			{
				tempFile.delete();
			}
		}
	}
}
