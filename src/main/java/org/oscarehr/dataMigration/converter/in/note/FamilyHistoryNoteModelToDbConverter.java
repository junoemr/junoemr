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
package org.oscarehr.dataMigration.converter.in.note;

import org.oscarehr.dataMigration.model.encounterNote.FamilyHistoryNote;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

@Component
public class FamilyHistoryNoteModelToDbConverter extends BaseNoteModelToDbConverter<FamilyHistoryNote>
{
	@Override
	public CaseManagementNote subConvert(
			FamilyHistoryNote input,
			CaseManagementNote dbNote)
	{
		if(input.getStartDate() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.STARTDATE, ConversionUtils.toNullableLegacyDate(input.getStartDate())));
		}
		if(input.getResolutionDate() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.RESOLUTIONDATE, ConversionUtils.toNullableLegacyDate(input.getResolutionDate())));
		}
		if(input.getAgeAtOnset() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.AGEATONSET, String.valueOf(input.getAgeAtOnset())));
		}
		if(input.getRelationship() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.RELATIONSHIP, String.valueOf(input.getRelationship())));
		}
		if(input.getTreatment() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.TREATMENT, String.valueOf(input.getTreatment())));
		}
		if(input.getLifeStage() != null)
		{
			dbNote.addExtension(getExt(dbNote, CaseManagementNoteExt.LIFESTAGE, input.getLifeStage()));
		}

		return dbNote;
	}
}
