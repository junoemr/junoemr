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

package org.oscarehr.casemgmt.dto;

import java.util.ArrayList;
import java.util.List;

public class EncounterNotes
{
	private List<EncounterSectionNote> encounterSectionNotes;
	private Integer offset;
	private Integer limit;
	private int noteCount;

	public EncounterNotes()
	{
	}

	public EncounterNotes(List<EncounterSectionNote> encounterSectionNotes, Integer offset, Integer limit, int noteCount)
	{
		this.encounterSectionNotes = encounterSectionNotes;
		this.offset = offset;
		this.limit = limit;
		this.noteCount = noteCount;
	}

	public static EncounterNotes noNotes()
	{
		return new EncounterNotes(new ArrayList<>(), null, null, 0);
	}

	public static EncounterNotes limitedEncounterNotes(List<EncounterSectionNote> noteList, Integer offset, Integer limit)
	{
		EncounterNotes notes = new EncounterNotes();
		notes.setOffset(offset);
		notes.setLimit(limit);
		notes.setNoteCount(noteList.size());

		// Limit the whole list every time.  Seems quite complicated to convert the logic to sql.
		if(limit != null && offset != null)
		{
			int upperLimit = offset + limit;
			if(upperLimit > noteList.size())
			{
				upperLimit = noteList.size();
			}
			notes.setEncounterSectionNotes(noteList.subList(offset, upperLimit));
		}
		else
		{
			notes.setEncounterSectionNotes(noteList);
		}

		return notes;
	}

	public List<EncounterSectionNote> getEncounterSectionNotes()
	{
		return encounterSectionNotes;
	}

	public void setEncounterSectionNotes(List<EncounterSectionNote> encounterSectionNotes)
	{
		this.encounterSectionNotes = encounterSectionNotes;
	}

	public Integer getOffset()
	{
		return offset;
	}

	public void setOffset(Integer offset)
	{
		this.offset = offset;
	}

	public Integer getLimit()
	{
		return limit;
	}

	public void setLimit(Integer limit)
	{
		this.limit = limit;
	}

	public int getNoteCount()
	{
		return noteCount;
	}

	public void setNoteCount(int noteCount)
	{
		this.noteCount = noteCount;
	}
}
