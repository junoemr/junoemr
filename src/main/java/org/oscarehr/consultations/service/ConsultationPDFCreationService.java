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
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.labs.dao.Hl7DocumentLinkDao;
import org.oscarehr.labs.model.Hl7DocumentLink;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
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
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ConsultationPDFCreationService
{
	private static final Logger logger = MiscUtils.getLogger();
	private static Hl7DocumentLinkDao hl7DocumentLinkDao = SpringUtils.getBean(Hl7DocumentLinkDao.class);
	private static DocumentDao documentDao = SpringUtils.getBean(DocumentDao.class);

	public List<InputStream> toEDocInputStreams(HttpServletRequest request, List<EDoc> attachedDocuments) throws IOException, DocumentException
	{
		List<InputStream> streamList = new ArrayList<>(attachedDocuments.size());
		for(EDoc doc : attachedDocuments)
		{
			if(doc.isPrintable())
			{
				GenericFile docFile = FileFactory.getDocumentFile(doc.getFileName());
				if(doc.isImage())
				{
					ByteOutputStream bos = new ByteOutputStream();
					request.setAttribute("imagePath", docFile.getFileObject().getPath());
					request.setAttribute("imageTitle", doc.getDescription());
					ImagePDFCreator ipdfc = new ImagePDFCreator(request, bos);
					ipdfc.printPdf();

					byte[] buffer = bos.getBytes();
					streamList.add(new ByteInputStream(buffer, bos.getCount()));
					bos.close();
				}
				else if(doc.isPDF())
				{
					streamList.add(docFile.toFileInputStream());
				}
				else
				{
					logger.error("EctConsultationFormRequestPrintAction: " +
							doc.getType() + " is marked as printable but no means have been established to print it.");
				}
			}
		}
		return streamList;
	}

	public List<InputStream> toLabInputStreams(HttpServletRequest request, List<LabResultData> attachedLabs) throws IOException, DocumentException
	{
		List<InputStream> streamList = new ArrayList<>();
		// Iterating over requested labs.
		for(LabResultData lab : attachedLabs)
		{
			// Storing the lab in PDF format inside a byte stream.
			int labNo = Integer.parseInt(lab.segmentID);
			List<Hl7DocumentLink> possibleDocs = hl7DocumentLinkDao.getDocumentsForLab(labNo);
			if (possibleDocs != null)
			{
				// Embedded PDFs need to be treated as documents for printing purposes
				for (Hl7DocumentLink doc : possibleDocs)
				{
					Document embeddedDoc = documentDao.find(doc.getDocumentNo());
					GenericFile file = FileFactory.getDocumentFile(embeddedDoc.getDocfilename());
					streamList.add(file.asFileInputStream());
				}
			}
			else
			{
				ByteOutputStream bos = new ByteOutputStream();
				LabPDFCreator labPDFCreator = new LabPDFCreator(bos, lab.segmentID, null);
				labPDFCreator.printPdf();

				// Transferring PDF to an input stream to be concatenated with
				// the rest of the documents.
				byte[] buffer = bos.getBytes();
				streamList.add(new ByteInputStream(buffer, bos.getCount()));
				bos.close();
			}
		}
		return streamList;
	}

	public List<InputStream> toEFormInputStreams(HttpServletRequest request, List<EFormData> attachedEForms) throws IOException, HtmlToPdfConversionException
	{
		List<InputStream> streamList = new ArrayList<>(attachedEForms.size());
		for(EFormData eForm : attachedEForms)
		{
			String eFormRequestUrl = WKHtmlToPdfUtils.getEformRequestUrl(request.getParameter("providerId"),
					String.valueOf(eForm.getId()), request.getScheme(), request.getContextPath());
			byte[]  buffer = WKHtmlToPdfUtils.convertToPdf(eFormRequestUrl);
			streamList.add(new ByteInputStream(buffer, buffer.length));
		}

		return streamList;
	}

	public OutputStream combineStreams(List<InputStream> streams, OutputStream outputStream)
	{
		//TODO this is dumb. make it not dumb
		ConcatPDF.concat(new ArrayList<>(streams), outputStream);
		return outputStream;
	}

	public InputStream getConsultationRequestAsStream(HttpServletRequest request, LoggedInInfo loggedInInfo) throws IOException, DocumentException
	{
		byte[] buffer;
		ByteOutputStream bos = new ByteOutputStream();
		ConsultationPDFCreator cpdfc = new ConsultationPDFCreator(request, bos);
		cpdfc.printPdf(loggedInInfo);

		buffer = bos.getBytes();
		ByteInputStream bis = new ByteInputStream(buffer, bos.getCount());
		bos.close();

		return bis;
	}
}
