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
package org.oscarehr.dx.search;

import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.Optional;

public class DxIcd9CodeCriteriaSearch extends DxCodeCriteriaSearch
{
	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		Junction junction = getEmptyJunction();

		if(Optional.ofNullable(code).isPresent())
		{
			junction.add(Restrictions.ilike("icd9", code, MatchMode.START));
		}
		if(Optional.ofNullable(description).isPresent())
		{
			junction.add(Restrictions.ilike("description", description, MatchMode.START));
		}

		criteria.add(junction);
		return criteria;
	}
}
