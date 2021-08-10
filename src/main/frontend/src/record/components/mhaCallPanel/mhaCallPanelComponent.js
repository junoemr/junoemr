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


import {
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	LABEL_POSITION
} from "../../../common/components/junoComponentConstants";
import {MhaCallPanelEvents} from "./mhaCallPanelEvents";
import MhaConfigService from "../../../lib/integration/myhealthaccess/service/MhaConfigService";

angular.module("Record.Components").component('mhaCallPanel', {
	templateUrl: 'src/record/components/mhaCallPanel/mhaCallPanel.jsp',
	bindings: {
		demographicNo: "<",
	},
	controller: [
		"$scope",
		function (
			$scope)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.LABEL_POSITION = LABEL_POSITION;

			ctrl.inSession = false;
			ctrl.selectedIntegration = null; // Type MhaIntegration
			ctrl.integrationList = []; // Type MhaIntegration[]
			ctrl.integrationOptions = [];

			ctrl.$onInit = async () =>
			{
				await ctrl.loadIntegrations();
				$scope.$apply();
			}

			ctrl.loadIntegrations = async () =>
			{
				const mhaConfigService = new MhaConfigService()
				ctrl.integrationList = await mhaConfigService.getMhaIntegrations();
				ctrl.integrationOptions = ctrl.integrationList.map((integration) =>
				{
					return {
						label: integration.siteName,
						value: integration.id,
						data: integration,
					};
				});

				if (ctrl.integrationList.length === 1)
				{
					ctrl.selectedIntegration = ctrl.integrationList[0];
				}
			}

			ctrl.startCall = () =>
			{
				ctrl.inSession = true;
			}

			ctrl.close = () =>
			{
				$scope.$emit(MhaCallPanelEvents.Close);
			}

			ctrl.componentClasses = () =>
			{
				return {
					"in-session": ctrl.inSession,
				};
			}
		}
	],
});
