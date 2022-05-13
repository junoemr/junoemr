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
package org.oscarehr.common.io.conversion;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.rtf.parser.RtfParser;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.PDFFile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class RtfToPdfFileConverter extends AbstractFileConverter<PDFFile>
{
	@Override
	public PDFFile toFile(InputStream inputStream) throws Exception
	{
		Document document = new Document();

		//TODO new conversion, this library doesn't convert rtf correctly and rtf support is abandoned
		GenericFile tempFile = FileFactory.createTempFile("-rtf.pdf");
		PdfWriter writer = PdfWriter.getInstance(document, tempFile.asFileOutputStream());
		// open the document for modifications

		document.open();
		// create a new parser to load the RTF file
		RtfParser parser = new RtfParser(null);
		// read the rtf file into a compatible document
		parser.convertRtfDocument(inputStream, document);

		// save the pdf to disk
		document.close();

		return (PDFFile) tempFile;
	}
}
