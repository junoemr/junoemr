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

import org.oscarehr.dataMigration.model.encounterNote.ConcernNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ProblemList;

@Component
public class CDSProblemNoteExportMapper extends AbstractCDSNoteExportMapper<ProblemList, ConcernNote>
{
	public CDSProblemNoteExportMapper()
	{
		super();
	}

	@Override
	public ProblemList exportFromJuno(ConcernNote exportStructure)
	{
		ProblemList problemList = objectFactory.createProblemList();

		problemList.setProblemDiagnosisDescription(exportStructure.getNoteText());
		problemList.setDiagnosisCode(null); // not available when exporting from notes
		problemList.setProblemDescription(exportStructure.getProblemDescription());
		problemList.setProblemStatus(exportStructure.getProblemStatus());
		problemList.setOnsetDate(toNullableDateFullOrPartial(exportStructure.getStartDate(), exportStructure.getObservationDate().toLocalDate()));
		problemList.setLifeStage(getLifeStage(exportStructure.getLifeStage()));
		problemList.setResolutionDate(toNullableDateFullOrPartial(exportStructure.getResolutionDate()));
		problemList.setNotes(exportStructure.getAnnotation());

		return problemList;
	}
}
