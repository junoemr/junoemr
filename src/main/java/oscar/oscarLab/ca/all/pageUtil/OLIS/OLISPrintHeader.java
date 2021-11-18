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
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.property.TextAlignment;
import lombok.SneakyThrows;

public class OLISPrintHeader implements IEventHandler
{
	private static final int margin = 38;
	private static final int fontSizeNormal = 8;
	private static final int line1YPos = fontSizeNormal + 10;
	private static final int line2YPos = fontSizeNormal + 20;
	private static final int TEXT_WRAP_THRESHOLD = 50;

	private final String header;
	private final String subHeader;
	private final String patientName;
	private final String patientData;

	public OLISPrintHeader(String header, String subHeader, String patientDisplayName, String patientDisplayData)
	{
		this.header = header;
		this.subHeader = subHeader;
		this.patientName = patientDisplayName;
		this.patientData = patientDisplayData;
	}

	@SneakyThrows
	@Override
	public void handleEvent(Event event)
	{
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;

		PdfPage page = docEvent.getPage();
		Rectangle pageSize = page.getPageSize();

		PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
		PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);


		Canvas canvas = new Canvas(new PdfCanvas(page), pageSize);
		canvas.setFontSize(fontSizeNormal);
		canvas.setFont(normalFont);

		// Write header info
		canvas.showTextAligned(header, pageSize.getLeft() + margin, pageSize.getTop() - line1YPos, TextAlignment.LEFT);

		canvas.setFont(boldFont);
		canvas.showTextAligned(subHeader, pageSize.getLeft() + margin, pageSize.getTop() - line2YPos, TextAlignment.LEFT);
		canvas.close();

		// write patient info
		canvas.setFont(normalFont);
		String combined = patientName + " | " + patientData;
		if(combined.length() <= TEXT_WRAP_THRESHOLD)
		{
			canvas.showTextAligned(combined, pageSize.getRight() - margin, pageSize.getTop() - line1YPos, TextAlignment.RIGHT);
		}
		else
		{
			canvas.showTextAligned(patientName, pageSize.getRight() - margin, pageSize.getTop() - line1YPos, TextAlignment.RIGHT);
			canvas.showTextAligned(patientData, pageSize.getRight() - margin, pageSize.getTop() - line2YPos, TextAlignment.RIGHT);
		}
	}
}
