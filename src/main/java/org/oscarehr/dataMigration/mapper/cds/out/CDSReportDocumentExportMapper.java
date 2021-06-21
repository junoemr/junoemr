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

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.model.document.Document;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ReportContent;
import xml.cds.v5_0.ReportFormat;
import xml.cds.v5_0.Reports;

import java.io.IOException;

@Component
public class CDSReportDocumentExportMapper extends AbstractCDSReportExportMapper<Document>
{
	public CDSReportDocumentExportMapper()
	{
		super();
	}

	@Override
	public Reports exportFromJuno(Document exportStructure) throws IOException
	{
		Reports reports = objectFactory.createReports();

		reports.setMedia(null);
		reports.setFormat(ReportFormat.BINARY);	// all Juno documents will be treated as binary reports

		GenericFile documentFile = exportStructure.getFile();

		ReportContent reportContent = objectFactory.createReportContent();
		reportContent.setMedia(documentFile.toByteArray());
		reports.setContent(reportContent);

		reports.setFileExtensionAndVersion(documentFile.getExtension().toLowerCase());

		reports.setClazz(toReportClass(exportStructure.getDocumentClass()));
		reports.setSubClass(exportStructure.getDocumentSubClass());
		reports.setEventDateTime(toNullableDateTimeFullOrPartial(exportStructure.getObservationDate().atStartOfDay()));
		reports.setReceivedDateTime(toNullableDateTimeFullOrPartial(exportStructure.getCreatedAt()));

		Reports.SourceAuthorPhysician sourceAuthorPhysician = objectFactory.createReportsSourceAuthorPhysician();
		sourceAuthorPhysician.setAuthorName(toPersonNameSimple(exportStructure.getCreatedBy()));
		reports.setSourceAuthorPhysician(sourceAuthorPhysician);

		reports.getReportReviewed().addAll(getReportReviewedList(exportStructure.getReviewer()));
		reports.setNotes(exportStructure.getAnnotation());

		// the following can be omitted for non-hrm documents:
		/*  1.SourceFacility
			2.SendingFacilityID
			3.SendingFacilityReport
			4.OBRContent/AccompanyingSubClass
			5.OBRContent/AccompanyingMnemonic
			6.OBRContent/AccompanyingDescription
			7.OBRContent/ObservationDateTime
			8.HRMResultStatus
			9.MessageUniqueID
			*/
		/* only populate RecipientName and DateTimeSent if the report was sent to another physician.
		* This does not apply to standard documents the way Juno uses them */

		return reports;
	}
}
