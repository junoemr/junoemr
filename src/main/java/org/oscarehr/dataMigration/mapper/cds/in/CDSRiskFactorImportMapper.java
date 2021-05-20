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

import org.apache.commons.lang.StringUtils;
import org.oscarehr.dataMigration.model.encounterNote.RiskFactorNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.RiskFactors;

@Component
public class CDSRiskFactorImportMapper extends AbstractCDSNoteImportMapper<RiskFactors, RiskFactorNote>
{
	public CDSRiskFactorImportMapper()
	{
		super();
	}

	@Override
	public RiskFactorNote importToJuno(RiskFactors importStructure)
	{
		RiskFactorNote note = new RiskFactorNote();

		note.setExposureDetails(importStructure.getExposureDetails());
		note.setAgeAtOnset(getAgeAtOnset(importStructure.getAgeOfOnset()));
		note.setStartDate(toNullablePartialDate(importStructure.getStartDate()));
		note.setResolutionDate(toNullablePartialDate(importStructure.getEndDate()));
		note.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		note.setAnnotation(importStructure.getNotes());
		note.setObservationDate(coalescePartialDatesToDateTimeWithDefault("Risk Factor Note", note.getStartDate(), note.getResolutionDate()));

		String noteText = StringUtils.trimToEmpty(importStructure.getRiskFactor());
		if(noteText.isEmpty())
		{
			logEvent("Risk Factor [" + note.getObservationDate() + "] has no text value");
		}
		note.setNoteText(noteText);

		return note;
	}
}
