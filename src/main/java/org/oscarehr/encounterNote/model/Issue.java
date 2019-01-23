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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity(name = "model.Issue")
@Table(name = "issue")
public class Issue extends AbstractModel<Long>
{
	public static final String SUMMARY_CODE_MEDICAL_HISTORY = "MedHistory";
	public static final String SUMMARY_CODE_SOCIAL_HISTORY = "SocHistory";
	public static final String SUMMARY_CODE_FAMILY_HISTORY = "FamHistory";
	public static final String SUMMARY_CODE_REMINDERS = "Reminders";


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "issue_id")
	private Long issueId;

	@Column(name = "code")
	private String code;

	@Column(name = "description")
	private String description;

	@Column(name = "role")
	private String role;

	@Column(name = "update_date")
	@Temporal(TemporalType.DATE)
	private Date updateDate;

	@Column(name = "priority")
	private String priority;

	@Column(name = "type")
	private String type;

	@Column(name = "sortOrderId")
	private Integer sortOrderId;


	@Override
	public Long getId()
	{
		return issueId;
	}

	public Long getIssueId()
	{
		return issueId;
	}

	public void setIssueId(Long issueId)
	{
		this.issueId = issueId;
	}

	public String getCode()
	{
		return code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

	public Date getUpdateDate()
	{
		return updateDate;
	}

	public void setUpdateDate(Date updateDate)
	{
		this.updateDate = updateDate;
	}

	public String getPriority()
	{
		return priority;
	}

	public void setPriority(String priority)
	{
		this.priority = priority;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public Integer getSortOrderId()
	{
		return sortOrderId;
	}

	public void setSortOrderId(Integer sortOrderId)
	{
		this.sortOrderId = sortOrderId;
	}
}
