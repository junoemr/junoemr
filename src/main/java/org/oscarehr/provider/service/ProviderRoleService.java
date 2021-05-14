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

import com.quatro.dao.security.SecuserroleDao;
import com.quatro.model.security.Secuserrole;
import org.apache.log4j.Logger;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.RecycleBinDao;
import org.oscarehr.common.model.RecycleBin;
import org.oscarehr.provider.dao.ProgramProviderDao;
import org.oscarehr.provider.model.ProgramProvider;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class ProviderRoleService
{
	public static final Logger logger = MiscUtils.getLogger();

	@Autowired
	SecRoleDao securityRoleDao;

	@Autowired
	ProgramProviderDao programProviderDao;

	@Autowired
	ProgramManager programManager;

	@Autowired
	SecuserroleDao secUserRoleDao;

	@Autowired
	SecRoleDao secRoleDao;

	@Autowired
	RecycleBinDao recycleBinDao;


	/**
	 * @param providerId - of the newly added provider's
	 */

	public boolean setDefaultRoleForNewProvider(Integer providerId)
	{
		try
		{
			SecRole defaultRole = secRoleDao.findSystemDefaultRole();
			addRole(providerId, defaultRole);
			return true;
		}
		catch (IllegalStateException e)
		{
			logger.error("Default Role Error", e);
		}
		return false;
	}

	public boolean setDefaultPrimaryRole(Integer providerNo)
	{
		try
		{
			SecRole defaultRole = secRoleDao.findSystemDefaultRole();
			return setPrimaryRole(providerNo, defaultRole);
		}
		catch (IllegalStateException e)
		{
			logger.error("Default Role Error", e);
		}
		return false;
	}

	/**
	 * Assign a primary role to the provider
	 * @param providerId - provider record id
	 * @param roleName - name of the role to assign
	 * @return - if no role in the table match property file's default role, return false;
	 * @deprecated - don't look up roles by name
	 */
	@Deprecated
	public boolean setPrimaryRole(Integer providerId, String roleName)
	{
		SecRole secRole = securityRoleDao.findByName(roleName);
		return  setPrimaryRole(providerId, secRole);
	}
	public boolean setPrimaryRole(Integer providerId, SecRole secRole)
	{
		// not roleName in the table that matching default roleName from property file
		if(secRole == null)
		{
			return false;
		}

		Long roleId = secRole.getId().longValue();
		Long caisiProgram = new Long(programManager.getDefaultProgramId());

		ProgramProvider programProvider = programProviderDao.getProgramProvider(String.valueOf(providerId), caisiProgram);
		if(programProvider != null)
		{
			programProvider.setRoleId(roleId);
			programProviderDao.merge(programProvider);
		}
		else
		{
			programProvider = new ProgramProvider();
			programProvider.setProgramId(caisiProgram);
			programProvider.setProviderNo(String.valueOf(providerId));
			programProvider.setRoleId(roleId);
			programProviderDao.persist(programProvider);
		}

		return true;
	}

	@Deprecated // don't look up roles by name
	public Secuserrole addRole(Integer roleProviderId, String roleName)
	{
		SecRole role = secRoleDao.findByName(roleName);
		return addRole(roleProviderId, role);
	}
	public Secuserrole addRole(Integer roleProviderId, SecRole secRole)
	{
		Secuserrole secUserRole = new Secuserrole();
		int defaultActiveyn = 1;

		secUserRole.setProviderNo(String.valueOf(roleProviderId));
		secUserRole.setRoleName(secRole.getName());
		secUserRole.setRoleId(secRole.getId());
		secUserRole.setActiveyn(defaultActiveyn);
		secUserRoleDao.save(secUserRole);
		return secUserRole;
	}

	@Deprecated // don't look up roles by name
	public Secuserrole addRoleAndAssignPrimary(Integer roleProviderId, String roleName)
	{
		SecRole secRole = secRoleDao.findByName(roleName);
		return addRoleAndAssignPrimary(roleProviderId, secRole);
	}
	public Secuserrole addRoleAndAssignPrimary(Integer roleProviderId, SecRole secRole)
	{
		Secuserrole secUserRole = addRole(roleProviderId, secRole);

		Long caisiProgram = new Long(programManager.getDefaultProgramId());
		ProgramProvider programProvider = programProviderDao.getProgramProvider(String.valueOf(roleProviderId), caisiProgram);
		if(programProvider == null)
		{
			programProvider = new ProgramProvider();
			programProvider.setProgramId(caisiProgram);
			programProvider.setProviderNo(String.valueOf(roleProviderId));
			programProvider.setRoleId(Long.valueOf(secRole.getId()));
			programProviderDao.persist(programProvider);
		}
		else
		{
			programProvider.setProgramId(caisiProgram);
			programProvider.setProviderNo(String.valueOf(roleProviderId));
			programProvider.setRoleId(Long.valueOf(secRole.getId()));
			programProviderDao.merge(programProvider);
		}
		return secUserRole;
	}

	public boolean hasRole(Integer roleProviderId, String roleName)
	{
		List<Secuserrole> existingRoles = secUserRoleDao.findByProviderAndRoleName(String.valueOf(roleProviderId), roleName);
		return !existingRoles.isEmpty();
	}

	public void updateRole(Integer currentProviderId, Integer roleProviderId, Integer roleId, String roleName)
	{
		if(!hasRole(roleProviderId, roleName))
		{
			deleteRole(currentProviderId, roleProviderId, roleId);
			addRole(roleProviderId, roleName);
		}
	}

	public void deleteRole(Integer currentProviderId, Integer roleProviderId, Integer roleId)
	{
		Secuserrole secUserRole = secUserRoleDao.findById(roleId);
		String oldRole = secUserRole.getRoleName();
		secUserRoleDao.delete(secUserRole);

		RecycleBin recycleBin = new RecycleBin();
		recycleBin.setProviderNo(String.valueOf(currentProviderId));
		recycleBin.setUpdateDateTime(new java.util.Date());
		recycleBin.setTableName("secUserRole");
		recycleBin.setKeyword(roleProviderId + "|" + oldRole);
		recycleBin.setTableContent("<provider_no>" + roleProviderId + "</provider_no>" + "<role_name>" + oldRole + "</role_name>");
		recycleBinDao.persist(recycleBin);

	}

	public boolean validRoleName(String roleName)
	{
		return secRoleDao.roleExistsWithName(roleName);
	}

	/**
	 * assign the specified roles to the provider
	 * @param roles - role id list
	 * @param providerNo - the provider to assign to.
	 */
	public synchronized void assignProviderRoles(List<Integer> roles, Integer providerNo)
	{
		if (roles != null)
		{
			for (Integer role : roles)
			{
				String roleName = secRoleDao.find(role).getName();
				if (!hasRole(providerNo, roleName))
				{
					addRole(providerNo, roleName);
				}
			}
		}
	}

	/**
	 * remove roles from a provider that are not contained in the role list.
	 * @param roles - the list of roles to keep
	 * @param providerNo - the provider to apply the operation on
	 */
	public synchronized void removeOtherProviderRoles(List<Integer> roles, Integer providerNo)
	{
		if (roles != null)
		{
			List<Secuserrole> secUserRoles = secUserRoleDao.findByProviderNo(providerNo.toString());
			for (Secuserrole userRole : secUserRoles)
			{
				boolean contains = false;
				for (Integer roleId : roles)
				{
					String roleName = secRoleDao.find(roleId).getName();
					if (roleName.equals(userRole.getRoleName()))
					{
						contains = true;
						break;
					}
				}

				if (!contains)
				{
					secUserRoleDao.delete(userRole);
				}
			}
		}
	}


}
