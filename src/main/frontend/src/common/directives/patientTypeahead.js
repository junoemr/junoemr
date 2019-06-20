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

angular.module('Common.Directives').directive('junoPatientTypeahead', [
	'$q',
	'typeaheadHelper',
	'demographicsService',

	function($q, typeaheadHelper, demographicsService)
	{
		var scope = typeaheadHelper.defaultTypeaheadScope();

		var linkFunction = function link_function($scope, element, attribute, controller)
		{
			typeaheadHelper.initTypeahead($scope);

			$scope.forceSelection = true;

			$scope.findMatches = function findMatches(search)
			{
				var deferred = $q.defer();

				var params = {
					type: demographicsService.SEARCH_MODE.Name,
					term: search,
					status: demographicsService.STATUS_MODE.ACTIVE,
					integrator: false,
					outofdomain: true
				};
				demographicsService.search(params, 0, 25).then(
					function success(response)
					{
						deferred.resolve(response.data);
					},
					function error()
					{
						deferred.reject();
					});

				return deferred.promise;
			};

			$scope.formatMatchSelection = function formatMatchSelection(demographic)
			{
				return demographic.lastName + ', ' + demographic.firstName;
			};

			$scope.typeaheadLabel = function(match)
			{
				return $scope.formatMatchSelection(match);
			};
		};

		return {
			restrict: 'E',
			scope: scope,
			templateUrl: 'src/common/directives/typeahead.jsp',
			replace: true,
			link: linkFunction
		};

	}
]);
