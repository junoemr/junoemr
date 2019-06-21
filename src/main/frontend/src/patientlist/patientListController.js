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

angular.module('PatientList').controller('PatientList.PatientListController', [

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

		controller.tabEnum = Object.freeze({
			recent:0,
			appointments:1
		});
		controller.activeTab = controller.tabEnum.recent;
		controller.activePatientList = [];

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
			providerService.getRecentPatientList().then(
				function success(results)
				{
					controller.activePatientList = results;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};

		controller.refreshAppointmentPatientList = function()
		{
			controller.scheduleApi.getAppointmentsForDay(controller.datepickerSelectedDate).then(
				function success(results)
				{
					controller.activePatientList = results.data.body.patients;
				},
				function error(errors)
				{
					console.log(errors);
				});
		};
		controller.refresh = function refresh(filter)
		{
			if(controller.isRecentPatientView() === true)
			{
				controller.refreshRecentPatientList();
			}
			else if(controller.isAppointmentPatientView() === true)
			{
				controller.refreshAppointmentPatientList();
			}
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

		$scope.$watch('patientListCtrl.datepickerSelectedDate', function(newValue, oldValue)
		{
			if(controller.isInitialized())
			{
				controller.refresh();
			}
		});

		controller.init();
	}
]);
