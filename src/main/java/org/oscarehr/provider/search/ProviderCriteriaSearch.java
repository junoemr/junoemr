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
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Junction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.provider.model.ProviderData;

@Getter
@Setter
public class ProviderCriteriaSearch extends AbstractCriteriaSearch
{

	private String providerNo = null;
	private String lastName = null;
	private String firstName = null;
	private String providerType = null;
	private Boolean activeStatus = null;
	
	private String practitionerNo = null;
	private String ohipNo = null;
	private String hsoNo = null;
	private String ontarioCnoNumber = null;
	private String albertaEDeliveryId = null;
	private String albertaConnectCareId = null;

	@Override
	public Criteria setCriteriaProperties(Criteria criteria)
	{
		// determine criteria join mode ('AND' filter criteria vs 'OR' filter criteria)
		Junction junction = getEmptyJunction();

		// set the search filters
		if (StringUtils.isNotBlank(getProviderNo()))
		{
			junction.add(Restrictions.eq("id", String.valueOf(getProviderNo())));
		}
		if (getActiveStatus() != null)
		{
			junction.add(Restrictions.eq("status", getActiveStatus() ? ProviderData.PROVIDER_STATUS_ACTIVE : ProviderData.PROVIDER_STATUS_INACTIVE));
		}
		if (StringUtils.isNotBlank(getFirstName()))
		{
			junction.add(Restrictions.eq("firstName", getFirstName()));
		}
		if (StringUtils.isNotBlank(getLastName()))
		{
			junction.add(Restrictions.eq("lastName", getLastName()));
		}
		if (StringUtils.isNotBlank(getProviderType()))
		{
			junction.add(Restrictions.eq("providerType", getProviderType()));
		}
		
		if (StringUtils.isNotBlank(getPractitionerNo()))
		{
			junction.add(Restrictions.eq("practitionerNo", getPractitionerNo()));
		}
		if (StringUtils.isNotBlank(getOhipNo()))
		{
			junction.add(Restrictions.eq("ohipNo", getOhipNo()));
		}
		if (StringUtils.isNotBlank(getHsoNo()))
		{
			junction.add(Restrictions.eq("hsoNo", getHsoNo()));
		}
		
		if (StringUtils.isNotBlank(getOntarioCnoNumber()))
		{
			junction.add(Restrictions.eq("ontarioCnoNumber", getOntarioCnoNumber()));
		}

		if (StringUtils.isNotBlank(getAlbertaEDeliveryId()))
		{
			junction.add(Restrictions.like("albertaEDeliveryIds", getAlbertaEDeliveryId(), MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(getAlbertaConnectCareId()))
		{
			junction.add(Restrictions.eq("albertaConnectCareId", getAlbertaConnectCareId()));
		}

		criteria.add(junction);
		return criteria;
	}
}
