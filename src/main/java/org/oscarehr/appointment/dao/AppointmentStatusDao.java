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
package org.oscarehr.appointment.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.model.AppointmentStatus;
import org.springframework.stereotype.Repository;

@Repository
public class AppointmentStatusDao extends AbstractDao<AppointmentStatus>
{

	public AppointmentStatusDao() {
		super(AppointmentStatus.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<AppointmentStatus> findAll() {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " x ORDER BY x.id");
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
    public List<AppointmentStatus> findByActive(boolean isActive)
    {
    	int active = isActive? 1 : 0;
	    Query query = entityManager.createQuery("SELECT a FROM AppointmentStatus a WHERE a.active = :active");
	    query.setParameter("active", active);

	    return query.getResultList();
    }
    
    public AppointmentStatus findByStatus(String status) {
    	if(status == null || status.length() == 0){
    		return null;
    	}
    	
    	Query q = entityManager.createQuery("select a from AppointmentStatus a where a.status like ?1");
    	q.setParameter(1, status.substring(0, 1) + "%");
    	
    	@SuppressWarnings("unchecked")
    	List<AppointmentStatus> results = q.getResultList();
    	
		
    	for(AppointmentStatus r:results) {
    		if(r.getStatus() != null && r.getStatus().length()>0 && r.getStatus().charAt(0) == status.charAt(0)) {
    			return r;
    		}
    	}
    	
    	return null;
    }

    public void modifyStatus(int ID, String strDesc, String strColor, String strJunoColor) {
    	AppointmentStatus appts = find(ID);
    	if(appts != null) {
    		appts.setDescription(strDesc);
            appts.setColor(strColor);
            appts.setJunoColor(strJunoColor);
    	}
    }

    public void changeStatus(int ID, int iActive) {
    	AppointmentStatus appts = find(ID);
    	if(appts != null) {
    		appts.setActive(iActive);
    	}
    }

	/**
	 * Get a list of all status codes which are currently in use.  Statuses which have been modified
	 * (ie: Verified V, Signed S) are considered separate from their unmodified version
	 *
	 * eg for a given status x: x, xS, xV, xVS, xSV are all considered distinct statuses.
	 *
	 * @return list of appointment statuses in use
	 */
	public List<String> getStatusesInUse()
	{
		String sql = "SELECT DISTINCT status FROM appointment ORDER BY status";
		Query query = entityManager.createNativeQuery(sql);

		return query.getResultList();
	}

	/**
	 * Find all inactive statuses that are currently used in any appointment. Return a list of these statuses.
	 *
	 * @param allStatus
	 * @return int
	 */
	public List<String> checkStatusUsuage(List<AppointmentStatus> allStatus)
	{
		int inactiveUseCount = 0;
		List<String> inactiveUsedStatuses = new ArrayList<String>();
		AppointmentStatus apptStatus = null;
		String sql = null;
		for (int i = 0; i < allStatus.size(); i++)
		{
			apptStatus = allStatus.get(i);
			if (apptStatus.getActive() == 1)
			{
				continue;
			}
			sql = "select count(*) as total from appointment a where a.status like ?1 ";
			// sql = sql + "collate latin1_general_cs";

			Query q = entityManager.createNativeQuery(sql);
			q.setParameter(1, apptStatus.getStatus() + "%");
			Object result = q.getSingleResult();

			inactiveUseCount = ((BigInteger) result).intValue();
			if (inactiveUseCount > 0)
			{
				inactiveUsedStatuses.add(apptStatus.getStatus());
			}
		}
		return inactiveUsedStatuses;
	}
}
