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
import org.oscarehr.encounterNote.model.CaseManagementNote;
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

	public EncounterNoteMapper()
	{
		message = null;
		provider = null;
	}
	public EncounterNoteMapper(ZPD_ZTR message, int providerRep)
	{
		this.message = message;
		this.provider = message.getPATIENT().getPROVIDER(providerRep);
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

		String commentText = StringUtils.trimToEmpty(getEncounterNoteReason(rep));
		String reasonText = StringUtils.trimToEmpty(getEncounterNoteComment(rep));
		String noteText = StringUtils.trimToEmpty( commentText + "\n\n" + reasonText).replaceAll("~crlf~", "\n");

		note.setNote(noteText);
		note.setObservationDate(getEncounterNoteContactDate(rep));
		note.setUpdateDate(getEncounterNoteContactDate(rep));

		return note;
	}

	public String getEncounterNoteComment(int rep) throws HL7Exception
	{
		return provider.getZPV(rep).getZpv4_comment().getValue();
	}

	public String getEncounterNoteReason(int rep) throws HL7Exception
	{
		return provider.getZPV(rep).getZpv3_contactReason().getValue();
	}

	public Date getEncounterNoteContactDate(int rep) throws HL7Exception
	{
		return ConversionUtils.fromDateString(provider.getZPV(rep).getZpv2_contactDate().getTs1_TimeOfAnEvent().getValue(), "yyyyMMdd");
	}
}
