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
package org.oscarehr.dataMigration.mapper.hrm.out;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.hrm.v4_3.ReportContent;
import xml.hrm.v4_3.ReportFormat;
import xml.hrm.v4_3.ReportsReceived;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class HRMReportExportMapper extends AbstractHRMExportMapper<ReportsReceived, HrmDocument>
{
	private static final Logger logger = Logger.getLogger(HRMReportExportMapper.class);

	public HRMReportExportMapper()
	{
		super();
	}

	@Override
	public ReportsReceived exportFromJuno(HrmDocument exportStructure) throws IOException
	{
		ReportsReceived reportsReceived = objectFactory.createReportsReceived();

		Document document = exportStructure.getDocument();
		if(document == null)
		{
			throw new RuntimeException("HRM document model cannot be exported without an attached Document model");
		}

		GenericFile documentFile = document.getFile();

		ReportContent reportContent = objectFactory.createReportContent();
		if (documentFile.getExtension().equals("txt"))
		{
			reportsReceived.setFormat(ReportFormat.TEXT);
			reportContent.setTextContent(new String(documentFile.toByteArray(), StandardCharsets.UTF_8));
		}
		else
		{
			reportsReceived.setFormat(ReportFormat.BINARY);
			reportContent.setMedia(documentFile.toByteArray());
		}

		reportsReceived.setContent(reportContent);
		// OMD-HRM files contain the '.' as part of this field
		reportsReceived.setFileExtensionAndVersion("." + documentFile.getExtension().toLowerCase());

		reportsReceived.setClazz(toReportClass(exportStructure.getReportClass()));
		reportsReceived.setSubClass(exportStructure.getReportSubClass());
		reportsReceived.setEventDateTime(toNullableDateFullOrPartial(exportStructure.getReportDateTime()));
		reportsReceived.setReceivedDateTime(toNullableDateFullOrPartial(exportStructure.getReceivedDateTime()));

		reportsReceived.setAuthorPhysician(toPersonNameSimple(exportStructure.getCreatedBy()));
		reportsReceived.setResultStatus(getReportStatus(exportStructure.getReportStatus()));

		if(!exportStructure.getReviewers().isEmpty())
		{
			Reviewer reviewer = exportStructure.getReviewers().get(0);
			reportsReceived.setReviewingOHIPPhysicianId(reviewer.getOhipNumber());
			reportsReceived.setReviewedDateTime(toNullableDateFullOrPartial(reviewer.getReviewDateTime()));
		}

		reportsReceived.getOBRContent().addAll(toObservationList(exportStructure.getObservations()));
		reportsReceived.setSendingFacility(exportStructure.getSendingFacilityId());
		reportsReceived.setSendingFacilityReportNumber(exportStructure.getSendingFacilityReport());

		return reportsReceived;
	}

	protected List<ReportsReceived.OBRContent> toObservationList(List<HrmObservation> observations)
	{
		List<ReportsReceived.OBRContent> contentList = new ArrayList<>(observations.size());
		for(HrmObservation observation : observations)
		{
			ReportsReceived.OBRContent content = objectFactory.createReportsReceivedOBRContent();
			content.setAccompanyingSubClass(observation.getAccompanyingSubClass());
			content.setAccompanyingMnemonic(observation.getAccompanyingMnemonic());
			content.setAccompanyingDescription(observation.getAccompanyingDescription());
			content.setObservationDateTime(toNullableDateFullOrPartial(observation.getObservationDateTime()));
			contentList.add(content);
		}
		return contentList;
	}

	protected xml.hrm.v4_3.ReportClass toReportClass(HrmDocument.ReportClass exportClass)
	{
		xml.hrm.v4_3.ReportClass reportClass = xml.hrm.v4_3.ReportClass.OTHER_LETTER;
		if(exportClass != null)
		{
			reportClass = xml.hrm.v4_3.ReportClass.fromValue(exportClass.getValue());
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