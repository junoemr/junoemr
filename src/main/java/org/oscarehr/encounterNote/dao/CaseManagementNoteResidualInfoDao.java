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
import org.oscarehr.encounterNote.model.CaseManagementNoteResidualInfo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteResidualInfoDao")
public class CaseManagementNoteResidualInfoDao extends AbstractDao<CaseManagementNoteResidualInfo>
{
	public CaseManagementNoteResidualInfoDao()
	{
		super(CaseManagementNoteResidualInfo.class);
	}

	public List<CaseManagementNoteResidualInfo> findByNoteId(Long noteId)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model_CaseManagementNoteResidualInfo x " +
				"WHERE x.note.noteId=:noteId";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("noteId", noteId);

		return query.getResultList();
	}
}
