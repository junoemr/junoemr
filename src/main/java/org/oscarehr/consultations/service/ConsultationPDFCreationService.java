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
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.document.dao.DocumentDao;
import org.oscarehr.document.model.Document;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.hospitalReportManager.service.HRMDocumentService;
import org.oscarehr.labs.dao.Hl7DocumentLinkDao;
import org.oscarehr.labs.model.Hl7DocumentLink;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.WKHtmlToPdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import oscar.dms.EDoc;
import oscar.oscarEncounter.oscarConsultationRequest.pageUtil.ConsultationPDFCreator;
import oscar.oscarEncounter.oscarConsultationRequest.pageUtil.ImagePDFCreator;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ConsultationPDFCreationService
{
	// 256*1024*1024 (256MB)
	private static final long maxMemoryUsage = 268435456L;

	private static final Logger logger = MiscUtils.getLogger();
	@Autowired
	private Hl7DocumentLinkDao hl7DocumentLinkDao;
	@Autowired
	private DocumentDao documentDao;

	@Autowired
	private HRMDocumentService hrmDocumentService;

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

	/**
	 * For every lab, attach either a PDF interpretation of the lab to the consultation
	 * or the contents of any associated embedded documents to the lab.
	 * @param attachedLabs list of labs to attach
	 * @return a list of input streams for the PDF content we're attaching to the consultation printout
	 */
	public List<InputStream> toLabInputStreams(List<LabResultData> attachedLabs) throws IOException, DocumentException
	{
		List<InputStream> streamList = new ArrayList<>();
		for(LabResultData lab : attachedLabs)
		{
			int labNo = Integer.parseInt(lab.segmentID);
			List<Hl7DocumentLink> possibleDocs = hl7DocumentLinkDao.getDocumentsForLab(labNo);
			if (possibleDocs != null && !possibleDocs.isEmpty())
			{
				// Embedded PDFs need to be treated as documents for printing purposes
				for (Hl7DocumentLink doc : possibleDocs)
				{
					Document embeddedDoc = documentDao.find(doc.getDocumentNo());
					if ("application/pdf".equals(embeddedDoc.getContenttype()))
					{
						// The orientation of each page within the embedded PDFs is not guaranteed to be portrait.
						// To change orientation for all pages to be portrait, operate on a copy of the file
						GenericFile origFile = FileFactory.getDocumentFile(embeddedDoc.getDocfilename());
						GenericFile copiedFile = FileFactory.copy(origFile);
						File pdfFile = copiedFile.getFileObject();

						PDDocument pdf = PDDocument.load(pdfFile, MemoryUsageSetting.setupMainMemoryOnly(maxMemoryUsage));
						// To change orientation, we need to operate on each page of the PDF individually
						for (PDPage page : pdf.getPages())
						{
							PDRectangle mediaBox = page.getMediaBox();
							if (mediaBox.getWidth() > mediaBox.getHeight())
							{
								page.setRotation(page.getRotation() + 90);
							}

						}
						// Save the temporary PDF changes
						pdf.save(pdfFile);
						pdf.close();
						streamList.add(new FileInputStream(pdfFile));
						// We no longer need the temp file once the changes are in memory as an InputStream
						copiedFile.deleteFile();
					}
					else
					{
						logger.error("Error with loading document with ID " + embeddedDoc.getId()
								+ " via lab with ID " + labNo
								+ ". Check content type and that the document can be opened");
					}
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

	public List<InputStream> toHRMInputStreams(HttpServletRequest request, List<HrmDocument> attachedHRM) throws IOException, HtmlToPdfConversionException, JAXBException, InterruptedException, SAXException
	{
		List<InputStream> streamList = new ArrayList<>(attachedHRM.size());
		for(HrmDocument hrmDocument : attachedHRM)
		{
			streamList.add(hrmDocumentService.toPdfInputStream(hrmDocument));
		}
		return streamList;
	}

	/**
	 * Helper function to combine several InputStreams and combine into a single temporary PDF.
	 * Used for previewing prints and for faxing purposes
	 * @param streams list of streams we want to concatenate
	 * @param outputStream the output stream to merge all the streams into
	 * @throws IOException if any of the InputStreams can't be read
	 * @return number of bytes over the limit, or 0 if we succeeded
	 */
	public long combineStreams(List<InputStream> streams, OutputStream outputStream)
			throws IOException
	{
		long totalSize = 0L;
		for (InputStream inputStream : streams)
		{
			totalSize += inputStream.available();
		}
		if (totalSize > maxMemoryUsage)
		{
			long excessSize = totalSize - maxMemoryUsage;
			String displaySize = FileUtils.byteCountToDisplaySize(excessSize);
			MiscUtils.getLogger().error("Total size of the streams is too large, over limit by: " + displaySize);
			return excessSize;
		}

		ConcatPDF.concat(new ArrayList<>(streams), outputStream);
		return 0L;
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
