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

angular.module("Common.Services").service("inboxService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/inbox';

		service.getDashboardItems = function getDashboardItems(limit)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/mine?limit=' + encodeURIComponent(limit),
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("inboxService::getDashboardItems error", errors);
					deferred.reject("An error occured while getting inbox content");
				});

			return deferred.promise;
		};

		service.getUnAckLabDocCount = function getUnAckLabDocCount()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/mine/count',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("inboxService::getUnAckLabDocCount error", errors);
					deferred.reject("An error occured while getting inbox content");
				});
			return deferred.promise;
		};
		service.getInboxCountByStatus = function getInboxCountByStatus(providerId, statusCode)
		{
			var deferred = $q.defer();

			$http(
				{
					url: service.apiPath + '/' +encodeURIComponent(providerId)+ '/' +encodeURIComponent(statusCode)+ '/count',
					method: "GET",
					headers: Juno.Common.ServiceHelper.configHeaders()
				}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("inboxService::getUnAckLabDocCount error", errors);
					deferred.reject("An error occured while getting inbox content");
				});
			return deferred.promise;
		};

		return service;
	}
]);