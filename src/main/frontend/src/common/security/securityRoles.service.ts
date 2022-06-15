'use strict';

/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
    This software is published under the GPL GNU General Public License.
    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public License
    as published by the Free Software Foundation; either version 2
    of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */

import {UserSecurityRolesTransfer} from "../../../generated";
import {SecurityPermissions} from "./securityConstants";

angular.module("Common.Security").service("securityRolesService", [
	'securityApiService',
	function(securityApiService)
	{
		const service = this;
		service.rolesData = null as UserSecurityRolesTransfer;
		service.isLoaded = false;

		service.loadUserRoles = async (): Promise<void> =>
		{
			service.isLoaded = false;
			service.rolesData = await securityApiService.getCurrentUserSecurityRoles();
			service.isLoaded = true;
		}

		service.isReady = () =>
		{
			return service.isLoaded;
		}

		/**
		 * check the current users permissions, return true if all requirements are met
		 * @param requiredPermissions - the permissions required
		 */
		service.hasSecurityPrivileges = (...requiredPermissions: SecurityPermissions[]): boolean =>
		{
			if (service.rolesData
				&& service.rolesData.securityPermissions
				&& requiredPermissions
				&& requiredPermissions.length > 0)
			{
				const permissions = service.rolesData.securityPermissions.map((value) => value.permission);
				for (let i = 0; i < requiredPermissions.length; i++)
				{
					if(!permissions.includes(requiredPermissions[i]))
					{
						return false;
					}
				}
				return true;
			}
			return false;
		}
	}
]);