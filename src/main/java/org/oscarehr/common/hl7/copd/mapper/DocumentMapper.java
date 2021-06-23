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
package org.oscarehr.common.hl7.copd.mapper;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.document.model.Document;
import org.oscarehr.util.MiscUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentMapper extends AbstractMapper
{

	public static final int DOCUMENT_DESCRIPTION_LENGTH = 255;

	public DocumentMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public int getNumDocuments()
	{
		return provider.getZATReps();
	}

	public List<Document> getDocumentList()
	{
		int numDocuments = getNumDocuments();
		List<Document> documentList = new ArrayList<>(numDocuments);
		for(int i=0; i< numDocuments; i++)
		{
			documentList.add(getDocument(i));
		}
		return documentList;
	}

	public Document getDocument(int rep)
	{
		Document document = new Document();

		document.setObservationdate(getObservationDate(rep));


		String docDescription = getDescription(rep);
		if (docDescription.trim().isEmpty())
		{
			docDescription = "No Name";
			MiscUtils.getLogger().warn("document, " + getFileName(rep) + " has no document description, setting to \"No Name\"");
		}

		if (docDescription.length() > DOCUMENT_DESCRIPTION_LENGTH)
		{
			logger.warn("document " + getFileName(rep) + " has too long of a document description, truncating");
			docDescription = StringUtils.left(docDescription, DOCUMENT_DESCRIPTION_LENGTH);
		}

		document.setDocdesc(docDescription);

		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{// Mediplan file names can include HTML escape sequences!
			document.setDocfilename(StringEscapeUtils.unescapeHtml(getFileName(rep)));
		}
		else
		{
			document.setDocfilename(getFileName(rep));
		}

		document.setContenttype(getContentType(rep));
		document.setStatus(Document.STATUS_ACTIVE);;
		document.setDoctype(getDocType(rep));

		return document;
	}

	public Date getObservationDate(int rep)
	{
		return getNullableDate(provider.getZAT(rep).getZat2_Date().getValue());
	}

	public String getFileName(int rep)
	{
		String fileName = provider.getZAT(rep).getZat4_Attachment().getPointer().getValue();
		fileName = fileName.replaceAll("'amp;", "&");
		return fileName;
	}

	public String getDescription(int rep)
	{
		String description = provider.getZAT(rep).getZat3_Name().getValue();
		return StringEscapeUtils.unescapeXml(description);
	}

	public String getContentType(int rep)
	{
		//TODO map to application/contenttype correctly
		return "application/" + StringUtils.lowerCase(provider.getZAT(rep).getZat4_Attachment().getSubtype().getValue());
	}

	/**
	 * get, Juno document type. things like, Consult, Lab, Procedure, ect.
	 * @param rep - the document you wish to get the type for.
	 * @return - the document type, if not overriden returns the string "N/A"
	 */
	public String getDocType(int rep)
	{
		return "N/A";
	}
}
