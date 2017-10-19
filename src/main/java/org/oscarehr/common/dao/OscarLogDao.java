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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import oscar.log.LogConst;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.OscarLog;
import org.springframework.stereotype.Repository;

@Repository
public class OscarLogDao extends AbstractDao<OscarLog> {

	public OscarLogDao() {
		super(OscarLog.class);
	}

    public List<OscarLog> findByDemographicId(Integer demographicId) {

    	String sqlCommand="select x from "+modelClass.getSimpleName()+" x where x.demographicId=?1";
    	
    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, demographicId);

	    @SuppressWarnings("unchecked")
		List<OscarLog> results=query.getResultList();
		
		return(results);
    }
    
    
    public boolean hasRead(String providerNo, String content, String contentId){
    	String sqlCommand="select x from "+modelClass.getSimpleName()+" x where x.action = 'read' and  x.providerNo=?1 and x.content = ?2 and x.contentId = ?3";
    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, providerNo);
		query.setParameter(2, content);
		query.setParameter(3, contentId);
		
	    @SuppressWarnings("unchecked")
		List<OscarLog> results=query.getResultList();
	    if(results.size() == 0){
	    	return false;
	    }
		
		return true;
    }
    
    public List<OscarLog> findByActionAndData(String action, String data) {
    	String sqlCommand="select x from "+modelClass.getSimpleName()+" x where x.action = ?1 and x.data = ?2 order by x.created DESC";
    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, action);
		query.setParameter(2, data);
		
	    @SuppressWarnings("unchecked")
		List<OscarLog> results=query.getResultList();
	    
	    return results;
    }
    
    
    public List<OscarLog> findByActionContentAndDemographicId(String action, String content, Integer demographicId) {

    	String sqlCommand="select x from "+modelClass.getSimpleName()+" x where x.action=?1 and x.content = ?2 and x.demographicId=?3 order by x.created desc";
    	
    	Query query = entityManager.createQuery(sqlCommand);
    	query.setParameter(1, action);
    	query.setParameter(2, content);
		query.setParameter(3, demographicId);

	    @SuppressWarnings("unchecked")
		List<OscarLog> results=query.getResultList();
		
		return(results);
    }

	public List<Integer> getDemographicIdsOpenedSinceTime(Date value) {
		String sqlCommand="select distinct demographicId from "+modelClass.getSimpleName()+" where dateTime >= ?1";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, value);

		@SuppressWarnings("unchecked")
		List<Integer> results=query.getResultList();
		results.removeAll(Collections.singleton(null));

		return(results);
	}

	/**
	 * 
	 * @param providerNo
	 * @param startPosition
	 * @param itemsToReturn
	 * @return List of Object array [demographicId (Integer), lastDateViewed Date]
	 */
	public List<OscarLog> getRecentDemographicsViewedByProvider(String providerNo, int startPosition, int itemsToReturn) {

		String sqlCommand =
				"SELECT MAX(dateTime) created, demographic_no FROM log " +
						"WHERE dateTime > :created_at_filter " +
						"AND content = :content AND provider_no = :providerNo " +
						"GROUP BY demographic_no " +
						"ORDER BY dateTime DESC ";

		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -1);
		Date oneMonthAgo = cal.getTime();

		Query query = entityManager.createNativeQuery(sqlCommand);
		query.setFirstResult(startPosition);
		query.setParameter("providerNo", providerNo);
		query.setParameter("content", LogConst.CON_DEMOGRAPHIC);
		query.setParameter("created_at_filter", oneMonthAgo, TemporalType.TIMESTAMP);
		setLimit(query, itemsToReturn);

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<OscarLog> logEntries = new ArrayList<OscarLog>();
		for(Object[] result: results)
		{
			OscarLog logEntry = new OscarLog();
			logEntry.setDemographicId((int)result[1]);
			logEntry.setCreated((Date)result[0]);
			logEntries.add(logEntry);
		};
		
		return logEntries;
	}

	@Override
    public void remove(AbstractModel<?> o) {
	    throw new SecurityException("Cannot remove audit log entries!");
    }

	@Override
    public boolean remove(Object id) {
		 throw new SecurityException("Cannot remove audit log entries!");
    }
	
	
}
