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

import {ScheduleApi} from "../../generated/api/ScheduleApi";
import {AppointmentApi} from "../../generated/api/AppointmentApi";
import {SystemPreferenceApi} from "../../generated/api/SystemPreferenceApi";
import {JUNO_BACKGROUND_STYLE, JUNO_STYLE} from "../common/components/junoComponentConstants";
import {SecurityPermissions} from "../common/security/securityConstants";

angular.module('Layout').component('leftAside', {
	bindings: {
		expandOn: "="
	},
	templateUrl: "src/layout/leftAside.jsp",
	controller: [
		"$rootScope",
		"$scope",
		"$q",
		"$http",
		"$httpParamSerializer",
		"$state",
		"angularUtil",
		"scheduleService",
		"providerService",
		"securityService",
		"securityRolesService",
		"systemPreferenceService",
		function (
			$rootScope,
			$scope,
			$q,
			$http,
			$httpParamSerializer,
			$state,
			angularUtil,
			scheduleService,
			providerService,
			securityService,
			securityRolesService,
			systemPreferenceService)
	{

		$scope.pageStyle = JUNO_STYLE.GREY;

		var ctrl = this;
		ctrl.initialized = false;

		ctrl.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');
		ctrl.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs');
		ctrl.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
			'../ws/rs');

		ctrl.SecurityPermissions = SecurityPermissions;

		ctrl.tabEnum = Object.freeze({
			appointments: 0,
			recent: 1,
			appointmentQueue: 2,
		});
		ctrl.activeTab = ctrl.tabEnum.appointments;
		ctrl.activePatientList = [];
		ctrl.activeAppointmentList = [];

		//for filter box
		ctrl.query = '';
		ctrl.datepickerSelectedDate = null;

		ctrl.refreshSettings = {
			timerVariable: null,
			defaultAutoRefreshMinutes: 3,
			preferredAutoRefreshMinutes: null
		};
		ctrl.eventStatusOptions = [];

		ctrl.show_appointment_queue = false;

		ctrl.init = async function ()
		{
			if(securityRolesService.hasSecurityPrivileges(SecurityPermissions.AppointmentRead))
			{
				await scheduleService.loadEventStatuses();
			}

			ctrl.eventStatusOptions = scheduleService.eventStatuses;
			ctrl.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(moment());

			ctrl.provider = securityService.getUser();
			ctrl.telehealthEnabled = ctrl.loadTelehealthEnabled();
			ctrl.loadProviderSettings();

			// only show the appointment queue if enabled
			ctrl.show_appointment_queue = await systemPreferenceService.isPreferenceEnabled("aqs_enabled", false);

			if(ctrl.componentEnabled())
			{
				if (ctrl.isAppointmentPatientViewEnabled())
				{
					ctrl.changeTab(ctrl.tabEnum.appointments);
				}
				else if (ctrl.isAppointmentQueueViewEnabled())
				{
					ctrl.changeTab(ctrl.tabEnum.appointmentQueue);
				}
				else if (ctrl.isRecentPatientViewEnabled())
				{
					ctrl.changeTab(ctrl.tabEnum.recent);
				}

				ctrl.loadWatches();
				ctrl.initListAutoRefresh();
			}
			ctrl.initialized = true;
		};

		ctrl.loadProviderSettings = function ()
		{
			providerService.getSettings().then(
					function success(result)
					{
						ctrl.loadedSettings = result;
					},
					function error(result)
					{
						console.error("Failed to fetch provider setting in left aside with error: " + result);
					}
			);
		};

		ctrl.changeTab = function changeTab(tabId)
		{
			ctrl.activeTab = tabId;
			ctrl.refresh();
		};

		ctrl.getTabClasses = (active) =>
		{
			let classHash = {
				'active': active
			};
			classHash[$scope.pageStyle + JUNO_BACKGROUND_STYLE.PRIMARY] = active;

			return classHash;
		}

		ctrl.goToRecord = function goToRecord(patient)
		{
			if (patient.demographicNo !== 0)
			{
				if (ctrl.loadedSettings.hideOldEchartLinkInAppointment)
				{
					var params = {
						demographicNo: patient.demographicNo
					};
					if (angular.isDefined(patient.appointmentNo))
					{
						params.appointmentNo = patient.appointmentNo;
						params.encType = "face to face encounter with client";

						if (angularUtil.inMobileView())
						{
							ctrl.showPatientList(false);
						}
					}
					$state.go('record.summary', params);
				}
				else
				{
					let startMoment = Juno.Common.Util.getDatetimeNoTimezoneMoment(new Date(patient.date));
					let params = {
						providerNo: ctrl.provider.providerNo,
						curProviderNo: ctrl.provider.providerNo,
						providerview: ctrl.provider.providerNo,
						userName: ctrl.displayName,
						demographicNo: patient.demographicNo,

						curDate: Juno.Common.Util.formatMomentDate(moment()),
						reason: patient.reason,
						appointmentNo: patient.appointmentNo,
						appointmentDate: Juno.Common.Util.formatMomentDate(startMoment),
						startTime: Juno.Common.Util.formatMomentTime(startMoment),
						encType: "face to face encounter with client",
					};
					window.open(scheduleService.getEncounterLink(params), 'popupWindow',
							'height=800,width=1000,left=100,top=100,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no');
				}
			}
		};

		ctrl.loadTelehealthEnabled = function loadTelehealthEnabled()
		{
			ctrl.systemPreferenceApi.getPropertyEnabled("myhealthaccess_telehealth_enabled").then(
				function success(rawResults)
				{
					ctrl.telehealthEnabled = rawResults.data.body;
				},
				function failure()
				{
					$scope.displayMessages.add_standard_error("Failed to load telehealth enabled, defaulting to false");
					ctrl.telehealthEnabled = false;
				}
			);
		};

		ctrl.openTelehealthLink = function openTelehealthLink(patient)
		{
			if (patient.isVirtual)
			{
				window.open("../integrations/myhealthaccess.do?method=connect"
					+ "&demographicNo=" + encodeURIComponent(patient.demographicNo)
					+ "&siteName=" + encodeURIComponent(patient.location)
					+ "&appt=" + encodeURIComponent(patient.appointmentNo), "_blank");
			}
		};

		ctrl.toggleShowPatientList = function toggleShowPatientList()
		{
			ctrl.showPatientList(!ctrl.expandOn);
		};

		ctrl.showPatientList = function showPatientList(state)
		{
			ctrl.expandOn = state;
		};


		ctrl.isRecentPatientView = function ()
		{
			return (ctrl.activeTab === ctrl.tabEnum.recent);
		};
		ctrl.isAppointmentPatientView = function ()
		{
			return (ctrl.activeTab === ctrl.tabEnum.appointments);
		};
		ctrl.isAppointmentQueueView = function ()
		{
			return (ctrl.activeTab === ctrl.tabEnum.appointmentQueue);
		}

		ctrl.isRecentPatientViewEnabled = () =>
		{
			return securityRolesService.hasSecurityPrivileges(
				SecurityPermissions.DemographicRead, SecurityPermissions.EchartRead);
		}
		ctrl.isAppointmentPatientViewEnabled = () =>
		{
			return securityRolesService.hasSecurityPrivileges(
				SecurityPermissions.AppointmentRead, SecurityPermissions.EchartRead);
		}
		ctrl.isAppointmentQueueViewEnabled = () =>
		{
			return ctrl.show_appointment_queue && securityRolesService.hasSecurityPrivileges(
				SecurityPermissions.AppointmentRead, SecurityPermissions.AqsQueuedAppointmentsRead);
		}

		ctrl.componentEnabled = () =>
		{
			return ctrl.isRecentPatientViewEnabled() || ctrl.isAppointmentPatientViewEnabled() || ctrl.isAppointmentQueueViewEnabled();
		}

		ctrl.isAppointmentStatusSelectEnabled = () =>
		{
			return securityRolesService.hasSecurityPrivileges(SecurityPermissions.AppointmentUpdate);
		}

		ctrl.refreshRecentPatientList = function ()
		{
			var deferred = $q.defer();

			providerService.getRecentPatientList().then(
				function success(results)
				{
					ctrl.activePatientList = results;
					deferred.resolve(ctrl.activePatientList);
				},
				function error(errors)
				{
					console.log(errors);
					deferred.reject();
				});
			return deferred.promise;
		};

		ctrl.refreshAppointmentPatientList = function ()
		{
			var deferred = $q.defer();

			ctrl.scheduleApi.getAppointmentsForDay(ctrl.datepickerSelectedDate).then(
				function success(results)
				{
					ctrl.activeAppointmentList = results.data.body;
					deferred.resolve(ctrl.activeAppointmentList);
				},
				function error(errors)
				{
					console.log(errors);
					deferred.reject();
				});

			return deferred.promise;
		};
		ctrl.updateAppointmentStatus = function (appointment)
		{
			var deferred = $q.defer();
			ctrl.appointmentApi.setStatus(appointment.appointmentNo, appointment.status).then(
				function success(result)
				{
					$rootScope.$broadcast('schedule:refreshEvents');
					deferred.resolve(result);
				}
			);

			return deferred.promise;
		};

		ctrl.refresh = function refresh(filter)
		{
			var deferred = $q.defer();

			if (ctrl.isRecentPatientView())
			{
				ctrl.refreshRecentPatientList().then(
					function success()
					{
						deferred.resolve();
					}
				);
			}
			else if (ctrl.isAppointmentPatientView())
			{
				ctrl.refreshAppointmentPatientList().then(
					function success()
					{
						deferred.resolve();
					}
				);
			}

			return deferred.promise;
		};

		$scope.$on('juno:patientListRefresh', function ()
		{
			ctrl.refresh();
		});

		ctrl.stepForward = function ()
		{
			// this value has a watch on it so no need to refresh here
			ctrl.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(
				Juno.Common.Util.getDateMoment(ctrl.datepickerSelectedDate).add(1, 'days'));
		};
		ctrl.stepBack = function ()
		{
			// this value has a watch on it so no need to refresh here
			ctrl.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(
				Juno.Common.Util.getDateMoment(ctrl.datepickerSelectedDate).add(-1, 'days'));
		};

		ctrl.isInitialized = function isInitialized()
		{
			return ctrl.initialized;
		};

		ctrl.initListAutoRefresh = function initListAutoRefresh()
		{
			var deferred = $q.defer();

			// if there is already a refresh set up, stop it
			var refresh = ctrl.refreshSettings.timerVariable;
			if (refresh !== null)
			{
				clearInterval(refresh);
			}

			// get the refresh interval from preferences, or use default
			var minutes = ctrl.refreshSettings.preferredAutoRefreshMinutes;
			if (!Juno.Common.Util.exists(minutes) || !Juno.Common.Util.isIntegerString(minutes))
			{
				minutes = ctrl.refreshSettings.defaultAutoRefreshMinutes;
			}
			else
			{
				minutes = parseInt(minutes);
			}

			if (minutes > 0)
			{
				// start the auto refresh and save its ID to global state
				ctrl.refreshSettings.timerVariable = setInterval(ctrl.refresh, minutes * 60 * 1000);
			}
			deferred.resolve();

			return deferred.promise;
		};

		//=========================================================================
		// Watches
		//=========================================================================/

		ctrl.loadWatches = function loadWatches()
		{
			$scope.$watch('$ctrl.datepickerSelectedDate', function (newValue, oldValue)
			{
				if (oldValue !== newValue)
				{
					ctrl.refresh();
				}
			});
		};

		ctrl.$onInit = ctrl.init();
	}]
});
