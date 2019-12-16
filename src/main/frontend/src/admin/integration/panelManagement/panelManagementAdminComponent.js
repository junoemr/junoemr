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

angular.module('Admin.Integration').component('panelManagementAdmin',
{
	templateUrl: 'src/admin/integration/panelManagement/panelManagement.jsp',
	bindings: {},
	controller: ['$scope', 'providersService', 'uxService', function ($scope, providersService, uxService)
	{
		let ctrl = this;

		ctrl.selectedProvider = null;
		ctrl.selectedPanel = null;
		ctrl.dashboardUrl = null;
		ctrl.missingRequiredFeildProvider = false;
		ctrl.missingRequiredFeildPanel = false;
		let DASHBOARD_URL_PATTERN = "../web/dashboard/display/DashboardDisplay.do?method=getDashboard&dashboardId=";

		ctrl.providers = [];
		ctrl.panels = [];

		ctrl.$onInit = function ()
		{
			providersService.getAll().then(
					function success(result)
					{
						ctrl.providers = [];
						for (let provider of result)
						{
							ctrl.providers.push({
								label: provider.name,
								value: provider.providerNo
							})
						}
					},
					function error(result)
					{
						console.error("Failed to get provider list with error: " + result);
					}
			);

			uxService.getAllDashboards().then(
					function success(result)
					{
						ctrl.panels = [];
						for (let panel of result)
						{
							if (panel.active)
							{
								ctrl.panels.push({
									label: panel.name,
									value: panel.id
								});
							}
						}
					},
					function error(result)
					{
						console.error("Failed to get dashboard list with error: " + result);
					}
			)
		};

		ctrl.runReport = function()
		{
			ctrl.missingRequiredFeildProvider = ctrl.selectedProvider == null;
			ctrl.missingRequiredFeildPanel 		= ctrl.selectedPanel == null;

			if (ctrl.selectedPanel != null && ctrl.selectedProvider != null)
			{
				ctrl.dashboardUrl = DASHBOARD_URL_PATTERN + ctrl.selectedPanel + "&providerNo=" + ctrl.selectedProvider;
				document.querySelector("#dashboard-embedded-page").contentWindow.location.reload();
			}
		};
	}]
});