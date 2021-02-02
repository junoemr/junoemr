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


package org.oscarehr.schedule.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.schedule.model.ScheduleDate;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class ScheduleDateDao extends AbstractDao<ScheduleDate>
{

	public ScheduleDateDao() {
		super(ScheduleDate.class);
	}

	public ScheduleDate findByProviderNoAndDate(String providerNo, Date date) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=?1 and s.date=?2 and s.status=?3");
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, 'A');

		return(getSingleResultOrNull(query));
	}

	public ScheduleDate findByProviderNoSiteAndDate(String providerNo, String site, Date date) {
		Query query = entityManager.createQuery("select sd from ScheduleDate sd where sd.providerNo=:providerNo and sd.date=:date and sd.status=:status and sd.site.name = :site");
		query.setParameter("providerNo", providerNo);
		query.setParameter("date", date);
		query.setParameter("status", 'A');
		query.setParameter("site", site);

		return(getSingleResultOrNull(query));
	}

	public List<ScheduleDate> findByProviderPriorityAndDateRange(String providerNo, char priority, Date date, Date date2) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=?1 and s.priority=?2 and s.date>=?3 and s.date <=?4");
		query.setParameter(1, providerNo);
		query.setParameter(2, priority);
		query.setParameter(3, date);
		query.setParameter(4, date2);

		
        List<ScheduleDate> results = query.getResultList();
		return results;
	}

	public List<ScheduleDate> findByProviderAndDateRange(String providerNo, Date date, Date date2) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.providerNo=?1 and s.date>=?2 and s.date <=?3");
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, date2);

		
        List<ScheduleDate> results = query.getResultList();
		return results;
	}

	public List<ScheduleDate> search_scheduledate_c(String providerNo) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.priority='c' and s.status = 'A' and s.providerNo=?1");
		query.setParameter(1, providerNo);
		
		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
	
	public List<ScheduleDate> search_numgrpscheduledate(String myGroupNo, Date sDate) {
		Query query = entityManager.createQuery("select s from MyGroup m, ScheduleDate s where m.id.myGroupNo = ?1 and s.date=?2 and m.id.providerNo = s.providerNo and s.available = '1' and s.status='A'");
		query.setParameter(1, myGroupNo);
		query.setParameter(2, sDate);
		
		
		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
	
	public List<Object[]> search_appttimecode(Date sDate, String providerNo) {
		Query query = entityManager.createQuery("FROM ScheduleTemplate st, ScheduleDate sd WHERE st.id.name=sd.hour and sd.date=?1 and sd.providerNo=?2 and sd.status='A' and (st.id.providerNo = sd.providerNo or st.id.providerNo='Public')");
		query.setParameter(1, sDate);
		query.setParameter(2, providerNo);
		
		
		@SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
		return results;
	}
	
	public List<ScheduleDate> search_scheduledate_teamp(Date date, Date date2, Character status, List<String> providerNos) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.date>=:sdate and s.date <=:edate and s.status=:status and s.providerNo in (:providers) order by s.date");
		query.setParameter("sdate", date);
		query.setParameter("edate", date2);
		query.setParameter("status", status);
		query.setParameter("providers", providerNos);

		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
	
	public List<ScheduleDate> search_scheduledate_datep(Date date, Date date2, Character status) {
		Query query = entityManager.createQuery("select s from ScheduleDate s where s.date>=:sdate and s.date <=:edate and s.status=:status  order by s.date");
		query.setParameter("sdate", date);
		query.setParameter("edate", date2);
		query.setParameter("status", status);
	
		@SuppressWarnings("unchecked")
        List<ScheduleDate> results = query.getResultList();
		return results;
	}
	

	public List<ScheduleDate> findByProviderStartDateAndPriority(String providerNo, Date apptDate, Character priority) {
		Query query = createQuery("sd", "sd.date = :apptDate AND sd.providerNo = :providerNo AND sd.priority = :priority");
		query.setParameter("providerNo", providerNo);
		query.setParameter("apptDate", apptDate);
		query.setParameter("priority", priority);
		return query.getResultList();
    }
	
	/**
	 * find an ordered list (by date, id) of schedule dates for multiple providers. 
	 * @param providerNos
	 * @param startDate
	 * @param endDate
	 * @param limit (0 or negative for no limit)
	 * @param daysOfWeek list of numbers 1-7 (1=Sunday, 7=Saturday)
	 * @return List of type ScheduleDate
	 */
	public List<ScheduleDate> findByProviderListAndDateRange(List<String> providerNos, Date startDate, Date endDate, int limit, List<Integer> daysOfWeek) {
		
		if (providerNos == null || providerNos.isEmpty()) {
			return new ArrayList<ScheduleDate>();
		}
		
		/* build the query */
		String queryString = "SELECT s FROM ScheduleDate s "
			+ "WHERE status = ?1 AND s.date >= ?2 AND s.date <= ?3 AND s.providerNo IN ( ";

		int paramCount = 4;
		for(int i=0; i< providerNos.size(); i++) {
			queryString += "?" + paramCount++;
			if ( i != providerNos.size()-1) {
				queryString += ", ";
			}
		}
		queryString += " ) ";
		if(daysOfWeek != null && !daysOfWeek.isEmpty()) {
			queryString += "AND DAYOFWEEK(s.date) IN ( ";
			for(int i=0; i< daysOfWeek.size(); i++) {
				queryString += "?" + paramCount++;
				if ( i != daysOfWeek.size()-1) {
					queryString += ", ";
				}
			}
			queryString += " ) ";
		}
		queryString += "ORDER BY s.date, s.providerNo, id ";
		
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
