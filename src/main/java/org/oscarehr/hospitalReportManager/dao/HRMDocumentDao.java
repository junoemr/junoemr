/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package org.oscarehr.hospitalReportManager.dao;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.hospitalReportManager.model.HRMDocument;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class HRMDocumentDao extends AbstractDao<HRMDocument> {

	public HRMDocumentDao() {
	    super(HRMDocument.class);
    }
	
	public List<HRMDocument> findById(int id) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.id=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, id);

		return query.getResultList();
	}

	public List<HRMDocument> findByDemographicId(Integer demographicId)
	{
		String sql = "SELECT x FROM " + this.modelClass.getName() + " x " +
				"INNER JOIN x.documentToDemographicList d " +
				"WHERE d.demographic.id = :demographicId " +
				"ORDER BY x.reportDate DESC, x.timeReceived DESC";
		Query query = entityManager.createQuery(sql);
		query.setParameter("demographicId", demographicId);

		return query.getResultList();
	}
	
	public List<HRMDocument> findAll(int offset, int limit) {
		String sql = "select x from " + this.modelClass.getName() + " x order by x.id";
		Query query = entityManager.createQuery(sql);
		query.setFirstResult(offset);
		query.setMaxResults(limit);

		return query.getResultList();
	}
	
	
	public List<Integer> findByHash(String hash) {
		String sql = "select distinct x.id from " + this.modelClass.getName() + " x where x.reportHash=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);
		List<Integer> matches = query.getResultList();
		return matches;
	}

	public List<Integer> findByMessageUniqueId(String uniqueId) {
		String sql = "select distinct x.id from " + this.modelClass.getName() + " x where x.messageUniqueId = :uniqueId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("uniqueId", uniqueId);
		List<Integer> matches = query.getResultList();
		return matches;
	}
	
	public List<HRMDocument> findByNoTransactionInfoHash(String hash) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.reportLessTransactionInfoHash=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);

		return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
    public List<Integer> findAllWithSameNoDemographicInfoHash(String hash) {
		String sql = "select distinct x.parentReport from " + this.modelClass.getName() + " x where x.reportLessDemographicInfoHash=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hash);
		List<Integer> matches = query.getResultList();
		
		if (matches != null && matches.size() == 1 && matches.get(0) == null) {
			sql = "select distinct x.id from " + this.modelClass.getName() + " x where x.reportLessDemographicInfoHash=?1";
			query = entityManager.createQuery(sql);
			query.setParameter(1, hash);
			matches = query.getResultList();
		}
		return matches;
	}
	
	@SuppressWarnings("unchecked")
    public List<HRMDocument> findAllDocumentsWithRelationship(Integer docId) {
		List<HRMDocument> documentsWithRelationship = new LinkedList<HRMDocument>();
		// Get the document that was specified first
		HRMDocument firstDocument = this.find(docId);
		if (firstDocument != null) {
			String sql = null;
			Query query = null;
			if (firstDocument.getParentReport() != null) {
				// This is a child report; get the parent and all siblings of this report
				sql = "select x from " + this.modelClass.getName() + " x where x.id = ?1 order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getParentReport());
				documentsWithRelationship.addAll(query.getResultList());
				
				sql = "select x from " + this.modelClass.getName() + " x where x.parentReport = ?1 order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getParentReport());
				documentsWithRelationship.addAll(query.getResultList());

				
			} else {
				// This is a parent report
				sql = "select x from " + this.modelClass.getName() + " x where x.parentReport = ?1 order by x.id asc";
				query = entityManager.createQuery(sql);
				query.setParameter(1, firstDocument.getId());
				documentsWithRelationship = query.getResultList();
			}
		
		}

		return documentsWithRelationship;
		
	}
	
	public List<HRMDocument> getAllChildrenOf(Integer docId) {
		String sql = "select x from " + this.modelClass.getName() + " x where x.parentReport=?1 order by id asc";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, docId);
		@SuppressWarnings("unchecked")
		List<HRMDocument> documents = query.getResultList();
		return documents;
    }
	
}