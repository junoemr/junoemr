/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import com.itextpdf.text.pdf.PdfReader;
import com.lowagie.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tika.io.IOUtils;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.service.OutgoingFaxService;
import org.oscarehr.fax.util.PdfCoverPageCreator;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EctConsultationFormFaxAction extends Action
{

	private static final Logger logger = MiscUtils.getLogger();
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static final OutgoingFaxService outgoingFaxService = SpringUtils.getBean(OutgoingFaxService.class);

	public EctConsultationFormFaxAction()
	{
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.READ, null, "_con");

		String reqId = request.getParameter("reqId");
		String demoNo = request.getParameter("demographicNo");
		String faxNumber = request.getParameter("letterheadFax");
		String consultResponsePage = request.getParameter("consultResponsePage");
		boolean doCoverPage = request.getParameter("coverpage").equalsIgnoreCase("true");

		// Retrieving fax recipients.
		String[] tmpRecipients = request.getParameterValues("faxRecipients");

		ArrayList<EDoc> docs;
		if (consultResponsePage == null)
		{
			docs = EDocUtil.listDocs(loggedInInfo, demoNo, reqId, EDocUtil.ATTACHED);
		}
		else
		{
			docs = EDocUtil.listResponseDocs(loggedInInfo, demoNo, reqId, EDocUtil.ATTACHED);
		}

		String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		ArrayList<Object> alist = new ArrayList<Object>();
		byte[] buffer;
		ByteInputStream bis;
		ByteOutputStream bos;
		CommonLabResultData consultLabs = new CommonLabResultData();
		ArrayList<InputStream> streams = new ArrayList<InputStream>();
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		ArrayList<LabResultData> labs;
		if (consultResponsePage == null)
		{
			labs = consultLabs.populateLabResultsData(loggedInInfo, demoNo, reqId, CommonLabResultData.ATTACHED);
		}
		else
		{
			labs = consultLabs.populateLabResultsDataConsultResponse(loggedInInfo, demoNo, reqId, CommonLabResultData.ATTACHED);
		}

		List<String> errorList = new ArrayList<>();
		File tempfile = null;
		try
		{
			// ensure valid fax number formatting. Throw exception if invalid
			HashSet<String> recipients = OutgoingFaxService.preProcessFaxNumbers(tmpRecipients);

			if (doCoverPage)
			{
				String note = request.getParameter("note") == null ? "" : request.getParameter("note");

				PdfCoverPageCreator pdfCoverPageCreator = new PdfCoverPageCreator(note);

				buffer = pdfCoverPageCreator.createCoverPage();
				bis = new ByteInputStream(buffer, buffer.length);
				streams.add(bis);
				alist.add(bis);

			}

			if (consultResponsePage == null)
			{ //fax for consultation request
				bos = new ByteOutputStream();
				ConsultationPDFCreator cpdfc = new ConsultationPDFCreator(request, bos);
				cpdfc.printPdf(loggedInInfo);

				buffer = bos.getBytes();
				bis = new ByteInputStream(buffer, bos.getCount());
				bos.close();
				streams.add(bis);
				alist.add(bis);
			}
			else
			{ //fax for consultation response
				String consultRespoonsePDF = ConsultResponsePDFCreator.create(consultResponsePage);
				alist.add(consultRespoonsePDF);
			}

			for (int i = 0; i < docs.size(); i++)
			{
				EDoc doc = docs.get(i);
				if (doc.isPrintable())
				{
					if (doc.isImage())
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
						alist.add(bis);

					}
					else if (doc.isPDF())
					{
						alist.add(path + doc.getFileName());
					}
					else
					{
						logger.error("EctConsultationFormRequestPrintAction: " + doc.getType() + " is marked as printable but no means have been established to print it.");
					}
				}
			}

			// Iterating over requested labs.
			for (int i = 0; labs != null && i < labs.size(); i++)
			{
				// Storing the lab in PDF format inside a byte stream.
				bos = new ByteOutputStream();
				request.setAttribute("segmentID", labs.get(i).segmentID);
				LabPDFCreator lpdfc = new LabPDFCreator(request, bos);
				lpdfc.printPdf();

				// Transferring PDF to an input stream to be concatenated with
				// the rest of the documents.
				buffer = bos.getBytes();
				bis = new ByteInputStream(buffer, bos.getCount());
				bos.close();
				streams.add(bis);
				alist.add(bis);

			}

			if (alist.size() > 0)
			{
				// Writing consultation request to disk as a pdf.
				tempfile = File.createTempFile("Consult_" + reqId + "-", ".pdf");
				String faxPdf = tempfile.getPath();
				FileOutputStream fos = null;

				try
				{
					fos = new FileOutputStream(faxPdf);
					ConcatPDF.concat(alist, fos);
				}
				finally
				{
					IOUtils.closeQuietly(fos);
				}

				String faxClinicId = OscarProperties.getInstance().getProperty("fax_clinic_id", "");

				PdfReader pdfReader = new PdfReader(faxPdf);
				pdfReader.close();
				GenericFile fileToCopy = FileFactory.getExistingFile(faxPdf);

				for(String faxNo : recipients)
				{
					String tempName = String.format("CRF-%s%s.%s.%d.pdf", faxClinicId, reqId, faxNo, System.currentTimeMillis());

					GenericFile fileToFax = FileFactory.copy(fileToCopy);
					fileToFax.rename(tempName);
					FaxOutboxTransferOutbound transfer = outgoingFaxService.sendFax(providerNo, Integer.parseInt(demoNo), faxNo, FaxOutbound.FileType.CONSULTATION, fileToFax);
					if(transfer.getSystemStatus().equals(FaxOutbound.Status.ERROR.name()))
					{
						errorList.add("Failed to send fax. Check account settings. " +
									"Reason: " + transfer.getSystemStatusMessage());
					}
					else if(transfer.getSystemStatus().equals(FaxOutbound.Status.QUEUED.name()))
					{
						errorList.add("Failed to send fax, it has been queued for automatic resend. " +
									"Reason: " + transfer.getSystemStatusMessage());
					}

				}
				LogAction.addLogEntry(providerNo, Integer.parseInt(demoNo), LogConst.ACTION_SENT, LogConst.CON_FAX, LogConst.STATUS_SUCCESS,
						reqId, loggedInInfo.getIp(), "CONSULT " + reqId);
				request.setAttribute("faxSuccessful", true);
			}
			else
			{
				throw new DocumentException("No faxable objects");
			}
		}
		catch(Exception e)
		{
			logger.error("Error occurred inside ConsultationPrintAction", e);
			errorList.add("System Error");
		}
		finally
		{
			// Cleaning up InputStreams created for concatenation.
			for (InputStream is : streams)
			{
				IOUtils.closeQuietly(is);
			}
			if(tempfile != null)
			{
				logger.info("delete consult fax tempfile: " + tempfile.getPath());
				tempfile.delete();
			}
		}
		if(!errorList.isEmpty())
		{
			request.setAttribute("errorList", errorList);
			request.setAttribute("autoClose", false);
			return mapping.findForward("error");
		}
		request.setAttribute("autoClose", true);
		return mapping.findForward("success");
	}
}
