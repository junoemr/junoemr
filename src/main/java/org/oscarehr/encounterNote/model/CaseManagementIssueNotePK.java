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
package org.oscarehr.encounterNote.model;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CaseManagementIssueNotePK implements Serializable
{
	@ManyToOne
	@JoinColumn(name = "id")
	private CaseManagementIssue caseManagementIssue;

	@ManyToOne
	@JoinColumn(name = "note_id")
	private CaseManagementNote caseManagementNote;

	public CaseManagementIssueNotePK() {}

	public CaseManagementIssueNotePK(CaseManagementIssue issue, CaseManagementNote note)
	{
		this.caseManagementIssue = issue;
		this.caseManagementNote = note;
	}

	public CaseManagementIssue getCaseManagementIssue()
	{
		return caseManagementIssue;
	}

	public void setCaseManagementIssue(CaseManagementIssue caseManagementIssue)
	{
		this.caseManagementIssue = caseManagementIssue;
	}

	public CaseManagementNote getCaseManagementNote()
	{
		return caseManagementNote;
	}

	public void setCaseManagementNote(CaseManagementNote caseManagementNote)
	{
		this.caseManagementNote = caseManagementNote;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(!(o instanceof CaseManagementIssueNotePK))
		{
			return false;
		}
		CaseManagementIssueNotePK that = (CaseManagementIssueNotePK) o;
		return Objects.equals(caseManagementIssue, that.caseManagementIssue) &&
				Objects.equals(caseManagementNote, that.caseManagementNote);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(caseManagementIssue, caseManagementNote);
	}
}
