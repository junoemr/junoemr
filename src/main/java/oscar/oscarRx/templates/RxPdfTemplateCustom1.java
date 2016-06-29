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
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import oscar.OscarProperties;

public class RxPdfTemplateCustom1 extends RxPdfTemplate {

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
		String clinicName;
		String clinicTel;
		String clinicFax;
		// check if satellite clinic is used
		String useSatelliteClinic = req.getParameter("useSC");
		logger.debug(useSatelliteClinic);
		if (useSatelliteClinic != null && useSatelliteClinic.equalsIgnoreCase("true")) {
			String scAddress = req.getParameter("scAddress");
			logger.debug("clinic detail" + "=" + scAddress);
			HashMap<String,String> hm = parseSCAddress(scAddress);
			clinicName =  hm.get("clinicName");
			clinicTel = hm.get("clinicTel");
			clinicFax = hm.get("clinicFax");
		} else {
			// parameters need to be passed to header and footer
			clinicName = req.getParameter("clinicName");
			logger.debug("clinicName" + "=" + clinicName);
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

		String[] cfgGraphicFile = req.getParameterValues("__cfgGraphicFile");
		String[] graphicPage = req.getParameterValues("__graphicPage");

		writer.setPageEvent(new EndPage(clinicName, clinicTel, clinicFax, patientPhone, patientCityPostal,
				patientAddress, patientName, patientDOB, sigDoctorName, rxDate, origPrintDate, numPrint, imgFile,
				patientHIN, patientChartNo, pracNo, locale));

		document.open();
		document.newPage();

		PdfContentByte cb = writer.getDirectContent();
		BaseFont bf; // = normFont;

		cb.setRGBColorStroke(0, 0, 255);
		// render prescriptions
		for (String rxStr : listRx) {
			// bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252,
			// BaseFont.NOT_EMBEDDED);
			bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			Paragraph p = new Paragraph(new Phrase(rxStr, new Font(bf, 10)));
			p.setKeepTogether(true);
			p.setSpacingBefore(5f);
			document.add(p);
		}
		// render additional notes
		if (additNotes != null && !additNotes.equals("")) {
			bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
			Paragraph p = new Paragraph(new Phrase(additNotes, new Font(bf, 10)));
			p.setKeepTogether(true);
			p.setSpacingBefore(10f);
			document.add(p);
		}

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
		return PageSize.A6;
	}
	@Override
	protected Document getDocument() {
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

			} 
			catch (Exception e) {
				logger.error("Error", e);
			}
		}
	}
}
