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
import org.oscarehr.fax.model.FaxFileType;
import org.oscarehr.fax.service.OutgoingFaxService;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class FaxAction
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final OutgoingFaxService outgoingFaxService = SpringUtils.getBean(OutgoingFaxService.class);

	private final String localUri;
	private final boolean skipSave;

	public FaxAction(HttpServletRequest request)
	{
		localUri = WKHtmlToPdfUtils.getEformRequestUrl(request.getParameter("providerId"),
				"", request.getScheme(), request.getContextPath());
		skipSave = "true".equals(request.getParameter("skipSave"));
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
	public List<FaxOutboxTransferOutbound> faxForms(String[] numbers, String formId, String providerId) throws IOException, HtmlToPdfConversionException, InterruptedException
	{
		HashSet<String> recipients = OutgoingFaxService.preProcessFaxNumbers(numbers);
		List<FaxOutboxTransferOutbound> transferList = new ArrayList<>(recipients.size());
		for (String recipient : recipients)
		{
			logger.info("Generating PDF for eForm with fdid = " + formId);

			String pdfFile = "EForm." + formId + "-" + System.currentTimeMillis();
			File tempFile = File.createTempFile(pdfFile, ".pdf");

			// convert to PDF
			String viewUri = localUri + formId;
			logger.info("Converting eForm content to pdf. Target file: " + tempFile.getCanonicalPath());
			WKHtmlToPdfUtils.convertToPdf(viewUri, tempFile);

			GenericFile fileToFax = FileFactory.getExistingFile(tempFile);
			FaxOutboxTransferOutbound transfer = outgoingFaxService.queueAndSendFax(providerId, null, recipient, FaxFileType.FORM, fileToFax);
			transferList.add(transfer);
		}

		if (skipSave)
		{
			EFormDataDao eFormDataDao = (EFormDataDao) SpringUtils.getBean("EFormDataDao");
			EFormData eFormData = eFormDataDao.find(Integer.parseInt(formId));
			eFormData.setCurrent(false);
			eFormDataDao.merge(eFormData);
		}
		return transferList;
	}
}
