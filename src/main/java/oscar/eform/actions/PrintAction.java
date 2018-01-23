/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.actions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.dao.EFormDataDao;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;

import oscar.util.UtilDateUtilities;

public class PrintAction extends Action
{

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;

	private boolean skipSave = false;
	private boolean printLabel = false;


	private HttpServletResponse response;

	public ActionForward execute(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response)
	{
		localUri = getEformRequestUrl(request);
		this.response = response;
		String id = (String) request.getAttribute("fdid");
		String providerId = request.getParameter("providerId");
		skipSave = "true".equals(request.getParameter("skipSave"));
		printLabel = "true".equals(request.getParameter("labelSizing"));
		try
		{
			printForm(id, providerId);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("", e);
			return mapping.findForward("error");
		}
		return mapping.findForward("success");
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
		url.append("/EFormViewForPdfGenerationServlet?parentAjaxId=eforms&providerId=");
		url.append(request.getParameter("providerId"));
		url.append("&fdid=");

		return (url.toString());
	}

	/**
	 * This method will take eforms and send them to a PHR.
	 */
	public void printForm(String formId, String providerId) throws HtmlToPdfConversionException
	{

		File tempFile = null;

		try
		{
			logger.info("Generating PDF for eform with fdid = " + formId);

			tempFile = File.createTempFile("EFormPrint." + formId, ".pdf");
			//tempFile.deleteOnExit();

			// convert to PDF
			String viewUri = localUri + formId;
			if (printLabel)
			{
				WKHtmlToPdfUtils.convertToPdfLabel(viewUri, tempFile);
			} else
			{
				WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);
			}
			logger.info("Writing pdf to : " + tempFile.getCanonicalPath());


			InputStream is = new BufferedInputStream(new FileInputStream(tempFile));
			ByteOutputStream bos = new ByteOutputStream();
			byte buffer[] = new byte[1024];
			int read;
			while (is.available() != 0)
			{
				read = is.read(buffer, 0, 1024);
				bos.write(buffer, 0, read);
			}

			//while (fos.read() != -1)
			response.setContentType("application/pdf");  //octet-stream
			response.setHeader("Content-Disposition", "attachment; filename=\"EForm-"
					+ formId + "-"
					+ UtilDateUtilities.getToday("yyyy-mm-dd.hh.mm.ss")
					+ ".pdf\"");
			response.getOutputStream().write(bos.getBytes(), 0, bos.getCount());

			// Removing the consulation pdf.
			tempFile.delete();

			// Removing the eform
			if (skipSave)
			{
				EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
				EFormData eFormData = eFormDataDao.find(Integer.parseInt(formId));
				eFormData.setCurrent(false);
				eFormDataDao.merge(eFormData);
			}
		}
		catch (IOException e)
		{
			logger.error("", e);
		}
	}


}
