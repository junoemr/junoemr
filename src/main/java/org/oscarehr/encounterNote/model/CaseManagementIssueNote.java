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

import org.oscarehr.common.model.AbstractModel;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "model_CaseManagementIssueNote")
@Table(name = "casemgmt_issue_notes")
public class CaseManagementIssueNote extends AbstractModel<CaseManagementIssueNotePK>
{
	@EmbeddedId
	private CaseManagementIssueNotePK id;

	public CaseManagementIssueNote()
	{
		this(new CaseManagementIssueNotePK());
	}
	public CaseManagementIssueNote(CaseManagementIssueNotePK pk)
	{
		this.id = pk;
	}

	/** construct a copy of the given issue note */
	public CaseManagementIssueNote(CaseManagementIssueNote issueNoteToCopy, CaseManagementNote referenceNote)
	{
		/* we want a new issue note (this), but not a new issue. use the existing linked issue for this demographic */
		this.id = new CaseManagementIssueNotePK(issueNoteToCopy.getId().getCaseManagementIssue(), referenceNote);
	}


	@Override
	public CaseManagementIssueNotePK getId()
	{
		return id;
	}

	public void setId(CaseManagementIssueNotePK id)
	{
		this.id = id;
	}
}
