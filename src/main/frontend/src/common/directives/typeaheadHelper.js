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

			scope.onAddFn = '&junoOnAddFn';
			// title for the search button
			scope.searchButtonTitle = '@junoSearchButtonTitle';

			scope.addButtonTitle = '@junoAddButtonTitle';

			scope.iconLeft = '=junoIconLeft';
			scope.iconRight = '=junoIconRight';

			return scope;
		};

		helper.initTypeahead = function initTypeahead($scope)
		{
			//=========================================================================
			// typeahead directives must define/override these
			//=========================================================================

			$scope.findMatches = function findMatches($viewValue)
			{
				console.log('findMatches function must be defined by the typeahead implementation');
			};

			$scope.formatMatchSelection = function formatMatchSelection($model)
			{
				console.log('formatMatch function must be defined by the typeahead implementation');
			};

			//=========================================================================
			// typeahead directives may define/override these
			//=========================================================================

			$scope.optionsTemplateUrl = null;
			$scope.forceSelection = false;

			//=========================================================================
			// common initialization for $scope, for use on all typeahead directives
			//=========================================================================

			// the internal ng-model
			$scope.searchField = null;

			// ng-model-options
			$scope.typeaheadModelOptions = {
				debounce:
				{
					default: 250,
					blur: 250
				}
			};

			// the passed-in model that holds the selection
			if (!angular.isDefined($scope.model))
			{
				$scope.model = null;
			}

			$scope.autocompleteMinLength = 1;

			$scope.hasButtons = function hasButtons()
			{
				return $scope.hasSearchButton() || $scope.hasAddButton();
			};

			$scope.hasSearchButton = function hasSearchButton()
			{
				return angular.isFunction($scope.onSearchFn());
			};

			$scope.hasAddButton = function hasAddButton()
			{
				return angular.isFunction($scope.onAddFn());
			};

			$scope.hasIcon = function hasIcon()
			{
				return $scope.hasLeftIcon() || $scope.hasRightIcon();
			};

			$scope.hasLeftIcon = function hasLeftIcon()
			{
				return $scope.iconLeft === true;
			};

			$scope.hasRightIcon = function hasRightIcon()
			{
				return $scope.iconRight === true;
			};

			$scope.hasTemplateUrl = function hasTemplateUrl()
			{
				return Juno.Common.Util.exists($scope.optionsTemplateUrl);
			};

			$scope.hasForceSelectionEnabled = function hasForceSelectionEnabled()
			{
				return Juno.Common.Util.exists($scope.forceSelection) && $scope.forceSelection;
			};

			$scope.formatMatch = function formatMatch($model)
			{
				if (!Juno.Common.Util.exists($model))
				{
					return null;
				}

				if ($scope.isDummySelection($model))
				{
					return $model.searchQuery;
				}

				return $scope.formatMatchSelection($model);
			};

			$scope.onSelect = function onSelect($item, $model, $label, $event)
			{
				$scope.model = $item;
			};

			$scope.onBlur = function onBlur()
			{
				if ($scope.hasForceSelectionEnabled() && angular.isString($scope.searchField))
				{
					console.log('typeaheadHelper::onBlur - setting model to null (forceSelectionEnabled)');
					// the searchField is not a valid selection and force selection is enabled
					// if there is a select, onSelect will fire after
					$scope.model = null;
				}
			};

			$scope.onChange = function onChange()
			{
				if (!$scope.hasForceSelectionEnabled())
				{
					// as user types into the typeahead, select a 'dummy' selection;
					// consumers need to check the isTypeaheadSearchQuery property
					$scope.onSelect($scope.createDummySelection());
				}
			};

			$scope.onSearch = function onSearch()
			{
				// must be in a $timeout because model may not be populated yet
				// because of delay specified by ng-model-options
				$timeout(function()
				{
					if (angular.isFunction($scope.onSearchFn()))
					{
						$scope.onSearchFn()($scope.model);
					}
				});
			};

			$scope.onAdd = function onAdd()
			{
				// must be in a $timeout because model may not be populated yet
				// because of delay specified by ng-model-options
				$timeout(function()
				{
					if (angular.isFunction($scope.onAddFn()))
					{
						$scope.onAddFn()($scope.model);
					}
				});
			};

			$scope.createDummySelection = function createDummySelection()
			{
				return {
					isTypeaheadSearchQuery: true,
					searchQuery: $scope.searchField
				};
			};

			$scope.isDummySelection = function isDummySelection(match)
			{
				return Juno.Common.Util.exists(match) && match.isTypeaheadSearchQuery;
			};

			$scope.$watch('model', function()
			{
				// when the 'external' model is updated, update the 'internal' model
				$scope.searchField = $scope.model;
			});

		};

		return helper;
	}

]);