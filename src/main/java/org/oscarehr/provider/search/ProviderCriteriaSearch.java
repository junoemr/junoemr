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
package org.oscarehr.provider.search;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;

@Getter
@Setter
public class ProviderCriteriaSearch extends AbstractCriteriaSearch
{

	private String providerNo = null;
	private String lastName = null;
	private String firstName = null;
	
	private String practitionerNo = null;
	private String ontarioCnoNumber = null;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		// set the search filters
		if (getProviderNo() != null)
		{
			criteria.add(Restrictions.eq("id", String.valueOf(getProviderNo())));
		}
		if (getFirstName() != null)
		{
			criteria.add(Restrictions.eq("firstName", getFirstName()));
		}
		if (getLastName() != null)
		{
			criteria.add(Restrictions.eq("lastName", getLastName()));
		}
		
		if (getPractitionerNo() != null)
		{
			criteria.add(Restrictions.eq("practitionerNo", getPractitionerNo()));
		}
		
		if (getOntarioCnoNumber() != null)
		{
			criteria.add(Restrictions.eq("ontarioCnoNumber", getOntarioCnoNumber()));
		}

		return criteria;
	}
}
