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

import org.oscarehr.dataMigration.model.encounterNote.MedicalHistoryNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.PastHealth;

@Component
public class CDSPastHealthImportMapper extends AbstractCDSNoteImportMapper<PastHealth, MedicalHistoryNote>
{
	public CDSPastHealthImportMapper()
	{
		super();
	}

	@Override
	public MedicalHistoryNote importToJuno(PastHealth importStructure)
	{
		MedicalHistoryNote note = new MedicalHistoryNote();

		note.setNoteText(getDiagnosisNoteText(importStructure.getPastHealthProblemDescriptionOrProcedures(), importStructure.getDiagnosisProcedureCode()));
		note.setStartDate(toNullablePartialDate(importStructure.getOnsetOrEventDate()));
		note.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		note.setResolutionDate(toNullablePartialDate(importStructure.getResolvedDate()));
		note.setProcedureDate(toNullablePartialDate(importStructure.getProcedureDate()));
		note.setAnnotation(importStructure.getNotes());
		note.setTreatment(importStructure.getProblemStatus());
		note.setObservationDate(coalescePartialDatesToDateTimeWithDefault("Past Health Note", note.getStartDate(), note.getResolutionDate(), note.getProcedureDate()));

		return note;
	}
}
