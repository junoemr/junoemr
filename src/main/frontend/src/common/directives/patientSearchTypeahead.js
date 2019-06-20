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

angular.module('Common.Directives').directive('junoPatientSearchTypeahead', [
	'$q',
	'typeaheadHelper',
	'demographicsService',

	function($q, typeaheadHelper, demographicsService)
	{
		var scope = typeaheadHelper.defaultTypeaheadScope();

		var linkFunction = function link_function($scope, element, attribute, controller)
		{
			typeaheadHelper.initTypeahead($scope);

			$scope.optionsTemplateUrl = 'src/common/directives/patientSearchTypeaheadOption.jsp';

			$scope.findMatches = function findMatches(search)
			{
				var deferred = $q.defer();
				demographicsService.quickSearch(search).then(
					function success(results)
					{
						var matches = results.data;
						var meta = results.meta;

						if(meta.total > 10)
						{
							matches.push({
								moreResults: true,
								total: meta.total,
								searchQuery: search
							});
						}
						deferred.resolve(matches);
					},
					function error()
					{
						deferred.reject();
					});
				return deferred.promise;
			};

			$scope.formatMatchSelection = function formatMatchSelection(demographic)
			{
				if(demographic.moreResults)
				{
					return null;
				}

				return Juno.Common.Util.formatName(demographic.firstName, demographic.lastName);
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
