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
package org.oscarehr.sharingcenter.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.sharingcenter.model.PatientSharingNetworkDataObject;
import org.springframework.stereotype.Repository;

@Repository
public class PatientSharingNetworkDao extends AbstractDao<PatientSharingNetworkDataObject> {

    public PatientSharingNetworkDao() {
        super(PatientSharingNetworkDataObject.class);
    }

    /**
     * Finds all Patient Sharing Network Association Objects in our Data model
     * 
     * @return
     * list of all Patient Sharing Network Data Objects
     */
    public List<PatientSharingNetworkDataObject> findPatientSharingNetworkDataObjects() {
        String sql = "FROM PatientSharingNetworkDataObject";
        Query query = entityManager.createQuery(sql);

        @SuppressWarnings("unchecked")
        List<PatientSharingNetworkDataObject> retVal = query.getResultList();
        return retVal;
    }

    public List<PatientSharingNetworkDataObject> findByDemographicId(int demographicId) {
        String sql = "FROM PatientSharingNetworkDataObject e where e.demographicNo = ?1";
        Query query = entityManager.createQuery(sql);
        query.setParameter(1, demographicId);

        return query.getResultList();
    }

    public PatientSharingNetworkDataObject findPatientSharingNetworkDataObject(int affinityDomain, int demographicId) {
        String sql = "FROM PatientSharingNetworkDataObject e where e.affinityDomain = ?1 and e.demographicNo = ?2";

        Query query = entityManager.createQuery(sql);
        query.setParameter(1, affinityDomain);
        query.setParameter(2, demographicId);

        query.setMaxResults(1);
        PatientSharingNetworkDataObject retVal = getSingleResultOrNull(query);
        return retVal;
    }

    public boolean isSharingEnabled(int affinityDomain, int demographicId) {
        String sql = "SELECT count(*) FROM PatientSharingNetworkDataObject e where e.affinityDomain = ?1 and e.demographicNo = ?2 and e.sharingEnabled = 1";

        Query query = entityManager.createQuery(sql);
        query.setParameter(1, affinityDomain);
        query.setParameter(2, demographicId);

        int retVal = ((Long) query.getSingleResult()).intValue();
        return retVal > 0;
    }

}
