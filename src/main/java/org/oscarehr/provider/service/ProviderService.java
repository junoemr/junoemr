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
import org.oscarehr.common.dao.ProviderSiteDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.exception.NoSuchRecordException;
import org.oscarehr.common.model.ProviderSite;
import org.oscarehr.common.model.Security;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.dataMigration.converter.out.ProviderDbToModelConverter;
import org.oscarehr.dataMigration.model.provider.ProviderModel;
import org.oscarehr.managers.ProviderManager2;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.providerBilling.dao.ProviderBillingDao;
import org.oscarehr.providerBilling.model.ProviderBilling;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.site.service.SiteService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.exception.SecurityRecordAlreadyExistsException;
import org.oscarehr.ws.rest.transfer.providerManagement.ProviderEditFormTo1;
import org.oscarehr.ws.rest.transfer.providerManagement.SecurityRecordTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.oscarProvider.data.ProviderBillCenter;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("provider.service.ProviderService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProviderService
{
	@Autowired
	ProviderDao providerDao;

	@Autowired
	ProviderDataDao providerDataDao;

	@Autowired
	SecurityDao securityDao;

	@Autowired
	UserPropertyDAO userPropertyDAO;

	@Autowired
	private ProviderRoleService providerRoleService;

	@Autowired
	private SiteService siteService;

	@Autowired
	private SecUserRoleDao secUserRoleDao;

	@Autowired
	private ProviderSiteDao providerSiteDao;

	@Autowired
	private ProviderBillingDao providerBillingDao;

	@Autowired
	private ProviderManager2 providerManager;

	@Autowired
	private ProviderDbToModelConverter providerDbToModelConverter;

	public ProviderData getProvider(String providerNo)
	{
		return providerDataDao.findByProviderNo(providerNo);
	}

	/**
	 * Fetch a provider including with all associations preloaded.  Use this get method if you are in a layer which
	 * doesn't support transactions and you need to read or write to lazily loaded entities.
	 *
	 * @param providerNo id of Provider to fetch
	 * @return Fully instantiated provider object
	 */
	public ProviderData getProviderEager(String providerNo)
	{
		return providerDataDao.eagerFindByProviderNo(providerNo);
	}

	public void saveProvider(ProviderData provider)
	{
		providerDataDao.merge(provider);
	}

	public List<ProviderModel> getActiveProviders()
	{
		return providerDbToModelConverter.convert(providerDataDao.findByActiveStatus(true));
	}

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

	public ProviderBilling getProviderBilling(String providerNo)
	{
		ProviderData provider = providerDataDao.findByProviderNo(providerNo);

		if (provider == null)
		{
			return null;
		}

		return provider.getBillingOpts();
	}

	/**
	 * enable or disable a provider
	 * @param providerNo - the provider to enable / disable
	 * @param enable - if true enable. if false disable.
	 */
	public void enableProvider(Integer providerNo, Boolean enable)
	{
		ProviderData provider = providerDataDao.find(providerNo.toString());
		if (provider != null)
		{
			if (enable)
			{
				provider.setStatus(ProviderData.PROVIDER_STATUS_ACTIVE);
			}
			else
			{
				provider.setStatus(ProviderData.PROVIDER_STATUS_INACTIVE);
			}
		}
		else
		{
			throw new NoSuchRecordException("No provider with id: " + providerNo);
		}
	}

	/**
	 * get the edit form transfer object for the given provider.
	 * @param providerNo - the provider to get the form for.
	 * @return - the edit provider form.
	 */
	public ProviderEditFormTo1 getEditFormForProvider(String providerNo, Security loggedInSecurity)
	{
		ProviderData provider = providerDataDao.findByProviderNo(providerNo);
		ProviderEditFormTo1 providerEditFormTo1 = new ProviderEditFormTo1();

		//set provider fields
		providerEditFormTo1.setProviderData(provider);

		//set billing fields
		ProviderBilling providerBilling = providerBillingDao.findByProviderId(providerNo);
		if (providerBilling != null)
		{
			providerEditFormTo1.setProviderBilling(providerBilling);
		}

		//set security records
		//Security unameSec = securityDao.findProviderUserNameSecurityRecord(providerNo.toString());
		providerEditFormTo1.setSecurityRecords(SecurityRecordTo1.fromList(securityDao.findByProviderNo(providerNo)));
		providerEditFormTo1.setCurrentSecurityRecord(loggedInSecurity.getSecurityNo());

		//set sites
		List<ProviderSite> providerSites = providerSiteDao.findByProviderNo(providerNo);
		ArrayList<Integer> siteList = new ArrayList<>();
		ArrayList<Integer> bcpSites = new ArrayList<>();
		for(ProviderSite providerSite: providerSites)
		{
			siteList.add(providerSite.getId().getSiteId());
			if (providerSite.isBcBCPEligible())
			{
				bcpSites.add(providerSite.getId().getSiteId());
			}
		}
		providerEditFormTo1.setSiteAssignments(siteList);
		providerEditFormTo1.setBcpSites(bcpSites);

		//set roles
		List<SecUserRole> userRoles = secUserRoleDao.getUserRoles(provider.getProviderNo().toString());
		List<Integer> roleIds = new ArrayList<>();
		for (SecUserRole role : userRoles)
		{
			roleIds.add(role.getSecRole().getId());
		}
		providerEditFormTo1.setUserRoles(roleIds);

		providerEditFormTo1.setOlisOfficialFirstName(userPropertyDAO.getStringValue(providerNo, UserProperty.OFFICIAL_FIRST_NAME));
		providerEditFormTo1.setOlisOfficialSecondName(userPropertyDAO.getStringValue(providerNo, UserProperty.OFFICIAL_SECOND_NAME));
		providerEditFormTo1.setOlisOfficialLastName(userPropertyDAO.getStringValue(providerNo, UserProperty.OFFICIAL_LAST_NAME));
		providerEditFormTo1.setOlisOfficialIdType(userPropertyDAO.getStringValue(providerNo, UserProperty.OFFICIAL_OLIS_IDTYPE));

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
		provider.setBillingOpts(providerBilling);
		providerDataDao.persist(provider);

		createProviderSecurityRecords(providerEditFormTo1, provider.getProviderNo());

		updateProviderSiteAndRole(providerEditFormTo1, provider.getProviderNo());

		setProviderProperties(providerEditFormTo1, provider.getId());

		setJunoUIAsDefault(provider.getProviderNo());
		return provider;
	}

	/**
	 * edit a provider record.
	 * @param providerEditFormTo1 - the provider edit forum from the front end containing the provider information to edit
	 * @param providerNo - the providerNo to edit
	 * @param editingProviderNo - the providerNo doing the edit
	 * @return - the update provider
	 */
	public synchronized ProviderData editProvider(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo, String editingProviderNo)
	{
		ProviderData providerData = providerDataDao.find(providerNo.toString());
		if (providerData != null)
		{
			ProviderData newProviderData = providerEditFormTo1.getProviderData();

			// set last update date
			newProviderData.setLastUpdateUser(editingProviderNo);
			newProviderData.setLastUpdateDate(new Date());

			// transfer super admin flag
			newProviderData.setSuperAdmin(providerData.isSuperAdmin());

			// edit provider
			newProviderData.setProviderNo(providerNo);

			// edit billing data
			ProviderBilling providerBilling = providerEditFormTo1.getProviderBilling();
			if (providerData.getBillingOpts() != null)
			{
				providerBilling.setId(providerData.getBillingOpts().getId());
			}
			newProviderData.setBillingOpts(providerBilling);

			providerDataDao.merge(newProviderData);

			editProviderSecurityRecords(providerEditFormTo1, newProviderData.getProviderNo());

			updateProviderSiteAndRole(providerEditFormTo1, newProviderData.getProviderNo());

			setProviderProperties(providerEditFormTo1, newProviderData.getId());

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
	public synchronized void updateProviderSiteAndRole(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		// assign provider sites
		siteService.assignProviderSites(providerEditFormTo1.getSiteAssignments(), providerNo);
		siteService.removeOtherSites(providerEditFormTo1.getSiteAssignments(), providerNo);

		// update bcp sites
		updateBCPSiteAssignment(providerNo, providerEditFormTo1.getBcpSites());

		// assign provider roles
		if (providerEditFormTo1.getUserRoles() != null)
		{
			providerRoleService.assignProviderRoles(providerEditFormTo1.getUserRoles(), String.valueOf(providerNo));
			providerRoleService.removeOtherProviderRoles(providerEditFormTo1.getUserRoles(), providerNo);
			providerRoleService.setDefaultPrimaryRole(String.valueOf(providerNo));
		}
	}

	/**
	 * set Juno UI as default for provider
	 * @param providerNo - the provider to set the default UI for
	 */
	public synchronized void setJunoUIAsDefault(Integer providerNo)
	{
		UserProperty property = new UserProperty();
		property.setName(UserProperty.COBALT);
		property.setProviderNo(Integer.toString(providerNo));
		property.setValue(UserProperty.PROPERTY_ON_YES);
		userPropertyDAO.saveProp(property);
	}

	/**
	 * create new provider security records
	 * @param providerEditFormTo1 - the provider edit form containing the records to create
	 * @param providerNo - the provider to create the records for.
	 */
	private synchronized void createProviderSecurityRecords(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		try
		{
			List<Security> securityList = providerEditFormTo1.getSecurityRecords(providerNo, false);
			for (Security security : securityList)
			{
				if (security.getUserName() != null)
				{
					if (securityDao.findByUserName(security.getUserName()) != null)
					{
						throw new SecurityRecordAlreadyExistsException("Security Record already exists");
					}
				}

				if (security.getEmail() != null)
				{
					if (securityDao.findByEmail(security.getEmail()) != null)
					{
						throw new SecurityRecordAlreadyExistsException("Security Record already exists");
					}
				}

				securityDao.persist(security);
			}
		}
		catch (NoSuchAlgorithmException nae)
		{
			throw new RuntimeException("Internal Server error " + nae.toString());
		}
	}

	/**
	 * edit provider security records
	 * @param providerEditFormTo1 - the provider edit form containing security record update info
	 * @param providerNo - the provider number to edit records for.
	 */
	private synchronized void editProviderSecurityRecords(ProviderEditFormTo1 providerEditFormTo1, Integer providerNo)
	{
		try
		{
			List<Security> securityList = providerEditFormTo1.getSecurityRecords(providerNo, true);
			for (Security newSecurityRecord : securityList)
			{
				Security recordToEdit = securityDao.find(newSecurityRecord.getSecurityNo());

				if (recordToEdit != null)
				{
					if (newSecurityRecord.getUserName() != null)
					{
						Security userNameSecurity = securityDao.findByUserName(newSecurityRecord.getUserName());
						if (userNameSecurity != null && !userNameSecurity.getSecurityNo().equals(recordToEdit.getSecurityNo()))
						{
							throw new SecurityRecordAlreadyExistsException("Security Record already exists");
						}
					}

					if (newSecurityRecord.getEmail() != null)
					{
						Security emailSecurity = securityDao.findByEmail(newSecurityRecord.getEmail());
						if (emailSecurity != null && !emailSecurity.getSecurityNo().equals(recordToEdit.getSecurityNo()))
						{
							throw new SecurityRecordAlreadyExistsException("Security Record already exists");
						}
					}

					updateSecurityRecord(recordToEdit, newSecurityRecord);
					securityDao.merge(recordToEdit);
				}
			}
		}
		catch (NoSuchAlgorithmException nae)
		{
			throw new RuntimeException("Internal Server error " + nae.toString());
		}
	}

	/**
	 * add bcp to sites in "bcpSites" and remove it from all others for this provider
	 * @param providerNo - the provider to apply the update to.
	 * @param bcpSites - a list of sites to enable bcp on.
	 */
	private synchronized void updateBCPSiteAssignment(Integer providerNo, List<Integer> bcpSites)
	{
		if (bcpSites != null)
		{
			List<ProviderSite> providerSites = providerSiteDao.findByProviderNo(providerNo.toString());
			for (ProviderSite providerSite : providerSites)
			{
				if (bcpSites.contains(providerSite.getId().getSiteId()))
				{
					siteService.setSiteAsBCP(providerSite.getId().getSiteId(), providerNo, true);
				} else
				{
					siteService.setSiteAsBCP(providerSite.getId().getSiteId(), providerNo, false);
				}
			}
		}
	}

	private void updateSecurityRecord(Security existingRecord, Security source)
	{
		if (source.getPassword() != null)
		{
			existingRecord.setPassword(source.getPassword());
		}
		if (source.getPin() != null)
		{
			existingRecord.setPin(source.getPin());
		}
		existingRecord.setUserName(source.getUserName());
		existingRecord.setEmail(source.getEmail());
		existingRecord.setBExpireset(source.getBExpireset());
		existingRecord.setDateExpiredate(source.getDateExpiredate());
		existingRecord.setBLocallockset(source.getBLocallockset());
		existingRecord.setBRemotelockset(source.getBRemotelockset());
		existingRecord.setForcePasswordReset(source.isForcePasswordReset());
	}

	public void setProviderProperties(ProviderEditFormTo1 providerEditFormTo1, String providerNo)
	{
		providerManager.updateSingleSetting(providerNo, UserProperty.OFFICIAL_FIRST_NAME, StringUtils.trimToEmpty(providerEditFormTo1.getOlisOfficialFirstName()));
		providerManager.updateSingleSetting(providerNo, UserProperty.OFFICIAL_SECOND_NAME, StringUtils.trimToEmpty(providerEditFormTo1.getOlisOfficialSecondName()));
		providerManager.updateSingleSetting(providerNo, UserProperty.OFFICIAL_LAST_NAME, StringUtils.trimToEmpty(providerEditFormTo1.getOlisOfficialLastName()));
		providerManager.updateSingleSetting(providerNo, UserProperty.OFFICIAL_OLIS_IDTYPE, StringUtils.trimToEmpty(providerEditFormTo1.getOlisOfficialIdType()));
	}

	public void createAndSaveProviderImdHealthUuid(ProviderData providerData)
	{
		UUID newUuid = UUID.randomUUID();
		providerData.setImdHealthUuid(newUuid.toString());
		providerDataDao.merge(providerData);
	}
}
