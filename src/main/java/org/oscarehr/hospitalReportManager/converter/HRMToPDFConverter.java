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
package org.oscarehr.hospitalReportManager.converter;

import org.apache.log4j.Logger;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.PDFFile;
import org.oscarehr.common.io.conversion.Base64GifToPdfFileConverter;
import org.oscarehr.common.io.conversion.Base64JpegToPdfFileConverter;
import org.oscarehr.common.io.conversion.Base64PngToPdfFileConverter;
import org.oscarehr.common.io.conversion.HtmlToPdfFileConverter;
import org.oscarehr.common.io.conversion.RtfToPdfFileConverter;
import org.oscarehr.common.io.conversion.TextToPdfFileConverter;
import org.oscarehr.common.io.conversion.TiffToPdfFileConverter;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class HRMToPDFConverter extends AbstractModelConverter<HRMReport, PDFFile>
{
	protected static final Logger logger = MiscUtils.getLogger();

	@Autowired
	protected Base64JpegToPdfFileConverter base64JpegToPdfFileConverter;

	@Autowired
	protected Base64PngToPdfFileConverter base64PngToPdfFileConverter;

	@Autowired
	protected Base64GifToPdfFileConverter base64GifToPdfFileConverter;

	@Autowired
	protected HtmlToPdfFileConverter htmlToPdfFileConverter;

	@Autowired
	protected RtfToPdfFileConverter rtfToPdfFileConverter;

	@Autowired
	protected TextToPdfFileConverter textToPdfFileConverter;

	@Autowired
	protected TiffToPdfFileConverter tiffToPdfFileConverter;

	@Override
	public PDFFile convert(HRMReport report)
	{
		PDFFile pdfFile;

		if(report.isBinary())
		{
			String fileExtension = report.getFileExtension().toLowerCase();
			fileExtension = fileExtension.replaceAll("\\.", "");
			InputStream contentStream = new ByteArrayInputStream(report.getBinaryContent());

			switch(fileExtension)
			{
				case "pdf":
				{
					try
					{
						pdfFile = (PDFFile) FileFactory.createTempFile(contentStream, ".pdf");
					}
					catch(IOException | InterruptedException e)
					{
						throw new RuntimeException("HRM embedded pdf error", e);
					}
					break;
				}
				case "html":
				{
					pdfFile = htmlToPdfFileConverter.convert(contentStream);
					break;
				}
				case "rtf":
				{
					pdfFile = rtfToPdfFileConverter.convert(contentStream);
					break;
				}
				case "tif":
				case "tiff":
				{
					pdfFile = tiffToPdfFileConverter.convert(contentStream);
					break;
				}
				case "gif":
				{
					String imageData = report.getBinaryContentBase64().orElseThrow(() -> new RuntimeException("Missing HRM embedded gif content"));
					pdfFile = base64GifToPdfFileConverter.convert(imageData);
					break;
				}
				case "jpg":
				case "jpeg":
				{
					String imageData = report.getBinaryContentBase64().orElseThrow(() -> new RuntimeException("Missing HRM embedded jpeg content"));
					pdfFile = base64JpegToPdfFileConverter.convert(imageData);
					break;
				}
				case "png":
				{
					String imageData = report.getBinaryContentBase64().orElseThrow(() -> new RuntimeException("Missing HRM embedded png content"));
					pdfFile = base64PngToPdfFileConverter.convert(imageData);
					break;
				}
				default:
				{
					throw new IllegalArgumentException("Unsupported hrm file extension: " + fileExtension);
				}
			}
		}
		else
		{
			pdfFile = textToPdfFileConverter.convert(report.getTextContent());
		}
		return pdfFile;
	}
}
