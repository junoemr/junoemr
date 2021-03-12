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
package org.oscarehr.dataMigration.converter.out;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.dataMigration.converter.out.note.EncounterNoteDbToModelConverter;
import org.oscarehr.dataMigration.model.common.PartialDateTime;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.lab.Lab;
import org.oscarehr.dataMigration.model.lab.LabObservation;
import org.oscarehr.dataMigration.model.lab.LabObservationResult;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.oscarehr.encounterNote.dao.CaseManagementNoteLinkDao;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class LabDbToModelConverter extends
		BaseDbToModelConverter<Hl7TextInfo, org.oscarehr.dataMigration.model.lab.Lab>
{
	@Autowired
	private ProviderLabRoutingDao providerLabRoutingDao;

	@Autowired
	private CaseManagementNoteLinkDao caseManagementNoteLinkDao;

	@Autowired
	private EncounterNoteDbToModelConverter encounterNoteDbToModelConverter;


	@Override
	public Lab convert(Hl7TextInfo hl7TextInfo)
	{
		if(hl7TextInfo == null)
		{
			return null;
		}
		Hl7TextMessage hl7TextMessage = hl7TextInfo.getHl7TextMessage();
		MessageHandler labHandler = Factory.getHandler(hl7TextMessage);
		Lab exportLab = new Lab();

		exportLab.setId(hl7TextMessage.getId());
		exportLab.setAccessionNumber(labHandler.getAccessionNum());
		exportLab.setVersion(labHandler.getFillerOrderNumber());
		exportLab.setMessageDateTime(ConversionUtils.toLocalDateTime(labHandler.getMsgDate()));
		exportLab.setEmrReceivedDateTime(ConversionUtils.toLocalDateTime(hl7TextMessage.getCreated()));
		exportLab.setSendingFacility(labHandler.getPatientLocation());

		Map<String, CaseManagementNote> annotationMap = caseManagementNoteLinkDao.getLabNotesByNoteLink(hl7TextMessage.getId());
		for(int i = 0; i < labHandler.getOBRCount(); i++)
		{
			exportLab.addObservation(getObservations(labHandler, i, annotationMap));
		}

		exportLab.setReviewers(getReviewers(hl7TextInfo));

		return exportLab;
	}

	private List<Reviewer> getReviewers(Hl7TextInfo hl7TextInfo)
	{
		List<ProviderLabRoutingModel> providerLabRoutingList = providerLabRoutingDao.getProviderLabRoutings(
				hl7TextInfo.getLabNumber(), ProviderLabRoutingModel.LAB_TYPE_LABS);

		List<Reviewer> reviewers = new ArrayList<>(providerLabRoutingList.size());
		for(ProviderLabRoutingModel providerLabRouting : providerLabRoutingList)
		{
			String reviewerId = providerLabRouting.getProviderNo();
			if(reviewerId != null && !String.valueOf(ProviderLabRoutingDao.PROVIDER_UNMATCHED).equals(reviewerId))
			{
				Reviewer reviewer = Reviewer.fromProvider(findProvider(reviewerId));
				reviewer.setReviewDateTime(PartialDateTime.from(ConversionUtils.toLocalDateTime(providerLabRouting.getTimestamp())));
				reviewers.add(reviewer);
			}
		}
		return reviewers;
	}

	private LabObservation getObservations(MessageHandler labHandler, int obrIndex, Map<String, CaseManagementNote> annotationMap)
	{
		LabObservation observation = new LabObservation();
		observation.setName(labHandler.getOBRName(obrIndex));
		observation.setProcedureCode(labHandler.getOBRProcedureCode(obrIndex));
		observation.setObservationDateTime(
				ConversionUtils.toNullableLocalDateTime(StringUtils.trimToNull(labHandler.getReportDate(obrIndex))));
		observation.setRequestDateTime(
				ConversionUtils.toNullableLocalDateTime(StringUtils.trimToNull(labHandler.getRequestDate(obrIndex))));

		for(int i = 0; i < labHandler.getOBXCount(obrIndex); i++)
		{
			observation.addResult(getObservationResult(labHandler, obrIndex, i, annotationMap));
		}

		for(int i = 0; i < labHandler.getOBRCommentCount(obrIndex); i++)
		{
			observation.addComment(labHandler.getOBRComment(obrIndex, i));
		}

		return observation;
	}


	private LabObservationResult getObservationResult(
			MessageHandler labHandler,
			int obrIndex,
			int obxIndex,
			Map<String, CaseManagementNote> annotationMap)
	{
		LabObservationResult result = new LabObservationResult();
		result.setAbnormal(labHandler.isOBXAbnormal(obrIndex, obxIndex));

		result.setObservationDateTime(
				ConversionUtils.toNullableLocalDateTime(StringUtils.trimToNull(labHandler.getReportDate(obrIndex))));

		result.setName(labHandler.getOBXName(obrIndex, obxIndex));
		result.setIdentifier(labHandler.getOBXIdentifier(obrIndex, obxIndex));
		result.setRange(labHandler.getOBXReferenceRange(obrIndex, obxIndex));
		result.setResultStatus(labHandler.getOBXResultStatus(obrIndex, obxIndex));
		result.setUnits(labHandler.getOBXUnits(obrIndex, obxIndex));
		result.setValue(labHandler.getOBXResult(obrIndex, obxIndex));

		for(int i = 0; i < labHandler.getOBXCommentCount(obrIndex, obxIndex); i++)
		{
			result.addComment(labHandler.getOBXComment(obrIndex, obxIndex, i));
		}
		result.setAnnotation(getAnnotationNote(annotationMap, obrIndex, obxIndex));

		return result;
	}

	private EncounterNote getAnnotationNote(Map<String, CaseManagementNote> annotationMap, int obrIndex, int obxIndex)
	{
		CaseManagementNote note = annotationMap.get(obrIndex + "-" + obxIndex);
		if(note == null)
		{
			return null;
		}
		return encounterNoteDbToModelConverter.convert(note);
	}
}
