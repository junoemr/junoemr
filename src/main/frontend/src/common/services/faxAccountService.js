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

angular.module("Common.Services").service("faxAccountService", [
	'$q',
	'junoHttp',
	function($q, junoHttp)
	{
		var service = {};

		service.apiPath = '../ws/rs/faxAccount';

		service.listAccounts = function listAccounts(page, perPage)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = {
				page: page,
				perPage: perPage
			};

			junoHttp.get(service.apiPath + '/search', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("faxService::listAccounts error", error);
					deferred.reject("An error occurred while getting fax account data");
				});
			return deferred.promise;
		};

		service.isEnabled = function isEnabled(id)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/' + id + '/enabled', config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("faxService::isEnabled error", error);
					deferred.reject("An error occurred while getting fax account data");
				});
			return deferred.promise;
		};
		service.getAccountSettings = function getAccountSettings(id)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();

			junoHttp.get(service.apiPath + '/' + id, config).then(
				function success(response) {
					deferred.resolve(response.data);
				},
				function error(error) {
					console.log("faxService::getAccountSettings error", error);
					deferred.reject("An error occurred while getting fax account data");
				});
			return deferred.promise;
		};

		service.addAccountSettings = function addAccountSettings(transfer)
		{
			var deferred = $q.defer();

			junoHttp.post(service.apiPath, transfer).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::setAccountSettings error", error);
					deferred.reject("An error occurred while setting fax account data");
				});
			return deferred.promise;
		};

		service.updateAccountSettings = function updateAccountSettings(id, transfer)
		{
			var deferred = $q.defer();

			junoHttp.put(service.apiPath + '/' + id, transfer).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::setAccountSettings error", error);
					deferred.reject("An error occurred while updating fax account data");
				});
			return deferred.promise;
		};

		service.testConnection = function testConnection(transfer)
		{
			var deferred = $q.defer();

			let id = transfer.id;
			let url = service.apiPath + '/testConnection';

			if(id && id != null && id !== '')
			{
				url = service.apiPath + '/' + transfer.id + '/testConnection'
			}

			junoHttp.post(url, transfer).then(
				function success(response)
				{
					deferred.resolve(response.data);
				},
				function error(error)
				{
					console.log("faxService::validateLogin error", error);
					deferred.reject("An error occurred while testing connection");
				});

			return deferred.promise;
		};

		service.getInbox = function getInbox(accountId, searchListHelper)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = searchListHelper.getParams();

			junoHttp.get(service.apiPath + '/' + accountId + '/inbox', config).then(
				function success(response)
				{
					deferred.resolve(response);
				},
				function error(error)
				{
					console.log("faxService::getInbox error", error);
					deferred.reject("An error occurred while retrieving inbox data");
				});

			return deferred.promise;
		};

		service.getOutbox = function getOutbox(accountId, searchListHelper)
		{
			var deferred = $q.defer();
			var config = Juno.Common.ServiceHelper.configHeaders();
			config.params = searchListHelper.getParams();

			junoHttp.get(service.apiPath + '/' + accountId + '/outbox', config).then(
				function success(response)
				{
					deferred.resolve(response);
				},
				function error(error)
				{
					console.log("faxService::getOutbox error", error);
					deferred.reject("An error occurred while retrieving outbox data");
				});

			return deferred.promise;
		};

		return service;
	}
]);