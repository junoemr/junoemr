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
import org.oscarehr.dataMigration.model.encounterNote.FamilyHistoryNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.FamilyHistory;

@Component
public class CDSFamilyHistoryImportMapper extends AbstractCDSNoteImportMapper<FamilyHistory, FamilyHistoryNote>
{
	public CDSFamilyHistoryImportMapper()
	{
		super();
	}

	@Override
	public FamilyHistoryNote importToJuno(FamilyHistory importStructure)
	{
		FamilyHistoryNote note = new FamilyHistoryNote();

		note.setObservationDate(coalescePartialDatesToDateTimeWithDefault("Family History Note", toNullablePartialDate(importStructure.getStartDate())));
		note.setStartDate(toNullablePartialDate(importStructure.getStartDate()));
		note.setAgeAtOnset(getAgeAtOnset(importStructure.getAgeAtOnset()));
		note.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		note.setTreatment(importStructure.getTreatment());
		note.setRelationship(importStructure.getRelationship());

		String noteText = StringUtils.trimToEmpty(generateNoteText(importStructure));

		if(noteText.isEmpty())
		{
			logEvent("FamilyHistoryNote [" + note.getObservationDate() + "] has no text value");
		}
		note.setNoteText(noteText);

		note.setAnnotation(generateNoteText(importStructure));
		note.setAnnotation(StringUtils.trimToEmpty(importStructure.getNotes()));

		note.setResidualInfo(importAllResidualInfo(importStructure.getResidualInfo()));

		return note;
	}

	protected String generateNoteText(FamilyHistory importStructure)
	{
		String note = "";
		if (StringUtils.trimToNull(importStructure.getProblemDiagnosisProcedureDescription()) != null)
		{
			note += "ProblemDiagnosisProcedureDescription: " + importStructure.getProblemDiagnosisProcedureDescription() + "\n";
		}
		if (importStructure.getDiagnosisProcedureCode() != null)
		{
			note += "\nStandard Coding System: " + importStructure.getDiagnosisProcedureCode().getStandardCodingSystem() + "\n";
			note += "Standard Code: " + importStructure.getDiagnosisProcedureCode().getStandardCode() + "\n";
			note += "Standard Coding Description: " + importStructure.getDiagnosisProcedureCode().getStandardCodeDescription() + "\n";
		}
		return StringUtils.trimToEmpty(note);
	}
}
