/*
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

import {ADMIN_PAGE_EVENTS} from "./adminConstants";

angular.module('Admin').controller('Admin.AdminController', [
	'$scope',
	'$http',
	'$location',
	'personaService',
	'$stateParams',
	function ($scope, $http, $location, personaService, $stateParams)
	{
		let controller = this;
		controller.navList = [];

		function generateTransition(newState, rawTransition)
		{
			return function()
			{
				if (rawTransition)
				{
					window.open(newState, "_blank");
				}
				else
				{
					angular.element(document.querySelector("html")).animate({scrollTop: 0}, 500);
					$location.url("/admin/" + newState);
				}
			}
		};

		// translate transitionState property of results in to transition function.
		function processNavResults(results)
		{
			results.forEach(function (group) {
				group.items.forEach(function (item) {
					item.callback = generateTransition(item.transitionState, item.rawTransition);

					// restore accordion state on reload
					if (
							($stateParams.frameUrl !== undefined && (item.transitionState.includes($stateParams.frameUrl) || item.transitionState.includes(encodeURIComponent($stateParams.frameUrl))))
							||
							($location.url().includes(item.transitionState))
					)
					{
						group.expanded = true;
					}

				})
			});

			return results;
		}

		function loadNavItems()
		{
			personaService.getAdminNav().then(
				function success(result)
				{
					controller.navList = processNavResults(result);
				},
				function error(result)
				{
					console.error("failed to load admin nav bar, with error: " + result);
				}
			)
		}

		// expand a group on the nav bar
		controller.expandNavGroup = function expandNavGroup(group)
		{
			let navGroup = controller.navList.find(function (el)
			{
				return el.name === group;
			});

			if (navGroup)
			{
				navGroup.expanded = true;
			}
		};

		// collapse all nav groups, without animation.
		controller.collapseAllNavGroups = function collapseAllNavGroups()
		{
			controller.navList.forEach(function (group)
			{
				group.expanded = false;
			})
		};

		$scope.$on(ADMIN_PAGE_EVENTS.ADMIN_RELOAD_NAV, function (event)
		{
			loadNavItems();
		});
		
		$scope.$on(ADMIN_PAGE_EVENTS.ADMIN_EXPAND_NAV_GROUP, function (event, group)
		{
			controller.collapseAllNavGroups();
			controller.expandNavGroup(group);
		});

		loadNavItems();
	}
]);
