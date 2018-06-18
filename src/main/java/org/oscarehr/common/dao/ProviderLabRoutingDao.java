/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.ProviderLabRoutingModel;
import org.oscarehr.common.model.ProviderInboxItem;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import org.apache.commons.lang.StringUtils;

@Repository
@Transactional
public class ProviderLabRoutingDao extends AbstractDao<ProviderLabRoutingModel> {

	public ProviderLabRoutingDao() {
		super(ProviderLabRoutingModel.class);
	}

	@SuppressWarnings("unchecked")
    private List<ProviderLabRoutingModel> getProviderLabRoutings(String labNo, String labType, String providerNo, String status) {
		labType = StringUtils.trimToNull(labType);
		providerNo = StringUtils.trimToNull(providerNo);
		status = StringUtils.trimToNull(status);

		Query q = entityManager.createQuery("SELECT x FROM " +
											modelClass.getName() +
											" x WHERE (:labNo IS NULL OR x.labNo=:labNo) " +
											"AND (:labType IS NULL OR x.labType=:labType) " +
											"AND (:providerNo IS NULL OR x.providerNo=:providerNo) " +
											"AND (:status IS NULL OR x.status=:status)");
		q.setParameter("labNo", labNo);
		q.setParameter("labType", labType);
		q.setParameter("providerNo", providerNo);
		q.setParameter("status", status);

		return q.getResultList();
	}

	public List<ProviderLabRoutingModel> getProviderLabRoutingDocuments(String labNo) {
		return getProviderLabRoutings(labNo, "DOC", null, null);
	}

	public List<ProviderLabRoutingModel> getProviderLabRoutingForLabProviderType(String labNo, String providerNo, String labType) {
		return getProviderLabRoutings(labNo, labType, providerNo, null);
	}

	public List<ProviderLabRoutingModel> getProviderLabRoutingForLabAndType(String labNo, String labType) {
		return getProviderLabRoutings(labNo, labType, null, ProviderInboxItem.NEW);
	}

	public List<ProviderLabRoutingModel> findByStatusANDLabNoType(int labNo, String labType, String status) {
		Query query = entityManager.createQuery("SELECT x FROM " + modelClass.getName() +" x WHERE x.labNo = :labNo AND x.labType = :labType AND x.status = :status");
		query.setParameter("labNo", labNo);
		query.setParameter("labType", labType);
		query.setParameter("status", status);
		return query.getResultList();
	}

	public void updateStatus(String labNo, String labType) {
		int labNoInt = Integer.parseInt(labNo);
		String updateString = "UPDATE " + modelClass.getName() + " x set x.status='N' WHERE x.labNo=:labNo AND x.labType=:labType";

		Query query = entityManager.createQuery(updateString);
		query.setParameter("labNo", labNoInt);
		query.setParameter("labType", labType);

		query.executeUpdate();
	}

	public void updateStatus(String newStatus, String labNo, String labType, String providerNo, String oldStatus) {

		String updateString = "UPDATE providerLabRouting set status = ? WHERE provider_no = ? AND lab_no = ? AND lab_type= ? AND status = ?";

		Query query = entityManager.createQuery(updateString);

        int paramIndex = 1;
        query.setParameter(paramIndex++, "N");
        query.setParameter(paramIndex++, providerNo);
        query.setParameter(paramIndex++, labNo);
        query.setParameter(paramIndex++, labType);
        query.setParameter(paramIndex++, "F");

		query.executeUpdate();
	}

}