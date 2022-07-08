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

import {BILLING_TYPE, INSTANCE_TYPE, SystemPreferences, SystemProperties} from "../../common/services/systemPreferenceServiceConstants";
import {DemographicApi, SystemPreferenceApi} from "../../../generated";
import {JUNO_STYLE} from "../../common/components/junoComponentConstants";
import {SecurityPermissions} from "../../common/security/securityConstants";
import {BILLING_REGION} from "../../billing/billingConstants";
import Demographic from "../../lib/demographic/model/Demographic";
import ToastService from "../../lib/alerts/service/ToastService";
import moment from "moment";
import Address from "../../lib/common/model/Address";

angular.module('Record.Details').component('detailsCtrl', {
	// note 'details' is a reserved html tag name
	bindings: {
		componentStyle: "<?",
		user: "<?",
	},
	templateUrl: "src/record/details/details.jsp",
	controller: [
	'$scope',
	'$http',
	'$location',
	'$stateParams',
	'$state',
	'$window',
	'$uibModal',
	'$httpParamSerializer',
	'$sce',
	'$timeout',
	'demographicService',
	'demographicsService',
	'errorsService',
	'patientDetailStatusService',
	'securityRolesService',

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
		$timeout,
		demographicService,
		demographicsService,
		messagesFactory,
		patientDetailStatusService,
		securityRolesService)
	{

		const controller = this;
		controller.page = {};
		controller.page.demo = null as Demographic;
		const demographicApi = new DemographicApi($http, $httpParamSerializer, "../ws/rs");
		const systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
		controller.toastService = new ToastService();


		controller.eligibilityMsg = $sce.trustAsHtml("...");
		controller.showEligibility = false;
		controller.rosteringModuleEnabled = false;
		controller.displayMessages = messagesFactory.factory();
		controller.validations = {};
		controller.SecurityPermissions = SecurityPermissions;

		$scope.JUNO_STYLE = JUNO_STYLE;
		$scope.pageStyle = JUNO_STYLE.GREY;

		controller.$onInit = async () =>
		{
			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.DemographicRead))
			{
				try
				{
					let results = await Promise.all([
						demographicService.getDemographic($stateParams.demographicNo),
						demographicApi.getDemographicContacts($stateParams.demographicNo, "professional"),
						patientDetailStatusService.getStatus($stateParams.demographicNo),
						systemPreferenceApi.getPropertyValue(SystemProperties.InstanceType, INSTANCE_TYPE.BC),
						systemPreferenceApi.getPreferenceEnabled(SystemPreferences.RosteringModule, false),
					]);

					controller.page.demo = results[0];
					controller.page.demoContactPros = results[1].data.body;
					controller.initPageDetails(results[2]);
					await controller.initShowEligibilityState(results[3].data.body);
					controller.rosteringModuleEnabled = results[4].data.body;

					controller.page.dataChanged = false;

					// controller.loadWatches();
				}
				catch (errors)
				{
					controller.toastService.errorToast("Error loading demographic");
					console.error(errors);
				}
			}
		};

		controller.initShowEligibilityState = async (type: INSTANCE_TYPE) =>
		{
			switch (type)
			{
				case INSTANCE_TYPE.BC:
				{
					controller.showEligibility = true; break;
				}
				case INSTANCE_TYPE.ON:
				{
					controller.showEligibility = true;
					let billingType = (
						await systemPreferenceApi.getPropertyValue(SystemProperties.BillingType, BILLING_TYPE.CLINICAID)
					).data.body;
					if (billingType === BILLING_TYPE.CLINICAID)
					{
						controller.showEligibility = true;
					}
				}
			}
		}
		controller.initPageDetails = (results: any) =>
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
		}

		controller.$postLink = () =>
		{
			// hack to prevent a change event on load flagging triggering the watch
			$timeout(() =>
			{
				controller.loadWatches(); // done once everything initialized
			}, 2000);
		}

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
		controller.loadWatches = () =>
		{
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
		};

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
				controller.page.demo.healthNumberProvinceCode = cardData.province;
				controller.displayMessages.add_field_warning('province', "Province Changed");
				controller.displayMessages.add_field_warning('hcType', "Health Card Type Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.lastName))
			{
				controller.page.demo.lastName = cardData.lastName;
				controller.displayMessages.add_field_warning('lastName', "Last Name Changed");
			}
			if (!Juno.Common.Util.isBlank(cardData.firstName))
			{
				controller.page.demo.firstName = cardData.firstName;
				controller.displayMessages.add_field_warning('firstName', "First Name Changed");
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

				controller.page.demo.healthNumberRenewDate = expireDate;
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

		controller.isPostalComplete = function isPostalComplete(address: Address)
		{
			if(address)
			{
				if(!address.isValidPostalOrZip())
				{
					Juno.Common.Util.errorAlert($uibModal, "Validation", "Invalid/Incomplete Postal Code");
					controller.resetEditState();
					return false;
				}
			}
			return true;
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
			if (controller.page.dataChanged)
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
				url = "../phr/PhrMessage.do?method=createMessage&providerNo=" + controller.user.providerNo + "&demographicNo=" + controller.page.demo.id;
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
			if (func === "ApptHistory")
			{
				url = "../demographic/demographiccontrol.jsp" +
					"?displaymode=appt_history" +
					"&dboperation=appt_history" +
					"&limit1=0" +
					"&limit2=25" +
					"&orderby=appttime" +
					"&demographic_no=" + controller.page.demo.id +
					"&last_name=" + encodeURIComponent(controller.page.demo.lastName) +
					"&first_name=" + encodeURIComponent(controller.page.demo.firstName);
			}
			else if (func === "WaitingList")
			{
				url = "../oscarWaitingList/SetupDisplayPatientWaitingList.do" +
					"?demographic_no=" + controller.page.demo.id;
			}
			window.open(url, "Appointment", "width=960, height=700");
		};

		controller.isClinicaidBilling = function isClinicaidBilling()
		{
			return controller.page.billregion === "CLINICAID";
		};

		//billing buttons
		controller.billingDo = function billingDo(func)
		{
			var now = new moment();
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
						"&last_name=" + encodeURIComponent(controller.page.demo.lastName) +
						"&first_name=" + encodeURIComponent(controller.page.demo.firstName) +
						"&orderby=appointment_date&displaymode=appt_history&dboperation=appt_history&limit1=0&limit2=10";
				}
				else
				{
					url = "../billing/CA/BC/billStatus.jsp?lastName=" + encodeURIComponent(controller.page.demo.lastName) +
						"&firstName=" + encodeURIComponent(controller.page.demo.firstName) +
						"&filterPatient=true&demographicNo=" + controller.page.demo.id;
				}
			}
			else if (func === "CreateInvoice")
			{
				url = "../billing.do?billRegion=" + controller.page.billregion + "&billForm=" + controller.page.defaultView +
					"&hotclick=&appointment_no=0&demographic_name=" + encodeURIComponent(controller.page.demo.lastName + "," + controller.page.demo.firstName) +
					"&demographic_no=" + controller.page.demo.id +
					"&providerview=" + controller.page.demo.mrpProvider?.id +
					"&user_no=" + controller.user.providerNo +
					"&apptProvider_no=none&appointment_date=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(now)) +
					"&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "FluBilling")
			{
				url = "../billing/CA/ON/specialtyBilling/fluBilling/addFluBilling.jsp?function=demographic&functionid=" + controller.page.demo.id +
					"&creator=" + controller.user.providerNo +
					"&demographic_name=" + encodeURIComponent(controller.page.demo.lastName + "," + controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&demo_sex=" + controller.page.demo.sex +
					"&demo_hctype=" + controller.page.demo.healthNumberProvinceCode +
					"&rd=" + encodeURIComponent(controller.page.demo.referralDoctor?.displayName) +
					"&rdohip=" + controller.page.demo.referralDoctor?.ohipNumber +
					"&dob=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(controller.page.demo.dateOfBirth)) +
					"&mrp=" + controller.page.demo.mrpProvider?.id;
			}
			else if (func === "HospitalBilling")
			{
				url = "../billing/CA/ON/billingShortcutPg1.jsp?billRegion=" + controller.page.billregion +
					"&billForm=" + encodeURIComponent(controller.page.hospitalView) +
					"&hotclick=&appointment_no=0&demographic_name=" + encodeURIComponent(controller.page.demo.lastName + "," + controller.page.demo.firstName) +
					"&demographic_no=" + controller.page.demo.id +
					"&providerview=" + controller.page.demo.mrpProvider?.id +
					"&user_no=" + controller.user.providerNo +
					"&apptProvider_no=none&appointment_date=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(now)) +
					"&start_time=00:00:00&bNewForm=1&status=t";
			}
			else if (func === "AddBatchBilling")
			{
				url = "../billing/CA/ON/addBatchBilling.jsp?demographic_no=" + controller.page.demo.id +
					"&creator=" + controller.user.providerNo +
					"&demographic_name=" + encodeURIComponent(controller.page.demo.lastName + "," + controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&dob=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(controller.page.demo.dateOfBirth));
			}
			else if (func === "AddINR")
			{
				url = "../billing/CA/ON/inr/addINRbilling.jsp?function=demographic&functionid=" + controller.page.demo.id +
					"&creator=" + controller.user.providerNo +
					"&demographic_name=" + encodeURIComponent(controller.page.demo.lastName + "," + controller.page.demo.firstName) +
					"&hin=" + controller.page.demo.healthNumber + controller.page.demo.healthNumberVersion +
					"&dob=" + encodeURIComponent(Juno.Common.Util.formatMomentDate(controller.page.demo.dateOfBirth));
			}
			else if (func === "BillINR")
			{
				url = "../billing/CA/ON/inr/reportINR.jsp?provider_no=" + controller.user.providerNo;
			}
			window.open(url, "Billing", "width=960, height=700");
		};

		//export demographic
		controller.exportDemographic = function exportDemographic()
		{
			var url = "../demographic/demographicExport.jsp?demographicNo=" + controller.page.demo.id;
			window.open(url, "DemographicExport", "width=960, height=700");
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
			if (!controller.isPostalComplete(controller.page.demo.address)) return;
			if (controller.page.demo.address2 && !controller.isPostalComplete(controller.page.demo.address2)) return;

			if (Juno.Common.Util.exists(controller.page.demo.healthNumber))
            {
                controller.page.demo.healthNumber = controller.page.demo.healthNumber.replace(/[\W_]/gi, '');
            }

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
	]
});
