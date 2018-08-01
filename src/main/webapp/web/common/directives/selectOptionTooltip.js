'use strict';

/**
 * Copyright (c) 2012-2018. CloudPractice Inc. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for
 * CloudPractice Inc.
 * Victoria, British Columbia
 * Canada
 */

angular.module('Common.Directives').directive('selectOptionTooltip', [
	'$parse',
	function($parse)
	{
		console.log("CUSTOM SELECT TOOLTIP LOADED");

		var linkFunction = function link_function($scope, element, attribute, controller)
		{
			$scope.addCustomAttr = function (attr, element, data, fnDisableIfTrue)
			{
				$("option", element).each(function (i, e)
				{
					var locals = {};
					locals[attr] = data[i];
					$(e).attr('title', fnDisableIfTrue($scope, locals));
				});
			};

			var expElements = attribute['selectOptionTooltip'].match(/(.+)\s+for\s+(.+)\s+in\s+(.+)/);
			var attrToWatch = expElements[3];
			var fnDisableIfTrue = $parse(expElements[1]);

			$scope.$watchCollection(attrToWatch, function (newValue)
			{
				if (newValue)
					$scope.addCustomAttr(expElements[2], element, newValue, fnDisableIfTrue);
			});
		};

		return {
			priority: 1000,
			require: 'ngModel',
			replace: true,
			link: linkFunction
		};

	}
]);