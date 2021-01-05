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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.xml.cds.v5_0.model.PersonNameSimple;
import org.oscarehr.common.xml.cds.v5_0.model.ReportClass;
import org.oscarehr.common.xml.cds.v5_0.model.ReportFormat;
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.demographicImport.model.common.PartialDateTime;
import org.oscarehr.demographicImport.model.document.Document;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class CDSReportDocumentImportMapper extends AbstractCDSReportImportMapper<Document>
{
	public CDSReportDocumentImportMapper()
	{
		super();
	}

	@Override
	public Document importToJuno(Reports importStructure)
	{
		Document document = new Document();
		try
		{
			document.setFile(getDocumentFile(importStructure));
		}
		catch(IOException | InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		document.setDocumentClass(getDocClass(importStructure.getClazz()));
		document.setDocumentSubClass(importStructure.getSubClass());
		document.setObservationDate(toNullableLocalDate(importStructure.getEventDateTime()));
		document.setCreatedAt(toNullableLocalDateTime(importStructure.getReceivedDateTime()));

		document.setCreatedBy(getAuthorPhysician(importStructure.getSourceAuthorPhysician()));
		document.setResponsible(document.getCreatedBy());
		document.setReviewer(getReviewer(importStructure.getReportReviewed()));

		document.setAnnotation(importStructure.getNotes());
		document.setStatus(Document.STATUS.ACTIVE);
		document.setDescription(getDocDescription(importStructure));

		return document;
	}

	protected GenericFile getDocumentFile(Reports importStructure) throws IOException, InterruptedException
	{
		GenericFile tempFile;

		ReportFormat format = importStructure.getFormat();
		if(format.equals(ReportFormat.BINARY)) //Document file
		{
			String filePath = importStructure.getFilePath();
			if(filePath != null) // external document
			{
				GenericFile externalFile = FileFactory.getExistingFile(importProperties.getExternalDocumentPath(), filePath);
				tempFile = FileFactory.createTempFile(externalFile.asFileInputStream(), "." + externalFile.getExtension().toLowerCase());
			}
			else
			{
				String fileExtension = importStructure.getFileExtensionAndVersion();
				byte[] base64Media = importStructure.getContent().getMedia();
				tempFile = FileFactory.createTempFile(new ByteArrayInputStream(base64Media), "." + fileExtension.toLowerCase());
			}
		}
		else //text report
		{
			String textContent = importStructure.getContent().getTextContent();
			tempFile = FileFactory.createTempFile(new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8)), ".txt");
		}
		return tempFile;
	}

	protected String getDocClass(ReportClass clazz)
	{
		if(clazz != null)
		{
			return clazz.value();
		}
		return null;
	}

	protected Provider getAuthorPhysician(Reports.SourceAuthorPhysician authorPhysician)
	{
		Provider provider = null;
		if(authorPhysician != null)
		{
			PersonNameSimple personNameSimple = authorPhysician.getAuthorName();
			if(personNameSimple != null)
			{
				provider = toProvider(authorPhysician.getAuthorName());
			}
			else
			{
				provider = toProviderNames(authorPhysician.getAuthorFreeText());
			}
		}
		return provider;
	}

	protected Reviewer getReviewer(List<Reports.ReportReviewed> reviewers)
	{
		Reviewer reviewer = null;
		if(reviewers != null && !reviewers.isEmpty())
		{
			Reports.ReportReviewed reviewed = reviewers.get(0);
			reviewer = Reviewer.fromProvider(toProvider(reviewed.getName()));
			reviewer.setReviewDateTime(PartialDateTime.from(toNullablePartialDate(reviewed.getDateTimeReportReviewed())));
			reviewer.setOhipNumber(reviewed.getReviewingOHIPPhysicianId());
		}
		return reviewer;
	}

	protected String getDocDescription(Reports importStructure)
	{
		String clazz = getDocClass(importStructure.getClazz());
		return (clazz != null) ? clazz : "Imported Report";
	}
}
