/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

/*
 * EctConsultationFormRequestPrintAction.java
 *
 * Created on November 19, 2007, 4:05 PM
 */

package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;


import com.lowagie.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.tika.io.IOUtils;
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.consultations.service.ConsultationAttachmentService;
import org.oscarehr.consultations.service.ConsultationPDFCreationService;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.eform.model.EFormData;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.security.model.Permission;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.xml.sax.SAXException;
import oscar.dms.EDoc;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.UtilDateUtilities;

import javax.naming.SizeLimitExceededException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Convert submitted preventions into pdf and return file
 */
public class EctConsultationFormRequestPrintAction extends Action
{
	private static final Logger logger = MiscUtils.getLogger();
	private static final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private static final ConsultationPDFCreationService consultationPDFCreationService = SpringUtils.getBean(ConsultationPDFCreationService.class);
	private static final ConsultationAttachmentService consultationAttachmentService = SpringUtils.getBean(ConsultationAttachmentService.class);
    
    public EctConsultationFormRequestPrintAction()
    {
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)
    {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);

	    String demographicNoStr = request.getParameter("demographicNo");
	    Integer demographicNo = Integer.parseInt(demographicNoStr);
	    String requestIdStr = (String) request.getAttribute("reqId");
	    if(request.getParameter("reqId") != null)
	    {
		    requestIdStr = request.getParameter("reqId");
	    }
	    Integer requestId = Integer.parseInt(requestIdStr);

	    securityInfoManager.requireAllPrivilege(loggedInInfo.getLoggedInProviderNo(), demographicNo, Permission.CONSULTATION_READ);

		String error = "";
		String friendlyReturnMessage = "";
	    List<InputStream> streamList = new ArrayList<>();

	    long excessBytes = 0L;

	    try
		{
			List<EDoc> attachedDocuments = consultationAttachmentService.getAttachedDocuments(loggedInInfo, demographicNo, requestId);
			List<LabResultData> attachedLabs = consultationAttachmentService.getAttachedLabs(loggedInInfo, demographicNo, requestId);
			List<EFormData> attachedEForms = consultationAttachmentService.getAttachedEForms(demographicNo, requestId);
			List<HrmDocument> attachedHRM = consultationAttachmentService.getAttachedHRMList(demographicNo, requestId);

			streamList.add(consultationPDFCreationService.getConsultationRequestAsStream(request, loggedInInfo));
			streamList.addAll(consultationPDFCreationService.toEDocInputStreams(request, attachedDocuments));
			streamList.addAll(consultationPDFCreationService.toLabInputStreams(attachedLabs));
			streamList.addAll(consultationPDFCreationService.toEFormInputStreams(request, attachedEForms));
			streamList.addAll(consultationPDFCreationService.toHRMInputStreams(request, attachedHRM));

			ByteOutputStream bos = new ByteOutputStream();
			excessBytes = consultationPDFCreationService.combineStreams(streamList, bos);
			if (excessBytes > 0)
			{
				throw new SizeLimitExceededException();
			}

			response.setContentType("application/pdf"); // octet-stream
			response.setHeader(
					"Content-Disposition",
					"inline; filename=\"combinedPDF-"
							+ UtilDateUtilities.getToday("yyyy-mm-dd.hh.mm.ss")
							+ ".pdf\"");
			response.getOutputStream().write(bos.getBytes(), 0, bos.getCount());
		}
		catch(HtmlToPdfConversionException ce)
		{
			error = "HtmlToPdfConversionException";
			friendlyReturnMessage = "Error when attempting to convert HTML to PDF, one or more of your eForms may be formatted incorrectly.";
		}
		catch(DocumentException de)
		{
			error = "DocumentException";
			friendlyReturnMessage = "Error when attempting to read one or more of the documents.";
		}
		catch(SAXException | JAXBException | IOException ioe)
		{
			error = "IOException";
			friendlyReturnMessage = "Error attempting to access an internal file.";
		}
	    catch (SizeLimitExceededException slee)
		{
			error = "SizeLimitExceededException";
			friendlyReturnMessage = "The attached files included with this consultation request are too large. Please remove some of the attached files and try again.";
		}
		finally
		{
			// Cleaning up InputStreams created for concatenation.
			for (InputStream is : streamList)
			{
				IOUtils.closeQuietly(is);
			}
		}
	    if(!error.isEmpty())
	    {
		    logger.error(error + " occurred inside ConsultationPrintAction");
		    request.setAttribute("printError", true);
		    request.setAttribute("errorMessage", friendlyReturnMessage);
		    request.setAttribute("demo", demographicNoStr);
		    return mapping.findForward("error");
	    }
	    return null;
    }
}
