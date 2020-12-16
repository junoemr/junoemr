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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.schedule.model.RSchedule;
import org.springframework.stereotype.Repository;

@Repository(value="rScheduleDao")
@SuppressWarnings("unchecked")
public class RScheduleDao extends AbstractDao<RSchedule>
{

	public RScheduleDao() {
		super(RSchedule.class);
	}

	public List<RSchedule> findByProviderAvailableAndDate(String providerNo, String available, Date startDate) {
		Query query = entityManager.createQuery("SELECT s FROM RSchedule s WHERE s.providerNo=:providerNo AND s.available=:available AND s.sDate=:startDate");
		query.setParameter("providerNo", providerNo);
		query.setParameter("available", available);
		query.setParameter("startDate", startDate);

        return query.getResultList();
	}
	
	public Long search_rschedule_overlaps(String providerNo, Date d1, Date d2, Date d3, Date d4, Date d5, Date d6, Date d7, Date d8, Date d9, Date d10, Date d11, Date d12,Date d13,Date d14) {
		Query query = entityManager.createQuery("select count(r.id) from RSchedule r where r.providerNo=?1 and ((r.sDate <?2 and r.eDate >=?3) or (?4 < r.sDate and r.sDate < ?5) or (?6 < r.eDate and r.eDate <= ?7) or ( ?8 < r.sDate and r.eDate <= ?9) or (r.sDate = ?10 and r.sDate = ?11) or (r.eDate = ?12 and r.eDate <= ?13) or (r.sDate = ?14 and r.eDate != ?15)) and r.status = 'A'");
		query.setParameter(1, providerNo);
		query.setParameter(2, d1);
		query.setParameter(3, d2);
		query.setParameter(4, d3);
		query.setParameter(5, d4);
		query.setParameter(6, d5);
		query.setParameter(7, d6);
		query.setParameter( 8, d7);
		query.setParameter(9, d8);
		query.setParameter(10, d9);
		query.setParameter(11, d10);
		query.setParameter(12, d11);
		query.setParameter(13, d12);
		query.setParameter(14, d13);
		query.setParameter(15, d14);

		Long results = (Long) query.getSingleResult();
		return results;
	}


	public Long search_rschedule_exists(String providerNo, Date startDate, Date endDate)
	{
		Query query = entityManager.createQuery("SELECT count(r.id) FROM RSchedule r WHERE r.providerNo=:providerNo and r.sDate =:startDate AND r.eDate =:endDate and r.status = 'A'");
		query.setParameter("providerNo", providerNo);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return (Long) query.getSingleResult();
	}
	
	public RSchedule search_rschedule_current(String providerNo, String available, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=?1 and s.available=?2 and s.sDate <= ?3 and s.status='A' order by s.sDate desc");
		query.setParameter(1, providerNo);
		query.setParameter(2, available);
		query.setParameter(3, sdate);
		query.setMaxResults(1);

		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	
	public List<RSchedule> search_rschedule_future(String providerNo, String available, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=?1 and s.available=?2 and s.sDate > ?3 and s.status='A' order by s.sDate");
		query.setParameter(1, providerNo);
		query.setParameter(2, available);
		query.setParameter(3, sdate);
		
		@SuppressWarnings("unchecked")
        List<RSchedule> results = query.getResultList();
		return results;
	}
	
	public RSchedule search_rschedule_current1(String providerNo, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=?1 and s.sDate <= ?2 and s.status='A' order by s.sDate desc");
		query.setParameter(1, providerNo);
		query.setParameter(2, sdate);
		query.setMaxResults(1);
		
		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	
	public RSchedule search_rschedule_current2(String providerNo, Date sdate) {
		Query query = entityManager.createQuery("select s from RSchedule s where s.providerNo=?1 and s.sDate >= ?2 and s.status='A' order by s.sDate");
		query.setParameter(1, providerNo);
		query.setParameter(2, sdate);
		query.setMaxResults(1);
		
		RSchedule result = this.getSingleResultOrNull(query);
		return result;
	}
	


	public List<RSchedule> findByEdateAfter(String providerNo, Date timeAfter)
	{
		Query query = createQuery("s","s.providerNo = :providerNo AND s.eDate > :timeAfter AND s.status='A' ORDER BY s.sDate");
		query.setParameter("providerNo", providerNo);
		query.setParameter("timeAfter", timeAfter);

		return query.getResultList();
	}

	public List<RSchedule> findByProviderNoAndDates(String providerNo, Date apptDate) {
		Query query = createQuery("r", "r.providerNo = :providerNo AND r.sDate <= :apptDate AND r.eDate >= :apptDate");
		query.setParameter("providerNo", providerNo);
		query.setParameter("apptDate", apptDate);
		return query.getResultList();
    }

	public List<RSchedule> findByProviderNoAndStartEndDates(String providerNo, Date startDate, Date endDate) {
		Query query = createQuery("r", "r.providerNo = :providerNo AND r.sDate = :startDate AND (:endDate IS NULL OR r.eDate=:endDate)");
		query.setParameter("providerNo", providerNo);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		return query.getResultList();
	}
}
