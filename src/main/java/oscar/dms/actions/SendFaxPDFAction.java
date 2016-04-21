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

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
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
public class SendFaxPDFAction extends Action {

    public ActionForward execute(ActionMapping mapping, ActionForm form, 
		HttpServletRequest request, HttpServletResponse response) 
	{
		if(!OscarProperties.getInstance().isPropertyActive("document_fax_enabled")) 
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
						sendFax(docNo, path, faxPdf, faxNo);
					}
					catch (DocumentException de) 
					{
						error = "DocumentException";
						message = de.getMessage();
						exception = de;
					} 
					catch (FileNotFoundException fnfe) 
					{
						error = "FileNotFoundException";
						message = "Cannot find file to fax (" + filename + ")";
						exception = fnfe;
					} 
					catch (IOException ioe) 
					{
						error = "IOException";
						message = "File error (" + filename + ")";
						exception = ioe;
					} 
					catch(Exception e)
					{
						error = "Exception";
						message = "A system error occurred";
						exception = e;
					}

					if (!error.equals("")) 
					{
						MiscUtils.getLogger().error(
							error + " occured insided SendFaxPDFAction", exception);
						errorList.add(message);
						request.setAttribute("printError", new Boolean(true));
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
        }
		
		request.setAttribute("errors", errorList);

        return mapping.findForward("success");
    }

	private void sendFax(String docNo, String path, String faxPdf, String faxNo)
		throws DocumentException, IOException, FileNotFoundException
	{
		if (faxNo.length() < 7) 
		{ 
			throw new DocumentException(
					"Document target fax number '"+faxNo+"' is invalid."); 
		}

		String tempPath = OscarProperties.getInstance().getProperty("fax_file_location");
		String tempName = "DOC-" + docNo + "-" + faxNo + "." + System.currentTimeMillis();

		String tempPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
		String tempTxt = String.format("%s%s%s.txt", tempPath, File.separator, tempName);

		MiscUtils.getLogger().info("======================================================");
		MiscUtils.getLogger().info(faxPdf);
		MiscUtils.getLogger().info(tempPdf);
		MiscUtils.getLogger().info(tempTxt);
		MiscUtils.getLogger().info("======================================================");

		// Copying the fax pdf.
		FileUtils.copyFile(new File(faxPdf), new File(tempPdf));

		// Creating text file with the specialists fax number.
		FileOutputStream fos = new FileOutputStream(tempTxt);				
		PrintWriter pw = new PrintWriter(fos);
		pw.println(faxNo);
		pw.close();
		fos.close();

		// A little sanity check to ensure both files exist.
		if (!new File(tempPdf).exists() || !new File(tempTxt).exists()) 
		{
			throw new DocumentException(
				"Unable to create files for fax of consultation request " + docNo + ".");
		}
	}

    /** Creates a new instance of CombinePDFAction */
    public SendFaxPDFAction() 
	{
    }

}


