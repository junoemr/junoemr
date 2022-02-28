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
package org.oscarehr.demographic.dao;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.demographic.entity.DemographicMerged;
import org.oscarehr.security.dao.SecObjPrivilegeDao;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@Transactional
public class DemographicMergedDao extends AbstractDao<DemographicMerged>
{
	@Autowired
	private SecObjPrivilegeDao secObjPrivilegeDao;

	public DemographicMergedDao() {
		super(DemographicMerged.class);
	}
	
	public List<DemographicMerged> findCurrentByMergedTo(int demographicNo)
	{
		Query query = entityManager.createQuery("SELECT d " +
				"FROM DemographicMerged d " +
				"WHERE d.mergedTo=:mergedTo " +
				"AND d.deleted=false");
		query.setParameter("mergedTo", demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = query.getResultList();
		
		return results;
	}
	
	public List<DemographicMerged> findCurrentByDemographicNo(int demographicNo) {
		Query q = entityManager.createQuery("select d from DemographicMerged d where d.demographicNo=?1 and d.deleted=0");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
	}

	public List<DemographicMerged> findByDemographicNo(int demographicNo)
	{
		Query query = entityManager.createQuery("SELECT d " +
				"FROM DemographicMerged d " +
				"WHERE d.demographicNo=:demographicNo");
		query.setParameter("demographicNo", demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = query.getResultList();
		
		return results;
	}

	@SuppressWarnings("unchecked")
    public List<DemographicMerged> findByParentAndChildIds(Integer parentId, Integer childId)
	{
		Query query = entityManager.createQuery("SELECT d " +
				"FROM DemographicMerged d " +
				"WHERE d.demographicNo=:childId " +
				"AND d.mergedTo=:parentId " +
				"AND d.deleted=false");
		query.setParameter("parentId", parentId);
		query.setParameter("childId", childId);
		return query.getResultList();
    }

	/**
	 * There should only ever be one active entry at a time for a demographic being merged into another.
	 * Get the current active entry.
	 * @param demographicNo demographic who we think maybe currently merged
	 * @return corresponding entity if exists, null if not merged
	 */
	public DemographicMerged getCurrentHead(Integer demographicNo)
	{
		Query query = entityManager.createQuery("SELECT d " +
				"FROM DemographicMerged d " +
				"WHERE d.demographicNo = :demographicNo " +
				"AND d.deleted=false");
		query.setParameter("demographicNo", demographicNo);

		return getSingleResultOrNull(query);
	}

	/**
	 * Procedure to merge demographic records. Checks possible error cases then adds a new entry
	 * @param providerNo provider requesting the merge
	 * @param demographicNo demographic whose content is being merged
	 * @param head demographic being merged into
	 * @return true if we successfully merged, false if we ran into an error state
	 */
	public synchronized boolean mergeDemographics(String providerNo, Integer demographicNo, Integer head)
	{
		if (demographicNo.equals(head))
		{
			MiscUtils.getLogger().error("Attempted to merge demographicNo " + demographicNo + " to itself");
			return false;
		}

		// check if we have an active entry for this before setting up the merge
		List<DemographicMerged> currentEntries = findByParentAndChildIds(head, demographicNo);
		if (currentEntries.size() > 0)
		{
			MiscUtils.getLogger().error("Demographic has already been merged!");
			return false;
		}

		DemographicMerged demographicMerged = new DemographicMerged();
		demographicMerged.setDemographicNo(demographicNo);
		demographicMerged.setMergedTo(head);
		demographicMerged.setLastUpdateUser(providerNo);
		demographicMerged.setLastUpdateDate(new Date());
		persist(demographicMerged);

		return true;
	}

	/**
	 * Procedure to unmerge demographic records.
	 * Only need the demographic number that's being un-merged, it should only ever be merged into
	 * a single other demographic at any other time.
	 * @param providerNo provider requesting the un-merge
	 * @param demographicNo demographic who was previously merged that is being split off
	 * @throws NoSuchElementException if we can't find an active merge for demographic
	 */
	public void unmergeDemographics(String providerNo, Integer demographicNo)
		throws NoSuchElementException
	{
		DemographicMerged demographicMerged = getCurrentHead(demographicNo);
		if (demographicMerged == null)
		{
			throw new NoSuchElementException();
		}
		demographicMerged.setLastUpdateDate(new Date());
		demographicMerged.setLastUpdateUser(providerNo);
		demographicMerged.delete();
		merge(demographicMerged);
	}
}
