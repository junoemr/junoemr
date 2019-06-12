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

angular.module("Common.Services").service("faxOutboundService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/faxOutbound';

		service.resendOutboundFax = function resendFax(id)
		{
			var deferred = $q.defer();

			junoHttp.put(service.apiPath + '/' + id + '/resend').then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::resendFax error", error);
					deferred.reject("An error occurred while resending a fax");
				});
			return deferred.promise;
		};


		service.getNextPushTime = function getNextPushTime()
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/getNextPushTime', config).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::getNextPushTime error", error);
					deferred.reject("An error occurred while getting scheduled push time");
				});
			return deferred.promise;
		};

		/* provide the endpoint url for downloading the faxed pdf document */
		service.getDownloadUrl = function(id)
		{
			return service.apiPath + '/' + id + '/download';
		};

		service.setNotificationStatus = function setNotificationStatus(id, status)
		{
			var deferred = $q.defer();

			junoHttp.put(service.apiPath + '/' + id + '/notificationStatus', status).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::setNotificationStatus error", error);
					deferred.reject("An error occurred while acknowledging a fax");
				});
			return deferred.promise;
		};

		service.archive = function archive(id)
		{
			var deferred = $q.defer();

			junoHttp.put(service.apiPath + '/' + id + '/archive').then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::archive error", error);
					deferred.reject("An error occurred while archiveing a fax");
				});
			return deferred.promise;
		};

		return service;
	}
]);