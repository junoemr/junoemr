'use strict';

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
angular.module("Common.Services").service("scheduleService", [
	'$http', '$q',
	function ($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/';

		service.getStatuses = function getStatuses()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'schedule/statuses',
				service.configHeadersWithCache).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::getStatuses error", error);
					deferred.reject("An error occured while fetching statuses");
				});

			return deferred.promise;
		};

		service.getTypes = function getTypes()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'schedule/types',
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::getStatuses error", error);
					deferred.reject("An error occured while fetching types");
				});

			return deferred.promise;
		};

		service.getAppointments = function getAppointments(day)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'schedule/day/' +
				encodeURIComponent(day)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::getAppointments error", error);
					deferred.reject("An error occured while getting appointments");
				});

			return deferred.promise;
		};

		service.addAppointment = function addAppointment(appointment)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/add',
				method: "POST",
				data: JSON.stringify(appointment),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::addAppointment error", error);
					deferred.reject("An error occured while saving appointment");
				});

			return deferred.promise;
		};

		service.getAppointment = function getAppointment(apptNo)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/getAppointment',
				method: "POST",
				data: { 'id': apptNo },
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data.appointment);
				},
				function error(error)
				{
					console.log("scheduleService::getAppointment error", error);
					deferred.reject("An error occured while getting appointment");
				});

			return deferred.promise;
		};

		service.deleteAppointment = function deleteAppointment(apptNo)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/deleteAppointment',
				method: "POST",
				data: { 'id': apptNo },
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::deleteAppointment error", error);
					deferred.reject("An error occured while deleting appointment");
				});

			return deferred.promise;
		};

		service.appointmentHistory = function appointmentHistory(demoNo)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/' +
				encodeURIComponent(demoNo) + "/appointmentHistory",
				method: "POST",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::appointmentHistory error", error);
					deferred.reject("An error occured while getting appointment history");
				});

			return deferred.promise;
		};

		service.cancelAppointment = function cancelAppointment(apptNo)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/appointment/' +
				encodeURIComponent(apptNo) + "/updateStatus",
				method: "POST",
				data: { status: 'C' },
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::cancelAppointment error", error);
					deferred.reject("An error occured while cancelling appointment");
				});

			return deferred.promise;
		};

		service.noShowAppointment = function noShowAppointment(apptNo)
		{
			var deferred = $q.defer();

			$http({
				url: service.apiPath + 'schedule/appointment/' +
				encodeURIComponent(apptNo) + "/updateStatus",
				method: "POST",
				data: { status: 'N' },
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("scheduleService::noShowAppointment error", error);
					deferred.reject("An error occured while setting no show appointment");
				});

			return deferred.promise;
		};

		return service;
	}
]);
