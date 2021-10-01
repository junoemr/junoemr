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
import {ProvidersServiceApi} from "../../../../generated";
import {SitesApi} from "../../../../generated";
import {BILLING_REGION} from "../../../billing/billingConstants";
import {LABEL_POSITION} from "../../../common/components/junoComponentConstants";


angular.module('Admin.Section').component('editProviderAdmin',
{
	templateUrl: 'src/admin/section/editProviderPage/editProviderAdmin.jsp',
	bindings: {},
	controller: [
		'$scope',
		'$stateParams',
		'$http',
		'$httpParamSerializer',
		'$location',
		'$uibModal',
		'staticDataService',
		'providerService',
		'billingService',
		function (
			$scope,
			$stateParams,
			$http,
			$httpParamSerializer,
			$location,
			$uibModal,
			staticDataService,
			providerService,
			billingService)
	{
		let ctrl = this;

		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
		let providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer, "../ws/rs");
		let sitesApi =  new SitesApi($http, $httpParamSerializer, '../ws/rs');

		ctrl.LABEL_POSITION = LABEL_POSITION;

		ctrl.modes = EDIT_PROVIDER_MODE;
		ctrl.mode = $stateParams.mode;
		ctrl.hasSubmitted = false;
		ctrl.allowSubmit = false; // if false submit is blocked.
		ctrl.loadingError = false; // if true submit is blocked. if any async function returns error set this.
		ctrl.fieldsDisabled = ctrl.mode === EDIT_PROVIDER_MODE.VIEW;
		ctrl.isMultisiteEnabled = false;

		ctrl.sexes = staticDataService.getGenders();
		ctrl.providerTypes = staticDataService.getProviderTypes();

		ctrl.roleOptions = [];
		ctrl.currentRoleSelection = null;

		ctrl.siteOptions = [];
		ctrl.currentSiteSelection = null;
		ctrl.bcpSiteOptions = [];
		ctrl.currentBcpSiteSelection = null;

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

		// options for the provider status
		ctrl.providerStatusOptions = [
			{
				label: "Active",
				value: "1"
			},
			{
				label: "Inactive",
				value: "0"
			}
		];

		// security records that can be edited.
		ctrl.securityRecordOptions = [];

		// options for the BC rural retention code field
		ctrl.bcBillingLocationOptions = [];

		// options for the ON visit location filed (internally, "ontario master number")
		ctrl.onVisitLocationOptions = [];

		// options for the ON service location indicator
		ctrl.onServiceLocationIndicatorOptions = staticDataService.getOntarioServiceLocationIndicators();
		
		// options for the BC service location field
		ctrl.bcServiceLocationOptions = [];

		// options for BCP eligibility
		ctrl.bcEligibilityOptions = [
			{
				label: "No",
				value: false,
			},
			{
				label: "Yes",
				value: true,
			},
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
			status: "1",// active by default

			// Login Info
			securityRecords: [],
			currentSecurityRecord: null,
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
			bookingNotificationNumbers: null,
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
			bcpSites: [],

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
			onCnoNumber: null,
			ihaProviderMnemonic: null,
			connectCareProviderId: null,
			takNumber: null,
			lifeLabsClientIds: null,
			eDeliveryIds: null,
			imdHealthUuid: null
		};

		ctrl.setupFormValidations = function()
		{
			// generic validations
			ctrl.providerValidations = {
				// User Info
				firstName: Juno.Validations.validationFieldRequired(ctrl.provider, 'firstName'),
				lastName: Juno.Validations.validationFieldRequired(ctrl.provider, 'lastName'),
				type: Juno.Validations.validationFieldRequired(ctrl.provider, 'type'),
				userRoles: Juno.Validations.validationCustom(() => ctrl.provider.userRoles.length >= 1),
				bookingNotificationNumbers: Juno.Validations.validationPhone(ctrl.provider, 'bookingNotificationNumbers'),
			};

			// password field validations
			if (ctrl.mode === EDIT_PROVIDER_MODE.ADD)
			{
				for (let secRecord of ctrl.provider.securityRecords)
				{
					secRecord.validations = {
						password: Juno.Validations.validationFieldRequired(secRecord, 'password',
								Juno.Validations.validationPassword(secRecord, 'password')),
						passwordVerify: Juno.Validations.validationFieldRequired(secRecord, 'passwordVerify'),
						passwordMatch: Juno.Validations.validationFieldsEqual(
								secRecord, 'password',
								secRecord, 'passwordVerify'),
						secondLevelPasscode: Juno.Validations.validationFieldRequired(secRecord, 'pin',
								Juno.Validations.validationFieldNumber(secRecord, 'pin')),
						secondLevelPasscodeVerify: Juno.Validations.validationFieldRequired(secRecord, 'pinVerify',
								Juno.Validations.validationFieldNumber(secRecord, 'pinVerify')),
						secondLevelPasscodeMatch: Juno.Validations.validationFieldsEqual(
								secRecord, 'pin',
								secRecord, 'pinVerify'),
						// user name / email
						emailOrUserName: Juno.Validations.validationFieldOr(
								Juno.Validations.validationFieldRequired(secRecord, 'userName'),
								Juno.Validations.validationFieldRequired(secRecord, 'email'))
					};
				}
			}
			else if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT)
			{
				for (let secRecord of ctrl.provider.securityRecords)
				{
					secRecord.validations = {
						// password blank or password and validation must match
						password: Juno.Validations.validationFieldBlankOrOther(secRecord, 'password',
								Juno.Validations.validationPassword(secRecord, 'password')),
						passwordVerify: Juno.Validations.validationFieldNop(),
						passwordMatch: Juno.Validations.validationFieldBlankOrOther(secRecord, 'password',
								Juno.Validations.validationFieldsEqual(
										secRecord, 'password',
										secRecord, 'passwordVerify')),
						// pin blank or pin and validation must match
						secondLevelPasscode: Juno.Validations.validationFieldBlankOrOther(secRecord, 'pin',
								Juno.Validations.validationFieldNumber(secRecord, 'pin')),
						secondLevelPasscodeVerify: Juno.Validations.validationFieldBlankOrOther(secRecord, 'pinVerify',
								Juno.Validations.validationFieldNumber(secRecord, 'pinVerify')),
						secondLevelPasscodeMatch: Juno.Validations.validationFieldBlankOrOther(secRecord, 'pin',
								Juno.Validations.validationFieldsEqual(
										secRecord, 'pin',
										secRecord, 'pinVerify')),
						// user name / email
						emailOrUserName: Juno.Validations.validationFieldOr(
								Juno.Validations.validationFieldRequired(secRecord, 'userName'),
								Juno.Validations.validationFieldRequired(secRecord, 'email'))
					};
				}
			}
		};

		ctrl.$onInit = async function()
		{
			providersServiceApi.getProviderRoles().then(
					function success(result)
					{
						ctrl.roleOptions = [];
						for (let role of result.data.body)
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
						ctrl.loadingError = true;
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
						ctrl.loadingError = true;
					}
			);

			systemPreferenceApi.getPropertyValue("billing_type", BILLING_REGION.BC).then(
					function success(result)
					{
						ctrl.billingRegion = result.data.body;
						if (ctrl.billingRegion === BILLING_REGION.CLINICAID)
						{
							systemPreferenceApi.getPropertyValue("instance_type", BILLING_REGION.BC).then((result) =>
							{
								ctrl.billingRegion = result.data.body;
							})
							.catch((error) =>
							{
							  console.error("Failed to fetch Clinicaid billing type with error" + error);
							  ctrl.loadingError = true;
							});
						}
					},
					function error(result)
					{
						console.error("Failed to fetch instance billing type with error: " + error);
						ctrl.loadingError = true;
					}
			);

			systemPreferenceApi.getPropertyEnabled("multisites").then(
				(response) =>
				{
					ctrl.isMultisiteEnabled = response.data.body;
				}
			);

			// when we switch bill region, load additional data.
			$scope.$watch('$ctrl.billingRegion', async function(newVal, oldVal)
			{
				if (newVal)
				{
					if (newVal === "AB")
					{
						await ctrl.loadAlbertaBillingData();
					}
					else if (newVal === "BC")
					{
						await ctrl.loadBCBillingData();
					}
					else if (newVal === "ON")
					{
						await ctrl.loadOntarioBillingData();
					}
				}
			});


			// load the provider object if in view mode.
			if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT || ctrl.mode === EDIT_PROVIDER_MODE.VIEW)
			{
				ctrl.loadProviderFrom($stateParams.providerNo);
			}
			else
			{
				ctrl.setupSecurityRecords();
				ctrl.setupFormValidations();
				ctrl.allowSubmit = true;
			}

			sitesApi.getSiteList().then(
					function success(result)
					{
						ctrl.siteOptions = [];
						for (let site of result.data.body)
						{
							let option = 	{
								label: site.name,
								value: site.siteId,
								bgColor: site.bgColor,
								province: site.province,
							};

							ctrl.siteOptions.push(option);
						}

						ctrl.updateBCPSiteList();
					},
					function error(result)
					{
						console.error("Failed to fetch site list with error: " + result);
						ctrl.loadingError = true;
					}
			);
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
				console.error("Failed to fetch alberta skill codes with error: " + e);
				ctrl.loadingError = true;
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
				ctrl.loadingError = true;
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
				ctrl.loadingError = true;
			}
		};

		ctrl.loadBCBillingData = async function()
		{
			try
			{
				let result = await billingService.getBCBillingVisitCodes();
				ctrl.bcServiceLocationOptions = [{
					label: "None",
					value: null,
				}];

				for (let visitCode of result.data.body)
				{
					ctrl.bcServiceLocationOptions.push({
						label: "(" + visitCode.visitType + ") " + visitCode.visitDescription,
						value: visitCode.visitType
					});
				}
			}
			catch (e)
			{
				console.error("Failed to fetch BC Billing visit codes with error: " + e);
				ctrl.loadingError = true;
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
				ctrl.loadingError = true;
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
				ctrl.loadingError = true;
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
			if (roleId && ctrl.mode !== EDIT_PROVIDER_MODE.VIEW)
			{
				let idx = ctrl.provider.userRoles.findIndex(el => el === roleId);
				ctrl.provider.userRoles.splice(idx, 1);
			}
		};

		ctrl.getUserRoleName = function(roleId)
		{
			if (ctrl.roleOptions && ctrl.roleOptions.length > 0)
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

			ctrl.updateBCPSiteList();
		};

		ctrl.removeSiteAssignment = function(siteId)
		{
			if (siteId && ctrl.mode !== EDIT_PROVIDER_MODE.VIEW)
			{
				let idx = ctrl.provider.siteAssignments.findIndex(el => el === siteId);
				ctrl.provider.siteAssignments.splice(idx, 1);
			}

			ctrl.updateBCPSiteList();
		};

		ctrl.addBCPSiteAssignment = function (siteId)
		{
			if (siteId && !ctrl.provider.bcpSites.includes(siteId))
			{
				ctrl.provider.bcpSites.push(siteId);
			}
		};

		ctrl.removeBCPSiteAssignment = function(siteId)
		{
			if (siteId && ctrl.mode !== EDIT_PROVIDER_MODE.VIEW)
			{
				let idx = ctrl.provider.bcpSites.findIndex(el => el === siteId);
				ctrl.provider.bcpSites.splice(idx, 1);
			}
		};

		// only sites assigned to the provider can have bcp disabled / enabled.
		ctrl.updateBCPSiteList = function()
		{
			ctrl.bcpSiteOptions = ctrl.siteOptions.filter((site) =>
			{
				return site.province === "BC" && ctrl.provider.siteAssignments.includes(site.value);
			});

			ctrl.provider.bcpSites = ctrl.provider.bcpSites.filter((bcpSite) => ctrl.provider.siteAssignments.includes(bcpSite));
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

		ctrl.isBCPAvailableForSite = function(roleId)
		{
			if (ctrl.siteOptions&& ctrl.siteOptions.length > 0)
			{
				return ctrl.siteOptions.find(el => el.value === roleId).province === "BC";
			}
			else
			{
				return false;
			}
		};

		ctrl.loadProviderFrom = function(providerNo)
		{
			providerService.getProviderEditForm(providerNo).then(
					function success(result)
					{
						ctrl.provider = result.body;
						ctrl.setupSecurityRecords();
						ctrl.setupFormValidations();
						ctrl.updateBCPSiteList();
						ctrl.allowSubmit = true;

					},
					function error(result)
					{
						console.error("Failed to load provider edit form with error: " + result);
						ctrl.loadingError = true;
					}
			);
		};

		ctrl.setupSecurityRecords = function ()
		{
			// in add mode add one "new" security record.
			if (ctrl.mode === EDIT_PROVIDER_MODE.ADD)
			{
				ctrl.provider.securityRecords = [];
				ctrl.provider.securityRecords.push({
					securityNo: -1,
					userName: "",
					email: "",
					password: "",
					providerNo: null,
					pin: ""
				});
				ctrl.provider.currentSecurityRecord = -1;
			}
			else if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT || ctrl.mode === EDIT_PROVIDER_MODE.VIEW)
			{
				// convert security record transfers in to option list for user selection.
				ctrl.securityRecordOptions = [];
				for (let securityRecord of ctrl.provider.securityRecords)
				{
					let label = securityRecord.userName;
					if (securityRecord.email)
					{
						label += " - " + securityRecord.email;
					}
					if (securityRecord.securityNo === -1)
					{
						label = "New Record"
					}
					ctrl.securityRecordOptions.push(
							{
								label: label,
								value: securityRecord.securityNo
							}
					)
				}

				// if current securityNo is not in the list set to no selection.
				if (!ctrl.securityRecordOptions.find((sec) => sec.value === ctrl.provider.currentSecurityRecord))
				{
					ctrl.provider.currentSecurityRecord = null;
				}
			}

			// add additional password / pin / validations fields to security objects
			for (let securityRecord of ctrl.provider.securityRecords)
			{
				securityRecord.passwordVerify = "";
				securityRecord.pinVerify = "";
				securityRecord.validations = null;
			}
		};

		ctrl.submit = function()
		{
			if (ctrl.allowSubmit && !ctrl.loadingError)
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
									ctrl.handleApiResponse(result, "created");
									if (result.status === "SUCCESS")
									{
										$location.url(`/admin/editUser?providerNo=${result.body.providerNo}`);
									}
								},
								function error(result)
								{
									Juno.Common.Util.errorAlert($uibModal, "Error", "Internal Server Error. Provider not created");
								}
						);
					}
					else
					{//invalid
						Juno.Common.Util.errorAlert($uibModal, "Validation Error", "Some fields are invalid please correct the highlighted fields");
					}
				}
				else if (ctrl.mode === EDIT_PROVIDER_MODE.EDIT)
				{ // update provider
					if (ctrl.allFieldsValid())
					{
						providerService.editProvider($stateParams.providerNo, ctrl.translateProviderObjForSubmit(ctrl.provider)).then(
								function success(result)
								{
									ctrl.handleApiResponse(result, "updated");
								},
								function error(result)
								{
									Juno.Common.Util.errorAlert($uibModal, "Error", "Internal Server Error. Provider not updated");
								}
						);
					} else
					{
						Juno.Common.Util.errorAlert($uibModal, "Validation Error", "Some fields are invalid please correct the highlighted fields");
					}
				}
			}
			else {
				Juno.Common.Util.errorAlert($uibModal, "Page Not Loaded", "Some of the page assets have failed to load. To protect your data you cannot submit");
			}
		};

		ctrl.handleApiResponse = function(result, actionString)
		{
			if (result.status === "SUCCESS")
			{
				Juno.Common.Util.successAlert($uibModal, "Success", "Provider successfully " + actionString);
			}
			else if (result.status === "ERROR")
			{
				if (result.error.message === "SECURITY_RECORD_EXISTS")
				{
					Juno.Common.Util.errorAlert($uibModal, "Validation Error", "User name or email already in use");
				}
				else if (result.error.message === "INSUFFICIENT_PRIVILEGE")
				{
					Juno.Common.Util.errorAlert($uibModal, "Sorry, you are not authorized to make changes to this user.");
				}
				else
				{
					Juno.Common.Util.errorAlert($uibModal, "Error", "Unknown error: " + result.error.message);
				}
			}
		};

		ctrl.translateProviderObjForSubmit = function (providerObj)
		{
			// copy provider obj
			let newProvider = {};
			Object.assign(newProvider, providerObj);
			return newProvider;
		};

		ctrl.allFieldsValid = function ()
		{
			for (let securityRecord of ctrl.provider.securityRecords)
			{
				if (!Juno.Validations.allValidationsValid(securityRecord.validations))
				{
					return false;
				}
			}

			return Juno.Validations.allValidationsValid(ctrl.providerValidations);
		};

		// transition to edit mode.
		ctrl.goToEdit = function()
		{
			$location.url("/admin/editUser?providerNo=" + $stateParams.providerNo);
		}
	}]
});
