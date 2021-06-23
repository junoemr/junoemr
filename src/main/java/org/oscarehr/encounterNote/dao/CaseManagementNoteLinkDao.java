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
import org.oscarehr.encounterNote.model.CaseManagementNoteLink;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
@Transactional
@Repository("encounterNote.dao.CaseManagementNoteLinkDao")
public class CaseManagementNoteLinkDao extends AbstractDao<CaseManagementNoteLink>
{
	public CaseManagementNoteLinkDao()
	{
		super(CaseManagementNoteLink.class);
	}

	public CaseManagementNoteLink findLatestAllergyNoteLinkById(Integer allergyId)
	{
		return this.findLatestByTableAndTableId(CaseManagementNoteLink.ALLERGIES, allergyId);
	}

	public CaseManagementNoteLink findLatestTicklerNoteLinkById(Integer ticklerId)
	{
		return this.findLatestByTableAndTableId(CaseManagementNoteLink.TICKLER, ticklerId);
	}

	/** because notes can have links to other notes! */
	public CaseManagementNoteLink findLatestNoteNoteLinkById(Long noteId)
	{
		return this.findLatestByTableAndTableId(CaseManagementNoteLink.CASEMGMTNOTE, Math.toIntExact(noteId));
	}

	public CaseManagementNoteLink findLatestByTableAndTableId(Integer tableName, Integer tableId)
	{
		// select model name must match specified @Entity name in model object
		String queryString = "SELECT x FROM model_CaseManagementNoteLink x " +
				"WHERE x.tableName=:tableName " +
				"AND x.tableId=:tableId " +
				"ORDER BY x.note.noteId DESC";
		Query query = entityManager.createQuery(queryString);
		query.setParameter("tableName", tableName);
		query.setParameter("tableId", tableId);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}
	public List<CaseManagementNoteLink> findAllNoteLinkByTableIdAndTableName(Integer noteId, Integer tableName)
	{
		// select model name must match specified @Entity name in model object
		String jpql = "SELECT x \n" +
				"FROM model_CaseManagementNoteLink x \n" +
				"WHERE x.tableId = :noteId\n" +
				"AND x.tableName = :tableName\n" +
				"ORDER BY x.note.noteId DESC";

		Query query = entityManager.createQuery(jpql);
		query.setParameter("noteId", noteId);
		query.setParameter("tableName", tableName);

		return query.getResultList();
	}
	public CaseManagementNoteLink getNoteLinkByTableIdAndTableName(Integer noteId, Integer tableName)
	{
		// select model name must match specified @Entity name in model object
		String jpql = "SELECT x \n" +
				"FROM model_CaseManagementNoteLink x \n" +
				"WHERE x.tableId = :noteId\n" +
				"AND x.tableName = :tableName\n" +
				"ORDER BY x.note.noteId DESC";

		Query query = entityManager.createQuery(jpql);
		query.setParameter("noteId", noteId);
		query.setParameter("tableName", tableName);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}
	public CaseManagementNoteLink getNoteLinkByTableIdAndTableName(Integer noteId, Integer tableName, String otherId)
	{
		// select model name must match specified @Entity name in model object
		String jpql = "SELECT x \n" +
				"FROM model_CaseManagementNoteLink x \n" +
				"WHERE x.tableId = :noteId\n" +
				"AND x.tableName = :tableName\n" +
				"AND x.otherId = :otherId\n" +
				"ORDER BY x.note.noteId DESC";

		Query query = entityManager.createQuery(jpql);
		query.setParameter("noteId", noteId);
		query.setParameter("tableName", tableName);
		query.setParameter("otherId", otherId);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}

	/**
	 * query very specific to lab notes. this maps associated notes based for a lab based on the otherId column as a key.
	 * This allows loading all annotations for a lab in a single query
	 * @param tableId - the hl7TextMessage id
	 * @return - map of lab notes
	 */
	public Map<String, CaseManagementNote> getLabNotesByNoteLink(Integer tableId)
	{
		// select model name must match specified @Entity name in model object
		String jpql = "SELECT x \n" +
				"FROM model_CaseManagementNoteLink x \n" +
				"WHERE x.tableId = :tableId \n" +
				"AND x.tableName = :tableName \n" +
				"ORDER BY x.note.noteId DESC";
		return entityManager.createQuery(jpql, CaseManagementNoteLink.class)
				.setParameter("tableId", tableId)
				.setParameter("tableName", CaseManagementNoteLink.HL7LAB)
				.getResultStream()
				.collect(
						Collectors.toMap(
								CaseManagementNoteLink::getOtherId,
								CaseManagementNoteLink::getNote
						)
				);
	}

	public CaseManagementNoteLink getNoteLinkByNoteIdAndTableName(CaseManagementNote note, Integer tableName)
	{
		String jpql = "SELECT c \n" +
				"FROM model_CaseManagementNoteLink c \n" +
				"WHERE c.note = :note\n" +
				"AND c.tableName = :tableName\n";

		Query query = entityManager.createQuery(jpql);
		query.setParameter("note", note);
		query.setParameter("tableName", tableName);
		query.setMaxResults(1);

		return this.getSingleResultOrNull(query);
	}
}
