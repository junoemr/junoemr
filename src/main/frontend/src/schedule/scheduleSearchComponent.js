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

		ctrl.$onInit = function()
		{
			ctrl.search = {
				provider: ctrl.resolve.providerId,
				dayOfWeek: null,
				startTime: ctrl.resolve.scheduleStartTime,
				endTime: ctrl.resolve.scheduleEndTime,
				appointmentCode: null,
			};

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
					label: "Monday",
					value: 0,
				},
				{
					label: "Tuesday",
					value: 1,
				},
				{
					label: "Wednesday",
					value: 2,
				},
				{
					label: "Thursday",
					value: 3,
				},
				{
					label: "Friday",
					value: 4,
				},
				{
					label: "Saturday",
					value: 5,
				},
				{
					label: "Sunday",
					value: 6,
				},
			];

			ctrl.timeList = [];
			for(var i=0; i< 24; i++)
			{
				var timeObj = {
					label: (i<12)? (i===0?12:i)+" am" : ((i-12)===0?12:(i-12))+" pm",
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

		ctrl.addAppointment = function cancel(calEvent)
		{
			ctrl.modalInstance.close(calEvent);
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
		};

		ctrl.search = function search()
		{

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
					console.info(result);
					var codeList = result.data.body;
					ctrl.appointmentCodeList = codeList.map(code => (
						{
							value: code.code,
							label: code.description
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