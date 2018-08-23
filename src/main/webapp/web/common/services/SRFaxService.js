'use strict';

/*

    Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
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

    This software was written for
    CloudPractice Inc.
    Victoria, British Columbia
    Canada

 */

angular.module("Common.Services").service("SRFaxService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/srfax';

		service.isEnabled = function isEnabled()
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/enabled', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("SRFaxService::isEnabled error", error);
					deferred.reject("An error occurred while getting SRFax account data");
				});
			return deferred.promise;
		};
		service.getAccountSettings = function getAccountSettings()
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/account', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("SRFaxService::getAccountSettings error", error);
					deferred.reject("An error occurred while getting SRFax account data");
				});
			return deferred.promise;
		};

		service.setAccountSettings = function setAccountSettings(transfer)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath + '/account', transfer).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("SRFaxService::setAccountSettings error", error);
					deferred.reject("An error occurred while setting SRFax account data");
				});
			return deferred.promise;
		};

		service.testConnection = function testConnection(transfer)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath + '/testConnection', transfer).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("SRFaxService::validateLogin error", error);
					deferred.reject("An error occurred while testing connection");
				});
			return deferred.promise;
		};

		return service;
	}
]);