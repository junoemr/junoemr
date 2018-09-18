/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.fax.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.fax.model.FaxConfig;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class FaxConfigDao extends AbstractDao<FaxConfig>
{
	public FaxConfigDao()
	{
		super(FaxConfig.class);
	}

	public FaxConfig getConfigByNumber(String number)
	{
		Query query = entityManager.createQuery("select config from FaxConfig config where config.faxNumber = :number");
		query.setParameter("number", number);

		return getSingleResultOrNull(query);
	}

	public List<FaxConfig> findByActiveStatus(boolean isActive, int offset, int limit)
	{
		Query query = entityManager.createQuery("SELECT config FROM FaxConfig config WHERE config.active = :active");
		query.setParameter("active", isActive);
		query.setMaxResults(limit);
		query.setFirstResult(offset);

		return query.getResultList();
	}

	public List<FaxConfig> findByActiveInbound(boolean isActive, boolean activeInbound, int offset, int limit)
	{
		Query query = entityManager.createQuery(
				"SELECT config FROM FaxConfig config " +
				"WHERE config.active = :active " +
				"AND config.activeInbound = :activeInbound"
		);
		query.setParameter("active", isActive);
		query.setParameter("activeInbound", activeInbound);
		query.setMaxResults(limit);
		query.setFirstResult(offset);

		return query.getResultList();
	}

	public List<FaxConfig> findByActiveOutbound(boolean isActive, boolean activeOutbound, int offset, int limit)
	{
		Query query = entityManager.createQuery(
				"SELECT config FROM FaxConfig config " +
						"WHERE config.active = :active " +
						"AND config.activeOutbound = :activeOutbound"
		);
		query.setParameter("active", isActive);
		query.setParameter("activeOutbound", activeOutbound);
		query.setMaxResults(limit);
		query.setFirstResult(offset);

		return query.getResultList();
	}
}
