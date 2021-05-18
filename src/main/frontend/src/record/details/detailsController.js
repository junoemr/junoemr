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

import {INSTANCE_TYPE, SYSTEM_PROPERTIES, BILLING_TYPE} from "../../common/services/systemPreferenceServiceConstants";
import {ProvidersServiceApi, SystemPreferenceApi} from "../../../generated";
import {JUNO_STYLE} from "../../common/components/junoComponentConstants";
import {BILLING_REGION} from "../../billing/billingConstants";

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
	'securityService',
	'staticDataService',
	'referralDoctorsService',
	'user',

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
		securityService,
		staticDataService,
		referralDoctorsService,
		user)
	{

		var controller = this;
		controller.page = {};

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
		controller.properties = $scope.$parent.recordCtrl.properties;
		controller.displayMessages = messagesFactory.factory();
		controller.validations = {};

		$scope.JUNO_STYLE = JUNO_STYLE;
		$scope.pageStyle = JUNO_STYLE.GREY;

		controller.init = function init()
		{
			demographicService.getDemographic($stateParams.demographicNo).then(
				function success(results)
				{
					controller.page.demo = results;
					controller.initDemographicVars();
					controller.checkAccess();

					// retrieve provider types for dropdown selection
					//TODO - are roles determined by security role or provider type?
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

					// retrieve contact lists for demographic
					demographicService.getDemographicContacts(controller.page.demo.demographicNo, "personal").then(
						function success(data) {
							controller.page.demoContacts = demoContactShow(data);
						}
					);
					demographicService.getDemographicContacts(controller.page.demo.demographicNo, "professional").then(
						function success(data) {
							controller.page.demoContactPros = demoContactShow(data);
						}
					);

					//show notes
					if (controller.page.demo.notes != null)
					{
						controller.page.demo.scrNotes = controller.page.demo.notes;
						if (/^<unotes>[\s\S]*/.test(controller.page.demo.scrNotes)) controller.page.demo.scrNotes = controller.page.demo.scrNotes.substring("<unotes>".length);
						if (/[\s\S]*<\/unotes>$/.test(controller.page.demo.scrNotes)) controller.page.demo.scrNotes = controller.page.demo.scrNotes.substring(0, controller.page.demo.scrNotes.lastIndexOf("</unotes>"));
					}

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

					if (controller.page.demo.extras != null)
					{
						controller.page.demo.extras = toArray(controller.page.demo.extras);
						for (var i = 0; i < controller.page.demo.extras.length; i++)
						{
							if (controller.page.demo.extras[i].key == "demo_cell") controller.page.demo.scrDemoCell = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "aboriginal") controller.page.demo.scrAboriginal = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "hPhoneExt") controller.page.demo.scrHPhoneExt = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "wPhoneExt") controller.page.demo.scrWPhoneExt = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "cytolNum") controller.page.demo.scrCytolNum = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "phoneComment") controller.page.demo.scrPhoneComment = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "paper_chart_archived") controller.page.demo.scrPaperChartArchived = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "paper_chart_archived_date") controller.page.demo.scrPaperChartArchivedDate = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "usSigned") controller.page.demo.scrUsSigned = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "privacyConsent") controller.page.demo.scrPrivacyConsent = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "informedConsent") controller.page.demo.scrInformedConsent = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "securityQuestion1") controller.page.demo.scrSecurityQuestion1 = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "securityAnswer1") controller.page.demo.scrSecurityAnswer1 = controller.page.demo.extras[i].value;
							else if (controller.page.demo.extras[i].key == "rxInteractionWarningLevel") controller.page.demo.scrRxInteractionLevel = controller.page.demo.extras[i].value;


							//record array position of extras by keys - to be used on saving
							posExtras[controller.page.demo.extras[i].key] = i;
						}
					}

					//show phone numbers with preferred check
					controller.page.demo.scrCellPhone = getPhoneNum(controller.page.demo.scrDemoCell);
					controller.page.demo.scrHomePhone = getPhoneNum(controller.page.demo.phone);
					controller.page.demo.scrWorkPhone = getPhoneNum(controller.page.demo.alternativePhone);

					//show waitingListNames
					if (controller.page.demo.waitingListNames != null)
					{
						if (controller.page.demo.waitingListNames.id != null)
						{ //only 1 entry, convert to array
							var tmp = {};
							tmp.id = controller.page.demo.waitingListNames.id;
							tmp.name = controller.page.demo.waitingListNames.name;
							tmp.groupNo = controller.page.demo.waitingListNames.groupNo;
							tmp.providerNo = controller.page.demo.waitingListNames.providerNo;
							tmp.createDate = controller.page.demo.waitingListNames.createDate;
							tmp.isHistory = controller.page.demo.waitingListNames.isHistory;
							controller.page.demo.waitingListNames = [tmp];
						}
					}


					controller.page.cellPhonePreferredMsg = defPhTitle;
					controller.page.homePhonePreferredMsg = defPhTitle;
					controller.page.workPhonePreferredMsg = defPhTitle;
					if (isPreferredPhone(controller.page.demo.scrDemoCell))
					{
						controller.page.demo.scrPreferredPhone = "C";
						controller.page.preferredPhoneNumber = controller.page.demo.scrCellPhone;
						controller.page.cellPhonePreferredMsg = prefPhTitle;
						controller.page.cellPhonePreferredColor = colorAttn;
					}
					else if (isPreferredPhone(controller.page.demo.phone))
					{
						controller.page.demo.scrPreferredPhone = "H";
						controller.page.preferredPhoneNumber = controller.page.demo.scrHomePhone;
						controller.page.homePhonePreferredMsg = prefPhTitle;
						controller.page.homePhonePreferredColor = colorAttn;
					}
					else if (isPreferredPhone(controller.page.demo.alternativePhone))
					{
						controller.page.demo.scrPreferredPhone = "W";
						controller.page.preferredPhoneNumber = controller.page.demo.scrWorkPhone;
						controller.page.workPhonePreferredMsg = prefPhTitle;
						controller.page.workPhonePreferredColor = colorAttn;
					}
					else
					{
						controller.page.preferredPhoneNumber = controller.page.demo.scrHomePhone;
					}

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

					controller.page.demo.age = Juno.Common.Util.calcAge(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay);
					controller.formatLastName(); //done on page load
					controller.formatFirstName(); //done on page load
				},
				function error(errors)
				{
					alert('Error loading demographic: ', errors) // TODO: Display actual error message
				}
			);

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

		};

		controller.initDemographicVars = function initDemographicVars()
		{
			var effDateMoment = moment(controller.page.demo.effDate);
			if(effDateMoment.isValid())
			{
				controller.page.demo.effDate = Juno.Common.Util.formatMomentDate(effDateMoment);
			}
			else
			{
				controller.page.demo.effDate = null;
			}
			var hcRenewDateMoment = moment(controller.page.demo.hcRenewDate);
			if(hcRenewDateMoment.isValid())
			{
				controller.page.demo.hcRenewDate = Juno.Common.Util.formatMomentDate(hcRenewDateMoment);
			}
			else
			{
				controller.page.demo.hcRenewDate = null;
			}

			// convert dates to moment
			controller.page.demo.dateOfBirth = Juno.Common.Util.getDateMomentFromComponents(controller.page.demo.dobYear,
					controller.page.demo.dobMonth, controller.page.demo.dobDay);
			controller.page.demo.effDate = Juno.Common.Util.getDateMoment(controller.page.demo.effDate);
			controller.page.demo.hcRenewDate = Juno.Common.Util.getDateMoment(controller.page.demo.hcRenewDate);
			controller.page.demo.rosterDate = Juno.Common.Util.getDateMoment(controller.page.demo.rosterDate);
			controller.page.demo.rosterTerminationDate = Juno.Common.Util.getDateMoment(controller.page.demo.rosterTerminationDate);
			controller.page.demo.dateJoined = moment(controller.page.demo.dateJoined);
			controller.page.demo.patientStatusDate = moment(controller.page.demo.patientStatusDate);
			controller.page.demo.endDate = Juno.Common.Util.getDateMoment(controller.page.demo.endDate);
			if (controller.page.demo.onWaitingListSinceDate)
			{
				controller.page.demo.onWaitingListSinceDate = Juno.Common.Util.getDateMomentFromComponents(controller.page.demo.onWaitingListSinceDate.getFullYear(),
						controller.page.demo.onWaitingListSinceDate.getMonth(), controller.page.demo.onWaitingListSinceDate.getDate());
			}

			// oscar stores no country of origin as "-1" because why not.
			if (controller.page.demo.countryOfOrigin === "-1")
			{
				controller.page.demo.countryOfOrigin = null;
			}

			phoneNum["C"] = controller.page.demo.scrCellPhone;
			phoneNum["H"] = controller.page.demo.scrHomePhone;
			phoneNum["W"] = controller.page.demo.scrWorkPhone;
			phoneNum["HX"] = controller.page.demo.scrHPhoneExt;
			phoneNum["WX"] = controller.page.demo.scrWPhoneExt;
			hin0 = controller.page.demo.hin;
			ver0 = controller.page.demo.ver;
			chartNo0 = controller.page.demo.chartNo;
			cytolNum0 = controller.page.demo.scrCytolNum;
			referralDocNo0 = controller.page.demo.scrReferralDocNo;
			sin0 = controller.page.demo.sin;
			effDate0 = controller.page.demo.effDate;
			hcRenewDate0 = controller.page.demo.hcRenewDate;
			rosterDate0 = controller.page.demo.rosterDate;
			rosterTerminationDate0 = controller.page.demo.rosterTerminationDate;
			patientStatusDate0 = controller.page.demo.patientStatusDate;
			dateJoined0 = controller.page.demo.dateJoined;
			endDate0 = controller.page.demo.endDate;
			onWaitingListSinceDate0 = controller.page.demo.onWaitingListSinceDate;
			paperChartArchivedDate0 = controller.page.demo.scrPaperChartArchivedDate;
		};

		controller.checkAccess = function checkAccess()
		{
			//get access rights
			securityService.hasRight("_demographic", "r", controller.page.demo.demographicNo).then(
				function success(results)
				{
					controller.page.canRead = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
			securityService.hasRight("_demographic", "u", controller.page.demo.demographicNo).then(
				function success(results)
				{
					controller.page.cannotChange = !results;
				},
				function error(errors)
				{
					console.log(errors);
				});

		};

		//disable click and keypress if user only has read-access
		controller.checkAction = function checkAction(event)
		{
			if (controller.page.cannotChange)
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
				} else {
					demographicService.getDemographic($stateParams.demographicNo).then(
						function success(results)
						{
							// TODO: Celebrate
						},
						function error(errors)
						{
							alert('Error loading demographic: ', errors) // TODO: Display actual error message
						}
					);
				
				}
			}
		});

		//format lastname, firstname
		controller.formatLastName = function formatLastName()
		{
			controller.page.demo.lastName = controller.page.demo.lastName.toUpperCase();
		};
		controller.formatFirstName = function formatFirstName()
		{
			controller.page.demo.firstName = controller.page.demo.firstName.toUpperCase();
		};

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
			patientDetailStatusService.getEligibilityInfo(controller.page.demo.demographicNo).then(
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
				controller.page.demo.hin = cardData.hin;
				controller.displayMessages.add_field_warning('hin', "HIN Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.versionCode))
			{
				controller.page.demo.ver = cardData.versionCode;
				controller.displayMessages.add_field_warning('ver', "Version Code Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.sex))
			{
				controller.page.demo.sex = cardData.sex;
				controller.displayMessages.add_field_warning('sex', "Sex Changed");
			}
			if (Oscar.HealthCardParser.validateDate(cardData.dobYear, cardData.dobMonth, cardData.dobDay))
			{
				controller.page.demo.dobYear = cardData.dobYear;
				controller.page.demo.dobMonth = cardData.dobMonth;
				controller.page.demo.dobDay = cardData.dobDay;
				controller.displayMessages.add_field_warning('dob', "Date of Birth Changed");
			}
			if (Oscar.HealthCardParser.validateDate(cardData.effYear, cardData.effMonth, cardData.effDay))
			{
				controller.page.demo.effDate = Juno.Common.Util.formatMomentDate(
					Juno.Common.Util.getDateMomentFromComponents(cardData.effYear, cardData.effMonth, cardData.effDay));
				controller.displayMessages.add_field_warning('effDate', "Effective Date Changed");
			}
			if (Oscar.HealthCardParser.validateDate(cardData.endYear, cardData.endMonth, cardData.endDay))
			{
				var expireDate = Juno.Common.Util.getDateMomentFromComponents(cardData.endYear, cardData.endMonth, cardData.endDay);

				controller.page.demo.hcRenewDate = Juno.Common.Util.formatMomentDate(expireDate);
				controller.displayMessages.add_field_warning('endDate', "Hin End Date Changed");

				var now = moment();
				if(now.isAfter(expireDate))
				{
					controller.displayMessages.add_field_warning('endDate', "Health Card Expired");
				}
			}

			if (!Juno.Common.Util.isBlank(cardData.address))
			{
				controller.page.demo.address.address = cardData.address;
				controller.displayMessages.add_field_warning('address', "Address Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.city))
			{
				controller.page.demo.address.city = cardData.city;
				controller.displayMessages.add_field_warning('city', "City Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.postal))
			{
				controller.page.demo.address.postal = cardData.postal;
				controller.displayMessages.add_field_warning('postal', "Postal Code Changed");
			}
		};

		//HCValidation
		controller.validateHC = function validateHC()
		{
			controller.displayMessages.remove_field_error('hin');

			if (controller.page.demo.hcType != "ON" || controller.page.demo.hin == null || controller.page.demo.hin == "") return;
			if (controller.page.demo.ver == null) controller.page.demo.ver = "";
			patientDetailStatusService.validateHC(controller.page.demo.hin, controller.page.demo.ver).then(
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

					if(!results.valid)
					{
						controller.displayMessages.add_field_error('hin', controller.page.swipecardMsg);
					}
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		//check Patient Status if endDate is entered
		controller.checkPatientStatus = function checkPatientStatus()
		{
			var now = new Date();
			if (controller.page.demo.patientStatus == "AC")
			{
				if (controller.page.demo.endDate != null && controller.page.demo.endDate != "")
				{
					if (dateValid(controller.page.demo.endDate))
					{
						var datePart = controller.page.demo.endDate.split("-");
						var endDate = new Date(datePart[0], datePart[1] - 1, datePart[2]);
						if (now > endDate)
						{
							alert("Patient Status cannot be Active after End Date.");
							return false;
						}
					}
				}
			}
			return true;
		};

		controller.isPostalComplete = function isPostalComplete()
		{
			var province = controller.page.demo.address.province;
			var postal = controller.page.demo.address.postal;
			// If Canadian province is selected, proceed with validation
			if (postal !== null && province !== null && province !== "OT" && province.indexOf("US") !== 0)
			{
				if (controller.isPostalValid())
				{
					return true;
				}

				controller.resetEditState();
				return false;
			}

			return true;
		};

		controller.isPostalValid = function isPostalValid()
		{
			var postal = controller.page.demo.address.postal.replace(/\s/g, ""); // Trim whitespace

			// If postal code is an empty string, set it to null and continue
			if(postal.length === 0)
			{
				controller.page.demo.address.postal = null;
				return true;
			}

			var regex = new RegExp(/^[A-Za-z]\d[A-Za-z]\d[A-Za-z]\d$/); // Match to Canadian postal code standard (minus the space)
			if (regex.test(postal))
			{
				// Format postal code to Canadian standard
				controller.page.demo.address.postal = postal.substring(0, 3) + " " + postal.substring(3);
				return true;
			}else {
				alert("Invalid/Incomplete Postal Code"); // TODO: Display proper error message
				return false;
			}
		};

		//check Chart No (length)
		controller.checkChartNo = function checkChartNo()
		{
			if (controller.page.demo.chartNo == null || controller.page.demo.chartNo == "")
			{
				chartNo0 = controller.page.demo.chartNo;
				return;
			}
			if (controller.page.demo.chartNo.length > 10) controller.page.demo.chartNo = chartNo0;
			else chartNo0 = controller.page.demo.chartNo;
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
			if (controller.page.demo.sin == null || controller.page.demo.sin == "")
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
			if (controller.page.demo.sin == null || controller.page.demo.sin == "") return true;

			var sin = controller.page.demo.sin.replace(/\s/g, "");
			if (isNumber(sin) && sin.length == 9)
			{
				var sinNumber = 0;
				for (var i = 0; i < sin.length; i++)
				{
					var n = Number(sin.charAt(i)) * (i % 2 + 1);
					sinNumber += n % 10 + Math.floor(n / 10);
				}
				if (sinNumber % 10 == 0) return true;
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
			var url = "../casemgmt/uploadimage.jsp?demographicNo=" + controller.page.demo.demographicNo;
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
				var url = "../demographic/Contact.do?method=manage&demographic_no=" + controller.page.demo.demographicNo;
				window.open(url, "ManageContacts", "width=960, height=700");
			}
		};

		//print buttons
		controller.printLabel = function printLabel(label)
		{
			var url = null;
			if (label === "PDFLabel") url = "../demographic/printDemoLabelAction.do?appointment_no=null&demographic_no=" + controller.page.demo.demographicNo;
			else if (label === "PDFAddress") url = "../demographic/printDemoAddressLabelAction.do?demographic_no=" + controller.page.demo.demographicNo;
			else if (label === "PDFChart") url = "../demographic/printDemoChartLabelAction.do?demographic_no=" + controller.page.demo.demographicNo;
			else if (label === "PrintLabel") url = "../demographic/demographiclabelprintsetting.jsp?demographic_no=" + controller.page.demo.demographicNo;
			else if (label === "ClientLab") url = "../demographic/printClientLabLabelAction.do?demographic_no=" + controller.page.demo.demographicNo;
			window.open(url, "Print", "width=960, height=700");
		};

		//integrator buttons
		controller.integratorDo = function integratorDo(func)
		{
			var url = null;
			if (func == "ViewCommunity") url = "../admin/viewIntegratedCommunity.jsp";
			else if (func == "Linking") url = "../integrator/manage_linked_clients.jsp?demographicId=" + controller.page.demo.demographicNo;
			else if (func == "Compare") url = "../demographic/DiffRemoteDemographics.jsp?demographicId=" + controller.page.demo.demographicNo;
			else if (func == "Update") url = "../demographic/copyLinkedDemographicInfoAction.jsp?displaymode=edit&dboperation=search_detail&demographicId=" + controller.page.demo.demographicNo + "&demographic_no=" + controller.page.demo.demographicNo;
			else if (func == "SendNote") url = "../demographic/followUpSelection.jsp?demographicId=" + controller.page.demo.demographicNo;
			window.open(url, "Integrator", "width=960, height=700");
		};

		//MacPHR buttons
		controller.macPHRDo = function macPHRDo(func)
		{
			var url = null;
			if (func == "Register")
			{
				if (!controller.page.macPHRLoggedIn)
				{
					alert("Please login to PHR first");
					return;
				}
				url = "../phr/indivo/RegisterIndivo.jsp?demographicNo=" + controller.page.demo.demographicNo;
			}
			else if (func == "SendMessage")
			{
				url = "../phr/PhrMessage.do?method=createMessage&providerNo=" + user.providerNo + "&demographicNo=" + controller.page.demo.demographicNo;
			}
			else if (func == "ViewRecord")
			{
				url = "../demographic/viewPhrRecord.do?demographic_no=" + controller.page.demo.demographicNo;
			}
			else if (func == "Verification")
			{
				url = "../phr/PHRVerification.jsp?demographic_no=" + controller.page.demo.demographicNo;
			}
			window.open(url, "MacPHR", "width=960, height=700");
		};

		//appointment buttons
		controller.appointmentDo = function appointmentDo(func)
		{
			var url = null;
			if (func == "ApptHistory") url = "../demographic/demographiccontrol.jsp?displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=25&orderby=appttime&demographic_no=" + controller.page.demo.demographicNo + "&last_name=" + encodeURI(controller.page.demo.lastName) + "&first_name=" + encodeURI(controller.page.demo.firstName);
			else if (func == "WaitingList") url = "../oscarWaitingList/SetupDisplayPatientWaitingList.do?demographic_no=" + controller.page.demo.demographicNo;
			window.open(url, "Appointment", "width=960, height=700");
		};

		controller.isClinicaidBilling = function isClinicaidBilling()
		{
			return controller.page.billregion == "CLINICAID";
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
					url = "../billing.do?billRegion=CLINICAID&action=invoice_reports&patient_remote_id=" + controller.page.demo.demographicNo;
				}
				else if (controller.page.billregion === BILLING_REGION.ON)
				{
					url = "../billing/CA/ON/billinghistory.jsp?demographic_no=" + controller.page.demo.demographicNo + "&last_name=" + encodeURI(controller.page.demo.lastName) + "&first_name=" + encodeURI(controller.page.demo.firstName) + "&orderby=appointment_date&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=10";
				}
				else
				{
					url = "../billing/CA/BC/billStatus.jsp?lastName=" + encodeURI(controller.page.demo.lastName) + "&firstName=" + encodeURI(controller.page.demo.firstName) + "&filterPatient=true&demographicNo=" + controller.page.demo.demographicNo;
				}
			}
			else if (func === "CreateInvoice")
			{
				url = "../billing.do?billRegion=" + controller.page.billregion + "&billForm=" + controller.page.defaultView + "&hotclick=&appointment_no=0&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) + "&demographic_no=" + controller.page.demo.demographicNo + "&providerview=" + controller.page.demo.providerNo + "&user_no=" + user.providerNo + "&apptProvider_no=none&appointment_date=" + now.getFullYear() + "-" + (now.getMonth() + 1) + "-" + now.getDate() + "&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "FluBilling")
			{
				url = "../billing/CA/ON/specialtyBilling/fluBilling/addFluBilling.jsp?function=demographic&functionid=" + controller.page.demo.demographicNo + "&creator=" + user.providerNo + "&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) + "&hin=" + controller.page.demo.hin + controller.page.demo.ver + "&demo_sex=" + controller.page.demo.sex + "&demo_hctype=" + controller.page.demo.hcType + "&rd=" + encodeURI(controller.page.demo.scrReferralDoc) + "&rdohip=" + controller.page.demo.scrReferralDocNo + "&dob=" + controller.page.demo.dobYear + controller.page.demo.dobMonth + controller.page.demo.dobDay + "&mrp=" + controller.page.demo.providerNo;
			}
			else if (func === "HospitalBilling")
			{
				url = "../billing/CA/ON/billingShortcutPg1.jsp?billRegion=" + controller.page.billregion + "&billForm=" + encodeURI(controller.page.hospitalView) + "&hotclick=&appointment_no=0&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) + "&demographic_no=" + controller.page.demo.demographicNo + "&providerview=" + controller.page.demo.providerNo + "&user_no=" + user.providerNo + "&apptProvider_no=none&appointment_date=" + now.getFullYear + "-" + (now.getMonth() + 1) + "-" + now.getDate() + "&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "AddBatchBilling")
			{
				url = "../billing/CA/ON/addBatchBilling.jsp?demographic_no=" + controller.page.demo.demographicNo + "&creator=" + user.providerNo + "&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) + "&hin=" + controller.page.demo.hin + controller.page.demo.ver + "&dob=" + controller.page.demo.dobYear + controller.page.demo.dobMonth + controller.page.demo.dobDay;
			}
			else if (func === "AddINR")
			{
				url = "../billing/CA/ON/inr/addINRbilling.jsp?function=demographic&functionid=" + controller.page.demo.demographicNo + "&creator=" + user.providerNo + "&demographic_name=" + encodeURI(controller.page.demo.lastName) + encodeURI(",") + encodeURI(controller.page.demo.firstName) + "&hin=" + controller.page.demo.hin + controller.page.demo.ver + "&dob=" + controller.page.demo.dobYear + controller.page.demo.dobMonth + controller.page.demo.dobDay;
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
			var url = "../demographic/demographicExport.jsp?demographicNo=" + controller.page.demo.demographicNo;
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
			if ((controller.page.demo.hin == null || controller.page.demo.hin === "") && doSave)
			{
				controller.save();
			}
			else
			{
				let hin = controller.page.demo.hin;
				let ver = controller.page.demo.ver;
				let hcType = controller.page.demo.hcType;
				let demographicNo = controller.page.demo.demographicNo;
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
			else if (dateEmpty(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay))
			{
				alert("Date of Birth is required");
				return;
			}

			//validate field inputs
			if (controller.page.demo.dateOfBirth == null)
			{
				alert("Invalid Date of Birth");
				return;
			}
			if (!controller.checkPatientStatus()) return;
			if (!controller.isPostalComplete()) return;
			if (!controller.validateSin()) return;
			if (!controller.validateDocNo(controller.page.demo.scrReferralDocNo)) return;
			if (!controller.validateDocNo(controller.page.demo.scrFamilyDocNo)) return;

			if (Juno.Common.Util.exists(controller.page.demo.hin))
            {
                controller.page.demo.hin = controller.page.demo.hin.replace(/[\W_]/gi, '');
            }

			//save notes
			if (controller.page.demo.scrNotes != null)
			{
				controller.page.demo.notes = "<unotes>" + controller.page.demo.scrNotes + "</unotes>";
			}

			//save referral doctor (familyDoctor)
			controller.page.demo.familyDoctor = controller.formatDocInput(controller.page.demo.scrReferralDoc, controller.page.demo.scrReferralDocNo);

			//save family doctor (familyDoctor2)
			controller.page.demo.familyDoctor2 = controller.formatFamilyDocInput(controller.page.demo.scrFamilyDoc, controller.page.demo.scrFamilyDocNo);

			//save phone numbers
			controller.page.demo.scrDemoCell = controller.page.demo.scrCellPhone;
			controller.page.demo.phone = controller.page.demo.scrHomePhone;
			controller.page.demo.alternativePhone = controller.page.demo.scrWorkPhone;

			if (controller.page.demo.scrPreferredPhone == "C") controller.page.demo.scrDemoCell += "*";
			else if (controller.page.demo.scrPreferredPhone == "H") controller.page.demo.phone += "*";
			else if (controller.page.demo.scrPreferredPhone == "W") controller.page.demo.alternativePhone += "*";

			//save extras
			var newDemoExtras = [];
			newDemoExtras = updateDemoExtras("demo_cell", controller.page.demo.scrDemoCell, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("aboriginal", controller.page.demo.scrAboriginal, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("hPhoneExt", controller.page.demo.scrHPhoneExt, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("wPhoneExt", controller.page.demo.scrWPhoneExt, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("cytolNum", controller.page.demo.scrCytolNum, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("phoneComment", controller.page.demo.scrPhoneComment, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("paper_chart_archived", controller.page.demo.scrPaperChartArchived, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("paper_chart_archived_date", controller.page.demo.scrPaperChartArchivedDate, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("usSigned", controller.page.demo.scrUsSigned, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("privacyConsent", controller.page.demo.scrPrivacyConsent, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("informedConsent", controller.page.demo.scrInformedConsent, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("securityQuestion1", controller.page.demo.scrSecurityQuestion1, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("securityAnswer1", controller.page.demo.scrSecurityAnswer1, posExtras, controller.page.demo.extras, newDemoExtras);
			newDemoExtras = updateDemoExtras("rxInteractionWarningLevel", controller.page.demo.scrRxInteractionLevel, posExtras, controller.page.demo.extras, newDemoExtras);
			controller.page.demo.extras = newDemoExtras;

			// clone the demographic, so that final modifications can be made before save.
			let demographicForSave = {};
			Object.assign(demographicForSave, controller.page.demo)

			// convert null back to "-1" why? because Oscar.
			if (!demographicForSave.countryOfOrigin)
			{
				demographicForSave.countryOfOrigin = "-1";
			}

			//save to database
			demographicService.updateDemographic(demographicForSave).then(
				function success()
				{
					controller.resetEditState();
				},

				function error()
				{
					controller.page.saving = false;
					alert('Failed to save demographic');
					// TODO: handle error
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

		controller.init(); // Initialize the controller
	}
]);


// Move these?
function updateDemoExtras(extKey, newVal, posExtras, oldExtras, newExtras)
{
	if (newVal == null) return newExtras;

	var pos = posExtras[extKey];
	if (pos != null && oldExtras[pos] != null)
	{ //existing ext
		if (oldExtras[pos].value != newVal)
		{
			newExtras.push(
			{
				id: oldExtras[pos].id,
				key: extKey,
				value: newVal,
				hidden: oldExtras[pos].hidden
			});
		}
	}
	else
	{ //newly added ext
		newExtras.push(
		{
			key: extKey,
			value: newVal
		});
	}
	return newExtras;
}

function dateEmpty(year, month, day)
{
	return ((year == null || year == "") && (month == null || month == "") && (day == null || day == ""));
}

function dateValid(dateStr)
{ //valid date format: yyyy-MM-dd
	if (dateStr == null || dateStr == "") return true;

	var datePart = dateStr.toString().split("-");
	if (datePart.length != 3) return false;

	var dateDate = new Date(datePart[0], datePart[1] - 1, datePart[2]);
	if (isNaN(dateDate.getTime())) return false;

	if (dateDate.getFullYear() != datePart[0]) return false;
	if (dateDate.getMonth() != datePart[1] - 1) return false;
	if (dateDate.getDate() != datePart[2]) return false;

	return true;
}

function isNumber(s)
{
	return /^[0-9]+$/.test(s);
}

function isPreferredPhone(phone)
{
	phone = String(phone);
	if (phone != null && phone != "")
	{
		if (phone.charAt(phone.length - 1) == "*") return true;
	}
	return false;
}

function getPhoneNum(phone)
{
	if (isPreferredPhone(phone))
	{
		phone = phone.substring(0, phone.length - 1);
	}
	return phone;
}

function demoContactShow(demoContact)
{
	var contactShow = demoContact;
	if (demoContact.role != null)
	{ //only 1 entry
		var tmp = {};
		tmp.role = demoContact.role;
		tmp.sdm = demoContact.sdm;
		tmp.ec = demoContact.ec;
		tmp.category = demoContact.category;
		tmp.lastName = demoContact.lastName;
		tmp.firstName = demoContact.firstName;
		tmp.phone = demoContact.phone;
		contactShow = [tmp];
	}
	for (var i = 0; i < contactShow.length; i++)
	{
		if (contactShow[i].sdm == true) contactShow[i].role += " /sdm";
		if (contactShow[i].ec == true) contactShow[i].role += " /ec";
		if (contactShow[i].role == null || contactShow[i].role == "") contactShow[i].role = "-";

		if (contactShow[i].phone == null || contactShow[i].phone == "")
		{
			contactShow[i].phone = "-";
		}
		else if (contactShow[i].phone.charAt(contactShow[i].phone.length - 1) == "*")
		{
			contactShow[i].phone = contactShow[i].phone.substring(0, contactShow[i].phone.length - 1);
		}
	}
	return contactShow;
}

function toArray(obj)
{ //convert single object to array
	if (obj instanceof Array) return obj;
	else if (obj == null) return [];
	else return [obj];
}
