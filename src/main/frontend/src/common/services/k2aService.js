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

import {SystemPreferenceApi} from "../../../generated";
import {SystemPreferences} from "./systemPreferenceServiceConstants";

angular.module("Common.Services").service("k2aService", [
	'$q',
	'junoHttp',
	'$http',
	'$httpParamSerializer',
	
	function($q, junoHttp, $http, $httpParamSerializer)
	{
		var service = {};
		
		service.apiPath = '../ws/rs';
		service.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, service.apiPath);
		
		/**
		 * Determine if the know2Act integration is enabled.  This is the not the same as "isActive or isInit"
		 * @returns Promise
		 */
		service.isK2AEnabled = async () =>
		{
			const response = await service.systemPreferenceApi.getPreferenceEnabled(SystemPreferences.K2AEnabled);
			return response.data.body;
		}
		
		service.getK2aFeed = function getK2aFeed(startPoint, numberOfRows)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				key:'k2a',
				startPoint:encodeURIComponent(startPoint),
				numberOfRows:encodeURIComponent(numberOfRows)
			};

			junoHttp.get(service.apiPath + '/rssproxy/rss', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("k2aService::getK2aFeed error", error);
					deferred.reject("An error occured while getting k2a content");
				});
			return deferred.promise;
		};
		
		/**
		 * Determine if K2A is Active.  For it to be active, it must satisfy both conditions:
		 * 1) enabled via super admin integration
		 * 2) set up by the user
		 *
		 * @returns Promise{boolean}
		 */
		service.isK2AInit = function isK2AInit()
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			
			junoHttp.get(service.apiPath + '/app/K2AActive', config).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.error("k2aService::isK2AInit error", error);
					deferred.reject("An error occured while getting k2a content");
				});
			
			return deferred.promise;
		};

		service.initK2A = function initK2A(clinicName)
		{
			var deferred = $q.defer();
			var transferObj = {
				name: clinicName
			};

			junoHttp.post(service.apiPath + '/app/K2AInit', transferObj).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.error("k2aService::initK2A error", error);
					deferred.reject("An error occured while trying to initialize k2a");
				});

			return deferred.promise;
		};

		service.postK2AComment = function postK2AComment(post)
		{
			var deferred = $q.defer();

			var commentItem = {
				post:post
			};
			junoHttp.post(service.apiPath + '/app/comment', commentItem).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::postK2AComment error", error);
					deferred.reject("An error occured while trying to post a comment to k2a");
				});

			return deferred.promise;
		};

		service.removeK2AComment = function removeK2AComment(commentId)
		{
			var deferred = $q.defer();

			junoHttp.del(service.apiPath + '/app/comment/' + encodeURIComponent(commentId)).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::removeK2AComment error", error);
					deferred.reject("An error occured while trying to remove a comment from k2a");
				});

			return deferred.promise;
		};

		service.preventionRulesList = function preventionRulesList()
		{
			var deferred = $q.defer();

			junoHttp.get(service.apiPath + '/resources/preventionRulesList',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::preventionRulesList error", error);
					deferred.reject("An error occured while trying to remove a comment from k2a");
				});

			return deferred.promise;
		};

		service.loadPreventionRuleById = function loadPreventionRuleById(id)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath + '/resources/loadPreventionRulesById/' +
				encodeURIComponent(id.id), id).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::loadPreventionRuleById error", error);
					deferred.reject("An error occured while trying to loadPreventionRulesById");
				});

			return deferred.promise;
		};

		service.getCurrentPreventionRulesVersion = function getCurrentPreventionRulesVersion()
		{
			var deferred = $q.defer();
			junoHttp.get(service.apiPath + '/resources/currentPreventionRulesVersion',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::getCurrentPreventionRulesVersion error", error);
					deferred.reject("An error occured while trying to getCurrentPreventionRulesVersion");
				});

			return deferred.promise;
		};

		service.getNotifications = function getNotifications()
		{
			var deferred = $q.defer();
			junoHttp.get(service.apiPath + '/resources/notifications',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::getNotifications error", error);
					deferred.reject("An error occured while trying to getNotifications");
				});

			return deferred.promise;
		};

		service.getMoreNotification = function getMoreNotification(id)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath + '/resources/notifications/readmore',
				id, Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::getMoreNotification error", error);
					deferred.reject("An error occured while trying to /resources/notifications/readmore");
				});

			return deferred.promise;
		};

		service.ackNotification = function ackNotification(id)
		{
			var deferred = $q.defer();
			junoHttp.post(service.apiPath + '/resources/notifications/' + id + '/ack',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("k2aService::ackNotification error", error);
					deferred.reject("An error occured while trying to /resources/notifications/ack");
				});

			return deferred.promise;
		};

		return service;
	}
]);