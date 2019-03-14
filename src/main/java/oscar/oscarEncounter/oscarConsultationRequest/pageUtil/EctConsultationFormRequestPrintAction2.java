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
import org.oscarehr.common.exception.HtmlToPdfConversionException;
import org.oscarehr.consultations.service.ConsultationPDFCreationService;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.UtilDateUtilities;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * Convert submitted preventions into pdf and return file
 */
public class EctConsultationFormRequestPrintAction2 extends Action
{

	private static final Logger logger = MiscUtils.getLogger();
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	private ConsultationPDFCreationService consultationPDFCreationService = SpringUtils.getBean(ConsultationPDFCreationService.class);
    
    public EctConsultationFormRequestPrintAction2()
    {
    }
    
    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response)
    {
    	LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);

	    String demographicNoStr = request.getParameter("demographicNo");
	    Integer demographicNo = Integer.parseInt(demographicNoStr);
	    String requestIdStr = (String) request.getAttribute("reqId");
	    if(request.getParameter("reqId") != null)
	    {
		    requestIdStr = request.getParameter("reqId");
	    }
	    Integer requestId = Integer.parseInt(requestIdStr);

	    securityInfoManager.requireOnePrivilege(loggedInInfo.getLoggedInProviderNo(), SecurityInfoManager.READ, demographicNo, "_con");

		String error = "";
		Exception exception = null;
		try
		{
			ByteOutputStream bos = consultationPDFCreationService.getRequestOutputStream(request, loggedInInfo, demographicNo, requestId);

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
			exception = ce;
		}
		catch(DocumentException de)
		{
			error = "DocumentException";
			exception = de;
		}
		catch(IOException ioe)
		{
			error = "IOException";
			exception = ioe;
		}
	    if(!error.isEmpty())
	    {
		    logger.error(error + " occurred inside ConsultationPrintAction", exception);
		    request.setAttribute("printError", true);
		    return mapping.findForward("error");
	    }
	    return null;
    }
}
