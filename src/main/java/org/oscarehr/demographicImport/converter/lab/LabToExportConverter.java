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
package org.oscarehr.demographicImport.converter.lab;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.conversion.AbstractModelConverter;
import org.oscarehr.common.model.Hl7TextInfo;
import org.oscarehr.common.model.Hl7TextMessage;
import org.oscarehr.demographicImport.model.lab.Lab;
import org.oscarehr.demographicImport.model.lab.LabObservation;
import org.oscarehr.demographicImport.model.lab.LabObservationResult;
import org.springframework.stereotype.Component;
import oscar.oscarLab.ca.all.parsers.Factory;
import oscar.oscarLab.ca.all.parsers.MessageHandler;
import oscar.util.ConversionUtils;

@Component
public class LabToExportConverter extends
		AbstractModelConverter<Hl7TextInfo, org.oscarehr.demographicImport.model.lab.Lab>
{
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
		exportLab.setReceivedDateTime(ConversionUtils.toLocalDateTime(hl7TextMessage.getCreated()));

		for(int i=0; i< labHandler.getOBRCount(); i++)
		{
			exportLab.addObservation(getObservations(labHandler, i));
		}

		return exportLab;
	}

	private LabObservation getObservations(MessageHandler labHandler, int obrIndex)
	{
		LabObservation observation = new LabObservation();
		observation.setName(labHandler.getOBRName(obrIndex));
		observation.setCode(labHandler.getOBRProcedureCode(obrIndex));
		observation.setObservationDateTime(
				ConversionUtils.toNullableLocalDateTime(StringUtils.trimToNull(labHandler.getReportDate(obrIndex))));
		observation.setRequestDateTime(
				ConversionUtils.toNullableLocalDateTime(StringUtils.trimToNull(labHandler.getRequestDate(obrIndex))));

		for(int i=0; i< labHandler.getOBXCount(obrIndex); i++)
		{
			observation.addResult(getObservationResult(labHandler, obrIndex, i));
		}
		return observation;
	}


	private LabObservationResult getObservationResult(MessageHandler labHandler, int obrIndex, int obxIndex)
	{
		LabObservationResult result = new LabObservationResult();

		result.setAbnormal(labHandler.isOBXAbnormal(obrIndex, obxIndex));

		String collectionDateTime = StringUtils.trimToNull(labHandler.getSpecimenCollectionDateTime(obrIndex, obxIndex));
		if(collectionDateTime != null)
		{
			result.setSpecimenCollectionDateTime(ConversionUtils.toNullableLocalDateTime(collectionDateTime));
		}

		String receivedDateTime = StringUtils.trimToNull(labHandler.getSpecimenCollectionDateTime(obrIndex, obxIndex));
		if(receivedDateTime != null)
		{
			result.setSpecimenReceivedDateTime(ConversionUtils.toNullableLocalDateTime(receivedDateTime));
		}
		result.setRange(labHandler.getOBXReferenceRange(obrIndex, obxIndex));
		result.setResultStatus(labHandler.getOBXResultStatus(obrIndex, obxIndex));
		result.setUnits(labHandler.getOBXUnits(obrIndex, obxIndex));
		result.setValue(labHandler.getOBXResult(obrIndex, obxIndex));

		return result;
	}
}
