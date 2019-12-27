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
package org.oscarehr.ws.rest.integrations.iceFall.transfer;

import java.time.LocalDateTime;

public class IceFallLogSearchTo1
{
	private String status;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	private Integer page;
	private Integer pageSize;
	private String sortBy;
	private String sortDirection;

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public LocalDateTime getStartDate()
	{
		return startDate;
	}

	public void setStartDate(LocalDateTime startDate)
	{
		this.startDate = startDate;
	}

	public LocalDateTime getEndDate()
	{
		return endDate;
	}

	public void setEndDate(LocalDateTime endDate)
	{
		this.endDate = endDate;
	}

	public Integer getPage()
	{
		return page;
	}

	public void setPage(Integer page)
	{
		this.page = page;
	}

	public Integer getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(Integer pageSize)
	{
		this.pageSize = pageSize;
	}

	public String getSortBy()
	{
		return sortBy;
	}

	public void setSortBy(String sortBy)
	{
		this.sortBy = sortBy;
	}

	public String getSortDirection()
	{
		return sortDirection;
	}

	public void setSortDirection(String sortDirection)
	{
		this.sortDirection = sortDirection;
	}
}
