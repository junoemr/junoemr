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
angular.module("Common.Services").service("uxService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/recordUX';

		service.menu = function menu(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/recordMenu').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("uxService::menu error", errors);
					deferred.reject("An error occurred while fetching menu");
				});

			return deferred.promise;
		};

		service.searchTemplates = function searchTemplates(search, startIndex, itemsToReturn)
		{
			var deferred = $q.defer();

			$http.post(service.apiPath + '/searchTemplates?startIndex=' +
				encodeURIComponent(startIndex) + "&itemsToReturn=" +
				encodeURIComponent(itemsToReturn), search).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("uxService::searchTemplates error", errors);
					deferred.reject("An error occurred while searching templates");
				});

			return deferred.promise;
		};

		service.getTemplate = function getTemplate(name)
		{
			var deferred = $q.defer();
			$http.post(service.apiPath + '/template', name).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("uxService::getTemplate error", errors);
					deferred.reject("An error occurred while fetching template");
				});

			return deferred.promise;
		};
		
		service.getAllDashboards = function ()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/dashboards').then(
					function success(results)
					{
						deferred.resolve(results.data.body);
					},
					function error(errors)
					{
						console.log("uxService:getAllDashboards error", errors);
						deferred.reject("An error occurred while fetching dashboard list.")
					}
			);
			return deferred.promise;
		};

		return service;
	}
]);