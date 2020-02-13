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

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.CtlBillingServicePremium;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class CtlBillingServicePremiumDao extends AbstractDao<CtlBillingServicePremium>{

	public CtlBillingServicePremiumDao() {
		super(CtlBillingServicePremium.class);
	}
	
	public List<CtlBillingServicePremium> findByServiceCode(String serviceCode) {
		Query q = entityManager.createQuery("select x from CtlBillingServicePremium x where x.serviceCode=?1");
		q.setParameter(1, serviceCode);
		
		
		List<CtlBillingServicePremium> results = q.getResultList();
		
		return results;
	}
	
	public List<CtlBillingServicePremium> findByStatus(String status) {
		Query q = entityManager.createQuery("select x from CtlBillingServicePremium x where x.status=?1");
		q.setParameter(1, status);
		
		
		List<CtlBillingServicePremium> results = q.getResultList();
		
		return results;
	}
	
	public List<Object[]> search_ctlpremium(String status) {
		Query q = entityManager.createNativeQuery("SELECT b.service_code, c.description FROM ctl_billingservice_premium b INNER JOIN billingservice c ON b.service_code=c.service_code " +
									"WHERE b.status=?1 AND c.billingservice_date = (SELECT MAX(c2.billingservice_date) FROM billingservice c2 " +
									"WHERE c2.service_code = c.service_code AND c2.billingservice_date <= now())");
		q.setParameter(1, status);
		
		List<Object[]> results = q.getResultList();
		
		return results;
	}
	
	public List<CtlBillingServicePremium> findByServceCodes(List<String> serviceCodes) {
		Query query = createQuery("p", "p.serviceCode in (:serviceCodes)");
		query.setParameter("serviceCodes", serviceCodes);
		return query.getResultList();
	}
}
