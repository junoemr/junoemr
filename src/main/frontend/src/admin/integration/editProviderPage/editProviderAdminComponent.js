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
import {SitesApi} from "../../../../generated";


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
		'billingService',
		function (
			$scope,
			$stateParams,
			$http,
			$httpParamSerializer,
			staticDataService,
			providersService,
			providerService,
			billingService)
	{
		let ctrl = this;

		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
		let sitesApi =  new SitesApi($http, $httpParamSerializer, '../ws/rs');

		ctrl.modes = EDIT_PROVIDER_MODE;
		ctrl.mode = $stateParams.mode;

		ctrl.sexes = staticDataService.getGenders();
		ctrl.providerTypes = staticDataService.getProviderTypes();

		ctrl.roleOptions = [];
		ctrl.currentRoleSelection = null;

		ctrl.siteOptions = [];
		ctrl.currentSiteSelection = null;

		// billingRegion. determines what controls display
		ctrl.billingRegionSelectEnabled = false;
		ctrl.billingRegion = null;
		ctrl.billingRegionOptions = staticDataService.getBillingRegions();

		// options for the AB skill code field.
		ctrl.skillCodeOptions = [];

		// options for the AB location code field.
		ctrl.locationCodeOptions = [
			{
				label: "Home (HOME)",
				value: "HOME"
			},
			{
				label: "Other (OTHR)",
				value: "OTHR"
			}
		];

		// options for the AB facilities field
		ctrl.albertaFacilityOptions = [];

		// options for the AB functional centers field
		ctrl.albertaFunctionalCenterOptions = [];

		// options for the AB default time/role modifier field
		ctrl.albertaDefaultTimeRoleOptions = staticDataService.getAlbertaTimeRoleModifier();

		// options for the SK mode billing field
		ctrl.saskatchewanBillingModeOptions = staticDataService.getSaskatchewanBillingModes();

		// options for the SK location field
		ctrl.saskatchewanLocationCodeOptions = staticDataService.getSaskatchewanLocationCodes();

		// options for the SK submission type field
		ctrl.saskatchewanSubmissionTypeOptions = staticDataService.getSaskatchewanSubmissionTypes();

		// options for the SK Corporation Indicator field
		ctrl.saskatchewanCorporationIndicatorOptions = staticDataService.getSaskatchewanCorporationIndicators();

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

			// site assignment
			siteAssignments: [],

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
			abClinic: null,
			abSourceCode: "ab",
			abSkillCode: null,
			abLocationCode: null,
			abBANumber: null,
			abFacilityNumber: null,
			abFunctionalCenter: null,
			abRoleModifier: null,

			// SK Billing
			skMode: null,
			skLocationCode: null,
			skSubmissionType: null,
			skCorporationIndicator: null,

			// Common Billing
			ohipNo: null,
			thirdPartyBillingNo: null,
			alternateBillingNo: null,

			//3rd Party Identifiers
			cpsid: null,
			ihaProviderMnemonic: null,
			connectCareProviderId: null,
			takNumber: null,
			lifeLabsClientIds: null,
			eDeliveryIds: null
		};

		// provider field validations built using, Expressive Validations TM.
		ctrl.providerValidations = {
			// User Info
			firstName: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'firstName'),
			lastName: Juno.Common.Util.validationFieldRequired(ctrl.provider,'lastName'),
			type: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'type'),

			// Login Info
			email: Juno.Common.Util.validationFieldNop(),
			userName: Juno.Common.Util.validationFieldNop(),
			password: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'password'),
			passwordVerify: Juno.Common.Util.validationFieldsEqual(
					ctrl.provider,'password',
					ctrl.provider, 'passwordVerify',
					Juno.Common.Util.validationFieldRequired(ctrl.provider, 'passwordVerify')),
			secondLevelPasscode: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'secondLevelPasscode'),
			secondLevelPasscodeVerify: Juno.Common.Util.validationFieldsEqual(
					ctrl.provider, "secondLevelPasscode",
					ctrl.provider, "secondLevelPasscodeVerify",
					Juno.Common.Util.validationFieldRequired(ctrl.provider, 'secondLevelPasscodeVerify')),
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

						if (ctrl.billingRegion === "AB")
						{
							ctrl.loadAlbertaBillingData();
						}
					},
					function error(result)
					{
						console.error("Failed to fetch instance billing type with error: " + error);
					}
			);

			sitesApi.getSiteList().then(
					function success(result)
					{
						ctrl.siteOptions = [];
						for (let site of result.data.body)
						{
							ctrl.siteOptions.push(
									{
										label: site.name,
										value: site.siteId,
										bgColor: site.bgColor
									}
							)
						}
					},
					function error(result)
					{
						console.error("Failed to fetch site list with error: " + result);
					}
			);

			// when we switch to AB bill region load additional data.
			$scope.$watch('$ctrl.billingRegion', function(newVal, oldVal)
			{
				if (newVal && newVal.value === "AB")
				{
					ctrl.loadAlbertaBillingData();
				}
			});
		};

		// load alberta specific data for billing fields
		ctrl.loadAlbertaBillingData = function()
		{
			// load skill codes
			billingService.getAlbertaSkillCodes().then(
					function success(result)
					{
						ctrl.skillCodeOptions = [];
						for (let skillCode of result.data.body)
						{
							ctrl.skillCodeOptions.push(
									{
										label: skillCode.description + "(" + skillCode.skillCode + ")",
										value: skillCode.skillCode
									}
							);
						}
					},
					function error(result)
					{
						console.log("Failed to fetch alberta skill codes with error: " + result);
					}
			);

			billingService.getAlbertaFacilities().then(
					function success(result)
					{
						ctrl.albertaFacilityOptions = [];
						for (let facility of result.data.body)
						{
							ctrl.albertaFacilityOptions.push(
									{
										label: facility.description + "(" + facility.code + ")",
										value: facility.code
									}
							);
						}
					},
					function error(result)
					{
						console.error("Failed to fetch alberta facilities with error: " + result);
					}
			);


			billingService.getAlbertaFunctionalCenters().then(
					function success(result)
					{
						ctrl.albertaFunctionalCenterOptions = [];
						for (let functionalCenter of result.data.body)
						{
							ctrl.albertaFunctionalCenterOptions.push(
									{
										label: functionalCenter.description + "(" + functionalCenter.code + ")",
										value: functionalCenter.code
									}
							);
						}
					},
					function error(result)
					{
						console.error("Failed to fetch alberta functional centers list with error: " + result);
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
		};

		ctrl.addSiteAssignment = function(siteId)
		{
			if (siteId && !ctrl.provider.siteAssignments.includes(siteId))
			{
				ctrl.provider.siteAssignments.push(siteId);
			}
		};

		ctrl.removeSiteAssignment = function(siteId)
		{
			if (siteId)
			{
				let idx = ctrl.provider.siteAssignments.findIndex(el => el === siteId);
				ctrl.provider.siteAssignments.splice(idx, 1);
			}
		};

		ctrl.getSiteName = function(roleId)
		{
			return ctrl.siteOptions.find(el => el.value === roleId).label;
		};

		ctrl.submit = function()
		{
			//validate fields
			if (ctrl.allFieldsValid())
			{// valid
				alert("WINWWIWIWNIWIWNWIWNWI");
			}
			else
			{//invalid
				alert("FAIL :(");
			}
		};

		ctrl.allFieldsValid = function ()
		{
			for(let validation in ctrl.providerValidations)
			{
				if (Object.prototype.hasOwnProperty.call(ctrl.providerValidations, validation)) {
					if (!ctrl.providerValidations[validation]())
					{
						return false;
					}
				}
			}
			return true;
		};
	}]
});