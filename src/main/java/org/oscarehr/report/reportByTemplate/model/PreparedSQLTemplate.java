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

package org.oscarehr.report.reportByTemplate.model;

import oscar.util.StringUtils;

import java.util.Map;

public class PreparedSQLTemplate
{
	private String preparedSQL;
	private Map<Integer, String[]> parameterMap;

	public String getPreparedSQL()
	{
		return preparedSQL;
	}

	public void setPreparedSQL(String preparedSQL)
	{
		this.preparedSQL = preparedSQL;
	}

	public Map<Integer, String[]> getParameterMap()
	{
		return parameterMap;
	}

	public void setParameterMap(Map<Integer, String[]> parameterMap)
	{
		this.parameterMap = parameterMap;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Prepared SQL:\n" + preparedSQL + "\nParameters:\n{");

		for(Integer key : parameterMap.keySet())
		{
			sb.append(key);
			sb.append(":[");
			sb.append(StringUtils.join(parameterMap.get(key), ","));
			sb.append("], ");
		}
		sb.append("}");
		return sb.toString();
	}
}
