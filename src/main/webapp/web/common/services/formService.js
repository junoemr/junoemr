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
angular.module("Common.Services").service("formService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/forms';

		service.getAllFormsByHeading = function getAllFormsByHeading(demographicNo, heading)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/all?heading=' + encodeURIComponent(heading),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getAllFormsByHeading error", errors);
					deferred.reject("An error occurred while fetching forms");
				});

			return deferred.promise;
		};

		service.getAllEncounterForms = function getAllEncounterForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/allEncounterForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getAllEncounterForms error", errors);
					deferred.reject("An error occurred while fetching encounter forms");
				});

			return deferred.promise;
		};

		service.getSelectedEncounterForms = function getSelectedEncounterForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/selectedEncounterForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getSelectedEncounterForms error", errors);
					deferred.reject("An error occurred while fetching selected encounter forms");
				});

			return deferred.promise;
		};

		service.getCompletedEncounterForms = function getCompletedEncounterForms(demographicNo)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/' + demographicNo + '/completedEncounterForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getCompletedEncounterForms error", errors);
					deferred.reject("An error occurred while fetching completed encounter forms");
				});

			return deferred.promise;
		};

		service.getAllEForms = function getAllEForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/allEForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("formService::getAllEForms error", errors);
					deferred.reject("An error occurred while fetching eforms");
				});

			return deferred.promise;
		};

		service.getGroupNames = function getGroupNames()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/groupNames',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("formService::getGroupNames error", errors);
					deferred.reject("An error occurred while fetching group names");
				});

			return deferred.promise;
		};

		service.getFormGroups = function getFormGroups()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/getFormGroups',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFormGroups error", errors);
					deferred.reject("An error occurred while fetching form groups");
				});

			return deferred.promise;
		};

		service.getFavouriteFormGroup = function getFavouriteFormGroup()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/getFavouriteFormGroup',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFavouriteFormGroup error", errors);
					deferred.reject("An error occurred while fetching favourite form groups");
				});

			return deferred.promise;
		};

		service.getFormOptions = function getFormOptions(demographicNo)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) + '/formOptions',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFormOptions error", errors);
					deferred.reject("An error occurred while fetching form options");
				});

			return deferred.promise;
		};
		
		service.saveEForm = function saveEForm()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/saveEForm',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::saveEForm error", errors);
					deferred.reject("An error occurred while saving an EForm");
				});

			return deferred.promise;
		};
		

		return service;
	}
]);