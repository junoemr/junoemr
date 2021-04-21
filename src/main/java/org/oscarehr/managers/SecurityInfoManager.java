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
import org.oscarehr.common.exception.PatientDirectiveException;
import org.oscarehr.provider.dao.ProviderDataDao;
import org.oscarehr.provider.model.ProviderData;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.util.OscarRoleObjectPrivilege;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Service
public class SecurityInfoManager {
	public static final String READ = "r";
	public static final String WRITE = "w";
	public static final String UPDATE = "u";
	public static final String DELETE = "d";
	public static final String NORIGHTS = "o";
	
	public enum PRIVILEGE_LEVEL
	{
		READ ("r"),
		WRITE ("w"),
		UPDATE ("u"),
		DELETE ("d"),
		NO_RIGHTS ("o");

		String level;

		PRIVILEGE_LEVEL(String level)
		{
			this.level = level;
		}

		public String asString()
		{
			return this.level;
		}
	}


	@Autowired
	private SecuserroleDao secUserRoleDao;
	
	@Autowired
	private SecobjprivilegeDao secobjprivilegeDao;

	@Autowired
	private ProviderDataDao providerDataDao;

	public List<Secuserrole> getRoles(String providerNo)
	{
		@SuppressWarnings("unchecked")
		List<Secuserrole> results = secUserRoleDao.findByProviderNo(providerNo);
		return results;
	}

	public List<Secobjprivilege> getSecurityObjects(LoggedInInfo loggedInInfo)
	{
		return getSecurityObjects(loggedInInfo.getLoggedInProviderNo());
	}
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
	public boolean hasPrivilege(LoggedInInfo loggedInInfo, String objectName, String privilege, String demographicNo)
	{
		return hasPrivilege(loggedInInfo.getLoggedInProviderNo(), objectName, privilege, demographicNo);
	}

	public boolean hasPrivilege(LoggedInInfo loggedInInfo, String objectName, String privilege, int demographicNo)
	{
		return hasPrivilege(loggedInInfo, objectName, privilege, String.valueOf(demographicNo));
	}

	/**
	 * check if the user has all of the requested privileges
	 * @param providerNo - provider to check
	 * @param privilege - privilege to check
	 * @param demographicNo - demographic on which the check should be preformed (can be null)
	 * @param hasObjList - a list of security objects to check
	 * @return - true or false indicating pass or fail of the privilege check.
	 */
	public boolean hasPrivileges(String providerNo, String privilege, Integer demographicNo, String... hasObjList)
	{
		for(String objectName:hasObjList)
		{
			if(!hasPrivilege(providerNo, objectName, privilege, (demographicNo != null ? String.valueOf(demographicNo):null)))
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * check if the user has any one of the requested privileges
	 * @param providerNo - provider to check
	 * @param privilege - privilege to check
	 * @param demographicNo - demographic on which the check should be preformed (can be null)
	 * @param hasObjList - a list of security objects to check
	 * @return - true or false indicating pass or fail of the privilege check.
	 */
	public boolean hasOnePrivileges(String providerNo, String privilege, Integer demographicNo, String... hasObjList)
	{
		for(String objectName:hasObjList)
		{
			if(hasPrivilege(providerNo, objectName, privilege, (demographicNo != null ? String.valueOf(demographicNo):null)))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasPrivilege(String providerNo, String objectName, String privilege, String demographicNo)
	{
		try
		{
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
			
			if (!noMatchingRoleToSpecificPatient && OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), NORIGHTS)) {
					throw new PatientDirectiveException("Patient has requested user not access record");
			} else  if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), "x")) {
				return true;
			}
			else if (OscarRoleObjectPrivilege.checkPrivilege(roleNames, (Properties)v.get(0), (List<String>)v.get(1), (List<String>)v.get(2), WRITE)) {
				return ((READ+UPDATE+WRITE).contains(privilege));
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
	public boolean isAllowedAccessToPatientRecord(LoggedInInfo loggedInInfo, Integer demographicNo) {
		return isAllowedAccessToPatientRecord(loggedInInfo.getLoggedInProviderNo(), demographicNo);
	}

	public boolean isAllowedAccessToPatientRecord(String providerNo, Integer demographicNo) {
		
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

	public void requireAllPrivilege(String providerNo, String privilege, Integer demographicNo, String... requiredObjList)
	{
		for(String objectName:requiredObjList)
		{
			if(!hasPrivilege(providerNo, objectName, privilege, (demographicNo != null ? String.valueOf(demographicNo):null)))
			{
				throw new SecurityException("missing required privilege: " + privilege + " for security object (" + objectName + ")");
			}
		}
	}

	public void requireOnePrivilege(String providerNo, String privilege, Integer demographicNo, String... requiredObjList)
	{
		for(String objectName:requiredObjList)
		{
			if(hasPrivilege(providerNo, objectName, privilege, (demographicNo != null ? String.valueOf(demographicNo):null)))
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
