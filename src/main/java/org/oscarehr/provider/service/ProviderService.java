/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */
package org.oscarehr.provider.service;

import org.apache.commons.lang.StringUtils;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.exception.NoSuchRecordException;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.ProviderSitePK;
import org.oscarehr.common.model.Security;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.providerBilling.dao.ProviderBillingDao;
import org.oscarehr.providerBilling.model.ProviderBilling;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.exception.SecurityRecordAlreadyExistsException;
import org.oscarehr.ws.rest.transfer.ProviderEditFormTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarProvider.data.ProviderBillCenter;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service("provider.service.ProviderService")
@Transactional
public class ProviderService
{
	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	SecurityDao securityDao;

	@Autowired
	private ProviderRoleService providerRoleService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private SecUserRoleDao secUserRoleDao;

	@Autowired
	private SecRoleDao secRoleDao;

	@Autowired
	private ProviderSiteDao providerSiteDao;

	@Autowired
	private ProviderBillingDao providerBillingDao;

	public ProviderData addNewProvider(String creatingProviderNo, ProviderData provider, String billCenterCode)
	{
		provider.setLastUpdateDate(new Date());
		provider.setLastUpdateUser(creatingProviderNo);
		if(provider.getStatus() == null)
		{
			provider.setStatus("1");
		}
		if(provider.getProviderType() == null)
		{
			provider.setProviderType("doctor");
		}
		if(provider.getSex() == null || provider.getSex().trim().isEmpty())
		{
			provider.setSex("U");
		}
		if(provider.getSpecialty() == null)
		{
			provider.setSpecialty("");
		}
		if(provider.getTeam() == null)
		{
			provider.setTeam("");
		}
		if(provider.getPhone() == null)
		{
			provider.setPhone("");
		}
		if(provider.getWorkPhone() == null)
		{
			provider.setWorkPhone("");
		}

		// providers don't have auto-generated IDs, so we have to pick one if it has not been provided
		if(provider.getId() == null)
		{
			Integer autoNumber = getNextProviderNumberInSequence(0, 900000);
			if(autoNumber == null)
			{
				// no providers exist in the given range, use id 1
				autoNumber = 1;
			}
			provider.set(String.valueOf(autoNumber));
		}
		providerDataDao.persist(provider);

		ProviderBillCenter billCenter = new ProviderBillCenter();
		billCenter.addBillCenter(String.valueOf(provider.getProviderNo()),StringUtils.trimToEmpty(billCenterCode));

		return provider;
	}

	/**
	 * get the highest id + 1 ignoring provider id's below the min threshold and above the ignore threshold
	 * @param minThreshold
	 * @param ignoreThreshold
	 * @return null if there are no providers in the given range, or the highest provider number + 1 otherwise
	 */
	public Integer getNextProviderNumberInSequence(int minThreshold, int ignoreThreshold)
	{
		return providerDataDao.getNextIdWithThreshold(minThreshold, ignoreThreshold);
	}

	/**
	 * get the edit form transfer object for the given provider.
	 * @param providerNo - the provider to get the form for.
	 * @return - the edit provider form.
	 */
	public ProviderEditFormTo1 getEditFormForProvider(Integer providerNo)
	{
		ProviderData provider = providerDataDao.findByProviderNo(providerNo.toString());
		ProviderEditFormTo1 providerEditFormTo1 = new ProviderEditFormTo1();

		//set provider fields
		providerEditFormTo1.setProviderData(provider);

		//set billing fields
		ProviderBilling providerBilling = providerBillingDao.getByProvider(providerNo);
		if (providerBilling != null)
		{
			providerEditFormTo1.setProviderBilling(providerBilling);
		}

		//set security records
		Security unameSec = securityDao.findProviderUserNameSecurityRecord(providerNo.toString());
		if (unameSec != null)
		{
			providerEditFormTo1.setUserName(unameSec.getUserName());
		}

		Security emailSec = securityDao.findProviderEmailSecurityRecord(providerNo.toString());
		if (emailSec != null)
		{
			providerEditFormTo1.setEmail(emailSec.getUserName());
		}

		//set sites
		List<ProviderSite> providerSites = providerSiteDao.findByProviderNo(provider.getProviderNo().toString());
		ArrayList<Integer> siteList = new ArrayList<>();
		for(ProviderSite providerSite: providerSites)
		{
			siteList.add(providerSite.getId().getSiteId());
		}
		providerEditFormTo1.setSiteAssignments(siteList);

		//set roles
		List<SecUserRole> userRoles = secUserRoleDao.getUserRoles(provider.getProviderNo().toString());
		List<Integer> roleIds = new ArrayList<>();
		for (SecUserRole role : userRoles)
		{
			roleIds.add(secRoleDao.findByName(role.getRoleName()).getId());
		}
		providerEditFormTo1.setUserRoles(roleIds);

		return providerEditFormTo1;
	}

	/**
	 * create a new provider record
	 * @param providerEditFormTo1 - provider creation form. contains info used to create the provider
	 * @param loggedInInfo - logged in info
	 * @return - the newly created provider object
	 */
	public synchronized ProviderData createProvider(ProviderEditFormTo1 providerEditFormTo1, LoggedInInfo loggedInInfo)
	{
		// create provider record
		ProviderData provider = this.addNewProvider(loggedInInfo.getLoggedInProviderNo(), providerEditFormTo1.getProviderData(), "");

		// save billing data
		ProviderBilling providerBilling = providerEditFormTo1.getProviderBilling();
		providerBilling.setProviderNo(provider.getProviderNo());
		providerBillingDao.persist(providerBilling);

		updateProviderSiteSecRole(providerEditFormTo1, provider.getProviderNo());

		return provider;
	}

	public synchronized ProviderData editProvider(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		ProviderData providerData = providerDataDao.find(providerNo.toString());
		if (providerData != null)
		{
			ProviderData newProviderData = providerEditFormTo1.getProviderData();

			// edit provider
			newProviderData.setProviderNo(providerNo);
			providerDataDao.merge(newProviderData);

			// edit billing data
			ProviderBilling providerBilling = providerEditFormTo1.getProviderBilling();
			ProviderBilling existingBillingData = providerBillingDao.getByProvider(providerNo);
			if (existingBillingData != null)
			{
				providerBilling.setId(existingBillingData.getId());
			}
			providerBilling.setProviderNo(newProviderData.getProviderNo());
			providerBillingDao.merge(providerBilling);

			updateProviderSiteSecRole(providerEditFormTo1, newProviderData.getProviderNo());

			return newProviderData;
		}
		else
		{
			throw new NoSuchRecordException("No provider record for ProviderNo:" + providerNo);
		}
	}

	/**
	 * assign / update provider security records, site records, and role records.
	 * @param providerEditFormTo1 - provider edit from containing site, security, role info.
	 * @param providerNo - provider no
	 */
	public synchronized void updateProviderSiteSecRole(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		// edit security records
		upsertProviderSecurityRecords(providerEditFormTo1, providerNo);

		// assign provider sites
		siteService.assignProviderSites(providerEditFormTo1.getSiteAssignments(), providerNo);
		siteService.removeOtherSites(providerEditFormTo1.getSiteAssignments(), providerNo);

		// assign provider roles
		if (providerEditFormTo1.getUserRoles() != null)
		{
			providerRoleService.assignProviderRoles(providerEditFormTo1.getUserRoles(), providerNo);
			providerRoleService.removeOtherProviderRoles(providerEditFormTo1.getUserRoles(), providerNo);
			providerRoleService.setDefaultPrimaryRole(providerNo);
		}
	}

	/**
	 * create or update provider security records.
	 * @param providerEditFormTo1 - the provider form to use to update / create records
	 * @param providerNo - the provider no to create for.
	 */
	private synchronized void upsertProviderSecurityRecords(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		try
		{
			List<Security> securityList = providerEditFormTo1.getSecurityRecords(providerNo);
			for (Security security : securityList)
			{
				List<Security> existingRecords = securityDao.findByUserName(security.getUserName());
				if (existingRecords != null && existingRecords.size() == 1)
				{
					if (existingRecords.get(0).getProviderNo().equals(providerNo.toString()))
					{// we "own" this record. update.
						security.setSecurityNo(existingRecords.get(0).getSecurityNo());
						securityDao.merge(security);
					}
					else
					{
						throw new SecurityRecordAlreadyExistsException("Security Record already exists");
					}
				}
				else
				{
					securityDao.persist(security);
				}
			}
		}
		catch (NoSuchAlgorithmException nae)
		{
			throw new RuntimeException("Internal Server error " + nae.toString());
		}
	}
}
