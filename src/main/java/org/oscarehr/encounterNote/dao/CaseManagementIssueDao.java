/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.encounterNote.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.encounterNote.model.CaseManagementIssue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementIssueDao")
public class CaseManagementIssueDao extends AbstractDao<CaseManagementIssue>
{
	public CaseManagementIssueDao()
	{
		super(CaseManagementIssue.class);
	}

	public CaseManagementIssue findByIssueCode(Integer demographicId, String code)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model.CaseManagementIssue x " +
				"INNER JOIN x.issue i " +
				"INNER JOIN x.demographic d " +
				"WHERE d.demographicId = :demographicId " +
				"AND i.code = :code " +
				"ORDER BY x.id ASC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("demographicId", demographicId);
		query.setParameter("code", code);
		query.setMaxResults(1);

		// allow the case where multiples of an issue exist, but always use the lowest ID
		List<CaseManagementIssue> resultList = query.getResultList();
		if(resultList.isEmpty())
		{
			return null;
		}
		return resultList.get(0);
	}

	public CaseManagementIssue findByIssueId(Long issueId)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model.CaseManagementIssue x " +
				"WHERE x.issue.issueId = :issueId " +
				"ORDER BY x.id ASC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("issueId", issueId);
		query.setMaxResults(1);

		return getSingleResultOrNull(query);
	}

	public CaseManagementIssue findByIssueId(Integer demographicId, Long issueId)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model.CaseManagementIssue x " +
				"INNER JOIN x.issue i " +
				"INNER JOIN x.demographic d " +
				"WHERE d.demographicId = :demographicId " +
				"AND x.issue.issueId = :issueId " +
				"ORDER BY x.id ASC";

		Query query = entityManager.createQuery(queryString);
		query.setParameter("demographicId", demographicId);
		query.setParameter("issueId", issueId);
		query.setMaxResults(1);

		// allow the case where multiples of an issue exist, but always use the lowest ID
		List<CaseManagementIssue> resultList = query.getResultList();
		if(resultList.isEmpty())
		{
			return null;
		}

		return resultList.get(0);
	}
}
