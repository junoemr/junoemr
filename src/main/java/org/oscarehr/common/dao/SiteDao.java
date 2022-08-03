/**
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 */

package org.oscarehr.common.dao;

import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.Site;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import oscar.util.ConversionUtils;

import javax.persistence.Query;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Repository
public class SiteDao extends AbstractDao<Site> {

	@Autowired
	private ProviderSiteDao providerSiteDao;

	@Autowired
	private ProviderDataDao providerDao;

	public SiteDao() {
		super(Site.class);
	}

	public void save(Site s) {
		boolean isUpdate = s.getSiteId() != null && s.getSiteId() > 0;
		if (isUpdate) {
			Site old = find(s.getSiteId());
			if (!old.getName().equals(s.getName())) {
				// site name changed, need to update all references as it serves as PK
				// so we need to update the tables that references to the site

				Query query = entityManager.createNativeQuery("update rschedule set avail_hour = replace(avail_hour, :oldname, :newname) ");
				query.setParameter("oldname", ">"+old.getName()+"<");
				query.setParameter("newname", ">"+s.getName()+"<");
				query.executeUpdate();

				query = entityManager.createNativeQuery("update scheduledate set reason = :newname where reason = :oldname");
				query.setParameter("oldname", old.getName());
				query.setParameter("newname", s.getName());
				query.executeUpdate();

				query = entityManager.createNativeQuery("update appointment set location = :newname where location = :oldname");
				query.setParameter("oldname", old.getName());
				query.setParameter("newname", s.getName());
				query.executeUpdate();

				query = entityManager.createNativeQuery("update billing_on_cheader1 set clinic = :newname where clinic = :oldname");
				query.setParameter("oldname", old.getName());
				query.setParameter("newname", s.getName());
				query.executeUpdate();


			}
		}

		Set<ProviderData> providers = new HashSet<>();

		List<ProviderSite> psList = providerSiteDao.findBySiteId(s.getSiteId());
		for(ProviderSite ps : psList) {
			ProviderData p = providerDao.find(ps.getId().getProviderNo());
			providers.add(p);
		}
		s.setAssignedProviders(providers);
		
		if(s.getSiteLogoId() != null && s.getSiteLogoId().intValue()==0)
			s.setSiteLogoId(null);
		merge(s);

	}

	public List<Site> getAllSites() {
		Query query = this.entityManager.createQuery("select s from Site s order by s.name");

		@SuppressWarnings("unchecked")
        List<Site> rs = query.getResultList();
		return rs;
	}

	public List<Site> getAllActiveSites() {
		Query query = this.entityManager.createQuery("select s from Site s where s.status=1 order by s.name");

		@SuppressWarnings("unchecked")
        List<Site> rs = query.getResultList();

		return rs;
	}

	@Deprecated  // Use SiteService.getActiveSitesForProvider instead
	public List<Site> getActiveSitesByProviderNo(String provider_no) {
		List<ProviderSite> pss = providerSiteDao.findByProviderNo(provider_no);

		List<Site> rs = new ArrayList<Site>();
		for(ProviderSite ps:pss) {
			rs.add(find(ps.getId().getSiteId()));
		}

		Iterator<Site> it = rs.iterator();
		while (it.hasNext()) {
			Site site = it.next();
			// remove inactive sites
			if (site.getStatus() == 0)
				it.remove();
		}

		Collections.sort(rs, new Comparator<Site>() {
			public int compare(Site o1, Site o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		return rs;
	}

	public Site getById(Integer id) {
		return find(id);
	}

	public Site getByLocation(String location) {
		Query query = this.entityManager.createQuery("select s from Site s where s.name=?1");
		query.setParameter(1, location);

		@SuppressWarnings("unchecked")
        List<Site> rs = query.getResultList();

		if (rs.size()>0)
			return rs.get(0);
		else
			return null;
	}

	public List<String> getGroupBySiteLocation(String location) {
		Query query = entityManager.createNativeQuery(
				"select distinct g.mygroup_no from mygroup g	" +
						" inner join provider p on p.provider_no = g.provider_no and p.status = 1 " +
						" inner join providersite ps on ps.provider_no = g.provider_no " +
						" inner join site s on s.site_id = ps.site_id " +
						" where  s.name = :sitename ");
		query.setParameter("sitename",location);

		@SuppressWarnings("unchecked")
        List<String> groupList = query.getResultList();


		return groupList;
	}

	public List<String> getProviderNoBySiteLocation(String location) {

		Query query = entityManager.createNativeQuery(
					"select distinct p.provider_no	" +
					" from provider p " +
					" inner join providersite ps on ps.provider_no = p.provider_no " +
					" inner join site s on s.site_id = ps.site_id " +
					" where  s.name = :sitename ") ;

		query.setParameter("sitename", location);

		@SuppressWarnings("unchecked")
        List<String> pList = query.getResultList();


		return pList;
	}

	public List<String> getProviderNoBySiteManagerProviderNo(String providerNo) {

		Query query = entityManager.createNativeQuery(
					"select distinct p.provider_no	" +
					" from provider p " +
					" inner join providersite ps on ps.provider_no = p.provider_no and p.status = 1" +
					" where ps.site_id in (select site_id from providersite where provider_no = :providerno)");
		query.setParameter("providerno", providerNo);

		@SuppressWarnings("unchecked")
        List<String> pList = query.getResultList();

		return pList;
	}

	/**
	 * Get the site that the provider is assigned to on the specific schedule date.
	 * @param providerNo - provider in question
	 * @param sdate - date in question
	 * @return - the assigned site.
	 */
	public Site getProviderSiteByScheduleDate(String providerNo, LocalDate sdate)
	{
		Query query = entityManager.createQuery("SELECT sd.site FROM ScheduleDate sd WHERE sd.date = :sdate AND sd.providerNo = :providerNo AND sd.status = 'A'");
		query.setParameter("sdate", ConversionUtils.toLegacyDate(sdate));
		query.setParameter("providerNo", providerNo);
		return getSingleResultOrNull(query);
	}

	public List<String> getGroupBySiteManagerProviderNo(String providerNo) {

		Query query = entityManager.createNativeQuery(
					"select distinct g.mygroup_no from mygroup g	" +
					" inner join provider p on p.provider_no = g.provider_no and p.status = 1 " +
					" inner join providersite ps on ps.provider_no = g.provider_no " +
					" where ps.site_id in (select site_id from providersite where provider_no = :providerno)");
		query.setParameter("providerno", providerNo);

		@SuppressWarnings("unchecked")
        List<String> groupList = query.getResultList();


		return groupList;
	}
	
	public Long site_searchmygroupcount(String myGroupNo, String siteName) {
		Query query = entityManager.createNativeQuery("select count(provider_no) from mygroup where mygroup_no=:groupno  and provider_no in (select ps.provider_no from providersite ps inner join site s on ps.site_id = s.site_id where s.name = :sitename)");
		query.setParameter("groupno", myGroupNo);
		query.setParameter("sitename", siteName);

		Long result = ((BigInteger)query.getSingleResult()).longValue();
		return result;
	}

	public String getSiteNameByAppointmentNo(String appointmentNo) {

		Query query = entityManager.createNativeQuery("select location from appointment where appointment_no = :appointmentno");
		query.setParameter("appointmentno", appointmentNo);

		@SuppressWarnings("unchecked")
        List<String> list = query.getResultList();
		if(list.size()>0) {
			return list.get(0);
		}

		return "";
	}
	
	public List<String> getGroupsBySiteProviderNo(String groupNo) {
		List<String> groupList = new ArrayList<String>();
		Query  query = entityManager.createNativeQuery(
				"select distinct g.mygroup_no from mygroup g	" +
				" inner join provider p on p.provider_no = g.provider_no and p.status = 1 " +
				" inner join providersite ps on ps.provider_no = g.provider_no " +
				" where ps.site_id in (select site_id from providersite where provider_no = :providerno)");
		query.setParameter("providerno", groupNo);

		groupList = query.getResultList();
		return groupList;
	}	
	
	public List<String> getGroupsForAllSites() {
		List<String> groupList = new ArrayList<String>();
		Query  query = entityManager.createNativeQuery(
				"select distinct g.mygroup_no from mygroup g	" +
				" inner join provider p on p.provider_no = g.provider_no and p.status = 1 " +
				" inner join providersite ps on ps.provider_no = g.provider_no");

		groupList = query.getResultList();
		return groupList;
	}
	
	public Site findByName(String name) {
		Query query = entityManager.createQuery("select site from Site site where site.name = ?1");
		query.setParameter(1, name);
		try {
			return (Site) query.getSingleResult();
		} catch (Exception e) {}
		return null;
	}

	public Site findByShortName(String sname)
	{
		Query query = entityManager.createQuery("select site from Site site where site.shortName = ?1");
		query.setParameter(1, sname);
		return getSingleResultOrNull(query);
	}
}
