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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.io.XMLFile;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.hrm.HrmComment;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.hospitalReportManager.HRMReport;
import org.oscarehr.hospitalReportManager.HRMReportParser;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ReportContent;
import xml.cds.v5_0.ReportFormat;
import xml.cds.v5_0.Reports;

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
	public Reports exportFromJuno(HrmDocument exportStructure) throws Exception
	{
		Reports reports = objectFactory.createReports();

		reports.setFormat(ReportFormat.BINARY);	// all Juno documents will be treated as binary reports

		byte[] media;
		Document document = exportStructure.getDocument();
		if(document != null)
		{
			GenericFile hrmDocumentFile = document.getFile();
			media = hrmDocumentFile.toByteArray();
			reports.setFileExtensionAndVersion(hrmDocumentFile.getExtension().toLowerCase());
		}
		else
		{
			HRMReport hrmReport = HRMReportParser.parseReport(new XMLFile(exportStructure.getReportFile().getFileObject()), exportStructure.getReportFileSchemaVersion());
			// Have to read the appropriate content type here
			media = hrmReport.getBinaryContent();
			reports.setFileExtensionAndVersion(hrmReport.getFileExtension());
		}

		ReportContent reportContent = objectFactory.createReportContent();
		reportContent.setMedia(media);
		reports.setContent(reportContent);

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

	protected xml.cds.v5_0.ReportClass toReportClass(HrmDocument.ReportClass exportClass)
	{
		xml.cds.v5_0.ReportClass reportClass = xml.cds.v5_0.ReportClass.OTHER_LETTER;
		if(exportClass != null)
		{
			reportClass = xml.cds.v5_0.ReportClass.fromValue(exportClass.getValue());
		}
		return reportClass;
	}

	protected String getReportStatus(HrmDocument.ReportStatus reportStatus)
	{
		if(reportStatus != null)
		{
			return reportStatus.getValue();
		}
		return null;
	}
}
