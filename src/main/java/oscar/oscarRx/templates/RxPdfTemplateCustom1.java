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
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.oscarehr.rx.service.RxWatermarkService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.web.PrescriptionQrCodeUIBean;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfLayer;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import oscar.OscarProperties;

public class RxPdfTemplateCustom1 extends RxPdfTemplate
{

	Font baseFont;
	Font headerFont;
	Font smallFont;

	public RxPdfTemplateCustom1(final HttpServletRequest req, final ServletContext ctx)
	{
		super(req, ctx);
	}

	@Override
	protected Rectangle getPageSize(String pageSizeParameter)
	{
		return PageSize.LETTER;
	}

	@Override
	protected Document documentSetup(Document document, PdfWriter writer)
	{

		String title = req.getParameter("__title") != null ? req.getParameter("__title") : "Unknown";
		document.addTitle(title);
		document.addSubject("");
		document.addKeywords("pdf, itext");
		document.addCreator("OSCAR");
		document.addAuthor("");
		document.addHeader("Expires", "0");
		return document;
	}

	@Override
	protected void buildPdfLayout(Document document, PdfWriter writer) throws DocumentException, IOException
	{

		headerFont = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		headerFont.setSize(18);

		baseFont = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		baseFont.setSize(12);

		smallFont = FontFactory.getFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
		smallFont.setSize(8);

		createRxPdf(document, writer);
	}

	/**
	 * Add's the table 'add' to the table 'main'.
	 * @param main the host table
	 * @param add the table being added
	 * @param border true if a border should surround the table being added
	 * @return the cell containing the table being added to the main table.	 *
	 */
	protected PdfPCell addToTable(PdfPTable main, PdfPTable add, boolean border)
	{
		PdfPCell cell = new PdfPCell(add);
		if (!border)
		{
			cell.setBorder(0);
		}
		main.addCell(cell);
		return cell;
	}

	protected void createRxPdf(Document document, PdfWriter writer) throws DocumentException, IOException
	{
		PdfLayer pdflayer = new PdfLayer("Main layer", writer);
		PdfLayer watermarkLayer = new PdfLayer("Watermark layer", writer);
		PdfContentByte cb = writer.getDirectContent();

		if (RxWatermarkService.isWatermarkEnabled() && RxWatermarkService.isWatermarkBackground())
		{
			Image image = createWaterMarkImage(document);
			if (image != null)
			{
				watermarkLayer.setOn(true);
				cb.beginLayer(watermarkLayer);
				cb.addImage(image);
				cb.endLayer();
			}
		}

		pdflayer.setOn(true);
		cb.beginLayer(pdflayer);
		PdfPTable mainTable = new PdfPTable(1);
		mainTable.setExtendLastRow(true);
		mainTable.setTotalWidth(document.getPageSize().getWidth());

		addToTable(mainTable, buildClinicHeader(), true);

		addToTable(mainTable, buildPatientInfoHeader(), true);

		addToTable(mainTable, buildPrescriptionBody(), false);

		// Align the footer to the bottom
		PdfPCell cell = new PdfPCell(buildPageFooter());
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setBorder(0);
		mainTable.addCell(cell);

		cb.endLayer();
		document.add(mainTable);

		if (RxWatermarkService.isWatermarkEnabled() && !RxWatermarkService.isWatermarkBackground())
		{
			Image image = createWaterMarkImage(document);
			if (image != null)
			{
				watermarkLayer.setOn(true);
				cb.beginLayer(watermarkLayer);
				cb.addImage(image);
				cb.endLayer();
			}
		}
	}

	protected Image createWaterMarkImage(Document document)
	{
		try
		{

			Image watermarkImg = Image.getInstance(RxWatermarkService.getWatermark().getFileObject().getAbsolutePath());
			float scaleFactor = (document.getPageSize().getWidth() * 0.8f) / watermarkImg.getWidth();
			watermarkImg.scalePercent(scaleFactor * 100f);
			watermarkImg.setAbsolutePosition(document.getPageSize().getWidth() / 2 - (watermarkImg.getWidth() * scaleFactor) / 2,
					document.getPageSize().getHeight() / 2 - (watermarkImg.getHeight() * scaleFactor) / 2);
			return watermarkImg;
		}
		catch (Exception e)
		{
			MiscUtils.getLogger().error("error creating watermark image: " + e.getMessage(), e);
		}
		return null;
	}

	protected Image buildLogoImage()
	{

		Image img = null;
		try
		{
			String custom_logo_name = OscarProperties.getInstance().getProperty("rx_custom_logo");
			if(custom_logo_name != null )
			{
				img = Image.getInstance(OscarProperties.getInstance().getProperty("eform_image") + custom_logo_name);
			}
			else
			{
				URL url = this.ctx.getResource("/oscarRx/img/rx.gif");
				img = Image.getInstance(url);
			}
		}
		catch (Exception e)
		{
			logger.error("Error loading Rx Logo image", e);
		}
		return img;
	}

	protected Image buildSignatureImage()
	{
		String imgFile = req.getParameter("imgFile");
		Image img = null;
		try
		{
			img = Image.getInstance(imgFile);
			img.setBorder(0);
		}
		catch (Exception e)
		{
			logger.error("Error loading Rx Signature image", e);
		}
		return img;
	}

	protected PdfPTable buildDoctorHeader()
	{
		String sigDoctorName = req.getParameter("sigDoctorName");
		String pracNo = req.getParameter("pracNo");
		String clinicName;
		String clinicTel;
		String clinicFax;
		// check if satellite clinic is used
		String useSatelliteClinic = req.getParameter("useSC");
		if (useSatelliteClinic != null && useSatelliteClinic.equalsIgnoreCase("true"))
		{
			String scAddress = req.getParameter("scAddress");
			HashMap<String, String> hm = parseSCAddress(scAddress);
			clinicName = hm.get("clinicName");
			clinicTel = hm.get("clinicTel");
			clinicFax = hm.get("clinicFax");
		}
		else
		{
			clinicName = req.getParameter("clinicName");
			clinicTel = req.getParameter("clinicPhone");
			clinicFax = req.getParameter("clinicFax");
		}

		PdfPTable headerTable = new PdfPTable(1);

		PdfPCell cell = new PdfPCell(new Phrase(sigDoctorName, headerFont));
		cell.setBorder(0);

		cell.setPadding(10f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(0);
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase(clinicName, baseFont));
		cell.setPaddingTop(0);
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("CPSO: " + pracNo, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Tel:  " + clinicTel, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Fax:  " + clinicFax, baseFont));
		cell.setPaddingBottom(10f);
		headerTable.addCell(cell);

		return headerTable;
	}

	protected PdfPTable buildClinicHeader()
	{

		float[] tableWidths = new float[]{ 1.0f, 3.0f };
		PdfPTable headerTable = new PdfPTable(tableWidths);
		headerTable.getDefaultCell().setPadding(5f);

		Image logo = buildLogoImage();
		if (logo != null)
		{
			logo.setAlignment(Element.ALIGN_CENTER);
			headerTable.getDefaultCell().setBorder(Rectangle.RIGHT);
			headerTable.addCell(logo);
		}

		addToTable(headerTable, buildDoctorHeader(), false);

		return headerTable;

	}

	protected PdfPTable buildPatientInfoHeader()
	{

		String patientPhone = req.getParameter("patientPhone");
		patientPhone = patientPhone.replace("Tel", "");
		String patientCityPostal = req.getParameter("patientCityPostal");
		String patientAddress = req.getParameter("patientAddress");
		String patientName = req.getParameter("patientName");
		String patientHIN = req.getParameter("patientHIN");
		String patientChartNo = req.getParameter("patientChartNo");
		String showPatientDOB = req.getParameter("showPatientDOB");
		String patientDOB = req.getParameter("patientDOB");
		String datePrescribed = req.getParameter("rxDate");

		PdfPTable headerTable = new PdfPTable(1);

		PdfPCell cell = new PdfPCell(new Phrase(datePrescribed, baseFont));
		cell.setBorder(0);
		cell.setPadding(10f);
		cell.setPaddingTop(5f);
		cell.setPaddingBottom(0);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		headerTable.addCell(cell);
		cell.setPaddingTop(0);
		cell.setPhrase(new Phrase(patientName, headerFont));
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		headerTable.addCell(cell);

		if (showPatientDOB != null && showPatientDOB.equalsIgnoreCase("true"))
		{
			cell.setPhrase(new Phrase("DOB: " + patientDOB, baseFont));
			headerTable.addCell(cell);
		}

		cell.setPhrase(new Phrase(patientAddress, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase(patientCityPostal, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Tel: " + patientPhone, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("HIN: " + patientHIN, baseFont));
		headerTable.addCell(cell);
		cell.setPhrase(new Phrase("Chart #: " + patientChartNo, baseFont));
		cell.setPaddingBottom(10f);
		headerTable.addCell(cell);

		return headerTable;
	}


	protected PdfPTable buildPrescriptionBody()
	{
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(req);
		String newline = System.getProperty("line.separator");
		String additNotes = req.getParameter("additNotes");
		String rx = req.getParameter("rx");
		if (rx == null)
		{
			rx = "";
		}
		String[] rxA = rx.split(newline);
		List<String> listRx = new ArrayList<String>();
		String listElem = "";
		// parse rx and put into a list of rx;
		for (String s : rxA)
		{
			if (s.equals("") || s.equals(newline) || s.length() == 1)
			{
				listRx.add(listElem);
				listElem = "";
			}
			else
			{
				listElem = listElem + s;
				listElem += newline;
			}
		}

		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(Rectangle.BOTTOM);
		table.getDefaultCell().setPadding(5f);

		// render prescriptions
		for (String rxStr : listRx)
		{
			Paragraph p = new Paragraph(new Phrase(rxStr, baseFont));
			p.setKeepTogether(true);
			p.setSpacingBefore(20f);
			table.addCell(p);
		}
		// render additional notes
		if (additNotes != null && !additNotes.equals(""))
		{
			Paragraph p = new Paragraph(new Phrase(additNotes, baseFont));
			p.setKeepTogether(true);
			p.setSpacingBefore(20f);
			table.addCell(p);
		}
		// render QrCode
		if (PrescriptionQrCodeUIBean.isPrescriptionQrCodeEnabledForProvider(loggedInInfo.getLoggedInProviderNo()))
		{
			Integer scriptId = Integer.parseInt(req.getParameter("scriptId"));
			byte[] qrCodeImage = PrescriptionQrCodeUIBean.getPrescriptionHl7QrCodeImage(scriptId);
			Image qrCode = null;
			try
			{
				qrCode = Image.getInstance(qrCodeImage);
				table.addCell(qrCode);
			}
			catch (Exception e)
			{
				logger.error("Failed to load QR Code image", e);
			}
		}
		return table;
	}

	protected PdfPTable buildPageFooter()
	{
		String method = req.getParameter("__method");
		String origPrintDate = req.getParameter("origPrintDate");
		String numPrint = req.getParameter("numPrints");
		String sigDoctorName = req.getParameter("sigDoctorName");

		float[] tableWidths = new float[]{ 1.0f, 3.0f };
		PdfPTable table = new PdfPTable(1);
		table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		PdfPTable subtable1 = new PdfPTable(tableWidths);
		subtable1.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		PdfPTable subtable2 = new PdfPTable(tableWidths);
		subtable2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		PdfPCell cell = new PdfPCell(new Phrase("Signature:", baseFont));
		cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(0);
		subtable1.addCell(cell);

		Image sig = buildSignatureImage();

		if (sig != null)
		{
			subtable1.addCell(sig);
		}
		else
		{
			cell = new PdfPCell();
			cell.setBorder(0);
			subtable1.addCell(cell);//empty cell
		}

		cell = new PdfPCell();
		cell.setBorder(0);
		subtable2.addCell(cell);//empty cell
		cell.setPhrase(new Phrase(sigDoctorName, baseFont));
		cell.setBorder(Rectangle.TOP);
		subtable2.addCell(cell);

		addToTable(table, subtable1, false);
		addToTable(table, subtable2, false);

		cell.setBorder(0);
		if (method != null && method.equalsIgnoreCase("rePrint"))
		{
			String printsLine = "Original print date: " + origPrintDate + ". printed " + numPrint + " times";

			cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			cell.setPhrase(new Phrase(printsLine, smallFont));
			table.addCell(cell);
		}
		cell.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell.setPadding(10f);
		cell.setPhrase(new Phrase("Created by: OSCAR The open-source EMR www.oscarcanada.org", baseFont));
		table.addCell(cell);

		return table;
	}
}