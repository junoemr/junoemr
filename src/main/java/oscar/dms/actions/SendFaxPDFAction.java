/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package oscar.dms.actions;

import com.lowagie.text.DocumentException;
import org.apache.commons.io.FilenameUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.fax.exception.FaxException;
import org.oscarehr.fax.model.FaxFileType;
import org.oscarehr.fax.model.FaxStatusInternal;
import org.oscarehr.fax.service.FaxUploadService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.fax.transfer.FaxOutboxTransferOutbound;
import oscar.OscarProperties;
import oscar.dms.EDocUtil;
import oscar.oscarEncounter.data.EctProgram;
import oscar.util.FaxUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


/**
 *
 * @author jay
 */
public class SendFaxPDFAction extends DispatchAction {

	private static final FaxUploadService faxUploadService = SpringUtils.getBean(FaxUploadService.class);

	public ActionForward faxDocument(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		if (!faxUploadService.isOutboundFaxEnabled())
		{
			return mapping.findForward("failed");
		}

		String demoNo = request.getParameter("demoId");
		String providerNo = request.getParameter("providerId");

		MiscUtils.getLogger().info("Send fax document: demo=" +demoNo+ ", provider=" + providerNo);

		Integer demographicId = Integer.parseInt(demoNo);

		String[] docNoArray = request.getParameterValues("docNo");
		String[] recipients = request.getParameterValues("faxRecipients");
	
		request.setAttribute("docNo", docNoArray);
		request.setAttribute("faxRecipients", recipients);

		String ContentDisposition=request.getParameter("ContentDisposition");
		ArrayList<Object> errorList = new ArrayList<Object>();
		try
		{
			Set<String> faxNoList = FaxUploadService.preProcessFaxNumbers(recipients);
			if(docNoArray != null)
			{
				MiscUtils.getLogger().debug("size = " + docNoArray.length);
				EDocUtil docData = new EDocUtil();
				for(String docNo : docNoArray)
				{
					String filename = docData.getDocumentName(docNo);
					// filename already has .pdf suffix
					File tempFile = File.createTempFile("DOC-" + docNo + "-", "");
					for(String faxNo : faxNoList)
					{
						FaxOutboxTransferOutbound transfer;
						try
						{
							GenericFile fileToCopy = FileFactory.getDocumentFile(docData.getDocumentName(docNo));
							GenericFile fileToFax = FileFactory.copy(fileToCopy);

							String faxFileName = FilenameUtils.removeExtension(tempFile.getName()) + "-" + faxNo;
							fileToFax.rename(faxFileName + fileToFax.getName());

							transfer = faxUploadService
								.queueAndSendFax(providerNo, demographicId, faxNo, FaxFileType.DOCUMENT, fileToFax);
							if(transfer.getSystemStatus().equals(FaxStatusInternal.ERROR))
							{
								errorList.add("Failed to send fax. Check account settings. " +
										"Reason: " + transfer.getSystemStatusMessage());
							}
							else if(transfer.getSystemStatus().equals(FaxStatusInternal.QUEUED))
							{
								errorList.add("Failed to send fax, it has been queued for automatic resend. " +
										"Reason: " + transfer.getSystemStatusMessage());
							}
						}
						catch(Exception e)
						{
							MiscUtils.getLogger().error(e.getClass().getCanonicalName() +
									" occurred while preparing document fax files.", e);
							String errorAt = " (Document: " + filename + " Recipient: " + faxNo + ")";
							errorList.add(getUserFriendlyError(e) + errorAt);
							continue;
						}

						/* -- OHSUPPORT-2932 -- */
						if(OscarProperties.getInstance().isPropertyActive(
								"encounter_notes_add_fax_notes_consult") && !demoNo.equals("-1"))
						{
							MiscUtils.getLogger().info("SAVING NOTE FOR " + demoNo);
							String programNo =
									new EctProgram(request.getSession()).getProgram(providerNo);
							FaxUtils.addFaxDocumentEncounterNote(demoNo, providerNo,
									programNo, faxNo, Long.valueOf(docNo));
						}
					}
					tempFile.delete();
				}
			}
		}
		catch(FaxException e)
		{
			errorList.add(e.getUserFriendlyMessage());
		}

		if (errorList.size() != 0)
		{
			request.setAttribute("printError", true);
		}
		
		request.setAttribute("errors", errorList);
		return mapping.findForward("success");
	}

	public ActionForward faxForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response)
			throws IOException
	{
		if (!faxUploadService.isOutboundFaxEnabled())
		{
			return mapping.findForward("failed");
		}
		String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();

		String[] recipients = request.getParameterValues("faxRecipients");
		String pdfPath = (String) request.getAttribute("pdfPath");
		String formName = (String) request.getAttribute("formName");
		String demographicNoStr = (String) request.getAttribute("demographicNo");
		Integer demographicNo = Integer.parseInt(demographicNoStr);

		ArrayList<Object> errorList = new ArrayList<>();
		File tempFile = File.createTempFile("Form-" + formName + "-", ".pdf");
		try
		{
			Set<String> faxNoList = FaxUploadService.preProcessFaxNumbers(recipients);
			for(String faxNo : faxNoList)
			{
				FaxOutboxTransferOutbound transfer;
				try
				{
					GenericFile fileToFax = FileFactory.getExistingFile(pdfPath);
					fileToFax.rename(GenericFile.getFormattedFileName(faxNo + "-" + tempFile.getName()));
					transfer = faxUploadService
						.queueAndSendFax(providerNo, demographicNo, faxNo, FaxFileType.FORM, fileToFax);
					if(transfer.getSystemStatus().equals(FaxStatusInternal.ERROR))
					{
						errorList.add("Failed to send fax. Check account settings. " +
								"Reason: " + transfer.getSystemStatusMessage());
					}
					else if(transfer.getSystemStatus().equals(FaxStatusInternal.QUEUED))
					{
						errorList.add("Failed to send fax, it has been queued for automatic resend. " +
								"Reason: " + transfer.getSystemStatusMessage());
					}
				}
				catch(Exception e)
				{
					MiscUtils.getLogger().error(e.getClass().getCanonicalName() +
							" occurred while preparing form fax files. ", e);
					String errorAt = " (Form: " + formName + " Recipient: " + faxNo + ")";
					errorList.add(getUserFriendlyError(e) + errorAt);
				}
			}
		}
		catch(FaxException e)
		{
			errorList.add(e.getUserFriendlyMessage());
		}
		finally
		{
			tempFile.delete();
		}

		request.setAttribute("errors", errorList);
		return mapping.findForward("success");
	}

	private String getUserFriendlyError(Exception e)
	{
		if (e instanceof DocumentException)
		{
			return e.getMessage();
		}
		if (e instanceof FileNotFoundException)
		{
			return "Cannot find file to fax.";
		}
		if (e instanceof IOException)
		{
			return "File error.";
		}

		return "A system error occurred.";
	}

    /** Creates a new instance of CombinePDFAction */
    public SendFaxPDFAction() 
	{
    }

}