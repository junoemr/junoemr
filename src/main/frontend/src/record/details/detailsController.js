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
import {SystemPreferenceApi} from "../../../generated";

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
	'providersService',
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
		providersService,
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

		controller.eligibilityMsg = $sce.trustAsHtml("...");
		controller.showEligibility = false;
		controller.properties = $scope.$parent.recordCtrl.properties;
		controller.displayMessages = messagesFactory.factory();


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
					providersService.getBySecurityRole("doctor").then(
						function success(data) {
							controller.page.doctors = data;
						}
					);
					providersService.getBySecurityRole("nurse").then(
						function success(data) {
							controller.page.nurses = data;
						}
					);
					providersService.getBySecurityRole("midwife").then(
						function success(data) {
							controller.page.midwives = data;
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

					//show referral doctor
					if (controller.page.demo.familyDoctor != null)
					{
						var referralDoc = controller.formatDocOutput(controller.page.demo.familyDoctor);
						controller.page.demo.scrReferralDocNo = referralDoc.number;
						controller.page.demo.scrReferralDoc = referralDoc.name;
					}

					//show family doctor
					if (controller.page.demo.familyDoctor2 != null)
					{
						var familyDoc = controller.formatDocOutput(controller.page.demo.familyDoctor2);
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

					//get static lists to be selected
					controller.page.genders = staticDataService.getGenders();
					controller.page.titles = staticDataService.getTitles();
					controller.page.provinces = staticDataService.getProvinces();
					controller.page.countries = staticDataService.getCountries();
					controller.page.engFre = staticDataService.getEngFre();
					controller.page.spokenlangs = staticDataService.getSpokenLanguages();
					controller.page.rosterTermReasons = staticDataService.getRosterTerminationReasons();
					controller.page.securityQuestions = staticDataService.getSecurityQuestions();
					controller.page.rxInteractionLevels = staticDataService.getRxInteractionLevels();

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

							controller.page.conformanceFeaturesEnabled = results.conformanceFeaturesEnabled;
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

					//show patientStatusList & rosterStatusList values
					demographicsService.getStatusList("roster").then(
						function success(data)
						{
							controller.page.rosterStatusList = toArray(data);
							controller.page.rosterStatusList.unshift(
								{
									"value": "FS",
									"label": "FS - fee for service"
								});
							controller.page.rosterStatusList.unshift(
								{
									"value": "TE",
									"label": "TE - terminated"
								});
							controller.page.rosterStatusList.unshift(
								{
									"value": "NR",
									"label": "NR - not rostered"
								});
							controller.page.rosterStatusList.unshift(
								{
									"value": "RO",
									"label": "RO - rostered"
								});
						}
					);
					demographicsService.getStatusList("patient").then(
						function success(data)
						{
							controller.page.patientStatusList = toArray(data);
							controller.page.patientStatusList.unshift(
								{
									"value": "FI",
									"label": "FI - Fired"
								});
							controller.page.patientStatusList.unshift(
								{
									"value": "MO",
									"label": "MO - Moved"
								});
							controller.page.patientStatusList.unshift(
								{
									"value": "DE",
									"label": "DE - Deceased"
								});
							controller.page.patientStatusList.unshift(
								{
									"value": "IN",
									"label": "IN - Inactive"
								});
							controller.page.patientStatusList.unshift(
								{
									"value": "AC",
									"label": "AC - Active"
								});
						}
					);

					controller.formatDate("DobM"); //done on page load
					controller.formatDate("DobD"); //done on page load
					controller.page.demo.age = Juno.Common.Util.calcAge(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay);
					controller.formatLastName(); //done on page load
					controller.formatFirstName(); //done on page load
					controller.validateHCSave();
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

		// Is there a shared location where this could be accessed from any controller? i.e. a utils file
		controller.isNaN = function(num)
		{
			return isNaN(num);
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

		//manage hin/hinVer entries
		controller.checkHin = function checkHin()
		{
			if (controller.page.demo.hcType == "ON" && controller.page.demo.hin != null && controller.page.demo.hin != "")
			{
				if (controller.page.demo.hin.length > 10) controller.page.demo.hin = hin0;
				if (!isNumber(controller.page.demo.hin)) controller.page.demo.hin = hin0;
			}
			hin0 = controller.page.demo.hin;
			controller.page.HCValidation = null;
		};
		controller.checkHinVer = function checkHinVer()
		{
			if (controller.page.demo.hcType == "ON")
			{
				if (controller.page.demo.ver.length > 2) controller.page.demo.ver = ver0;
				if (!(/^[a-zA-Z()]*$/.test(controller.page.demo.ver))) controller.page.demo.ver = ver0;
				controller.page.demo.ver = controller.page.demo.ver.toUpperCase();
			}
			ver0 = controller.page.demo.ver;
		};

		//manage date entries
		controller.checkDate = function checkDate(id)
		{
			if (id == "DobY")
			{
				controller.page.demo.dobYear = checkYear(controller.page.demo.dobYear);
			}
			else if (id == "DobM")
			{
				controller.page.demo.dobMonth = checkMonth(controller.page.demo.dobMonth);
			}
			else if (id == "DobD")
			{
				controller.page.demo.dobDay = checkDay(controller.page.demo.dobDay, controller.page.demo.dobMonth, controller.page.demo.dobYear);
			}
			console.log('MONTH: ', controller.page.demo.dobMonth);
			controller.page.demo.age = Juno.Common.Util.calcAge(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay);
		};

		controller.formatDate = function formatDate(id)
		{
			// controller.calculateAge();

			if (id == "DobM" && controller.page.demo.dobMonth != null && String(controller.page.demo.dobMonth).length == 1)
			{
				controller.page.demo.dobMonth = "0" + controller.page.demo.dobMonth;
			}
			else if (id == "DobD" && controller.page.demo.dobDay != null && String(controller.page.demo.dobDay).length == 1)
			{
				controller.page.demo.dobDay = "0" + controller.page.demo.dobDay;
			}
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

		//check email
		controller.checkEmail = function checkEmail()
		{
			if (controller.page.demo.email == null || controller.page.demo.email == "") return true;

			var regex = /^[^@]+@[^@]+$/;
			if (regex.test(controller.page.demo.email))
			{
				var email = controller.page.demo.email.split("@");

				regex = /^[!#%&'=`~\{}\-\$\*\+\/\?\^\|\w]+(\.[!#%&'=`~\{}\-\$\*\+\/\?\^\|\w]+)*$/;
				if (regex.test(email[0]))
				{ //test email local address part

					regex = /^[^\W_]+(([^\W_]|-)+[^\W_]+)*(\.[^\W_]+(([^\W_]|-)+[^\W_]+)*)*\.[^\W_]{2,3}$/;
					if (regex.test(email[1])) return true; //test email address domain part
				}
			}
			alert("Invalid email address");
			return false;
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

		//check Cytology Number
		controller.checkCytoNum = function checkCytoNum()
		{
			if (controller.page.demo.scrCytolNum == null || controller.page.demo.scrCytolNum == "")
			{
				cytolNum0 = controller.page.demo.scrCytolNum;
				return;
			}
			if (!isNumber(controller.page.demo.scrCytolNum)) controller.page.demo.scrCytolNum = cytolNum0;
			else cytolNum0 = controller.page.demo.scrCytolNum;
		};

		//check Referral Doctor No
		controller.checkReferralDocNo = function checkReferralDocNo()
		{
			var isValid = controller.validateDocNo(controller.page.demo.scrReferralDocNo, true);
			if (isValid)
				referralDocNo0 = controller.page.demo.scrReferralDocNo;
			else
				controller.page.demo.scrReferralDocNo = referralDocNo0;
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

		//prevent manual input dates
		controller.preventManualEffDate = function preventManualEffDate()
		{
			if (controller.page.demo.effDate == null) controller.page.demo.effDate = effDate0;
			else effDate0 = controller.page.demo.effDate;
		};
		controller.preventManualHcRenewDate = function preventManualHcRenewDate()
		{
			if (controller.page.demo.hcRenewDate == null) controller.page.demo.hcRenewDate = hcRenewDate0;
			else hcRenewDate0 = controller.page.demo.hcRenewDate;
		};
		controller.preventManualRosterDate = function preventManualRosterDate()
		{
			if (controller.page.demo.rosterDate == null) controller.page.demo.rosterDate = rosterDate0;
			else rosterDate0 = controller.page.demo.rosterDate;
		};
		controller.preventManualRosterTerminationDate = function preventManualRosterTerminationDate()
		{
			if (controller.page.demo.rosterTerminationDate == null) controller.page.demo.rosterTerminationDate = rosterTerminationDate0;
			else rosterTerminationDate0 = controller.page.demo.rosterTerminationDate;
		};
		controller.preventManualPatientStatusDate = function preventManualPatientStatusDate()
		{
			if (controller.page.demo.patientStatusDate == null) controller.page.demo.patientStatusDate = patientStatusDate0;
			else patientStatusDate0 = controller.page.demo.patientStatusDate;
		};
		controller.preventManualDateJoined = function preventManualDateJoined()
		{
			if (controller.page.demo.dateJoined == null) controller.page.demo.dateJoined = dateJoined0;
			else dateJoined0 = controller.page.demo.dateJoined;
		};
		controller.preventManualEndDate = function preventManualEndDate()
		{
			if (controller.page.demo.endDate == null) controller.page.demo.endDate = endDate0;
			else endDate0 = controller.page.demo.endDate;
		};
		controller.preventManualOnWaitingListSinceDate = function preventManualOnWaitingListSinceDate()
		{
			if (controller.page.demo.onWaitingListSinceDate == null) controller.page.demo.onWaitingListSinceDate = onWaitingListSinceDate0;
			else onWaitingListSinceDate0 = controller.page.demo.onWaitingListSinceDate;
		};
		controller.preventManualPaperChartArchivedDate = function preventManualPaperChartArchivedDate()
		{
			if (controller.page.demo.scrPaperChartArchivedDate == null) controller.page.demo.scrPaperChartArchivedDate = paperChartArchivedDate0;
			else paperChartArchivedDate0 = controller.page.demo.scrPaperChartArchivedDate;
		};

		//show/hide items
		// TODO: FIGURE OUT BETTER WAY TO SYNCHRONIZE THIS WITH DEMOGRAPHIC LOADING
		controller.isRosterTerminated = function isRosterTerminated()
		{
			if(controller.page.demo !== null && controller.page.demo !== undefined)
				return (controller.page.demo.rosterStatus === "TE");

			return null;
		};
		controller.showReferralDocList = function showReferralDocList()
		{
			controller.page.showReferralDocList = !controller.page.showReferralDocList;
		};

		controller.searchReferralDocsName = function searchReferralDocsName(searchName)
		{
			return controller.searchReferralDocs(searchName, null);
		};
		controller.searchReferralDocsRefNo = function searchReferralDocsRefNo(searchRefNo)
		{
			return controller.searchReferralDocs(null, searchRefNo);
		};
		controller.searchReferralDocs = function searchReferralDocs(searchName, searchRefNo)
		{
			return referralDoctorsService.searchReferralDoctors(searchName, searchRefNo, 1, 10).then(
				function success(results) {

					var referralDoctors = new Array(results.length);
					for (var i = 0; i < results.length; i++)
					{
						var displayName = results[i].lastName + ', ' + results[i].firstName;
						referralDoctors[i] = {
							label: displayName,
							name: displayName,
							referralNo: results[i].referralNo
						};
						if (results[i].specialtyType != null && results[i].specialtyType != "")
						{
							referralDoctors[i].label += " [" + results[i].specialtyType + "]";
						}
					}
					return referralDoctors;
				},
				function failure(errors) {
					return [];
				}
			);
		};
		controller.chooseReferralDoc = function chooseReferralDoc(item, model, label)
		{
			controller.page.demo.scrReferralDocNo = item.referralNo;
			controller.page.demo.scrReferralDoc = item.name;
			controller.checkReferralDocNo();
		};
		controller.chooseFamilyDoc = function chooseFamilyDoc(item, model, label)
		{
			controller.page.demo.scrFamilyDocNo = item.referralNo;
			controller.page.demo.scrFamilyDoc = item.name;
			controller.checkFamilyDocNo();
		};
		controller.showAddNewRosterStatus = function showAddNewRosterStatus()
		{
			controller.page.showAddNewRosterStatus = !controller.page.showAddNewRosterStatus;
			controller.page.newRosterStatus = null;
		};
		controller.showAddNewPatientStatus = function showAddNewPatientStatus()
		{
			controller.page.showAddNewPatientStatus = !controller.page.showAddNewPatientStatus;
			controller.page.newPatientStatus = null;
		};

		//add new Roster Status
		controller.addNewRosterStatus = function addNewRosterStatus()
		{
			if (controller.page.newRosterStatus != null && controller.page.newRosterStatus != "")
			{
				controller.page.rosterStatusList.push(
				{
					"value": controller.page.newRosterStatus,
					"label": controller.page.newRosterStatus
				});
				controller.page.demo.rosterStatus = controller.page.newRosterStatus;
			}
			controller.showAddNewRosterStatus();
		};

		//add new Patient Status
		controller.addNewPatientStatus = function addNewPatientStatus()
		{
			if (controller.page.newPatientStatus != null && controller.page.newPatientStatus != "")
			{
				controller.page.patientStatusList.push(
				{
					"value": controller.page.newPatientStatus,
					"label": controller.page.newPatientStatus
				});
				controller.page.demo.patientStatus = controller.page.newPatientStatus;
			}
			controller.showAddNewPatientStatus();
		};

		//check phone numbers
		controller.checkPhone = function checkPhone(type)
		{
			if (type == "C")
			{
				if (invalidPhoneNumber(controller.page.demo.scrCellPhone)) controller.page.demo.scrCellPhone = phoneNum["C"];
				else phoneNum["C"] = controller.page.demo.scrCellPhone;
			}
			else if (type == "H")
			{
				if (invalidPhoneNumber(controller.page.demo.scrHomePhone)) controller.page.demo.scrHomePhone = phoneNum["H"];
				else phoneNum["H"] = controller.page.demo.scrHomePhone;
			}
			else if (type == "W")
			{
				if (invalidPhoneNumber(controller.page.demo.scrWorkPhone)) controller.page.demo.scrWorkPhone = phoneNum["W"];
				else phoneNum["W"] = controller.page.demo.scrWorkPhone;
			}
			else if (type == "HX" && controller.page.demo.scrHPhoneExt != null && controller.page.demo.scrHPhoneExt != "")
			{
				if (!isNumber(controller.page.demo.scrHPhoneExt)) controller.page.demo.scrHPhoneExt = phoneNum["HX"];
				else phoneNum["HX"] = controller.page.demo.scrHPhoneExt;
			}
			else if (type == "WX" && controller.page.demo.scrWPhoneExt != null && controller.page.demo.scrWPhoneExt != "")
			{
				if (!isNumber(controller.page.demo.scrWPhoneExt)) controller.page.demo.scrWPhoneExt = phoneNum["WX"];
				else phoneNum["WX"] = controller.page.demo.scrWPhoneExt;
			}
		};

		//set preferred contact phone number
		controller.setPreferredPhone = function setPreferredPhone()
		{
			controller.page.cellPhonePreferredMsg = defPhTitle;
			controller.page.cellPhonePreferredColor = "";
			controller.page.homePhonePreferredMsg = defPhTitle;
			controller.page.homePhonePreferredColor = "";
			controller.page.workPhonePreferredMsg = defPhTitle;
			controller.page.workPhonePreferredColor = "";

			if (controller.page.demo.scrPreferredPhone == "C")
			{
				controller.page.preferredPhoneNumber = controller.page.demo.scrCellPhone;
				controller.page.cellPhonePreferredMsg = prefPhTitle;
				controller.page.cellPhonePreferredColor = colorAttn;
			}
			else if (controller.page.demo.scrPreferredPhone == "H")
			{
				controller.page.preferredPhoneNumber = controller.page.demo.scrHomePhone;
				controller.page.homePhonePreferredMsg = prefPhTitle;
				controller.page.homePhonePreferredColor = colorAttn;
			}
			else if (controller.page.demo.scrPreferredPhone == "W")
			{
				controller.page.preferredPhoneNumber = controller.page.demo.scrWorkPhone;
				controller.page.workPhonePreferredMsg = prefPhTitle;
				controller.page.workPhonePreferredColor = colorAttn;
			}
		};

		//disable set-preferred if phone number empty
		controller.isPhoneVoid = function isPhoneVoid(phone)
		{
			return (phone == null || phone == "");
		};

		//show enrollment history (roster staus history)
		controller.showEnrollmentHistory = function showEnrollmentHistory()
		{
			var url = "../demographic/EnrollmentHistory.jsp?demographicNo=" + controller.page.demo.demographicNo;
			window.open(url, "enrollmentHistory", "width=650, height=1000");
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
				if (controller.page.billregion === "CLINICAID")
				{
					url = "../billing.do?billRegion=CLINICAID&action=invoice_reports&patient_remote_id=" + controller.page.demo.demographicNo;
				}
				else if (controller.page.billregion === "ON")
				{
					url = "../billing/CA/ON/billinghistory.jsp?demographic_no=" + controller.page.demo.demographicNo + "&last_name=" + encodeURI(controller.page.demo.lastName) + "&first_name=" + encodeURI(controller.page.demo.firstName) + "&orderby=appointment_date&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=10";
				}
				else
				{
					url = "../billing/CA/BC/billcontroller.page.jsp?lastName=" + encodeURI(controller.page.demo.lastName) + "&firstName=" + encodeURI(controller.page.demo.firstName) + "&filterPatient=true&demographicNo=" + controller.page.demo.demographicNo;
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

		controller.formatDocOutput = function formatDocOutput(value)
		{
			var number, name;
			var begin = value.indexOf("<rdohip>") + "<rdohip>".length;
			var end = value.indexOf("</rdohip>");
			if (end > begin && end >= 0 && begin >= 0) number = value.substring(begin, end);

			begin = value.indexOf("<rd>") + "<rd>".length;
			end = value.indexOf("</rd>");
			if (end > begin && end >= 0 && begin >= 0) name = value.substring(begin, end);

			return {"number": number, "name": name};
		};

		controller.formatDocInput = function formatDocInput(name, number)
		{
			var docNo = "<rdohip></rdohip>";
			var doc = "<rd></rd>";
			if (number != null && number !== "")
			{
				docNo = "<rdohip>" + number + "</rdohip>";
			}
			if (name != null && name !== "")
			{
				doc = "<rd>" + name + "</rd>";
			}
			return docNo + doc;
		};

		//HCValidation on open & save
		controller.validateHCSave = function validateHCSave(doSave)
		{
			if (controller.page.demo.hin == null || controller.page.demo.hin == "")
			{
				if (doSave) controller.save();
			}
			else
			{
				patientDetailStatusService.isUniqueHC(controller.page.demo.hin, controller.page.demo.demographicNo).then(
					function success(results)
					{
						if (!results.success)
						{
							alert("HIN is already in use!");
						}
						else if (controller.page.demo.hcType != "ON")
						{
							if (doSave) controller.save();
						}
						else
						{
							if (controller.page.demo.ver == null) controller.page.demo.ver = "";
							patientDetailStatusService.validateHC(controller.page.demo.hin, controller.page.demo.ver).then(
								function success(results)
								{
									if (results.valid == null)
									{
										controller.page.HCValidation = "n/a";
									}
									else if (!results.valid)
									{
										alert("Health Card Validation failed: " + results.responseDescription + " (" + results.responseCode + ")");
										doSave = false;
									}
									if (doSave) controller.save();
								},
								function error(errors)
								{
									console.log(errors);
								});
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
			controller.page.demo.dateOfBirth = buildDate(controller.page.demo.dobYear, controller.page.demo.dobMonth, controller.page.demo.dobDay);
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

			//save notes
			if (controller.page.demo.scrNotes != null)
			{
				controller.page.demo.notes = "<unotes>" + controller.page.demo.scrNotes + "</unotes>";
			}

			//save referral doctor (familyDoctor)
			controller.page.demo.familyDoctor = controller.formatDocInput(controller.page.demo.scrReferralDoc, controller.page.demo.scrReferralDocNo);

			//save family doctor (familyDoctor2)
			controller.page.demo.familyDoctor2 = controller.formatDocInput(controller.page.demo.scrFamilyDoc, controller.page.demo.scrFamilyDocNo);

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

			//save to database
			demographicService.updateDemographic(controller.page.demo).then(
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

function buildDate(year, month, day)
{
	if (dateEmpty(year, month, day)) return "";
	if (date3Valid(year, month, day)) return year + "-" + month + "-" + day;
	return null;
}

function checkYear(year)
{
	for (var i = 0; i < year.length; i++)
	{
		if (!isNumber(year.charAt(i)))
		{
			year = year.substring(0, i) + year.substring(i + 1);
		}
	}
	if (year != "")
	{
		year = parseInt(year).toString();
		if (year.length > 4) year = year.substring(0, 4);
		if (year == 0) year = "";
	}
	return year;
}

function checkMonth(month)
{
	for (var i = 0; i < month.length; i++)
	{
		if (!isNumber(month.charAt(i)))
		{
			month = month.substring(0, i) + month.substring(i + 1);
		}
	}
	if (month != "")
	{
		if (month.length > 2) month = month.substring(0, 2);
		if (month > 12) month = month.substring(0, 1);
	}
	return month;
}

var daysOfMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

function checkDay(day, month, year)
{
	for (var i = 0; i < day.length; i++)
	{
		if (!isNumber(day.charAt(i)))
		{
			day = day.substring(0, i) + day.substring(i + 1);
		}
	}
	if (day != "")
	{
		if (day.length > 2) day = day.substring(0, 2);

		if (month == null)
		{
			if (day > 31) day = day.substring(0, 1);
		}
		else if (year == null)
		{
			if (day > daysOfMonth[month - 1]) day.substring(0, 1);
		}
		else if (!date3Valid(year, month, day))
		{
			day = day.substring(0, 1);
		}
	}
	return day;
}

function date3Valid(year, month, day)
{
	if (year != null && year != "" && month != null && month != "" && day != null && day != "")
	{
		var maxDaysOfMonth = daysOfMonth[month - 1];
		if (month == 2)
		{
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
			{
				maxDaysOfMonth = 29;
			}
		}
		return (day > 0 && day <= maxDaysOfMonth);
	}
	return dateEmpty(year, month, day);
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

function invalidPhoneNumber(phone)
{
	if (phone == null) return false; //phone number is NOT invalid
	return !(/^[0-9 \-\()]*$/.test(phone));
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