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
package org.oscarehr.demographicImport.mapper.hrm.out;

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.hrm.v4_3.ReportClass;
import xml.hrm.v4_3.ReportContent;
import xml.hrm.v4_3.ReportFormat;
import xml.hrm.v4_3.ReportsReceived;

import java.io.IOException;

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

		reportsReceived.setFormat(ReportFormat.BINARY);	// all Juno documents will be treated as binary reports
		GenericFile documentFile = exportStructure.getDocument().getFile();
		ReportContent reportContent = objectFactory.createReportContent();
		reportContent.setMedia(documentFile.toBase64ByteArray());
		reportsReceived.setContent(reportContent);
		reportsReceived.setFileExtensionAndVersion(documentFile.getExtension().toLowerCase());

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

		reportsReceived.setSendingFacility(exportStructure.getSendingFacilityId());
		reportsReceived.setSendingFacilityReportNumber(exportStructure.getSendingFacilityReport());

		return reportsReceived;
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
