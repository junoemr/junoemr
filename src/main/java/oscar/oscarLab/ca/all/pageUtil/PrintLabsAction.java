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


/*
 * PrintLabsAction.java
 *
 * Created on November 27, 2007, 9:42 AM
 *
 */

package oscar.oscarLab.ca.all.pageUtil;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.oscarLab.ca.all.parsers.OLIS.OLISHL7Handler;

/**
 *
 * @author wrighd
 */
public class PrintLabsAction extends Action{
    
    Logger logger = Logger.getLogger(PrintLabsAction.class);
    private final SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
    
    /** Creates a new instance of PrintLabsAction */
    public PrintLabsAction() {
    }

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	{
        String providerNo = LoggedInInfo.getLoggedInInfoFromSession(request).getLoggedInProviderNo();
        securityInfoManager.requireOnePrivilege(providerNo, SecurityInfoManager.READ, null, "_lab");

        try
        {
            String segmentId = request.getParameter("segmentID");
            MessageHandler handler = Factory.getHandler(segmentId);
			String filename = GenericFile.getSanitizedFileName(handler.getPatientName());

            if("CELLPATHR".equals(handler.getHeaders().get(0)))
            {//if it is a VIHA RTF lab
                response.setContentType("text/rtf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "_LabReport.rtf\"");
                LabPDFCreator pdf = new LabPDFCreator(request, response.getOutputStream());
                pdf.printRtf();
            }
            else if(handler instanceof OLISHL7Handler)
            {
				String labRequestUrl = request.getParameter("labRequestUrl") + "?segmentID=" + segmentId;

				response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "_OLISLabReport.pdf\"");
				htmlToPdf(labRequestUrl, request, response.getOutputStream());
            }
            else
            {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "_LabReport.pdf\"");
                LabPDFCreator pdf = new LabPDFCreator(request, response.getOutputStream());
                pdf.printPdf();
            }
		}
		catch (DocumentException de)
		{
			logger.error("DocumentException occurred inside PrintLabsAction", de);
			request.setAttribute("printError", true);
			return mapping.findForward("error");
		}
		catch (IOException ioe)
		{
			logger.error("IOException occurred inside PrintLabsAction", ioe);
			request.setAttribute("printError", true);
			return mapping.findForward("error");
		}
		catch (Exception e)
		{
			logger.error("Unknown Exception occurred inside PrintLabsAction", e);
			request.setAttribute("printError", true);
			return mapping.findForward("error");
		}
        return null;
    }

	private void htmlToPdf(String urlStr, HttpServletRequest request, OutputStream outputStream)
		throws IOException
	{
		String baseUri = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		logger.info("Convert to pdf: " + urlStr);
		logger.info("base URI: " + baseUri);

		URL url = new URL(urlStr);
		URLConnection connection = url.openConnection();
		connection.addRequestProperty("user", LoggedInInfo.getLoggedInInfoFromSession(request.getSession()).getLoggedInProviderNo());

		Cookie[] cookies = request.getCookies();
		if(cookies != null)
		{
			connection.addRequestProperty("Cookie",
				Arrays.stream(cookies)
					.map((cookie) -> cookie.getName() + "=" + cookie.getValue())
					.collect(Collectors.joining("; ")));
		}

		InputStream inputStream = connection.getInputStream();

		ConverterProperties props = new ConverterProperties();
		props.setMediaDeviceDescription(new MediaDeviceDescription(MediaType.PRINT));
		props.setBaseUri(baseUri);

		HtmlConverter.convertToPdf(connection.getInputStream(), outputStream, props);
		inputStream.close();

		logger.info("PDF conversion complete");
	}
    
}
