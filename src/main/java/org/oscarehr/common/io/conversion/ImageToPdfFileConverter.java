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

import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.io.PDFFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import static org.oscarehr.common.io.conversion.HtmlToPdfFileConverter.HTML_WRAPPER_TEMPLATE;

@Component
public abstract class ImageToPdfFileConverter extends AbstractModelConverter<String, PDFFile>
{
	@Autowired
	protected HtmlToPdfFileConverter htmlToPdfFileConverter;

	protected PDFFile convert(String base64Content, String mimeType)
	{
		String htmlContent = MessageFormat.format("<img src=\"data:{0};base64, {1}\"></img>", mimeType, base64Content);

		byte[] textContentBytes = MessageFormat.format(HTML_WRAPPER_TEMPLATE, htmlContent).getBytes(StandardCharsets.UTF_8);
		return htmlToPdfFileConverter.convert(new ByteArrayInputStream(textContentBytes));
	}
}
