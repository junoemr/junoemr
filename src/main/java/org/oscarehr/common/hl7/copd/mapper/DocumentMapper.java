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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.document.model.Document;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DocumentMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	public DocumentMapper()
	{
		message = null;
		provider = null;
	}
	public DocumentMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
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
		return ConversionUtils.fromDateString(provider.getZAT(rep).getZat2_Date().getValue(), "yyyyMMdd");
	}

	public String getFileName(int rep)
	{
		return provider.getZAT(rep).getZat4_Attachment().getPointer().getValue();
	}

	public String getDescription(int rep)
	{
		return provider.getZAT(rep).getZat3_Name().getValue();
	}

	public String getContentType(int rep)
	{
		//TODO map to apllication/contenttype correctly
		return "application/" + StringUtils.lowerCase(provider.getZAT(rep).getZat4_Attachment().getSubtype().getValue());
	}
}
