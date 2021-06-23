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
package org.oscarehr.dataMigration.converter.in.hrm;

import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.hrm.HrmComment;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.oscarehr.hospitalReportManager.model.HRMDocumentComment;
import org.oscarehr.hospitalReportManager.model.HRMDocumentSubClass;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class HrmDocumentModelToDbConverter extends BaseModelToDbConverter<HrmDocument, HRMDocument>
{
	@Override
	public HRMDocument convert(HrmDocument input)
	{
		HRMDocument hrmDocument = new HRMDocument();
		hrmDocument.setDescription(input.getDescription());
		hrmDocument.setReportType(reportType(input.getReportClass()));
		hrmDocument.setReportStatus(reportStatus(input.getReportStatus()));
		hrmDocument.setTimeReceived(ConversionUtils.toNullableLegacyDateTime(input.getReceivedDateTime()));
		hrmDocument.setReportDate(ConversionUtils.toNullableLegacyDateTime(input.getReportDateTime()));
		hrmDocument.setSourceFacility(input.getSourceFacility());
		hrmDocument.setSendingFacilityId(input.getSendingFacilityId());
		hrmDocument.setSendingFacilityReportId(input.getSendingFacilityReport());
		hrmDocument.setMessageUniqueId(input.getMessageUniqueId());
		hrmDocument.setDeliverToUserId(input.getDeliverToUserId());

		hrmDocument.setReportFile(getReportFileName(input.getReportFile()));
		hrmDocument.setReportFileSchemaVersion(input.getReportFileSchemaVersion());

		hrmDocument.setDocumentSubClassList(convertSubClassList(hrmDocument, input.getObservations()));
		hrmDocument.setCommentList(convertCommentList(hrmDocument, input.getComments()));
		hrmDocument.setDocumentToProviderList(convertProviderLinks(hrmDocument, input.getReviewers()));

		return hrmDocument;
	}

	protected String getReportFileName(GenericFile file)
	{
		if(file != null)
		{
			return file.getName();
		}
		return null;
	}

	protected String reportType(HrmDocument.REPORT_CLASS reportClass)
	{
		String reportType = null;
		if(reportClass != null)
		{
			reportType = reportClass.getValue();
		}
		return reportType;
	}

	protected String reportStatus(HrmDocument.REPORT_STATUS reportStatus)
	{
		String status = null;
		if(reportStatus != null)
		{
			status = reportStatus.getValue();
		}
		return status;
	}

	protected List<HRMDocumentSubClass> convertSubClassList(HRMDocument hrmDocument, List<HrmObservation> observations)
	{
		List<HRMDocumentSubClass> hrmDocumentSubClassList = new ArrayList<>(observations.size());
		for(HrmObservation observation : observations)
		{
			HRMDocumentSubClass hrmDocumentSubClass = new HRMDocumentSubClass();
			hrmDocumentSubClass.setActive(true);
			hrmDocumentSubClass.setSubClass(observation.getAccompanyingSubClass());
			hrmDocumentSubClass.setSubClassDescription(observation.getAccompanyingDescription());
			hrmDocumentSubClass.setSubClassMnemonic(observation.getAccompanyingMnemonic());
			hrmDocumentSubClass.setSubClassDateTime(ConversionUtils.toNullableLegacyDateTime(observation.getObservationDateTime()));
			hrmDocumentSubClass.setHrmDocument(hrmDocument);
			hrmDocumentSubClassList.add(hrmDocumentSubClass);
		}
		return hrmDocumentSubClassList;
	}

	protected List<HRMDocumentComment> convertCommentList(HRMDocument hrmDocument, List<HrmComment> comments)
	{
		List<HRMDocumentComment> hrmDocumentCommentList = new ArrayList<>(comments.size());
		for(HrmComment comment : comments)
		{
			HRMDocumentComment hrmDocumentComment = new HRMDocumentComment();
			hrmDocumentComment.setComment(comment.getText());
			hrmDocumentComment.setDeleted(false);
			hrmDocumentComment.setHrmDocument(hrmDocument);
			hrmDocumentComment.setCommentTime(ConversionUtils.toNullableLegacyDateTime(comment.getObservationDateTime()));
			hrmDocumentComment.setProvider(findOrCreateProviderRecord(comment.getProvider(), false));
			hrmDocumentCommentList.add(hrmDocumentComment);
		}
		return hrmDocumentCommentList;
	}

	protected List<HRMDocumentToProvider> convertProviderLinks(HRMDocument hrmDocument, List<Reviewer> reviewers)
	{
		List<HRMDocumentToProvider> hrmDocumentToProviderList = new ArrayList<>(reviewers.size());
		for(Reviewer reviewer : reviewers)
		{
			HRMDocumentToProvider hrmDocumentToProvider = new HRMDocumentToProvider();
			hrmDocumentToProvider.setHrmDocument(hrmDocument);
			hrmDocumentToProvider.setProvider(findOrCreateProviderRecord(reviewer, false));

			PartialDateTime signOffDateTime = reviewer.getReviewDateTime();
			boolean signed = (signOffDateTime != null);
			hrmDocumentToProvider.setSignedOff(signed);
			if(signed)
			{
				hrmDocumentToProvider.setSignedOffTimestamp(ConversionUtils.toLegacyDateTime(signOffDateTime.toLocalDateTime()));
			}
			hrmDocumentToProviderList.add(hrmDocumentToProvider);
		}
		return hrmDocumentToProviderList;
	}
}
