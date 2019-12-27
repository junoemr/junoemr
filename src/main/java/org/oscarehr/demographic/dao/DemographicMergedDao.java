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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.dao.SecObjPrivilegeDao;
import org.oscarehr.common.model.SecObjPrivilege;
import org.oscarehr.common.model.SecObjPrivilegePrimaryKey;
import org.oscarehr.demographic.model.DemographicMerged;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicMergedDao extends AbstractDao<DemographicMerged>
{

	private SecObjPrivilegeDao secObjPrivilegeDao = SpringUtils.getBean(SecObjPrivilegeDao.class);

	public DemographicMergedDao() {
		super(DemographicMerged.class);
	}
	
	public List<DemographicMerged> findCurrentByMergedTo(int demographicNo) {
		Query q = entityManager.createQuery("select d from DemographicMerged d where d.mergedTo=? and d.deleted=0");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
	}
	
	public List<DemographicMerged> findCurrentByDemographicNo(int demographicNo) {
		Query q = entityManager.createQuery("select d from DemographicMerged d where d.demographicNo=? and d.deleted=0");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
	}
	
	public List<DemographicMerged> findByDemographicNo(int demographicNo) {
		Query q = entityManager.createQuery("select d from DemographicMerged d where d.demographicNo=?");
		q.setParameter(1, demographicNo);
		
		@SuppressWarnings("unchecked")
		List<DemographicMerged> results = q.getResultList();
		
		return results;
	}

	@SuppressWarnings("unchecked")
    public List<DemographicMerged> findByParentAndChildIds(Integer parentId, Integer childId) {
		Query q = createQuery("d", "d.demographicNo = :childId AND d.mergedTo = :parentId");
		q.setParameter("parentId", parentId);
		q.setParameter("childId", childId);
		return q.getResultList();
    }

	/**
	 * Procedure to merge demographic records. Checks possible error cases then adds a new entry
	 * @param providerNo provider requesting the merge
	 * @param demographicNo demographic whose content is being merged
	 * @param head demographic being merged into
	 * @return true if we successfully merged, false if we ran into an error state
	 */
	public boolean mergeDemographics(String providerNo, Integer demographicNo, Integer head)
	{
		if (demographicNo.equals(head))
		{
			MiscUtils.getLogger().error("Attempted to merge demographicNo " + demographicNo + " to itself");
			return false;
		}

		// check if we have an active entry for this before setting up the merge
		List<DemographicMerged> currentEntries = findByParentAndChildIds(head, demographicNo);
		for (DemographicMerged entry : currentEntries)
		{
			if (entry.getDeleted() == 0)
			{
				MiscUtils.getLogger().error("Demographic has already been merged!");
				return false;
			}
		}

		DemographicMerged demographicMerged = new DemographicMerged();
		demographicMerged.setDemographicNo(demographicNo);
		demographicMerged.setMergedTo(head);
		demographicMerged.setLastUpdateUser(providerNo);
		demographicMerged.setLastUpdateDate(new Date());
		persist(demographicMerged);

		// Relic from the old merge method, unsure if this is needed at all
		SecObjPrivilegePrimaryKey mergeKey = new SecObjPrivilegePrimaryKey("_all","_eChart$" + demographicNo);
		if(secObjPrivilegeDao.find(mergeKey) == null)
		{
			SecObjPrivilege secObjPrivilege = new SecObjPrivilege();
			secObjPrivilege.setId(mergeKey);
			secObjPrivilege.setPrivilege("|or|");
			secObjPrivilege.setPriority(0);
			secObjPrivilege.setProviderNo("0");
			secObjPrivilegeDao.persist(secObjPrivilege);
		}

		return true;
	}
}
