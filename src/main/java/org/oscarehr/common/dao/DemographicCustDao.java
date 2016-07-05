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

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.DemographicCust;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicCustDao extends AbstractDao<DemographicCust> {

	public DemographicCustDao() {
		super(DemographicCust.class);
	}

    public List<DemographicCust> findMultipleMidwife(Collection<Integer> demographicNos, String oldMidwife) {
    	String sql = "select x from DemographicCust x where x.id IN (?1) and x.midwife=?2";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1,demographicNos);
    	query.setParameter(2, oldMidwife);

        @SuppressWarnings("unchecked")
        List<DemographicCust> results = query.getResultList();
        return results;
    }

    public List<DemographicCust> findMultipleResident(Collection<Integer> demographicNos, String oldResident) {
    	String sql = "select x from DemographicCust x where x.id IN (?1) and x.resident=?2";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1,demographicNos);
    	query.setParameter(2, oldResident);

        @SuppressWarnings("unchecked")
        List<DemographicCust> results = query.getResultList();
        return results;
    }

    public List<DemographicCust> findMultipleNurse(Collection<Integer> demographicNos, String oldNurse) {
    	String sql = "select x from DemographicCust x where x.id IN (?1) and x.nurse=?2";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1,demographicNos);
    	query.setParameter(2, oldNurse);

        @SuppressWarnings("unchecked")
        List<DemographicCust> results = query.getResultList();
        return results;
    }

	/*
	public void save(DemographicCust demographicCust){
		this.getHibernateTemplate().saveOrUpdate(demographicCust);
	}
	*/
}
