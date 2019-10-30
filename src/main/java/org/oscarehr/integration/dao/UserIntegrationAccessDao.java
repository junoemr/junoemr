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

package org.oscarehr.integration.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.model.Security;
import org.oscarehr.integration.model.Integration;
import org.oscarehr.integration.model.UserIntegrationAccess;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
public class UserIntegrationAccessDao extends AbstractDao<UserIntegrationAccess>
{
	public UserIntegrationAccessDao()
	{
		super(UserIntegrationAccess.class);
	}

	public UserIntegrationAccess findByIntegrationAndSecurity(Integration integration, Security security)
	{
		Query query = entityManager.createQuery(
				"SELECT i FROM UserIntegrationAccess i WHERE i.integration = :integration AND i.security = :security");

		query.setParameter("integration", integration);
		query.setParameter("security", security);

		return this.getSingleResultOrNull(query);
	}

	public UserIntegrationAccess findByIntegrationAndProviderNo(Integration integration, String providerNo)
	{
		Query query = entityManager.createQuery(
				"SELECT i FROM UserIntegrationAccess i WHERE i.integration = :integration AND i.security.providerNo = :providerNo");

		query.setParameter("integration", integration);
		query.setParameter("providerNo", providerNo);

		return this.getSingleResultOrNull(query);
	}

	public UserIntegrationAccess findBySecurityNoAndIntegration(Integer securityNo, String integrationType)
	{
		String sql = "SELECT i FROM UserIntegrationAccess i " +
					 "WHERE i.security.id = :securityNo " +
					 "AND i.integration.integrationType = :integrationType " +
					 "AND i.integration.site IS NULL";

		Query query = entityManager.createQuery(sql);

		query.setParameter("securityNo", securityNo);
		query.setParameter("integrationType", integrationType);

		return this.getSingleResultOrNull(query);
	}

	public UserIntegrationAccess findBySecurityNoAndSiteName(Integer securityNo, String siteName)
	{
		String sql = "SELECT i FROM UserIntegrationAccess i WHERE i.security.id = :securityNo AND i.integration.site.name = :siteName";

		Query query = entityManager.createQuery(sql);

		query.setParameter("securityNo", securityNo);
		query.setParameter("siteName", siteName);

		return this.getSingleResultOrNull(query);
	}

	public void save(UserIntegrationAccess userIntegrationAccess)
	{
		Integer id = userIntegrationAccess.getId();

		if (id != null)
		{
			merge(userIntegrationAccess);
		}
		else
		{
			persist(userIntegrationAccess);
		}
	}


}
