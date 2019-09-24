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

angular.module("Common.Services").service("providersService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/providers';

		service.search = function search(searchText, searchMode, page, perPage)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				searchText: searchText,
				searchMode: searchMode,
				page: page,
				perPage: perPage
			};

			junoHttp.get(service.apiPath, config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providersService::search error", errors);
					deferred.reject("An error occurred while getting providers data");
				});

			return deferred.promise;
		};

		service.getBySecurityRole = function getBySecurityRole(role)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				role: role
			};

			junoHttp.get(service.apiPath + '/bySecurityRole', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providersService::getBySecurityRole error", errors);
					deferred.reject("An error occurred while getting providers data");
				});

			return deferred.promise;
		};

		service.getByType = function getByProviderType(type)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				type: type
			};

			junoHttp.get(service.apiPath + '/byType', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providersService::getByType error", errors);
					deferred.reject("An error occurred while getting providers data");
				});

			return deferred.promise;
		};

		service.getAll = function getAll()
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/all', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("providersService::getAll error", errors);
					deferred.reject("An error occurred while getting providers data");
				});

			return deferred.promise;
		};

		return service;
	}
]);