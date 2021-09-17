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

import org.apache.log4j.Logger;
import org.oscarehr.common.io.GenericFile;
import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.hrm.HrmComment;
import org.oscarehr.dataMigration.model.hrm.HrmDocument;
import org.oscarehr.dataMigration.model.hrm.HrmObservation;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.healthReportManager.model.HRMDocument;
import org.oscarehr.healthReportManager.model.HRMDocumentComment;
import org.oscarehr.healthReportManager.model.HRMDocumentSubClass;
import org.oscarehr.healthReportManager.model.HRMDocumentToProvider;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.provider.search.ProviderCriteriaSearch;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class HrmDocumentModelToDbConverter extends BaseModelToDbConverter<HrmDocument, HRMDocument>
{
	private Logger logger = MiscUtils.getLogger();
	
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
		
		hrmDocument.setReportFile(getReportFileRelativePath(input.getReportFile()));
		hrmDocument.setReportFileSchemaVersion(input.getReportFileSchemaVersion());

		hrmDocument.setDocumentSubClassList(convertSubClassList(hrmDocument, input.getObservations()));
		hrmDocument.setCommentList(convertCommentList(hrmDocument, input.getComments()));
		
		hrmDocument.setReportHash(input.getMessageUniqueId());
		hrmDocument.setNumDuplicatesReceived(0);
		
		List<HRMDocumentToProvider> providerLinks = new ArrayList<HRMDocumentToProvider>();
		
		HRMDocumentToProvider deliverToProviderLink = createDeliverToLink(hrmDocument, input.getDeliverToUserId());
		if (deliverToProviderLink != null)
		{
			providerLinks.add(deliverToProviderLink);
		}
		
		if (input.getInternalReviewers() != null && !input.getInternalReviewers().isEmpty())
		{
			providerLinks.addAll(convertReviewerLinks(hrmDocument, input.getInternalReviewers()));
		}
		
		hrmDocument.setDocumentToProviderList(providerLinks);

		return hrmDocument;
	}

	protected String getReportFileRelativePath(GenericFile file)
	{
		if (file != null)
		{
			Path hrmBaseDirectory = Paths.get(GenericFile.HRM_BASE_DIR);
			Path relativePath = hrmBaseDirectory.relativize(Paths.get(file.getPath()));
			
			return relativePath.toString();
		}
		
		return null;
	}

	protected String reportType(HrmDocument.ReportClass reportClass)
	{
		String reportType = null;
		if(reportClass != null)
		{
			reportType = reportClass.getValue();
		}
		return reportType;
	}

	protected HRMDocument.STATUS reportStatus(HrmDocument.ReportStatus reportStatus)
	{
		HRMDocument.STATUS status = null;
		if(reportStatus != null)
		{
			status = HRMDocument.STATUS.fromValueString(reportStatus.getValue());
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

	protected List<HRMDocumentToProvider> convertReviewerLinks(HRMDocument hrmDocument, List<Reviewer> reviewers)
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
	
	/**
	 * Find the provider associated with the deliverToID.  If the ID starts with a "D", the id refers to the CPSID
	 * of a physician.  If it starts with an "N", then it's the CNO number of a nurse.
	 * @param deliverToID Practitioner No of the provider for the link
	 * @returns provider link if provider exists in the system, otherwise null
	 */
	protected HRMDocumentToProvider createDeliverToLink(HRMDocument document, String deliverToID)
	{
		ProviderCriteriaSearch searchParams = new ProviderCriteriaSearch();
		if (!deliverToID.isEmpty())
		{
			String practitionerNumber = deliverToID.substring(1);
			
			HrmDocument.DeliveryPrefix prefix = HrmDocument.DeliveryPrefix.fromString(deliverToID.substring(0, 1));
			
			if (HrmDocument.DeliveryPrefix.DOCTOR.equals(prefix))
			{
				searchParams.setPractitionerNo(practitionerNumber);
			}
			else if (HrmDocument.DeliveryPrefix.NURSE.equals(prefix))
			{
				searchParams.setOntarioCnoNumber(practitionerNumber);
			}
		}
		
		List<ProviderData> providers = searchProviders(searchParams);
		HRMDocumentToProvider link = null;
		
		if (providers == null || providers.size() == 0)
		{
			logger.info(String.format("Could not match provider (%s) for HRM document (unlinked): %s",
			                          document.getDeliverToUserId(),
			                          document.getReportFile()));
		}
		else if (providers.size() == 1)
		{
			ProviderData foundProvider = providers.get(0);
			
			link = new HRMDocumentToProvider();
			link.setProviderNo(String.valueOf(foundProvider.getProviderNo()));
			link.setHrmDocument(document);
		}
		else
		{
			logger.info(String.format("Multiple providers (%s) matched for HRM document (unlinked): %s",
			                          document.getDeliverToUserId(),
			                          document.getReportFile()));
		}
		
		return link;
	}
}
