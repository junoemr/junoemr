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
package org.oscarehr.dataMigration.mapper.cds.out;

import org.oscarehr.dataMigration.model.encounterNote.RiskFactorNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.RiskFactors;

@Component
public class CDSRiskFactorExportMapper extends AbstractCDSNoteExportMapper<RiskFactors, RiskFactorNote>
{
	public CDSRiskFactorExportMapper()
	{
		super();
	}

	@Override
	public RiskFactors exportFromJuno(RiskFactorNote exportStructure)
	{
		RiskFactors riskFactors = objectFactory.createRiskFactors();

		riskFactors.setRiskFactor(exportStructure.getNoteText());
		riskFactors.setExposureDetails(exportStructure.getExposureDetails());
		riskFactors.setAgeOfOnset(getAgeAtOnset(exportStructure.getAgeAtOnset()));
		riskFactors.setStartDate(toNullableDateFullOrPartial(exportStructure.getStartDate(), exportStructure.getObservationDate().toLocalDate()));
		riskFactors.setEndDate(toNullableDateFullOrPartial(exportStructure.getResolutionDate()));
		riskFactors.setLifeStage(getLifeStage(exportStructure.getLifeStage()));
		riskFactors.setNotes(exportStructure.getAnnotation());

		return riskFactors;
	}
}
