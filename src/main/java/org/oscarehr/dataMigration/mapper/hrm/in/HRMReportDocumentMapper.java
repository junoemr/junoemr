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

package org.oscarehr.dataMigration.mapper.hrm.in;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.model.document.Document;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.hospitalReportManager.reportImpl.HRMReport_4_3;
import org.springframework.stereotype.Component;
import xml.hrm.v4_3.ReportsReceived;

import java.io.IOException;
import java.util.List;

@Component
public class HRMReportDocumentMapper extends AbstractHRMImportMapper<HRMReport_4_3, Document>
{
	@Override
	public Document importToJuno(HRMReport_4_3 importStructure) throws IOException
	{
		Document document = new Document();
		document.setStatus(Document.STATUS.ACTIVE);
		document.setFile(FileFactory.getExistingFile(importStructure.getFileLocation()));
		
		ReportsReceived report = importStructure.getDocumentRoot().getPatientRecord().getReportsReceived().get(0);
		
		document.setDocumentClass(report.getClazz().value());
		document.setDocumentSubClass(report.getSubClass());
		document.setObservationDate(toNullableLocalDate(report.getEventDateTime()));
		document.setCreatedAt(toNullableLocalDateTime(report.getReceivedDateTime()));
		document.setSource("HRM");
		document.setResponsible(stubProviderFromPersonName(report.getAuthorPhysician()));
		
		document.setReviewer(getFirstStubReviewer(report));
		
		document.setResponsible(document.getCreatedBy());
		document.setDescription(document.getDocumentClass());
		document.setSourceFacility(importStructure.getSendingFacilityId());
		
		document.setAnnotation(null);
		
		return document;
	}
	
	protected Reviewer getFirstStubReviewer(ReportsReceived report)
	{
		List<Reviewer> reviewerList = stubReviewers(report.getReviewingOHIPPhysicianId(), report.getReviewedDateTime());
		
		if (reviewerList == null || reviewerList.isEmpty())
		{
			return null;
		}
		
		return reviewerList.get(0);
	}
}