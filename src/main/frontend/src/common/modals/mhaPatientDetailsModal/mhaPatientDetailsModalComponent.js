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

import {
	LABEL_POSITION,
	JUNO_BUTTON_COLOR,
	JUNO_STYLE,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_TAB_TYPE
} from "../../components/junoComponentConstants";
import {MhaDemographicApi, MhaIntegrationApi, PatientTo1} from "../../../../generated";
import {JUNO_SIMPLE_MODAL_FILL_COLOR} from "../junoSimpleModal/junoSimpleModalConstants";
import MhaConfigService from "../../../lib/integration/myhealthaccess/service/MhaConfigService";
import MhaPatientService from "../../../lib/integration/myhealthaccess/service/MhaPatientService";

angular.module('Common.Components').component('mhaPatientDetailsModal',
{
	templateUrl: 'src/common/modals/mhaPatientDetailsModal/mhaPatientDetailsModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: ['$scope',
			'$http',
			'$httpParamSerializer',
			'$uibModal',
		function ($scope,
		          $http,
		          $httpParamSerializer,
		          $uibModal)
	{
		const ctrl = this;

		const mhaConfigService = new MhaConfigService();
		const mhaPatientService = new MhaPatientService();

		// load apis
		let mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer,
				'../ws/rs');

		$scope.LABEL_POSITION = LABEL_POSITION;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		$scope.JUNO_SIMPLE_MODAL_FILL_COLOR = JUNO_SIMPLE_MODAL_FILL_COLOR;
		$scope.JUNO_TAB_TYPE = JUNO_TAB_TYPE;

		ctrl.isLoadingProfile = false;
		ctrl.currentProfile = null; // Type MhaPatient
		ctrl.currentIntegration = null; // Type MhaIntegration
		ctrl.integrationOptions = []; // Type MhaIntegration[]

		ctrl.$onInit = async () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
			ctrl.demographic = ctrl.resolve.demographic;

			await ctrl.loadIntegrationOptions();
		}

		// load integration list and format as options
		ctrl.loadIntegrationOptions = async () =>
		{
			ctrl.integrationOptions = (await mhaConfigService.getMhaIntegrations()).map((integration) =>
			{
				return {label: integration.siteName, value: integration};
			});

			// default to first integration
			if (ctrl.integrationOptions.length > 0)
			{
				ctrl.currentIntegration = ctrl.integrationOptions[0].value;
			}

			$scope.$apply();
		}

		ctrl.onConnectionStatusUpdated = async () =>
		{
			ctrl.connectionStatusChanged = true;
			await ctrl.loadMhaProfile();
		}

		ctrl.loadMhaProfile = async () =>
		{
			if (ctrl.currentIntegration && ctrl.demographic)
			{
				try
				{
					ctrl.isLoadingProfile = true;
					ctrl.currentProfile = await mhaPatientService.profileForDemographic(ctrl.currentIntegration.id, ctrl.demographic.demographicNo);
					$scope.$apply();
				}
				finally
				{
					ctrl.isLoadingProfile = false;
				}
			}
		}

		$scope.$watch("$ctrl.currentIntegration", ctrl.loadMhaProfile);
		$scope.$watch("$ctrl.demographic", ctrl.loadMhaProfile);

		ctrl.openInviteConfirmModal = async () =>
		{
			try
			{
				ctrl.currentIntegration.inviteSent = await $uibModal.open(
					{
						component: 'mhaPatientInviteConfirmModal',
						backdrop: 'static',
						windowClass: "juno-modal sml",
						resolve: {
							style: () => JUNO_STYLE.GREY, //TODO regular style use when it doesn't break button/text colours
							demographicNo: () => ctrl.demographic.demographicNo,
							demographicEmail: () => ctrl.demographic.email,
							integrationsList: () => ctrl.integrationsList,
							selectedIntegration: () => ctrl.currentIntegration,
						}
					}
				).result;
			}
			catch(err)
			{
				// user pressed ESC key
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close(ctrl.connectionStatusChanged);
		}

		ctrl.getInviteButtonText = () =>
		{
			if(ctrl.currentIntegration && ctrl.currentIntegration.inviteSent)
			{
				return "Resend Invite";
			}
			return "Send Invite";
		}
	}]
});