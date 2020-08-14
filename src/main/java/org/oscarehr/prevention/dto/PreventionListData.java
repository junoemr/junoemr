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

package org.oscarehr.prevention.dto;

import java.time.LocalDateTime;

public class PreventionListData
{
	private Integer preventionId;
	private String type;
	private LocalDateTime preventionDate;
	private Character refused;
	private String preventionResult;
	private Integer preventionCount;

	public Integer getPreventionId()
	{
		return preventionId;
	}

	public void setPreventionId(Integer preventionId)
	{
		this.preventionId = preventionId;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public LocalDateTime getPreventionDate()
	{
		return preventionDate;
	}

	public void setPreventionDate(LocalDateTime preventionDate)
	{
		this.preventionDate = preventionDate;
	}

	public Character getRefused()
	{
		return refused;
	}

	public void setRefused(Character refused)
	{
		this.refused = refused;
	}

	public String getPreventionResult()
	{
		return preventionResult;
	}

	public void setPreventionResult(String preventionResult)
	{
		this.preventionResult = preventionResult;
	}

	public Integer getPreventionCount()
	{
		return preventionCount;
	}

	public void setPreventionCount(Integer preventionCount)
	{
		this.preventionCount = preventionCount;
	}
}
