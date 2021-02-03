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
import org.oscarehr.common.xml.cds.v5_0.model.AlertsAndSpecialNeeds;
import org.oscarehr.demographicImport.model.encounterNote.ReminderNote;
import org.springframework.stereotype.Component;

@Component
public class CDSAlertImportMapper extends AbstractCDSNoteImportMapper<AlertsAndSpecialNeeds, ReminderNote>
{
	private static final Logger logger = Logger.getLogger(CDSAlertImportMapper.class);

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
		reminderNote.setObservationDate(coalescePartialDates(reminderNote.getStartDate(), reminderNote.getResolutionDate()));

		String noteText = StringUtils.trimToEmpty(
				StringUtils.trimToEmpty(importStructure.getAlertDescription()) + "\n" + StringUtils.trimToEmpty(importStructure.getNotes())
		);
		reminderNote.setNoteText(noteText);


		if(reminderNote.getNoteText() == null || reminderNote.getNoteText().isEmpty())
		{
			logger.warn("ReminderNote has no text value");
			reminderNote.setNoteText("");
		}

		return reminderNote;
	}
}
