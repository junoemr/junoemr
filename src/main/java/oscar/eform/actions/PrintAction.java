/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package oscar.eform.actions;

import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.printing.HtmlToPdfServlet;
import org.oscarehr.eform.dao.EFormDataDao;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import oscar.util.UtilDateUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PrintAction extends Action
{

	private static final Logger logger = MiscUtils.getLogger();

	private String localUri = null;

	private boolean skipSave = false;
	private boolean printLabel = false;

	private HttpServletResponse response;

	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response)
	{

		if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_eform", "r", null))
		{
			throw new SecurityException("missing required security object (_eform)");
		}

		localUri = WKHtmlToPdfUtils.getEformRequestUrl(request.getParameter("providerId"),
				"", request.getScheme(), request.getContextPath());
		this.response = response;
		String id = (String) request.getAttribute("fdid");
		String providerId = request.getParameter("providerId");
		skipSave = "true".equals(request.getParameter("skipSave"));
		printLabel = "true".equalsIgnoreCase(request.getParameter("labelSizing"));
		try
		{
			printForm(id, providerId);
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("Error printing eForm", e);
			return mapping.findForward("error");
		}
		return mapping.findForward("success");
	}

	/**
	 * This method will take eforms and send them to a PHR.
	 */
	public void printForm(String formId, String providerId)
			throws HtmlToPdfConversionException, IOException
	{

		File tempFile = null;

		logger.info("Generating PDF for eform with fdid = " + formId);

		tempFile = File.createTempFile("EFormPrint." + formId, ".pdf");

		// convert to PDF
		String viewUri = localUri + formId;
		if (printLabel)
		{
			WKHtmlToPdfUtils.convertToPdfLabel(viewUri, tempFile);
		}
		else
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
		is.close();

		bos.flush();
		byte[] pdf;
		if (printLabel)
		{
			pdf = bos.getBytes();
			bos.close();
		}
		else
		{
			try
			{
				// append page number & confidentiality warning
				pdf = HtmlToPdfServlet.stamp(bos.getBytes());
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
			finally
			{
				bos.close();
			}
		}

		response.setContentType("application/pdf"); // octet-stream
		response.setHeader("Content-Disposition", "attachment; filename=\"EForm-" + formId + "-"
				+ UtilDateUtilities.getToday("yyyy-mm-dd.hh.mm.ss") + ".pdf\"");
		HtmlToPdfServlet.stream(response, pdf, false);

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
}
