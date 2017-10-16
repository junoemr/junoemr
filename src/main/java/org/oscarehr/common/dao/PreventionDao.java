/**
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */
package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.Prevention;
import org.springframework.stereotype.Repository;

@Repository
public class PreventionDao extends AbstractDao<Prevention> {

	public PreventionDao() {
		super(Prevention.class);
	}
	
	public List<Prevention> findByDemographicId(Integer demographicId) {
		Query query = entityManager.createQuery("select x from Prevention x where x.demographicId=?1");
		query.setParameter(1, demographicId);

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}

	public List<Prevention> findByDemographicIdAfterDatetime(Integer demographicId, Date dateTime) {
		Query query = entityManager.createQuery("select x from Prevention x where x.demographicId=?1 and x.lastUpdateDate>=?2");
		query.setParameter(1, demographicId);
		query.setParameter(2, dateTime);

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}
	
	public List<Prevention> findNotDeletedByDemographicId(Integer demographicId) {
		Query query = entityManager.createQuery("select x from Prevention x where x.demographicId=?1 and x.deleted=?2");
		query.setParameter(1, demographicId);
		query.setParameter(2, '0');

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}
	
	public List<Prevention> findByTypeAndDate(String preventionType, Date startDate, Date endDate) {
		Query query = entityManager.createQuery("select x from Prevention x where x.preventionType=?1 and x.preventionDate>=?2 and x.preventionDate<=?3 and x.deleted='0' and x.refused='0' order by x.preventionDate");
		query.setParameter(1, preventionType);
		query.setParameter(2, startDate);
		query.setParameter(3, endDate);
		
		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}
	
	public List<Prevention> findByTypeAndDemoNo(String preventionType, Integer demoNo) {
		Query query = entityManager.createQuery("select x from Prevention x where x.preventionType=?1 and x.demographicId=?2 and x.deleted='0' order by x.preventionDate");
		query.setParameter(1, preventionType);
		query.setParameter(2, demoNo);
		
		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}

	public Prevention findMostRecentByTypeAndDemoNo(String preventionType, Integer demoNo) {
		Query query = entityManager.createQuery("SELECT x FROM Prevention x " +
				"WHERE x.preventionType = :type " +
				"AND x.demographicId = :demographicNo " +
				"AND x.deleted='0' " +
				"ORDER BY x.preventionDate DESC, x.id DESC");

		query.setParameter("type", preventionType);
		query.setParameter("demographicNo", demoNo);

		query.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();
		return (results.isEmpty()) ? null : results.get(0);
	}
}
