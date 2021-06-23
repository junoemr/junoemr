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
package org.oscarehr.dataMigration.converter.out.note;

import org.oscarehr.dataMigration.model.common.PartialDate;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import static org.oscarehr.encounterNote.model.CaseManagementNoteExt.LIFESTAGE;
import static org.oscarehr.encounterNote.model.CaseManagementNoteExt.PROBLEMDESC;
import static org.oscarehr.encounterNote.model.CaseManagementNoteExt.PROBLEMSTATUS;
import static org.oscarehr.encounterNote.model.CaseManagementNoteExt.RESOLUTIONDATE;
import static org.oscarehr.encounterNote.model.CaseManagementNoteExt.STARTDATE;

@Component
public class ConcernNoteDbToModelConverter extends
		BaseNoteDbToModelConverter<org.oscarehr.dataMigration.model.encounterNote.ConcernNote>
{

	@Override
	public org.oscarehr.dataMigration.model.encounterNote.ConcernNote subConvert(
			CaseManagementNote input,
			org.oscarehr.dataMigration.model.encounterNote.ConcernNote exportNote)
	{
		exportNote.setAnnotation(getLinkedAnnotation(input));

		for(CaseManagementNoteExt ext : input.getNoteExtensionList())
		{
			if(ext.getKey().equals(STARTDATE))
			{
				exportNote.setStartDate(PartialDate.from(ConversionUtils.toNullableLocalDate(ext.getDateValue()), getExtPartialDate(ext.getId())));
			}
			if(ext.getKey().equals(RESOLUTIONDATE))
			{
				exportNote.setResolutionDate(PartialDate.from(ConversionUtils.toNullableLocalDate(ext.getDateValue()), getExtPartialDate(ext.getId())));
			}
			if(ext.getKey().equals(PROBLEMSTATUS))
			{
				exportNote.setProblemStatus(ext.getValue());
			}
			if(ext.getKey().equals(PROBLEMDESC))
			{
				exportNote.setProblemDescription(ext.getValue());
			}
			if(ext.getKey().equals(LIFESTAGE))
			{
				exportNote.setLifeStage(ext.getValue());
			}
		}
		return exportNote;
	}

	@Override
	public org.oscarehr.dataMigration.model.encounterNote.ConcernNote getNewNoteObject()
	{
		return new org.oscarehr.dataMigration.model.encounterNote.ConcernNote();
	}
}
