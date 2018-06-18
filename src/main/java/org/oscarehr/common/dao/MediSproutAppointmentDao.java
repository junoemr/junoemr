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

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.MediSproutAppointment;
import org.springframework.stereotype.Repository;

@Repository
public class MediSproutAppointmentDao extends AbstractDao<MediSproutAppointment> {
	
	public MediSproutAppointmentDao() {
		super(MediSproutAppointment.class);
	}
	
	public MediSproutAppointment getAppointment(int appointmentNo) {
		
		// return the most recent data for this demographic 
		String sqlCommand = "select x from MediSproutAppointment x where x.appointment_no=?1";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, appointmentNo);
		
		return getSingleResultOrNull(query);
	}
	
	public List<MediSproutAppointment> getAppointmentsToDownloadDocs() {
		// return the most recent data for this demographic 
		String sqlCommand = "select x from MediSproutAppointment x where dowloadeddocs = 0";

		Query query = entityManager.createQuery(sqlCommand);
		
		@SuppressWarnings("unchecked")
        List<MediSproutAppointment> apptList = query.getResultList();

        if (apptList != null) {
            return apptList;
        } else {
	    return null;
	}
	}
	

}
