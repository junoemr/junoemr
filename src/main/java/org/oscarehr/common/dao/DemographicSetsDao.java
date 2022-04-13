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

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.DemographicSets;
import org.springframework.stereotype.Repository;

@Repository
public class DemographicSetsDao extends AbstractDao<DemographicSets>{

	public DemographicSetsDao() {
		super(DemographicSets.class);
	}

	public List<DemographicSets> findBySetName(String setName) {
		String sql = "SELECT x FROM DemographicSets x WHERE x.archive != :archive AND x.name = :setName ORDER BY x.demographic_no";
		Query query = entityManager.createQuery(sql);
		query.setParameter("archive", "1");
		query.setParameter("setName", setName);
		@SuppressWarnings("unchecked")
		List<DemographicSets> results = query.getResultList();
		return results;
	}

	public List<DemographicSets> findBySetNames(Collection<String> setNameList) {
		String sql = "SELECT x FROM DemographicSets x WHERE x.archive != :archive AND x.name IN (:nameList)";
		Query query = entityManager.createQuery(sql);
		query.setParameter("archive", "1");
		query.setParameter("nameList", setNameList);
		@SuppressWarnings("unchecked")
		List<DemographicSets> results = query.getResultList();
		return results;
	}

	public List<DemographicSets> findBySetNameAndEligibility(String setName, String eligibility) {
		String sql = "SELECT x FROM DemographicSets x WHERE x.archive != :archive AND x.name = :setName AND x.eligibility = :eligiblity";
		Query query = entityManager.createQuery(sql);
		query.setParameter("archive", "1");
		query.setParameter("setName", setName);
		query.setParameter("eligiblity", eligibility);
		@SuppressWarnings("unchecked")
		List<DemographicSets> results = query.getResultList();
		return results;
	}

	public List<String> findSetNamesByDemographicNo(Integer demographicNo) {
		String sql = "SELECT distinct(x.name) FROM DemographicSets x JOIN x.demographic d WHERE x.archive != :archive AND d.DemographicNo = :demoNo";
		Query query = entityManager.createQuery(sql);
		query.setParameter("archive", "1");
		query.setParameter("demoNo", demographicNo);
		@SuppressWarnings("unchecked")
		List<String> results = query.getResultList();
		return results;
	}

	public List<String> findSetNames() {
		String sql = "SELECT distinct(x.name) FROM DemographicSets x";
		Query query = entityManager.createQuery(sql);
		@SuppressWarnings("unchecked")
		List<String> results = query.getResultList();
		return results;
	}

	public DemographicSets findBySetNameAndDemographicNo(String setName, int demographicNo)
	{
		String sql = "SELECT x FROM DemographicSets x WHERE x.name = :setName AND x.demographic.DemographicNo = :demoNo";
		Query query = entityManager.createQuery(sql);
		query.setParameter("setName", setName);
		query.setParameter("demoNo", demographicNo);
		return getSingleResultOrNull(query);
	}
	public void deletePatientSet(String setName) {
		String sql = "DELETE FROM DemographicSets x WHERE x.name = :setName";
		Query query = entityManager.createQuery(sql);
		query.setParameter("setName", setName);
		query.executeUpdate();
	}
}
