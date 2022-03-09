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
import org.oscarehr.demographic.entity.Demographic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.List;

@Entity(name = "model.CaseManagementIssue")
@Table(name = "casemgmt_issue")
public class CaseManagementIssue extends AbstractModel<Long>
{
	public static final String ISSUE_FILTER_ALL = "all";
	public static final String ISSUE_FILTER_RESOLVED = "resolved";
	public static final String ISSUE_FILTER_UNRESOLVED = "unresolved";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "acute")
	private Boolean acute = false;

	@Column(name = "certain")
	private Boolean certain = false;

	@Column(name = "major")
	private Boolean major = false;

	@Column(name = "resolved")
	private Boolean resolved = false;

	@Column(name = "program_id")
	private Integer programId;

	@Column(name = "type")
	private String type;

	@Column(name = "update_date")
	@Temporal(TemporalType.DATE)
	private Date updateDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "demographic_no")
	private Demographic demographic;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "issue_id")
	private Issue issue;

	@OneToMany(fetch = FetchType.LAZY, mappedBy="id.caseManagementNote")
	private List<CaseManagementIssueNote> caseManagementIssueNoteList;

	@Override
	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Boolean getAcute()
	{
		return acute;
	}

	public void setAcute(Boolean acute)
	{
		this.acute = acute;
	}

	public Boolean getCertain()
	{
		return certain;
	}

	public void setCertain(Boolean certain)
	{
		this.certain = certain;
	}

	public Boolean getMajor()
	{
		return major;
	}

	public void setMajor(Boolean major)
	{
		this.major = major;
	}

	public Boolean getResolved()
	{
		return resolved;
	}

	public void setResolved(Boolean resolved)
	{
		this.resolved = resolved;
	}

	public Integer getProgramId()
	{
		return programId;
	}

	public void setProgramId(Integer programId)
	{
		this.programId = programId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}

	public Demographic getDemographic()
	{
		return demographic;
	}

	public void setDemographic(Demographic demographic)
	{
		this.demographic = demographic;
	}

	public Issue getIssue()
	{
		return issue;
	}

	public void setIssue(Issue issue)
	{
		this.issue = issue;
	}

	public List<CaseManagementIssueNote> getCaseManagementIssueNoteList()
	{
		return caseManagementIssueNoteList;
	}

	public void setCaseManagementIssueNoteList(List<CaseManagementIssueNote> caseManagementIssueNoteList)
	{
		this.caseManagementIssueNoteList = caseManagementIssueNoteList;
	}
}
