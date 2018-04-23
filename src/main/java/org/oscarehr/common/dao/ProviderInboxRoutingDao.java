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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.IncomingLabRules;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

import oscar.oscarLab.ca.on.CommonLabResultData;

/**
 *
 * @author jay gallagher
 */
@Repository
public class ProviderInboxRoutingDao extends AbstractDao<ProviderInboxItem> {

	public ProviderInboxRoutingDao() {
		super(ProviderInboxItem.class);
	}
    



    public void removeLinkFromDocument(String docType, String docId, String providerNo) throws SQLException
	{
    	int dId = Integer.parseInt(docId);
		CommonLabResultData.updateReportStatus(dId, providerNo, 'X', "Archived", "DOC");
    }

	public List<ProviderInboxItem> getProvidersWithRoutingForDocument(String docType, String docId) {
		int dId = Integer.parseInt(docId);
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.labType = ? and p.labNo = ?");
		query.setParameter(1, docType);
		query.setParameter(2, dId);

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

	public boolean hasProviderBeenLinkedWithDocument(String docType, String docId, String providerNo) {
		int dId = Integer.parseInt(docId);
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.labType = ? and p.labNo = ? and p.providerNo=?");
		query.setParameter(1, docType);
		query.setParameter(2, dId);
		query.setParameter(3, providerNo);

		@SuppressWarnings("unchecked")
		List<ProviderInboxItem> results = query.getResultList();

		return (results.size() > 0);
	}

	public int howManyDocumentsLinkedWithAProvider(String providerNo) {
		Query query = entityManager.createQuery("select p from ProviderInboxItem p where p.providerNo=?");
		query.setParameter(1, providerNo);

		@SuppressWarnings("unchecked")
		List<ProviderInboxItem> results = query.getResultList();

		return results.size();
	}

	/**
	 * Adds lab results to the provider inbox
	 * 
	 * @param providerNo
	 * 		Provider to add lab results to
	 * @param labNo
	 * 		Document id to be added to the inbox
	 * @param labType
	 * 		Type of the document to be added. Available document types are defined in {@link oscar.oscarLab.ca.on.LabResultData} class.
	 * 
	 */
	// TODO Replace labType parameter with an enum
	@SuppressWarnings("unchecked")
    public void addToProviderInbox(String providerNo, String labNo, String labType) {
		ArrayList<String> listofAdditionalProviders = new ArrayList<String>();
		boolean fileForMainProvider = false;

		try {
			Query rulesQuery = entityManager.createQuery("FROM IncomingLabRules r WHERE r.archive = 0 AND r.providerNo = :providerNo");
			rulesQuery.setParameter("providerNo", providerNo);

			for (IncomingLabRules rules : (List<IncomingLabRules>) rulesQuery.getResultList()) {
				String status = rules.getStatus();
				String frwdProvider = rules.getFrwdProviderNo();

				listofAdditionalProviders.add(frwdProvider);
				if (status != null && status.equals(ProviderInboxItem.FILE)) fileForMainProvider = true;
			}

			ProviderInboxItem p = new ProviderInboxItem();
			p.setProviderNo(providerNo);
			p.setLabNo(labNo);
			p.setLabType(labType);
			p.setStatus(fileForMainProvider ? ProviderInboxItem.FILE : ProviderInboxItem.NEW);

			if (!hasProviderBeenLinkedWithDocument(labType, labNo, providerNo)) persist(p);

			//See if the provider we're adding is already linked with the document
			ProviderInboxItem labForProvider = getRoutingForProviderLabNo(labType, Integer.parseInt(labNo), providerNo);

			//If the document is archived for the provider, move the document from the archive back to their inbox as they've been re-linked to the document
			if (labForProvider.getStatus().equals(ProviderInboxItem.ARCHIVED)) {
				CommonLabResultData.updateReportStatus(Integer.parseInt(labNo), providerNo, 'N', null, labType);
			}

			for (String s : listofAdditionalProviders) {
				if (!hasProviderBeenLinkedWithDocument(labType, labNo, s)) {
					p = new ProviderInboxItem();

					p.setProviderNo(s);
					p.setLabNo(labNo);
					p.setLabType(labType);
					p.setStatus(fileForMainProvider ? ProviderInboxItem.FILE : ProviderInboxItem.NEW);

					persist(p);
				}
			}
		} catch (Exception e) {
			MiscUtils.getLogger().error("Error", e);
		}

	}

}
