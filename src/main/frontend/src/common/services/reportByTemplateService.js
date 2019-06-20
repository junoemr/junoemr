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
angular.module("Common.Services").service("reportByTemplateService", [
	'$http',
	'$q',
	'junoHttp',
	function($http,
	         $q,
	         junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs';

		service.isK2AInit = function isK2AInit()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/reportByTemplate/K2AActive',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("reportByTemplateService::isK2AInit error", errors);
					deferred.reject("An error occurred while fetching k2a content");
				});

			return deferred.promise;
		};

		service.getAllK2AReports = function getAllK2AReports()
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			junoHttp.get(service.apiPath + '/reportByTemplate/allReports', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("reportByTemplateService::getAllK2AReports error", errors);
					deferred.reject("An error occurred while fetching k2a reports");
				});

			return deferred.promise;
		};

		service.getK2AReportById = function getK2AReportById(id)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/reportByTemplate/getReportById/' + encodeURIComponent(id),
				method: "POST",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("reportByTemplateService::getK2AReportById error", errors);
					deferred.reject("An error occurred while fetching k2a report");
				});

			return deferred.promise;
		};

		service.getK2AUrl = function getK2AUrl()
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeaders();
			junoHttp.get(service.apiPath + '/reportByTemplate/K2AUrl/', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("reportByTemplateService::getK2AUrl error", errors);
					deferred.reject("An error occurred while fetching k2a url");
				});

			return deferred.promise;
		};

		return service;
	}
]);