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
package org.oscarehr.common.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public abstract class AbstractCriteriaSearch
{
	public enum SORTDIR
	{
		asc, desc
	}

	// determine criteria join mode ('AND' filter criteria vs 'OR' filter criteria)
	public enum JUNCTION_TYPE
	{
		conjunction, //AND
		disjunction // OR
	}

	private int limit = 100;
	private int offset = 0;
	private boolean noLimit = false;
	private SORTDIR sortDir = SORTDIR.asc;
	private JUNCTION_TYPE junctionType = JUNCTION_TYPE.conjunction;

	public abstract Criteria setCriteriaProperties(Criteria criteria);

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public void setNoLimit()
	{
		this.noLimit = true;
	}

	public boolean hasNoLimit()
	{
		return this.noLimit;
	}

	public int getOffset()
	{
		return offset;
	}

	public void setOffset(int offset)
	{
		this.offset = offset;
	}

	public SORTDIR getSortDir()
	{
		return sortDir;
	}

	public void setSortDir(SORTDIR sortDir)
	{
		this.sortDir = sortDir;
	}

	public void setSortDirAscending()
	{
		setSortDir(SORTDIR.asc);
	}

	public void setSortDirDescending()
	{
		setSortDir(SORTDIR.desc);
	}

	protected Order getOrder(String propertyName)
	{
		return (SORTDIR.asc.equals(sortDir))? Order.asc(propertyName) : Order.desc(propertyName);
	}
	protected Junction getEmptyJunction()
	{
		return (getJunctionType() == JUNCTION_TYPE.disjunction)? Restrictions.disjunction() : Restrictions.conjunction();
	}

	public JUNCTION_TYPE getJunctionType()
	{
		return junctionType;
	}

	public void setJunctionType(JUNCTION_TYPE junctionType)
	{
		this.junctionType = junctionType;
	}

	public void setJunctionTypeAND()
	{
		this.setJunctionType(JUNCTION_TYPE.conjunction);
	}
	public void setJunctionTypeOR()
	{
		this.setJunctionType(JUNCTION_TYPE.disjunction);
	}
}
