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
import org.oscarehr.integration.model.Integration;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class IntegrationDao extends AbstractDao<Integration>
{
    public IntegrationDao()
    {
        super(Integration.class);
    }

    public Integration findByIntegrationAndRemoteId(String remoteId, String integrationType)
    {
        Query query = entityManager.createQuery(
                "SELECT i FROM Integration i WHERE i.remoteId = :remoteId AND i.integrationType = :integrationType");
        query.setParameter("remoteId", remoteId);
        query.setParameter("integrationType", integrationType);

        return this.getSingleResultOrNull(query);
    }

    public Integration findDefaultByIntegration(String integrationType)
    {
        String sql = "SELECT i FROM Integration i WHERE i.integrationType = :integrationType AND i.site IS NULL";

        Query query = entityManager.createQuery(sql);
        query.setParameter("integrationType", integrationType);

        return this.getSingleResultOrNull(query);
    }

    public Integration findByIntegrationAndSiteName(String siteName, String integrationType)
    {
        String sql = "SELECT i FROM Integration i WHERE i.integrationType = :integrationType AND i.site.name = :siteName";

        Query query = entityManager.createQuery(sql);
        query.setParameter("siteName", siteName);
        query.setParameter("integrationType", integrationType);

        return this.getSingleResultOrNull(query);
    }

    public List<Integration> findMyHealthAccessIntegrations()
    {
        Query query = entityManager.createQuery(
                "SELECT i FROM Integration i WHERE i.integrationType = i.INTEGRATION_TYPE_MHA");

        @SuppressWarnings("unchecked")
        List<Integration> results = query.getResultList();
        return results;
    }

    public void save(Integration integration)
    {
        String remoteId = integration.getId();
        String integrationType = integration.getIntegrationType();

        if (integration.getId() != null && findByIntegrationAndRemoteId(remoteId, integrationType) != null)
        {
            merge(integration);
        }
        else
        {
            persist(integration);
        }
    }


}