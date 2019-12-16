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
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.common.dao.RecycleBinDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.model.RecycleBin;
import org.oscarehr.common.model.SecRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oscar.OscarProperties;

import java.util.List;

@Service
@Transactional
public class ProviderRoleService
{

	@Autowired
	SecRoleDao securityRoleDao;

	@Autowired
	ProgramProviderDAO programProviderDao;

	@Autowired
	ProgramManager programManager;

	@Autowired
	SecuserroleDao secUserRoleDao;

	@Autowired
	SecRoleDao secRoleDao;

	@Autowired
	RecycleBinDao recycleBinDao;


	/**
	 * @param providerID - of the newly added provider's
	 */

	public boolean setDefaultRoleForNewProvider(Integer providerID)
	{

		String providerDefaultRoleName = OscarProperties.getInstance().getProperty("default_provider_role_name");

		boolean isDefaultRoleNameExist = setPrimaryRole(providerID, providerDefaultRoleName);

		if(!isDefaultRoleNameExist)
		{
			return false;
		}

		addRole(providerID, providerDefaultRoleName);

		return true;
	}

	/**
	 * Assign a primary role to the provider
	 * @param providerId - provider record id
	 * @param roleName - name of the role to assign
	 * @return - if no role in the table match property file's default role, return false;
	 */
	public boolean setPrimaryRole(Integer providerId, String roleName)
	{
		SecRole secRole = securityRoleDao.findByName(roleName);

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
			programProviderDao.saveProgramProvider(programProvider);
		}
		else
		{
			programProvider = new ProgramProvider();
			programProvider.setProgramId(caisiProgram);
			programProvider.setProviderNo(String.valueOf(providerId));
			programProvider.setRoleId(roleId);
			programProviderDao.saveProgramProvider(programProvider);
		}

		return true;
	}


	public Secuserrole addRole(Integer roleProviderId, String roleName)
	{
		Secuserrole secUserRole = new Secuserrole();
		int defaultActiveyn = 1;

		secUserRole.setProviderNo(String.valueOf(roleProviderId));
		secUserRole.setRoleName(roleName);
		secUserRole.setActiveyn(defaultActiveyn);
		secUserRoleDao.save(secUserRole);
		return secUserRole;
	}


	public Secuserrole addRoleAndAssignPrimary(Integer roleProviderId, String roleName)
	{


		Secuserrole secUserRole = addRole(roleProviderId, roleName);

		Long caisiProgram = new Long(programManager.getDefaultProgramId());
		ProgramProvider programProvider = programProviderDao.getProgramProvider(String.valueOf(roleProviderId), caisiProgram);
		if(programProvider == null)
		{
			programProvider = new ProgramProvider();
		}
		programProvider.setProgramId(caisiProgram);
		programProvider.setProviderNo(String.valueOf(roleProviderId));
		programProvider.setRoleId(Long.valueOf(secRoleDao.findByName(roleName).getId()));
		programProviderDao.saveProgramProvider(programProvider);
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
		return (secRoleDao.findByName(roleName) != null);
	}


}
