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

public class EncounterSectionNote
{
	private Integer id;
	private String text;
	private String editors;
	private Integer revision;
	private LocalDateTime updateDate;
	private LocalDateTime observationDate;
	private String noteIssuesString;
	private String noteExtsString;

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
}
