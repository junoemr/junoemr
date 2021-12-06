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
import org.oscarehr.hospitalReportManager.model.HRMObservation;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
@SuppressWarnings("unchecked")
public class HRMDocumentSubClassDao extends AbstractDao<HRMObservation>
{
	public HRMDocumentSubClassDao()
	{
		super(HRMObservation.class);
	}

	public List<HRMObservation> getSubClassesByDocumentId(Integer id)
	{
		String sql = "SELECT x FROM " + this.modelClass.getName() + " x WHERE x.hrmDocument.id=:documentId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("documentId", id);
		return query.getResultList();
	}

	public List<HRMObservation> getActiveSubClassesByDocumentId(Integer id)
	{
		String sql = "SELECT x FROM " + this.modelClass.getName() + " x WHERE x.hrmDocument.id=:documentId and x.active=:active";
		Query query = entityManager.createQuery(sql);
		query.setParameter("documentId", id);
		query.setParameter("active", true);
		return query.getResultList();
	}

	public boolean setAllSubClassesForDocumentAsInactive(Integer id)
	{
		String sql = "UPDATE " + this.modelClass.getName() + " x SET x.active=:active WHERE x.hrmDocument.id=:documentId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("documentId", id);
		query.setParameter("active", false);
		return query.executeUpdate() > 0;
	}
}