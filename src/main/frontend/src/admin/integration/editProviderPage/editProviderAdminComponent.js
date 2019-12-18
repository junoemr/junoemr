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
import {EDIT_PROVIDER_MODE} from "./editProviderAdminConstants";


angular.module('Admin.Integration').component('editProviderAdmin',
{
	templateUrl: 'src/admin/integration/editProviderPage/editProviderAdmin.jsp',
	bindings: {},
	controller: ['$scope', '$stateParams', 'staticDataService', 'providersService', function ($scope, $stateParams, staticDataService, providersService)
	{
		let ctrl = this;

		ctrl.modes = EDIT_PROVIDER_MODE;
		ctrl.mode = $stateParams.mode;

		ctrl.sexes = staticDataService.getGenders();
		ctrl.providerTypes = staticDataService.getProviderTypes();

		ctrl.roleOptions = ['fizbang', 'foobar'];
		ctrl.currentRoleSelection = null;

		ctrl.provider = {
			// User Info
			firstName: null,
			lastName: null,
			type: 'doctor',
			speciality: null,
			team: null,
			sex: null,
			dateOfBirth: null,

			// Login Info
			email: null,
			userName: null,
			password: null,
			passwordVerify: null,
			secondLevelPasscode: null,
			secondLevelPasscodeVerify: null,

			// Access Roles
			userRoles: [],
		};

		ctrl.$onInit = function()
		{
			providersService.getAllProviderRoles().then(
					function success(result)
					{
						ctrl.roleOptions = [];
						for (let role of result)
						{
							ctrl.roleOptions.push({
								label: role.roleName,
								value: role.roleId,
							});
						}
					},
					function error(result)
					{
						console.error("Failed to fetch provider roles with error: " + error);
					}
			);

		};


		ctrl.addUserRole = function(roleId)
		{
			if (roleId && !ctrl.provider.userRoles.includes(roleId))
			{
				ctrl.provider.userRoles.push(roleId);
			}
		};

		ctrl.removeUserRole = function(roleId)
		{
			if (roleId)
			{
				let idx = ctrl.provider.userRoles.findIndex(el => el === roleId);
				ctrl.provider.userRoles.splice(idx, 1);
			}
		};

		ctrl.getUserRoleName = function(roleId)
		{
			return ctrl.roleOptions.find(el => el.value === roleId).label;
		}
	}]
});