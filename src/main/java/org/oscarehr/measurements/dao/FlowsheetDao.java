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


package org.oscarehr.measurements.dao;

import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.measurements.model.Flowsheet;
import org.springframework.stereotype.Repository;

@Repository
public class FlowsheetDao extends AbstractDao<Flowsheet>
{

	public FlowsheetDao() {
		super(Flowsheet.class);
	}
	
	public List<Flowsheet> findAll() {
		Query query = entityManager.createQuery("select f from Flowsheet f");
		
		@SuppressWarnings("unchecked")
		List<Flowsheet> results = query.getResultList();
		
		return results;
	}
	
	public Flowsheet findByName(String name) {
		Query query = entityManager.createQuery("select f from Flowsheet f where f.name=?1");
		query.setParameter(1, name);
		
		return getSingleResultOrNull(query);
	}

	/**
	 * Method to specifically pull flowsheet entries from the DB that are system-level ones.
	 * These entries get inserted whenever someone hits enable or disable flowsheet.
	 */
	public Flowsheet findExternalByName(String name)
	{
		Query query = entityManager.createQuery("SELECT f " +
				"FROM Flowsheet f " +
				"WHERE f.name=:name " +
				"AND f.external=true");
		query.setParameter("name", name);

		return getSingleResultOrNull(query);
	}

	/**
	 * Given the name of a flowsheet, find the corresponding DB entry and set it as disabled.
	 * @param name name of the flowsheet to disable
	 */
	public void disableFlowsheet(String name)
	{
		Flowsheet flowsheet = findByName(name);
		if (flowsheet != null)
		{
			flowsheet.setEnabled(false);
			merge(flowsheet);
		}
	}

	/**
	 * Given the name of a flowsheet, find the corresponding DB entry and enable it.
	 * @param name name of the flowsheet to disable
	 */
	public void enableFlowsheet(String name)
	{
		Flowsheet flowsheet = findByName(name);
		if (flowsheet != null)
		{
			flowsheet.setEnabled(true);
			merge(flowsheet);
		}
	}
}
