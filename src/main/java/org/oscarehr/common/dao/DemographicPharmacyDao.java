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

package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.DemographicPharmacy;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicPharmacyDao extends AbstractDao<DemographicPharmacy> {

	public DemographicPharmacyDao() {
		super(DemographicPharmacy.class);
	}

	public DemographicPharmacy addPharmacyToDemographic(Integer pharmacyId, Integer demographicNo, Integer preferredOrder) {
		// If there is an existing pharmacy entry existing that we want to modify, grab it
		String sql = "SELECT x FROM DemographicPharmacy x WHERE x.status = ?1 AND x.demographicNo = ?2 AND x.pharmacyId = ?3";
		Query query = entityManager.createQuery(sql);		
		query.setParameter(1, DemographicPharmacy.ACTIVE);
		query.setParameter(2, demographicNo);
		query.setParameter(3, pharmacyId);
		DemographicPharmacy demographicPharmacy = getSingleResultOrNull(query);

		sql = "SELECT x FROM DemographicPharmacy x WHERE x.status = ?1 AND x.demographicNo = ?2 ORDER BY x.preferredOrder";
		query = entityManager.createQuery(sql);
		query.setParameter(1, DemographicPharmacy.ACTIVE);
		query.setParameter(2, demographicNo);

		@SuppressWarnings("unchecked")
		List<DemographicPharmacy> results = query.getResultList();
		DemographicPharmacy targetPharmacy;

		// Determine if the pharmacy we want is already in the list or needs a new DB entry
		if (demographicPharmacy == null)
		{
			targetPharmacy = new DemographicPharmacy();
			targetPharmacy.setAddDate(new Date());
			targetPharmacy.setStatus(DemographicPharmacy.ACTIVE);
			targetPharmacy.setDemographicNo(demographicNo);
			targetPharmacy.setPharmacyId(pharmacyId);
			targetPharmacy.setPreferredOrder(preferredOrder);
			persist(targetPharmacy);
		}
		else
		{
			targetPharmacy = demographicPharmacy;
			results.remove(targetPharmacy);
		}
		// Now that we know the list no longer contains the pharmacy being modified, add it to the correct spot
		results.add(preferredOrder - 1, targetPharmacy);

		// Use the list's order to re-assign the correct preferredPharmacy order (index+1 for every node)
		for (DemographicPharmacy demographicPharmacy2 : results)
		{
			if (demographicPharmacy2.getPreferredOrder() - 1 == results.indexOf(demographicPharmacy2))
			{
				continue;
			}
			int newOrder = results.indexOf(demographicPharmacy2) + 1;
			demographicPharmacy2.setPreferredOrder(newOrder);
			if (newOrder > 10)
			{
				demographicPharmacy2.setStatus(DemographicPharmacy.INACTIVE);
			}

			merge(demographicPharmacy2);
		}

		return targetPharmacy;
	}
	
	public void unlinkPharmacy(Integer pharmacyId, Integer demographicNo ) {
		String sql = "select x from DemographicPharmacy x where x.status = ?1 and x.demographicNo = ?2 and x.pharmacyId = ?3";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, DemographicPharmacy.ACTIVE);
		query.setParameter(2, demographicNo);
		query.setParameter(3, pharmacyId);
		DemographicPharmacy demographicPharmacy = getSingleResultOrNull(query);
		
		if( demographicPharmacy != null ) {
			
			demographicPharmacy.setStatus(DemographicPharmacy.INACTIVE);
		    merge(demographicPharmacy);
		    
			sql = "select x from DemographicPharmacy x where x.status = ?1 and x.demographicNo = ?2 and x.preferredOrder > ?3";
			query = entityManager.createQuery(sql);
			query.setParameter(1, DemographicPharmacy.ACTIVE);
			query.setParameter(2, demographicNo);
			query.setParameter(3, demographicPharmacy.getPreferredOrder());
						
			@SuppressWarnings("unchecked")
            List<DemographicPharmacy> demographicPharmacies = query.getResultList();
			
			int preferredOrder;
			for( DemographicPharmacy demographicPharmacy2 : demographicPharmacies ) {
				preferredOrder = demographicPharmacy2.getPreferredOrder();
				--preferredOrder;				
				demographicPharmacy2.setPreferredOrder(preferredOrder);
				merge(demographicPharmacy2);
			}
			
		}
		else {
			MiscUtils.getLogger().error("UNKNOWN PHARMACY TO UNLINK");
		}
	}

	public List<DemographicPharmacy> findByDemographicId(Integer demographicNo) {
		String sql = "select x from DemographicPharmacy x where x.status=?1 and x.demographicNo=?2 order by x.preferredOrder";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, DemographicPharmacy.ACTIVE);
		query.setParameter(2, demographicNo);
		@SuppressWarnings("unchecked")
		List<DemographicPharmacy> results = query.getResultList();
		return results;
	}

	@SuppressWarnings("unchecked")
    public List<DemographicPharmacy> findAllByDemographicId(Integer demographicNo) {
		Query query = createQuery("dp", "dp.demographicNo = :demoNo AND dp.status = 1");
		query.setParameter("demoNo", demographicNo);
		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
    public List<DemographicPharmacy> findAllByPharmacyId(Integer pharmacyId) {
		
		String sql = "select x from DemographicPharmacy x where x.pharmacyId = ?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, pharmacyId);
		
		return query.getResultList();
		
	}
}
