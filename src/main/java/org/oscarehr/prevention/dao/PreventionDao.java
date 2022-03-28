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
package org.oscarehr.prevention.dao;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.prevention.dto.PreventionListData;
import org.oscarehr.prevention.model.Prevention;
import org.oscarehr.prevention.model.PreventionExt;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("dao.preventionDao")
@Transactional
public class PreventionDao extends AbstractDao<Prevention>
{

	public PreventionDao() {
		super(Prevention.class);
	}

	public List<Prevention> findByDemographicId(Integer demographicId) {
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x where x.demographicId=?1");
		query.setParameter(1, demographicId);

		List<Prevention> results = query.getResultList();

		return (results);
	}
    
	/**
	 * @return results ordered by lastUpdateDate
	 */
	public List<Prevention> findByUpdateDate(Date updatedAfterThisDateExclusive, int itemsToReturn) {
		String sqlCommand = "select x from "+modelClass.getSimpleName()+" x where x.lastUpdateDate>?1 order by x.lastUpdateDate";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter(1, updatedAfterThisDateExclusive);
		setLimit(query, itemsToReturn);
		
		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();
		return (results);
	}

    public List<Prevention> findByDemographicIdAfterDatetime(Integer demographicId, Date dateTime) {
    	Query query = entityManager.createQuery("select x from Prevention x where x.demographicId=?1 and x.lastUpdateDate>=?2 and x.deleted='0'");
    	query.setParameter(1, demographicId);
		query.setParameter(2, dateTime);

		@SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

		return (results);
	}
    
	/*
	 * for integrator
	 */
	public List<Integer> findDemographicIdsAfterDatetime(Date dateTime) {
		Query query = entityManager.createQuery("select x.demographicId from Prevention x where x.lastUpdateDate > ?1");
		query.setParameter(1, dateTime);

		@SuppressWarnings("unchecked")
		List<Integer> results = query.getResultList();

		return (results);
	}
	
	public List<Prevention> findByProviderDemographicLastUpdateDate(String providerNo, Integer demographicId, Date updatedAfterThisDateExclusive, int itemsToReturn) {
		String sqlCommand = "select x from "+modelClass.getSimpleName()+" x where x.demographicId=:demographicId and x.providerNo=:providerNo and x.lastUpdateDate>:updatedAfterThisDateExclusive order by x.lastUpdateDate";

		Query query = entityManager.createQuery(sqlCommand);
		query.setParameter("demographicId", demographicId);
		query.setParameter("providerNo", providerNo);
		query.setParameter("updatedAfterThisDateExclusive", updatedAfterThisDateExclusive);
		setLimit(query, itemsToReturn);
		
		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();
		return (results);
	}

	public List<Prevention> findNotDeletedByDemographicIdAfterDatetime(Integer demographicId, Date dateTime) {
		Query query = entityManager.createQuery("select x from Prevention x where x.demographicId=?1 and x.lastUpdateDate> ?2");
		query.setParameter(1, demographicId);
		query.setParameter(2, dateTime);

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();

		return (results);
	}
	
	public List<Integer> findNonDeletedIdsByDemographic(Integer demographicId) {
		Query query = entityManager.createQuery("select x.id from Prevention x where x.demographicId=?1 and x.deleted='0'");
		query.setParameter(1, demographicId);
	
		@SuppressWarnings("unchecked")
		List<Integer> results = query.getResultList();

		return (results);
	}
	

	public List<Prevention> findNotDeletedByDemographicId(Integer demographicId) {
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x where x.demographicId=?1 and x.deleted=?2");
		query.setParameter(1, demographicId);
		query.setParameter(2, '0');

		@SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

		return (results);
	}

	public List<Prevention> findByTypeAndDate(String preventionType, Date startDate, Date endDate)
	{
		String sql = "SELECT x FROM Prevention x " +
				"WHERE x.preventionType = :preventionType " +
				"AND x.preventionDate BETWEEN :startDate AND :endDate " +
				"AND x.deleted='0'" +
				"AND x.refused='0'" +
				"ORDER BY x.preventionDate";
		Query query = entityManager.createQuery(sql);
		query.setParameter("preventionType", preventionType);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		@SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();

		return (results);
	}

	public List<Prevention> findByTypeAndDemoNo(String preventionType, Integer demoNo) {
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x where x.preventionType=?1 and x.demographicId=?2 and x.deleted='0' order by x.preventionDate");
		query.setParameter(1, preventionType);
		query.setParameter(2, demoNo);
		
		@SuppressWarnings("unchecked")
        List<Prevention> results = query.getResultList();
		return (results);
	}

	@SuppressWarnings("unchecked")
    public List<Prevention> findActiveByDemoId(Integer demoId) {
		Query query = createQuery("p", "p.demographicId = :demoNo and p.deleted <> '1' ORDER BY p.preventionType, p.preventionDate");
		query.setParameter("demoNo", demoId);
		return query.getResultList();
	}
	

	public List<Prevention> findMostRecentByDemographic(Integer demographicId)
	{
		String sql = "SELECT p.* FROM (\n" +
				"    SELECT ROW_NUMBER() OVER (PARTITION BY prevention_type ORDER BY prevention_date DESC) AS rank, p1.id\n" +
				"    FROM preventions p1" +
				"    WHERE p1.demographic_no=:demographicId\n" +
				"    AND p1.deleted=:deleted) as filter\n" +
				"JOIN preventions p ON (p.id = filter.id)\n" +
				"WHERE filter.rank = 1";

		Query query = entityManager.createNativeQuery(sql, Prevention.class);
		query.setParameter("demographicId", demographicId);
		query.setParameter("deleted", '0');

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();
		return (results);
	}

	public Prevention findMostRecentByTypeAndDemoNo(String preventionType, Integer demoNo)
	{
		Query query = entityManager.createQuery("SELECT x FROM Prevention x " +
				"WHERE x.preventionType = :preventionType " +
				"AND x.demographicId = :demographicNo " +
				"AND x.deleted='0' " +
				"ORDER BY x.preventionDate DESC, x.id DESC");

		query.setParameter("preventionType", preventionType);
		query.setParameter("demographicNo", demoNo);

		query.setMaxResults(1);

		@SuppressWarnings("unchecked")
		List<Prevention> results = query.getResultList();
		return (results.isEmpty()) ? null : results.get(0);
	}
	
	
	@NativeSql("preventions")
	public List<Integer> findNewPreventionsSinceDemoKey(String keyName) {
		
		String sql = "select distinct dr.demographic_no from preventions dr,demographic d,demographicExt e where dr.demographic_no = d.demographic_no and d.demographic_no = e.demographic_no and e.key_val=?1 and dr.lastUpdateDate > e.value";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter(1,keyName);
		return query.getResultList();
	}

	public Prevention getPreventionFromExt(PreventionExt ext)
	{
		return ext.getPrevention();
	}

	public Map<String, PreventionListData> getPreventionListData(String demographicNo)
	{
		return getPreventionListData(Integer.parseInt(demographicNo));
	}

	@NativeSql("preventions")
	public Map<String, PreventionListData> getPreventionListData(Integer demographicNo)
	{
		String sql = "select " +
				"  p.id,\n" +
				"  p.prevention_type,\n" +
				"  p.prevention_date,\n" +
				"  p.refused,\n" +
				"  pe_result.val as ext_result,\n" +
				"  p_count.count as prevention_count\n" +
				"from preventions p\n" +
				"left join preventions p_filter\n" +
				"    on binary p.prevention_type = binary p_filter.prevention_type\n" +
				"    and p.demographic_no = p_filter.demographic_no\n" +
				"    and p.deleted = p_filter.deleted\n" +
				"    and (\n" +
				"        p.prevention_date < p_filter.prevention_date\n" +
				"        or (p.prevention_date = p_filter.prevention_date and p.id < p_filter.id)\n" +
				"        )\n" +
				"join (\n" +
				"         select p.prevention_type, count(*) as count\n" +
				"         from preventions p\n" +
				"         where p.demographic_no = :demographicNo\n" +
				"           and p.deleted = 0\n" +
				"         group by binary p.prevention_type\n" +
				"     ) as p_count on binary p.prevention_type = binary p_count.prevention_type\n" +
				"left join preventionsExt pe_result on p.id = pe_result.prevention_id and pe_result.keyval = 'result'\n" +
				"where p.demographic_no = :demographicNo\n" +
				"and p_filter.id is null\n" +
				"and p.deleted = 0\n";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("demographicNo", demographicNo);
		List<Object[]> results = query.getResultList();

		Map<String, PreventionListData> out = new HashMap<>();
		for(Object[] row: results)
		{
			int column = 0;

			PreventionListData data = new PreventionListData();

			data.setPreventionId((Integer) row[column++]);

			String type = (String) row[column++];
			data.setType(type);

			LocalDateTime preventionDate = null;
			if(row[column++] != null)
			{
				preventionDate = ((Timestamp) row[column - 1]).toLocalDateTime();
			}
			data.setPreventionDate(preventionDate);

			data.setRefused((Character) row[column++]);
			data.setPreventionResult((String) row[column++]);
			data.setPreventionCount(((BigInteger) row[column++]).intValue());

			out.put(type, data);
		}

		return out;
	}
}
