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
angular.module("Common.Services").service("demographicsService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/demographics';

		service.SEARCH_MODE = Object.freeze(
			{
				Name: "search_name",
				DOB: "search_dob",
				Phone: "search_phone",
				Hin: "search_hin",
				Address: "search_address",
				Email: "search_email",
				ChartNo: "search_chart_no",
				DemographicNo: "search_demographic_no"
			});

		service.STATUS_MODE = Object.freeze(
			{
				ALL: "all",
				ACTIVE: "active",
				INACTIVE: "inactive"
			});

		service.quickSearch = function quickSearch(search)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				query: search
			};

			junoHttp.get(service.apiPath + '/quickSearch', config).then(
				function success(results)
				{
					deferred.resolve(results);
				},
				function error(errors)
				{
					console.log("demographicsService::quickSearch error", errors);
					deferred.reject("An error occurred while searching");
				});

			return deferred.promise;
		};

		service.search = function search(search, page, perPage)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				jsonData: search,
				page: page,
				perPage: perPage
			};

			junoHttp.get(service.apiPath + '/search', config).then(
				function success(results)
				{
					deferred.resolve(results);
				},
				function error(errors)
				{
					console.log("demographicsService::search error", errors);
					deferred.reject("An error occurred while searching");
				});

			return deferred.promise;
		};

		service.searchIntegrator = function searchIntegrator(search, itemsToReturn)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				jsonData: search,
				itemsToReturn: itemsToReturn
			};

			junoHttp.get(service.apiPath + '/searchIntegrator', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("demographicsService::searchIntegrator error", errors);
					deferred.reject("An error occurred while searching");
				});

			return deferred.promise;
		};

		service.getStatusList = function getStatusList(listType)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				type: listType
			};

			junoHttp.get(service.apiPath + '/statusList', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("demographicsService::getStatusList error", errors);
					deferred.reject("An error occurred while getting status list");
				});

			return deferred.promise;
		};

		return service;
	}
]);