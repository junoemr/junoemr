/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.actions;

import org.apache.log4j.Logger;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.fax.service.OutgoingFaxService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import oscar.OscarProperties;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public final class FaxAction
{

	private static final Logger logger = MiscUtils.getLogger();
	private static final OutgoingFaxService outgoingFaxService = SpringUtils.getBean(OutgoingFaxService.class);

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
		if (prop_scheme != null && !prop_scheme.isEmpty())
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
		logger.info("Generating PDF for eForm with fdid = " + formId);

		String pdfFile = "EForm." + formId + "-" + System.currentTimeMillis();
		File tempFile = File.createTempFile(pdfFile, ".pdf");

		// convert to PDF
		String viewUri = localUri + formId;
		logger.info("Converting eForm content to pdf. Target file: " + tempFile.getCanonicalPath());
		WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);

		HashSet<String> recipients = OutgoingFaxService.preProcessFaxNumbers(numbers);
		for (String recipient : recipients)
		{
			GenericFile fileToFax = FileFactory.getExistingFile(tempFile);
			outgoingFaxService.sendFax(providerId, null, recipient, fileToFax);
		}

		if (skipSave)
		{
			EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
			EFormData eFormData = eFormDataDao.find(Integer.parseInt(formId));
			eFormData.setCurrent(false);
			eFormDataDao.merge(eFormData);
		}

	}

}
