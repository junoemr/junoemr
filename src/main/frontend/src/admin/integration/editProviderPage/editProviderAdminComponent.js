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
import {SystemPreferenceApi} from "../../../../generated/api/SystemPreferenceApi";


angular.module('Admin.Integration').component('editProviderAdmin',
{
	templateUrl: 'src/admin/integration/editProviderPage/editProviderAdmin.jsp',
	bindings: {},
	controller: [
		'$scope',
		'$stateParams',
		'$http',
		'$httpParamSerializer',
		'staticDataService',
		'providersService',
		'providerService',
		function (
				$scope,
				$stateParams,
				$http,
				$httpParamSerializer,
				staticDataService,
				providersService,
				providerService)
	{
		let ctrl = this;

		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
				'../ws/rs');

		ctrl.modes = EDIT_PROVIDER_MODE;
		ctrl.mode = $stateParams.mode;

		ctrl.sexes = staticDataService.getGenders();
		ctrl.providerTypes = staticDataService.getProviderTypes();

		ctrl.roleOptions = [];
		ctrl.currentRoleSelection = null;

		// billingRegion. determines what controls display
		ctrl.billingRegionSelectEnabled = false;
		ctrl.billingRegion = null;
		ctrl.billingRegionOptions = staticDataService.getBillingRegions();

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

			// Contact Information
			address: null,
			homePhone: null,
			workPhone: null,
			cellPhone: null,
			otherPhone: null,
			fax: null,
			contactEmail: null,
			pager: null,

			// Access Roles
			userRoles: [],

			// BC Billing
			billingNo: null,
			ruralRetentionCode: null,
			serviceLocation: null,

			// ON Billing
			groupNumber: null,
			specialityCode: null,
			visitLocation: null,
			serviceLocationIndicator: null,

			// AB Billing
			clinic: null,
			sourceCode: "ab",
			skillCode: null,
			locationCode: null,
			BANumber: null,
			FacilityNumber: null,
			functional: null,
			roleModifier: null,

			// Common Billing
			ohipNo: null,
			thirdPartyBillingNo: null,
			alternateBillingNo: null,

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

							if (role.roleName === 'doctor' && ctrl.mode === EDIT_PROVIDER_MODE.ADD)
							{// if adding a new provider push the default doctor role.
								ctrl.provider.userRoles.push(role.roleId);
							}
						}
					},
					function error(result)
					{
						console.error("Failed to fetch provider roles with error: " + error);
					}
			);

			// check if this provider is super admin
			providerService.getMe().then(
					function success(result)
					{
						ctrl.billingRegionSelectEnabled = result.superAdmin;
					},
					function error(result)
					{
						console.error("Failed to fetch provider data with Error: " + result);
					}
			);

			systemPreferenceApi.getPropertyValue("billing_type", "BC").then(
					function success(result)
					{
						ctrl.billingRegion = {label: result.data.body, value: result.data.body};
					},
					function error(result)
					{
						console.error("Failed to fetch instance billing type with error: " + error);
					}
			)

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