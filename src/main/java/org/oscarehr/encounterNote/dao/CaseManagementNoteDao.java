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

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteDao")
public class CaseManagementNoteDao extends AbstractDao<CaseManagementNote>
{
	public CaseManagementNoteDao()
	{
		super(CaseManagementNote.class);
	}

	/**
	 * get the most recent revision of the most recent unsigned chart note
	 * @param providerNo
	 * @param demographicNo
	 * @return the note object
	 */
	public CaseManagementNote getNewestUnsignedNote(String providerNo, Integer demographicNo)
	{
		Query query = entityManager.createQuery(
				"SELECT x FROM model.CaseManagementNote x " +
				"LEFT JOIN x.noteLinkList l " +
				"WHERE x.noteId = (SELECT MAX(cmn2.noteId) FROM model.CaseManagementNote cmn2 " +
						"WHERE x.uuid = cmn2.uuid GROUP BY cmn2.uuid) " +
				"AND x.provider.id = :provNo " +
				"AND x.demographic.demographicId = :demoNo " +
				"AND l IS NULL " +
				"AND x.signed = :signed " +
				"ORDER BY x.noteId DESC, l.id DESC");
		query.setParameter("provNo", providerNo);
		query.setParameter("demoNo", demographicNo);
		query.setParameter("signed", false);

		return this.getSingleResultOrNull(query);
	}
}
