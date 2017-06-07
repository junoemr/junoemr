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
angular.module('Patient.Search').controller('Patient.Search.PatientSearchController', [

	'$q',
	'$state',
	'$stateParams',
	'$uibModal',
	'NgTableParams',
	'securityService',
	'demographicService',

	function(
		$q,
		$state,
		$stateParams,
		$uibModal,
		NgTableParams,
		securityService,
		demographicService)
	{
		var controller = {};

		//=========================================================================
		// Initialization
		//=========================================================================

		controller.demographicReadAccess = null;
		controller.search = null;

		controller.init = function init()
		{
			controller.clearParams();
			if (Juno.Common.Util.exists($stateParams.term))
			{
				controller.search.term = $stateParams.term;
			}

			securityService.hasRights(
			{
				items: [
				{
					objectName: '_demographic',
					privilege: 'r'
				}]
			}).then(
				function success(results)
				{
					if (Juno.Common.Util.exists(results.content) && results.content.length === 1)
					{
						controller.demographicReadAccess = results.content[0];
						if (controller.demographicReadAccess)
						{
							controller.initTable();
						}
					}
					else
					{
						console.log('failed to load rights', results);
						controller.demographicReadAccess = false;
					}
				},
				function error(errors)
				{
					console.log(errors);
					controller.demographicReadAccess = false;
				});
		};

		//=========================================================================
		// Methods
		//=========================================================================

		controller.initTable = function initTable()
		{
			controller.tableParams = new NgTableParams(
			{
				page: 1,
				count: 10,
				sorting:
				{
					Name: 'asc'
				}
			},
			{
				getData: function(params)
				{
					var deferred = $q.defer();

					var count = params.url().count;
					var page = params.url().page;

					controller.search.params = params.url();

					var promiseArray = [];
					promiseArray.push(demographicService.search(
						controller.search, ((page - 1) * count), count));

					controller.integratorResults = null;
					if (controller.search.integrator)
					{
						promiseArray.push(demographicService.searchIntegrator(
							controller.search, 100));
					}

					$q.all(promiseArray).then(
						function success(results)
						{
							var demographicSearchResults = results[0];
							params.total(demographicSearchResults.total);

							if (controller.search.integrator)
							{
								controller.integratorResults = results[1];
							}

							deferred.resolve(demographicSearchResults.content);
						},
						function error(promiseErrors)
						{
							console.log('patient search failed', promiseErrors);
							deferred.reject();
						});

					return deferred.promise;
				}
			});
		};

		controller.searchPatients = function searchPatients()
		{
			if (controller.search.type === "DOB")
			{
				var dobMoment = moment(controller.search.term, ["YYYY-MM-DD", "YYYY/MM/DD"], true);
				if (dobMoment.isValid())
				{
					controller.search.term = dobMoment.format("YYYY-MM-DD");
				}
				else
				{
					alert("Please enter Date of Birth in format YYYY-MM-DD.");
					return;
				}
			}
			controller.tableParams.reload();
		};

		controller.clearParams = function(searchType)
		{
			// default search type
			if (!Juno.Common.Util.exists(searchType))
			{
				searchType = 'Name';
			}

			// reset the parameters
			controller.search = {
				type: searchType,
				term: '',
				active: true,
				integrator: false,
				outofdomain: true
			};

			// update the placeholder
			controller.searchTermPlaceHolder = (controller.search.type === "DOB") ?
				"YYYY-MM-DD" : "Search Term";

			// do the search (if initialized)
			if (Juno.Common.Util.exists(controller.tableParams))
			{
				controller.tableParams.reload();
			}
		};

		controller.toggleParam = function toggleParam(param)
		{
			if (['active', 'integrator', 'outofdomain'].indexOf(param) > -1)
			{
				controller.search[param] = !controller.search[param];
			}
		};

		controller.loadRecord = function loadRecord(demographicNo)
		{
			$state.go('record.details',
			{
				demographicNo: demographicNo,
				hideNote: true
			});
		};

		controller.showIntegratorResults = function showIntegratorResults()
		{
			var results = [];
			var total = 0;

			if (Juno.Common.Util.exists(controller.integratorResults))
			{
				results = controller.integratorResults.content;
				total = controller.integratorResults.total;
			}

			$uibModal.open(
			{
				templateUrl: 'patientsearch/remotePatientResults.jsp',
				controller: 'RemotePatientResultsController',
				resolve:
				{
					results: function()
					{
						return results;
					},
					total: function()
					{
						return total;
					}
				}
			});
		};

		return controller;
	}
]);