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
import java.util.List;

import javax.persistence.Query;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ProfessionalSpecialistDao extends AbstractDao<ProfessionalSpecialist> {

	public ProfessionalSpecialistDao() {
		super(ProfessionalSpecialist.class);
	}

	/**
	 * Sorted by lastname,firstname
	 */
	public List<ProfessionalSpecialist> findAll()
	{
		Query query = entityManager.createQuery("select x from " + modelClass.getSimpleName() + " x where x.hideFromView = false order by x.lastName,x.firstName");

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> results=query.getResultList();

		return(results);
	}

	public long getNumOfSpecialists(String searchText, Boolean hideFromView)
	{
		String queryString = "SELECT COUNT(x) FROM " + modelClass.getSimpleName() + " x WHERE (x.firstName LIKE :searchText OR x.lastName LIKE :searchText) AND (:hideFromView IS NULL OR x.hideFromView=:hideFromView)";

		Query query = entityManager.createQuery(queryString);
		query.setParameter("searchText", searchText + "%");
		query.setParameter("hideFromView", hideFromView);

		Long numOfSpecialists = (Long) query.getSingleResult();
		return numOfSpecialists;
	}

	public long getNumOfSpecialistsDeleted(String searchText)
	{
		return getNumOfSpecialists(searchText, true);
	}

	public long getNumOfSpecialistsActive(String searchText)
	{
		return getNumOfSpecialists(searchText, false);
	}

	public List<ProfessionalSpecialist> findBySearchName(String searchText, int offset, int maxResults, Boolean hideFromView)
	{
		String queryString = "SELECT x FROM " + modelClass.getSimpleName() + " x WHERE (x.firstName LIKE :searchText OR x.lastName LIKE :searchText) AND (:hideFromView IS NULL OR x.hideFromView=:hideFromView) ORDER BY x.lastName, x.firstName";

		Query query = entityManager.createQuery(queryString);
		query.setParameter("searchText", searchText + "%");
		query.setParameter("hideFromView", hideFromView);

		query.setFirstResult(offset);
		query.setMaxResults(maxResults);

		List<ProfessionalSpecialist> results = query.getResultList();
		return results;
	}

	public List<ProfessionalSpecialist> findBySearchNameDeleted(String searchText, int offset, int maxResults)
	{
		return findBySearchName(searchText, offset, maxResults, true);
	}

	public List<ProfessionalSpecialist> findBySearchNameActive(String searchText, int offset, int maxResults)
	{
		return findBySearchName(searchText, offset, maxResults, false);
	}

	/**
	 * Sorted by lastname,firstname
	 */
	public List<ProfessionalSpecialist> findByEDataUrlNotNull()
	{
		Query query = entityManager.createQuery("select x from "+modelClass.getSimpleName()+" x where x.hideFromView = false and x.eDataUrl is not null order by x.lastName,x.firstName");

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> results=query.getResultList();

		return(results);
	}

	/**
	 * use version with limit and offset. This method returns null when no results are found?
	 */
	@Deprecated
	public List<ProfessionalSpecialist> findByFullName(String lastName, String firstName)
	{
		List<ProfessionalSpecialist> cList = findByFullName(lastName, firstName, null, null);
		if (cList != null && cList.size() > 0)
		{
			return cList;
		}
		return null;
	}

	public List<ProfessionalSpecialist> findByLastName(String lastName) {
		return findByFullName(lastName, "", null, null);
	}


	public List<ProfessionalSpecialist> findBySpecialty(String specialty) {
		Query query = entityManager.createQuery("select x from " + modelClass.getName() + " x WHERE x.hideFromView = false and x.specialtyType like ?1 order by x.lastName");
		query.setParameter(1, "%"+specialty+"%");

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> cList = query.getResultList();

		if (cList != null && cList.size() > 0) {
			return cList;
		}

		return null;

	}

	/**
	 * use version with limit and offset. This method returns null when no results are found?
	 */
	@Deprecated
	public List<ProfessionalSpecialist> findByReferralNo(String referralNo)
	{
		if (StringUtils.isBlank(referralNo))
		{
			return null;
		}
		List<ProfessionalSpecialist> cList = findByReferralNo(referralNo, null, null);

		if (cList != null && cList.size() > 0)
		{
			return cList;
		}
		return null;
	}

	public List<ProfessionalSpecialist> findByReferralNo(String referralNo, Integer offset, Integer maxResults)
	{
		return findByFullNameAndReferralNo(null, null, referralNo, offset, maxResults);
	}

	public List<ProfessionalSpecialist> findByFullName(String lastName, String firstName, Integer offset, Integer maxResults)
	{
		return findByFullNameAndReferralNo(lastName, firstName, null, offset, maxResults);
	}

	public List<ProfessionalSpecialist> findByFullNameAndReferralNo(String lastName, String firstName, String referralNo, Integer offset, Integer maxResults)
	{
		// set up the query
		String queryString =
				"SELECT x FROM " + modelClass.getSimpleName() + " x " +
				"WHERE x.hideFromView = false ";

		if (lastName != null)
			queryString += "AND ( x.lastName LIKE :lastName ) ";
		if (firstName != null)
			queryString += "AND ( x.firstName LIKE :firstName ) ";
		if (referralNo != null)
			queryString += "AND ( x.referralNo LIKE :refNo ) ";

		queryString += "ORDER BY x.lastName, x.firstName";

		Query query = entityManager.createQuery(queryString);

		// set parameters
		if (lastName != null)
			query.setParameter("lastName", lastName + "%");
		if (firstName != null)
			query.setParameter("firstName", firstName + "%");
		if (referralNo != null)
			query.setParameter("refNo", referralNo + "%");
		if (offset != null)
			query.setFirstResult(offset);
		if (maxResults != null)
			query.setMaxResults(maxResults);

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> results = query.getResultList();
		return results;
	}

	public ProfessionalSpecialist getByReferralNo(String referralNo) {
		List<ProfessionalSpecialist> cList = findByReferralNo(referralNo);

		if (cList != null && cList.size() > 0) {
			return cList.get(0);
		}
		return null;
	}

	public boolean hasRemoteCapableProfessionalSpecialists()
	{
		return(findByEDataUrlNotNull().size()>0);
	}


	public List<ProfessionalSpecialist> search(String keyword) {
		StringBuilder where = new StringBuilder();
		List<String> paramList = new ArrayList<String>();

		String searchMode = "search_name";
		String orderBy = "c.lastName,c.firstName";

		if(searchMode.equals("search_name")) {
			String[] temp = keyword.split("\\,\\p{Space}*");
			if(temp.length>1) {
		      where.append("c.lastName like ?1 and c.firstName like ?2");
		      paramList.add(temp[0]+"%");
		      paramList.add(temp[1]+"%");
		    } else {
		      where.append("c.lastName like ?1");
		      paramList.add(temp[0]+"%");
		    }
		}
		String sql = "SELECT c from ProfessionalSpecialist c where " + where.toString() + " and c.hideFromView = false order by " + orderBy;
		MiscUtils.getLogger().info(sql);
		Query query = entityManager.createQuery(sql);
		for(int x=0;x<paramList.size();x++) {
			query.setParameter(x+1,paramList.get(x));
		}

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> contacts = query.getResultList();
		return contacts;
	}

	public List<ProfessionalSpecialist> findByFullNameAndSpecialtyAndAddress(String lastName, String firstName, String specialty, String address, Boolean showHidden) {
		String sql = "select x from " + modelClass.getName() + " x WHERE (x.lastName like ?1 and x.firstName like ?2) ";

		int paramCount = 3;
		if(!StringUtils.isEmpty(specialty)) {
			sql += " AND x.specialtyType LIKE ?" + paramCount++ + " ";
		}

		if(!StringUtils.isEmpty(address)) {
			sql += " AND x.streetAddress LIKE ?" + paramCount++ + " ";
		}

		if(showHidden == null || !showHidden) {
			sql += " AND x.hideFromView=false ";
		}
		sql += " order by x.lastName";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, "%"+lastName+"%");
		query.setParameter(2, "%"+firstName+"%");

		int index=3;
		if(!StringUtils.isEmpty(specialty)) {
			query.setParameter(index++, "%" + specialty +"%");
		}
		if(!StringUtils.isEmpty(address)) {
			query.setParameter(index++, "%" + address +"%");
		}

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> cList = query.getResultList();

		return cList;
	}

	public List<ProfessionalSpecialist> findByService(String serviceName) {
		Query query = entityManager.createQuery("select x from " + modelClass.getName() + " x, ConsultationServices cs, ServiceSpecialists ss WHERE x.hideFromView = false and x.id = ss.id.specId and ss.id.serviceId = cs.serviceId and cs.serviceDesc = ?1");
		query.setParameter(1, serviceName);

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> cList = query.getResultList();


		return cList;
	}

	public List<ProfessionalSpecialist> findByServiceId(Integer serviceId) {
		Query query = entityManager.createQuery("select x from " + modelClass.getName() + " x, ServiceSpecialists ss WHERE x.hideFromView = false and x.id = ss.id.specId and ss.id.serviceId = ?1");
		query.setParameter(1, serviceId);

		@SuppressWarnings("unchecked")
		List<ProfessionalSpecialist> cList = query.getResultList();


		return cList;
	}


}
