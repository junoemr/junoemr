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
package org.oscarehr.ws.rest.transfer;

import org.oscarehr.common.model.Dashboard;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

public class DashboardTo1 implements Serializable
{
	private Integer id;
	private String name;
	private String description;
	private Boolean active;
	private Boolean locked;

	public DashboardTo1() {}

	public DashboardTo1(Dashboard dashboard)
	{
		BeanUtils.copyProperties(dashboard, this);
	}

	/**
	 * update the provided dashboard model with the values of this transfer object
	 * @param dashboard - the dashboard model to update
	 * @return - the new dashboard model
	 */
	public Dashboard toDashboard(Dashboard dashboard)
	{
		BeanUtils.copyProperties(this, dashboard);
		return dashboard;
	}

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public Boolean getActive()
	{
		return active;
	}

	public void setActive(Boolean active)
	{
		this.active = active;
	}

	public Boolean getLocked()
	{
		return locked;
	}

	public void setLocked(Boolean locked)
	{
		this.locked = locked;
	}
}
