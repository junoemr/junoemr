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
package org.oscarehr.demographicImport.converter.out;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.dao.ProviderLabRoutingDao;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.demographicImport.converter.out.ProviderDbToModelConverter;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.oscarehr.demographicImport.model.provider.Reviewer;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class LabDbToModelConverter extends
		AbstractModelConverter<Hl7TextInfo, org.oscarehr.demographicImport.model.lab.Lab>
{
	@Autowired
	private ProviderLabRoutingDao providerLabRoutingDao;

	@Autowired
	private ProviderDbToModelConverter providerConverter;

	@Autowired
	private ProviderDataDao providerDao;


	@Override
	public org.oscarehr.demographicImport.model.lab.Lab convert(Hl7TextInfo hl7TextInfo)
	{
		if(hl7TextInfo == null)
		{
			return null;
		}
		Hl7TextMessage hl7TextMessage = hl7TextInfo.getHl7TextMessage();
		MessageHandler labHandler = Factory.getHandler(hl7TextInfo.getLabNumber());
		Lab exportLab = new Lab();

		exportLab.setAccessionNumber(labHandler.getAccessionNum());
		exportLab.setVersion(labHandler.getFillerOrderNumber());
		exportLab.setMessageDateTime(ConversionUtils.toLocalDateTime(labHandler.getMsgDate()));
		exportLab.setEmrReceivedDateTime(ConversionUtils.toLocalDateTime(hl7TextMessage.getCreated()));
		exportLab.setSendingFacility(labHandler.getPatientLocation());

		for(int i = 0; i < labHandler.getOBRCount(); i++)
		{
			exportLab.addObservation(getObservations(labHandler, i));
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
			ProviderData provider = providerDao.find(providerLabRouting.getProviderNo());
			if(provider != null)
			{
				Reviewer reviewer = Reviewer.fromProvider(providerConverter.convert(provider));
				reviewer.setReviewDateTime(ConversionUtils.toLocalDateTime(providerLabRouting.getTimestamp()));
				reviewers.add(reviewer);
			}
		}
		return reviewers;
	}

	private LabObservation getObservations(MessageHandler labHandler, int obrIndex)
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
			observation.addResult(getObservationResult(labHandler, obrIndex, i));
		}

		for(int i = 0; i < labHandler.getOBRCommentCount(obrIndex); i++)
		{
			observation.addComment(labHandler.getOBRComment(obrIndex, i));
		}

		return observation;
	}


	private LabObservationResult getObservationResult(MessageHandler labHandler, int obrIndex, int obxIndex)
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
		result.setNotes(labHandler.getNteForOBX(obrIndex, obxIndex));

		for(int i = 0; i < labHandler.getOBXCommentCount(obrIndex, obxIndex); i++)
		{
			result.addComment(labHandler.getOBXComment(obrIndex, obxIndex, i));
		}

		return result;
	}
}
