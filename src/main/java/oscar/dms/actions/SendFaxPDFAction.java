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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;
import oscar.dms.EDocUtil;
import oscar.util.FaxUtils;
import oscar.oscarEncounter.data.EctProgram;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.apache.commons.io.FileUtils;

import com.lowagie.text.DocumentException;
import java.io.FileNotFoundException;
import java.io.IOException;



/**
 *
 * @author jay
 */
public class SendFaxPDFAction extends DispatchAction {

    public ActionForward faxDocument(ActionMapping mapping, ActionForm form,
		HttpServletRequest request, HttpServletResponse response) 
	{
		if (!OscarProperties.getInstance().isDocumentFaxEnabled())
		{
			return mapping.findForward("failed");
		}

		String demoNo = request.getParameter("demoId");
		String providerNo = request.getParameter("providerId");

		MiscUtils.getLogger().info("demo and provider ====================================");
		MiscUtils.getLogger().info(demoNo);
		MiscUtils.getLogger().info(providerNo);
		MiscUtils.getLogger().info("======================================================");


        String[] docNoArray = request.getParameterValues("docNo");
		String[] recipients = request.getParameterValues("faxRecipients");
	
		request.setAttribute("docNo", docNoArray);
		request.setAttribute("faxRecipients", recipients);

        String ContentDisposition=request.getParameter("ContentDisposition");
        ArrayList<Object> errorList = new ArrayList<Object>();
        if (docNoArray != null)
		{
			MiscUtils.getLogger().debug("size = " + docNoArray.length);
			EDocUtil docData = new EDocUtil();
            for (int i =0 ; i < docNoArray.length ; i++)
			{
				String docNo = docNoArray[i];
				String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
				String filename =  docData.getDocumentName(docNo);
				String faxPdf = path + filename;

				for (int j = 0; j < recipients.length; j++)
				{
					String faxNo = recipients[j].replaceAll("\\D", "");
					String error = "";
					String message = "";
					Exception exception = null;
					try
					{
						sendFax("DOC-" + docNo, faxPdf, faxNo);
					}
					catch(Exception e)
					{
						MiscUtils.getLogger().error(e.getClass().getCanonicalName() +
								" occurred while preparing document fax files.");
						String errorAt = " (Document: " + filename + " Recipient: " + recipients[j] + ")";
						errorList.add(getUserFriendlyError(e) + errorAt);
						continue;
					}

					/* -- OHSUPPORT-2932 -- */
					if(OscarProperties.getInstance().isPropertyActive(
						"encounter_notes_add_fax_notes_consult") && demoNo != "-1")
					{
						MiscUtils.getLogger().info("SAVING NOTE FOR " + demoNo);
						String programNo = 
							new EctProgram(request.getSession()).getProgram(providerNo);
						FaxUtils.addFaxDocumentEncounterNote(demoNo, providerNo, 
							programNo, faxNo, Long.valueOf(docNo));
					}
				}
			}
			if (errorList.size() != 0)
			{
				request.setAttribute("printError", true);
			}
        }
		
		request.setAttribute("errors", errorList);
        return mapping.findForward("success");
    }

	public ActionForward faxForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response)
	{
		if (!OscarProperties.getInstance().isFormFaxEnabled())
		{
			return mapping.findForward("failed");
		}

		String[] recipients = request.getParameterValues("faxRecipients");
		String pdfPath = (String) request.getAttribute("pdfPath");
		String formName = (String) request.getAttribute("formName");

		ArrayList<Object> errorList = new ArrayList<Object>();
		for (int i = 0; i < recipients.length; i++)
		{
			try
			{
				faxTempFile("Form-" + formName, pdfPath, recipients[i]);
			}
			catch (Exception e)
			{
				MiscUtils.getLogger().error(e.getClass().getCanonicalName() +
						" occurred while preparing form fax files. ", e);
				String errorAt = " (Form: " + formName + " Recipient: " + recipients[i] + ")";
				errorList.add(getUserFriendlyError(e) + errorAt);
			}
		}

		request.setAttribute("errors", errorList);
		return mapping.findForward("success");
	}

	private void faxTempFile(String fileName, String tmpPdf, String faxNo)
		throws DocumentException, IOException
	{
		sendFax(fileName, tmpPdf, faxNo);

		// clear temp files on JVM exit
		new File(tmpPdf).deleteOnExit();
	}


	private void sendFax(String fileName, String pdf, String faxNo)
		throws DocumentException, IOException
	{
		String tempPath = OscarProperties.getInstance().getProperty("fax_file_location");
		String tempName = fileName + "-" + faxNo + "." + System.currentTimeMillis();

		String faxPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
		String faxTxt = String.format("%s%s%s.txt", tempPath, File.separator, tempName);

		MiscUtils.getLogger().info("======================================================");
		MiscUtils.getLogger().info(pdf);
		MiscUtils.getLogger().info(faxPdf);
		MiscUtils.getLogger().info(faxTxt);
		MiscUtils.getLogger().info("======================================================");

		// Copying the fax pdf.
		FileUtils.copyFile(new File(pdf), new File(faxPdf));

		// Creating text file with the specialists fax number.
		FileOutputStream fos = new FileOutputStream(faxTxt);
		PrintWriter pw = new PrintWriter(fos);
		pw.println(faxNo);
		pw.close();
		fos.close();

		// A little sanity check to ensure both files exist.
		if (!new File(faxPdf).exists() || !new File(faxTxt).exists())
		{
			throw new DocumentException(
				"Unable to create files for fax of " + fileName + ".");
		}
	}

	private String getUserFriendlyError(Exception e)
	{
		if (e instanceof DocumentException)
		{
			return e.getMessage();
		}
		if (e instanceof  FileNotFoundException)
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
