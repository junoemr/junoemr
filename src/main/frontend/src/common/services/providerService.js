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

angular.module("Common.Services").service("providerService", [
	'$http',
	'$q',
	'junoHttp',
	function($http, $q, junoHttp)
	{

		var service = {};

		service.apiPath = '../ws/rs/providerService';

		service.getMe = function getMe()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/provider/me', service.configHeaders).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providerService::getMe error", errors);
					deferred.reject("An error occured while getting user data");
				});

			return deferred.promise;
		};

		service.getProvider = function getProvider(id)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/provider/' + encodeURIComponent(id),
				service.configHeaders).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providerService::getProvider error", errors);
					deferred.reject("An error occured while getting user data");
				});

			return deferred.promise;
		};

		service.searchProviders = function searchProviders(filter)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/providers/search',
				method: "POST",
				data: JSON.stringify(filter),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("providerService::searchProviders error", errors);
					deferred.reject("An error occured while fetching provider list");
				});

			return deferred.promise;
		};

		//TODO move to its own service
		service.getSettings = function getSettings()
		{
			var deferred = $q.defer();

			$http(
			{
				url: '../ws/rs/providerSettings/get',
				method: "GET"
			}).then(
				function success(results)
				{
					deferred.resolve(results.data.content[0]);
				},
				function error(errors)
				{
					console.log("providerService::getSettings error", errors);
					deferred.reject("An error occured while fetching provider settings");
				});

			return deferred.promise;
		};

		//TODO move to its own service
		service.saveSettings = function saveSettings(providerNo, settings)
		{
			var deferred = $q.defer();

			$http(
			{
				url: '../ws/rs/providerSettings/' + providerNo + '/save',
				method: "POST",
				data: JSON.stringify(settings),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providerService::saveSettings error", errors);
					deferred.reject("An error occured while saving settings");
				});

			return deferred.promise;
		};

		service.getActiveTeams = function getActiveTeams()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/getActiveTeams',
				method: "GET"
			}).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("providerService::getActiveTeams error", errors);
					deferred.reject("An error occured while fetching provider teams");
				});

			return deferred.promise;
		};

		service.getRecentPatientList = function getRecentPatientList()
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/getRecentDemographicsViewed', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					deferred.reject("An error occurred while getting RecentDemographicsViewed: " + error);
				});
			return deferred.promise;
		};

		return service;
	}
]);