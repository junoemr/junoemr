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
import MhaPatient from "../../../lib/integration/myhealthaccess/model/MhaPatient";
import MhaPatientService from "../../../lib/integration/myhealthaccess/service/MhaPatientService";
import MhaAppointmentService from "../../../lib/integration/myhealthaccess/service/MhaAppointmentService";
import MhaSSOService from "../../../lib/integration/myhealthaccess/service/MhaSSOService";

angular.module("Record.Components").component('mhaCallPanel', {
	templateUrl: 'src/record/components/mhaCallPanel/mhaCallPanel.jsp',
	bindings: {
		demographicNo: "<",
	},
	controller: [
		"$scope",
		"$sce",
		function (
			$scope,
			$sce)
		{
			const ctrl = this;

			$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
			$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
			$scope.LABEL_POSITION = LABEL_POSITION;

			ctrl.iframeUrl = null;
			ctrl.inSession = false;
			ctrl.selectedIntegration = null; // Type MhaIntegration
			ctrl.integrationList = []; // Type MhaIntegration[]
			ctrl.integrationOptions = [];

			ctrl.$onInit = async () =>
			{
				await ctrl.loadIntegrations();

				// if only one integration start call.
				if (ctrl.selectedIntegration)
				{
					ctrl.startCall();
				}
				$scope.$apply();
			}

			ctrl.loadIntegrations = async () =>
			{
				const mhaConfigService = new MhaConfigService()
				const mhaPatientService = new MhaPatientService();

				// get integration list
				ctrl.integrationList = await mhaConfigService.getMhaIntegrations();

				// filter out integrations where the patient is not confirmed.
				const profileIntegrationMap = await Promise.all(ctrl.integrationList.map( async (integration) =>
				{
					return [integration, await mhaPatientService.profileForDemographic(integration.id, ctrl.demographicNo)];
				}));
				ctrl.integrationList = profileIntegrationMap.filter((integrationProfile) => integrationProfile[1] && integrationProfile[1].isConfirmed)
					.map((integrationProfile) => integrationProfile[0]);

				// build integration option list for UI.
				ctrl.integrationOptions = ctrl.integrationList.map((integration) =>
				{
					return {
						label: integration.siteName,
						value: integration,
					};
				});

				if (ctrl.integrationList.length === 1)
				{
					ctrl.selectedIntegration = ctrl.integrationList[0];
				}
			}

			ctrl.startCall = async () =>
			{
				const mhaAppointmentService = new MhaAppointmentService();
				const mhaPatientService = new MhaPatientService();
				const mhaSSOService = new MhaSSOService();

				const newAppointment = await mhaAppointmentService.bookOnDemandAudioAppointment(ctrl.selectedIntegration, await mhaPatientService.profileForDemographic(ctrl.selectedIntegration.id, ctrl.demographicNo));
				ctrl.iframeUrl = $sce.trustAsResourceUrl(await mhaSSOService.getOnDemandAudioCallSSOLink(ctrl.selectedIntegration, newAppointment));
				ctrl.inSession = true;

				$scope.$apply();
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
