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
		ctrl.hasSubmitted = false;

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


		// options for the BC rural retention code field
		ctrl.bcBillingLocationOptions = [];

		// options for the ON visit location filed (internally, "ontario master number")
		ctrl.onVisitLocationOptions = [];

		// options for the ON service location indicator
		ctrl.onServiceLocationIndicatorOptions = staticDataService.getOntarioServiceLocationIndicators();

		// options for the BC service location field
		ctrl.bcServiceLocationOptions = [];

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
			pagerNumber: null,

			// Access Roles
			userRoles: [],

			// site assignment
			siteAssignments: [],

			// BC Billing
			bcBillingNo: null,
			bcRuralRetentionCode: null,
			bcServiceLocation: null,

			// ON Billing
			onGroupNumber: null,
			onSpecialityCode: null,
			onVisitLocation: null,
			onServiceLocationIndicator: null,

			// AB Billing
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
			emailOrUserName: Juno.Common.Util.validationFieldOr(
					Juno.Common.Util.validationFieldRequired(ctrl.provider, 'userName'),
					Juno.Common.Util.validationFieldRequired(ctrl.provider, 'email')
			),
			password: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'password'),
			passwordVerify: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'passwordVerify'),
			passwordMatch: Juno.Common.Util.validationFieldsEqual(
					ctrl.provider,'password',
					ctrl.provider, 'passwordVerify'),
			secondLevelPasscode: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'secondLevelPasscode'),
			secondLevelPasscodeVerify: Juno.Common.Util.validationFieldRequired(ctrl.provider, 'secondLevelPasscodeVerify'),
			secondLevelPasscodeMatch: Juno.Common.Util.validationFieldsEqual(
					ctrl.provider,'secondLevelPasscode',
					ctrl.provider, 'secondLevelPasscodeVerify'),
		};

		ctrl.$onInit = async function()
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

			// when we switch bill region, load additional data.
			$scope.$watch('$ctrl.billingRegion', async function(newVal, oldVal)
			{
				if (newVal)
				{
					if (newVal.value === "AB")
					{
						await ctrl.loadAlbertaBillingData();
					}
					else if (newVal.value === "BC")
					{
						await ctrl.loadBCBillingData();
					}
					else if (newVal.value === "ON")
					{
						await ctrl.loadOntarioBillingData();
					}
				}
				ctrl.mapTypeaheadValues();
			});


			// load the provider object if in view mode.
			if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT)
			{
				ctrl.loadProviderFrom($stateParams.providerNo);
			}
		};

		// load alberta specific data for billing fields
		ctrl.loadAlbertaBillingData = async function()
		{
			// load skill codes
			try
			{
				let result = await billingService.getAlbertaSkillCodes();
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
			}
			catch (e)
			{
				console.log("Failed to fetch alberta skill codes with error: " + e);
			}

			try
			{
				let result = await billingService.getAlbertaFacilities();
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
			}
			catch (e)
			{
				console.error("Failed to fetch alberta facilities with error: " + e);
			}

			try
			{
				let result = await billingService.getAlbertaFunctionalCenters();
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
			}
			catch (e)
			{
				console.error("Failed to fetch alberta functional centers list with error: " + result);
			}
		};

		ctrl.loadBCBillingData = async function()
		{
			try
			{
				let result = await billingService.getBCBillingVisitCodes();
				ctrl.bcServiceLocationOptions = [];
				for (let visitCode of result.data.body)
				{
					ctrl.bcServiceLocationOptions.push(
							{
								label: "(" + visitCode.visitType + ") " + visitCode.visitDescription,
								value: visitCode.visitType
							}
					);
				}
			}
			catch (e)
			{
				console.error("Failed to fetch BC Billing visit codes with error: " + e);
			}

			try
			{
				let result = await billingService.getBCBillingLocations();
				ctrl.bcBillingLocationOptions = [];
				for (let location of result.data.body)
				{
					ctrl.bcBillingLocationOptions.push({
						label: "(" + location.billingLocation + ") " + location.description,
						value: location.billingLocation,
					});
				}
			}
			catch (e)
			{
				console.error("Failed to fetch BC Service Locations with error: " + e);
			}
		};

		ctrl.loadOntarioBillingData = async function()
		{
			try
			{
				let result = await billingService.getOntarioMasterNumbers();
				ctrl.onVisitLocationOptions = [];
				for (let masterNum of result.data.body)
				{
					ctrl.onVisitLocationOptions.push(
							{
								label: "[" + masterNum.type + "] " + masterNum.location + " (" + masterNum.masterNumber + ") " + masterNum.name,
								value: masterNum.masterNumber
							}
					)
				}
			}
			catch (e)
			{
				console.error("Failed to fetch master number list with error: " + e);
			}
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
			if (ctrl.roleOptions)
			{
				return ctrl.roleOptions.find(el => el.value === roleId).label;
			}
			else
			{
				return "loading...";
			}
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
			if (ctrl.siteOptions&& ctrl.siteOptions.length > 0)
			{
				return ctrl.siteOptions.find(el => el.value === roleId).label;
			}
			else
			{
				return "loading...";
			}
		};

		ctrl.loadProviderFrom = function(providerNo)
		{
			providerService.getProviderEditForm(providerNo).then(
					function success(result)
					{
						ctrl.provider = result.body;
						ctrl.mapTypeaheadValues();
					},
					function error(result)
					{
						console.error("Failed to load provider edit form with error: " + result);
					}
			);
		};

		// map typeahead values to labels. because typeahead does not auto fill label.
		ctrl.mapTypeaheadValues = function()
		{
			// map bc service location.
			ctrl.provider.bcServiceLocation = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.bcServiceLocation, ctrl.bcServiceLocationOptions);

			// map on visit location
			ctrl.provider.onVisitLocation = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.onVisitLocation, ctrl.onVisitLocationOptions);

			// map ab billing fields
			// skill code
			ctrl.provider.abSkillCode = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.abSkillCode, ctrl.skillCodeOptions);
			// location code
			ctrl.provider.abLocationCode = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.abLocationCode, ctrl.locationCodeOptions);
			// facility number
			ctrl.provider.abFacilityNumber = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.abFacilityNumber, ctrl.albertaFacilityOptions);
			// functional centers
			ctrl.provider.abFunctionalCenter = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.abFunctionalCenter, ctrl.albertaFunctionalCenterOptions);
			// time role modifier
			ctrl.provider.abRoleModifier = Juno.Common.Util.typeaheadValueLookup(ctrl.provider.abRoleModifier, ctrl.albertaDefaultTimeRoleOptions);
		};

		ctrl.submit = function()
		{
			ctrl.hasSubmitted = true;

			if (ctrl.mode === EDIT_PROVIDER_MODE.ADD)
			{// create new provider

				//validate fields
				if (ctrl.allFieldsValid())
				{// valid
					providerService.createProvider(ctrl.translateProviderObjForSubmit(ctrl.provider)).then(
							function success(result)
							{
								if (result.body.status === "SUCCESS")
								{
									alert("WIN");
								} else if (result.body.status === "SECURITY_RECORD_EXISTS")
								{
									alert("User Name or Email already in use.")
								}
							},
							function error(result)
							{
								alert("API ERROR");
							}
					);
				} else
				{//invalid
					alert("INVALID");
				}
			}
			else if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT)
			{ // update provider
				providerService.editProvider($stateParams.providerNo, ctrl.translateProviderObjForSubmit(ctrl.provider)).then(
						function success(result)
						{
							alert("Provider Updated");
						},
						function error(result)
						{
							alert("Update Error");
						}
				);
			}
		};

		ctrl.translateProviderObjForSubmit = function (providerObj)
		{
			// copy provider obj
			let newProvider = {};
			Object.assign(newProvider, providerObj);

			//translate fields
			// BC Billing
			if (providerObj.bcServiceLocation)
			{
				newProvider.bcServiceLocation = providerObj.bcServiceLocation.value;
			}
			// ON Billing
			if (providerObj.onVisitLocation)
			{
				newProvider.onVisitLocation = providerObj.onVisitLocation.value;
			}
			// AB Billing
			if(providerObj.abSkillCode)
			{
				newProvider.abSkillCode = providerObj.abSkillCode.value;
			}
			if(providerObj.abLocationCode)
			{
				newProvider.abLocationCode = providerObj.abLocationCode.value;
			}
			if (providerObj.abFacilityNumber)
			{
				newProvider.abFacilityNumber = providerObj.abFacilityNumber.value;
			}
			if (providerObj.abFunctionalCenter)
			{
				newProvider.abFunctionalCenter = providerObj.abFunctionalCenter.value;
			}
			if (providerObj.abRoleModifier)
			{
				newProvider.abRoleModifier = providerObj.abRoleModifier.value;
			}

			return newProvider;
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