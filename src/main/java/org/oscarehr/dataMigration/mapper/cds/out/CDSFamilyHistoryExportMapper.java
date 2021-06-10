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

import org.oscarehr.dataMigration.model.dx.DxCode;
import org.oscarehr.dataMigration.model.encounterNote.FamilyHistoryNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.FamilyHistory;
import xml.cds.v5_0.StandardCoding;

import java.util.List;

@Component
public class CDSFamilyHistoryExportMapper extends AbstractCDSNoteExportMapper<FamilyHistory, FamilyHistoryNote>
{
	public CDSFamilyHistoryExportMapper()
	{
		super();
	}

	@Override
	public FamilyHistory exportFromJuno(FamilyHistoryNote exportStructure)
	{
		FamilyHistory familyHistory = objectFactory.createFamilyHistory();

		// use start date field if we can, otherwise use the observation date
		familyHistory.setStartDate(toNullableDateFullOrPartial(exportStructure.getStartDate(), exportStructure.getObservationDate().toLocalDate()));
		familyHistory.setAgeAtOnset(getAgeAtOnset(exportStructure.getAgeAtOnset()));
		familyHistory.setLifeStage(getLifeStage(exportStructure.getLifeStage()));
		familyHistory.setProblemDiagnosisProcedureDescription(exportStructure.getNoteText());
		familyHistory.setDiagnosisProcedureCode(getDiagnosisProcedureCode(exportStructure));
		familyHistory.setTreatment(exportStructure.getTreatment());
		familyHistory.setRelationship(exportStructure.getRelationship());
		familyHistory.setNotes(exportStructure.getAnnotation());

		return familyHistory;
	}

	/**
	 * we can only export 1 attached code per note in the cds format, so we will just pick the first one
	 */
	protected StandardCoding getDiagnosisProcedureCode(FamilyHistoryNote exportStructure)
	{
		List<DxCode> dxCodes = exportStructure.getDxIssueCodes();
		if(dxCodes != null && !dxCodes.isEmpty())
		{
			return getStandardCoding(dxCodes.get(0));
		}
		return null;
	}
}
