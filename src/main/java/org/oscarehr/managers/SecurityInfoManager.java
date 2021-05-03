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

import com.quatro.dao.security.SecobjprivilegeDao;
import com.quatro.dao.security.SecuserroleDao;
import com.quatro.model.security.Secobjprivilege;
import com.quatro.model.security.Secuserrole;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.EnumUtils;
import org.oscarehr.common.dao.DemographicSetsDao;
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.security.model.Permission;
import org.oscarehr.security.model.SecObjectName;
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
import java.util.List;
import java.util.Properties;
import java.util.Vector;

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
	private SecuserroleDao secuserroleDao;
	
	@Autowired
	private SecobjprivilegeDao secobjprivilegeDao;

	@Autowired
	private ProviderDataDao providerDataDao;

	@Autowired
	private DemographicSetsDao demographicSetsDao;

	@Autowired
	private SecurityRolesService securityRolesService;

	@Autowired
	private SecuritySetsService securitySetsService;

	@Deprecated // use roles service
	public List<Secuserrole> getRoles(String providerNo)
	{
		@SuppressWarnings("unchecked")
		List<Secuserrole> results = secuserroleDao.findByProviderNo(providerNo);
		return results;
	}

	@Deprecated // use roles service
	public List<Secobjprivilege> getSecurityObjects(LoggedInInfo loggedInInfo)
	{
		return getSecurityObjects(loggedInInfo.getLoggedInProviderNo());
	}
	@Deprecated // use roles service
	public List<Secobjprivilege> getSecurityObjects(String providerNo)
	{
		List<String> roleNames = new ArrayList<>();
		for(Secuserrole role : getRoles(providerNo))
		{
			roleNames.add(role.getRoleName());
		}
		roleNames.add(providerNo);

		List<Secobjprivilege> results = secobjprivilegeDao.getByRoles(roleNames);

		return results;
	}

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

	/**
	 * check if the user has all of the requested privileges
	 * @param providerNo - provider to check
	 * @param privilege - privilege to check
	 * @param demographicNo - demographic on which the check should be preformed (can be null)
	 * @param hasObjList - a list of security objects to check
	 * @return - true or false indicating pass or fail of the privilege check.
	 */
	@Deprecated
	public boolean hasPrivileges(String providerNo, PRIVILEGE_LEVEL privilege, Integer demographicNo, SecObjectName.OBJECT_NAME... hasObjList)
	{
		for(SecObjectName.OBJECT_NAME objectName : hasObjList)
		{
			if(!hasPrivilege(providerNo, privilege, demographicNo, objectName))
			{
				return false;
			}
		}
		return true;
	}

	public boolean hasPrivileges(String providerNo, Permission... permissions)
	{
		return hasPrivileges(providerNo, null, permissions);
	}

	public boolean hasPrivileges(String providerNo, Integer demographicId, Permission... permissions)
	{
		return (demographicId == null || isAllowedAccessToPatientRecord(providerNo, demographicId)) && hasRoleAccess(providerNo, permissions);
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

	private boolean hasRoleAccess(String providerNo, Permission... permissions)
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

	@Deprecated //deprecated - use Permission enum version
	private boolean hasPrivilege(String providerNo, String objectName, String privilege, Integer demographicNo)
	{
		try
		{
			if(demographicNo != null && !isAllowedAccessToPatientRecord(providerNo, demographicNo))
			{
				return false;
			}

			//TODO remove old logic when possible
			List<String> roleNameLs = new ArrayList<>();
			for(Secuserrole role : getRoles(providerNo))
			{
				roleNameLs.add(role.getRoleName());
			}
			roleNameLs.add(providerNo);
			String roleNames = StringUtils.join(roleNameLs, ",");
			
			boolean noMatchingRoleToSpecificPatient = true;
			List v = null;
			if (demographicNo!=null) {
				v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName+"$"+demographicNo);
				List<String> roleInObj = (List<String>)v.get(1);
				
				for (String objRole : roleInObj) {
					if (roleNames.toLowerCase().contains(objRole.toLowerCase().trim())) {
						noMatchingRoleToSpecificPatient = false;
						break;
					}
				}
			}
			if (noMatchingRoleToSpecificPatient) v = OscarRoleObjectPrivilege.getPrivilegeProp(objectName);
			
			if (!noMatchingRoleToSpecificPatient && OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), NO_RIGHTS))
			{
					throw new PatientDirectiveException("Patient has requested user not access record");
			} else  if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), "x")) {
				return true;
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), CREATE)) {
				return ((READ+UPDATE+ CREATE).contains(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), UPDATE)) {
				return ((READ+UPDATE).contains(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), READ)) {
				return (READ.equals(privilege));
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), DELETE)) {
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
		List<String> blacklist = securitySetsService.getSecurityDemographicSetNames(providerNo);
		List<String> setsWithPatient = demographicSetsDao.findSetNamesByDemographicNo(demographicNo);

		for(String blacklistedSet : blacklist)
		{
			if(setsWithPatient.contains(blacklistedSet))
			{
				return false;
			}
		}

		//TODO remove this old system when we can
		List<String> roleNameLs = new ArrayList<>();
		for(Secuserrole role:getRoles(providerNo)) {
			roleNameLs.add(role.getRoleName());
		}
		roleNameLs.add(providerNo);
		String roleNames = StringUtils.join(roleNameLs, ",");
		
		
		Vector v = OscarRoleObjectPrivilege.getPrivilegeProp("_demographic$"+demographicNo);
		if(OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), "o")) {
			return false;
		}
		
		v = OscarRoleObjectPrivilege.getPrivilegeProp("_eChart$"+demographicNo);
		if(OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), "o")) {
			return false;
		}
		
		return true;
	}

	/**
	 * Check that the given provider has all of the required security access rights
	 * @param providerNo - the provider ID
	 * @param privilege - the privilege level required
	 * @param demographicNo - an optional demographic number ( for blocking individual patient records where appropriate)
	 * @param requiredObjList - the required security objects for access to the emr module
	 * @throws SecurityException - if the requirements are not me by the provider record
	 * @deprecated - use Permission enum version
	 */
	@Deprecated
	public void requireAllPrivilege(String providerNo, PRIVILEGE_LEVEL privilege, Integer demographicNo, SecObjectName.OBJECT_NAME... requiredObjList)
	{
		if(requiredObjList == null)
		{
			return;
		}
		for(SecObjectName.OBJECT_NAME objectName : requiredObjList)
		{
			if(!hasPrivilege(providerNo, objectName.getValue(), privilege.asString(), demographicNo))
			{
				throw new SecurityException("missing required privilege: " + privilege + " for security object (" + objectName.getValue() + ")");
			}
		}
	}

	public void requireAllPrivilege(String providerNo, Permission... permissions)
	{
		requireAllPrivilege(providerNo, null, permissions);
	}
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
	 * Check that the given provider has all of the required security access rights
	 * @param providerNo - the provider ID
	 * @param privilege - the privilege level required
	 * @param requiredObjList - the required security objects for access to the emr module
	 * @throws SecurityException - if the requirements are not me by the provider record
	 * @deprecated - use Permission enum version
	 */
	@Deprecated
	public void requireAllPrivilege(String providerNo, PRIVILEGE_LEVEL privilege, SecObjectName.OBJECT_NAME... requiredObjList)
	{
		requireAllPrivilege(providerNo, privilege, null, requiredObjList);
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
	 *  Action that requires logined provider/user has superadmin provilege
	 * @param currentProviderNo logged in provider
	 * @param providerNoToModify provider that will be changed
	 */
	public void requireSuperAdminPrivilege(String currentProviderNo, String providerNoToModify) throws SecurityException
	{
		if (!superAdminModificationCheck(currentProviderNo,providerNoToModify))
		{
			throw new SecurityException("Super Admin privileges are required");
		}
	}

	/**
	 * check if it's a non super-admin provider attempts to modify a super-admin provider
	 * @param currentProviderNo - the providerId of the current user
	 * @param providerNoToModify - the providerId of the user they are attempting to modify
	 * @return  true if current user has priviledge to set a provider
	 */
	public boolean superAdminModificationCheck(String currentProviderNo, String providerNoToModify)
	{
		ProviderData providerToModify = providerDataDao.find(providerNoToModify);
		ProviderData currentProvider = providerDataDao.find(currentProviderNo);

		return currentProvider.isSuperAdmin() || !providerToModify.isSuperAdmin();
	}
}
