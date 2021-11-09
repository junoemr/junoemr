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
package org.oscarehr.measurements.search;

import lombok.Data;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

import java.util.Optional;

@Data
public class MeasurementTypeCriteriaSearch extends AbstractCriteriaSearch
{
	private String type;
	private String name;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		Junction junction = getEmptyJunction();

		if(Optional.ofNullable(name).isPresent())
		{
			junction.add(Restrictions.ilike("typeDisplayName", name, MatchMode.START));
		}
		if(Optional.ofNullable(type).isPresent())
		{
			junction.add(Restrictions.ilike("type", type, MatchMode.START));
		}

		criteria.add(junction);

		// order by length, then alphabetically
		criteria.addOrder(new OrderByLength("typeDisplayName", this.isSortDirAscending()));
		criteria.addOrder(getOrder("typeDisplayName"));

		return criteria;
	}
}
