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
import org.apache.log4j.Logger;
import org.oscarehr.common.hl7.copd.model.v24.group.ZPD_ZTR_PROVIDER;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.demographicImport.service.CoPDImportService;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.MiscUtils;
import oscar.util.ConversionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EncounterNoteMapper
{
	private static final Logger logger = MiscUtils.getLogger();
	private final ZPD_ZTR message;
	private final ZPD_ZTR_PROVIDER provider;

	private final CoPDImportService.IMPORT_SOURCE importSource;

	public EncounterNoteMapper()
	{
		message = null;
		provider = null;
		importSource = CoPDImportService.IMPORT_SOURCE.UNKNOWN;
	}
	public EncounterNoteMapper(ZPD_ZTR message, int providerRep, CoPDImportService.IMPORT_SOURCE importSource)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
		this.importSource = importSource;
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
			encounterNoteList.add(getEncounterNote(i));
		}
		return encounterNoteList;
	}

	public CaseManagementNote getEncounterNote(int rep) throws HL7Exception
	{
		CaseManagementNote note = new CaseManagementNote();

		note.setNote(getEncounterNoteText(rep));
		note.setObservationDate(getEncounterNoteContactDate(rep));
		note.setUpdateDate(getEncounterNoteContactDate(rep));

		return note;
	}

	private String getEncounterNoteText(int rep) throws HL7Exception
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

		if(!signatureText.isEmpty())
		{
			Date noteDate = getEncounterNoteContactDate(rep);
			String dateStr = ConversionUtils.toDateString(noteDate, "dd-MMM-yyyy HH:mm");
			signatureText = "[Signed on " + dateStr + " by " + signatureText + "]";
			text += signatureText;
		}

		return StringUtils.trimToEmpty(text.replaceAll("~crlf~", "\n"));
	}

	public ProviderData getSigningProvider(int rep) throws HL7Exception
	{
		ProviderData signingProvider = null;

		/* Wolf puts provider names for a note in the form of 'first last' in the comment signature.
		 * Here we attempt to parse the names out and match them to a provider record */
		if(importSource.equals(CoPDImportService.IMPORT_SOURCE.WOLF))
		{
			String noteProviderStr = getEncounterNoteSignature(rep);
			if(noteProviderStr != null && noteProviderStr.contains(" "))
			{
				// no idea how to handle a name with a space in it here.
				String[] providerNames = noteProviderStr.split(" ");
				if(providerNames.length > 2)
				{
					logger.error("Malformed provider name contains too many spaces: '" + noteProviderStr + "'");
				}
				signingProvider = new ProviderData();
				signingProvider.setFirstName(providerNames[0]);
				signingProvider.setLastName(providerNames[1]);
			}
			else
			{
				/* Wolf exports their internal communication notes sometimes as notes without associated provider information. */
				logger.debug("WOLF signing provider data is empty or malformed.");
			}
		}
		return signingProvider;
	}

	public Date getEncounterNoteContactDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPV(rep).getZpv2_contactDate().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
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
}
