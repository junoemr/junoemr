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

import {SystemPreferenceApi} from "../../../generated";
import {API_BASE_PATH} from "../../lib/constants/ApiConstants";

angular.module("Common.Services").service("systemPreferenceService", [
	'$q',
	'$http',
	'$httpParamSerializer',
	'junoHttp',
	function($q, $http, $httpParamSerializer, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/systemPreference';
		service.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, API_BASE_PATH);

		service.getPreference = function getPreference(key, defaultValue)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				default: defaultValue
			};

			junoHttp.get(service.apiPath + '/' + key, config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("systemPreferenceService::getPreference error", error);
					deferred.reject("An error occurred while getting system preference data");
				});
			return deferred.promise;
		};

		service.setPreference = function setPreference(key, value)
		{
			var deferred = $q.defer();

			junoHttp.put(service.apiPath + '/' + key, value).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("systemPreferenceService::setPreference error", error);
					deferred.reject("An error occurred while setting system preference data");
				});
			return deferred.promise;
		};

		service.isPreferenceEnabled = function isPreferenceEnabled(key, defaultValue)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				default: defaultValue
			};

			junoHttp.get(service.apiPath + '/' + key + '/enabled', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("systemPreferenceService::isPreferenceEnabled error", error);
					deferred.reject("An error occurred while getting system preference enabled status");
				});
			return deferred.promise;
		};

		service.getPropertyEnabled = async (key) =>
		{
			return (await service.systemPreferenceApi.getPropertyEnabled(key)).data.body;
		}

		return service;
	}
]);