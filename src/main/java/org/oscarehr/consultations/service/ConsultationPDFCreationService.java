/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.consultations.service;

import com.lowagie.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.dms.EDoc;
import oscar.oscarEncounter.oscarConsultationRequest.pageUtil.ConsultationPDFCreator;
import oscar.oscarEncounter.oscarConsultationRequest.pageUtil.ImagePDFCreator;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ConsultationPDFCreationService
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	private ConsultationAttachmentService consultationAttachmentService;

	public ByteOutputStream getRequestOutputStream(HttpServletRequest request, LoggedInInfo loggedInInfo, Integer demographicNo, Integer requestId) throws IOException, DocumentException, HtmlToPdfConversionException
	{
		ByteOutputStream bos;
		List<InputStream> streams = new ArrayList<>();

		try
		{
			List<EDoc> attachedDocuments = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demographicNo, requestId);
			List<LabResultData> attachedLabs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demographicNo, requestId);
			List<EFormData> attachedEForms = consultationAttachmentService.getAttachedEForms(demographicNo, requestId);

			String path = GenericFile.DOCUMENT_BASE_DIR;
			List<Object> inputList = new ArrayList<>();

			byte[] buffer;
			ByteInputStream bis;

			bos = new ByteOutputStream();
			ConsultationPDFCreator cpdfc = new ConsultationPDFCreator(request, bos);
			cpdfc.printPdf(loggedInInfo);

			buffer = bos.getBytes();
			bis = new ByteInputStream(buffer, bos.getCount());
			bos.close();
			streams.add(bis);
			inputList.add(bis);

			for(EDoc doc : attachedDocuments)
			{
				if(doc.isPrintable())
				{
					if(doc.isImage())
					{
						bos = new ByteOutputStream();
						request.setAttribute("imagePath", path + doc.getFileName());
						request.setAttribute("imageTitle", doc.getDescription());
						ImagePDFCreator ipdfc = new ImagePDFCreator(request, bos);
						ipdfc.printPdf();

						buffer = bos.getBytes();
						bis = new ByteInputStream(buffer, bos.getCount());
						bos.close();
						streams.add(bis);
						inputList.add(bis);
					}
					else if(doc.isPDF())
					{
						inputList.add(path + doc.getFileName());
					}
					else
					{
						logger.error("EctConsultationFormRequestPrintAction: " +
								doc.getType() + " is marked as printable but no means have been established to print it.");
					}
				}
			}

			// Iterating over requested labs.
			for(LabResultData lab : attachedLabs)
			{
				// Storing the lab in PDF format inside a byte stream.
				bos = new ByteOutputStream();
				LabPDFCreator labPDFCreator = new LabPDFCreator(bos, lab.segmentID, null);
				labPDFCreator.printPdf();

				// Transferring PDF to an input stream to be concatenated with
				// the rest of the documents.
				buffer = bos.getBytes();
				bis = new ByteInputStream(buffer, bos.getCount());
				bos.close();
				streams.add(bis);
				inputList.add(bis);
			}

			// Iterating over requested eforms.
			for(EFormData eForm : attachedEForms)
			{
				String eFormRequestUrl = WKHtmlToPdfUtils.getEformRequestUrl(request.getParameter("providerId"),
						String.valueOf(eForm.getId()), request.getScheme(), request.getContextPath());
				buffer = WKHtmlToPdfUtils.convertToPdf(eFormRequestUrl);
				bis = new ByteInputStream(buffer, buffer.length);
				streams.add(bis);
				inputList.add(bis);
			}

			if(!inputList.isEmpty())
			{
				bos = new ByteOutputStream();
				ConcatPDF.concat(inputList, bos);
			}
		}
		finally
		{
			// Cleaning up InputStreams created for concatenation.
			for(InputStream is : streams)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
					logger.error("Error closing streams", e);
				}
			}
		}
		return bos;
	}
}
