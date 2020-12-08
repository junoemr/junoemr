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
package org.oscarehr.demographicImport.mapper.cds.in;

import org.oscarehr.common.xml.cds.v5_0.model.ProblemList;
import org.oscarehr.common.xml.cds.v5_0.model.StandardCoding;
import org.oscarehr.demographicImport.model.encounterNote.ConcernNote;

public class CDSProblemImportMapper extends AbstractCDSImportMapper<ProblemList, ConcernNote>
{
	public CDSProblemImportMapper()
	{
		super();
	}

	@Override
	public ConcernNote importToJuno(ProblemList importStructure)
	{
		ConcernNote note = new ConcernNote();

		note.setNoteText(getNoteText(importStructure));
		note.setProblemDescription(importStructure.getProblemDescription());
		note.setProblemStatus(importStructure.getProblemStatus());
		note.setStartDate(toNullablePartialDate(importStructure.getOnsetDate()));
		note.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		note.setResolutionDate(toNullablePartialDate(importStructure.getResolutionDate()));
		note.setAnnotation(importStructure.getNotes());

		return note;
	}

	protected String getNoteText(ProblemList importStructure)
	{
		String noteText;
		String description = importStructure.getProblemDiagnosisDescription();
		StandardCoding diagnosisCode = importStructure.getDiagnosisCode();
		if(diagnosisCode != null)
		{
			String codeDescription = diagnosisCode.getStandardCodeDescription();
			noteText = "Diagnosis Code [" + diagnosisCode.getStandardCodingSystem() + "]: " + diagnosisCode.getStandardCode()
					+ "\n" + codeDescription;

			// sometimes the two descriptions will be the same, according to spec. in that case no need to duplicate it
			if(description != null && !description.equals(codeDescription))
			{
				noteText += "\n" + description;
			}
		}
		else if(description != null)
		{
			noteText = description;
		}
		else
		{
			noteText = "Import: No description available";
		}
		return  noteText;
	}
}
