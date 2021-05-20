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
package org.oscarehr.security.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.security.model.SecRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import javax.persistence.Query;
import java.util.List;

@Repository
@Transactional
public class SecRoleDao extends AbstractDao<SecRole>
{

	public SecRoleDao() {
		super(SecRole.class);
	}

    public List<SecRole> findAll()
	{
		StringBuilder sb=new StringBuilder();
		sb.append("select x from SecRole x");

		sb.append(" order by x.name");

		Query query = entityManager.createQuery(sb.toString());

		@SuppressWarnings("unchecked")
		List<SecRole> results = query.getResultList();

		return(results);
	}
    
    public List<String> findAllNames()
 	{
 		StringBuilder sb=new StringBuilder();
 		sb.append("select x.name from SecRole x");

 		sb.append(" order by x.name");

 		Query query = entityManager.createQuery(sb.toString());

 		@SuppressWarnings("unchecked")
 		List<String> results = query.getResultList();

 		return(results);
 	}

	public SecRole findSystemDefaultRole()
	{
		// this should be the only case where roles are found by name
		String providerDefaultRoleName = OscarProperties.getInstance().getProperty("default_provider_role_name");
		Query q = entityManager.createQuery("select x from SecRole x where x.name=:name");
		q.setParameter("name", providerDefaultRoleName);

		SecRole defaultRole = this.getSingleResultOrNull(q);
		if(defaultRole == null)
		{
			throw new IllegalStateException("Default system role '" + providerDefaultRoleName + "' does not exist");
		}
		return defaultRole;
	}

    public boolean roleExistsWithName(String name)
    {
	    Query query = entityManager.createQuery("SELECT count(x) FROM SecRole x WHERE x.name=:name");
	    query.setParameter("name", name);
	    return ((Long) query.getSingleResult() > 0);
    }

    public List<SecRole> findAllOrderByRole()
	{
		Query query = entityManager.createQuery("select x from SecRole x order by x.name");

		@SuppressWarnings("unchecked")
		List<SecRole> results = query.getResultList();

		return(results);
	}

	public List<SecRole> findAllOrderByRole(String[] ignoreList)
	{
		if(ignoreList == null || ignoreList.length < 1)
		{
			return findAllOrderByRole();
		}
		Query query = entityManager.createQuery("select x from SecRole x WHERE x.name NOT IN (:ignoreList) order by x.name");
		query.setParameter("ignoreList", ignoreList);

		@SuppressWarnings("unchecked")
		List<SecRole> results = query.getResultList();

		return(results);
	}
}
