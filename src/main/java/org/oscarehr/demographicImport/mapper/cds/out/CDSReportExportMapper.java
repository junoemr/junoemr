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

import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.common.xml.cds.v5_0.model.ReportClass;
import org.oscarehr.common.xml.cds.v5_0.model.ReportContent;
import org.oscarehr.common.xml.cds.v5_0.model.ReportFormat;
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.demographicImport.model.document.Document;

import java.io.IOException;

public class CDSReportExportMapper extends AbstractCDSExportMapper<Reports, Document>
{
	public CDSReportExportMapper()
	{
		super();
	}

	@Override
	public Reports exportFromJuno(Document exportStructure)
	{
		Reports reports = objectFactory.createReports();

		// all Juno documents will be treated as binary reports
		reports.setFormat(ReportFormat.BINARY);
		try
		{
			GenericFile documentFile = exportStructure.getFile();

			ReportContent reportContent = objectFactory.createReportContent();
			reportContent.setMedia(documentFile.toBase64ByteArray());
			reports.setContent(reportContent);

			reports.setFileExtensionAndVersion(documentFile.getExtension());
		}
		catch(IOException e)
		{
			throw new RuntimeException("Failed Document Conversion", e);
		}

		reports.setClazz(toReportClass(exportStructure.getDocumentClass()));
		reports.setSubClass(exportStructure.getDocumentSubClass());
		reports.setEventDateTime(toNullableDateTimeFullOrPartial(exportStructure.getObservationDate().atStartOfDay()));
		reports.setReceivedDateTime(toNullableDateTimeFullOrPartial(exportStructure.getCreatedAt()));

		Reports.SourceAuthorPhysician sourceAuthorPhysician = objectFactory.createReportsSourceAuthorPhysician();
		sourceAuthorPhysician.setAuthorName(toPersonNameSimple(exportStructure.getCreatedBy()));
		reports.setSourceAuthorPhysician(sourceAuthorPhysician);

		reports.setSourceFacility(exportStructure.getSourceFacility());

		return reports;
	}

	protected ReportClass toReportClass(String docClass)
	{
		ReportClass reportClass;
		if(EnumUtils.isValidEnum(ReportClass.class, docClass))
		{
			reportClass = ReportClass.valueOf(docClass);
		}
		else
		{
			reportClass = ReportClass.OTHER_LETTER;
		}
		return reportClass;
	}
}
