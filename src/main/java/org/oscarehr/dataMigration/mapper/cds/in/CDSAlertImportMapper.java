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
import org.oscarehr.dataMigration.model.encounterNote.ReminderNote;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.AlertsAndSpecialNeeds;

@Component
public class CDSAlertImportMapper extends AbstractCDSNoteImportMapper<AlertsAndSpecialNeeds, ReminderNote>
{
	public CDSAlertImportMapper()
	{
		super();
	}

	@Override
	public ReminderNote importToJuno(AlertsAndSpecialNeeds importStructure)
	{
		ReminderNote reminderNote = new ReminderNote();
		reminderNote.setStartDate(toNullablePartialDate(importStructure.getDateActive()));
		reminderNote.setResolutionDate(toNullablePartialDate(importStructure.getEndDate()));
		reminderNote.setObservationDate(coalescePartialDatesToDateTimeWithDefault("Alert", reminderNote.getStartDate(), reminderNote.getResolutionDate()));
		reminderNote.setNoteText(StringUtils.trimToEmpty(importStructure.getAlertDescription()));

		if(reminderNote.getNoteText().isEmpty())
		{
			logEvent("Reminder Note [" + reminderNote.getObservationDate() + "] has no text value");
		}

		reminderNote.setAnnotation(StringUtils.trimToEmpty(importStructure.getNotes()));
		reminderNote.setResidualInfo(importAllResidualInfo(importStructure.getResidualInfo()));

		return reminderNote;
	}
}
