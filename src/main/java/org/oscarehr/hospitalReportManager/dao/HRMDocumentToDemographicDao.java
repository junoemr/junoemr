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
import org.oscarehr.hospitalReportManager.model.HRMDocumentToDemographic;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class HRMDocumentToDemographicDao extends AbstractDao<HRMDocumentToDemographic>
{
	public HRMDocumentToDemographicDao()
	{
		super(HRMDocumentToDemographic.class);
	}

	public List<HRMDocumentToDemographic> findByDemographicNo(Integer demographicNo)
	{
		String sql = "select x from " + this.modelClass.getName() + " x where x.demographicNo=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToDemographic> documentToDemographics = query.getResultList();
		return documentToDemographics;
	}

	public List<HRMDocumentToDemographic> findByHrmDocumentId(Integer hrmDocumentId)
	{
		String sql = "select x from " + this.modelClass.getName() + " x where x.hrmDocumentId=?1";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, hrmDocumentId);
		@SuppressWarnings("unchecked")
		List<HRMDocumentToDemographic> documentToDemographics = query.getResultList();
		return documentToDemographics;
	}

	public HRMDocumentToDemographic findByHrmDocumentIdAndDemographicNo(Integer hrmDocumentId, Integer demographicId)
	{
		String sql = "SELECT x FROM " + this.modelClass.getName() + " x WHERE x.hrmDocumentId=:documentId AND x.demographicNo=:demographicId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("documentId", hrmDocumentId);
		query.setParameter("demographicId", demographicId);

		return this.getSingleResultOrNull(query);
	}
}
