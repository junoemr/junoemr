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
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tika.io.IOUtils;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.consultations.service.ConsultationPDFCreationService;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.fax.exception.FaxException;
import org.oscarehr.fax.model.FaxOutbound;
import org.oscarehr.fax.service.OutgoingFaxService;
import org.oscarehr.fax.util.PdfCoverPageCreator;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.ws.rest.transfer.fax.FaxOutboxTransferOutbound;
import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

import javax.naming.SizeLimitExceededException;
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
	private static final ConsultationPDFCreationService consultationPDFCreationService = SpringUtils.getBean(ConsultationPDFCreationService.class);
	private static final ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);

	public EctConsultationFormFaxAction()
	{
	}

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), Permission.CONSULTATION_READ);

		String reqId = request.getParameter("reqId");
		String demoNo = request.getParameter("demographicNo");
		String faxNumber = request.getParameter("letterheadFax");
		String consultResponsePage = request.getParameter("consultResponsePage");
		boolean doCoverPage = request.getParameter("coverpage").equalsIgnoreCase("true");

		Integer demographicNo = Integer.parseInt(demoNo);
		Integer requestId = Integer.parseInt(reqId);
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		// Retrieving fax recipients.
		String[] tmpRecipients = request.getParameterValues("faxRecipients");

		List<InputStream> streamList = new ArrayList<>();
		List<String> errorList = new ArrayList<>();
		File tempfile = null;

		long excessBytes;

		try
		{
			// ensure valid fax number formatting. Throw exception if invalid
			HashSet<String> recipients = OutgoingFaxService.preProcessFaxNumbers(tmpRecipients);

			List<EDoc> attachedDocuments;
			List<LabResultData> attachedLabs;
			List<EFormData> attachedEForms;

			if (doCoverPage)
			{
				String note = request.getParameter("note") == null ? "" : request.getParameter("note");

				PdfCoverPageCreator pdfCoverPageCreator = new PdfCoverPageCreator(note);
				byte[] buffer = pdfCoverPageCreator.createCoverPage();
				streamList.add(new ByteInputStream(buffer, buffer.length));
			}

			if(consultResponsePage != null) // consult response
			{
				CommonLabResultData consultLabs = new CommonLabResultData();
				attachedDocuments = EDocUtil.listResponseDocs(loggedInInfo, demoNo, reqId, EDocUtil.ATTACHED);
				attachedLabs = consultLabs.populateLabResultsDataConsultResponse(loggedInInfo, demoNo, reqId, CommonLabResultData.ATTACHED);
				attachedEForms = new ArrayList<>(0); //TODO-legacy populate eform attachments

				String consultResponsePDF = ConsultResponsePDFCreator.create(consultResponsePage);
				GenericFile tempFile = FileFactory.getExistingFile(consultResponsePDF);
				streamList.add(tempFile.toFileInputStream());
			}
			else // consult request
			{
				attachedDocuments = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demographicNo, requestId);
				attachedLabs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demographicNo, requestId);
				attachedEForms = consultationAttachmentService.getAttachedEForms(demographicNo, requestId);

				streamList.add(consultationPDFCreationService.getConsultationRequestAsStream(request, loggedInInfo));
			}
			streamList.addAll(consultationPDFCreationService.toEDocInputStreams(request, attachedDocuments));
			streamList.addAll(consultationPDFCreationService.toLabInputStreams(attachedLabs));
			streamList.addAll(consultationPDFCreationService.toEFormInputStreams(request, attachedEForms));

			if(!streamList.isEmpty())
			{
				// Writing consultation request to disk as a pdf.
				tempfile = File.createTempFile("Consult_" + reqId + "-", ".pdf");
				String faxPdf = tempfile.getPath();
				FileOutputStream fos = null;

				try
				{
					fos = new FileOutputStream(faxPdf);
					excessBytes = consultationPDFCreationService.combineStreams(streamList, fos);
					if (excessBytes > 0)
					{
						throw new SizeLimitExceededException();
					}
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
					GenericFile fileToFax = FileFactory.copy(fileToCopy);
					String tempName = String.format("CRF-%s%s.%s.%s", faxClinicId, reqId, faxNo, fileToFax.getName());

					fileToFax.rename(tempName);
					FaxOutboxTransferOutbound transfer = outgoingFaxService.queueAndSendFax(providerNo, Integer.parseInt(demoNo), faxNo, FaxOutbound.FileType.CONSULTATION, fileToFax);
					if(transfer.getSystemStatus().equals(FaxOutbound.Status.ERROR))
					{
						errorList.add("Failed to send fax. Check account settings. " +
									"Reason: " + transfer.getSystemStatusMessage());
					}
					else if(transfer.getSystemStatus().equals(FaxOutbound.Status.QUEUED))
					{
						errorList.add("Failed to send fax, it has been queued for automatic resend. " +
									"Reason: " + transfer.getSystemStatusMessage());
					}
				}
				request.setAttribute("faxSuccessful", true);
			}
			else
			{
				throw new DocumentException("No faxable objects");
			}
		}
		catch(FaxException e)
		{
			logger.error("Error occurred inside ConsultationPrintAction", e);
			errorList.add(e.getUserFriendlyMessage(request.getLocale()));
		}
		catch(SizeLimitExceededException e)
		{
			// Error is known, don't need to log the error again. Just display something nice to the user
			errorList.add("The attached files included with this consultation request are too large. Please remove some of the attached files and try again.");
		}
		catch(Exception e)
		{
			logger.error("Error occurred inside ConsultationPrintAction", e);
			errorList.add("System Error");
		}
		finally
		{
			// Cleaning up InputStreams created for concatenation.
			for (InputStream is : streamList)
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
