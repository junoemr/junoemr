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
package org.oscarehr.common.hl7.copd.mapper;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.dataMigration.service.ImporterExporterFactory;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.provider.model.ProviderData;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterNoteMapper extends AbstractMapper
{
	public EncounterNoteMapper(ZPD_ZTR message, int providerRep, ImporterExporterFactory.IMPORT_SOURCE importSource)
	{
		super(message, providerRep, importSource);
	}

	public int getNumEncounterNotes()
	{
		return provider.getZPVReps();
	}

	public List<CaseManagementNote> getEncounterNoteList() throws HL7Exception
	{
		int numNotes = getNumEncounterNotes();
		List<CaseManagementNote> encounterNoteList = new ArrayList<>(numNotes);
		for(int i=0; i< numNotes; i++)
		{
			if(!isMessageNote(i))
			{
				encounterNoteList.add(getEncounterNote(i));
			}
		}
		return encounterNoteList;
	}

	public CaseManagementNote getEncounterNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		String noteText = getEncounterNoteText(rep);
		if (CoPDImportService.IMPORT_SOURCE.MEDIPLAN.equals(importSource))
		{
			noteText = noteText.replace(" / ", "\n");
		}

		note.setNote(noteText);
		note.setObservationDate(getEncounterNoteContactDate(rep));
		note.setUpdateDate(getEncounterUpdatedDate(rep));

		return note;
	}

	protected String getEncounterNoteText(int rep) throws HL7Exception
	{
		String reasonText = StringUtils.trimToEmpty(getEncounterNoteReason(rep));
		String commentText = StringUtils.trimToEmpty(getEncounterNoteComment(rep));
		String signatureText = StringUtils.trimToEmpty(getEncounterNoteSignature(rep));

		String text = "";

		if(!reasonText.isEmpty())
		{
			text += reasonText + "\n";
		}

		if(!commentText.isEmpty())
		{
			text += "\n" + commentText + "\n";
		}

		if(!signatureText.isEmpty() && !signatureText.equals("|"))
		{
			Date noteDate = getEncounterUpdatedDate(rep);
			String dateStr = ConversionUtils.toDateString(noteDate, "dd-MMM-yyyy HH:mm");
			signatureText = "[Signed on " + dateStr + " by " + signatureText.replaceAll("\\|", " ") + "]";
			text += signatureText;
		}

		return StringUtils.trimToEmpty(text.replaceAll("~crlf~", "\n"));
	}

	public ProviderData getSigningProvider(int rep) throws HL7Exception
	{
		return null;
	}
	public ProviderData getCreatingProvider(int rep) throws HL7Exception
	{
		return null;
	}

	public Date getEncounterNoteContactDate(int rep) throws HL7Exception
	{
		return getNullableDateTime(provider.getZPV(rep)
				.getZpv2_contactDate().getTs1_TimeOfAnEvent().getValue());
	}

	public Date getEncounterUpdatedDate(int rep) throws HL7Exception
	{
		return getEncounterNoteContactDate(rep);
	}

	public String getEncounterNoteReason(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv3_contactReason().getValue());
	}

	public String getEncounterNoteComment(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv4_comment().getValue());
	}

	public String getEncounterNoteSignature(int rep) throws HL7Exception
	{
		return StringUtils.trimToNull(provider.getZPV(rep).getZpv5_commentSignature().getValue());
	}

	/** messages may be sent as encounter notes. Here we try to separate them out. */
	public boolean isMessageNote(int rep) throws HL7Exception
	{
		return false;
	}
}
