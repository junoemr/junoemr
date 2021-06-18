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
package org.oscarehr.common.hl7.copd.mapper.wolf;

import ca.uhn.hl7v2.HL7Exception;
import org.apache.commons.lang.StringUtils;
import org.olap4j.impl.ArrayMap;
import org.oscarehr.common.hl7.copd.mapper.EncounterNoteMapper;
import org.oscarehr.common.hl7.copd.model.v24.message.ZPD_ZTR;
import org.oscarehr.dataMigration.service.CoPDImportService;
import org.oscarehr.provider.model.ProviderData;

import java.util.Date;
import java.util.Map;

public class EncounterNoteMapperWolf extends EncounterNoteMapper
{
	protected static final String NOTE_KEY_LAST_EDIT_DATE = "LastEdited";
	protected static final String NOTE_KEY_CREATED_DATE = "Created";
	protected static final String NOTE_KEY_LAST_EDITOR = "LastEditedBy";
	protected static final String NOTE_KEY_CREATOR = "CreatedBy";
	protected static final String NOTE_KEY_TEXT = "NoteText";

	public EncounterNoteMapperWolf(ZPD_ZTR message, int providerRep)
	{
		super(message, providerRep, CoPDImportService.IMPORT_SOURCE.WOLF);
	}

	@Override
	/* Wolf wants us to use the parsed provider name in the notes as the note creator and signature */
	public ProviderData getSigningProvider(int rep) throws HL7Exception
	{
		String noteProviderStr = getEncounterNoteSignature(rep);
		ProviderData provider = getWOLFParsedProviderInfo(noteProviderStr, "ZPV.5");
		if(provider == null)
		{
			provider = getEditingProvider(rep);
		}
		return provider;
	}

	@Override
	/* Wolf wants us to use the parsed provider name in the notes as the note creator and signature */
	public ProviderData getCreatingProvider(int rep) throws HL7Exception
	{
		Map<String, String> noteMap = parseNoteToMap(rep);
		String providerName = noteMap.get(NOTE_KEY_CREATOR);
		ProviderData provider = getWOLFParsedProviderInfo(providerName, "ZPV.4");
		if(provider == null)
		{
			provider = getSigningProvider(rep);
		}
		return provider;
	}

	/** Wolf sends their provider messages in encounter notes. Here we try to separate them out. */
	@Override
	public boolean isMessageNote(int rep) throws HL7Exception
	{
		String noteText = getEncounterNoteComment(rep);
		return (noteText != null && noteText.startsWith("Message:"));
	}

	@Override
	public String getEncounterNoteComment(int rep) throws HL7Exception
	{
		Map<String, String> noteMap = parseNoteToMap(rep);
		return noteMap.get(NOTE_KEY_TEXT);
	}

	@Override
	public Date getEncounterUpdatedDate(int rep) throws HL7Exception
	{
		Map<String, String> noteMap = parseNoteToMap(rep);
		return getNullableDate(noteMap.get(NOTE_KEY_CREATED_DATE));
	}

	// wolf may send this in the note text, separate from the signing / creating provider
	private ProviderData getEditingProvider(int rep) throws HL7Exception
	{
		Map<String, String> noteMap = parseNoteToMap(rep);
		String providerName = noteMap.get(NOTE_KEY_LAST_EDITOR);
		return getWOLFParsedProviderInfo(providerName, "ZPV.4");
	}

	/**
	 * Wolf has sent us note data in the form of
	 * {{note_text}}$LastEdited:{{date}}$Created:{{date}}$$LastEditedBy:{{providerName}}$$CreatedBy:{{providerName}}$
	 * in this case we can extract the data to a map. however just in case there is a delimiter in the original note, any section that
	 * doesn't start with one of these specific keys is kept as part of the main note text
	 * @param rep - the segment rep
	 * @return map of not sectionals
	 * @throws HL7Exception - hopefully not
	 */
	private Map<String, String> parseNoteToMap(int rep) throws HL7Exception
	{
		String fullComment = StringUtils.trimToEmpty(provider.getZPV(rep).getZpv4_comment().getValue());
		String[] commentParts = fullComment.split("\\$");
		Map<String, String> noteMap = new ArrayMap<>();

		StringBuilder noteTextBuilder = new StringBuilder();
		String subSeparator = ":";
		for(String commentPart: commentParts)
		{
			commentPart = StringUtils.trimToEmpty(commentPart);
			if(commentPart.startsWith(NOTE_KEY_CREATED_DATE + subSeparator))
			{
				noteMap.put(NOTE_KEY_CREATED_DATE, commentPart.split(subSeparator)[1]);
			}
			else if(commentPart.startsWith(NOTE_KEY_LAST_EDIT_DATE + subSeparator))
			{
				noteMap.put(NOTE_KEY_LAST_EDIT_DATE, commentPart.split(subSeparator)[1]);
			}
			else if(commentPart.startsWith(NOTE_KEY_CREATOR + subSeparator))
			{
				noteMap.put(NOTE_KEY_CREATOR, commentPart.split(subSeparator)[1]);
			}
			else if(commentPart.startsWith(NOTE_KEY_LAST_EDITOR + subSeparator))
			{
				noteMap.put(NOTE_KEY_LAST_EDITOR, commentPart.split(subSeparator)[1]);
			}
			else
			{
				noteTextBuilder.append(commentPart);
			}
		}
		noteMap.put(NOTE_KEY_TEXT, noteTextBuilder.toString());

		return noteMap;
	}
}
