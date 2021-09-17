/**
 * Copyright (c) 2008-2012 Indivica Inc.
 *
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */
package org.oscarehr.healthReportManager.dao;


import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.healthReportManager.model.HRMProviderConfidentialityStatement;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;

@Repository
public class HRMProviderConfidentialityStatementDao extends AbstractDao<HRMProviderConfidentialityStatement>
{

	public HRMProviderConfidentialityStatementDao()
	{
		super(HRMProviderConfidentialityStatement.class);
	}

	public String getConfidentialityStatementForProvider(String providerNo)
	{
		String sql = "SELECT x.statement FROM " + this.modelClass.getName() + " x WHERE x.providerNo=:providerNo";
		Query query = entityManager.createQuery(sql);
		query.setParameter("providerNo", providerNo);
		try
		{
			return (String) query.getSingleResult();
		}
		catch(Exception e)
		{
			// No statement for this provider
			return "";
		}
	}
}
