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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.oscarehr.common.model.ScheduleDate;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ScheduleDateDao extends AbstractDao<ScheduleDate>{

	static Logger logger = MiscUtils.getLogger();
	
	public ScheduleDateDao() {
		super(ScheduleDate.class);
	}

	public ScheduleDate findByProviderNoAndDate(String providerNo, Date date) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=? and s.date=? and s.status=?");
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, 'A');

		return(getSingleResultOrNull(query));
	}

	public List<ScheduleDate> findByProviderPriorityAndDateRange(String providerNo, char priority, Date date, Date date2) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=? and s.priority=? and s.date>=? and s.date <=? and s.status=?");
		query.setParameter(1, providerNo);
		query.setParameter(2, priority);
		query.setParameter(3, date);
		query.setParameter(4, date2);
		query.setParameter(5, 'A');

		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}

	public List<ScheduleDate> findByProviderAndDateRange(String providerNo, Date date, Date date2) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=? and s.date>=? and s.date <=?");
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, date2);

		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
	
	/**
	 * find an ordered list (by date, id) of schedule dates for multiple providers. 
	 * @param providerNos
	 * @param startDate
	 * @param endDate
	 * @param limit (0 or negative for no limit)
	 * @param daysOfWeek list of numbers 1-7 (1=Sunday, 7=Saturday)
	 * @return
	 */
	public List<ScheduleDate> findByProviderListAndDateRange(List<String> providerNos, Date startDate, Date endDate, int limit, List<Integer> daysOfWeek) {
		
		if (providerNos == null || providerNos.isEmpty()) {
			return new ArrayList<ScheduleDate>();
		}
		
		/* build the query */
		String queryString = "SELECT s FROM ScheduleDate s "
			+ "WHERE status = ? AND s.date >= ? AND s.date <= ? AND s.providerNo IN ( ";
		
		for(int i=0; i< providerNos.size(); i++) {
			queryString += "?";
			if ( i != providerNos.size()-1) {
				queryString += ", ";
			}
		}
		queryString += " ) ";
		if(daysOfWeek != null && !daysOfWeek.isEmpty()) {
			queryString += "AND DAYOFWEEK(s.date) IN ( ";
			for(int i=0; i< daysOfWeek.size(); i++) {
				queryString += "?";
				if ( i != daysOfWeek.size()-1) {
					queryString += ", ";
				}
			}
			queryString += " ) ";
		}
		queryString += "ORDER BY s.date, id ";
		
		logger.debug("QUERY: " + queryString);
		Query query = entityManager.createQuery( queryString );
		if(limit > 0) {
			query.setMaxResults(limit);
		}
		
		/* set the query parameters */
		int index = 1;
		query.setParameter(index++, 'A');
		query.setParameter(index++, startDate);
		query.setParameter(index++, endDate);
		
		for(int i=0; i< providerNos.size(); i++) {
			query.setParameter(index++, providerNos.get(i));
		}

		if(daysOfWeek != null && !daysOfWeek.isEmpty()) {
			for(int i=0; i< daysOfWeek.size(); i++) {
				query.setParameter(index++, daysOfWeek.get(i));
			}
		}

        @SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
}
