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

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.OscarCommLocations;
import org.springframework.stereotype.Repository;

@Repository
public class OscarCommLocationsDao extends AbstractDao<OscarCommLocations>{

	public OscarCommLocationsDao() {
		super(OscarCommLocations.class);
	}
	
	public List<OscarCommLocations> findByCurrent1(int current1) {
		Query q = entityManager.createQuery("SELECT x FROM OscarCommLocations x WHERE x.current1=?1");
		q.setParameter(1, current1);
		
		@SuppressWarnings("unchecked")
		List<OscarCommLocations> results = q.getResultList();
		
		return results;
		
	}

	/**
	 * Gets a list of oscarCommLocations entries with the provided locationDesc
	 * @param description Description to find
	 * @return List of OscarCommLocations
	 */
	public List<OscarCommLocations> findByLocationDesc(String description) {
		Query query = createQuery("ocl", "ocl.locationDesc = :locationDesc");
		query.setParameter("locationDesc", description);
		return query.getResultList();
	}

	@NativeSql({"messagetbl", "oscarcommlocations"})
	public List<Object[]> findFormLocationByMesssageId(String messId) {
		String sql = "select ocl.locationDesc, mess.thesubject from messagetbl mess, oscarcommlocations ocl where mess.sentByLocation = ocl.locationId and mess.messageid = '" + messId + "' ";
		Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
    }
	
	@NativeSql({"messagetbl", "oscarcommlocations"})
	public List<Object[]> findAttachmentsByMessageId(String messageId) {
		String sql = "SELECT m.thesubject, m.theime, m.thedate, m.attachment, m.themessage, m.sentBy, ocl.locationDesc  "
		        +"FROM messagetbl m, oscarcommlocations ocl where m.sentByLocation = ocl.locationId and "
		        +" messageid = '"+messageId+"'";
		Query query = entityManager.createNativeQuery(sql);
		return query.getResultList();
	}
}
