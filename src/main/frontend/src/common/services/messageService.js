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

angular.module("Common.Services").service("messageService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/messaging';

		service.getUnread = function getUnread(limit)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/unread?startIndex=0&limit=' + encodeURIComponent(limit),
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("messageService::getUnread error", errors);
					deferred.reject("An error occurred while getting messages");
				});

			return deferred.promise;
		};

		service.getUnreadCount = function getUnreadCount()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/count',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("messageService::getUnreadCount error", errors);
					deferred.reject("An error occurred while getting messages");
				});

			return deferred.promise;
		};

		return service;
	}
]);