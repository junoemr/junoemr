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
import org.oscarehr.document.model.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentMapper extends AbstractMapper
{
	public DocumentMapper(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep);
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
		document.setDocdesc(getDescription(rep));
		document.setDocfilename(getFileName(rep));
		document.setContenttype(getContentType(rep));
		document.setStatus(Document.STATUS_ACTIVE);

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
}
