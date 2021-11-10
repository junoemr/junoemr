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
angular.module("Common.Services").service("summaryService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/recordUX';

		service.getSummaryHeaders = function(demographicNo, key)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/summary/' + key).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getSummaryHeaders error", errors);
					deferred.reject("An error occurred while fetching summary headers");
				});

			return deferred.promise;
		};

		service.getFullSummary = function(demographicNo, summaryCode)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/fullSummary/' + summaryCode).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getFullSummary error", errors);
					deferred.reject("An error occurred while fetching full summary");
				});

			return deferred.promise;
		};

		service.getFamilyHistory = function(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getFamilyHistory').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getFamilyHistory error", errors);
					deferred.reject("An error occurred while fetching family history");
				});

			return deferred.promise;
		};

		service.getMedicalHistory = function(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getMedicalHistory').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getMedicalHistory error", errors);
					deferred.reject("An error occurred while fetching medical history");
				});

			return deferred.promise;
		};

		service.getSocialHistory = function getSocialHistory(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getSocialHistory').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getSocialHistory error", errors);
					deferred.reject("An error occurred while fetching social history");
				});

			return deferred.promise;
		};

		service.getOngoingConcerns = function(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getOngoingConcerns').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getOngoingConcerns error", errors);
					deferred.reject("An error occurred while fetching ongoing concerns");
				});

			return deferred.promise;
		};

		service.getDiseaseRegistry = function(demographicNo)
		{
			let deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getDiseaseRegistry').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getDiseaseRegistry error", errors);
					deferred.reject("An error occurred while fetching disease registry");
				});

			return deferred.promise;
		};

		service.getOtherMeds = function getOtherMeds(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getOtherMeds').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getOtherMeds error", errors);
					deferred.reject("An error occurred while fetching other meds");
				});

			return deferred.promise;
		};

		service.getReminders = function getReminders(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getReminders').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getReminders error", errors);
					deferred.reject("An error occurred while fetching reminders");
				});

			return deferred.promise;
		};

		service.getRiskFactors = function getRiskFactors(demographicNo)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/getRiskFactors').then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("summaryService::getRiskFactors error", errors);
					deferred.reject("An error occurred while fetching risk factors");
				});

			return deferred.promise;
		};

		return service;
	}
]);