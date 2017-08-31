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
angular.module("Common.Services").service("consultService", [
	'$http',
	'$q',
	'junoHttp',
	function($http,
	         $q,
	         junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/consults/';

		service.searchRequests = function searchRequests(search)
		{
			var deferred = $q.defer();

			var config = Juno.Common.ServiceHelper.configHeadersWithCache();
			config.params = search;

			junoHttp.get(service.apiPath + 'searchRequests', config).then(
				function success(results)
				{
					deferred.resolve(results);
				},
				function error(errors)
				{
					console.log("consultService::searchRequests error", errors);
					deferred.reject("An error occured while searching consult requests");
				});

			return deferred.promise;
		};

		service.getRequest = function getRequest(requestId, demographicId)
		{
			var deferred = $q.defer();

			if (requestId === "new")
			{
				requestId = 0;
			}

			$http.get(service.apiPath + 'getRequest',
			{
				params:
				{
					requestId: requestId,
					demographicId: demographicId
				}
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::getRequest error", errors);
					deferred.reject(
						"An error occurred while getting consult request (requestId=" + requestId + ")");
				});

			return deferred.promise;
		};

		service.getRequestAttachments = function getRequestAttachments(
			requestId, demographicId, attached)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'getRequestAttachments?requestId=' +
				encodeURIComponent(requestId) + '&demographicId=' +
				encodeURIComponent(demographicId) + '&attached=' +
				encodeURIComponent(attached)
			).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::getRequestAttachments error", errors);
					deferred.reject(
						"An error occured while getting consult attachments (requestId=" + requestId + ")");
				});

			return deferred.promise;
		};

		service.saveRequest = function saveRequest(request)
		{
			var deferred = $q.defer();

			var requestTo1 = {
				consultationRequestTo1: request
			};
			$http.post(service.apiPath + 'saveRequest', requestTo1).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::saveRequest error", error);
					deferred.reject("An error occurred while fetching consult request after save");
				});

			return deferred.promise;
		};

		service.eSendRequest = function eSendRequest(requestId)
		{
			var deferred = $q.defer();

			$http.get(service.apiPath + 'eSendRequest?requestId=' + encodeURIComponent(requestId)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::eSendRequest error", errors);
					deferred.reject(
						"An error occurred while e-sending consult request (requestId=" + requestId + ")");
				});

			return deferred.promise;
		};

		service.searchResponses = function searchResponses(search)
		{
			var deferred = $q.defer();
			$http.post(service.apiPath + 'searchResponses', search).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::searchResponses error", errors);
					deferred.reject("An error occurred while searching consult responses");
				});

			return deferred.promise;
		};

		service.getResponse = function getResponse(responseId, demographicNo)
		{
			var deferred = $q.defer();

			if (responseId === "new")
			{
				responseId = 0;
			}

			$http.get(service.apiPath + 'getResponse',
			{
				params:
				{
					responseId: responseId,
					demographicNo: demographicNo
				}
			}).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::getResponse error", errors);
					deferred.reject(
						"An error occurred while getting consult response (responseId=" + responseId + ")");
				});

			return deferred.promise;
		};

		service.getResponseAttachments = function getResponseAttachments(
			responseId, demographicNo, attached)
		{
			var deferred = $q.defer();
			$http.get(
				service.apiPath + 'getResponseAttachments?responseId=' + encodeURIComponent(responseId) +
				'&demographicNo=' + encodeURIComponent(demographicNo) +
				'&attached=' + encodeURIComponent(attached)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::getResponseAttachments error", errors);
					deferred.reject(
						"An error occurred while getting consult response attachments (responseId=" +
						responseId + ")");
				});

			return deferred.promise;
		};

		service.saveResponse = function saveResponse(response)
		{
			var deferred = $q.defer();

			var responseTo1 = {
				consultationResponseTo1: response
			};
			$http.post(service.apiPath + 'saveResponse', responseTo1).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::saveResponse error", errors);
					deferred.reject("An error occurred while fetching consult response after save");
				});

			return deferred.promise;
		};

		service.getReferralPathwaysByService = function getReferralPathwaysByService(serviceName)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + 'getReferralPathwaysByService?serviceName=' +
				encodeURIComponent(serviceName)).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("consultService::getReferralPathwaysByService error", errors);
					deferred.reject("An error occured while fetching referral pathways");
				});

			return deferred.promise;
		};

		return service;
	}
]);