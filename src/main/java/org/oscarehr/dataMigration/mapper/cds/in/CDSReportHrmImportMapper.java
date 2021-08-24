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

import org.oscarehr.dataMigration.exception.InvalidDocumentException;
import org.oscarehr.dataMigration.mapper.cds.CDSConstants;
import org.oscarehr.dataMigration.model.hrm.HrmComment;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.Reports;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CDSReportHrmImportMapper extends AbstractCDSReportImportMapper<HrmDocument>
{
	@Autowired
	protected CDSReportDocumentImportMapper documentImportMapper;

	public CDSReportHrmImportMapper()
	{
		super();
	}

	@Override
	public HrmDocument importToJuno(Reports importStructure) throws IOException, InterruptedException, InvalidDocumentException
	{
		HrmDocument document = new HrmDocument();

		document.setDocument(documentImportMapper.importToJuno(importStructure));
		document.setReportClass(HrmDocument.ReportClass.fromValueString(getReportClass(importStructure.getClazz())));
		document.setReportSubClass(importStructure.getSubClass());
		document.setReportDateTime(toNullableLocalDateTime(importStructure.getEventDateTime()));
		document.setReceivedDateTime(toNullableLocalDateTime(importStructure.getReceivedDateTime()));

		document.setCreatedBy(getAuthorPhysician(importStructure.getSourceAuthorPhysician()));
		Reviewer reviewer = getReviewer(importStructure.getReportReviewed());
		if(reviewer != null)
		{
			document.addReviewer(reviewer);
		}

		document.setReportStatus(HrmDocument.ReportStatus.fromValueString(importStructure.getHRMResultStatus()));
		document.setObservations(getObservations(importStructure.getOBRContent()));
		document.addComment(getNoteAsHrmComment(importStructure.getNotes(), document.getCreatedBy(), document.getReportDateTime()));
		document.setDescription(CDSConstants.DEFAULT_HRM_DESCRIPTION);

		document.setSourceFacility(importStructure.getSourceFacility());
		document.setSendingFacilityId(importStructure.getSendingFacilityId());
		document.setSendingFacilityReport(importStructure.getSendingFacilityReport());
		document.setMessageUniqueId(importStructure.getMessageUniqueID());
		document.setDeliverToUserId(null); // can this be derived somehow?

		return document;
	}

	protected List<HrmObservation> getObservations(List<Reports.OBRContent> obrContents)
	{
		List<HrmObservation> observationList = new ArrayList<>(obrContents.size());
		for(Reports.OBRContent obrContent : obrContents)
		{
			HrmObservation observation = new HrmObservation();
			observation.setAccompanyingDescription(obrContent.getAccompanyingDescription());
			observation.setAccompanyingMnemonic(obrContent.getAccompanyingMnemonic());
			observation.setAccompanyingSubClass(obrContent.getAccompanyingSubClass());
			observation.setObservationDateTime(toNullableLocalDateTime(obrContent.getObservationDateTime()));

			observationList.add(observation);
		}
		return observationList;
	}

	protected HrmComment getNoteAsHrmComment(String note, Provider commentProvider, LocalDateTime dateTime)
	{
		HrmComment comment = null;
		if(note != null)
		{
			comment = new HrmComment();
			comment.setText(note);
			comment.setProvider(commentProvider);
			comment.setObservationDateTime(dateTime);
		}
		return comment;
	}
}
