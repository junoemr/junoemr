/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package org.oscarehr.provider.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.provider.model.ProviderRecentDemographicAccess;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class ProviderRecentDemographicAccessDao extends AbstractDao<ProviderRecentDemographicAccess>
{
	protected ProviderRecentDemographicAccessDao()
	{
		super(ProviderRecentDemographicAccess.class);
	}

	public ProviderRecentDemographicAccess findByPrimaryKey(Integer providerNo, Integer demographicNo)
	{
		String querySql = "SELECT x " +
				"FROM ProviderRecentDemographicAccess x " +
				"WHERE x.providerRecentDemographicAccessId.providerNo = :providerNo " +
				"AND x.providerRecentDemographicAccessId.demographicNo = :demographicNo " +
				"ORDER BY x.accessDateTime ASC";

		Query query = entityManager.createQuery(querySql);
		query.setParameter("providerNo", providerNo);
		query.setParameter("demographicNo", demographicNo);

		return this.getSingleResultOrNull(query);
	}

	public List<ProviderRecentDemographicAccess> findByProviderNo(Integer providerNo, int offset, int limit)
	{
		String querySql = "SELECT x " +
				"FROM ProviderRecentDemographicAccess x " +
				"WHERE x.providerRecentDemographicAccessId.providerNo = :providerNo " +
				"ORDER BY x.accessDateTime ASC";

		Query query = entityManager.createQuery(querySql);
		query.setParameter("providerNo", providerNo);
		if(offset > 0)
		{
			query.setFirstResult(offset);
		}
		if(limit > 0)
		{
			query.setMaxResults(limit);
		}

		return query.getResultList();
	}
}
