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

import org.oscarehr.dataMigration.converter.in.BaseModelToDbConverter;
import org.oscarehr.dataMigration.model.encounterNote.BaseNote;
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.oscarehr.encounterNote.model.CaseManagementNoteExt;
import org.springframework.stereotype.Component;
import oscar.util.ConversionUtils;

import java.util.Date;

@Component
public abstract class BaseNoteModelToDbConverter<N extends BaseNote> extends
		BaseModelToDbConverter<N, CaseManagementNote>
{

	@Override
	public CaseManagementNote convert(N input)
	{
		if(input == null)
		{
			return null;
		}

		CaseManagementNote dbNote = new CaseManagementNote();

		String id = input.getId();
		if(id != null)
		{
			dbNote.setNoteId(Long.parseLong(id));
		}
		dbNote.setNote(input.getNoteText());
		dbNote.setUuid(input.getRevisionId());
		dbNote.setObservationDate(ConversionUtils.toNullableLegacyDateTime(input.getObservationDate()));
		dbNote.setProvider(findOrCreateProviderRecord(input.getProvider(), false));
		dbNote.setSigningProvider(findOrCreateProviderRecord(input.getSigningProvider(), true));
		dbNote.setProgramNo(input.getProgramId());
		dbNote.setReporterCaisiRole(input.getRoleId());

		dbNote.setSigned(true); // always sign the inbound chart notes
		if(dbNote.getSigningProvider() == null)
		{
			dbNote.setSigningProvider(dbNote.getProvider());
		}

		return subConvert(input, dbNote);
	}

	public abstract CaseManagementNote subConvert(N input, CaseManagementNote dbNote);

	protected CaseManagementNoteExt getExt(CaseManagementNote dbNote, String key, String value)
	{
		CaseManagementNoteExt ext = new CaseManagementNoteExt();
		ext.setKey(key);
		ext.setValue(value);
		ext.setNote(dbNote);
		return ext;
	}
	protected CaseManagementNoteExt getExt(CaseManagementNote dbNote, String key, Date date)
	{
		CaseManagementNoteExt ext = new CaseManagementNoteExt();
		ext.setKey(key);
		ext.setDateValue(date);
		ext.setNote(dbNote);
		return ext;
	}
}
