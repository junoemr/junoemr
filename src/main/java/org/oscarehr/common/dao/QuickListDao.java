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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.QuickList;
import org.oscarehr.common.model.QuickListView;
import org.springframework.stereotype.Repository;

@Repository
public class QuickListDao extends AbstractDao<QuickList>{

	public QuickListDao() {
		super(QuickList.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<QuickList> findAll() {
		Query query = createQuery("x", null);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<Object> findDistinct() {
    	Query query = entityManager.createQuery("select distinct ql.quickListName from QuickList ql");
    	return query.getResultList();
	}

	@SuppressWarnings("unchecked")
    public List<QuickList> findByNameResearchCodeAndCodingSystem(String quickListName, String researchCode, String codingSystem) {
	    Query query = entityManager.createQuery("from QuickList q where q.quickListName = :qlName AND q.dxResearchCode = :rc AND q.codingSystem = :cs");
	    query.setParameter("qlName", quickListName);
	    query.setParameter("rc", researchCode);
	    query.setParameter("cs", codingSystem);
	    return query.getResultList();
    }

	public QuickList findLast() {
		Query query = createQuery("ql", "ORDER BY ql.quickListName");
		query.setMaxResults(1);
		return getSingleResultOrNull(query);
    }

	@SuppressWarnings("unchecked")
	public List<QuickList> findByCodingSystem(String codingSystem) {
		String csQuery = "";
        if ( codingSystem != null ){
        	csQuery = " WHERE ql.codingSystem = :cs";
        }
		Query query = entityManager.createQuery("select ql from QuickList ql " + csQuery + " GROUP BY ql.quickListName");
		if (codingSystem != null) {
			query.setParameter("cs", codingSystem);
		}
		return query.getResultList();
    }

	@NativeSql
	@SuppressWarnings("unchecked")
	public List<Object[]> findResearchCodeAndCodingSystemDescriptionByCodingSystem(String codingSystem, String quickListName) {
		try {
        	String sql = "Select q.dxResearchCode, c.description FROM quickList q, "+codingSystem
					+" c where codingSystem = '"+codingSystem+"' and quickListName='"+ quickListName +"' AND c."+codingSystem
					+" = q.dxResearchCode order by c.description";
			Query query = entityManager.createNativeQuery(sql);
			return query.getResultList();
		} catch (Exception e) {
			// TODO-legacy replace when test ignores are merged
			return new ArrayList<Object[]>();
		}
		
    }

	/**
	 * gets a list of quick List entries with some data from their respective code tables
	 * This is native since jpa has trouble with this kind of join
	 * @return List of quickListView objects
	 */
	@NativeSql
	@SuppressWarnings("unchecked")
	public List<QuickListView> getQuickLists() {
		String sql = "SELECT DISTINCT " +
				"x.quickListName, " +
				"x.dxResearchCode AS code, " +
				"x.codingSystem, " +
				"COALESCE(d9.description, ich.description) as description " +
				"FROM quickList x " +
				"LEFT JOIN icd9 d9 ON (x.dxResearchCode = d9.icd9 AND x.codingSystem='icd9') " +
				"LEFT JOIN ichppccode ich ON (x.dxResearchCode = ich.ichppccode AND x.codingSystem='ichppc') " +
				"ORDER BY x.quickListName" +
				";";
		Query query = entityManager.createNativeQuery(sql);
		List<Object[]> queryResults = query.getResultList();
		return toQuickListView(queryResults);
	}
	/**
	 * gets a list of quick List entries with some data from their respective code tables
	 * The results have a corresponding code entry in the issue table
	 * This is native since jpa has trouble with this kind of join
	 * @return List of quickListView objects
	 */
	@NativeSql
	@SuppressWarnings("unchecked")
	public List<QuickListView> getIssueQuickLists() {

		String sql = "SELECT DISTINCT " +
				"x.quickListName, " +
				"x.dxResearchCode AS code, " +
				"x.codingSystem, " +
				"COALESCE(d9.description, ich.description) as description " +
				"FROM quickList x " +
				"JOIN issue iss ON (x.dxResearchCode = iss.code AND x.codingSystem = iss.type)" +
				"LEFT JOIN icd9 d9 ON (x.dxResearchCode = d9.icd9 AND x.codingSystem='icd9') " +
				"LEFT JOIN ichppccode ich ON (x.dxResearchCode = ich.ichppccode AND x.codingSystem='ichppc') " +
				"ORDER BY x.quickListName" +
				";";
		Query query = entityManager.createNativeQuery(sql);
		List<Object[]> queryResults = query.getResultList();
		return toQuickListView(queryResults);
	}
	/** convert native sql results directly to an object */
	private List<QuickListView> toQuickListView(List<Object[]> queryResults) {

		List<QuickListView> resultList = new ArrayList<QuickListView>();
		for(Object[] record : queryResults) {
			String quickListName = (String) record[0];
			String code = (String) record[1];
			String codingSystem = (String) record[2];
			String description = (String) record[3];

			QuickListView entry = new QuickListView();
			entry.setQuickListName(quickListName);
			entry.setCode(code);
			entry.setCodingSystem(codingSystem);
			entry.setDescription(description);

			resultList.add(entry);
		}
		return resultList;
	}
}
