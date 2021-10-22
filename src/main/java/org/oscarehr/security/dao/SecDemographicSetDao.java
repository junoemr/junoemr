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
package org.oscarehr.security.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.security.model.SecDemographicSet;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class SecDemographicSetDao extends AbstractDao<SecDemographicSet>
{
	protected SecDemographicSetDao()
	{
		super(SecDemographicSet.class);
	}

	public List<SecDemographicSet> findAllByProvider(String providerId)
	{
		String sql = "SELECT s FROM SecDemographicSet s WHERE s.provider.id=:providerId ORDER BY s.setName ASC";

		Query query = entityManager.createQuery(sql);
		query.setParameter("providerId", providerId);

		return query.getResultList();
	}

	public SecDemographicSet findByProviderAndSetName(String providerId, String setName)
	{
		String sql = "SELECT s FROM SecDemographicSet s WHERE s.provider.id=:providerId AND s.setName=:setName";

		Query query = entityManager.createQuery(sql);
		query.setParameter("providerId", providerId);
		query.setParameter("setName", setName);

		return this.getSingleResultOrNull(query);
	}
}
