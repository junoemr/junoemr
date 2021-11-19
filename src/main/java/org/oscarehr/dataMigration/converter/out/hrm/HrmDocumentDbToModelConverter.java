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
package org.oscarehr.dataMigration.converter.out.hrm;

import org.oscarehr.common.io.FileFactory;
import org.oscarehr.dataMigration.converter.out.BaseDbToModelConverter;
import org.oscarehr.dataMigration.model.hrm.HrmCategory;
import org.oscarehr.dataMigration.model.hrm.HrmComment;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmDocumentMatchingData;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.hospitalReportManager.model.HRMCategory;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentComment;
import org.oscarehr.hospitalReportManager.model.HRMDocumentSubClass;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class HrmDocumentDbToModelConverter extends
		BaseDbToModelConverter<HRMDocument, HrmDocument>
{

	@Override
	public HrmDocument convert(HRMDocument input)
	{
		HrmDocument hrmDocument = new HrmDocument();

		hrmDocument.setId(input.getId());
		hrmDocument.setReportDateTime(ConversionUtils.toNullableLocalDateTime(input.getReportDate()));
		hrmDocument.setReceivedDateTime(ConversionUtils.toNullableLocalDateTime(input.getTimeReceived()));
		hrmDocument.setDescription(input.getDescription());
		hrmDocument.setSendingFacility(input.getSendingFacility());
		hrmDocument.setSendingFacilityId(input.getSendingFacilityId());
		hrmDocument.setSendingFacilityReport(input.getSendingFacilityReportId());
		hrmDocument.setCreatedBy(null); // TODO not sure how to determine this
		hrmDocument.setReportStatus(HrmDocument.ReportStatus.fromValueString(input.getReportStatus().toValueString()));
		hrmDocument.setReportClass(HrmDocument.ReportClass.fromValueString(input.getReportType()));
		hrmDocument.setMessageUniqueId(input.getMessageUniqueId());
		hrmDocument.setDeliverToUserId(input.getDeliverToUserId());

		try
		{
			hrmDocument.setReportFile(FileFactory.getHrmFile(input.getReportFile()));
			hrmDocument.setReportFileSchemaVersion(input.getReportFileSchemaVersion());
			hrmDocument.setDocument(null); // leave it null for now I guess
		}
		catch(IOException e)
		{
			throw new RuntimeException("Missing HRM Document File", e);
		}

		hrmDocument.setHashData(getHashData(input));
		hrmDocument.setObservations(getObservations(input.getDocumentSubClassList()));
		hrmDocument.setComments(getComments(input.getCommentList()));
		hrmDocument.setCategory(getCategory(input.getHrmCategory()));

		return hrmDocument;
	}

	protected HrmDocumentMatchingData getHashData(HRMDocument input)
	{
		HrmDocumentMatchingData hashData = new HrmDocumentMatchingData();
		hashData.setReportHash(input.getReportHash());
		hashData.setReportLessDemographicInfoHash(input.getReportLessDemographicInfoHash());
		hashData.setReportLessTransactionInfoHash(input.getReportLessTransactionInfoHash());
		hashData.setNumDuplicatesReceived(input.getNumDuplicatesReceived());
		hashData.setUnmatchedProviders(input.getUnmatchedProviders());
		return hashData;
	}

	protected List<HrmObservation> getObservations(List<HRMDocumentSubClass> subClassList)
	{
		List<HrmObservation> observations = new ArrayList<>(subClassList.size());

		for(HRMDocumentSubClass subClass : subClassList)
		{
			HrmObservation observation = new HrmObservation();
			observation.setId(subClass.getId());
			observation.setAccompanyingSubClass(subClass.getSubClass());
			observation.setAccompanyingMnemonic(subClass.getSubClassMnemonic());
			observation.setAccompanyingDescription(subClass.getSubClassDescription());
			observation.setObservationDateTime(ConversionUtils.toNullableLocalDateTime(subClass.getSubClassDateTime()));
			observations.add(observation);
		}

		return observations;
	}

	protected List<HrmComment> getComments(List<HRMDocumentComment> commentList)
	{
		List<HrmComment> comments = new ArrayList<>(commentList.size());
		for(HRMDocumentComment hrmComment : commentList)
		{
			HrmComment comment = new HrmComment();
			comment.setId(hrmComment.getId());
			comment.setProvider(findProvider(hrmComment.getProvider()));
			comment.setObservationDateTime(ConversionUtils.toNullableLocalDateTime(hrmComment.getCommentTime()));
			comment.setText(hrmComment.getComment());
			comments.add(comment);
		}
		return comments;
	}

	protected HrmCategory getCategory(HRMCategory hrmCategory)
	{
		HrmCategory category = null;
		if(hrmCategory != null)
		{
			category = new HrmCategory();
			category.setId(hrmCategory.getId());
			category.setName(hrmCategory.getCategoryName());
			category.setDisabledAt(hrmCategory.getDisabledAt());
		}
		return category;
	}
}