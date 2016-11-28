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
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class EFormDataDao extends AbstractDao<EFormData> {

	public static final int DEFAULT_MAX_RESULTS = 10;
	public static final int DEFAULT_PAGE = 0;

	private static final Logger logger=MiscUtils.getLogger();

	public EFormDataDao() {
		super(EFormData.class);
	}

	public List<EFormData> findFiltered(
		Integer demographicId,
		Integer eformId,
		Date startDate,
		Date endDate,
		Integer itemsPerPage,
		Integer page,
		boolean enabledPaging)
	{
		int maxResults = DEFAULT_MAX_RESULTS;
		int cleanPage = DEFAULT_PAGE;

		if(itemsPerPage != null)
		{
			maxResults = itemsPerPage;
		}

		if(page != null)
		{
			cleanPage = page;
		}

		int firstResult = (maxResults * cleanPage);

		List<Object> paramArray = new ArrayList<Object>();

		int paramCount = 1;
		String sql = "from " + modelClass.getSimpleName() + " x " +
			"where true = true ";

		// Filter by demographic if it's provided OR if there is no paging
		if(!enabledPaging || (demographicId != null && demographicId != 0))
		{
			sql += "and x.demographicId=?" + paramCount + " ";
			paramArray.add(demographicId);
			paramCount++;
		}

		if(startDate != null)
		{
			sql += "and x.formDate >= ?" + paramCount + " ";
			paramArray.add(startDate);
			paramCount++;
		}

		if(endDate != null)
		{
			sql += "and x.formDate <= ?" + paramCount + " ";
			paramArray.add(endDate);
			paramCount++;
		}

		sql += "order by x.id desc ";

		String querySql = "select x " + sql;

		// Make the query object
		Query query = entityManager.createQuery(querySql);

		addParameters(query, paramArray);

		if(enabledPaging)
		{
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
		}

		@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}

    public List<EFormData> findByDemographicId(Integer demographicId)
	{
		Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.demographicId=?1");
		query.setParameter(1, demographicId);

		@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}

    public List<EFormData> findByDemographicIdAndEForm(Integer demographicId, Integer eformId)
	{
		Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.demographicId=?1 and x.formId=?2");
		query.setParameter(1, demographicId);
		query.setParameter(2, eformId);

		@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}

    public List<EFormData> findByDemographicIdSinceLastDate(Integer demographicId,Date lastDate)
	{
    	Calendar cal1 = Calendar.getInstance();
    	cal1.setTime(lastDate);

		Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.demographicId=?1 and x.formDate > ?2 or (x.formDate= ?3 and x.formTime >= ?4)");
		query.setParameter(1, demographicId);
		query.setParameter(2, lastDate);
		query.setParameter(3, lastDate);
		query.setParameter(4, lastDate);

		@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}

    /**
     * @param demographicId can not be null
     * @param current can be null for both
     * @param patientIndependent can be null to be both
     * @return
     */
    public List<EFormData> findByDemographicIdCurrentPatientIndependent(Integer demographicId, Boolean current, Boolean patientIndependent)
	{
    	StringBuilder sb=new StringBuilder();
    	sb.append("select x from ");
    	sb.append(modelClass.getSimpleName());
    	sb.append(" x where x.demographicId=?1");

    	int counter=2;

    	if (current!=null)
    	{
    		sb.append(" and x.current=?");
    		sb.append(counter);
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		sb.append(" and x.patientIndependent=?");
    		sb.append(counter);
    		counter++;
    	}

    	String sqlCommand=sb.toString();

    	logger.debug("SqlCommand="+sqlCommand);

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, demographicId);

    	counter=2;

    	if (current!=null)
    	{
    		query.setParameter(counter, current);
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		query.setParameter(counter, patientIndependent);
    		counter++;
    	}

    	@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}
    
    /**
     * @param demographicId can not be null
     * @param current can be null for both
     * @param patientIndependent can be null to be both
     * @return
     */
    public List<EFormData> findByDemographicIdCurrentPatientIndependentGroupByFormId(Integer demographicId, Boolean current, Boolean patientIndependent)
	{
    	String sqlCommand = "select * from "
    			+ "(select * from eform_data order by fdid desc) eform_data_ordered "
    			+ "where demographic_no=(?1) and status='1' ";
    	
    	int counter=2;
    	if (current!=null)
    	{
    		sqlCommand += " and status=(?"+counter+")";
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		sqlCommand += " and patient_independent=(?"+counter+")";
    		counter++;
    	}
    	
    	sqlCommand += " group by fid order by fdid";
    	
    	logger.debug("SqlCommand="+sqlCommand);

		Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
		query.setParameter(1, demographicId);

    	counter=2;

    	if (current!=null)
    	{
    		query.setParameter(counter, current);
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		query.setParameter(counter, patientIndependent);
    		counter++;
    	}

    	@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();
    	MiscUtils.getLogger().debug("here?");
    	MiscUtils.getLogger().debug(results);

		return(results);
	}
    
    /**
     * @param demographicId can not be null
     * @param current can be null for both
     * @param patientIndependent can be null to be both
     * @return
     */
    public List<Map<String,Object>> findByDemographicIdCurrentPatientIndependentNoData(Integer demographicId, Boolean current, Boolean patientIndependent)
	{
    	StringBuilder sb=new StringBuilder();
    	sb.append("select new map(x.id as id, x.formId as formId, x.formName as formName, x.subject as subject, x.demographicId as demographicId, x.current as current, x.formDate as formDate, x.formTime as formTime, x.providerNo as providerNo, x.patientIndependent as patientIndependent, x.roleType as roleType) from ");
    	sb.append(modelClass.getSimpleName());
    	sb.append(" x where x.demographicId=?1");

    	int counter=2;

    	if (current!=null)
    	{
    		sb.append(" and x.current=?");
    		sb.append(counter);
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		sb.append(" and x.patientIndependent=?");
    		sb.append(counter);
    		counter++;
    	}

    	String sqlCommand=sb.toString();

    	logger.debug("SqlCommand="+sqlCommand);

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, demographicId);

    	counter=2;

    	if (current!=null)
    	{
    		query.setParameter(counter, current);
    		counter++;
    	}

    	if (patientIndependent!=null)
    	{
    		query.setParameter(counter, patientIndependent);
    		counter++;
    	}

    	@SuppressWarnings("unchecked")
		List<Map<String,Object>> results=query.getResultList();

		return(results);
	}

    public List<EFormData> findPatientIndependent(Boolean current)
	{
    	StringBuilder sb=new StringBuilder();
    	sb.append("select x from ");
    	sb.append(modelClass.getSimpleName());
    	sb.append(" x where x.patientIndependent=true");

    	if (current!=null)
    	{
    		sb.append(" and x.current=?1");
    	}

    	String sqlCommand=sb.toString();
    	logger.debug("SqlCommand="+sqlCommand);

		Query query = entityManager.createQuery(sqlCommand);
		
    	if (current!=null)
    	{
    		query.setParameter(1, current);
    	}

    	@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();

		return(results);
	}
    
    public List<EFormData> findByFdids(List<Integer> ids)
	{
    	if(ids.size()==0)
    		return new ArrayList<EFormData>();
    	
    	Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.id in (:ids)");
		query.setParameter("ids", ids);
		
		@SuppressWarnings("unchecked")
		List<EFormData> results=query.getResultList();
		
		return results;	
	}

	private void addParameters(Query query, List paramArray)
	{
		int paramCount = 1;
		Iterator paramIterator = paramArray.iterator();
		while(paramIterator.hasNext())
		{
			query.setParameter(paramCount, paramIterator.next());

			paramCount++;
		}
	}
}
