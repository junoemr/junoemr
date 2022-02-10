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

import org.oscarehr.common.model.AbstractModel;
import org.oscarehr.common.model.OscarLog;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
    
    
    public boolean hasRead(String providerNo, String content, String contentId)
	{
    	String sqlCommand="SELECT COUNT(log) " +
			"FROM " + modelClass.getSimpleName() + " AS log " +
			"WHERE log.action = 'read' AND log.providerNo = :providerNo AND log.content = :content and log.contentId = :contentId";

    	Query query = entityManager.createQuery(sqlCommand);
		query.setParameter("providerNo", providerNo);
		query.setParameter("content", content);
		query.setParameter("contentId", contentId);
		
	    @SuppressWarnings("unchecked")
		List<Long> results = query.getResultList();

		return (results.get(0) != 0);
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

	@Override
    public void remove(AbstractModel<?> o) {
	    throw new SecurityException("Cannot remove audit log entries!");
    }

	@Override
    public boolean remove(Object id) {
		 throw new SecurityException("Cannot remove audit log entries!");
    }
	
	
}
