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

package oscar.oscarRx.templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.oscarehr.util.LocaleUtils;
import org.oscarehr.web.PrescriptionQrCodeUIBean;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import oscar.OscarProperties;

public class RxPdfTemplateCustom1 extends RxPdfTemplate {
	
	Font baseFont;
	Font headerFont;
	Font smallFont;

	public RxPdfTemplateCustom1(final HttpServletRequest req, final ServletContext ctx) {
		super(req, ctx);
	}

	@Override
	protected void buildPdfLayout(Document document, PdfWriter writer) throws DocumentException, IOException {
		
		String method = req.getParameter("__method");
		String origPrintDate = null;
		String numPrint = null;
		if (method != null && method.equalsIgnoreCase("rePrint")) {
			origPrintDate = req.getParameter("origPrintDate");
			numPrint = req.getParameter("numPrints");
		}
		// parameters need to be passed to header and footer
		String clinicName;
		String clinicTel;
		String clinicFax;
		// check if satellite clinic is used
		String useSatelliteClinic = req.getParameter("useSC");
		if (useSatelliteClinic != null && useSatelliteClinic.equalsIgnoreCase("true")) {
			String scAddress = req.getParameter("scAddress");
			HashMap<String,String> hm = parseSCAddress(scAddress);
			clinicName =  hm.get("clinicName");
			clinicTel = hm.get("clinicTel");
			clinicFax = hm.get("clinicFax");
		} 
		else {
			clinicName = req.getParameter("clinicName");
			clinicTel = req.getParameter("clinicPhone");
			clinicFax = req.getParameter("clinicFax");
		}
		String patientPhone = req.getParameter("patientPhone");
		String patientCityPostal = req.getParameter("patientCityPostal");
		String patientAddress = req.getParameter("patientAddress");
		String patientName = req.getParameter("patientName");
		String sigDoctorName = req.getParameter("sigDoctorName");
		String rxDate = req.getParameter("rxDate");
		String rx = req.getParameter("rx");
        String patientDOB=req.getParameter("patientDOB");
        String showPatientDOB=req.getParameter("showPatientDOB");
        String imgFile=req.getParameter("imgFile");
        String patientHIN=req.getParameter("patientHIN");
        String patientChartNo = req.getParameter("patientChartNo");
        String pracNo=req.getParameter("pracNo");
        Locale locale = req.getLocale();
        
        boolean isShowDemoDOB=false;
        if(showPatientDOB!=null&&showPatientDOB.equalsIgnoreCase("true")){
            isShowDemoDOB=true;
        }
        if(!isShowDemoDOB)
            patientDOB="";
		if (rx == null) {
			rx = "";
		}
		String newline = System.getProperty("line.separator");
		String additNotes = req.getParameter("additNotes");
		String[] rxA = rx.split(newline);
		List<String> listRx = new ArrayList<String>();
		String listElem = "";
		// parse rx and put into a list of rx;
		for (String s : rxA) {
			if (s.equals("") || s.equals(newline) || s.length() == 1) {
				listRx.add(listElem);
				listElem = "";
			} 
			else {
				listElem = listElem + s;
				listElem += newline;
			}
		}

		document.open();
		document.newPage();
		
		createRxPdf(document, writer);

		PdfContentByte cb = writer.getDirectContent();
		
		Rectangle pageSize = document.getPageSize();
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		
		cb.setRGBColorStroke(0, 0, 255);
		// render prescriptions
		/*for (String rxStr : listRx) {
			Paragraph p = new Paragraph(new Phrase(rxStr, new Font(bf, 10)));
			p.setKeepTogether(true);
			p.setSpacingBefore(5f);
			document.add(p);
		}*/
		// render additional notes
		/*if (additNotes != null && !additNotes.equals("")) {
			Paragraph p = new Paragraph(new Phrase(additNotes, new Font(bf, 10)));
			p.setKeepTogether(true);
			p.setSpacingBefore(10f);
			document.add(p);
		}*/

		// render QrCode
		if (PrescriptionQrCodeUIBean.isPrescriptionQrCodeEnabledForCurrentProvider()) {
			Integer scriptId = Integer.parseInt(req.getParameter("scriptId"));
			byte[] qrCodeImage = PrescriptionQrCodeUIBean.getPrescriptionHl7QrCodeImage(scriptId);
			Image qrCode = Image.getInstance(qrCodeImage);
			document.add(qrCode);
		}
	}
	@Override
	protected Rectangle getPageSize(String pageSizeParameter) {
		return PageSize.LETTER;
	}
	@Override
	protected Document documentSetup() {
		Document document = new Document();
		
		String title = req.getParameter("__title") != null ? req.getParameter("__title") : "Unknown";
		document.addTitle(title);
		document.addSubject("");
		document.addKeywords("pdf, itext");
		document.addCreator("OSCAR");
		document.addAuthor("");
		document.addHeader("Expires", "0");
		return document;
	}
	
	/**
	 * Add's the table 'add' to the table 'main' (with no border surrounding it.)
	 * @param main the host table
	 * @param add the table being added
	 * @return the cell containing the table being added to the main table.
	 */
	protected PdfPCell addTable(PdfPTable main, PdfPTable add) {
		return addToTable(main, add, false);
	}

	/**
	 * Add's the table 'add' to the table 'main'.
	 * @param main the host table
	 * @param add the table being added
	 * @param border true if a border should surround the table being added
	 * @return the cell containing the table being added to the main table.	 *
	 */
	protected PdfPCell addToTable(PdfPTable main, PdfPTable add, boolean border) {
		PdfPCell cell = new PdfPCell(add);
		if (!border) { cell.setBorder(0); }
		//cell.setPadding(3);
		//cell.setColspan(1);
		main.addCell(cell);
		return cell;
	}
	
	protected void createRxPdf(Document document, PdfWriter writer) throws DocumentException {
		
		headerFont = FontFactory.getFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		headerFont.setSize(24);
		
		baseFont = FontFactory.getFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		baseFont.setSize(12);
		
		smallFont = FontFactory.getFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		smallFont.setSize(10);
		
		PdfPTable mainTable = new PdfPTable(1);
		document.setMargins(15, document.getPageSize().getWidth() - 285f + 5f, 170, 60);
		//mainTable.setTotalWidth(document.getPageSize().getWidth());
		//mainTable.setLockedWidth(true);
		
		addToTable(mainTable, buildClinicHeader(), true);
		
		addToTable(mainTable, buildPatientInfoHeader(), true);
		
		addToTable(mainTable, buildPrescriptionBody(), true);
		
		addToTable(mainTable, buildPageFooter(), true);
		
		document.add(mainTable);
	}
	
	protected Image buildLogoImage() {
		
		Image img = null;
		try {
			String custom_logo_name = OscarProperties.getInstance().getProperty("rx_custom_logo");
			if(custom_logo_name != null ){
				img = Image.getInstance(OscarProperties.getInstance().getProperty("eform_image") + custom_logo_name);
			}
			else {
				img = Image.getInstance(System.getProperty( "catalina.base" ) + "/webapps" + req.getContextPath() + "/oscarRx/img/rx.gif");
			}
			//img.scaleToFit(100, 100);
			img.setBorder(0);
		}
		catch(Exception e) {
			logger.error("Error loading Rx Logo image", e);
		}
		return img;
	}
	
	protected Image buildSignatureImage() {
		String imgFile=req.getParameter("imgFile");		
		Image img = null;
		try {
			img = Image.getInstance(imgFile);
			img.setBorder(0);
		}
		catch(Exception e) {
			logger.error("Error loading Rx Logo image", e);
		}
		return img;
	}
	
	protected PdfPTable buildDoctorHeader() {
		String sigDoctorName = req.getParameter("sigDoctorName");
		String pracNo=req.getParameter("pracNo");
		String clinicName;
		String clinicTel;
		String clinicFax;
		// check if satellite clinic is used
		String useSatelliteClinic = req.getParameter("useSC");
		if (useSatelliteClinic != null && useSatelliteClinic.equalsIgnoreCase("true")) {
			String scAddress = req.getParameter("scAddress");
			HashMap<String,String> hm = parseSCAddress(scAddress);
			clinicName =  hm.get("clinicName");
			clinicTel = hm.get("clinicTel");
			clinicFax = hm.get("clinicFax");
		} 
		else {
			clinicName = req.getParameter("clinicName");
			clinicTel = req.getParameter("clinicPhone");
			clinicFax = req.getParameter("clinicFax");
		}
		
		
		PdfPTable headerTable = new PdfPTable(1);
		
		PdfPCell cell = new PdfPCell(new Phrase(sigDoctorName, headerFont));
		cell.setBorder(0);
				
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase(clinicName, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("CPSO: " + pracNo, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Tel:  " + clinicTel, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Fax:  " + clinicFax, baseFont));
		headerTable.addCell(cell);
		
		return headerTable;
	}
	protected PdfPTable buildClinicHeader() {
		
		float[] tableWidths = new float[]{ 1.0f, 3.0f };
		PdfPTable headerTable = new PdfPTable(tableWidths);
		
		Image logo = buildLogoImage();
		if(logo != null) {
			headerTable.addCell(logo);
		}
		
		addTable(headerTable, buildDoctorHeader());
		
		return headerTable;
		
	}
	
	protected PdfPTable buildPatientInfoHeader() {
		
		String patientPhone = req.getParameter("patientPhone");
		patientPhone = patientPhone.replace("Tel", "");
		String patientCityPostal = req.getParameter("patientCityPostal");
		String patientAddress = req.getParameter("patientAddress");
		String patientName = req.getParameter("patientName");
        String patientHIN=req.getParameter("patientHIN");
        String patientChartNo = req.getParameter("patientChartNo");

		
		
		
		PdfPTable headerTable = new PdfPTable(1);
		
		PdfPCell cell = new PdfPCell(new Phrase(patientName, headerFont));
		cell.setBorder(0);
		
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase(patientAddress, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase(patientCityPostal, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Tel: " + patientPhone, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Health #: " + patientHIN, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Chart #: " + patientChartNo, baseFont));
		headerTable.addCell(cell);
		
		return headerTable;
	}
	
	
	protected PdfPTable buildPrescriptionBody() {
		String newline = System.getProperty("line.separator");
		String additNotes = req.getParameter("additNotes");
		String rx = req.getParameter("rx");
		if (rx == null) {
			rx = "";
		}
		String[] rxA = rx.split(newline);
		List<String> listRx = new ArrayList<String>();
		String listElem = "";
		// parse rx and put into a list of rx;
		for (String s : rxA) {
			if (s.equals("") || s.equals(newline) || s.length() == 1) {
				listRx.add(listElem);
				listElem = "";
			} 
			else {
				listElem = listElem + s;
				listElem += newline;
			}
		}
		
		PdfPTable table = new PdfPTable(1);
		
		// render prescriptions
		for (String rxStr : listRx) {
			Paragraph p = new Paragraph(new Phrase(rxStr, baseFont));
			p.setKeepTogether(true);
			p.setSpacingBefore(5f);
			table.addCell(p);
		}
		// render additional notes
		if (additNotes != null && !additNotes.equals("")) {
			Paragraph p = new Paragraph(new Phrase(additNotes, baseFont));
			p.setKeepTogether(true);
			p.setSpacingBefore(10f);
			table.addCell(p);
		}
		
		return table;
	}
	
	protected PdfPTable buildPageFooter() {
		String method = req.getParameter("__method");
		String origPrintDate = req.getParameter("origPrintDate");
		String numPrint = req.getParameter("numPrints");
		String sigDoctorName = req.getParameter("sigDoctorName");
		
		
		PdfPTable table = new PdfPTable(1);
		Image sig = buildSignatureImage();
		
		if(sig != null) {
			table.addCell(sig);
		}
		table.addCell(new Phrase(sigDoctorName, baseFont));
		
		if (method != null && method.equalsIgnoreCase("rePrint")) {
			String printsLine = "Original print date: " + origPrintDate + ". printed " + numPrint + " times";
			
			table.addCell(new Phrase(printsLine, smallFont));
		}
		
		
		return table;
	}
	
	

	
	/**
	 * the form txt file has lines in the form: For Checkboxes: ie. ohip : left, 76, 193, 0, BaseFont.ZAPFDINGBATS, 8, \u2713 requestParamName : alignment, Xcoord, Ycoord, 0, font, fontSize, textToPrint[if empty, prints the value of the request param]
	 * NOTE: the Xcoord and Ycoord refer to the bottom-left corner of the element For single-line text: ie. patientCity : left, 242, 261, 0, BaseFont.HELVETICA, 12 See checkbox explanation For multi-line text (textarea) ie. aci : left, 20, 308, 0,
	 * BaseFont.HELVETICA, 8, _, 238, 222, 10 requestParamName : alignment, bottomLeftXcoord, bottomLeftYcoord, 0, font, fontSize, _, topRightXcoord, topRightYcoord, spacingBtwnLines NOTE: When working on these forms in linux, it helps to load the PDF file
	 * into gimp, switch to pt. coordinate system and use the mouse to find the coordinates. Prepare to be bored!
	 */

	private class EndPage extends PdfPageEventHelper {

		private String clinicName;
		private String clinicTel;
		private String clinicFax;
		private String patientPhone;
		private String patientCityPostal;
		private String patientAddress;
		private String patientName;
        private String patientDOB;
        private String patientHIN;
        private String patientChartNo;
        private String pracNo;
		private String sigDoctorName;
		private String rxDate;
		private String promoText;
		private String origPrintDate = null;
		private String numPrint = null;
		private String imgPath;
                Locale locale = null;
                
		public EndPage() {
		}

        public EndPage(String clinicName, String clinicTel, String clinicFax, String patientPhone, String patientCityPostal, String patientAddress,
                String patientName,String patientDOB, String sigDoctorName, String rxDate,String origPrintDate,String numPrint, String imgPath, String patientHIN, String patientChartNo,String pracNo, Locale locale) {
			this.clinicName = clinicName;
			this.clinicTel = clinicTel;
			this.clinicFax = clinicFax;
			this.patientPhone = patientPhone;
			this.patientCityPostal = patientCityPostal;
			this.patientAddress = patientAddress;
			this.patientName = patientName;
            this.patientDOB=patientDOB;
			this.sigDoctorName = sigDoctorName;
			this.rxDate = rxDate;
			this.promoText = OscarProperties.getInstance().getProperty("FORMS_PROMOTEXT");
			this.origPrintDate = origPrintDate;
			this.numPrint = numPrint;
			if (promoText == null) {
				promoText = "";
			}
			this.imgPath = imgPath;
			this.patientHIN = patientHIN;
                        this.patientChartNo = patientChartNo;
			this.pracNo = pracNo;     
                        this.locale = locale;
		}

		@Override
        public void onEndPage(PdfWriter writer, Document document) {
			renderPage(writer, document);
		}

		public void writeDirectContent(PdfContentByte cb, BaseFont bf, float fontSize, int alignment, String text, float x, float y, float rotation) {
			cb.beginText();
			cb.setFontAndSize(bf, fontSize);
			cb.showTextAligned(alignment, text, x, y, rotation);
			cb.endText();
		}
		public String geti18nTagValue(Locale locale, String tag) {
			return LocaleUtils.getMessage(locale, tag);
		}

		public void renderPage(PdfWriter writer, Document document) {
			Rectangle pageSize = document.getPageSize();
			PdfContentByte cb = writer.getDirectContent();

			try {
				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				// get the end of paragraph
				float endPara = writer.getVerticalPosition(true);
				// Render "Signature:"
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgSignature"), 20f, endPara - 30f, 0);
				// Render line for Signature 75, 55, 280, 55, 0.5
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				// cb.moveTo(75f, 50f);
				// cb.lineTo(280f, 50f);
				cb.moveTo(75f, endPara - 30f);
				cb.lineTo(280f, endPara - 30f);
				cb.stroke();

				if (this.imgPath != null) {
					Image img = Image.getInstance(this.imgPath);
					// image, image_width, 0, 0, image_height, x, y
					//         131, 55, 375, 75, 0
					cb.addImage(img, 207, 0, 0, 20, 75f, endPara-30f);
				}

				// Render doctor name
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, this.sigDoctorName, 90, endPara - 40f, 0);
			} 
			catch (Exception e) {
				logger.error("Error", e);
			}
		}
	}
}
