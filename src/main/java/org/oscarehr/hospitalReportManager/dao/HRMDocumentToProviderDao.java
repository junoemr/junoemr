/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.hospitalReportManager.model.HRMDocumentToProvider;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
public class HRMDocumentToProviderDao extends AbstractDao<HRMDocumentToProvider> {

	public HRMDocumentToProviderDao() {
		super(HRMDocumentToProvider.class);
	}

	public List<HRMDocumentToProvider> findAllUnsigned(Integer page, Integer pageSize) {
		String sql = "select x from " + this.modelClass.getName() + " x where (x.signedOff IS NULL or x.signedOff = 0)";
		Query query = entityManager.createQuery(sql);
		query.setMaxResults(pageSize);
		query.setFirstResult(page*pageSize);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}

	public List<HRMDocumentToProvider> findByProviderNo(String providerNo, Integer page, Integer pageSize) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.providerNo=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setMaxResults(pageSize);
		query.setFirstResult(page*pageSize);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}

	public List<HRMDocumentToProvider> findByProviderNoLimit(String providerNo, Date newestDate, Date oldestDate, Boolean viewed, Boolean signedOff) {
		String sql = "select x from " + this.modelClass.getName() + " x, HRMDocument h where x.hrmDocument.id=h.id and x.providerNo like ?1";
		if (newestDate != null)
			sql += " and h.reportDate <= :newest";
		if (oldestDate != null)
			sql += " and h.reportDate >= :oldest";
		if (viewed != null)
			sql += " and x.viewed = :viewed";
		if (signedOff != null)
			sql += " and x.signedOff = :signedOff";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);

		if (newestDate != null)
			query.setParameter("newest", newestDate);

		if (oldestDate != null)
			query.setParameter("oldest", oldestDate);

		if (viewed != null)
			query.setParameter("viewed", viewed);

		if (signedOff != null)
			query.setParameter("signedOff", signedOff);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}


	public List<HRMDocumentToProvider> findByHrmDocumentId(Integer hrmDocumentId) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.hrmDocument.id=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hrmDocumentId);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}

	public List<HRMDocumentToProvider> findByHrmDocumentIdNoSystemUser(Integer hrmDocumentId)
	{
		String sql = "select x from " + this.modelClass.getName() + " x where x.hrmDocument.id=:hrmDocumentId and x.providerNo != :providerNo";
		Query query = entityManager.createQuery(sql);
		query.setParameter("hrmDocumentId", hrmDocumentId);
		query.setParameter("providerNo", "-1");
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}

	public HRMDocumentToProvider findByHrmDocumentIdAndProviderNo(Integer hrmDocumentId, String providerId)
	{
		String sql = "SELECT x FROM " + this.modelClass.getName() + " x WHERE x.hrmDocument.id=:documentId AND x.providerNo=:providerId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("documentId", hrmDocumentId);
		query.setParameter("providerId", providerId);

		return this.getSingleResultOrNull(query);
	}
	
	public List<HRMDocumentToProvider> findByHrmDocumentIdAndProviderNoList(Integer hrmDocumentId, String providerNo) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.hrmDocument.id=?1 and x.providerNo=?2";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hrmDocumentId);
		query.setParameter(2, providerNo);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToProvider> documentToProviders = query.getResultList();
		return documentToProviders;
	}
}
