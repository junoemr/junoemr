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

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import com.itextpdf.text.pdf.codec.TiffImage;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.PDFFile;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class TiffToPdfFileConverter extends AbstractFileConverter<InputStream, PDFFile>
{
	@Override
	public PDFFile toFile(InputStream inputStream) throws Exception
	{
		GenericFile tempTiffFile = FileFactory.createTempFile(inputStream, ".tiff");

		//TODO better way to hande this without writing tiff file to count pages
		RandomAccessFileOrArray myTiffFile = new RandomAccessFileOrArray(tempTiffFile.getPath());
		// Find number of images in Tiff file
		int numberOfPages = TiffImage.getNumberOfPages(myTiffFile);
		Document document = new Document();

		GenericFile tempFile = FileFactory.createTempFile("-tiff.pdf");
		PdfWriter pdfWriter = PdfWriter.getInstance(document, tempFile.asFileOutputStream());

		pdfWriter.setStrictImageSequence(true);
		document.open();

		// Run a for loop to extract images from Tiff file
		// into a Image object and add to PDF recursively
		for(int i = 1; i <= numberOfPages; i++)
		{
			Image tempImage = TiffImage.getTiffImage(myTiffFile, i);
			Rectangle pageSize = new Rectangle(tempImage.getWidth(), tempImage.getHeight());
			document.setPageSize(pageSize);
			document.newPage();
			document.add(tempImage);
		}
		document.close();
		tempTiffFile.deleteFile();

		return (PDFFile) tempFile;
	}
}
