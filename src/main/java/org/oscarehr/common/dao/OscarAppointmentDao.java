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

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.Map.Entry;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.AppointmentArchive;
import org.oscarehr.common.model.Facility;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.schedule.dto.AppointmentDetails;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class OscarAppointmentDao extends AbstractDao<Appointment> {

	public OscarAppointmentDao() {
		super(Appointment.class);
	}

	/**
	 * Check if the provided appointment conflicts with another appointment.  A conflict occurs if the provider has
	 * another active appointment which occurs at any point during the provided appointment's time.
	 *
	 * @param appointment An appointment to check
	 * @return true if a conflict is detected.
	 */
	public boolean checkForConflict(Appointment appointment) {
		String sb = "select a from Appointment a where a.appointmentDate = :appDate and " +
						"((a.startTime >= :startTime and a.startTime <= :endTime) or" +
						" (a.endTime >= :startTime and a.endTime <= :endTime) or " +
						" (a.startTime <= :startTime and a.endTime >= :endTime)) and" +
						" a.providerNo = :providerNo and a.status != 'N' and a.status != 'C'";

		Query query = entityManager.createQuery(sb);

		query.setParameter("appDate", appointment.getAppointmentDate());
		query.setParameter("startTime", appointment.getStartTime());
		query.setParameter("endTime", appointment.getEndTime());
		query.setParameter("providerNo", appointment.getProviderNo());

		List<Facility> results = query.getResultList();

		return !results.isEmpty();
	}
	
	public List<Appointment> getAppointmentHistory(Integer demographicNo, Integer offset, Integer limit) {
		String sql = "select a from Appointment a where a.demographicNo=?1 order by a.appointmentDate DESC, a.startTime DESC";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);
		query.setFirstResult(offset);
		query.setMaxResults(limit);
		
		List<Appointment> result = query.getResultList();
		
		return result;
	}
	
	public List<AppointmentArchive> getDeletedAppointmentHistory(Integer demographicNo, Integer offset, Integer limit) {
		
		List<Object> result = new ArrayList<Object>();
			
		String sql2 = "select a from AppointmentArchive a where a.demographicNo=?1 order by a.appointmentDate DESC, a.startTime DESC, id desc";
		Query query2 = entityManager.createQuery(sql2);
		query2.setParameter(1, demographicNo);
		query2.setFirstResult(offset);
		query2.setMaxResults(limit);
		
		List<AppointmentArchive> results = query2.getResultList();
		
		
		return results;
	}

	public List<Appointment> getAppointmentHistory(Integer demographicNo) {
		String sql = "select a from Appointment a where a.demographicNo=?1 order by a.appointmentDate DESC, a.startTime DESC";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public void archiveAppointment(int appointmentNo) {
		Appointment appointment = this.find(appointmentNo);
		if (appointment != null) {
			AppointmentArchive apptArchive = new AppointmentArchive();
			String[] ignores={"id"};
			BeanUtils.copyProperties(appointment, apptArchive, ignores);
			apptArchive.setAppointmentNo(appointment.getId());
			entityManager.persist(apptArchive);
		}
	}

	public List<Appointment> getAllByDemographicNo(Integer demographicNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = " + demographicNo + " ORDER BY a.id";
		Query query = entityManager.createQuery(sql);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}
	
	/**
	 * @return results ordered by lastUpdateDate
	 */
	public List<Appointment> findByUpdateDate(Date updatedAfterThisDateExclusive, int itemsToReturn) {
		String sqlCommand = "select x from "+modelClass.getSimpleName()+" x where x.updateDateTime>?1 order by x.updateDateTime";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, updatedAfterThisDateExclusive);
		setLimit(query, itemsToReturn);
		
		@SuppressWarnings("unchecked")
		List<Appointment> results = query.getResultList();
		return (results);
	}

	
	public List<Appointment> getAllByDemographicNoSince(Integer demographicNo,Date lastUpdateDate ) {
		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = " + demographicNo + " and a.updateDateTime > ?1 ORDER BY a.id";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, lastUpdateDate);

		List<Appointment> rs = query.getResultList();
		return rs;
	}
	
	public List<Integer> getAllDemographicNoSince(Date lastUpdateDate, List<Program> programs ) {
		StringBuilder sb = new StringBuilder();
    	int i=0;
    	for(Program p:programs) {
    		if(i++ > 0)
    			sb.append(",");
    		sb.append(p.getId());
    	}
		String sql = "select a.demographicNo SELECT a FROM Appointment a WHERE a.updateDateTime > ?1 and program_id in ("+sb.toString()+") ORDER BY a.id";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, 	lastUpdateDate);

		List<Integer> rs = query.getResultList();
		return rs;
	}


	public List<Appointment> findByDateRange(Date startTime, Date endTime) {
		String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=?1 and a.appointmentDate < ?2";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, startTime);
		query.setParameter(2, endTime);
		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> findByDateRangeAndProvider(Date startTime, Date endTime, String providerNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=?1 and a.appointmentDate < ?2 and providerNo = ?3";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, startTime);
		query.setParameter(2, endTime);
		query.setParameter(3, providerNo);


		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> getByProviderAndDay(Date date, String providerNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and a.status != 'N' and a.status != 'C' order by a.startTime";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> findByProviderAndDayandNotStatuses(String providerNo, Date date, String[] notThisStatus) {
		String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and a.status NOT IN ( ?3 )";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, Arrays.asList(notThisStatus));

		List<Appointment> results = query.getResultList();
		return results;
	}
	
	public List<Appointment> findByProviderAndDayandNotStatus(String providerNo, Date date, String notThisStatus) {
		String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and SUBSTR(a.status, 1, 1) != ?3 ORDER BY a.appointmentDate, a.startTime";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, notThisStatus);

		
		List<Appointment> results = query.getResultList();
		return results;
	}

	public List<Appointment> findByProviderDayAndStatus(String providerNo,Date date, String status) {
		String sql = "SELECT a FROM Appointment a WHERE a.providerNo=?1 and a.appointmentDate = ?2 and a.status=?3";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setParameter(2, date);
		query.setParameter(3, status);
		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> findByDayAndStatus(Date date, String status) {
		String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate = ?1 and a.status=?2";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, date);
		query.setParameter(2, status);
		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> find(Date date, String providerNo,Date startTime, Date endTime, String name,
			String notes, String reason, Date createDateTime, String creator, Integer demographicNo) {

		String sql = "SELECT a FROM Appointment a " +
				"WHERE a.appointmentDate = ?1 and a.providerNo=?2 and a.startTime=?3" +
				"and a.endTime=?4 and a.name=?5 and a.notes=?6 and a.reason=?7 and a.createDateTime=?8" +
				"and a.creator=?9 and a.demographicNo=?10";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, date);
		query.setParameter(2, providerNo);
		query.setParameter(3, startTime);
		query.setParameter(4, endTime);
		query.setParameter(5, name);
		query.setParameter(6, notes);
		query.setParameter(7, reason);
		query.setParameter(8, createDateTime);
		query.setParameter(9, creator);
		query.setParameter(10, demographicNo);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	/**
	 * @return return results ordered by appointmentDate, most recent first
	 */
	public List<Appointment> findByDemographicId(Integer demographicId, int startIndex, int itemsToReturn) {
		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ?1 ORDER BY a.appointmentDate desc";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicId);
		query.setFirstResult(startIndex);
		query.setMaxResults(itemsToReturn);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}

	public List<Appointment> findAll() {
		String sql = "SELECT a FROM Appointment a";
		Query query = entityManager.createQuery(sql);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}
	
	
    public List<Appointment> findNonCancelledFutureAppointments(Integer demographicId) {
		Query query = entityManager.createQuery("FROM Appointment appt WHERE appt.demographicNo = :demographicNo AND appt.status NOT LIKE '%C%' " +
				" AND appt.appointmentDate >= CURRENT_DATE ORDER BY appt.appointmentDate");
		query.setParameter("demographicNo", demographicId);
		return query.getResultList();
	}
	
	/**
	 * Finds appointment after current date and time for the specified demographic
	 * 
	 * @param demographicId
	 * 		Demographic to find appointment for
	 * @return
	 * 		Returns the next non-cancelled future appointment or null if there are no appointments
	 * 	scheduled
	 */
	public Appointment findNextAppointment(Integer demographicId) {
		Query query = entityManager.createQuery("FROM Appointment appt WHERE appt.demographicNo = :demographicNo AND appt.status NOT LIKE '%C%' " +
				"	AND (appt.appointmentDate > CURRENT_DATE OR (appt.appointmentDate = CURRENT_DATE AND appt.startTime >= CURRENT_TIME)) ORDER BY appt.appointmentDate");
		query.setParameter("demographicNo", demographicId);
		query.setMaxResults(1);
		return getSingleResultOrNull(query);
	}


	public Appointment findDemoAppointmentToday(Integer demographicNo) {
		Appointment appointment = null;

		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ?1 AND a.appointmentDate=DATE(NOW())";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);

		try {
			appointment = (Appointment) query.getSingleResult();
		} catch (Exception e) {
			MiscUtils.getLogger().info("Couldn't find appointment for demographic " + demographicNo + " today.");
		}

		return appointment;
	}
	

	public List<Appointment> findByEverything(Date appointmentDate, String providerNo, Date startTime, Date endTime, String name, String notes, String reason, Date createDateTime, String creator, int demographicNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate=?1 and a.providerNo=?2 and a.startTime=?3 and a.endTime=?4 and a.name=?5 and a.notes=?6 and a.reason=?7 and a.createDateTime like ?8 and a.creator = ?9 and a.demographicNo=?10";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, appointmentDate);
		query.setParameter(2, providerNo);
		query.setParameter(3, startTime);
		query.setParameter(4, endTime);
		query.setParameter(5, name);
		query.setParameter(6, notes);
		query.setParameter(7, reason);
		query.setParameter(8, createDateTime);
		query.setParameter(9, creator);
		query.setParameter(10, demographicNo);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}
	
	
    public List<Appointment> findByProviderAndDate(String providerNo, Date appointmentDate) {
		Query query = createQuery("a", "a.providerNo = :pNo and a.appointmentDate= :aDate");
		query.setParameter("pNo", providerNo);
		query.setParameter("aDate", appointmentDate);
	    return query.getResultList();
    }

	
	public List<Object[]> findAppointments(Date sDate, Date eDate) {
		String sql = "FROM Appointment a, Demographic d " +
				"WHERE a.demographicNo = d.DemographicNo " +
				"AND d.Hin <> '' " +
				"AND a.appointmentDate >= :sDate " +
				"AND a.appointmentDate <= :eDate " +
				"AND (" +
				"	UPPER(d.HcType) = 'ONTARIO' " +
				"	OR d.HcType='ON' " +
				") GROUP BY d.DemographicNo " +
				"ORDER BY d.LastName";
		Query query = entityManager.createQuery(sql);
		query.setParameter("sDate", sDate == null ? new Date(Long.MIN_VALUE) : sDate);
		query.setParameter("eDate", eDate == null ? new Date(Long.MAX_VALUE) : eDate);
		return query.getResultList();
	}
	
    public List<Object[]> findPatientAppointments(String providerNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Demographic d, Appointment a, Provider p " +
                "WHERE a.demographicNo = d.DemographicNo " +
                "AND a.providerNo = p.ProviderNo ");

        	Map<String, Object> params = new HashMap<String, Object>();
        	if(providerNo != null && !providerNo.trim().equals("")){
		       sql.append("and a.providerNo = :pNo ");
		       params.put("pNo", providerNo);
		   }
        	
		   if(from != null){
		       sql.append("AND a.appointmentDate >= :from ");
		       params.put("from", from);
		   }if(to != null){
		       sql.append("AND a.appointmentDate <= :to ");
		       params.put("to", to);
		   }
		   sql.append("ORDER BY a.appointmentDate");
		   
		   Query query = entityManager.createQuery(sql.toString());
		   for(Entry<String, Object> e : params.entrySet()) {
			   query.setParameter(e.getKey(), e.getValue());
		   }
		   return query.getResultList();
        }

	public List<Appointment> search_unbill_history_daterange(String providerNo, Date startDate, Date endDate) {
		String sql = "select a from Appointment a where a.providerNo=?1 and a.appointmentDate >=?2 and a.appointmentDate<=?3 and (a.status='P' or a.status='H' or a.status='PV' or a.status='PS') and a.demographicNo <> 0 order by a.appointmentDate desc, a.startTime desc";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		query.setParameter(2, startDate);
		query.setParameter(3, endDate);
		
		return query.getResultList();
	}
    
	public List<Appointment> findByDateAndProvider(Date date, String provider_no) {
		Query query = createQuery("a", "a.providerNo = :provider_no and a.appointmentDate = :date order by a.startTime asc");
		query.setParameter("provider_no", provider_no);
		query.setParameter("date", date);
		return query.getResultList();
    }
	
	public List<Appointment> search_appt(Date startTime, Date endTime, String providerNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.appointmentDate >=?1 and a.appointmentDate <= ?2 and a.providerNo = ?3 order by a.appointmentDate,a.startTime,a.endTime";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, startTime);
		query.setParameter(2, endTime);
		query.setParameter(3, providerNo);

		
		List<Appointment> rs = query.getResultList();

		return rs;
	}
	
	//search_appt_name
	public List<Appointment> search_appt(Date date, String providerNo, Date startTime1, Date startTime2, Date endTime1, Date endTime2, Date startTime3, Date endTime3, Integer programId) {
		String sql = "select a from Appointment a where a.appointmentDate = ?1 and a.providerNo = ?2 and a.status <>'C' and ((a.startTime >= ?3 and a.startTime<= ?4) or (a.endTime>= ?5 and a.endTime<= ?6) or (a.startTime <= ?7 and a.endTime>= ?8) ) and program_id=?9";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, date);
		query.setParameter(2, providerNo);
		query.setParameter(3, startTime1);
		query.setParameter(4, startTime2);
		query.setParameter(5, endTime1);
		query.setParameter(6, endTime2);
		query.setParameter(7, startTime3);
		query.setParameter(8, endTime3);
		query.setParameter(9, programId);
		
		List<Appointment> rs = query.getResultList();

		return rs;
	}
	
    public List<Object[]> search_appt_future(Integer demographicNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Appointment a, Provider p " +
                "WHERE a.providerNo = p.ProviderNo and " +
                "a.demographicNo = ?1 and " +
                "a.appointmentDate >= ?2 and " +
                "a.appointmentDate < ?3  " +
                "order by a.appointmentDate desc, a.startTime desc");
        
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter(1, demographicNo);
        query.setParameter(2, from);
        query.setParameter(3, to);
        

        return query.getResultList();
    }
    
    public List<Object[]> search_appt_past(Integer demographicNo, Date from, Date to) {
        StringBuilder sql = new StringBuilder("FROM Appointment a, Provider p " +
                "WHERE a.providerNo = p.ProviderNo and " +
                "a.demographicNo = ?1 and " +
                "a.appointmentDate < ?2 and " +
                "a.appointmentDate > ?3  " +
                "order by a.appointmentDate desc, a.startTime desc");
        
        Query query = entityManager.createQuery(sql.toString());
        query.setParameter(1, demographicNo);
        query.setParameter(2, from);
        query.setParameter(3, to);
        

        return query.getResultList();
    }
    
    public Appointment search_appt_no(String providerNo, Date appointmentDate, Date startTime, Date endTime, Date createDateTime, String creator, Integer demographicNo) {
    	String sql = "select a from Appointment a where a.providerNo=?1 and a.appointmentDate=?2 and a.startTime=?3 and "+
    				"a.endTime=?4 and a.createDateTime=?5 and a.creator=?6 and a.demographicNo=?7 order by a.id desc";
    	Query query = entityManager.createQuery(sql.toString());
        query.setParameter(1, providerNo);
        query.setParameter(2, appointmentDate);
        query.setParameter(3, startTime);
        query.setParameter(4, endTime);
        query.setParameter(5, createDateTime);
        query.setParameter(6, creator);
        query.setParameter(7, demographicNo);
        query.setMaxResults(1);
        
        return this.getSingleResultOrNull(query);
    }
    
    public List<Object[]> search_appt_data1(String providerNo, Date appointmentDate, Date startTime, Date endTime, Date createDateTime, String creator, Integer demographicNo) {
    	String sql = "from Provider prov, Appointment app " +
    			"where app.providerNo = prov.id and " +
    			"app.providerNo=?1 and " +
    			"app.appointmentDate=?2 and " +
    			"app.startTime=?3 and "  +
    			"app.endTime=?4 and " +
    			"app.createDateTime=?5 and " +
    			"app.creator=?6 and " +
    			"app.demographicNo=?7 " +
    			"order by app.id desc";
    	Query query = entityManager.createQuery(sql);
    	query.setMaxResults(1);
    	query.setParameter(1, providerNo);
    	 
         query.setParameter(2, appointmentDate);
         query.setParameter(3, startTime);
         query.setParameter(4, endTime);
         query.setParameter(5, createDateTime);
         query.setParameter(6, creator);
         query.setParameter(7, demographicNo);
         
         return query.getResultList();
    }
    
    public List<Object[]> export_appt(Integer demographicNo) {
    	String sql="SELECT app, prov " +
			    "FROM Appointment app, Provider prov " +
			    "WHERE app.providerNo = prov.ProviderNo " +
			    "AND app.demographicNo = :demographicNo";
    	Query query = entityManager.createQuery(sql);
    	query.setParameter("demographicNo", demographicNo);
         
        return query.getResultList();
    }
    
    public List<Appointment> search_otherappt(Date appointmentDate, Date startTime1, Date endTime1, Date startTime2, Date startTime3) {
    	String sql = "from Appointment a where a.appointmentDate=?1 and ((a.startTime <= ?2 and a.endTime >= ?3) or (a.startTime > ?4 and a.startTime < ?5) ) order by a.providerNo, a.startTime";
    	
    	
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1, appointmentDate);
         query.setParameter(2, startTime1);
         query.setParameter(3, endTime1);
         query.setParameter(4, startTime2);
         query.setParameter(5, startTime3);
          
         return query.getResultList();
    }
    
    public List<Appointment> search_group_day_appt(String myGroup, Integer demographicNo, Date appointmentDate) {
    	String sql = "select a  from Appointment a, MyGroup m " +
    			"where m.id.providerNo = a.providerNo " +
    			"and a.status <> 'C' " +
    			"and m.id.myGroupNo = ?1 " +
    			"and a.demographicNo = ?2 " +
    			"and a.appointmentDate = ?3";
    	
    	
    	Query query = entityManager.createQuery(sql);
    	query.setParameter(1, myGroup);
        query.setParameter(2, demographicNo);
        query.setParameter(3, appointmentDate);
        
        return query.getResultList();
    }


	public Appointment findByDate(Date appointmentDate) {
		Query query = createQuery("a", "a.appointmentDate < :appointmentDate ORDER BY a.appointmentDate DESC");
		query.setMaxResults(1);
		query.setParameter("appointmentDate", appointmentDate);
		return getSingleResultOrNull(query);
    }
	
	public List<Object[]> findAppointmentAndProviderByAppointmentNo(Integer apptNo) {
		String sql = "FROM Appointment a, Provider p WHERE a.providerNo = p.ProviderNo AND a.id = :apptNo";
		Query query = entityManager.createQuery(sql);
		query.setParameter("apptNo", apptNo);
		return query.getResultList();
	}
    
    public List<Appointment> searchappointmentday(String providerNo, Date appointmentDate, Integer programId) {
    	Query query = createQuery("appointment", "appointment.providerNo = :providerNo AND appointment.appointmentDate = :appointmentDate AND appointment.programId = :programId ORDER BY appointment.startTime, appointment.status DESC");
    	query.setParameter("providerNo", providerNo);
        query.setParameter("appointmentDate", appointmentDate);
        query.setParameter("programId", programId);
        return query.getResultList();
    }

	@NativeSql({"demographic", "appointment", "drugs", "provider"})
    public List<Object[]> findAppointmentsByDemographicIds(Set<String> demoIds, Date from, Date to) {   	
		String sql = "" +
				"select " +
				"a.appointment_date, " +
				"concat(pAppt.first_name, ' ', pAppt.last_name), " +
				"concat(pFam.first_name, ' ', pFam.last_name), " +
				"bi.service_code, " +
				"drugs.BN, " +
				"concat(pDrug.first_name,' ',pDrug.last_name), " +
				"a.demographic_no, " +
				"drugs.GN, " +
				"drugs.customName " +
				"from demographic d," +
				"appointment a left outer join drugs " +
				"on drugs.demographic_no = a.demographic_no and drugs.rx_date = a.appointment_date and a.appointment_date >= :from and a.appointment_date <= :to and a.demographic_no in (:demoIds) " +
				" left join provider pDrug on pDrug.provider_no = drugs.provider_no, billing_on_cheader1 bc, billing_on_item bi, provider pAppt, provider pFam where a.appointment_date >= :from and a.appointment_date <= :to and a.demographic_no = d.demographic_no" +
				" and a.provider_no = pAppt.provider_no and d.provider_no = pFam.provider_no and bc.appointment_no = a.appointment_no and bi.ch1_id = bc.id and a.demographic_no in (:demoIds) order by a.demographic_no, a.appointment_date";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("demoIds", demoIds);
		query.setParameter("from", from);
		query.setParameter("to", to);
		return query.getResultList();
    }

	/**
	 * Get billed appointment history. 
	 * Used if using the Clinicaid billing integration.
	 */
	public List<Appointment> findPatientBilledAppointmentsByProviderAndAppointmentDate(
			String providerNo, 
			Date startAppointmentDate, 
			Date endAppointmentDate ) 
	{
		String queryString = "FROM Appointment WHERE " +
			"providerNo = ?1 AND " +
			"appointmentDate >= ?2 AND " +
			"appointmentDate <= ?3 AND " +
			"status LIKE 'B%' AND " +
			"demographicNo <> 0 " + 
			"ORDER BY appointmentDate DESC, startTime DESC ";

		Query q = entityManager.createQuery(queryString);
		q.setParameter(1, providerNo);
		q.setParameter(2, startAppointmentDate);
		q.setParameter(3, endAppointmentDate);
		
		@SuppressWarnings("unchecked")
		List<Appointment> results = q.getResultList();
		
		return results;
	}
	
    
	/**
	 * Get unbilled appointment history. 
	 * Used if using the Clinicaid billing integration.
	 */
	public List<Appointment> findPatientUnbilledAppointmentsByProviderAndAppointmentDate(
			String providerNo, 
			Date startAppointmentDate, 
			Date endAppointmentDate ) 
	{

		String queryString = "FROM Appointment WHERE " +
			"providerNo = ?1 AND " +
			"appointmentDate >= ?2 AND " +
			"appointmentDate <= ?3 AND " +
			"status NOT LIKE 'B%' AND " + 
			"status NOT LIKE 'C%' AND " + 
			"status NOT LIKE 'N%' AND " + 
			"status NOT LIKE 'T%' AND " +
			"status NOT LIKE 't%' AND " + 
			"demographicNo != 0 " + 
			"ORDER BY appointmentDate DESC, startTime DESC";

		Query q = entityManager.createQuery(queryString);
		q.setParameter(1, providerNo);
		q.setParameter(2, startAppointmentDate);
		q.setParameter(3, endAppointmentDate);
		
		@SuppressWarnings("unchecked")
		List<Appointment> results = q.getResultList();
		
		return results;
	}
	
    public List<Appointment> findByProgramProviderDemographicDate(Integer programId, String providerNo, Integer demographicId, Date updatedAfterThisDateExclusive, int itemsToReturn) {
		Query query = entityManager.createQuery("select x from Appointment x where (x.programId=?1 or x.programId is null or x.programId=0) and x.demographicNo=?2 and x.providerNo=?3 and x.updateDateTime>?4 order by x.updateDateTime");
		query.setParameter(1, programId);
		query.setParameter(2, demographicId);
		query.setParameter(3, providerNo);
		query.setParameter(4, updatedAfterThisDateExclusive);

		setLimit(query, itemsToReturn);
		
		List<Appointment> results = query.getResultList();
		return results;
    }

    /**
     * @param programId can be null for all
     */
	public List<Integer> findAllDemographicIdByProgramProvider(Integer programId, String providerNo) {
		String sql = "select distinct(x.demographicNo)from Appointment x where x.providerNo=?1"+(programId==null?"":" and x.programId=?2");
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, providerNo);
		if (programId!=null) query.setParameter(2, programId);

		setDefaultLimit(query);
		
		List<Integer> rs = query.getResultList();
		return rs;
	}

	public List<Appointment> findDemoAppointmentsToday(Integer demographicNo) {
		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ?1 AND a.appointmentDate=DATE(NOW()) ORDER BY a.appointmentDate, a.startTime";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);

		@SuppressWarnings("unchecked")
		List<Appointment> results =  query.getResultList();

		return results;
	}
	
	public List<Appointment> findDemoAppointmentsOnDate(Integer demographicNo,Date date) {
		String sql = "SELECT a FROM Appointment a WHERE a.demographicNo = ?1 AND a.appointmentDate=?2 ORDER BY a.appointmentDate, a.startTime";
		Query query = entityManager.createQuery(sql);
		query.setParameter(1, demographicNo);
		query.setParameter(2, date);

		@SuppressWarnings("unchecked")
		List<Appointment> results =  query.getResultList();

		return results;
	}

	public List<Appointment> testMarker()
	{
		return testMarker("");
	}

	public List<Appointment> testMarker(String label) {
		String sql = "SELECT '" + label + "============================================================='";
		Query query = entityManager.createNativeQuery(sql);

		@SuppressWarnings("unchecked")
		List<Appointment> results =  query.getResultList();

		return results;
	}

	public SortedMap<LocalTime, List<AppointmentDetails>> findAppointmentDetailsByDateAndProvider(
		LocalDate date, Integer providerNo, String site)
	{
		return findAppointmentDetailsByDateAndProvider(date, date, providerNo, site);
	}

	public SortedMap<LocalTime, List<AppointmentDetails>> findAppointmentDetailsByDateAndProvider(
		LocalDate startDate, LocalDate endDate, Integer providerNo, String site)
	{
		String sql = "SELECT\n" +
				"  a.appointment_no,\n" +
				"  a.demographic_no,\n" +
				"  a.appointment_date,\n" +
				"  a.start_time,\n" +
				"  a.end_time,\n" +
				"  a.name,\n" +
				"  a.notes,\n" +
				"  a.reason,\n" +
				"  a.reasonCode,\n" +
				"  a.location,\n" +
				"  a.resources,\n" +
				"  a.type,\n" +
				"  a.style,\n" +
				"  a.bookingSource,\n" +
				"  a.creatorSecurityId, \n" +
				"  a.status,\n" +
				"  a.urgency,\n" +
				"  a.isVirtual,\n" +
				"  a.confirmed_at,\n" +
				"  aps.description,\n" +
				"  aps.color,\n" +
				"  aps.juno_color,\n" +
				"  aps.icon,\n" +
				"  aps.short_letter_colour,\n" +
				"  aps.short_letters,\n" +
				"  d.first_name,\n" +
				"  d.last_name,\n" +
				"  d.hc_renew_date,\n" +
				"  d.ver,\n" +
				"  d.roster_status,\n" +
				"  d.year_of_birth,\n" +
				"  d.month_of_birth,\n" +
				"  d.date_of_birth,\n" +
				"  d.hin,\n" +
				"  d.chart_no,\n" +
				"  d.family_doctor,\n" +
				"  dc.content AS cust_notes,\n" +
				"  dc.cust3 AS cust_alert,\n" +
				"  p.value AS color_property,\n" +
				"  MAX(t.tickler_no) AS max_tickler_no,\n" +
				"  GROUP_CONCAT(t.message SEPARATOR '\n') AS tickler_messages,\n" +
				"  a.virtual_type\n" +
				"FROM appointment a\n" +
				"LEFT JOIN appointment_status aps ON BINARY SUBSTRING(a.status, 1, 1) = aps.status\n" +
				"LEFT JOIN demographic d ON a.demographic_no = d.demographic_no\n" +
				"LEFT JOIN demographiccust dc ON a.demographic_no = dc.demographic_no\n" +
				"LEFT JOIN property p \n" +
				"  ON d.provider_no = p.provider_no AND p.name = :property_name\n" +
				"LEFT JOIN tickler t \n" +
				"  ON d.demographic_no = t.demographic_no \n" +
				"  AND DATE(t.service_date) <= a.appointment_date \n" +
				"  AND t.status = 'A'\n" +
				"WHERE a.appointment_date >= :startDate\n" +
				"AND a.appointment_date <= :endDate\n" +
				"AND a.provider_no = :providerNo\n";

				if(site != null)
				{
					sql += "AND a.location = :location\n";
				}

				sql += "GROUP BY   \n" +
				"  a.appointment_no,\n" +
				"  a.demographic_no,\n" +
				"  a.appointment_date,\n" +
				"  a.start_time,\n" +
				"  a.end_time,\n" +
				"  a.name,\n" +
				"  a.notes,\n" +
				"  a.reason,\n" +
				"  a.reasonCode,\n" +
				"  a.location,\n" +
				"  a.resources,\n" +
				"  a.type,\n" +
				"  a.style,\n" +
				"  a.bookingSource,\n" +
				"  a.creatorSecurityId, \n" +
				"  a.status,\n" +
				"  a.urgency,\n" +
				"  a.isVirtual,\n" +
				"  aps.description,\n" +
				"  aps.color,\n" +
				"  aps.juno_color,\n" +
				"  aps.icon,\n" +
				"  aps.short_letter_colour,\n" +
				"  aps.short_letters,\n" +
				"  d.first_name,\n" +
				"  d.last_name,\n" +
				"  d.hc_renew_date,\n" +
				"  d.ver,\n" +
				"  d.roster_status,\n" +
				"  d.year_of_birth,\n" +
				"  d.month_of_birth,\n" +
				"  d.date_of_birth,\n" +
				"  d.hin,\n" +
				"  d.chart_no,\n" +
				"  d.family_doctor,\n" +
				"  dc.content,\n" +
				"  dc.cust3,\n" +
				"  p.value\n" +
				"ORDER BY a.start_time, appointment_no\n";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("property_name", UserProperty.PROVIDER_COLOUR);
		query.setParameter("startDate", java.sql.Date.valueOf(startDate), TemporalType.DATE);
		query.setParameter("endDate", java.sql.Date.valueOf(endDate), TemporalType.DATE);
		query.setParameter("providerNo", providerNo);

		if(site != null)
		{
			query.setParameter("location", site);
		}

		List<Object[]> results = query.getResultList();

		SortedMap<LocalTime, List<AppointmentDetails>> appointmentDetails = new TreeMap<>();

		for(Object[] result: results)
		{
			int index = 0;
			Integer appointmentNo = (Integer) result[index++];
			Integer demographicNo = (Integer) result[index++];
			LocalDate appointmentDate = ((java.sql.Date) result[index++]).toLocalDate();
			LocalTime startTime = ((java.sql.Time) result[index++]).toLocalTime();
			LocalTime endTime = ((java.sql.Time) result[index++]).toLocalTime();
			String name = (String) result[index++];
			String notes = (String) result[index++];
			String reason = (String) result[index++];
			Integer reasonCode = (Integer) result[index++];
			String location = (String) result[index++];
			String resources = (String) result[index++];
			String type = (String) result[index++];
			String style = (String) result[index++];
			String bookingSource = (String) result[index++];
			Integer creatorSecurityId = (Integer) result[index++];
			String status = (String) result[index++];
			String urgency = (String) result[index++];
			Byte isVirtualResult = (Byte) result[index++];
			boolean isVirtual = (Byte.toUnsignedInt(isVirtualResult) == 1);
			java.sql.Timestamp confirmedAt = (java.sql.Timestamp) result[index++];
			boolean isConfirmed = confirmedAt != null;
			String statusTitle = (String) result[index++];
			String color = (String) result[index++];
			String junoColor = (String) result[index++];
			String iconImage = (String) result[index++];
			Integer shortLetterColour = (Integer) result[index++];
			String shortLetters = (String) result[index++];
			String firstName = (String) result[index++];
			String lastName = (String) result[index++];
			java.sql.Date hcRenewDateRaw = (java.sql.Date) result[index++];
			String ver = (String) result[index++];
			String rosterStatus = (String) result[index++];
			String yearOfBirth = (String) result[index++];
			String monthOfBirth = (String) result[index++];
			String dayOfBirth = (String) result[index++];
			String hin = (String) result[index++];
			String chartNo = (String) result[index++];
			String familyDoctor = (String) result[index++];
			String custNotes = (String) result[index++];
			String custAlert = (String) result[index++];
			String colorProperty = (String) result[index++];
			Integer maxTicklerNo = (Integer) result[index++];
			String ticklerMessages = (String) result[index++];
			Appointment.VirtualAppointmentType virtualAppointmentType = Optional.ofNullable((String) result[index++]).map(Appointment.VirtualAppointmentType::valueOf).orElse(Appointment.VirtualAppointmentType.NONE);

			if(status != null)
			{
				status = status.trim();
			}

			if(bookingSource != null)
			{
				bookingSource = bookingSource.trim();
			}

			List<AppointmentDetails> currentValue = appointmentDetails.get(startTime);

			if(currentValue == null)
			{
				appointmentDetails.put(startTime, new ArrayList<>());
			}

			LocalDate hcRenewDate = null;
			if(hcRenewDateRaw != null)
			{
				hcRenewDate = hcRenewDateRaw.toLocalDate();
			}

			LocalDate birthday = null;
			if(yearOfBirth != null && monthOfBirth != null && dayOfBirth != null)
			{
				int year = Integer.parseInt(yearOfBirth);
				int month = Integer.parseInt(monthOfBirth);
				int day = Integer.parseInt(dayOfBirth);

				try
				{
					birthday = LocalDate.of(year, month, day);
				}
				catch (DateTimeException ex)
				{
					MiscUtils.getLogger().error("Demographic [" + demographicNo + "] has invalid dob with error: " + ex.getMessage());
				}

			}
			boolean hasTicklers = (maxTicklerNo != null);

			appointmentDetails.get(startTime).add(new AppointmentDetails(
					appointmentNo,
					demographicNo,
					appointmentDate,
					startTime,
					endTime,
					name,
					notes,
					reason,
					reasonCode,
					location,
					resources,
					type,
					style,
					bookingSource,
					status,
					urgency,
					statusTitle,
					color,
					junoColor,
					iconImage,
					shortLetterColour,
					shortLetters,
					firstName,
					lastName,
					ver,
					hin,
					chartNo,
					familyDoctor,
					rosterStatus,
					hcRenewDate,
					custNotes,
					custAlert,
					colorProperty,
					birthday,
					hasTicklers,
					ticklerMessages,
					isVirtual,
					isConfirmed,
					creatorSecurityId,
					virtualAppointmentType
			));

		}

		return appointmentDetails;
	}

	public List<Appointment> findPatientAppointmentsWithProvider(String demographicNo, String providerNo, LocalDate minDate, LocalDate maxDate)
	{
		String sql = "SELECT a FROM Appointment a\n" +
					 "WHERE a.demographicNo = :demographicNo\n" +
					 "AND a.providerNo = :providerNo\n" +
	 				 "AND a.status != :cancelledStatus\n" +
					 "AND a.appointmentDate BETWEEN :minDate AND :maxDate\n" +
					 "ORDER BY a.appointmentDate, a.startTime";

		Query query = entityManager.createQuery(sql);
		query.setParameter("demographicNo", Integer.parseInt(demographicNo));
		query.setParameter("providerNo", providerNo);
		query.setParameter("minDate", java.sql.Date.valueOf(minDate));
		query.setParameter("maxDate", java.sql.Date.valueOf(maxDate));
		query.setParameter("cancelledStatus", Appointment.CANCELLED);

		@SuppressWarnings("unchecked")
		List<Appointment> results =  query.getResultList();

		return results;
	}

	public Map<LocalDate, List<Appointment>> findProviderAppointmentsForMonth(String providerNo, LocalDate minDate, LocalDate maxDate)
	{
		Map<LocalDate, List<Appointment>> monthlyAppointments = new HashMap<>();

		String sql = "SELECT a FROM Appointment a\n" +
				"WHERE a.providerNo = :providerNo\n" +
				"AND a.status != :cancelledStatus\n" +
				"AND a.appointmentDate BETWEEN :minDate AND :maxDate\n" +
				"ORDER BY a.appointmentDate, a.startTime";

		Query query = entityManager.createQuery(sql);
		query.setParameter("providerNo", providerNo);
		query.setParameter("minDate", java.sql.Date.valueOf(minDate));
		query.setParameter("maxDate", java.sql.Date.valueOf(maxDate));
		query.setParameter("cancelledStatus", Appointment.CANCELLED);

		@SuppressWarnings("unchecked")
		List<Appointment> results = query.getResultList();

		for (Appointment appointment : results)
		{
			LocalDate appointmentDate = LocalDate.parse(appointment.getAppointmentDate().toString());

			List<Appointment> dayAppointments = monthlyAppointments.get(appointmentDate);

			if (dayAppointments == null)
			{
				dayAppointments = new ArrayList<>();
			}

			dayAppointments.add(appointment);

			monthlyAppointments.put(appointmentDate, dayAppointments);
		}

		return monthlyAppointments;
	}

    public List<Appointment> findByDateRangeAndDemographic(LocalDate startDate, LocalDate endDate, Integer demographicNo)
    {
    	String sql = "SELECT a from Appointment a " +
					 "WHERE a.demographicNo = :demographicNo " +
					 "AND a.appointmentDate BETWEEN :startDate AND :endDate " +
				     "AND a.status != :cancelledStatus";

    	Query query = entityManager.createQuery(sql);
    	query.setParameter("demographicNo", demographicNo);
    	query.setParameter("startDate", java.sql.Date.valueOf(startDate));
    	query.setParameter("endDate", java.sql.Date.valueOf(endDate));
		query.setParameter("cancelledStatus", Appointment.CANCELLED);

    	return query.getResultList();
    }

	/**
	 * Given a demographic, find their most recent appointment before time of query.
	 * @param demographicNo the demographic to get an appointment for
	 * @return the most recent appointment, or null if they've never had an appointment
	 */
	public Appointment findLastAppointment(int demographicNo)
	{
		String sql = "SELECT a FROM Appointment a " +
				"WHERE ADDTIME(a.appointmentDate, a.startTime) < NOW() " +
				"AND a.demographicNo=:demographicNo " +
				"ORDER BY a.appointmentDate DESC";
		Query query = entityManager.createQuery(sql);
		query.setParameter("demographicNo", demographicNo);

		return getSingleResultOrNull(query);

	}

	/**
	 * Given a demographic, find the number of appointments that occurred within past year.
	 * @param demographicNo demographic to find appointment count for
	 * @return number of appointments that have occurred within last year
	 */
	public Integer findAppointmentsWithinLastYear(int demographicNo)
	{
		String sql = "SELECT a FROM Appointment a " +
				"WHERE a.demographicNo=:demographicNo " +
				"AND a.appointmentDate BETWEEN :startDate AND CURDATE()";
		Query query = entityManager.createQuery(sql);
		query.setParameter("demographicNo", demographicNo);
		// JPA itself doesn't allow for DATE_SUB, otherwise we could do DATE_SUB(CURDATE(), INTERVAL 1 YEAR)
		Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, -1);
		Date previousYear = lastYear.getTime();
		query.setParameter("startDate", previousYear);

		List<Appointment> appointments = query.getResultList();
		return appointments.size();
	}
}
