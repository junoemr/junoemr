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
import org.oscarehr.encounterNote.model.CaseManagementNote;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteDao")
public class CaseManagementNoteDao extends AbstractDao<CaseManagementNote>
{
	public CaseManagementNoteDao()
	{
		super(CaseManagementNote.class);
	}

	public CaseManagementNote findLatestByUUID(String uuid)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model.CaseManagementNote x " +
				"WHERE x.uuid=:uuid " +
				"ORDER BY x.noteId DESC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("uuid", uuid);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}

	public List<CaseManagementNote> findByDemographicAndIssue(Integer demographicNo, Long issueId)
	{
		String queryString = "SELECT cm FROM model.CaseManagementNote cm " +
				"WHERE cm.demographic.demographicId=:demographicNo " +
				"AND :issueId = ANY (" +
				"	SELECT cin.id.caseManagementIssue.issue.issueId " +
				"	FROM cm.issueNoteList cin" +
				")";

		Query query = entityManager.createQuery(queryString);
		query.setParameter("demographicNo", demographicNo);
		query.setParameter("issueId", issueId);
		return query.getResultList();
	}

	public List<CaseManagementNote> findAllCurrentNotesForDemographic(Integer demographicNo)
	{
		String queryString = "SELECT cm.* " +
				"FROM casemgmt_note cm " +
				"LEFT JOIN casemgmt_note cm2 " +
				"ON cm.uuid = cm2.uuid " +
				"AND cm2.update_date > cm.update_date " +
				"WHERE cm.demographic_no=:demographicNo " +
				"AND cm2.uuid IS NULL " +
				"AND cm.include_issue_innote = false " +
				"GROUP BY cm.uuid " +
				"ORDER BY cm.update_date ASC";

		Query query = entityManager.createNativeQuery(queryString, CaseManagementNote.class);
		query.setParameter("demographicNo", demographicNo);
		List<CaseManagementNote> results = query.getResultList();

		return results;
	}
}
