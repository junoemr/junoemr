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

package org.oscarehr.hospitalReportManager.search;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

@Getter
@Setter
public class HrmSubClassCriteriaSearch extends AbstractCriteriaSearch
{
	private String sendingFacilityId;
	private String className;
	private String subClassName;
	private String accompanyingSubClassName;
	private boolean activeOnly = true;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		if (getSendingFacilityId() != null)
		{
			criteria.add(Restrictions.eq("sendingFacilityId", getSendingFacilityId()));
		}
		if (getClassName() != null)
		{
			criteria.add(Restrictions.eq("className", getClassName()));
		}
		if (getSubClassName() != null)
		{
			criteria.add(Restrictions.eq("subClassName", getSubClassName()));
		}
		if (getAccompanyingSubClassName() != null)
		{
			criteria.add(Restrictions.eq("accompanyingSubClassName", getAccompanyingSubClassName()));
		}

		if (activeOnly)
		{
			criteria.add(Restrictions.isNull("disabledAt"));
		}

		return criteria;
	}
}