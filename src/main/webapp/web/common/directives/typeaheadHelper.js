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
angular.module("Common.Directives").service("typeaheadHelper", [
	'$timeout',
	function($timeout)
	{
		var helper = {};

		helper.defaultTypeaheadScope = function()
		{
			var scope = {};

			// holds the selected match
			scope.model = '=junoModel';

			// placeholder for the text input
			scope.placeholder = '@junoPlaceholder';

			// called when the search button is clicked (search button will only appear if defined)
			scope.onSearchFn = '&junoOnSearchFn';
			// title for the search button
			scope.searchTitle = '@junoSearchTitle';

			return scope;
		};

		helper.initTypeahead = function initTypeahead($scope)
		{
			// common initialization for $scope, for use on all typeahead directives

			// ng-model-options
			$scope.typeaheadModelOptions = {
				debounce: {
					default: 250,
					blur: 250
				}
			};

			// the passed-in model that holds the selection
			if(!angular.isDefined($scope.model))
			{
				$scope.model = null;
			}

			$scope.autocompleteMinLength = 1;

			$scope.hasButtons = function hasButtons()
			{
				return angular.isFunction($scope.onSearchFn());
			};

			$scope.hasTemplateUrl = function hasTemplateUrl()
			{
				return Juno.Common.Util.exists($scope.optionsTemplateUrl);
			};

			$scope.findMatches = function findMatches($viewValue)
			{
				console.log('findMatches function should be defined by the typeahead implementation');
			};

			$scope.formatMatch = function formatMatch($model)
			{
				if(!Juno.Common.Util.exists($model))
				{
					return null;
				}

				if($scope.isDummySelection($model))
				{
					return $model.searchQuery;
				}

				return $scope.formatMatchSelection($model);
			};

			$scope.formatMatchSelection = function formatMatchSelection($model)
			{
				console.log('formatMatch function should be defined by the typeahead implementation');
			};

			$scope.onSelect = function onSelect($item, $model, $label, $event)
			{
				console.log('typeaheadHelper::onSelect', $item);
				$scope.model = $item;
			};

			$scope.onChange = function onChange()
			{
				// as user types into the typeahead, save a 'dummy' selection object to the model
				// some consumers will want to process non-selected values from the typeahead;
				// they need to check the isTypeaheadSearchQuery property
				$scope.onSelect($scope.createDummySelection());
			};

			$scope.onSearch = function onSearch()
			{
				// must be in a $timeout because model may not be populated yet
				// because of delay specified by ng-model-options
				$timeout(function() {
					if(angular.isFunction($scope.onSearchFn()))
					{
						$scope.onSearchFn()($scope.model);
					}
				});
			};

			$scope.createDummySelection = function createDummySelection()
			{
				return {
					isTypeaheadSearchQuery: true,
					searchQuery: $scope.model
				};
			};

			$scope.isDummySelection = function isDummySelection(match)
			{
				return Juno.Common.Util.exists(match) && match.isTypeaheadSearchQuery;
			};

		};

		return helper;
	}

]);
