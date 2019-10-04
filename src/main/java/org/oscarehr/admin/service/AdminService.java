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

package org.oscarehr.admin.service;

import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.ws.rest.to.model.AdminNavGroupTo1;
import org.oscarehr.ws.rest.to.model.AdminNavItemTo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Service
public class AdminService
{
	@Autowired
	SecurityInfoManager securityInfoManager;

	/**
	 * construct a list of admin nav group transfer objects. This group is passed to the adminController.js to build the ui.
	 * @param resourceBundle - a resource bundle from which to pull message strings
	 * @param providerNo - the provider viewing this resources
	 * @return - a list of admin nav group objects
	 */
	public List<AdminNavGroupTo1> getAdminNavGroups(ResourceBundle resourceBundle, String providerNo)
	{
		List<AdminNavGroupTo1> adminNavList = new ArrayList<>();

		if (securityInfoManager.hasOnePrivileges(providerNo, SecurityInfoManager.READ, null, "_admin", "_admin.userAdmin" , "_admin.provider" ))
		{
			adminNavList.add(getAdminNavUserManagementGroup(resourceBundle));
		}

		return adminNavList;
	}

	/**
	 * get the user management admin nav group
	 * @param resourceBundle - a resource bundle from which to pull message strings
	 * @return - the adminNavGroup for the user management list.
	 */
	private AdminNavGroupTo1 getAdminNavUserManagementGroup(ResourceBundle resourceBundle)
	{
		AdminNavGroupTo1 userManagement = new AdminNavGroupTo1();
		List<AdminNavItemTo1> userManagementItems = new ArrayList<>();

		userManagement.setName(resourceBundle.getString("admin.admin.UserManagement"));

		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddProvider"), "frame?frameUrl=/admin/provideraddarecordhtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSearchProvider"), "frame?frameUrl=/admin/providersearchrecordshtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnAddLogin"), "frame?frameUrl=/admin/securityaddarecord.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.btnSearchLogin"), "frame?frameUrl=/admin/providersearchrecordshtm.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.assignRole"), "frame?frameUrl=/admin/providerRole.jsp"));
		userManagementItems.add(new AdminNavItemTo1(resourceBundle.getString("admin.admin.unlockAcct"), "frame?frameUrl=/admin/unLock.jsp"));

		userManagement.setItems(userManagementItems);
		return userManagement;
	}
}
