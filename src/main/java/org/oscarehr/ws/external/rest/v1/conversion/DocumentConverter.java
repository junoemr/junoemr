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

package org.oscarehr.ws.external.rest.v1.conversion;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.document.model.Document;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferBasic;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferInbound;
import org.oscarehr.ws.external.rest.v1.transfer.document.DocumentTransferOutbound;
import oscar.util.ConversionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class DocumentConverter
{
	public static Document getBasicAsDomainObject(DocumentTransferBasic transfer)
	{
		Document document = new Document();

		document.setDocumentNo(transfer.getDocumentNo());

		document.setStatus(transfer.getStatus().charAt(0));
		document.setPublic1(transfer.getPublicDocument());
		document.setObservationdate(ConversionUtils.toNullableLegacyDate(transfer.getObservationDate()));
		document.setContentdatetime(ConversionUtils.toNullableLegacyDateTime(transfer.getCreatedDateTime()));
//		document.setUpdatedatetime(ConversionUtils.toNullableLegacyDateTime(transfer.getUpdateDateTime()));

		document.setDocClass(transfer.getDocumentClass());
		document.setDocSubClass(transfer.getDocumentSubClass());
		document.setDocdesc(transfer.getDocumentDescription());
		document.setDoctype(transfer.getDocumentType());
		document.setDocxml(transfer.getDocumentXml());
		document.setAppointmentNo(transfer.getAppointmentNo());
		document.setSource(transfer.getSource());
		document.setSourceFacility(transfer.getSourceFacility());

		document.setDocCreator(transfer.getDocumentCreator());
		document.setReviewer(transfer.getReviewer());
		document.setResponsible(transfer.getResponsible());
		document.setReviewdatetime(ConversionUtils.toNullableLegacyDateTime(transfer.getReviewDateTime()));

		return document;
	}
	public static Document getInboundAsDomainObject(DocumentTransferInbound transfer)
	{
		Document document = getBasicAsDomainObject(transfer);
		document.setDocfilename(transfer.getFileName());
//		document.setContenttype(transfer.getContentType());
//		document.setNumberofpages(transfer.getNumberOfPages());

		return document;
	}

	public static DocumentTransferOutbound getAsTransferObject(Document document, Boolean includeData) throws IOException
	{
		DocumentTransferOutbound transfer = new DocumentTransferOutbound();

		transfer.setDocumentNo(document.getDocumentNo());
		transfer.setFileName(document.getDocfilename());
		transfer.setContentType(document.getContenttype());
		transfer.setNumberOfPages(document.getNumberofpages());
		transfer.setStatus(String.valueOf(document.getStatus()));
		transfer.setPublicDocument(document.isPublic());
		transfer.setObservationDate(ConversionUtils.toNullableLocalDate(document.getObservationdate()));
		transfer.setCreatedDateTime(ConversionUtils.toNullableLocalDateTime(document.getContentdatetime()));
		transfer.setUpdateDateTime(ConversionUtils.toNullableLocalDateTime(document.getUpdatedatetime()));

		transfer.setDocumentClass(document.getDocClass());
		transfer.setDocumentSubClass(document.getDocSubClass());
		transfer.setDocumentDescription(document.getDocdesc());
		transfer.setDocumentXml(document.getDocxml());
		transfer.setAppointmentNo(document.getAppointmentNo());
		transfer.setSource(document.getSource());
		transfer.setSourceFacility(document.getSourceFacility());

		transfer.setDocumentCreator(document.getDoccreator());
		transfer.setReviewer(document.getReviewer());
		transfer.setResponsible(document.getResponsible());
		transfer.setReviewDateTime(ConversionUtils.toNullableLocalDateTime(document.getReviewdatetime()));

		if (includeData)
		{
			// convert file contents to base64 encoded string
			GenericFile genericFile = FileFactory.getDocumentFile(document.getDocfilename());
			File file = genericFile.getFileObject();

			FileInputStream imageInFile = new FileInputStream(file);
			byte fileData[] = new byte[(int) file.length()];
			imageInFile.read(fileData);
			String base64File = Base64.getEncoder().encodeToString(fileData);
			transfer.setBase64EncodedFile(base64File);
		}

		return transfer;
	}
}
