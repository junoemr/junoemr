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

import {SecurityObjectsTransfer, SecurityRolesApi, UserSecurityRolesTransfer} from "../../../generated";
import PrivilegesEnum = UserSecurityRolesTransfer.PrivilegesEnum;
import AccessObjectsEnum = SecurityObjectsTransfer.AccessObjectsEnum;

angular.module("Common.Store").service("securityRolesStore", [
	'$http',
	'$httpParamSerializer',
	function(
		$http,
		$httpParamSerializer)
	{
		const service = this;
		service.securtyRolesApi = new SecurityRolesApi($http, $httpParamSerializer, '../ws/rs');

		service.rolesData = null as UserSecurityRolesTransfer;

		service.loadUserRoles = async (): Promise<void> =>
		{
			service.rolesData = (await service.securtyRolesApi.getCurrentUserSecurityRoles()).data.body;
		}

		/**
		 * make the security check available to all pages
		 * @param access - the access required
		 * @param requiredPrivileges - the privilege levels required
		 */
		service.hasSecurityPrivileges = (access: AccessObjectsEnum, ...requiredPrivileges: PrivilegesEnum[]): boolean =>
		{
			if (service.rolesData)
			{
				const userPrivileges = service.rolesData.privileges[access];
				if (userPrivileges)
				{
					for (let i = 0; i < requiredPrivileges.length; i++)
					{
						if (!userPrivileges.includes(requiredPrivileges[i]))
						{
							return false;
						}
					}
					return true;
				}
			}
			return false;
		}
	}
]);
