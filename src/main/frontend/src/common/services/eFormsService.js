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
angular.module("Common.Services").service("EFormsService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/eforms';

        service.getEFormList = function getEFormList()
        {
            var deferred = $q.defer();
	        junoHttp.get(service.apiPath + '/',
                Juno.Common.ServiceHelper.configHeaders()).then(
                function success(results)
                {
                    deferred.resolve(results);
                },
                function error(errors)
                {
                    console.log("formService::getEFormList error", errors);
                    deferred.reject("An error occurred while retrieving the eForm List");
                });

            return deferred.promise;
        };
        service.getEFormImageList = function getEFormImageList()
        {
            var deferred = $q.defer();
	        junoHttp.get(service.apiPath + '/images',
                Juno.Common.ServiceHelper.configHeaders()).then(
                function success(results)
                {
                    deferred.resolve(results);
                },
                function error(errors)
                {
                    console.log("formService::getEFormImageList error", errors);
                    deferred.reject("An error occurred while retrieving the eForm Image List");
                });

            return deferred.promise;
        };
		service.getEFormDatabaseTagList = function getEFormDatabaseTagList()
		{
			var deferred = $q.defer();
			junoHttp.get(service.apiPath + '/databaseTags',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results);
				},
				function error(errors)
				{
					console.log("formService::getEFormImageList error", errors);
					deferred.reject("An error occurred while retrieving the eForm Tag List");
				});

			return deferred.promise;
		};

		return service;
	}
]);