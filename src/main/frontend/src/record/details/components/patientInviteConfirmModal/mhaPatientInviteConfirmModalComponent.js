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


import {MhaDemographicApi, SystemPreferenceApi} from "../../../../../generated";
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../../common/components/junoComponentConstants";

angular.module('Common.Components').component('mhaPatientInviteConfirmModal',
{
	templateUrl: 'src/record/details/components/patientInviteConfirmModal/mhaPatientInviteConfirmModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: ['$scope',
		'$http',
		'$httpParamSerializer',
		function ($scope,
		          $http,
		          $httpParamSerializer)
		{
		let ctrl = this;

		// load apis
		let mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer,
				'../ws/rs');
		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
				'../ws/rs');

		ctrl.LABEL_POSITION = LABEL_POSITION;
		ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

		ctrl.integrationsList = [];
		ctrl.selectedIntegrationId = null;
		ctrl.demographicNo = null;
		ctrl.isMultisiteEnabled = false;
		ctrl.isLoading = true;

		ctrl.$onInit = () =>
		{
			ctrl.integrationsList = ctrl.resolve.integrationsList.map((obj) => ({ label: obj.siteName, value: obj.id}));
			ctrl.demographicNo = ctrl.resolve.demographicNo;
			ctrl.demographicEmail = ctrl.resolve.demographicEmail;
			ctrl.initialIntegration = ctrl.resolve.selectedIntegration;
			ctrl.hideIntegrationSelect = ctrl.resolve.hideIntegrationSelect || false;

			systemPreferenceApi.getPropertyEnabled("multisites").then((response) =>
			{
				ctrl.isMultisiteEnabled = response.data.body;

				// if no multisites, there should be exactly 1 integration. auto-select it
				if (!ctrl.isMultisiteEnabled)
				{
					ctrl.selectedIntegrationId = ctrl.integrationsList[0].value;
				}
				else if (ctrl.initialIntegration)
				{
					ctrl.selectedIntegrationId = ctrl.initialIntegration.id;
				}

			}).finally(() =>
			{
				ctrl.isLoading = false;
			})
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.dismiss(false);
		}

		ctrl.sendPatientInvite = () =>
		{
			if (ctrl.selectedIntegrationId)
			{
				ctrl.isLoading = true;
				mhaDemographicApi.patientInvite(ctrl.selectedIntegrationId, ctrl.demographicNo, ctrl.demographicEmail).then((response) =>
				{
					ctrl.modalInstance.close(true);
				}).catch((error) =>
				{
					console.error("Failed to invite patient to MHA", error);
					alert("An error occurred. Check the integration settings or contact support if it persists.");
				}).finally(() =>
				{
					ctrl.isLoading = false;
				});
			}
		}

		ctrl.sendDisabled = () =>
		{
			return (ctrl.isLoading || ctrl.integrationsList.length <= 0 || ctrl.selectedIntegrationId == null);
		}

		ctrl.getComponentClasses = () =>
		{
			return [ctrl.resolve.style]
		}
	}]
});