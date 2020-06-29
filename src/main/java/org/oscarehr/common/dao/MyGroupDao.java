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



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.oscarehr.common.model.MyGroup;
import org.oscarehr.common.model.MyGroupPrimaryKey;
import org.oscarehr.common.model.Provider;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;
/**
 *
 * @author Toby
 */
@Repository
public class MyGroupDao extends AbstractDao<MyGroup> {

	public MyGroupDao() {
		super(MyGroup.class);
	}

	@SuppressWarnings("unchecked")
	public List<MyGroup> findAll() {
		Query query = createQuery("x", null);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<MyGroup> findAllOrdered()
	{
		Query query = entityManager.createQuery("SELECT g FROM MyGroup g, ProviderData p " +
				"WHERE p.id = g.id.providerNo " +
				"AND p.status = 1 " +
				"GROUP BY g.id.myGroupNo " +
				"ORDER BY g.id.myGroupNo, g.id.providerNo");
		return query.getResultList();
	}

     public List<String> getGroupDoctors (String groupNo){

        Query query = entityManager.createQuery("SELECT g.id.providerNo FROM MyGroup g WHERE g.id.myGroupNo=?1");
        query.setParameter(1, groupNo);

        @SuppressWarnings("unchecked")
        List<String> dList = query.getResultList();

        if (dList != null && dList.size() > 0) {
            return dList;
        } else {
            return null;
        }
     }

     public List<String> getGroups(){
    	 Query query = entityManager.createQuery("SELECT distinct g.id.myGroupNo FROM MyGroup g");

         @SuppressWarnings("unchecked")
         List<String> dList = query.getResultList();

         return dList;
     }
     
     public List<MyGroup> getGroupByGroupNo(String groupNo) {
         Query query = entityManager.createQuery("SELECT g FROM MyGroup g where g.id.myGroupNo = ?1");
         query.setParameter(1, groupNo);
         
         @SuppressWarnings("unchecked")
         List<MyGroup> dList = query.getResultList();

         return dList;
     }

    /**
     * Gets only the group members that have a schedule for the provided date.
     * @param groupNo The group to get the records for
	 * @param date The date to check for schedules
     * @param providerNo Include this provider even if there is no schedule
     * @return List of type MyGroup
     */
    public List<MyGroup> getGroupWithScheduleByGroupNo(String groupNo, LocalDate date, Integer providerNo)
	{
        String sql =
			"SELECT \n" +
			"  g.mygroup_no, \n" +
			"  g.provider_no, \n" +
			"  g.last_name,\n" +
			"  g.first_name, \n" +
			"  g.vieworder, \n" +
			"  g.default_billing_form\n" +
			"FROM mygroup g \n" +
			"LEFT JOIN scheduledate sd \n" +
			"ON sd.status = 'A' \n" +
			"AND sd.sdate = :date \n" +
			"AND sd.provider_no = g.provider_no\n" +
			"WHERE g.mygroup_no = :groupNo \n" +
			"AND (sd.id IS NOT NULL OR g.provider_no = :providerNo)";

		Query query = entityManager.createNativeQuery(sql);

		query.setParameter("date", java.sql.Date.valueOf(date), TemporalType.DATE);
		query.setParameter("groupNo", groupNo);
		query.setParameter("providerNo", providerNo);

        @SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		List<MyGroup> groupList = new ArrayList<>();
		for(Object[] result: results)
		{
			MyGroup myGroup = new MyGroup();

			MyGroupPrimaryKey id = new MyGroupPrimaryKey();
			id.setMyGroupNo((String) result[0]);
			id.setProviderNo((String) result[1]);

			myGroup.setId(id);
			myGroup.setLastName((String) result[2]);
			myGroup.setFirstName((String) result[3]);
			myGroup.setViewOrder((String) result[4]);
			myGroup.setDefaultBillingForm((String) result[5]);

			groupList.add(myGroup);
		}

        return groupList;
    }

     public void deleteGroupMember(String myGroupNo, String providerNo){
    	 MyGroupPrimaryKey key = new MyGroupPrimaryKey();
    	 key.setMyGroupNo(myGroupNo);
    	 key.setProviderNo(providerNo);
    	 remove(key);
     }
     
     public List<MyGroup> getProviderGroups(String providerNo) {
         Query query = entityManager.createQuery("SELECT g FROM MyGroup g WHERE g.id.providerNo = ?1");
         query.setParameter(1, providerNo);
         
         @SuppressWarnings("unchecked")
         List<MyGroup> dList = query.getResultList();

         return dList;
     }
     
     public String getDefaultBillingForm(String myGroupNo) {
         Query query = entityManager.createQuery("SELECT distinct g.defaultBillingForm FROM MyGroup g WHERE g.id.myGroupNo = ?1");
         query.setParameter(1, myGroupNo);
         
         @SuppressWarnings("unchecked")
         List<String> dList = query.getResultList();         

         if (dList.size() > 1)
             MiscUtils.getLogger().warn("More than one Default biling form for this group. Should only be one");
         String billingForm = "";         
         if (dList != null && !dList.isEmpty())
             billingForm = dList.get(0);
         return billingForm;
     }
     
     public List<Provider> search_groupprovider (String groupNo){

         Query query = entityManager.createQuery("SELECT p  FROM MyGroup g, Provider p WHERE g.id.myGroupNo=?1 and p.ProviderNo = g.id.providerNo order by p.LastName");
         query.setParameter(1, groupNo);

         @SuppressWarnings("unchecked")
         List<Provider> dList = query.getResultList();

         return dList;
      }
     
     public List<MyGroup> search_mygroup(String groupNo) {
         Query query = entityManager.createQuery("SELECT g FROM MyGroup g WHERE g.id.myGroupNo like ?1 group by g.id.myGroupNo order by g.id.myGroupNo");
         query.setParameter(1, groupNo);
         
         @SuppressWarnings("unchecked")
         List<MyGroup> dList = query.getResultList();

         return dList;
     }
     
 
     public List<MyGroup> searchmygroupno() {
         Query query = entityManager.createQuery("SELECT g FROM MyGroup g group by g.id.myGroupNo order by g.id.myGroupNo");
         
         @SuppressWarnings("unchecked")
         List<MyGroup> dList = query.getResultList();

         return dList;
     }
     
     public List<MyGroup> search_providersgroup(String lastName, String firstName) {
         Query query = entityManager.createQuery("SELECT g FROM MyGroup g where g.lastName like ?1 and g.firstName like ?2 order by g.lastName, g.firstName, g.id.myGroupNo");
         query.setParameter(1, lastName);
         query.setParameter(2, firstName);
         
         @SuppressWarnings("unchecked")
         List<MyGroup> dList = query.getResultList();

         return dList;
     }
}
