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

import {ADMIN_PAGE_EVENTS} from "../../adminConstants";

angular.module('Admin.Integration').component('integrationModules',
{
	templateUrl: 'src/admin/integration/integrationModules/integrationModules.jsp',
	bindings: {

	},
	controller: ['$scope', 'systemPreferenceService', function ($scope, systemPreferenceService)
	{
		let ctrl = this;

		ctrl.integrationList = [
			{
				name: "CareConnect",
				enabled: false,
				propertyName: "integration.CareConnect.enabled",
				configUrl:""
			},
			{
				name: "IceFall",
				enabled: false,
				propertyName: "icefall_enabled",
				configUrl: ""
			},
		];

		ctrl.$onInit = function()
		{
			for (let integration of ctrl.integrationList)
			{
				systemPreferenceService.getPreference(integration.propertyName, "false").then(
						function success(response)
						{
							integration.enabled = response.toUpperCase() === "TRUE";
						},
						function error(response)
						{
							integration.enabled = false;
							console.error("Failed to fetch preference, " + integration.propertyName);
						}
				);
			}
		};

		ctrl.enableProperty = function(propertyName, enable)
		{
			systemPreferenceService.setPreference(propertyName, enable.toString());
			$scope.$emit(ADMIN_PAGE_EVENTS.ADMIN_RELOAD_NAV);
		}

	}]
});