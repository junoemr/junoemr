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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ClinicalNotes;
import xml.cds.v5_0.DateTimeFullOrPartial;

@Component
public class CDSEncounterNoteImportMapper extends AbstractCDSNoteImportMapper<ClinicalNotes, EncounterNote>
{
	private static final Logger logger = Logger.getLogger(CDSEncounterNoteImportMapper.class);

	public CDSEncounterNoteImportMapper()
	{
		super();
	}

	@Override
	public EncounterNote importToJuno(ClinicalNotes importStructure)
	{
		EncounterNote note = new EncounterNote();
		String noteText = "";
		note.setObservationDate(coalescePartialDatesToDateTimeWithDefault("Encounter Note", toNullablePartialDateTime(importStructure.getEventDateTime())));

		// TODO how to choose the mrp provider when there are multiple providers or reviewers?
		if (!importStructure.getParticipatingProviders().isEmpty())
		{
			for(ClinicalNotes.ParticipatingProviders participatingProvider : importStructure.getParticipatingProviders())
			{
				Provider provider = new Provider();
				provider.setFirstName(participatingProvider.getName().getFirstName());
				provider.setLastName(participatingProvider.getName().getLastName());
				provider.setOhipNumber(participatingProvider.getOHIPPhysicianId());
				note.addEditor(provider);
			}

			// for now, first provider will be the MRP
			ClinicalNotes.ParticipatingProviders participatingProvider = importStructure.getParticipatingProviders().get(0);

			Provider provider = new Provider();
			provider.setFirstName(participatingProvider.getName().getFirstName());
			provider.setLastName(participatingProvider.getName().getLastName());
			provider.setOhipNumber(participatingProvider.getOHIPPhysicianId());
			note.setProvider(provider);

			if(importStructure.getParticipatingProviders().size() > 1)
			{
				logEvent("EncounterNote [" + note.getObservationDate() + "] has multiple editors. One was assigned as the MRP");
			}
		}

		if (!importStructure.getNoteReviewer().isEmpty())
		{
			// for now, first provider will be the reviewer
			ClinicalNotes.NoteReviewer noteReviewer = importStructure.getNoteReviewer().get(0);
			DateTimeFullOrPartial reviewDateTime = noteReviewer.getDateTimeNoteReviewed();

			Reviewer reviewer = new Reviewer();
			reviewer.setFirstName(noteReviewer.getName().getFirstName());
			reviewer.setLastName(noteReviewer.getName().getLastName());
			reviewer.setOhipNumber(noteReviewer.getOHIPPhysicianId());
			reviewer.setReviewDateTime(toNullablePartialDateTime(reviewDateTime));
			note.setSigningProvider(reviewer);

			if(importStructure.getNoteReviewer().size() > 1)
			{
				logEvent("EncounterNote [" + note.getObservationDate() + "] has multiple reviewers that could not be set");
			}
		}

		noteText += StringUtils.trimToEmpty(importStructure.getNoteType()) + "\n";
		noteText += StringUtils.trimToEmpty(importStructure.getMyClinicalNotesContent());

		noteText = StringUtils.trimToEmpty(noteText);
		if(noteText.isEmpty())
		{
			logEvent("EncounterNote [" + note.getObservationDate() + "] has no text value");
		}
		note.setNoteText(noteText);

		return note;
	}
}
