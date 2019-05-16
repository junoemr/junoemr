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
angular.module("Common.Services").service("ticklerService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/tickler';

		service.setCompleted = function setCompleted(ticklerIds)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/complete',
				method: "POST",
				data: JSON.stringify(
				{
					"ticklers": ticklerIds
				}),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::setCompleted error", errors);
					deferred.reject("An error occurred while setting ticklers to completed status");
				});

			return deferred.promise;
		};

		service.setDeleted = function setDeleted(ticklerIds)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/delete',
				method: "POST",
				data: JSON.stringify(
				{
					"ticklers": ticklerIds
				}),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::setDeleted error", errors);
					deferred.reject("An error occurred while setting ticklers to deleted status");
				});

			return deferred.promise;
		};

		service.search = function search(filter, startIndex, limit)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/search?startIndex=' +
					encodeURIComponent(startIndex) +
					'&limit=' +
					encodeURIComponent(limit),
				method: "POST",
				data: JSON.stringify(filter),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::search error", errors);
					deferred.reject("An error occurred while searching ticklers");
				});

			return deferred.promise;
		};

		service.update = function update(tickler)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/update',
				method: "POST",
				data: JSON.stringify(tickler),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::update error", errors);
					deferred.reject("An error occurred while updating tickler");
				});

			return deferred.promise;
		};

		service.getTextSuggestions = function getTextSuggestions()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/textSuggestions',
				method: "GET"
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::getTextSuggestions error", errors);
					deferred.reject("An error occurred while getting tickler text suggestions");
				});

			return deferred.promise;
		};

		service.add = function add(tickler)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/add',
				method: "POST",
				data: JSON.stringify(tickler),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("ticklerService::add error", errors);
					deferred.reject("An error occurred while saving tickler");
				});

			return deferred.promise;
		};

		return service;
	}
]);