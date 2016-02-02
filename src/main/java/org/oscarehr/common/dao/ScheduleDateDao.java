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
	 * @return
	 */
	public List<ScheduleDate> findByProviderListAndDateRange(List<String> providerNos, Date startDate, Date endDate, int limit) {
		
		if (providerNos == null || providerNos.isEmpty()) {
			return new ArrayList<ScheduleDate>();
		}
		
		String queryString = "SELECT s FROM ScheduleDate s WHERE s.providerNo IN (";
		
		for(int i=0; i< providerNos.size(); i++) {
			queryString += "?";
			if ( i != providerNos.size()-1) {
				queryString += ", ";
			}
		}
		queryString += ") AND s.date >= ? and s.date <= ? AND status = ? ";
		queryString += "ORDER BY s.date, id ";
		
		Query query = entityManager.createQuery( queryString );
		if(limit > 0) {
			query.setMaxResults(limit);
		}
		
		for(int i=0; i< providerNos.size(); i++) {
			query.setParameter(i+1, providerNos.get(i));
		}
		int index = providerNos.size();
		query.setParameter(index+1, startDate);
		query.setParameter(index+2, endDate);
		query.setParameter(index+3, 'A');
		
		logger.debug("QUERY: " + queryString);

        @SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
}
