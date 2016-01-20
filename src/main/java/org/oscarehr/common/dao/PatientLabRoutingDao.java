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

import org.oscarehr.common.model.PatientLabRouting;
import org.springframework.stereotype.Repository;

@Repository
public class PatientLabRoutingDao extends AbstractDao<PatientLabRouting> {

	public static final Integer UNMATCHED = 0;
	public static final String HL7 = "HL7";
	public static final String DOC = "DOC";

	public PatientLabRoutingDao() {
		super(PatientLabRouting.class);
	}

	/**
	 * Finds routing record containing reference to the demographic record with the 
	 * specified lab results reference number of {@link #HL7} lab type. 
	 * 
	 * LabId is also referred to as Lab_no, and segmentId.
	 */
	public PatientLabRouting findDemographicByLabId(Integer labId) {
		return findDemographics(HL7, labId);
	}

	/**
	 * Finds routing record containing reference to the demographic record with the 
	 * specified lab type and lab results reference number. 
	 * 
	 * @param labType
	 * 		Type of the lab record to look up
	 * @param labNo
	 * 		Number of the lab record to look up
	 * @return
	 * 		Returns the container pointing to the demographics or null of no matching container is found.
	 */
	public PatientLabRouting findDemographics(String labType, Integer labNo) {
    		String sqlCommand="select x from "+ this.modelClass.getName() +" x where x.labType=?1 and x.labNo=?2";
    		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, labType);
		query.setParameter(2, labNo);
		return(getSingleResultOrNull(query));
	}

    public List<PatientLabRouting> findDocByDemographic(String docNum) {
    	return findRouteList(Integer.parseInt(docNum), DOC);
    }
    public List<PatientLabRouting> findByLabNoAndLabType(int labNo, String labType) {
    	return findRouteList(labNo, labType);
    }
    @SuppressWarnings("unchecked")
    public List<PatientLabRouting> findRouteList(Integer labNo, String labType) {
    	String query = "select x from " + modelClass.getName() + " x where x.labNo=? and x.labType=?";
    	Query q = entityManager.createQuery(query);

    	q.setParameter(1, labNo);
    	q.setParameter(2, labType);
    	
    	return q.getResultList();
    }
    
    /**
     * Retrieves a single PatientLabRoute from the database of type DOC, with the given lab id and demographic number
     * @param labNo
     * @param demographicNo
     * @return PatientLabRoute or null
     */
	public PatientLabRouting findSingleDocRoute(Integer labNo, Integer demographicNo) {
		return findSingleRoute(labNo, DOC, demographicNo);
	}
    /**
     * Retrieves a single PatientLabRoute from the database of type HL7, with the given lab id and demographic number
     * @param labNo
     * @param demographicNo
     * @return PatientLabRoute or null
     */
    public PatientLabRouting findSingleLabRoute(Integer labNo, Integer demographicNo) {
    	return findSingleRoute(labNo, HL7, demographicNo);
    }
    /**
     * Retrieves a single PatientLabRoute from the database with the given lab id, type, and demographic number
     * @param labNo
     * @param labType
     * @param demographicNo
     * @return PatientLabRoute or null
     */
    public PatientLabRouting findSingleRoute(Integer labNo, String labType, Integer demographicNo) {
		String sqlCommand="SELECT x FROM "+ this.modelClass.getName() +" x WHERE x.labType=?1 AND x.labNo=?2 AND x.demographicNo=?3";
		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, labType);
		query.setParameter(2, labNo);
		query.setParameter(3, demographicNo);
		return(getSingleResultOrNull(query));
    }

}
