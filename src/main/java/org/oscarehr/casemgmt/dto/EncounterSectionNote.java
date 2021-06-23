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

import org.oscarehr.casemgmt.service.MultiSearchResult;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

public class EncounterSectionNote extends MultiSearchResult
{
	private Integer id;
	private String text;
	private String title;
	private String[] textLineArray;
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

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public String[] getTextLineArray()
	{
		return textLineArray;
	}

	public void setTextLineArray(String[] textLineArray)
	{
		this.textLineArray = textLineArray;
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

	public static int compare(Object o1, Object o2, boolean asc, boolean truncateToDate, boolean sortTextOpposite, boolean blankHighest)
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

		int result = 0;
		if( d1 == null && d2 != null )
		{
			if(blankHighest)
			{
				result = 1;
			}
			else
			{
				result = -1;
			}
		}
		else if( d1 != null && d2 == null )
		{
			if(blankHighest)
			{
				result = -1;
			}
			else
			{
				result = 1;
			}
		}
		else if( d1 == null && d2 == null )
		{
			result = 0;
		}
		else
		{
			result = i1.getUpdateDate().compareTo(i2.getUpdateDate());

			if(result == 0)
			{
				result = i1.getText().compareToIgnoreCase(i2.getText());

				if(sortTextOpposite)
				{
					result = (result * -1);
				}
			}
		}

		if(!asc)
		{
			return (result * -1);
		}

		return result;
	}

	public static class SortChronologicAsc implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(
					o1,
					o2,
					true,
					false,
					false,
					false
			);
		}
	}

	public static class SortChronologic implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(
					o1,
					o2,
					false,
					false,
					false,
					false
			);
		}
	}

	public static class SortChronologicBlankDateFirst implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(
					o1,
					o2,
					false,
					false,
					false,
					true
			);
		}
	}

	public static class SortChronologicDescTextAsc implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(
					o1,
					o2,
					false,
					false,
					true,
					false
			);
		}
	}

	public static class SortChronologicDateAsc implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return EncounterSectionNote.compare(
					o1,
					o2,
					true,
					true,
					false,
					false
			);
		}
	}

	public static class SortAlphabetic implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return MultiSearchResult.compareText(o1, o2);
		}
	}
}
