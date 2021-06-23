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
angular.module("Common.Services").service("eFormService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/eform';

		//TODO-legacy this file is not used anywhere and requires testing
		service.saveEForm = function saveEForm()
		{
			var deferred = $q.defer();
			junoHttp.put(service.apiPath + '/',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results);
				},
				function error(errors)
				{
					console.log("formService::saveEForm error", errors);
					deferred.reject("An error occurred while saving an EForm");
				});

			return deferred.promise;
		};
		service.updateEForm = function updateEForm(formId)
        {
            var deferred = $q.defer();
	        junoHttp.post(service.apiPath + '/' + formId,
                Juno.Common.ServiceHelper.configHeaders()).then(
                function success(results)
                {
                    deferred.resolve(results);
                },
                function error(errors)
                {
                    console.log("formService::updateEForm error", errors);
                    deferred.reject("An error occurred while updating an EForm");
                });

            return deferred.promise;
        };
		service.loadEForm = function loadEForm(formId)
        {
            var deferred = $q.defer();
	        junoHttp.get(service.apiPath + '/' + formId,
                Juno.Common.ServiceHelper.configHeaders()).then(
                function success(results)
                {
                    deferred.resolve(results);
                },
                function error(errors)
                {
                    console.log("formService::loadEForm error", errors);
                    deferred.reject("An error occurred while loading an EForm");
                });

            return deferred.promise;
        };

		return service;
	}
]);