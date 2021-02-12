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
package org.oscarehr.demographicImport.mapper.cds.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.model.hrm.HrmComment;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.demographicImport.model.hrm.HrmObservation;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ReportClass;
import xml.cds.v5_0.ReportContent;
import xml.cds.v5_0.ReportFormat;
import xml.cds.v5_0.Reports;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class CDSReportHrmExportMapper extends AbstractCDSReportExportMapper<HrmDocument>
{
	public CDSReportHrmExportMapper()
	{
		super();
	}

	@Override
	public Reports exportFromJuno(HrmDocument exportStructure) throws IOException
	{
		Reports reports = objectFactory.createReports();

		GenericFile documentFile = exportStructure.getDocument().getFile();
		reports.setFormat(ReportFormat.BINARY);	// all Juno documents will be treated as binary reports
		ReportContent reportContent = objectFactory.createReportContent();
		reportContent.setMedia(documentFile.toBase64ByteArray());
		reports.setContent(reportContent);
		reports.setFileExtensionAndVersion(documentFile.getExtension().toLowerCase());

		reports.setClazz(toReportClass(exportStructure.getReportClass()));
		reports.setSubClass(exportStructure.getReportSubClass());
		reports.setEventDateTime(toNullableDateTimeFullOrPartial(exportStructure.getReportDateTime()));
		reports.setReceivedDateTime(toNullableDateTimeFullOrPartial(exportStructure.getReceivedDateTime()));

		Reports.SourceAuthorPhysician sourceAuthorPhysician = objectFactory.createReportsSourceAuthorPhysician();
		sourceAuthorPhysician.setAuthorName(toPersonNameSimple(exportStructure.getCreatedBy()));
		reports.setSourceAuthorPhysician(sourceAuthorPhysician);

		reports.getReportReviewed().addAll(getReportReviewedList(exportStructure.getReviewers().toArray(new Reviewer[]{})));

		//HRM Specific fields
		reports.setSourceFacility(exportStructure.getSourceFacility());
		reports.setSendingFacilityId(exportStructure.getSendingFacilityId());
		reports.setSendingFacilityReport(exportStructure.getSendingFacilityReport());

		reports.getOBRContent().addAll(toObservationList(exportStructure.getObservations()));
		reports.setNotes(toNotes(exportStructure.getComments()));

		reports.setHRMResultStatus(getReportStatus(exportStructure.getReportStatus()));
		reports.setMessageUniqueID(exportStructure.getMessageUniqueId());

		return reports;
	}

	protected List<Reports.OBRContent> toObservationList(List<HrmObservation> observations)
	{
		List<Reports.OBRContent> contentList = new ArrayList<>(observations.size());
		for(HrmObservation observation : observations)
		{
			Reports.OBRContent content = objectFactory.createReportsOBRContent();
			content.setAccompanyingSubClass(observation.getAccompanyingSubClass());
			content.setAccompanyingMnemonic(observation.getAccompanyingMnemonic());
			content.setAccompanyingDescription(observation.getAccompanyingDescription());
			content.setObservationDateTime(toNullableDateTimeFullOrPartial(observation.getObservationDateTime()));
			contentList.add(content);
		}
		return contentList;
	}

	protected String toNotes(List<HrmComment> comments)
	{
		StringBuilder noteText = new StringBuilder();
		if(!comments.isEmpty())
		{
			for(HrmComment comment : comments)
			{
				noteText.append(comment.getText());
				noteText.append("\n");
			}
		}
		return StringUtils.trimToNull(noteText.toString());
	}

	protected ReportClass toReportClass(HrmDocument.REPORT_CLASS exportClass)
	{
		ReportClass reportClass = ReportClass.OTHER_LETTER;
		if(exportClass != null)
		{
			reportClass = ReportClass.fromValue(exportClass.getValue());
		}
		return reportClass;
	}

	protected String getReportStatus(HrmDocument.REPORT_STATUS reportStatus)
	{
		if(reportStatus != null)
		{
			return reportStatus.getValue();
		}
		return null;
	}
}
