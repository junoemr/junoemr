/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.actions;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.dao.FaxConfigDao;
import org.oscarehr.common.dao.FaxJobDao;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.model.FaxConfig;
import org.oscarehr.common.model.FaxJob;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import oscar.OscarProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public final class FaxAction
{

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;

	private boolean skipSave = false;

	public FaxAction(HttpServletRequest request)
	{
		localUri = getEformRequestUrl(request);
		skipSave = "true".equals(request.getParameter("skipSave"));
	}

	/**
	 * This method is a copy of Apache Tomcat's ApplicationHttpRequest getRequestURL method with the exception that the uri is removed and replaced with our eform viewing uri. Note that this requires that the remote url is valid for local access. i.e. the
	 * host name from outside needs to resolve inside as well. The result needs to look something like this : https://127.0.0.1:8443/oscar/eformViewForPdfGenerationServlet?fdid=2&parentAjaxId=eforms
	 */
	private String getEformRequestUrl(HttpServletRequest request)
	{
		StringBuilder url = new StringBuilder();
		String scheme = request.getScheme();
		String prop_scheme = OscarProperties.getInstance().getProperty("oscar_protocol");
		if (prop_scheme != null && prop_scheme != "")
		{
			scheme = prop_scheme;
		}

		Integer port;
		try
		{
			port = new Integer(OscarProperties.getInstance().getProperty("oscar_port"));
		}
		catch (Exception e)
		{
			port = 8443;
		}
		if (port < 0) port = 80; // Work around java.net.URL bug

		url.append(scheme);
		url.append("://");
		//url.append(request.getServerName());
		url.append("127.0.0.1");

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
	 * @param providerId the provider number to record in the faxJob
	 * @throws IOException
	 * @throws HtmlToPdfConversionException
	 */
	public void faxForms(String[] numbers, String formId, String providerId) throws IOException, HtmlToPdfConversionException
	{
		String faxFileLocation = OscarProperties.getInstance().getProperty(
				"fax_file_location", System.getProperty("java.io.tmpdir"));
		File tempFile = null;

		try
		{
			logger.info("Generating PDF for eForm with fdid = " + formId);

			String pdfFile = "EForm." + formId + System.currentTimeMillis();
			tempFile = File.createTempFile(pdfFile, ".pdf");

			// convert to PDF
			String viewUri = localUri + formId;
			logger.info("Converting eForm content to pdf. Target file: " + tempFile.getCanonicalPath());
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);

			// Removing all non digit characters from fax numbers.
			for (int i = 0; i < numbers.length; i++)
			{
				numbers[i] = numbers[i].trim().replaceAll("\\D", "");
			}

			// Removing duplicate phone numbers.
			HashSet<String> recipients = new HashSet<>(Arrays.asList(numbers));

			FileOutputStream fos;
			FaxJobDao faxJobDao = SpringUtils.getBean(FaxJobDao.class);
			FaxConfigDao faxConfigDao = SpringUtils.getBean(FaxConfigDao.class);
			List<FaxConfig> faxConfigs = faxConfigDao.findAll(null, null);
			for (String recipient : recipients)
			{
				String fileNameFormat = "EForm-" + formId + "." + System.currentTimeMillis();
				String pdfFileName = String.format("%s%s%s.pdf", faxFileLocation, File.separator, fileNameFormat);
				String txtFileName = String.format("%s%s%s.txt", faxFileLocation, File.separator, fileNameFormat);

				// Copying the fax pdf.
				FileUtils.copyFile(tempFile, new File(pdfFileName));

				FaxJob faxJob;

				for (FaxConfig faxConfig : faxConfigs)
				{
					PdfReader pdfReader = new PdfReader(tempFile.getAbsolutePath());

					faxJob = new FaxJob();
					faxJob.setDestination(recipient);
					faxJob.setFax_line(null);
					faxJob.setFile_name(tempFile.getName());
					faxJob.setUser(faxConfig.getFaxUser());
					faxJob.setNumPages(pdfReader.getNumberOfPages());
					faxJob.setStamp(new Date());
					faxJob.setStatus(FaxJob.STATUS.SENT);
					faxJob.setOscarUser(providerId);
					faxJob.setDemographicNo(null);

					faxJobDao.persist(faxJob);
					break;
				}

				// Creating text file with the specialists fax number.
				fos = new FileOutputStream(txtFileName);
				PrintWriter pw = new PrintWriter(fos);
				pw.println(recipient);
				pw.close();
				fos.close();

				// A little sanity check to ensure the file exists.
				if (!new File(txtFileName).exists())
				{
					throw new IOException("Unable to create fax file for eForm " + formId + ".");
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
			// Removing the temp pdf.
			if (tempFile != null)
			{
				tempFile.delete();
			}
		}
	}

}
