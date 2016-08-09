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

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.MeasurementType;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class MeasurementTypeDao extends AbstractDao<MeasurementType> {

	static org.apache.log4j.Logger log = MiscUtils.getLogger();
	
	public MeasurementTypeDao() {
		super(MeasurementType.class);
	}

	public List<MeasurementType> findAll() {
		String sqlCommand = "select x from " + modelClass.getSimpleName();

		Query query = entityManager.createQuery(sqlCommand);

		@SuppressWarnings("unchecked")
		List<MeasurementType> results = query.getResultList();

		return (results);
	}
	
	public List<MeasurementType> findByType(String type) {
		String sqlCommand = "select x from " + modelClass.getSimpleName()+" x where x.type=?1";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, type);

		@SuppressWarnings("unchecked")
		List<MeasurementType> results = query.getResultList();

		return (results);
	}
	
	
	/**
	 * check the database for duplicates matching Instruction and Name strings.
	 * @param measuringInstrc
	 * @param typeDisplayName
	 * @return true if at least one entry exists matching both parameters, false otherwise
	 */
	public boolean isDuplicate(String typeDisplayName, String measuringInstrc) {
		
        String sql = "SELECT count(x) FROM "+modelClass.getSimpleName()+" x WHERE x.measuringInstruction = :instruct AND x.typeDisplayName = :name ";
        
		Query query = entityManager.createQuery(sql);
		query.setParameter("instruct", measuringInstrc);
		query.setParameter("name", typeDisplayName);
		
		Long count =  (Long) query.getSingleResult();

		return (count > 0);
	}
	/**
	 * check the database for duplicates matching type.
	 * @param type
	 * @return true if at least one entry exists matching the type parameter, false otherwise
	 */
	public boolean isDuplicate(String type) {
		
        String sql = "SELECT count(x) FROM "+modelClass.getSimpleName()+" x WHERE x.type = :type ";
        
		Query query = entityManager.createQuery(sql);
		query.setParameter("type", type);
		
		Long count =  (Long) query.getSingleResult();

		return (count > 0);
	}
	
	/**
	 * find a list of measurement types matching the given display name
	 * @param displayName
	 * @return list of results
	 */
	public List<MeasurementType> findByDisplayName(String displayName) {
		
		String sqlCommand = "SELECT x FROM " + modelClass.getSimpleName() + " x WHERE x.typeDisplayName = :name ";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter("name", displayName);

		@SuppressWarnings("unchecked")
		List<MeasurementType> results = query.getResultList();

		return (results);
	}
	
	/**
	 * Saves a new measurementType to the database with the provided values
	 * @param type
	 * @param typeDisplayName
	 * @param measureingInstruction
	 * @param creationDate
	 * @param validation
	 * @return the id of the measurement created
	 */
	public MeasurementType saveNewMeasurementType(String type, String typeDesc, String typeDisplayName, String measuringInstruction, String validation ) {
		MeasurementType mt = new MeasurementType();
		mt.setType(type);
		mt.setTypeDescription(typeDesc);
		mt.setTypeDisplayName(typeDisplayName);
		mt.setMeasuringInstruction(measuringInstruction);
		mt.setValidation(validation);
		
		this.persist(mt);
		
		return mt;
	}
	
}
