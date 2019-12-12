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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.rx.service.RxWatermarkService;
import org.oscarehr.util.LocaleUtils;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.oscarehr.web.PrescriptionQrCodeUIBean;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import oscar.OscarProperties;

public class RxPdfTemplatePrescriptionPad extends RxPdfTemplate {

	public RxPdfTemplatePrescriptionPad(final HttpServletRequest req, final ServletContext ctx) {
		super(req, ctx);
	}

	@Override
	protected Rectangle getPageSize(String pageSizeParameter) {
		// A0-A10, LEGAL, LETTER, HALFLETTER, _11x17, LEDGER, NOTE, B0-B5, ARCH_A-ARCH_E, FLSA
		// and FLSE
		// the following shows a temp way to get a print page size
		Rectangle pageSize = PageSize.LETTER;
		if (pageSizeParameter != null) {
			if ("PageSize.HALFLETTER".equals(pageSizeParameter)) {
				pageSize = PageSize.HALFLETTER;
			} else if ("PageSize.A6".equals(pageSizeParameter)) {
				pageSize = PageSize.A6;
			} else if ("PageSize.A4".equals(pageSizeParameter)) {
				pageSize = PageSize.A4;
			}
		}
		return pageSize;
	}
	@Override
	protected Document documentSetup(Document document, PdfWriter writer) {

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
		String patientDOB=req.getParameter("patientDOB");
		String showPatientDOB=req.getParameter("showPatientDOB");
		String imgFile=req.getParameter("imgFile");
		String patientHIN=req.getParameter("patientHIN");
		String patientChartNo = req.getParameter("patientChartNo");
		String pracNo=req.getParameter("pracNo");
		Locale locale = req.getLocale();

		String[] cfgGraphicFile = req.getParameterValues("__cfgGraphicFile");
		String[] graphicPage = req.getParameterValues("__graphicPage");

		boolean isShowDemoDOB=false;
		if(showPatientDOB!=null&&showPatientDOB.equalsIgnoreCase("true")){
			isShowDemoDOB=true;
		}
		if(!isShowDemoDOB)
			patientDOB="";

		String title = req.getParameter("__title") != null ? req.getParameter("__title") : "Unknown";
		document.addTitle(title);
		document.addSubject("");
		document.addKeywords("pdf, itext");
		document.addCreator("OSCAR");
		document.addAuthor("");
		document.addHeader("Expires", "0");

		document.setMargins(15, document.getPageSize().getWidth() - 285f + 5f, 170, 60);

		writer.setPageEvent(new EndPage(clinicName, clinicTel, clinicFax, patientPhone, patientCityPostal,
				patientAddress, patientName, patientDOB, sigDoctorName, rxDate, origPrintDate, numPrint, imgFile,
				patientHIN, patientChartNo, pracNo, locale));

		return document;
	}
	@Override
	protected void buildPdfLayout(Document document, PdfWriter writer) throws DocumentException, IOException {

		String rx = req.getParameter("rx");
		if (rx == null) {
			rx = "";
		}
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(req);
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
		if (PrescriptionQrCodeUIBean.isPrescriptionQrCodeEnabledForProvider(loggedInInfo.getLoggedInProviderNo())) {
			Integer scriptId = Integer.parseInt(req.getParameter("scriptId"));
			byte[] qrCodeImage = PrescriptionQrCodeUIBean.getPrescriptionHl7QrCodeImage(scriptId);
			Image qrCode = Image.getInstance(qrCodeImage);
			document.add(qrCode);
		}
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

			UserPropertyDAO userPropertyDAO = SpringUtils.getBean(UserPropertyDAO.class);
			if (userPropertyDAO.getProp(UserProperty.RX_PROMO_TEXT) == null)
			{
				this.promoText = OscarProperties.getInstance().getProperty("FORMS_PROMOTEXT");
			}
			else
			{
				this.promoText = userPropertyDAO.getProp(UserProperty.RX_PROMO_TEXT).getValue();
			}
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
			Rectangle page = document.getPageSize();
			PdfContentByte cb = writer.getDirectContent();


			try {


				float height = page.getHeight();
				float pageWidth = 285f;
				boolean showPatientDOB=false;
				// get the end of paragraph
				float endPara = writer.getVerticalPosition(true);
				//head.writeSelectedRows(0, 1,document.leftMargin(), page.height() - document.topMargin()+ head.getTotalHeight(),writer.getDirectContent());
				if(this.patientDOB!=null && this.patientDOB.length()>0){
					showPatientDOB=true;
				}

				// render the watermark in the background
				if (RxWatermarkService.isWatermarkEnabled() && RxWatermarkService.isWatermarkBackground())
				{
					try
					{
						Image watermarkImg = Image.getInstance(RxWatermarkService.getWatermark().getFileObject().getAbsolutePath());
						float scale = (pageWidth * 0.8f) / watermarkImg.getWidth();
						float x = pageWidth / 2 - (watermarkImg.getWidth() * scale) / 2;
						float y = (page.getHeight() - (page.getHeight() - (endPara - 80)) / 2) - (watermarkImg.getHeight() * scale) / 2;

						PdfContentByte cbUnder = writer.getDirectContentUnder();
						renderImageToPdf(watermarkImg, x, y, scale, cbUnder);
					}
					catch(FileNotFoundException e)
					{
						MiscUtils.getLogger().error("Could not open RxWatermark when writing prescription");
					}
				}

				//header table for patient's information.
				PdfPTable head = new PdfPTable(1);
				String newline = System.getProperty("line.separator");
				StringBuilder hStr = new StringBuilder(newline);
				hStr = hStr.append(this.patientName);
				if(showPatientDOB){
					hStr.append("   DOB: ").append(this.patientDOB).append(newline);}
				else{
					hStr.append(newline);
				}

				hStr.append(this.patientAddress).append(newline).append(this.patientCityPostal).append(newline).append(this.patientPhone);

				if (patientHIN != null && patientHIN.trim().length() > 0) {
					hStr.append(newline).append("Health Ins #. ").append(patientHIN);
				}

				if (patientChartNo != null && !patientChartNo.isEmpty()) {
					String chartNoTitle = org.oscarehr.util.LocaleUtils.getMessage(locale, "oscar.oscarRx.chartNo");
					hStr.append(newline).append(chartNoTitle).append(patientChartNo);
				}

				BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				Phrase hPhrase = new Phrase(hStr.toString(), new Font(bf, 10));
				head.addCell(hPhrase);
				head.setTotalWidth(272f);
				head.writeSelectedRows(0, -1, 13f, height - 100f, cb);

				String custom_logo_name = OscarProperties.getInstance().getProperty("rx_custom_logo");
				if(custom_logo_name != null ){
					Image img = Image.getInstance(OscarProperties.getInstance().getProperty("eform_image") + custom_logo_name);
					img.scaleToFit(50, 50);
					img.setAbsolutePosition(20, page.getHeight()-50);
					document.add(img);
				}
				else {
					bf = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
					writeDirectContent(cb, bf, 12, PdfContentByte.ALIGN_LEFT, "o s c a r", 21, page.getHeight() - 60, 90);
					// draw R
					writeDirectContent(cb, bf, 50, PdfContentByte.ALIGN_LEFT, "P", 24, page.getHeight() - 53, 0);

					bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
					// draw X
					writeDirectContent(cb, bf, 43, PdfContentByte.ALIGN_LEFT, "X", 38, page.getHeight() - 69, 0);
				}


				bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, this.sigDoctorName, 80, (page.getHeight() - 25), 0);
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, this.rxDate, 188, (page.getHeight() - 112), 0);

				bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				int fontFlags = Font.NORMAL;
				Font font = new Font(bf, 10, fontFlags);
				ColumnText ct = new ColumnText(cb);
				ct.setSimpleColumn(80, (page.getHeight() - 25), 280, (page.getHeight() - 90), 11, Element.ALIGN_LEFT);
				// p("value of clinic name", this.clinicName);
				ct.setText(new Phrase(12, clinicName+(pracNo != null && pracNo.trim().length() >0 ? "\r\n"+geti18nTagValue(locale, "RxPreview.PractNo")+": "+ pracNo : ""), font));
				ct.go();
				// render clnicaTel;
				// bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				//bf = BaseFont.createFont(BaseFont.COURIER, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
				if (this.clinicTel.length() <= 14) {
					//RxPreview.msgTel

					writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgTel")+":" + this.clinicTel, 188, (page.getHeight() - 70), 0);
					// render clinicFax;
					writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgFax")+":" + this.clinicFax, 188, (page.getHeight() - 80), 0);
				} else {
					String str1 = this.clinicTel.substring(0, 14);
					String str2 = this.clinicTel.substring(14);
					writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgTel")+":" + str1, 188, (page.getHeight() - 70), 0);
					writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, str2, 188, (page.getHeight() - 80), 0);
					writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgFax")+":" + this.clinicFax, 188, (page.getHeight() - 88), 0);
				}

				// draw left line
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				// cb.moveTo(13f, 20f);
				cb.moveTo(13f, endPara - 80);
				cb.lineTo(13f, height - 15f);
				cb.stroke();

				// draw right line 285, 20, 285, 405, 0.5
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				// cb.moveTo(285f, 20f);
				cb.moveTo(pageWidth, endPara - 80);
				cb.lineTo(pageWidth, height - 15f);
				cb.stroke();
				// draw top line 10, 405, 285, 405, 0.5
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				cb.moveTo(13f, height - 15f);
				cb.lineTo(pageWidth, height - 15f);
				cb.stroke();

				// draw bottom line 10, 20, 285, 20, 0.5
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				// cb.moveTo(13f, 20f);
				// cb.lineTo(285f, 20f);
				cb.moveTo(13f, endPara - 80);
				cb.lineTo(pageWidth, endPara - 80);
				cb.stroke();
				// Render "Signature:"
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, geti18nTagValue(locale, "RxPreview.msgSignature"), 20f, endPara - 50f, 0);
				// Render line for Signature 75, 55, 280, 55, 0.5
				cb.setRGBColorStrokeF(0f, 0f, 0f);
				cb.setLineWidth(0.5f);
				// cb.moveTo(75f, 50f);
				// cb.lineTo(280f, 50f);
				cb.moveTo(75f, endPara - 50f);
				cb.lineTo(275f, endPara - 50f);
				cb.stroke();

				try
				{
					if (this.imgPath != null && !this.imgPath.isEmpty())
					{
						Image img = Image.getInstance(this.imgPath);
						img.scaleToFit(100, 50);
						img.setAbsolutePosition(75f, endPara - 50f);
						cb.addImage(img);
					}
				}
				catch (IOException ioe)
				{
					MiscUtils.getLogger().error("signature error: " + ioe.getMessage());
				}

				// Render doctor name
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_LEFT, this.sigDoctorName, 90, endPara - 60f, 0);
				// public void writeDirectContent(PdfContentByte cb, BaseFont bf, float fontSize, int alignment, String text, float x, float y, float rotation)
				// render reprint origPrintDate and numPrint
				if (origPrintDate != null && numPrint != null) {
					String rePrintStr = geti18nTagValue(locale, "RxPreview.msgReprintBy")+" " + this.sigDoctorName + "; "+geti18nTagValue(locale, "RxPreview.msgOrigPrinted")+": " + origPrintDate + "; "+geti18nTagValue(locale, "RxPreview.msgTimesPrinted") +": " + numPrint;
					writeDirectContent(cb, bf, 6, PdfContentByte.ALIGN_LEFT, rePrintStr, 50, endPara - 68, 0);
				}
				// print promoText
				writeDirectContent(cb, bf, 6, PdfContentByte.ALIGN_LEFT, this.promoText, 70, endPara - 77, 0);
				// print page number
				String footer = "" + writer.getPageNumber();
				writeDirectContent(cb, bf, 10, PdfContentByte.ALIGN_RIGHT, footer, 280, endPara - 77, 0);

				// render watermark in the foreground
				if (RxWatermarkService.isWatermarkEnabled() && !RxWatermarkService.isWatermarkBackground())
				{
					try
					{
						Image watermarkImg = Image.getInstance(RxWatermarkService.getWatermark().getFileObject().getAbsolutePath());
						float scale = (pageWidth * 0.8f) / watermarkImg.getWidth();
						float x = pageWidth / 2 - (watermarkImg.getWidth() * scale) / 2;
						float y = (page.getHeight() - (page.getHeight() - (endPara - 80)) / 2) - (watermarkImg.getHeight() * scale) / 2;
						renderImageToPdf(watermarkImg, x, y, scale, cb);
					}
					catch(FileNotFoundException e)
					{
						MiscUtils.getLogger().error("Could not open RxWatermark when writing prescription");
					}
				}

			} catch (Exception e) {
				logger.error("Error", e);
			}
		}

		protected void renderImageToPdf(Image img, float x, float y, float scale, PdfContentByte cb) throws DocumentException
		{
			img.scalePercent(scale*100f);
			img.setAbsolutePosition(x, y);
			cb.addImage(img);
		}
	}
}
