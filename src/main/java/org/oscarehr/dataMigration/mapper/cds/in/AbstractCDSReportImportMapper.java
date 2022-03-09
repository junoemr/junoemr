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

package org.oscarehr.dataMigration.mapper.cds.in;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.exception.InvalidDocumentException;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.PersonNameSimple;
import xml.cds.v5_0.ReportClass;
import xml.cds.v5_0.ReportContent;
import xml.cds.v5_0.ReportFormat;
import xml.cds.v5_0.Reports;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public abstract class AbstractCDSReportImportMapper<E> extends AbstractCDSImportMapper<Reports, E>
{
	public AbstractCDSReportImportMapper()
	{
		super();
	}

	protected String getReportClass(ReportClass clazz)
	{
		if(clazz != null)
		{
			return clazz.value();
		}
		return null;
	}

	protected GenericFile getDocumentFile(Reports importStructure) throws IOException, InterruptedException, InvalidDocumentException
	{
		GenericFile tempFile;

		try
		{
			ReportFormat format = importStructure.getFormat();
			String fileExtention = importStructure.getFileExtensionAndVersion();

			if(format.equals(ReportFormat.BINARY)) //Document file
			{
				String filePath = importStructure.getFilePath().replace('\\','/');

				if(filePath != null) // external document
				{
					if(filePath.endsWith(fileExtention))
					{
						GenericFile externalFile = FileFactory.getExistingFile(patientImportContextService.getContext().getImportPreferences()
								.getExternalDocumentPath(), filePath);
						tempFile = FileFactory.createTempFile(externalFile.asFileInputStream(),
							"." + externalFile.getExtension().toLowerCase());
					}
					else
					{
						GenericFile externalFile = FileFactory.getExistingFile(patientImportContextService.getContext().getImportPreferences()
								.getExternalDocumentPath(), filePath+fileExtention);
						tempFile = FileFactory.createTempFile(externalFile.asFileInputStream(),
							"." + externalFile.getExtension().toLowerCase());
					}
				}
				else
				{
					String fileExtension = importStructure.getFileExtensionAndVersion();
					byte[] media = getReportContent(importStructure).getMedia();
					tempFile = FileFactory.createTempFile(new ByteArrayInputStream(media), "." + fileExtension.toLowerCase());
				}
			}
			else //text report
			{
				String textContent = getReportContent(importStructure).getTextContent();
				tempFile = FileFactory.createTempFile(new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8)), ".txt");
			}
		}
		catch(FileNotFoundException e)
		{
			String message = "Missing External Document: " + importStructure.getFilePath();
			throw new InvalidDocumentException(message, e);
		}
		return tempFile;
	}

	protected ProviderModel getAuthorPhysician(Reports.SourceAuthorPhysician authorPhysician)
	{
		ProviderModel provider = null;
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

	protected Reviewer getFirstReviewer(List<Reports.ReportReviewed> reviewers)
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

	protected List<Reviewer> getReviewers(List<Reports.ReportReviewed> reviews)
	{
		List<Reviewer> reviewers = new ArrayList<>();
		for (Reports.ReportReviewed review : reviews)
		{
			Reviewer reviewer = Reviewer.fromProvider(toProvider(review.getName()));
			reviewer.setReviewDateTime(PartialDateTime.from(toNullablePartialDate(review.getDateTimeReportReviewed())));
			reviewer.setOhipNumber(review.getReviewingOHIPPhysicianId());

			reviewers.add(reviewer);
		}

		return reviewers;
	}

	private ReportContent getReportContent(Reports importStructure) throws InvalidDocumentException
	{
		ReportContent content = importStructure.getContent();

		if(content == null)
		{
			throw new InvalidDocumentException("Missing ReportContent");
		}
		return content;
	}
}