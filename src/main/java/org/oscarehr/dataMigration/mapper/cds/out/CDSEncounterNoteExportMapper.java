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

import org.oscarehr.dataMigration.model.encounterNote.EncounterNote;
import org.oscarehr.dataMigration.model.provider.Provider;
import org.oscarehr.dataMigration.model.provider.Reviewer;
import org.springframework.stereotype.Component;
import xml.cds.v5_0.ClinicalNotes;

import java.util.ArrayList;
import java.util.List;

@Component
public class CDSEncounterNoteExportMapper extends AbstractCDSNoteExportMapper<ClinicalNotes, EncounterNote>
{
	public CDSEncounterNoteExportMapper()
	{
		super();
	}

	@Override
	public ClinicalNotes exportFromJuno(EncounterNote exportStructure)
	{
		ClinicalNotes clinicalNotes = objectFactory.createClinicalNotes();

		clinicalNotes.setNoteType("Chart Note");
		clinicalNotes.setMyClinicalNotesContent(exportStructure.getNoteText());
		clinicalNotes.setEventDateTime(toNullableDateTimeFullOrPartial(exportStructure.getObservationDate()));
		clinicalNotes.getParticipatingProviders().addAll(getNoteProviders(exportStructure));
		clinicalNotes.getNoteReviewer().addAll(getNoteReviewers(exportStructure));

		return clinicalNotes;
	}

	protected List<ClinicalNotes.ParticipatingProviders> getNoteProviders(EncounterNote exportStructure)
	{
		List<Provider> editors = exportStructure.getEditors();
		List<ClinicalNotes.ParticipatingProviders> providers = new ArrayList<>(editors.size());

		for(Provider provider : editors)
		{
			providers.add(getNoteProvider(exportStructure, provider));
		}

		return providers;
	}
	protected ClinicalNotes.ParticipatingProviders getNoteProvider(EncounterNote exportStructure, Provider provider)
	{
		ClinicalNotes.ParticipatingProviders participatingProvider = null;
		if(provider != null)
		{
			participatingProvider = objectFactory.createClinicalNotesParticipatingProviders();
			participatingProvider.setName(toPersonNameSimple(provider));
			participatingProvider.setDateTimeNoteCreated(toNullableDateTimeFullOrPartial(exportStructure.getObservationDate()));
			participatingProvider.setOHIPPhysicianId(provider.getOhipNumber());
		}
		return participatingProvider;
	}

	protected List<ClinicalNotes.NoteReviewer> getNoteReviewers(EncounterNote exportStructure)
	{
		List<ClinicalNotes.NoteReviewer> reviewers = new ArrayList<>(1);

		Reviewer signingProvider = exportStructure.getSigningProvider();
		if(signingProvider != null)
		{
			ClinicalNotes.NoteReviewer reviewer = objectFactory.createClinicalNotesNoteReviewer();
			reviewer.setName(toPersonNameSimple(signingProvider));
			reviewer.setDateTimeNoteReviewed(toNullableDateTimeFullOrPartial(signingProvider.getReviewDateTime()));
			reviewer.setOHIPPhysicianId(signingProvider.getOhipNumber());
			reviewers.add(reviewer);
		}
		return reviewers;
	}

}
