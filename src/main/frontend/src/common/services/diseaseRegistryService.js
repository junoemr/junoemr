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
angular.module("Common.Services").service("diseaseRegistryService", [
	'$http',
	'$q',
	'junoHttp',

	function(
		$http,
		$q,
		junoHttp
        )
	{
		var service = {};

		service.apiPath = '../ws/rs/dxRegisty/';

		service.getQuickLists = function getQuickLists()
		{
			var deferred = $q.defer();
			junoHttp.get(service.apiPath + 'quickLists/',
				Juno.Common.ServiceHelper.configHeadersWithCache()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("diseaseRegistryService::getQuickLists error", errors);
					deferred.reject("An error occurred while fetching quick lists");
				});

			return deferred.promise;
		};

        service.getIssueQuickLists = function getIssueQuickLists()
        {
            var deferred = $q.defer();
            junoHttp.get(service.apiPath + 'issueQuickLists/',
                Juno.Common.ServiceHelper.configHeadersWithCache()).then(
                function success(results)
                {
                    deferred.resolve(results.data);
                },
                function error(errors)
                {
                    console.log("diseaseRegistryService::getIssueQuickLists error", errors);
                    deferred.reject("An error occurred while fetching quick lists");
                });

            return deferred.promise;
        };

		service.addToDxRegistry = function addToDxRegistry(demographicNo, disease)
		{
			var deferred = $q.defer();

			var issueToSend = {};
			issueToSend.id = disease.id;
			issueToSend.type = disease.type;
			issueToSend.code = disease.code;
			issueToSend.description = disease.description;

			$http.post(service.apiPath + encodeURIComponent(demographicNo) + '/add', issueToSend).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("diseaseRegistryService::addToDxRegistry error", errors);
					deferred.reject("An error occurred while adding to dx registry");
				});

			return deferred.promise;
		};

		service.findDxIssue = function findDxIssue(code, codingSystem)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeadersWithCache();
			config.params = {
				codingSystem: codingSystem,
				code: code
            };

            junoHttp.get(service.apiPath + 'findDxIssue', config).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("diseaseRegistryService::findDxIssue error", errors);
					deferred.reject("An error occurred while retrieving a dx issue");
				});

			return deferred.promise;
		};

		return service;
	}
]);