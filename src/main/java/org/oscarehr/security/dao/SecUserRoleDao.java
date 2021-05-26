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
import org.oscarehr.security.model.SecUserRole;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class SecUserRoleDao extends AbstractDao<SecUserRole>
{
    protected SecUserRoleDao()
    {
        super(SecUserRole.class);
    }

    public List<SecUserRole> getUserRoles(String providerNo)
    {
        if (providerNo == null)
        {
            throw new IllegalArgumentException();
        }

        Query query = entityManager.createQuery("SELECT s FROM SecUserRole s WHERE s.providerNo = :providerNo");
        query.setParameter("providerNo", providerNo);

        return query.getResultList();
    }

    public List<SecUserRole> getSecUserRolesByRoleName(String roleName)
    {
        Query query = entityManager.createQuery("SELECT s FROM SecUserRole s WHERE s.roleName = :roleName");
        query.setParameter("roleName", roleName);

        return query.getResultList();
    }

    public List<SecUserRole> findByRoleNameAndProviderNo(String roleName, String providerNo)
    {
        Query query = entityManager.createQuery("SELECT s FROM SecUserRole s WHERE s.providerNo = :providerNo AND s.roleName = :roleName");
        query.setParameter("providerNo", providerNo);
        query.setParameter("roleName", roleName);

        return query.getResultList();
    }

    public SecUserRole findByProviderAndRoleId(String providerId, Integer secRoleId)
    {
        Query query = entityManager.createQuery("SELECT s FROM SecUserRole s WHERE s.providerNo = :providerId AND s.secRole.id = :secRoleId");
        query.setParameter("providerId", providerId);
        query.setParameter("secRoleId", secRoleId);

        return this.getSingleResultOrNull(query);
    }

    public List<String> getRecordsAddedAndUpdatedSinceTime(Date date)
    {
        Query query = entityManager.createQuery("SELECT s.providerNo FROM SecUserRole s WHERE s.lastUpdateDate > :date");
        query.setParameter("date", date);

        return query.getResultList();
    }
}
