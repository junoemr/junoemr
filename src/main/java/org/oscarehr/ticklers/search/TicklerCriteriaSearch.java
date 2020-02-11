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
package org.oscarehr.ticklers.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.model.Tickler;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.util.Date;

public class TicklerCriteriaSearch extends AbstractCriteriaSearch
{
	public enum SORT_MODE
	{
		UpdateDate,
		DemographicName,
		Creator,
		ServiceDate,
		Priority,
		TaskAssignedTo,
		Status,
		Message
	}

	// private MatchMode matchMode = MatchMode.START;

	// fields here
	private Date startDate;
	private Date endDate;
	private String creator;
	private String taskAssignedTo;
	private String programId;
	private Integer demographicNo;
	private String message;
	private Tickler.PRIORITY priority;
	private Tickler.STATUS status;
	private String mrp;

	private SORT_MODE sortMode = SORT_MODE.UpdateDate;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		Junction junction = getEmptyJunction();

		// set search filters
		if (getDemographicNo() != null)
		{
			junction.add(Restrictions.eq("demographicNo", getDemographicNo()));
		}

		if (getCreator() != null)
		{
			junction.add(Restrictions.eq("creator", getCreator()));
		}

		if (getTaskAssignedTo() != null)
		{
			junction.add(Restrictions.eq("taskAssignedTo", getTaskAssignedTo()));
		}

		if (getPriority() != null)
		{
			junction.add(Restrictions.eq("priority", getPriority()));
		}

		if (getMessage() != null)
		{
			junction.add(Restrictions.eq("message", getMessage()));
		}

		if (getProgramId() != null)
		{
			junction.add(Restrictions.eq("programId", getProgramId()));
		}

		if (getStatus() != null)
		{
			junction.add(Restrictions.eq("status", getStatus()));
		}

		if (getMrp() != null)
		{
			junction.add(Restrictions.eq("mrp", getMrp()));
		}

		// date searching
		if (getStartDate() != null && getEndDate() != null)
		{
			junction.add(Restrictions.between("serviceDate", getStartDate(), getEndDate()));
		}
		else if (getStartDate() != null)
		{
			junction.add(Restrictions.ge("serviceDate", getStartDate()));
		}
		else if (getEndDate() != null)
		{
			junction.add(Restrictions.le("serviceDate", getEndDate()));
		}

		setOrderByCriteria(criteria);
		criteria.add(junction);

		return criteria;
	}

	private void setOrderByCriteria(Criteria criteria)
	{
		switch(sortMode)
		{
			case DemographicName:
				// this is not what it was before. need to address this
				// join on demographic?
				criteria.addOrder(getOrder("demographicNo"));
				break;
			case Creator:
				criteria.addOrder(getOrder("creator"));
				break;
			case ServiceDate:
				criteria.addOrder(getOrder("serviceDate"));
				break;
			case Priority:
				criteria.addOrder(getOrder("priority"));
				break;
			case TaskAssignedTo:
				criteria.addOrder(getOrder("taskAssignedTo"));
				break;
			case Status:
				criteria.addOrder(getOrder("status"));
				break;
			case Message:
				criteria.addOrder(getOrder("message"));
				break;
			case UpdateDate:
			default:
				criteria.addOrder(getOrder("updateDate"));
				break;
		}
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getEndDate()
	{
		return endDate;
	}

	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getTaskAssignedTo()
	{
		return taskAssignedTo;
	}

	public void setTaskAssignedTo(String taskAssignedTo)
	{
		this.taskAssignedTo = taskAssignedTo;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public Tickler.PRIORITY getPriority()
	{
		return priority;
	}

	public void setPriority(Tickler.PRIORITY priority)
	{
		this.priority = priority;
	}

	public Integer getDemographicNo()
	{
		return demographicNo;
	}

	public void setDemographicNo(Integer demographicNo)
	{
		this.demographicNo = demographicNo;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public SORT_MODE getSortMode()
	{
		return sortMode;
	}

	public void setSortMode(SORT_MODE sortMode)
	{
		this.sortMode = sortMode;
	}

	public Tickler.STATUS getStatus()
	{
		return status;
	}

	public void setStatus(Tickler.STATUS status)
	{
		this.status = status;
	}

	public String getMrp()
	{
		return mrp;
	}

	public void setMrp(String mrp)
	{
		this.mrp = mrp;
	}
}
