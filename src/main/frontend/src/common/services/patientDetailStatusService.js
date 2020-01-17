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
angular.module("Common.Services").service("patientDetailStatusService", [
	'$http', '$q',
	function($http, $q)
	{

		var service = {};

		service.apiPath = '../ws/rs/patientDetailStatusService';

		service.getStatus = function getStatus(demographicNo)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + "/getStatus?demographicNo=" + encodeURIComponent(demographicNo),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("patientDetailStatusService::getStatus error", errors);
					deferred.reject("An error occurred while fetching status");
				});

			return deferred.promise;
		};

		service.validateHC = function validateHC(healthCardNo, versionCode)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + "/validateHC?hin=" + encodeURIComponent(healthCardNo) +
				"&ver=" + encodeURIComponent(versionCode),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("patientDetailStatusService::validateHC error", errors);
					deferred.reject("An error occurred while fetching health card validation info");
				});

			return deferred.promise;
		};

		service.isUniqueHC = function isUniqueHC(healthCardNo, demographicNo)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + "/isUniqueHC?hin=" +
				encodeURIComponent(healthCardNo) + "&demographicNo=" +
				encodeURIComponent(demographicNo),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("patientDetailStatusService::isUniqueHC error", errors);
					deferred.reject("An error occurred while checking health card uniqueness");
				});

			return deferred.promise;
		};

		// check the patients eligibility, returning a descriptive message about the result
		service.getEligibilityInfo = function (demographicNo)
		{
			let deferred = $q.defer();
			$http.get("../billing/CA/BC/ManageTeleplan.do?demographic=" + demographicNo + "&method=checkElig&rand=" + Math.random()).then(
					function success(result)
					{
						deferred.resolve(result.data);
					},
					function error(result)
					{
						console.error("Failed to perform eligibility check on demographic, " + demographicNo + " With error: " + result);
						deferred.reject("Failed to perform eligibility check on demographic, " + demographicNo + " With error: " + result);
					}
			);
			return deferred.promise;
		};

		return service;
	}
]);