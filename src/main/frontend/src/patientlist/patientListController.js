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

angular.module('PatientList').controller('PatientList.PatientListController', [

	'$rootScope',
	'$scope',
	'$q',
	'$http',
	'$httpParamSerializer',
	'$state',
	'$uibModal',
	'angularUtil',
	'Navigation',
	'personaService',
	'scheduleService',
	'providerService',

	function(
		$rootScope,
		$scope,
		$q,
		$http,
		$httpParamSerializer,
		$state,
		$uibModal,
		angularUtil,
		Navigation,
		personaService,
		scheduleService,
		providerService)
	{

		var controller = this;
		controller.initialized = false;

		controller.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');
		controller.appointmentApi = new AppointmentApi($http, $httpParamSerializer,
			'../ws/rs');

		controller.tabEnum = Object.freeze({
			appointments:0,
			recent:1,
		});
		controller.activeTab = controller.tabEnum.appointments;
		controller.activePatientList = [];
		controller.activePatientListUnregisterFunctions = [];

		//for filter box
		controller.query = '';
		controller.datepickerSelectedDate = null;

		controller.refreshSettings = {
			scheduleAutoRefresh: null,
			defaultAutoRefreshMinutes: 1,
			scheduleAutoRefreshMinutes: null
		};

		controller.eventStatusOptions = [];
		controller.selectedEventStatus = null;

		controller.init = function()
		{
			scheduleService.loadEventStatuses().then(
				function success() {
					controller.eventStatusOptions = scheduleService.eventStatuses;

					controller.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(moment());
					controller.changeTab(controller.activeTab);

					controller.loadWatches();
					controller.initialized = true;
				}
			);
		};

		controller.changeTab = function changeTab(tabId)
		{
			controller.activeTab = tabId;
			controller.refresh();
		};

		controller.goToRecord = function goToRecord(patient)
		{
			if (patient.demographicNo != 0)
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
						controller.hidePatientList();
					}
				}
				$state.go('record.summary', params);
			}
		};

		$scope.$on('togglePatientListFilter', function(event, data)
		{
			console.log("received a togglePatientListFilter event:" + data);
			controller.showFilter = data;
		});

		controller.showPatientList = function showPatientList()
		{
			$scope.$emit('configureShowPatientList', true);
		};

		controller.hidePatientList = function hidePatientList()
		{
			$scope.$emit('configureShowPatientList', false);
		};

		controller.isRecentPatientView = function()
		{
			return (controller.activeTab === controller.tabEnum.recent);
		};
		controller.isAppointmentPatientView = function()
		{
			return (controller.activeTab === controller.tabEnum.appointments);
		};

		controller.refreshRecentPatientList = function()
		{
			var deferred = $q.defer();

			controller.unregisterPatientListWatches();
			providerService.getRecentPatientList().then(
				function success(results)
				{
					controller.activePatientList = results;
					deferred.resolve(controller.activePatientList);
				},
				function error(errors)
				{
					console.log(errors);
					deferred.reject();
				});
			return deferred.promise;
		};

		controller.refreshAppointmentPatientList = function()
		{
			var deferred = $q.defer();

			controller.unregisterPatientListWatches();
			controller.scheduleApi.getAppointmentsForDay(controller.datepickerSelectedDate).then(
				function success(results)
				{
					controller.activePatientList = results.data.body.patients;
					controller.registerPatientListWatches();
					deferred.resolve(controller.activePatientList);
				},
				function error(errors)
				{
					console.log(errors);
					deferred.reject();
				});

			return deferred.promise;
		};
		controller.unregisterPatientListWatches = function()
		{
			for(var i = 0; i< controller.activePatientListUnregisterFunctions.length; i++)
			{
				// deregister the watch function by calling it's function
				controller.activePatientListUnregisterFunctions[i]();
			}
			controller.activePatientListUnregisterFunctions = [];
		};
		controller.registerPatientListWatches = function()
		{
			for(var i=0; i < controller.activePatientList.length; i++)
			{
				var deregisterFn = $scope.$watch('patientListCtrl.activePatientList['+i+']', function (newValue, oldValue)
				{
					if (oldValue !== newValue)
					{
						controller.updateAppointmentStatus(newValue);
					}
				}, true);
				controller.activePatientListUnregisterFunctions.push(deregisterFn);
			}
		};
		controller.updateAppointmentStatus = function(appointment)
		{
			var deferred = $q.defer();

			controller.appointmentApi.setStatus(appointment.appointmentNo, appointment.status).then(
				function success(result)
				{
					$rootScope.$broadcast('schedule:refreshEvents');
					deferred.resolve(result);
				}
			);

			return deferred.promise;
		};

		controller.refresh = function refresh(filter)
		{
			var deferred = $q.defer();

			if(controller.isRecentPatientView())
			{
				controller.refreshRecentPatientList().then(
					function success()
					{
						deferred.resolve();
					}
				);
			}
			else if(controller.isAppointmentPatientView())
			{
				controller.refreshAppointmentPatientList().then(
					function success()
					{
						deferred.resolve();
					}
				);
			}

			return deferred.promise;
		};

		$scope.$on('juno:patientListRefresh', function()
		{
			controller.refresh();
		});

		controller.stepForward = function()
		{
			// this value has a watch on it so no need to refresh here
			controller.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(
				Juno.Common.Util.getDateMoment(controller.datepickerSelectedDate).add(1, 'days'));
		};
		controller.stepBack = function()
		{
			// this value has a watch on it so no need to refresh here
			controller.datepickerSelectedDate = Juno.Common.Util.formatMomentDate(
				Juno.Common.Util.getDateMoment(controller.datepickerSelectedDate).add(-1, 'days'));
		};

		controller.isInitialized = function isInitialized()
		{
			return controller.initialized;
		};

		//=========================================================================
		// Watches
		//=========================================================================/

		controller.loadWatches = function loadWatches()
		{
			$scope.$watch('patientListCtrl.datepickerSelectedDate', function (newValue, oldValue)
			{
				if (oldValue !== newValue)
				{
					controller.refresh();
				}
			});
		};

		controller.init();
	}
]);
