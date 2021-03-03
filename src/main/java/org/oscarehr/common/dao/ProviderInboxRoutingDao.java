/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */

package org.oscarehr.common.dao;

import org.oscarehr.common.model.ProviderInboxItem;
import org.springframework.stereotype.Repository;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

import javax.persistence.Query;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author jay gallagher
 */
@Repository
public class ProviderInboxRoutingDao extends AbstractDao<ProviderInboxItem> {

	public ProviderInboxRoutingDao() {
		super(ProviderInboxItem.class);
	}

    public void removeLinkFromDocument(Integer docId, String providerNo) throws SQLException
	{
		CommonLabResultData.updateReportStatus(docId, providerNo, ProviderInboxItem.ARCHIVED, "Archived", LabResultData.DOCUMENT);
    }

	public List<ProviderInboxItem> getProvidersWithRoutingForDocument(String docType, Integer docId) {
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.labType = ?1 and p.labNo = ?2");
		query.setParameter(1, docType);
		query.setParameter(2, docId);

		@SuppressWarnings("unchecked")
		List<ProviderInboxItem> results = query.getResultList();

		return results;
	}

	public ProviderInboxItem getRoutingForProviderLabNo(String labType, Integer labNo, String providerNo) {
		Query query = entityManager.createQuery("SELECT p FROM ProviderInboxItem p WHERE p.providerNo=:providerNo AND p.labType=:labType AND p.labNo=:labNo");
		query.setParameter("providerNo", providerNo);
		query.setParameter("labType", labType);
		query.setParameter("labNo", labNo);

		return(getSingleResultOrNull(query));
	}

	public boolean hasProviderBeenLinkedWithDocument(String docType, Integer docId, String providerNo) {
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.labType = ?1 and p.labNo = ?2 and p.providerNo=?3");
		query.setParameter(1, docType);
		query.setParameter(2, docId);
		query.setParameter(3, providerNo);

		@SuppressWarnings("unchecked")
		List<ProviderInboxItem> results = query.getResultList();

		return (results.size() > 0);
	}

	public int howManyDocumentsLinkedWithAProvider(String providerNo) {
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.providerNo=?1");
		query.setParameter(1, providerNo);

		@SuppressWarnings("unchecked")
		List<ProviderInboxItem> results = query.getResultList();

		return results.size();
	}

	/**
	 * get a list of inbox items for a specific lab/document
	 * @param tableName - the document type
	 * @param tableId - the id
	 * @return - the map, with providerId as the key
	 */
	public Map<String, ProviderInboxItem> findAllByTableId(String tableName, Integer tableId)
	{
		String jpql = "SELECT x \n" +
				"FROM ProviderInboxItem x \n" +
				"WHERE x.labNo = :tableId \n" +
				"AND x.labType = :tableName";
		return entityManager.createQuery(jpql, ProviderInboxItem.class)
				.setParameter("tableName", tableName)
				.setParameter("tableId", tableId)
				.getResultStream()
				.collect(
						Collectors.toMap(
								ProviderInboxItem::getProviderNo,
								inboxItem -> (inboxItem)
						)
				);
	}
}
