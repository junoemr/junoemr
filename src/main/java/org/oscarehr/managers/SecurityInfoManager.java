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
package org.oscarehr.managers;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.dao.DemographicSetsDao;
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.security.dao.SecRoleDao;
import org.oscarehr.security.dao.SecUserRoleDao;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjectName;
import org.oscarehr.security.model.SecRole;
import org.oscarehr.security.model.SecUserRole;
import org.oscarehr.security.service.SecurityRolesService;
import org.oscarehr.security.service.SecuritySetsService;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import oscar.util.OscarRoleObjectPrivilege;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class SecurityInfoManager
{
	// avoid use of these, use enum instead
	public static final String READ = "r";
	public static final String CREATE = "w";
	public static final String UPDATE = "u";
	public static final String DELETE = "d";
	public static final String NO_RIGHTS = "o";
	public static final String ALL = "x";

	public enum PRIVILEGE_LEVEL
	{
		READ("r"),
		UPDATE("u"),
		CREATE("w"),
		DELETE("d");

		String level;

		PRIVILEGE_LEVEL(String level)
		{
			this.level = level;
		}

		public String asString()
		{
			return this.level;
		}

		public static PRIVILEGE_LEVEL fromStringIgnoreCase(String enumString)
		{
			if(EnumUtils.isValidEnumIgnoreCase(PRIVILEGE_LEVEL.class, enumString))
			{
				return PRIVILEGE_LEVEL.valueOf(enumString.toUpperCase());
			}
			return null;
		}

		public static PRIVILEGE_LEVEL fromValueString(String value)
		{
			for(PRIVILEGE_LEVEL level : PRIVILEGE_LEVEL.values())
			{
				if(level.asString().equalsIgnoreCase(value))
				{
					return level;
				}
			}
			return null;
		}
	}

	@Autowired
	private SecRoleDao secRoleDao;

	@Autowired
	private SecUserRoleDao secUserRoleDao;

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private DemographicSetsDao demographicSetsDao;

	@Autowired
	private SecurityRolesService securityRolesService;

	@Autowired
	private SecuritySetsService securitySetsService;

	/**
	 * Checks to see if this provider has the privilege to the security object being requested.
	 * 
	 * The way it's coded now
	 * 
	 * get all the roles associated with the logged in provider, including the roleName=providerNo.
	 * find the privileges using the roles list.
	 * 
	 * Loop through all the rights, if we find one that can evaluate to true , we exit..else we keep checking
	 * 
	 * if r then an entry with r | u |w | x  is required
	 * if u then an entry with u | w | x is required
	 * if w then an entry with w | x is required
	 * if d then an entry with d | x is required
	 * 
	 * Privileges priority is taken care of by OscarRoleObjectPrivilege.checkPrivilege()
	 *
	 * If patient-specific privileges are present, it takes priority over the general privileges.
	 * For checking non-patient-specific object privileges, call with demographicNo==null.
	 * 
	 * @param loggedInInfo
	 * @param objectName
	 * @param privilege
	 * @param demographicNo
	 * @return boolean
	 */
	@Deprecated // use enum version instead
	public boolean hasPrivilege(LoggedInInfo loggedInInfo, String objectName, String privilege, String demographicNo)
	{
		return hasPrivilege(loggedInInfo.getLoggedInProviderNo(), objectName, privilege, (demographicNo != null ? Integer.parseInt(demographicNo) : null));
	}

	@Deprecated
	public boolean hasPrivilege(String providerNo, PRIVILEGE_LEVEL privilege, Integer demographicNo, SecObjectName.OBJECT_NAME objectName)
	{
		return hasPrivilege(providerNo, objectName.getValue(), privilege.asString(), demographicNo);
	}

	@Deprecated
	public boolean hasPrivilege(String providerNo, PRIVILEGE_LEVEL privilege, SecObjectName.OBJECT_NAME objectName)
	{
		return hasPrivilege(providerNo, privilege, null, objectName);
	}

	public boolean hasPrivileges(String providerNo, Permission... permissions)
	{
		return hasPrivileges(providerNo, null, permissions);
	}

	public boolean hasPrivileges(String providerNo, Integer demographicId, Permission... permissions)
	{
		return (demographicId == null || isAllowedAccessToPatientRecord(providerNo, demographicId)) && hasAllPermissions(providerNo, permissions);
	}

	/**
	 * check if the user has any one of the requested privileges
	 * @param providerNo - provider to check
	 * @param privilege - privilege to check
	 * @param demographicNo - demographic on which the check should be preformed (can be null)
	 * @param hasObjList - a list of security objects to check
	 * @return - true or false indicating pass or fail of the privilege check.
	 * @deprecated use Permission enum version
	 */
	@Deprecated
	public boolean hasOnePrivileges(String providerNo, PRIVILEGE_LEVEL privilege, Integer demographicNo, SecObjectName.OBJECT_NAME... hasObjList)
	{
		for(SecObjectName.OBJECT_NAME objectName : hasObjList)
		{
			if(hasPrivilege(providerNo, privilege, demographicNo, objectName))
			{
				return true;
			}
		}
		return false;
	}

	@Deprecated //deprecated - use Permission enum version
	public boolean hasOnePrivileges(String providerNo, PRIVILEGE_LEVEL privilege, SecObjectName.OBJECT_NAME... hasObjList)
	{
		return hasOnePrivileges(providerNo, privilege, null, hasObjList);
	}

	private boolean hasAllPermissions(String providerNo, Permission... permissions)
	{
		List<Permission> userPermissions = securityRolesService.getSecurityPermissionsForUser(providerNo);
		for(Permission permission : permissions)
		{
			if(!userPermissions.contains(permission))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * used by classic security tag system. do not use for new code
	 * @param roleNames role names separated by ,
	 * @param objectName the object name
	 * @param rights the required rights
	 * @return the role has the required rights
	 */
	@Deprecated
	public boolean hasPrivilege(String roleNames, String objectName, String rights)
	{
		Set<String> roleNameSet = new HashSet<>();

		String[] roleNameArr = roleNames.split(",");
		for(String roleName : roleNameArr)
		{
			SecRole role = secRoleDao.findByRoleName(roleName);
			if(role == null)
			{
				// unknown role name, might be the provider ID
				roleNameSet.add(roleName);
			}
			else
			{
				// with role inheritance, checking a role also requires us to check parent roles
				// this is technically more permissive than it should be,
				// as disabled permissions within the child roles will pass due to checking the parent.
				do
				{
					roleNameSet.add(role.getName());
					role = role.getParentSecRole();
				}
				while(role != null);
			}
		}

		String allRoleNames = StringUtils.join(roleNameSet, ",");
		List privilegeProps = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
		return OscarRoleObjectPrivilege.checkPrivilege(allRoleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), rights);
	}

	@Deprecated //deprecated - use Permission enum version
	private boolean hasPrivilege(String providerNo, String objectName, String privilege, Integer demographicNo)
	{
		try
		{
			// if the requested legacy permission is also used in the new system, use the new check instead
			SecObjectName.OBJECT_NAME objectNameEnum = SecObjectName.OBJECT_NAME.fromValueString(objectName);
			if(Permission.includesObjectAsValue(objectNameEnum))
			{
				SecurityInfoManager.PRIVILEGE_LEVEL privilegeLevel = SecurityInfoManager.PRIVILEGE_LEVEL.fromValueString(privilege);
				Permission permission = Permission.from(objectNameEnum, privilegeLevel);
				return hasPrivileges(providerNo, demographicNo, permission);
			}

			if(demographicNo != null && !isAllowedAccessToPatientRecord(providerNo, demographicNo))
			{
				return false;
			}

			List<String> roleNameLs = new ArrayList<>();
			for(SecUserRole role : secUserRoleDao.getUserRoles(providerNo))
			{
				roleNameLs.add(role.getRoleName());
			}
			roleNameLs.add(providerNo);
			String roleNames = StringUtils.join(roleNameLs, ",");

			List privilegeProps = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
			if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), "x")) {
				return true;
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), CREATE)) {
				return ((READ+UPDATE+ CREATE).contains(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), UPDATE)) {
				return ((READ+UPDATE).contains(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), READ)) {
				return (READ.equals(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)privilegeProps.get(0), (List<String>)privilegeProps.get(1), (List<String>)privilegeProps.get(2), DELETE)) {
				return (DELETE.equals(privilege));
			}
	
		} catch (PatientDirectiveException ex) {
			throw(ex);
		} catch (Exception ex) {
			MiscUtils.getLogger().error("Error checking privileges", ex);
		}
		
		return false;
	}

	public boolean isAllowedAccessToPatientRecord(String providerNo, Integer demographicNo)
	{
		List<String> blacklist = securitySetsService.getSecurityDemographicSetNamesBlacklist(providerNo);
		List<String> setsWithPatient = demographicSetsDao.findSetNamesByDemographicNo(demographicNo);

		for(String blacklistedSet : blacklist)
		{
			if(setsWithPatient.contains(blacklistedSet))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * require that the given provider has all of the required permissions, throw exception if requirements are not met
	 * @param providerNo - the provider ID
	 * @param permissions - the required security objects for access to the emr module
	 * @throws SecurityException - if the requirements are not me by the provider record
	 */
	public void requireAllPrivilege(String providerNo, Permission... permissions)
	{
		requireAllPrivilege(providerNo, null, permissions);
	}

	/**
	 * require that the given provider has all of the required permissions, throw exception if requirements are not met.
	 * also checks access to a specific demographic chart
	 * @param providerNo - the provider ID
	 * @param demographicId - the demographic to check access
	 * @param permissions - the required security objects for access to the emr module
	 * @throws SecurityException - if the requirements are not me by the provider record
	 */
	public void requireAllPrivilege(String providerNo, Integer demographicId, Permission... permissions)
	{
		if(permissions == null)
		{
			return;
		}
		if(demographicId != null && !isAllowedAccessToPatientRecord(providerNo, demographicId))
		{
			throw new SecurityException("user des not have access to patient #" + demographicId);
		}

		List<Permission> userPermissions = securityRolesService.getSecurityPermissionsForUser(providerNo);
		for(Permission permission : permissions)
		{
			if(!userPermissions.contains(permission))
			{
				throw new SecurityException("missing required permissions: " + permission.name());
			}
		}
	}

	/**
	 * Check that the given provider has at least one of the required security access rights
	 * @param providerNo - the provider ID
	 * @param privilege - the privilege level required
	 * @param demographicNo - an optional demographic number ( for blocking individual patient records where appropriate)
	 * @param requiredObjList - the required security objects for access to the emr module
	 * @throws SecurityException - if the requirements are not me by the provider record
	 * @deprecated - use Permission enum version of requireAllPrivilege. no access should use an OR on securityObjects
	 */
	@Deprecated
	public void requireOnePrivilege(String providerNo, String privilege, Integer demographicNo, String... requiredObjList)
	{
		for(String objectName:requiredObjList)
		{
			if(hasPrivilege(providerNo, objectName, privilege, demographicNo))
			{
				return;
			}
		}
		throw new SecurityException("missing one or more required privileges: " + privilege + " for security objects (" + String.join(",", requiredObjList) + ")");
	}

	public void requireSuperAdminFlag(String providerNo)
	{
		ProviderData provider = providerDataDao.find(providerNo);
		if(!provider.isSuperAdmin())
		{
			throw new SecurityException("Super Admin privileges are required");
		}
	}

	/**
	 * check if the provider is a super admin
	 * @param providerNo -
	 * @return true if super admin. false otherwise
	 */
	public boolean isSuperAdmin(String providerNo)
	{
		ProviderData provider = providerDataDao.find(providerNo);
		return provider.isSuperAdmin();
	}

	/**
	 * Ensure that provider has sufficient privilege to modify the given provider.
	 * Only throws an exception when current user attempting to do stuff needs super admin but doesn't have it
	 * @param currentProviderNo logged in provider
	 * @param providerNoToModify provider that will be changed
	 */
	public void requireUserCanModify(String currentProviderNo, String providerNoToModify) throws SecurityException
	{
		if (!userCanModify(currentProviderNo, providerNoToModify))
		{
			throw new SecurityException("Super Admin privileges are required");
		}
	}

	/**
	 * Check if current user is allowed to modify the given provider number.
	 * Currently, this checks to see if a super admin record is being edited by a non super admin.
	 * @param currentProviderNo - the providerId of the current user
	 * @param providerNoToModify - the providerId of the user they are attempting to modify
	 * @return  true if current user is able to edit the other provider's record
	 */
	public boolean userCanModify(String currentProviderNo, String providerNoToModify)
	{
		ProviderData providerToModify = providerDataDao.find(providerNoToModify);
		ProviderData currentProvider = providerDataDao.find(currentProviderNo);

		return currentProvider.isSuperAdmin() || !providerToModify.isSuperAdmin();
	}
}
