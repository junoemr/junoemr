/*

    Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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

    This software was written for the
    Department of Family Medicine
    McMaster University
    Hamilton
    Ontario, Canada

*/

import {BILLING_TYPE, INSTANCE_TYPE, SYSTEM_PROPERTIES} from "../../common/services/systemPreferenceServiceConstants";
import {DemographicApi, ProvidersServiceApi, SystemPreferenceApi} from "../../../generated";
import {JUNO_STYLE} from "../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../common/security/securityConstants";
import {BILLING_REGION} from "../../billing/billingConstants";
import Demographic from "../../lib/demographic/model/Demographic";
import ToastService from "../../lib/alerts/service/ToastService";
import moment from "moment";

angular.module('Record.Details').controller('Record.Details.DetailsController', [

	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'$window',
	'$uibModal',
	'$httpParamSerializer',
	'$sce',
	'demographicService',
	'demographicsService',
	'errorsService',
	'patientDetailStatusService',
	'securityRolesService',
	'staticDataService',
	'referralDoctorsService',
	'user',
	'uxService',

	function(
		$scope,
		$http,
		$location,
		$stateParams,
		$state,
		$window,
		$uibModal,
		$httpParamSerializer,
		$sce,
		demographicService,
		demographicsService,
		messagesFactory,
		patientDetailStatusService,
		securityRolesService,
		staticDataService,
		referralDoctorsService,
		user,
		uxService)
	{

		const controller = this;
		controller.page = {};
		controller.page.demo = null as Demographic;
		const demographicApi = new DemographicApi($http, $httpParamSerializer, "../ws/rs");

		controller.toastService = new ToastService();

		// Global variables
		var posExtras = {};
		var hcParts = {};
		var phoneNum = {};
		var colorAttn = "#ffff99";
		var defPhTitle = "Check to set preferred contact number";
		var prefPhTitle = "Preferred contact number";
		var hin0;
		var ver0;
		var chartNo0;
		var cytolNum0;
		var referralDocNo0;
		var familyDocNo0;
		var sin0;
		var effDate0;
		var hcRenewDate0;
		var rosterDate0;
		var rosterTerminationDate0;
		var patientStatusDate0;
		var dateJoined0;
		var endDate0;
		var onWaitingListSinceDate0;
		var paperChartArchivedDate0;

		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
				'../ws/rs');

		let providersServiceApi = new ProvidersServiceApi($http, $httpParamSerializer, "../ws/rs");
		controller.eligibilityMsg = $sce.trustAsHtml("...");
		controller.showEligibility = false;
		controller.rosteringModuleEnabled = false;
		controller.displayMessages = messagesFactory.factory();
		controller.validations = {};
		controller.SecurityPermissions = SecurityPermissions;

		$scope.JUNO_STYLE = JUNO_STYLE;
		$scope.pageStyle = JUNO_STYLE.GREY;

		controller.$onInit = () =>
		{
			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicRead))
			{
				demographicService.getDemographic($stateParams.demographicNo).then(
					function success(results: Demographic)
					{
						controller.page.demo = results;
						controller.initDemographicVars();

						// retrieve provider types for dropdown selection
						//TODO-legacy - are roles determined by security role or provider type?
						providersServiceApi.getBySecurityRole("doctor").then(
							function success(results) {
								controller.page.doctors = results.data.body;
							}
						);
						providersServiceApi.getBySecurityRole("nurse").then(
							function success(results) {
								controller.page.nurses = results.data.body;
							}
						);
						providersServiceApi.getBySecurityRole("midwife").then(
							function success(results) {
								controller.page.midwives = results.data.body;
							}
						);
					demographicApi.getDemographicContacts(controller.page.demo.id, "professional").then(
                    (data) => {
						controller.page.demoContactPros = data.data.body;
                    	}
                    );

						// show notes
						// if (controller.page.demo.notes != null)
						// {
						// 	controller.page.demo.scrNotes = controller.page.demo.notes;
						// 	if (/^<unotes>[\s\S]*/.test(controller.page.demo.scrNotes)) controller.page.demo.scrNotes = controller.page.demo.scrNotes.substring("<unotes>".length);
						// 	if (/[\s\S]*<\/unotes>$/.test(controller.page.demo.scrNotes)) controller.page.demo.scrNotes = controller.page.demo.scrNotes.substring(0, controller.page.demo.scrNotes.lastIndexOf("</unotes>"));
						// }

						// format referral doctor
						if (controller.page.demo.familyDoctor != null)
						{
							const referralDoc = controller.formatReferralDocXMLToJSON(controller.page.demo.familyDoctor);
							controller.page.demo.scrReferralDocNo = referralDoc.number;
							controller.page.demo.scrReferralDoc = referralDoc.name;
						}

						// format family doctor
						if (controller.page.demo.familyDoctor2 != null)
						{
							const familyDoc = controller.formatFamilyDocXMLToJSON(controller.page.demo.familyDoctor2, 'fd', 'fdname');
							controller.page.demo.scrFamilyDocNo = familyDoc.number;
							controller.page.demo.scrFamilyDoc = familyDoc.name;
						}

						// if (controller.page.demo.extras != null)
						// {
						// 	controller.page.demo.extras = toArray(controller.page.demo.extras);
						// 	for (var i = 0; i < controller.page.demo.extras.length; i++)
						// 	{
						// 		if (controller.page.demo.extras[i].key == "demo_cell") controller.page.demo.scrDemoCell = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "aboriginal") controller.page.demo.scrAboriginal = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "hPhoneExt") controller.page.demo.scrHPhoneExt = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "wPhoneExt") controller.page.demo.scrWPhoneExt = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "cytolNum") controller.page.demo.scrCytolNum = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "phoneComment") controller.page.demo.scrPhoneComment = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "paper_chart_archived") controller.page.demo.scrPaperChartArchived = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "paper_chart_archived_date") controller.page.demo.scrPaperChartArchivedDate = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "usSigned") controller.page.demo.scrUsSigned = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "privacyConsent") controller.page.demo.scrPrivacyConsent = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "informedConsent") controller.page.demo.scrInformedConsent = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "securityQuestion1") controller.page.demo.scrSecurityQuestion1 = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "securityAnswer1") controller.page.demo.scrSecurityAnswer1 = controller.page.demo.extras[i].value;
						// 		else if (controller.page.demo.extras[i].key == "rxInteractionWarningLevel") controller.page.demo.scrRxInteractionLevel = controller.page.demo.extras[i].value;
						//
						//
						// 		//record array position of extras by keys - to be used on saving
						// 		posExtras[controller.page.demo.extras[i].key] = i;
						// 	}
						// }

						//show phone numbers with preferred check
						// controller.page.demo.scrCellPhone = getPhoneNum(controller.page.demo.scrDemoCell);
						// controller.page.demo.scrHomePhone = getPhoneNum(controller.page.demo.phone);
						// controller.page.demo.scrWorkPhone = getPhoneNum(controller.page.demo.alternativePhone);

						//show waitingListNames
						if (controller.page.demo.waitingListNames != null)
						{
							if (controller.page.demo.waitingListNames.id != null)
							{ //only 1 entry, convert to array
								// var tmp = {};
								// tmp.id = controller.page.demo.waitingListNames.id;
								// tmp.name = controller.page.demo.waitingListNames.name;
								// tmp.groupNo = controller.page.demo.waitingListNames.groupNo;
								// tmp.providerNo = controller.page.demo.waitingListNames.providerNo;
								// tmp.createDate = controller.page.demo.waitingListNames.createDate;
								// tmp.isHistory = controller.page.demo.waitingListNames.isHistory;
								// controller.page.demo.waitingListNames = [tmp];
								// todo
							}
						}


						// controller.page.cellPhonePreferredMsg = defPhTitle;
						// controller.page.homePhonePreferredMsg = defPhTitle;
						// controller.page.workPhonePreferredMsg = defPhTitle;
						// if (isPreferredPhone(controller.page.demo.scrDemoCell))
						// {
						// 	controller.page.demo.scrPreferredPhone = "C";
						// 	controller.page.preferredPhoneNumber = controller.page.demo.scrCellPhone;
						// 	controller.page.cellPhonePreferredMsg = prefPhTitle;
						// 	controller.page.cellPhonePreferredColor = colorAttn;
						// }
						// else if (isPreferredPhone(controller.page.demo.phone))
						// {
						// 	controller.page.demo.scrPreferredPhone = "H";
						// 	controller.page.preferredPhoneNumber = controller.page.demo.scrHomePhone;
						// 	controller.page.homePhonePreferredMsg = prefPhTitle;
						// 	controller.page.homePhonePreferredColor = colorAttn;
						// }
						// else if (isPreferredPhone(controller.page.demo.alternativePhone))
						// {
						// 	controller.page.demo.scrPreferredPhone = "W";
						// 	controller.page.preferredPhoneNumber = controller.page.demo.scrWorkPhone;
						// 	controller.page.workPhonePreferredMsg = prefPhTitle;
						// 	controller.page.workPhonePreferredColor = colorAttn;
						// }
						// else
						// {
						// 	controller.page.preferredPhoneNumber = controller.page.demo.scrHomePhone;
						// }

						controller.page.dataChanged = false;

						//get patient detail status
						patientDetailStatusService.getStatus($stateParams.demographicNo).then(
							function success(results)
							{
								controller.page.macPHRLoggedIn = results.macPHRLoggedIn;
								controller.page.macPHRIdsSet = results.macPHRIdsSet;
								controller.page.macPHRVerificationLevel = results.macPHRVerificationLevel;

								controller.page.integratorEnabled = results.integratorEnabled;
								controller.page.integratorOffline = results.integratorOffline;
								controller.page.integratorAllSynced = results.integratorAllSynced;

								controller.page.workflowEnhance = results.workflowEnhance;
								controller.page.billregion = results.billregion;
								controller.page.defaultView = results.defaultView;
								controller.page.hospitalView = results.hospitalView;

								if (controller.page.integratorEnabled)
								{
									if (controller.page.integratorOffline)
									{
										controller.page.integratorStatusColor = "#ff5500";
										controller.page.integratorStatusMsg = "NOTE: Integrator is not available at this time";
									}
									else if (!controller.page.integratorAllSynced)
									{
										controller.page.integratorStatusColor = "#ff5500";
										controller.page.integratorStatusMsg = "NOTE: Integrated Community is not synced";
									}
								}

								controller.page.billingHistoryLabel = "Invoice List";
								if (controller.page.billregion == "ON") controller.page.billingHistoryLabel = "Billing History";
							},
							function error(errors)
							{
								console.log(errors);
							});

						// controller.page.demo.age = Juno.Common.Util.calcAge(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay);
						// controller.formatLastName(); //done on page load
						// controller.formatFirstName(); //done on page load
					},
					function error(errors)
					{
						controller.toastService.errorToast("Error loading demographic");
						console.error(errors);
					}
				);
			}
			// show eligibility check button only if instance is BC OR (ON AND billing type CLINICAID)
			systemPreferenceApi.getPropertyValue(SYSTEM_PROPERTIES.INSTANCE_TYPE, INSTANCE_TYPE.BC).then(
					function success(result)
					{
						if (result.data.body === INSTANCE_TYPE.BC)
						{
							controller.showEligibility = true;
						}
						else if (result.data.body === INSTANCE_TYPE.ON)
						{
							systemPreferenceApi.getPropertyValue(SYSTEM_PROPERTIES.BILLING_TYPE, BILLING_TYPE.CLINICAID).then(
									function success(result)
									{
										if (result.data.body === BILLING_TYPE.CLINICAID)
										{
											controller.showEligibility = true;
										}
									},
									function error(result)
									{
										console.error("Failed to fetch instance billing type with error: " + result);
									}
							)
						}
					},
					function error(result)
					{
						console.error("Failed to fetch instance type with error: " + result);
					}
			);

			systemPreferenceApi.getPreferenceEnabled(SYSTEM_PROPERTIES.ROSTERING_MODULE, false).then((result) =>
			{
				controller.rosteringModuleEnabled = result.data.body;
			});
		};

		controller.canAccessAppointments = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.AppointmentRead);
		}
		controller.canAccessBilling = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.BillingRead);
		}
		controller.canAccessExport = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicExportRead);
		}
		controller.canEdit = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicUpdate);
		}

		controller.initDemographicVars = function initDemographicVars()
		{
			// var effDateMoment = moment(controller.page.demo.effDate);
			// if(effDateMoment.isValid())
			// {
			// 	controller.page.demo.effDate = Juno.Common.Util.formatMomentDate(effDateMoment);
			// }
			// else
			// {
			// 	controller.page.demo.effDate = null;
			// }
			// var hcRenewDateMoment = moment(controller.page.demo.hcRenewDate);
			// if(hcRenewDateMoment.isValid())
			// {
			// 	controller.page.demo.hcRenewDate = Juno.Common.Util.formatMomentDate(hcRenewDateMoment);
			// }
			// else
			// {
			// 	controller.page.demo.hcRenewDate = null;
			// }

			// convert dates to moment
			// controller.page.demo.dateOfBirth = Juno.Common.Util.getDateMomentFromComponents(controller.page.demo.dobYear,
			// 		controller.page.demo.dobMonth, controller.page.demo.dobDay);
			// controller.page.demo.effDate = Juno.Common.Util.getDateMoment(controller.page.demo.effDate);
			// controller.page.demo.hcRenewDate = Juno.Common.Util.getDateMoment(controller.page.demo.hcRenewDate);
			// controller.page.demo.rosterDate = Juno.Common.Util.getDateMoment(controller.page.demo.rosterDate);
			// controller.page.demo.rosterTerminationDate = Juno.Common.Util.getDateMoment(controller.page.demo.rosterTerminationDate);
			// controller.page.demo.dateJoined = moment(controller.page.demo.dateJoined);
			// controller.page.demo.patientStatusDate = moment(controller.page.demo.patientStatusDate);
			// controller.page.demo.endDate = Juno.Common.Util.getDateMoment(controller.page.demo.endDate);
			if (controller.page.demo.onWaitingListSinceDate)
			{
				controller.page.demo.onWaitingListSinceDate = Juno.Common.Util.getDateMomentFromComponents(controller.page.demo.onWaitingListSinceDate.getFullYear(),
						controller.page.demo.onWaitingListSinceDate.getMonth(), controller.page.demo.onWaitingListSinceDate.getDate());
			}

			// oscar stores no country of origin as "-1" because why not.
			// if (controller.page.demo.countryOfOrigin === "-1")
			// {
			// 	controller.page.demo.countryOfOrigin = null;
			// }

			// phoneNum["C"] = controller.page.demo.scrCellPhone;
			// phoneNum["H"] = controller.page.demo.scrHomePhone;
			// phoneNum["W"] = controller.page.demo.scrWorkPhone;
			// phoneNum["HX"] = controller.page.demo.scrHPhoneExt;
			// phoneNum["WX"] = controller.page.demo.scrWPhoneExt;
			// hin0 = controller.page.demo.hin;
			// ver0 = controller.page.demo.ver;
			// chartNo0 = controller.page.demo.chartNo;
			// cytolNum0 = controller.page.demo.scrCytolNum;
			// referralDocNo0 = controller.page.demo.scrReferralDocNo;
			// sin0 = controller.page.demo.sin;
			// effDate0 = controller.page.demo.effDate;
			// hcRenewDate0 = controller.page.demo.hcRenewDate;
			// rosterDate0 = controller.page.demo.rosterDate;
			// rosterTerminationDate0 = controller.page.demo.rosterTerminationDate;
			// patientStatusDate0 = controller.page.demo.patientStatusDate;
			// dateJoined0 = controller.page.demo.dateJoined;
			// endDate0 = controller.page.demo.endDate;
			// onWaitingListSinceDate0 = controller.page.demo.onWaitingListSinceDate;
			// paperChartArchivedDate0 = controller.page.demo.scrPaperChartArchivedDate;
		};

		//disable click and keypress if user only has read-access
		controller.checkAction = function checkAction(event)
		{
			if (!controller.canEdit())
			{
				event.preventDefault();
				event.stopPropagation();
			}
		};

		//----------------------//
		// on-screen operations //
		//----------------------//
		//monitor data changed
		$scope.$watch(function()
		{
			return controller.page.demo;
		}, function(newValue, oldValue)
		{
			if (newValue !== oldValue && angular.isDefined(oldValue) && angular.isDefined(newValue))
			{
				controller.page.dataChanged = true;
			}

		}, true);

		$window.onbeforeunload = function ()
		{
			/* Have to check if we are on the details page since the controller is
				not necessarily destroyed upon leaving the page
			*/
			if (controller.page.dataChanged === true && $state.current.name === 'record.details')
			{
				return 'You have unsaved patient data. Are you sure you want to leave?';
			}
		};

		$scope.$on('$destroy', function() {
			delete $window.onbeforeunload;
		});

		// Warn user about unsaved data before a state change
		$scope.$on("$stateChangeStart", function(event)
		{
			if (controller.page.dataChanged === true)
			{
				var discard = confirm("You have unsaved patient data. Are you sure you want to leave?");
				if (!discard)
				{
					event.preventDefault();
				}
			}
		});

		//format lastname, firstname
		// controller.formatLastName = function formatLastName()
		// {
		// 	controller.page.demo.lastName = controller.page.demo.lastName.toUpperCase();
		// };
		// controller.formatFirstName = function formatFirstName()
		// {
		// 	controller.page.demo.firstName = controller.page.demo.firstName.toUpperCase();
		// };

		controller.openSwipecardModal = function openSwipecardModal()
		{
			var modalInstance = $uibModal.open(
				{
					templateUrl: 'src/record/details/swipecard.jsp',
					controller: 'Record.Details.SwipecardController as swipecardController',
					backdrop: 'static',
					windowClass: 'juno-modal',
				});
			modalInstance.result.then(
				// the object passed back on closing
				function success(cardInfo)
				{
					// console.info(cardInfo);
					controller.fillDataFromSwipecard(cardInfo.data);
				},
				function error(errors)
				{
					// do nothing on dismissal
				});
		};

		controller.checkEligibility = function ()
		{
			patientDetailStatusService.getEligibilityInfo(controller.page.demo.id).then(
				function success(result)
				{
					controller.eligibilityMsg = $sce.trustAsHtml(result);
				},
				function error(result)
				{
					console.error("Failed to check eligibility with error: " + result);
				}
			);
		};

		controller.fillDataFromSwipecard = function fillDataFromSwipecard(cardData)
		{
			controller.displayMessages.clear();

			if (!Juno.Common.Util.isBlank(cardData.province))
			{
				controller.page.demo.address.province = cardData.province;
				controller.page.demo.hcType = cardData.province;
				controller.displayMessages.add_field_warning('province', "Province Changed");
				controller.displayMessages.add_field_warning('hcType', "Health Card Type Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.lastName))
			{
				controller.page.demo.lastName = cardData.lastName;
				controller.displayMessages.add_field_warning('lastName', "Last Name Changed");
				controller.formatLastName();
			}
			if (!Juno.Common.Util.isBlank(cardData.firstName))
			{
				controller.page.demo.firstName = cardData.firstName;
				controller.displayMessages.add_field_warning('firstName', "First Name Changed");
				controller.formatFirstName();
			}
			if (!Juno.Common.Util.isBlank(cardData.hin))
			{
				controller.page.demo.healthNumber = cardData.hin;
				controller.displayMessages.add_field_warning('hin', "HIN Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.versionCode))
			{
				controller.page.demo.healthNumberVersion = cardData.versionCode;
				controller.displayMessages.add_field_warning('ver', "Version Code Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.sex))
			{
				controller.page.demo.sex = cardData.sex;
				controller.displayMessages.add_field_warning('sex', "Sex Changed");
			}
			// @ts-ignore
			if (Oscar.HealthCardParser.validateDate(cardData.dobYear, cardData.dobMonth, cardData.dobDay))
			{
				controller.page.demo.dateOfBirth = Juno.Common.Util.getDateMomentFromComponents(cardData.dobYear, cardData.dobMonth, cardData.dobDay)
				controller.displayMessages.add_field_warning('dob', "Date of Birth Changed");
			}
			// @ts-ignore
			if (Oscar.HealthCardParser.validateDate(cardData.effYear, cardData.effMonth, cardData.effDay))
			{
				controller.page.demo.healthNumberEffectiveDate = Juno.Common.Util.formatMomentDate(
					Juno.Common.Util.getDateMomentFromComponents(cardData.effYear, cardData.effMonth, cardData.effDay));
				controller.displayMessages.add_field_warning('effDate', "Effective Date Changed");
			}
			// @ts-ignore
			if (Oscar.HealthCardParser.validateDate(cardData.endYear, cardData.endMonth, cardData.endDay))
			{
				let expireDate = Juno.Common.Util.getDateMomentFromComponents(cardData.endYear, cardData.endMonth, cardData.endDay);

				controller.page.demo.healthNumberRenewDate = Juno.Common.Util.formatMomentDate(expireDate);
				controller.displayMessages.add_field_warning('endDate', "Hin End Date Changed");

				let now = moment();
				if(now.isAfter(expireDate))
				{
					controller.displayMessages.add_field_warning('endDate', "Health Card Expired");
				}
			}

			if (!Juno.Common.Util.isBlank(cardData.address))
			{
				controller.page.demo.address.addressLine1 = cardData.address;
				controller.displayMessages.add_field_warning('address', "Address Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.city))
			{
				controller.page.demo.address.city = cardData.city;
				controller.displayMessages.add_field_warning('city', "City Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.postal))
			{
				controller.page.demo.address.postalCode = cardData.postal;
				controller.displayMessages.add_field_warning('postal', "Postal Code Changed");
			}
		};

		//HCValidation
		controller.validateHC = function validateHC()
		{
			controller.displayMessages.remove_field_error('hin');

			if (controller.page.demo.healthNumberCountryCode != "ON" || controller.page.demo.healthNumber == null || controller.page.demo.healthNumber == "") return;
			if (controller.page.demo.healthNumberVersion == null) controller.page.demo.healthNumberVersion = "";
			{
				patientDetailStatusService.validateHC(
					controller.page.demo.healthNumber,
					controller.page.demo.healthNumberVersion).then(
					function success(results)
					{
						if (results.valid == null)
						{
							controller.page.HCValidation = "n/a";
							controller.page.swipecardMsg = "Done Health Card Action";
						}
						else
						{
							controller.page.HCValidation = results.valid ? "valid" : "invalid";
							controller.page.swipecardMsg = results.responseDescription + " (" + results.responseCode + ")";
						}

						if (!results.valid)
						{
							controller.displayMessages.add_field_error('hin', controller.page.swipecardMsg);
						}
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		//check Patient Status if endDate is entered
		controller.checkPatientStatus = function checkPatientStatus()
		{
			if (controller.page.demo.patientStatus == "AC")
			{
				if (controller.page.demo.dateEnded != null && controller.page.demo.dateEnded.isValid())
				{
					if (moment().isAfter(controller.page.demo.dateEnded))
					{
						alert("Patient Status cannot be Active after End Date.");
						return false;
					}
				}
			}
			return true;
		};

		controller.isPostalComplete = function isPostalComplete(postal, province)
		{
			// If Canadian province is selected, proceed with validation
			if (postal && province && province !== "OT" && province.indexOf("US") !== 0)
			{
				if (controller.isPostalValidCanadian(postal))
				{
					return true;
				}

				controller.resetEditState();
				return false;
			}

			return true;
		};

		controller.isPostalValidCanadian = function isPostalValidCanadian(postalCode)
		{
			const regex = new RegExp(/^[A-Za-z]\d[A-Za-z][ ]?\d[A-Za-z]\d$/); // Match to Canadian postal code standard
			if (regex.test(postalCode))
			{
				return true;
			}
			else
			{
				Juno.Common.Util.errorAlert($uibModal, "Validation", "Invalid/Incomplete Postal Code");
				return false;
			}
		};

		//check Chart No (length)
		controller.checkChartNo = function checkChartNo()
		{
			if (controller.page.demo.chartNumber == null || controller.page.demo.chartNumber === "")
			{
				chartNo0 = controller.page.demo.chartNumber;
				return;
			}
			if (controller.page.demo.chartNumber.length > 10) controller.page.demo.chartNumber = chartNo0;
			else chartNo0 = controller.page.demo.chartNumber;
		};

		//check Family Doctor No
		controller.checkFamilyDocNo = function checkFamilyDocNo()
		{
			var isValid = controller.validateDocNo(controller.page.demo.scrFamilyDocNo, true);
			if (isValid)
				familyDocNo0 = controller.page.demo.scrFamilyDocNo;
			else
				controller.page.demo.scrFamilyDocNo = familyDocNo0;
		};

		controller.validateDocNo = function validateDocNo(docNo, quiet)
		{
			if (docNo == null || docNo === "" || (isNumber(docNo) && docNo.length < 10))
				return true;

			if (!quiet)
				alert("Invalid Doctor Number");

			return false;
		};

		//check SIN
		controller.checkSin = function checkSin()
		{
			if (controller.page.demo.sin == null || controller.page.demo.sin === "")
			{
				sin0 = controller.page.demo.sin;
				return;
			}

			var sin = controller.page.demo.sin.replace(/\s/g, "");
			if (!isNumber(sin) || sin.length > 9)
			{
				controller.page.demo.sin = sin0;
			}
			else
			{
				if (sin.length > 6)
				{
					controller.page.demo.sin = sin.substring(0, 3) + " " + sin.substring(3, 6) + " " + sin.substring(6);
				}
				else if (sin.length > 3)
				{
					controller.page.demo.sin = sin.substring(0, 3) + " " + sin.substring(3);
				}
				sin0 = controller.page.demo.sin;
			}
		};

		controller.validateSin = function validateSin()
		{
			if (controller.page.demo.sin == null || controller.page.demo.sin === "") return true;

			var sin = controller.page.demo.sin.replace(/\s/g, "");
			if (isNumber(sin) && sin.length === 9)
			{
				var sinNumber = 0;
				for (var i = 0; i < sin.length; i++)
				{
					var n = Number(sin.charAt(i)) * (i % 2 + 1);
					sinNumber += n % 10 + Math.floor(n / 10);
				}
				if (sinNumber % 10 === 0) return true;
			}
			alert("Invalid SIN #");
			return false;
		};

		//show/hide items
		controller.chooseFamilyDoc = function chooseFamilyDoc(item, model, label)
		{
			controller.page.demo.scrFamilyDocNo = item.referralNo;
			controller.page.demo.scrFamilyDoc = item.name;
			controller.checkFamilyDocNo();
		};

		//upload photo
		controller.launchPhoto = function launchPhoto()
		{
			var url = "../casemgmt/uploadimage.jsp?demographicNo=" + controller.page.demo.id;
			window.open(url, "uploadWin", "width=500, height=300");
		};

		//manage contacts
		controller.manageContacts = function manageContacts()
		{
			var discard = true;
			if (controller.page.dataChanged > 0)
			{
				discard = confirm("You may have unsaved data. Are you sure to leave?");
			}
			if (discard)
			{
				var url = "../demographic/Contact.do?method=manage&demographic_no=" + controller.page.demo.id;
				window.open(url, "ManageContacts");
			}
		};

		//print buttons
		controller.printLabel = function printLabel(label)
		{
			var url = null;
			if (label === "PDFLabel") url = "../demographic/printDemoLabelAction.do?appointment_no=null&demographic_no=" + controller.page.demo.id;
			else if (label === "PDFAddress") url = "../demographic/printDemoAddressLabelAction.do?demographic_no=" + controller.page.demo.id;
			else if (label === "PDFChart") url = "../demographic/printDemoChartLabelAction.do?demographic_no=" + controller.page.demo.id;
			else if (label === "PrintLabel") url = "../demographic/demographiclabelprintsetting.jsp?demographic_no=" + controller.page.demo.id;
			else if (label === "ClientLab") url = "../demographic/printClientLabLabelAction.do?demographic_no=" + controller.page.demo.id;
			window.open(url, "Print", "width=960, height=700");
		};

		//integrator buttons
		controller.integratorDo = function integratorDo(func)
		{
			var url = null;
			if (func == "ViewCommunity") url = "../admin/viewIntegratedCommunity.jsp";
			else if (func == "Linking") url = "../integrator/manage_linked_clients.jsp?demographicId=" + controller.page.demo.id;
			else if (func == "Compare") url = "../demographic/DiffRemoteDemographics.jsp?demographicId=" + controller.page.demo.id;
			else if (func == "Update") url = "../demographic/copyLinkedDemographicInfoAction.jsp?displaymode=edit&dboperation=search_detail&demographicId=" + controller.page.demo.id + "&demographic_no=" + controller.page.demo.id;
			else if (func == "SendNote") url = "../demographic/followUpSelection.jsp?demographicId=" + controller.page.demo.id;
			window.open(url, "Integrator", "width=960, height=700");
		};

		//MacPHR buttons
		controller.macPHRDo = function macPHRDo(func)
		{
			var url = null;
			if (func === "Register")
			{
				if (!controller.page.macPHRLoggedIn)
				{
					alert("Please login to PHR first");
					return;
				}
				url = "../phr/indivo/RegisterIndivo.jsp?demographicNo=" + controller.page.demo.id;
			}
			else if (func === "SendMessage")
			{
				url = "../phr/PhrMessage.do?method=createMessage&providerNo=" + user.providerNo + "&demographicNo=" + controller.page.demo.id;
			}
			else if (func === "ViewRecord")
			{
				url = "../demographic/viewPhrRecord.do?demographic_no=" + controller.page.demo.id;
			}
			else if (func === "Verification")
			{
				url = "../phr/PHRVerification.jsp?demographic_no=" + controller.page.demo.id;
			}
			window.open(url, "MacPHR", "width=960, height=700");
		};

		//appointment buttons
		controller.appointmentDo = function appointmentDo(func)
		{
			var url = null;
			if (func === "ApptHistory") url = "../demographic/demographiccontrol.jsp?displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=25&orderby=appttime&demographic_no=" + controller.page.demo.id + "&last_name=" + encodeURI(controller.page.demo.lastName) + "&first_name=" + encodeURI(controller.page.demo.firstName);
			else if (func === "WaitingList") url = "../oscarWaitingList/SetupDisplayPatientWaitingList.do?demographic_no=" + controller.page.demo.id;
			window.open(url, "Appointment", "width=960, height=700");
		};

		controller.isClinicaidBilling = function isClinicaidBilling()
		{
			return controller.page.billregion === "CLINICAID";
		};

		//billing buttons
		controller.billingDo = function billingDo(func)
		{
			var now = new Date();
			var url = null;
			if (func === "BillingHistory")
			{
				if (controller.page.billregion === BILLING_REGION.CLINICAID)
				{
					url = "../billing.do?billRegion=CLINICAID&action=invoice_reports&patient_remote_id=" + controller.page.demo.id;
				}
				else if (controller.page.billregion === BILLING_REGION.ON)
				{
					url = "../billing/CA/ON/billinghistory.jsp?demographic_no=" + controller.page.demo.id +
						"&last_name=" + encodeURI(controller.page.demo.lastName) +
						"&first_name=" + encodeURI(controller.page.demo.firstName) +
						"&orderby=appointment_date&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=10";
				}
				else
				{
					url = "../billing/CA/BC/billStatus.jsp?lastName=" + encodeURI(controller.page.demo.lastName) +
						"&firstName=" + encodeURI(controller.page.demo.firstName) +
						"&filterPatient=true&demographicNo=" + controller.page.demo.id;
				}
			}
			else if (func === "CreateInvoice")
			{
				url = "../billing.do?billRegion=" + controller.page.billregion + "&billForm=" + controller.page.defaultView +
					"&hotclick=&appointment_no=0&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) +
					"&demographic_no=" + controller.page.demo.id +
					"&providerview=" + controller.page.demo.providerNo +
					"&user_no=" + user.providerNo +
					"&apptProvider_no=none&appointment_date=" + now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() +
					"&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "FluBilling")
			{
				url = "../billing/CA/ON/specialtyBilling/fluBilling/addFluBilling.jsp?function=demographic&functionid=" + controller.page.demo.id +
					"&creator=" + user.providerNo +
					"&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&demo_sex=" + controller.page.demo.sex +
					"&demo_hctype=" + controller.page.demo.healthNumberProvinceCode +
					"&rd=" + encodeURI(controller.page.demo.scrReferralDoc) +
					"&rdohip=" + controller.page.demo.scrReferralDocNo +
					"&dob=" + encodeURI(Juno.Common.Util.formatMomentDate(controller.page.demo.dateOfBirth)) +
					"&mrp=" + controller.page.demo.providerNo;
			}
			else if (func === "HospitalBilling")
			{
				url = "../billing/CA/ON/billingShortcutPg1.jsp?billRegion=" + controller.page.billregion +
					"&billForm=" + encodeURI(controller.page.hospitalView) +
					"&hotclick=&appointment_no=0&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) +
					"&demographic_no=" + controller.page.demo.id +
					"&providerview=" + controller.page.demo.providerNo +
					"&user_no=" + user.providerNo +
					"&apptProvider_no=none&appointment_date=" + now.getFullYear + "-" + (now.getMonth() + 1) + "-" + now.getDate() +
					"&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "AddBatchBilling")
			{
				url = "../billing/CA/ON/addBatchBilling.jsp?demographic_no=" + controller.page.demo.id +
					"&creator=" + user.providerNo +
					"&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&dob=" + controller.page.demo.dobYear + controller.page.demo.dobMonth + controller.page.demo.dobDay;
			}
			else if (func === "AddINR")
			{
				url = "../billing/CA/ON/inr/addINRbilling.jsp?function=demographic&functionid=" + controller.page.demo.id +
					"&creator=" + user.providerNo +
					"&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&dob=" + encodeURI(Juno.Common.Util.formatMomentDate(controller.page.demo.dateOfBirth));
			}
			else if (func === "BillINR")
			{
				url = "../billing/CA/ON/inr/reportINR.jsp?provider_no=" + user.providerNo;
			}
			window.open(url, "Billing", "width=960, height=700");
		};

		//export demographic
		controller.exportDemographic = function exportDemographic()
		{
			var url = "../demographic/demographicExport.jsp?demographicNo=" + controller.page.demo.id;
			window.open(url, "DemographicExport", "width=960, height=700");
		};

 		controller.formatFamilyDocXMLToJSON = (familyDocXML) =>
		{
		    let number = "";
		    let name = "";

		    let begin = familyDocXML.indexOf("<fd>") + "<fd>".length;
		    let end = familyDocXML.indexOf("</fd>");

		    if (end > begin && end >= 0 && begin >= 0)
		    {
                number = familyDocXML.substring(begin, end);
            }

		    begin = familyDocXML.indexOf("<fdname>") + "<fdname>".length;
		    end = familyDocXML.indexOf("</fdname>");

		    if (end > begin && end >= 0 && begin >= 0)
            {
                name = familyDocXML.substring(begin, end)
            }

            return {number: number, name: name};
        };

        controller.formatReferralDocXMLToJSON = (referralDocXML) =>
        {
            let number = "";
            let name = "";

            let begin = referralDocXML.indexOf("<rdohip>") + "<rdohip>".length;
            let end = referralDocXML.indexOf("</rdohip>");

            if (end > begin && end >= 0 && begin >= 0)
            {
                number = referralDocXML.substring(begin, end);
            }

            begin = referralDocXML.indexOf("<rd>") + "<rd>".length;
            end = referralDocXML.indexOf("</rd>");

            if (end > begin && end >= 0 && begin >= 0)
            {
                name = referralDocXML.substring(begin, end)
            }

            return {number: number, name: name};
        };

		/**
		 * Wrap the familyDoctor field in required HTML tags.
		 * @param name name of the referral doctor
		 * @param number referral doctor #
		 * @return {string} name and referral # wrapped appropriately
		 */
		controller.formatDocInput = function formatDocInput(name, number)
		{
			let docNo = "<rdohip></rdohip>";
			let doc = "<rd></rd>";
			if (Juno.Common.Util.exists(number))
			{
				docNo = "<rdohip>" + number + "</rdohip>";
			}
			if (Juno.Common.Util.exists(name))
			{
				doc = "<rd>" + name + "</rd>";
			}
			return docNo + doc;
		};

		/**
		 * Slightly different from formatDocInput as internally the two expect different wrapping HTML tags.
		 * @param name name of the doctor
		 * @param number doctor's referral #
		 * @return {string} name and referral # for the doctor wrapped in the appropriate <fd / > & <fname /> tags
		 */
		controller.formatFamilyDocInput = function formatFamilyDocInput(name, number)
		{
			let docNo = "<fd></fd>";
			let doc = "<fdname></fdname>";

			if (Juno.Common.Util.exists(number))
			{
				docNo = "<fd>" + number + "</fd>";
			}

			if (Juno.Common.Util.exists(name))
			{
				doc = "<fdname>" + name + "</fdname>";
			}

			return docNo + doc;

		};

		//HCValidation on open & save
		controller.validateHCSave = function validateHCSave(doSave)
		{
			if ((controller.page.demo.healthNumber == null || controller.page.demo.healthNumber === "") && doSave)
			{
				controller.save();
			}
			else
			{
				let hin = controller.page.demo.healthNumber;
				let ver = controller.page.demo.healthNumberVersion;
				let hcType = controller.page.demo.healthNumberProvinceCode;
				let demographicNo = controller.page.demo.id;
				patientDetailStatusService.isUniqueHC(hin, ver, hcType, demographicNo).then(
					function success(results)
					{
						if (!results.success)
						{
							alert("HIN is already in use!");
						}
						else if (doSave)
						{
							controller.save();
						}
					},
					function error(errors)
					{
						console.log(errors);
					});
			}
		};

		//-----------------//
		// save operations //
		//-----------------//
		controller.save = function save()
		{
			if (!Juno.Validations.allValidationsValid(controller.validations))
			{
				alert("Some fields are invalid, Please correct the highlighted fields");
				return;
			}

			controller.page.saving = true;
			controller.displayMessages.clear();

			//check required fields
			if (controller.page.demo.lastName == null || controller.page.demo.lastName == "")
			{
				alert("Last Name is required");
				return;
			}
			else if (controller.page.demo.firstName == null || controller.page.demo.firstName == "")
			{
				alert("First Name is required");
				return;
			}
			else if (controller.page.demo.sex == null || controller.page.demo.sex == "")
			{
				alert("Sex is required");
				return;
			}

			//validate field inputs
			else if (controller.page.demo.dateOfBirth == null)
			{
				alert("Invalid Date of Birth");
				return;
			}
			else if (!controller.page.demo.dateOfBirth.isValid())
			{
				alert("Date of Birth is required");
				return;
			}

			if (!controller.checkPatientStatus()) return;
			// if (!controller.isPostalComplete(controller.page.demo.address.postal, controller.page.demo.address.province)) return;
			// if (!controller.isPostalComplete(controller.page.demo.address2.postal, controller.page.demo.address2.province)) return;
			// if (!controller.validateDocNo(controller.page.demo.scrReferralDocNo)) return;
			// if (!controller.validateDocNo(controller.page.demo.scrFamilyDocNo)) return;
			//todo

			if (Juno.Common.Util.exists(controller.page.demo.healthNumber))
            {
                controller.page.demo.healthNumber = controller.page.demo.healthNumber.replace(/[\W_]/gi, '');
            }

			//save notes
			// if (controller.page.demo.patientNote != null)
			// {
			// 	controller.page.demo.patientNote = "<unotes>" + controller.page.demo.patientNote + "</unotes>";
			// }

			//save referral doctor (familyDoctor)
			controller.page.demo.familyDoctor = controller.formatDocInput(controller.page.demo.scrReferralDoc, controller.page.demo.scrReferralDocNo);

			//save family doctor (familyDoctor2)
			controller.page.demo.familyDoctor2 = controller.formatFamilyDocInput(controller.page.demo.scrFamilyDoc, controller.page.demo.scrFamilyDocNo);

			//save phone numbers
			// controller.page.demo.scrDemoCell = controller.page.demo.scrCellPhone;
			// controller.page.demo.phone = controller.page.demo.scrHomePhone;
			// controller.page.demo.alternativePhone = controller.page.demo.scrWorkPhone;

			// if (controller.page.demo.scrPreferredPhone == "C") controller.page.demo.scrDemoCell += "*";
			// else if (controller.page.demo.scrPreferredPhone == "H") controller.page.demo.phone += "*";
			// else if (controller.page.demo.scrPreferredPhone == "W") controller.page.demo.alternativePhone += "*";

			//save extras
			// var newDemoExtras = [];
			// newDemoExtras = updateDemoExtras("demo_cell", controller.page.demo.scrDemoCell, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("aboriginal", controller.page.demo.scrAboriginal, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("hPhoneExt", controller.page.demo.scrHPhoneExt, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("wPhoneExt", controller.page.demo.scrWPhoneExt, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("cytolNum", controller.page.demo.scrCytolNum, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("phoneComment", controller.page.demo.scrPhoneComment, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("paper_chart_archived", controller.page.demo.scrPaperChartArchived, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("paper_chart_archived_date", controller.page.demo.scrPaperChartArchivedDate, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("usSigned", controller.page.demo.scrUsSigned, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("privacyConsent", controller.page.demo.scrPrivacyConsent, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("informedConsent", controller.page.demo.scrInformedConsent, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("securityQuestion1", controller.page.demo.scrSecurityQuestion1, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("securityAnswer1", controller.page.demo.scrSecurityAnswer1, posExtras, controller.page.demo.extras, newDemoExtras);
			// newDemoExtras = updateDemoExtras("rxInteractionWarningLevel", controller.page.demo.scrRxInteractionLevel, posExtras, controller.page.demo.extras, newDemoExtras);
			// controller.page.demo.extras = newDemoExtras;

			console.debug("pre-save", controller.page.demo);

			//save to database
			demographicService.updateDemographic(controller.page.demo).then(
				function success()
				{
					controller.resetEditState();
				},

				function error(e)
				{
					controller.page.saving = false;
					controller.toastService.errorToast('Failed to save demographic');
					console.error(e);
				}
			);
		};

		controller.resetEditState = function resetEditState()
		{
			controller.page.saving = false;
			controller.page.dataChanged = false;
		};

		controller.pageClasses = () =>
		{
			if ($scope.pageStyle === JUNO_STYLE.DRACULA)
			{
				return ["juno-style-dracula-background"]
			}
		}

		controller.setStyle = (style) =>
		{
			$scope.pageStyle = style;
		}
	}
]);


// Move these?
// function updateDemoExtras(extKey, newVal, posExtras, oldExtras, newExtras)
// {
// 	if (newVal == null) return newExtras;
//
// 	var pos = posExtras[extKey];
// 	if (pos != null && oldExtras[pos] != null)
// 	{ //existing ext
// 		if (oldExtras[pos].value != newVal)
// 		{
// 			newExtras.push(
// 			{
// 				id: oldExtras[pos].id,
// 				key: extKey,
// 				value: newVal,
// 				hidden: oldExtras[pos].hidden
// 			});
// 		}
// 	}
// 	else
// 	{ //newly added ext
// 		newExtras.push(
// 		{
// 			key: extKey,
// 			value: newVal
// 		});
// 	}
// 	return newExtras;
// }

// function dateValid(dateStr)
// { //valid date format: yyyy-MM-dd
// 	if (dateStr == null || dateStr == "") return true;
//
// 	var datePart = dateStr.toString().split("-");
// 	if (datePart.length != 3) return false;
//
// 	var dateDate = new Date(datePart[0], datePart[1] - 1, datePart[2]);
// 	if (isNaN(dateDate.getTime())) return false;
//
// 	if (dateDate.getFullYear() != datePart[0]) return false;
// 	if (dateDate.getMonth() != datePart[1] - 1) return false;
// 	if (dateDate.getDate() != datePart[2]) return false;
//
// 	return true;
// }

function isNumber(s)
{
	return /^[0-9]+$/.test(s);
}

// function isPreferredPhone(phone)
// {
// 	phone = String(phone);
// 	if (phone != null && phone != "")
// 	{
// 		if (phone.charAt(phone.length - 1) == "*") return true;
// 	}
// 	return false;
// }

// function getPhoneNum(phone)
// {
// 	if (isPreferredPhone(phone))
// 	{
// 		phone = phone.substring(0, phone.length - 1);
// 	}
// 	return phone;
// }

// function toArray(obj)
// { //convert single object to array
// 	if (obj instanceof Array) return obj;
// 	else if (obj == null) return [];
// 	else return [obj];
// }
