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
angular.module("Common.Services").service("billingService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/';

		service.getUniqueServiceTypes = function getUniqueServiceTypes()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'billing/uniqueServiceTypes',
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("billingService::getUniqueServiceTypes error", errors);
					deferred.reject("An error occured while fetching billing service types");
				});

			return deferred.promise;
		};

		service.getBillingRegion = function getBillingRegion()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'billing/billingRegion',
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("billingService::getBillingRegion error", errorsz);
					deferred.reject("An error occured while setting billingRegion");
				});

			return deferred.promise;
		};

		service.getDefaultView = function getDefaultView()
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'billing/defaultView',
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("billingService::getDefaultView error", errors);
					deferred.reject("An error occured while setting defaultView");
				});

			return deferred.promise;
		};

		return service;
	}
]);