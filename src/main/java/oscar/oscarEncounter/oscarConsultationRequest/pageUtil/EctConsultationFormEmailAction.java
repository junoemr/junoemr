/**
 * Copyright (c) 2005-2012. OscarHost Inc. All Rights Reserved.
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
 * OscarHost, a Division of Cloud Practice Inc.
 */

package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.io.PrintWriter;
import java.util.ArrayList;
import org.oscarehr.util.EmailUtils;
//import java.util.Arrays;
//import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.commons.mail.EmailException;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.oscarLab.ca.all.pageUtil.LabPDFCreator;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.ConcatPDF;

import com.lowagie.text.DocumentException;
import com.sun.xml.messaging.saaj.util.ByteInputStream;
import com.sun.xml.messaging.saaj.util.ByteOutputStream;

public class EctConsultationFormEmailAction extends Action {

	private static final Logger logger = MiscUtils.getLogger();
	
	public EctConsultationFormEmailAction() {
	}
    @Override
    public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response){
    	MiscUtils.getLogger().debug("inside consultationformemailaction...");
    	String reqId = (String) request.getAttribute("reqId");
		String demoNo = request.getParameter("demographicNo");
		ArrayList<EDoc> docs = EDocUtil.listDocs(demoNo, reqId, EDocUtil.ATTACHED);
		String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
		ArrayList<Object> alist = new ArrayList<Object>();
		byte[] buffer;
		ByteInputStream bis;
		ByteOutputStream bos;
		CommonLabResultData consultLabs = new CommonLabResultData();
		ArrayList<InputStream> streams = new ArrayList<InputStream>();

		ArrayList<LabResultData> labs = consultLabs.populateLabResultsData(demoNo, reqId, CommonLabResultData.ATTACHED);
		String error = "";
		Exception exception = null;
		try {
			MiscUtils.getLogger().debug("1");
			bos = new ByteOutputStream();
			ConsultationPDFCreator cpdfc = new ConsultationPDFCreator(request, bos);
			cpdfc.printPdf();
			
			buffer = bos.getBytes();
			bis = new ByteInputStream(buffer, bos.getCount());
			bos.close();
			streams.add(bis);
			alist.add(bis);
			MiscUtils.getLogger().debug("2");

			for (int i = 0; i < docs.size(); i++) {
				MiscUtils.getLogger().debug("3");
				EDoc doc = docs.get(i);  
				if (doc.isPrintable()) {
					if (doc.isImage()) {
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
					else if (doc.isPDF()) {
						alist.add(path + doc.getFileName());
					}
					else {
						logger.error("EctConsultationFormRequestPrintAction: " + doc.getType() + " is marked as printable but no means have been established to print it.");	
					}
				}
			}
			MiscUtils.getLogger().debug("4");
			// Iterating over requested labs.
			for (int i = 0; labs != null && i < labs.size(); i++) {
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
			MiscUtils.getLogger().debug("5");
			MiscUtils.getLogger().debug("6 six:"+alist.size());
			if (alist.size() > 0) {
			/*	
				String referralFax = request.getParameter("fax");
				
				// Retrieving additional fax recipients.
				String[] tmpRecipients = request.getParameterValues("faxRecipients");
				
				// Removing all non digit characters from fax numbers.
				for (int i = 0; tmpRecipients != null && i < tmpRecipients.length; i++) { 
					tmpRecipients[i] = tmpRecipients[i].trim().replaceAll("\\D", "");
				}
				ArrayList<String> recipients = tmpRecipients == null ? new ArrayList<String>() : new ArrayList<String>(Arrays.asList(tmpRecipients));
				
				// Including consultant fax number if appropriate.
				if (referralFax != null && !referralFax.equals("")) {
					recipients.add(referralFax.trim().replaceAll("\\D", ""));					
				}
				
				// Removing duplicate phone numbers.
				recipients = new ArrayList<String>(new HashSet<String>(recipients));
			*/	
				// Writing consultation request to disk as a pdf.
				String tempPath = OscarProperties.getInstance().getProperty("email_file_location");
				String emailPdf = String.format("%s%sConsult-%s.pdf", tempPath, File.separator, reqId + System.currentTimeMillis());
				FileOutputStream fos = new FileOutputStream(emailPdf);				
				ConcatPDF.concat(alist, fos);				
				fos.close();
				
				MiscUtils.getLogger().debug("7:"+emailPdf);
				
				//for (int i = 0; i < recipients.size(); i++) {					
				    //String faxNo = recipients.get(i).replaceAll("\\D", "");
				    //if (faxNo.length() < 7) { throw new DocumentException("Document target fax number '"+faxNo+"' is invalid."); }
					//String tempName = "CRF-" + reqId + "." + System.currentTimeMillis();
					
					//String tempPdf = String.format("%s%s%s.pdf", tempPath, File.separator, tempName);
					//String tempTxt = String.format("%s%s%s.txt", tempPath, File.separator, tempName);
					
					// Copying the fax pdf.
					//FileUtils.copyFile(new File(emailPdf), new File(tempPdf));
					
					// Creating text file with the specialists fax number.
					//fos = new FileOutputStream(tempTxt);				
					//PrintWriter pw = new PrintWriter(fos);
					//pw.println(faxNo);
					//pw.close();
					//fos.close();
					
					// A little sanity check to ensure both files exist.
				//if (!new File(tempPdf).exists() || !new File(tempTxt).exists()) {
				//	throw new DocumentException("Unable to create files for fax of consultation request " + reqId + ".");
				//}		
				//}
				// Removing the consultation PDF.
				//new File(emailPdf).delete();
    			String recipient = (String) request.getAttribute("recipient");
				String toEmailAddress = null;
				String toName = null;
				if(recipient.equals("provider")){
					toEmailAddress = request.getParameter("toProviderEmail");
					toName = request.getParameter("toProviderName");
				}else if(recipient.equals("patient")){
					toEmailAddress = request.getParameter("toPatientEmail");
					toName = request.getParameter("toPatientName");
				}
				String emailSubject=OscarProperties.getInstance().getProperty("eform_email_subject");
				String fromEmailAddress = OscarProperties.getInstance().getProperty("eform_email_from_address");
				
				MiscUtils.getLogger().debug("8:"+toEmailAddress);
				MiscUtils.getLogger().debug("9:"+toName);
				MiscUtils.getLogger().debug("10:"+fromEmailAddress);
				MiscUtils.getLogger().debug("11:"+emailSubject);

				emailPdf(emailPdf, emailSubject, toEmailAddress, toName, fromEmailAddress);
				request.setAttribute("emailSuccessful", true);
				return mapping.findForward("success");
			}
		} catch (EmailException ee){
			error = "EmailException";
			exception = ee;
		} catch (DocumentException de) {
			error = "DocumentException";
			exception = de;
		} catch (IOException ioe) {
			error = "IOException";
			exception = ioe;
		} finally { 
			// Cleaning up InputStreams created for concatenation.
			for (InputStream is : streams) {
				try {
					is.close();
				} catch (IOException e) {
					error = "IOException";
				}
			}
		}
		if (!error.equals("")) {
			logger.error(error + " occured insided ConsultationPrintAction", exception);
			request.setAttribute("printError", new Boolean(true));
			return mapping.findForward("error");
		}
    	return null;
    }  
	
	private void emailPdf(String pdfPath, String emailSubject, String toEmailAddress, String toName, String fromEmailAddress) throws EmailException{
		logger.debug("Sending email to "+toEmailAddress + " from " + fromEmailAddress);
		EmailUtils.sendEmailWithAttachment(toEmailAddress, toName, fromEmailAddress, null, emailSubject, null, null, pdfPath);
	}
}
