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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.DxAssociation;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

// To prevent SQL injection attacks, we will enumerate the possible coding systems.  These correspond
// to structural elements of the SQL query (table names, column names) when looking in the database.
//
// Adding a new coding system will require it to be enumerated here.
enum CodingSystem {

	ICD9 ("icd9"),
	ICHPPCCODE("ichppccode");

	private String text;
	private static final Map<String, CodingSystem> VALID_SYSTEMS;
	private static Logger logger = MiscUtils.getLogger();

	static
	{
		Map<String, CodingSystem> initMap = new ConcurrentHashMap<>();
		for (CodingSystem system : CodingSystem.values())
		{
			initMap.put(system.toString(), system);
		}
		VALID_SYSTEMS = Collections.unmodifiableMap(initMap);
	}

	CodingSystem(String name)
	{
		this.text = name;
	}

	public String toString()
	{
		return this.text;
	}

	public static String selectSystem(String name)
	{
		CodingSystem toReturn = VALID_SYSTEMS.get(name);

		if (toReturn == null)
		{
			String errorMessage = "Invalid coding system specified: " + name;
			logger.error(errorMessage);
			throw new IllegalArgumentException(errorMessage);
		}

		return toReturn.toString();
	}

}

@Repository
public class DxDao extends AbstractDao<DxAssociation> {

	private static final Logger logger = MiscUtils.getLogger();

	public DxDao() {
		super(DxAssociation.class);
	}

	public List<DxAssociation> findAllAssociations()
	{			
		Query query = entityManager.createQuery("select x from DxAssociation x order by x.dxCodeType,x.dxCode");
		
		@SuppressWarnings("unchecked")
		List<DxAssociation> results = query.getResultList();

		return(results);
	}
    
    public int removeAssociations() {
    	Query query = entityManager.createQuery("DELETE from DxAssociation");
    	return query.executeUpdate();
    }
    
    public DxAssociation findAssociation(String codeType, String code) {    	
    	Query query = entityManager.createQuery("SELECT x from DxAssociation x where x.codeType = ?1 and x.code = ?2");
    	query.setParameter(1, codeType);
    	query.setParameter(2, code);
    	    	
        @SuppressWarnings("unchecked")
    	List<DxAssociation> results = query.getResultList();
    	if(!results.isEmpty()) {
    		return results.get(0);
    	}
    	return null;
    }

    @NativeSql
    @SuppressWarnings("unchecked")
	public List<Object[]> findCodingSystemDescription(String codingSystem, String code) {
		try {
			codingSystem = CodingSystem.selectSystem(codingSystem);

			String sql = "SELECT " + codingSystem +", description FROM " + codingSystem + " WHERE " + codingSystem + " = :code";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("code", code);
			return query.getResultList();
		} catch (Exception e) {
			// TODO-legacy Add exclude to the test instead when it's merged
			return new ArrayList<Object[]>();
		}
    }
	
	@NativeSql
    @SuppressWarnings("unchecked")
	public List<Object[]> findCodingSystemDescription(String codingSystem, String[] keywords)
	{
		try
		{
			codingSystem = CodingSystem.selectSystem(codingSystem);
			StringBuilder buf = new StringBuilder("select " + codingSystem + ", description from " + codingSystem);

			List<String> keywordsArray = new ArrayList<>();

			for (String keyword : keywords)
			{
				if (keyword != null && !keyword.trim().isEmpty())
				{
					keywordsArray.add(keyword);
				}
			}

			for (int i = 0; i < keywordsArray.size(); i++)
			{
				if (i == 0)
				{
					buf.append(" where ");
				}
				else
				{
					buf.append(" or ");
				}

				// We will rely on the persistence library to escape potentially dangerous strings for us by
				// parameterizing all search strings ('%:Param1%', '%:Param2%', etc)
				String queryParam = ":Param" + i;
				buf.append(" " + codingSystem + " like " + queryParam + " or description like " + queryParam);
			}

			Query query = entityManager.createNativeQuery(buf.toString());

			for (int i = 0; i < keywordsArray.size(); i++)
			{
				String paramName = "Param" + i;
				query.setParameter(paramName, "%" + keywordsArray.get(i) + "%");
			}
			return query.getResultList();
		}
		catch (Exception e)
		{
			logger.error("error",e);
			return new ArrayList<Object[]>();
		}
	}

	@NativeSql
	public String getCodeDescription(String codingSystem, String code)
	{
		String desc = "";
		codingSystem = CodingSystem.selectSystem(codingSystem);
		StringBuilder buf = new StringBuilder("select description from " + codingSystem + " where " + codingSystem + "= :code");
		try
		{
			Query query = entityManager.createNativeQuery(buf.toString());
			query.setParameter("code", code);
			desc = (String) query.getSingleResult();
		}
		catch(Exception e)
		{
			logger.error("error " + buf, e);
		}
		return desc;
	}
}
