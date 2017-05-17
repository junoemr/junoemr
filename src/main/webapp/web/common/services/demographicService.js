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
angular.module("Common.Services").service("demographicService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/';

		service.getDemographic = function getDemographic(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'demographics/' + encodeURIComponent(demographicNo),
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("demographicServices::getDemographic error", error);
					deferred.reject("An error occurred while fetching demographic");
				});

			return deferred.promise;
		};

		service.saveDemographic = function saveDemographic(demographic)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + 'demographics', demographic).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("demographicServices::updateDemographic error", error);
					deferred.reject("An error occurred while saving demographic");
				});

			return deferred.promise;
		};

		service.updateDemographic = function updateDemographic(demographic)
		{
			var deferred = $q.defer();

			$http.put(service.apiPath + 'demographics', demographic).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("demographicServices::updateDemographic error", error);
					deferred.reject("An error occurred while updating demographic");
				});

			return deferred.promise;
		};

		service.search = function search(search, startIndex, itemsToReturn)
		{
			var deferred = $q.defer();
			$http.post(service.apiPath + 'demographics/search?startIndex=' +
				encodeURIComponent(startIndex) + "&itemsToReturn=" +
				encodeURIComponent(itemsToReturn), search).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("demographicServices::search error", error);
					deferred.reject("An error occurred while searching");
				});

			return deferred.promise;
		};

		service.searchIntegrator = function searchIntegrator(search, itemsToReturn)
		{
			var deferred = $q.defer();
			$http.post(service.apiPath + 'demographics/searchIntegrator?itemsToReturn=' +
				encodeURIComponent(itemsToReturn), search).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("demographicServices::searchIntegrator error", error);
					deferred.reject("An error occurred while searching");
				});

			return deferred.promise;
		};

		return service;
	}
]);