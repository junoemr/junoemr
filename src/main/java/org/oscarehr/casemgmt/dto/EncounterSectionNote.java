/*
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class EncounterSectionNote
{
	private Integer id;
	private String text;
	private String value;
	private String onClick;
	private String editors;
	private Integer revision;
	private LocalDateTime updateDate;
	private LocalDateTime observationDate;
	private String noteIssuesString;
	private String noteExtsString;
	private String colour;
	private String[] titleClasses;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getValue()
	{
		return value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	public String getOnClick()
	{
		return onClick;
	}

	public void setOnClick(String onClick)
	{
		this.onClick = onClick;
	}

	public String getEditors()
	{
		return editors;
	}

	public void setEditors(String editors)
	{
		this.editors = editors;
	}

	public Integer getRevision()
	{
		return revision;
	}

	public void setRevision(Integer revision)
	{
		this.revision = revision;
	}

	public LocalDateTime getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(LocalDateTime updateDate)
	{
		this.updateDate = updateDate;
	}

	public LocalDateTime getObservationDate()
	{
		return observationDate;
	}

	public void setObservationDate(LocalDateTime observationDate)
	{
		this.observationDate = observationDate;
	}

	public String getNoteIssuesString()
	{
		return noteIssuesString;
	}

	public void setNoteIssuesString(String noteIssuesString)
	{
		this.noteIssuesString = noteIssuesString;
	}

	public String getNoteExtsString()
	{
		return noteExtsString;
	}

	public void setNoteExtsString(String noteExtsString)
	{
		this.noteExtsString = noteExtsString;
	}

	public String getColour()
	{
		return colour;
	}

	public void setColour(String colour)
	{
		this.colour = colour;
	}

	public String[] getTitleClasses()
	{
		return titleClasses;
	}

	public void setTitleClasses(String[] titleClasses)
	{
		this.titleClasses = titleClasses;
	}

	public boolean isColouredTitle()
	{
		return (this.titleClasses != null && this.titleClasses.length > 0);
	}

	public static int compare(Object o1, Object o2, boolean asc, boolean truncateToDate )
	{
		EncounterSectionNote i1 = (EncounterSectionNote)o1;
		EncounterSectionNote i2 = (EncounterSectionNote)o2;
		LocalDateTime d1 = i1.getUpdateDate();
		LocalDateTime d2 = i2.getUpdateDate();

		if(truncateToDate)
		{
			d1 = d1.truncatedTo(ChronoUnit.DAYS);
			d2 = d2.truncatedTo(ChronoUnit.DAYS);
		}

		if( d1 == null && d2 != null )
		{
			return -1;
		}
		else if( d1 != null && d2 == null )
		{
			return 1;
		}
		else if( d1 == null && d2 == null )
		{
			return 0;
		}
		else
		{
			int dateCompare = i1.getUpdateDate().compareTo(i2.getUpdateDate());

			if(asc)
			{
				dateCompare = -dateCompare;
			}

			if(dateCompare == 0)
			{
				dateCompare = i1.getText().compareTo(i2.getText());
			}

			return dateCompare;
		}
	}

	public static int compareText(Object o1, Object o2)
	{
		EncounterSectionNote i1 = (EncounterSectionNote)o1;
		EncounterSectionNote i2 = (EncounterSectionNote)o2;
		String t1 = i1.getText();
		String t2 = i2.getText();

		if( t1 == null && t2 != null )
		{
			return -1;
		}
		else if( t1 != null && t2 == null )
		{
			return 1;
		}
		else if( t1 == null && t2 == null )
		{
			return 0;
		}
		else
		{
			return t1.compareTo(t2);
		}
	}

	public static class SortChronologicAsc implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(o1, o2, true, false);
		}
	}

	public static class SortChronologic implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(o1, o2, false, false);
		}
	}

	public static class SortChronologicDateAsc implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(o1, o2, true, true);
		}
	}

	public static class SortAlphabetic implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compareText(o1, o2);
		}
	}
}
