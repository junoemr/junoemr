/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package oscar.oscarLab.ca.all.pageUtil.OLIS;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static oscar.util.ConversionUtils.DATE_TIME_ZONE_DISPLAY_PATTERN;

public class OLISPrintFooter implements IEventHandler
{
	protected static final int fontSizeNormal = 8;
	protected static final int fontSizeSmall = 6;
	protected static final int margin = 38;

	protected PdfFormXObject placeholder;
	protected PdfFont normalFont;
	protected String currentUser;
	protected float side = 20;
	protected float x = 300;
	protected float y = 25;
	protected float space = 4f;
	protected float descent = 3;

	public OLISPrintFooter(String currentUserName) throws IOException
	{
		placeholder = new PdfFormXObject(new Rectangle(0, 0, side, side));
		normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		currentUser = currentUserName;
	}

	@Override
	public void handleEvent(Event event)
	{
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfDocument pdf = docEvent.getDocument();


		PdfPage page = docEvent.getPage();
		Rectangle pageSize = page.getPageSize();
		int pageNumber = pdf.getPageNumber(page);

		PdfCanvas pdfCanvas = new PdfCanvas(page);
		Canvas canvas = new Canvas(new PdfCanvas(page), pageSize);
		canvas.setFontSize(fontSizeNormal);
		canvas.setFont(normalFont);

		// Write text at position
		Paragraph paragraph = new Paragraph()
				.add("Page ")
				.add(String.valueOf(pageNumber))
				.add(" of");

		canvas.showTextAligned(paragraph, x, y, TextAlignment.RIGHT);

		canvas.setFontSize(fontSizeSmall);
		canvas.showTextAligned("CONFIDENTIAL - report contains Personal Health Information", pageSize.getLeft() + margin, y, TextAlignment.LEFT);
		canvas.showTextAligned("Created by " + currentUser + " at " +
						ConversionUtils.toDateTimeString(ZonedDateTime.now(), DateTimeFormatter.ofPattern(DATE_TIME_ZONE_DISPLAY_PATTERN)),
				pageSize.getRight() - margin, y, TextAlignment.RIGHT);
		canvas.close();

		// Create placeholder object to write number of pages
		pdfCanvas.addXObjectAt(placeholder, x + space, y - descent);
		pdfCanvas.release();

		if(pageNumber >= pdf.getNumberOfPages())
		{
			writeTotal(pdf);
		}
	}

	private void writeTotal(PdfDocument pdf)
	{
		Canvas canvas = new Canvas(placeholder, pdf);
		canvas.setFontSize(fontSizeNormal);
		canvas.setFont(normalFont);
		canvas.showTextAligned(String.valueOf(pdf.getNumberOfPages()), 0, descent, TextAlignment.LEFT);
		canvas.close();
	}
}
