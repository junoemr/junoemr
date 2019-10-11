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
angular.module("Common.Services").service("personaService", [
	'$http', '$q',
	function($http, $q)
	{
		var service = {};

		service.apiPath = '../ws/rs/persona';

		service.getDashboardMenu = function getDashboardMenu()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/dashboardMenu',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("personaService::getDashboardMenu error", errors);
					deferred.reject("An error occured while getting the dashboard menu from persona");
				});

			return deferred.promise;
		};

		service.getNavBar = function getNavBar()
		{
			var deferred = $q.defer();
			$http(
			{
				url: service.apiPath + '/navbar',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("personaService::getNavBar error", errors);
					deferred.reject("An error occured while getting navbar from persona");
				});

			return deferred.promise;
		};

		service.getAdminNav = function()
		{
			var deferred = $q.defer();
			$http(
				{
					url: service.apiPath + '/adminNav',
					method: "GET",
					headers: Juno.Common.ServiceHelper.configHeaders()
				}).then(
				function success(results)
				{
					if (results.data.status === "SUCCESS")
					{
						deferred.resolve(results.data.body);
					}
					else
					{
						console.error("personaService::getAdminNav error", results.data.error);
						deferred.reject("An error occured while getting admin nav from persona");
					}
				},
				function error(errors)
				{
					console.error("personaService::getAdminNav error", errors);
					deferred.reject("An error occured while getting admin nav from persona");
				});

			return deferred.promise;
		};

		service.getPatientListConfig = function getPatientListConfig()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/patientList/config',
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("personaService::getPatientListConfig error", errors);
					deferred.reject("An error occured while getting getPatientListConfig from persona");
				});

			return deferred.promise;
		};

		service.setPatientListConfig = function setPatientListConfig(patientListConfig)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/patientList/config',
				method: "POST",
				data: patientListConfig,
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(errors)
				{
					console.log("personaService::setPatientListConfig error", errors);
					deferred.reject("An error occured while setting setPatientListConfig from persona");
				});

			return deferred.promise;
		};

		service.setCurrentProgram = function setCurrentProgram(programId)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath +
					'/setDefaultProgramInDomain?programId=' +
					encodeURIComponent(programId),
				method: "GET",
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(errors)
				{
					console.log("personaService::setCurrentProgram error", errors);
					deferred.reject("An error occured while setting current");
				});

			return deferred.promise;
		};

		service.getDashboardPreferences = function getDashboardPreferences()
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/preferences',
				method: "POST",
				data:
				{
					type: 'dashboard'
				},
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(errors)
				{
					console.log("personaService::getDashboardPreferences error", errors);
					deferred.reject("An error occured while getting preferences from persona");
				});

			return deferred.promise;
		};

		service.updateDashboardPreferences = function updateDashboardPreferences(prefs)
		{
			var deferred = $q.defer();

			$http(
			{
				url: service.apiPath + '/updatePreferences',
				method: "POST",
				data: JSON.stringify(prefs),
				headers: Juno.Common.ServiceHelper.configHeaders()
			}).then(
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(errors)
				{
					console.log("personaService::updateDashboardPreferences error", errors);
					deferred.reject("An error occured while updating preferences");
				});
			return deferred.promise;
		};

		return service;
	}
]);