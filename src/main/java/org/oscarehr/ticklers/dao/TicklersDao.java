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
package org.oscarehr.ticklers.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Session;
import org.oscarehr.common.PaginationQuery;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.ticklers.entity.Tickler;
import org.oscarehr.common.search.AbstractCriteriaSearch;
import org.oscarehr.ticklers.web.TicklerQuery;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.Query;
import java.util.List;

@Repository
public class TicklersDao extends AbstractDao<Tickler> {

	public TicklersDao() {
		super(Tickler.class);
	}

	public int getTicklersCount(PaginationQuery paginationQuery) {
		StringBuilder sql = this.generateQuery(paginationQuery,true);
		Query query = entityManager.createQuery(sql.toString());
		
		Long x = (Long)query.getSingleResult();
		return x.intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Tickler> getTicklers(TicklerQuery ticklerQuery) {
		StringBuilder sql = this.generateQuery(ticklerQuery,false);
		Query query = entityManager.createQuery(sql.toString());
		query.setFirstResult(ticklerQuery.getStart());
		query.setMaxResults(ticklerQuery.getLimit());
		return query.getResultList();
	}

	private StringBuilder generateQuery(PaginationQuery paginationQuery, boolean selectCountOnly) {
		TicklerQuery ticklerQuery = (TicklerQuery) paginationQuery;
		StringBuilder sql = new StringBuilder("select "+ (selectCountOnly?"count(*)":"t") + " FROM Tickler t where "
			+ " t.serviceDate >= '" + DateFormatUtils.ISO_DATETIME_FORMAT.format(ticklerQuery.getStartDate()) 
			+ "' and t.serviceDate <= '" + DateFormatUtils.ISO_DATETIME_FORMAT.format(ticklerQuery.getEndDate()) + "' ");
		
		
		if (ticklerQuery.getMrps() != null && ticklerQuery.getMrps().length>0) {
			sql = new StringBuilder("select "+ (selectCountOnly?"count(*)":"t") + " FROM Tickler t, Demographic d where "
					+ " t.serviceDate >= '" + DateFormatUtils.ISO_DATETIME_FORMAT.format(ticklerQuery.getStartDate()) 
					+ "' and t.serviceDate <= '" + DateFormatUtils.ISO_DATETIME_FORMAT.format(ticklerQuery.getEndDate()) + "' ");
			sql.append("and d.DemographicNo = cast(t.demographicNo as integer) ");
			
			sql.append(" and d.ProviderNo IN (");
			String[] mrps = ticklerQuery.getMrps();
			for (int x = 0; x < mrps.length; x++) {
				if (x > 0) {
					sql.append(",");
				}
				sql.append("'").append(mrps[x]).append("'");
			}
			sql.append(") ");
		}	
		
		if (StringUtils.isNotBlank(ticklerQuery.getStatus())) {
			sql.append(" and t.status = '" + ticklerQuery.getStatus() + "' ");
		}		

		if (StringUtils.isNotBlank(ticklerQuery.getKeyword())) {
			sql.append("and (");
			sql.append("t.demographicNo like '%" + ticklerQuery.getKeyword() + "%' ");
			sql.append("or t.provider like '%" + ticklerQuery.getKeyword() + "%' ");
			sql.append("or t.message like '%" + ticklerQuery.getKeyword() + "%' ");
			sql.append("or t.creator like '%" + ticklerQuery.getKeyword() + "%' ");
			sql.append("or t.taskAssignedTo like '%" + ticklerQuery.getKeyword() + "%' ");
			sql.append(") ");
		}
		
		if (ticklerQuery != null) {
			if (StringUtils.equals("true", ticklerQuery.getWithOption())) {

				if (StringUtils.isNotBlank(ticklerQuery.getProgramId())) {
					sql.append(" and t.programId = '" + ticklerQuery.getProgramId() + "' ");
				}
				
				if (StringUtils.isNotBlank(ticklerQuery.getDemographicNo())) {
					sql.append(" and t.demographicNo = '" + ticklerQuery.getDemographicNo() + "' ");
				}
				if (StringUtils.isNotBlank(ticklerQuery.getClient())) {
					sql.append(" and t.demographicNo = '" + ticklerQuery.getClient() + "' ");
				}
				if (StringUtils.isNotBlank(ticklerQuery.getMessage())) {
					sql.append(" and t.message = '" + ticklerQuery.getMessage() + "' ");
				}		
				
				if (StringUtils.isNotBlank(ticklerQuery.getProviderNo())) {
					sql.append("and t.provider = '" + ticklerQuery.getProviderNo() + "' ");
				}
				
				if (ticklerQuery.getProviders() != null && ticklerQuery.getProviders().length>0) {
					sql.append(" and t.creator IN (");
					String[] providers = ticklerQuery.getProviders();
					for (int x = 0; x < providers.length; x++) {
						if (x > 0) {
							sql.append(",");
						}
						sql.append("'").append(providers[x]).append("'");
					}
					sql.append(") ");
				}


				if (ticklerQuery.getAssignees() != null && ticklerQuery.getAssignees().length>0) {
					sql.append(" and t.taskAssignedTo IN (");
					String[] assignees = ticklerQuery.getAssignees();
					for (int x = 0; x < assignees.length; x++) {
						if (x > 0) {
							sql.append(",");
						}
						sql.append("'").append(assignees[x]).append("'");
					}
					sql.append(") ");
				}
			}
			String sort = ticklerQuery.getSort();
			if(!sort.equalsIgnoreCase("asc") && !sort.equalsIgnoreCase("desc")) {
				MiscUtils.getLogger().warn("invalid sort parameter passwd for ticklers: "  + sort);
				sort = "";
			}
			
			String orderby = ticklerQuery.getOrderby();
			if (StringUtils.isBlank(orderby) || "null".equals(orderby)) {
				sql.append(" order by t.serviceDate Asc ");
			} else if (orderby.equals("serviceDate")) {
				sql.append(" order by t.serviceDate " + sort);
			} else if (orderby.equals("demographicName")) {
				sql.append(" order by t.provider " + sort);
			} else if (orderby.equals("updateDate")) {
				sql.append(" order by t.updateDate " + sort);
			} else if (orderby.equals("providerName")) {
				sql.append(" order by t.creator " + sort);
			} else if (orderby.equals("assigneeName")) {
				sql.append(" order by t.taskAssignedTo " + sort);
			} else if (orderby.equals("priority")) {
				sql.append(" order by t.priority " + sort);
			} else if (orderby.equals("status")) {
				sql.append(" order by t.status " + sort);
			}  else {
				sql.append(" order by t." + orderby + " " + sort);
			}
		}
		return sql;
	}

	/**
	 * Deactivate the specified ticklers
	 *
	 * @param ticklerIDs tickler IDs to deactivate
	 */
	public void deactivateAllTicklers(List<Integer> ticklerIDs)
	{
		Query query = entityManager.createQuery("UPDATE Tickler t SET t.status = :deleteStatus WHERE t.id IN (:ticklerIDs)");
		query.setParameter("deleteStatus", Tickler.STATUS.D);
		query.setParameter("ticklerIDs", ticklerIDs);

		query.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tickler> criteriaSearch(AbstractCriteriaSearch criteriaSearch)
	{
		Session session = (Session) entityManager.getDelegate();

		Criteria criteria = session.createCriteria(Tickler.class);
		criteria = criteriaSearch.setCriteriaProperties(criteria);

		criteria.setFetchMode("ticklerCategory", FetchMode.LAZY);
		criteria.setFetchMode("updates", FetchMode.LAZY);
		criteria.setFetchMode("comments", FetchMode.LAZY);
		criteria.setFetchMode("demographic", FetchMode.LAZY);
		criteria.setFetchMode("provider", FetchMode.LAZY);
		criteria.setFetchMode("assignee", FetchMode.LAZY);
		criteria.setFetchMode("program", FetchMode.LAZY);

		criteria.setMaxResults(criteriaSearch.getLimit());
		criteria.setFirstResult(criteriaSearch.getOffset());

		return criteria.list();
	}
}
