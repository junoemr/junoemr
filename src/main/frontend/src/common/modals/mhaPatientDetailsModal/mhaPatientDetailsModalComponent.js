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
import MhaPatientAccessService from "../../../lib/integration/myhealthaccess/service/MhaPatientAccessService";

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
		const mhaPatientAccessService = new MhaPatientAccessService();

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
		ctrl.integrationList = []; // Type MhaIntegration[]
		ctrl.integrationOptions = []; // Type {label: string, value: MhaIntegration}

		ctrl.$onInit = async () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
			ctrl.demographic = ctrl.resolve.demographic;

			await ctrl.loadIntegrationOptions();
		}

		ctrl.rejectConnection = async () =>
		{
			const ok = await Juno.Common.Util.confirmationDialog(
				$uibModal,
				"Are you sure?",
				"Rejecting this patient will disconnect them from your clinic. They will be unable to reconnect until you un-reject them.",
				ctrl.resolve.style);

			if (ok)
			{
				await mhaPatientAccessService.rejectPatient(ctrl.currentIntegration.id, ctrl.currentProfile.id);
				await ctrl.onConnectionStatusUpdated();
			}
		}

		ctrl.cancelRejectConnection = async () =>
		{
			await mhaPatientAccessService.cancelPatientRejection(ctrl.currentIntegration.id, ctrl.currentProfile.id);
			await ctrl.onConnectionStatusUpdated();
		}

		// load integration list and format as options
		ctrl.loadIntegrationOptions = async () =>
		{
			ctrl.integrationList = await mhaConfigService.getMhaIntegrations();
			ctrl.integrationOptions = ctrl.integrationList.map((integration) =>
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
				}
				finally
				{
					ctrl.isLoadingProfile = false;
					$scope.$apply();
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
							integrationsList: () => ctrl.integrationList,
							selectedIntegration: () => ctrl.currentIntegration,
							hideIntegrationSelect: () => true,
						}
					}
				).result;

				await ctrl.onConnectionStatusUpdated();
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