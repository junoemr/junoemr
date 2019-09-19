/*

	Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

	This software was written for
	CloudPractice Inc.
	Victoria, British Columbia
	Canada

 */

import {ScheduleApi} from "../../generated/api/ScheduleApi";

angular.module('Schedule').component('scheduleSearch', {
	templateUrl: "src/schedule/scheduleSearch.jsp",
	bindings: {
		modalInstance: "<", // modalInstance is the parent $uibModalInstance
		resolve: "<",
	},
	controller: [
		'$q',
		'$http',
		'$httpParamSerializer',
		'providerService',
		function (
			$q,
			$http,
			$httpParamSerializer,
			providerService
		)
	{
		var ctrl = this;

		ctrl.scheduleApi = new ScheduleApi($http, $httpParamSerializer,
			'../ws/rs');

		ctrl.formattedDate = Juno.Common.Util.DisplaySettings.dateFormat;
		ctrl.formattedTime = Juno.Common.Util.DisplaySettings.timeFormat;

		ctrl.$onInit = function()
		{
			ctrl.search = {
				provider: ctrl.resolve.providerId,
				dayOfWeek: null,
				startTime: ctrl.resolve.scheduleStartTime,
				endTime: ctrl.resolve.scheduleEndTime,
				appointmentCode: null,
			};
			ctrl.clean = true;

			ctrl.resultList = [];

			ctrl.providerList = [];
			ctrl.loadProviderList();

			ctrl.appointmentCodeList = [];
			ctrl.loadAppointmentCodes();

			ctrl.dayOfWeekList = [
				{
					label: "Any",
					value: null,
				},
				{
					label: "Any Weekday",
					value: "daily",
				},
				{
					label: "Sunday",
					value: 1,
				},
				{
					label: "Monday",
					value: 2,
				},
				{
					label: "Tuesday",
					value: 3,
				},
				{
					label: "Wednesday",
					value: 4,
				},
				{
					label: "Thursday",
					value: 5,
				},
				{
					label: "Friday",
					value: 6,
				},
				{
					label: "Saturday",
					value: 7,
				},
			];

			ctrl.timeList = [];
			for(var i=0; i< 24; i++)
			{
				var timeObj = {
					label: (i<12)? (i===0?12:i)+" am" : (((i-12)===0)? 12:(i-12))+" pm",
					value: Juno.Common.Util.pad0(i) + ":00"
				};
				ctrl.timeList.push(timeObj);
			}


			ctrl.working = false;
		};

		ctrl.cancel = function cancel()
		{
			ctrl.modalInstance.dismiss("cancel");
		};

		ctrl.addAppointment = function cancel(result)
		{
			var scheduleSlot = result.scheduleSlot;
			var provider = result.provider;

			var startTime = moment(scheduleSlot.appointmentDateTime);
			var endTime = angular.copy(startTime).add(scheduleSlot.durationMinutes, 'minutes');
			var resourceId = provider.providerNo;

			ctrl.modalInstance.close({
				start:startTime,
				end: endTime,
				resourceId: resourceId
			});
		};

		ctrl.reset = function reset()
		{
			ctrl.search = {
				provider: ctrl.resolve.providerId,
				dayOfWeek: null,
				startTime: ctrl.resolve.scheduleStartTime,
				endTime: ctrl.resolve.scheduleEndTime,
				appointmentCode: null,
			};
			ctrl.resultList = [];
			ctrl.clean = true;
		};

		ctrl.searchSchedules = function searchSchedules()
		{
			var deferred = $q.defer();
			ctrl.working = true;

			ctrl.scheduleApi.searchAvailable(
				ctrl.search.provider,
				ctrl.search.dayOfWeek,
				ctrl.search.startTime,
				ctrl.search.endTime,
				ctrl.search.appointmentCode,
				8,
			).then(
				function success(results)
				{
					ctrl.resultList = results.data.body;
					ctrl.working = false;
					ctrl.clean = false;
					deferred.resolve(results.data.body);
				},
				function failure(errors)
				{
					console.error(errors);
					ctrl.working = false;
					deferred.reject();
				}
			);
			return deferred;
		};

		ctrl.isWorking = function isWorking()
		{
			return ctrl.working;
		};

		ctrl.loadProviderList = function loadProviderList()
		{
			var deferred = $q.defer();
			providerService.searchProviders({
				active: true
			}).then(
				function success(providerList)
				{
					ctrl.providerList = providerList.map(provider => (
						{
							value: provider.providerNo,
							label: provider.name
						}));
					deferred.resolve(ctrl.providerList);
				},
				function failure()
				{
					deferred.reject();
				}
			);
			return deferred;
		};

		ctrl.loadAppointmentCodes = function loadAppointmentCodes()
		{
			var deferred = $q.defer();
			ctrl.scheduleApi.getScheduleTemplateCodes().then(
				function success(result)
				{
					var codeList = result.data.body;
					ctrl.appointmentCodeList = codeList.map(code => (
						{
							value: code.code,
							label: "(" + code.code + ") " + code.description
						}));
					deferred.resolve(ctrl.appointmentCodeList);
				},
				function failure()
				{
					deferred.reject();
				}
			);
			return deferred;
		};
	}]
});