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
angular.module("Common.Services").service("formService", [
	'$http', '$q', 'junoHttp', 'providerService',
	function($http, $q, junoHttp, providerService)
	{
		var service = {};

		service.apiPath = '../ws/rs/forms';
		service.popupOptions = 'left=100,top=100,resizable=yes,scrollbars=yes,toolbar=no,menubar=no,location=no,directories=no';
		// load users preferred form popup size
		providerService.getSettings().then(
				function success(results)
				{
					service.popupOptions = "height=" + results.eformPopupHeight + ",width=" + results.eformPopupWidth + "," + service.popupOptions
				},
				function error(results)
				{
					console.error("Failed to load provider settings with error: " + results);
				}
		);

		service.getAllFormsByHeading = function getAllFormsByHeading(demographicNo, heading)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) +
				'/all?heading=' + encodeURIComponent(heading),
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getAllFormsByHeading error", errors);
					deferred.reject("An error occurred while fetching forms");
				});

			return deferred.promise;
		};

		service.genericFormGet = function genericFormGet(url)
		{
			var deferred = $q.defer();
			$http.get(url,
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getAllFormsByHeading error", errors);
					deferred.reject("An error occurred while fetching forms");
				});

			return deferred.promise;
		};

		service.getAllEncounterForms = function getAllEncounterForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/allEncounterForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getAllEncounterForms error", errors);
					deferred.reject("An error occurred while fetching encounter forms");
				});

			return deferred.promise;
		};

		service.getSelectedEncounterForms = function getSelectedEncounterForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/selectedEncounterForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getSelectedEncounterForms error", errors);
					deferred.reject("An error occurred while fetching selected encounter forms");
				});

			return deferred.promise;
		};

		// get all completed form instances for the given demographic
		service.getCompletedEncounterForms = function getCompletedEncounterForms(demographicNo)
		{
			return service.genericFormGet(service.apiPath + "/" + encodeURIComponent(demographicNo) + "/all/completed")
		};

		// get all forms that can be added to the given demographic
		service.getAddForms = function getAddForms(demographicNo)
		{
			return service.genericFormGet(service.apiPath + "/allForms")
		};

		// get all form revisions for the given demographic
		service.getRevisionForms = function (demographicNo)
		{
			return service.genericFormGet(service.apiPath + "/" + encodeURIComponent(demographicNo) + "/all/revisions")
		};

		service.getDeletedForms = function (demographicNo)
		{
			return service.genericFormGet(service.apiPath + "/" + encodeURIComponent(demographicNo) + "/all/deleted")
		};

		service.getAllEForms = function getAllEForms()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/allEForms',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("formService::getAllEForms error", errors);
					deferred.reject("An error occurred while fetching eforms");
				});

			return deferred.promise;
		};

		// delete a form
		service.deleteForm = function deleteForm(id, type)
		{
			let deferred = $q.defer();
			$http.put(service.apiPath + "/delete/" + encodeURIComponent(id) + "?type=" + encodeURIComponent(type),
				null, Juno.Common.ServiceHelper.configHeaders()).then (
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(result)
				{
					console.error("Error deleting form: " + id);
					deferred.reject("Error deleting form: " + id);
				}
			);

			return deferred.promise;
		};

		// restore a form (un delete)
		service.restoreForm = function restoreForm(id, type)
		{
			let deferred = $q.defer();
			$http.put(service.apiPath + "/restore/" + encodeURIComponent(id) + "?type=" + encodeURIComponent(type),
				null, Juno.Common.ServiceHelper.configHeaders()).then (
				function success(result)
				{
					deferred.resolve(result.data);
				},
				function error(result)
				{
					console.error("Error restoring form: " + id);
					deferred.reject("Error restoring form: " + id);
				}
			);

			return deferred.promise;
		};

		service.getGroupNames = function getGroupNames()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/groupNames',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data.content);
				},
				function error(errors)
				{
					console.log("formService::getGroupNames error", errors);
					deferred.reject("An error occurred while fetching group names");
				});

			return deferred.promise;
		};

		service.getFormGroups = function getFormGroups()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/getFormGroups',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFormGroups error", errors);
					deferred.reject("An error occurred while fetching form groups");
				});

			return deferred.promise;
		};

		service.getFavouriteFormGroup = function getFavouriteFormGroup()
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/getFavouriteFormGroup',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFavouriteFormGroup error", errors);
					deferred.reject("An error occurred while fetching favourite form groups");
				});

			return deferred.promise;
		};

		service.getFormOptions = function getFormOptions(demographicNo)
		{
			var deferred = $q.defer();
			$http.get(service.apiPath + '/' + encodeURIComponent(demographicNo) + '/formOptions',
				Juno.Common.ServiceHelper.configHeaders()).then(
				function success(results)
				{
					deferred.resolve(results.data);
				},
				function error(errors)
				{
					console.log("formService::getFormOptions error", errors);
					deferred.reject("An error occurred while fetching form options");
				});

			return deferred.promise;
		};

		// open a popup window for the specified eform. returns a promise that is resolved when the popup is closed
		service.openEFormPopup = function (demographicNo, id, appointmentNo)
		{
			if (appointmentNo === undefined)
			{
				appointmentNo = null;
			}

			let url = '../eform/efmformadd_data.jsp?fid=' + encodeURIComponent(id) + '&demographic_no=' + encodeURIComponent(demographicNo) + '&appointment=' + encodeURIComponent(appointmentNo);
			return Juno.Common.Util.windowClosedPromise(window.open(url,'_blank', service.popupOptions));
		};

		// open a popup window for the specified eform instance. returns a promise that is resolved when the popup is closed
		service.openEFormInstancePopup = function (demographicNo, fdid, appointmentNo)
		{
			if (appointmentNo === undefined)
			{
				appointmentNo = null;
			}

			let url = '../eform/efmshowform_data.jsp?fdid=' + encodeURIComponent(fdid) + '&demographic_no=' + encodeURIComponent(demographicNo) + '&appointment=' + encodeURIComponent(appointmentNo);
			return Juno.Common.Util.windowClosedPromise(window.open(url,'_blank', service.popupOptions));
		};

		// open a popup window for the specified form. returns a promise that is resolved when the popup is closed
		service.openFormPopup = function (providerNo, demographicNo, appointmentNo, url)
		{
			if (appointmentNo === undefined)
			{
				appointmentNo = "";
			}

			url = url + encodeURIComponent(demographicNo) + "&formId=0&provNo=" + encodeURIComponent(providerNo) + "&parentAjaxId=forms&appointmentNo=" + encodeURIComponent(appointmentNo);
			return Juno.Common.Util.windowClosedPromise(window.open(url, '_blank', service.popupOptions));
		};

		// open a popup window for the specified form instance. returns a promise that is resolved when the popup is closed
		service.openFormInstancePopup = function(formName, demographicNo, appointmentNo, id)
		{
			if (appointmentNo === undefined || appointmentNo === null)
			{
				appointmentNo = "";
			}

			let url = "../form/forwardshortcutname.jsp?formname=" + encodeURIComponent(formName) + "&demographic_no=" + encodeURIComponent(demographicNo) +
				"&appointmentNo=" + encodeURIComponent(appointmentNo) + "&formId=" + encodeURIComponent(id);
			return Juno.Common.Util.windowClosedPromise(window.open(url, '_blank', service.popupOptions));
		};


		return service;
	}
]);