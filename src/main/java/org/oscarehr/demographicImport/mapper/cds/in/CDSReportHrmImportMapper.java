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

import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.Reports;
import org.oscarehr.demographicImport.mapper.cds.CDSConstants;
import org.oscarehr.demographicImport.model.hrm.HrmComment;
import org.oscarehr.demographicImport.model.hrm.HrmDocument;
import org.oscarehr.demographicImport.model.hrm.HrmObservation;
import org.oscarehr.demographicImport.model.provider.Provider;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class CDSReportHrmImportMapper extends AbstractCDSReportImportMapper<HrmDocument>
{
	private static final Logger logger = MiscUtils.getLogger();

	@Autowired
	protected CDSReportDocumentImportMapper documentImportMapper;

	public CDSReportHrmImportMapper()
	{
		super();
	}

	@Override
	public HrmDocument importToJuno(Reports importStructure) throws IOException, InterruptedException
	{
		HrmDocument document = new HrmDocument();

		document.setDocument(documentImportMapper.importToJuno(importStructure));
		document.setReportClass(HrmDocument.REPORT_CLASS.fromValueString(getReportClass(importStructure.getClazz())));
		document.setReportSubClass(importStructure.getSubClass());
		document.setReportDateTime(toNullableLocalDateTime(importStructure.getEventDateTime()));
		document.setReceivedDateTime(toNullableLocalDateTime(importStructure.getReceivedDateTime()));

		document.setCreatedBy(getAuthorPhysician(importStructure.getSourceAuthorPhysician()));
		Reviewer reviewer = getReviewer(importStructure.getReportReviewed());
		if(reviewer != null)
		{
			document.addReviewer(reviewer);
		}

		document.setReportStatus(HrmDocument.REPORT_STATUS.fromValueString(importStructure.getHRMResultStatus()));
		document.setObservations(getObservations(importStructure.getOBRContent()));
		document.addComment(getNoteAsHrmComment(importStructure.getNotes(), document.getCreatedBy(), document.getReportDateTime()));
		document.setDescription(CDSConstants.DEFAULT_HRM_DESCRIPTION);

		document.setSourceFacility(importStructure.getSourceFacility());
		document.setSendingFacilityId(importStructure.getSendingFacilityId());
		document.setSendingFacilityReport(importStructure.getSendingFacilityReport());
		document.setMessageUniqueId(importStructure.getMessageUniqueID());

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
