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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.common.xml.cds.v5_0.model.FamilyHistory;
import org.oscarehr.demographicImport.model.encounterNote.FamilyHistoryNote;
import org.springframework.stereotype.Component;

@Component
public class CDSFamilyHistoryImportMapper extends AbstractCDSImportMapper<FamilyHistory, FamilyHistoryNote>
{
	private static final Logger logger = Logger.getLogger(CDSFamilyHistoryImportMapper.class);

	public CDSFamilyHistoryImportMapper()
	{
		super();
	}

	@Override
	public FamilyHistoryNote importToJuno(FamilyHistory importStructure)
	{
		FamilyHistoryNote note = new FamilyHistoryNote();

		note.setObservationDate(toNullableLocalDateTime(importStructure.getStartDate()));
		note.setStartDate(toNullablePartialDate(importStructure.getStartDate()));
		note.setAgeAtOnset(getAgeAtOnset(importStructure.getAgeAtOnset()));
		note.setLifeStage(getLifeStage(importStructure.getLifeStage()));
		note.setTreatment(importStructure.getProblemDiagnosisProcedureDescription());
		note.setRelationship(importStructure.getRelationship());

		String noteText = StringUtils.trimToEmpty(
				StringUtils.trimToEmpty(importStructure.getProblemDiagnosisProcedureDescription()) + "\n" +
						StringUtils.trimToEmpty(importStructure.getNotes())
		);
		note.setNoteText(noteText);

		if(note.getNoteText() == null || note.getNoteText().isEmpty())
		{
			logger.warn("FamilyHistoryNote has no text value");
			note.setNoteText("");
		}

		return note;
	}
}
