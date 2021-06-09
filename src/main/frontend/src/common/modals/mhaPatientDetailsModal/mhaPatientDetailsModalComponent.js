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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE, JUNO_BUTTON_COLOR_PATTERN} from "../../components/junoComponentConstants";
import {MhaDemographicApi, MhaIntegrationApi, PatientTo1} from "../../../../generated";

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
		let ctrl = this;

		// load apis
		let mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer,
			'../ws/rs');
		let mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer,
				'../ws/rs');

		$scope.LABEL_POSITION = LABEL_POSITION;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		$scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;

		ctrl.currentProfile = null;
		ctrl.connectionStatus = null;
		ctrl.connectionStatusChanged = false;

		ctrl.integrationTabs = [];
		ctrl.currentIntegration = null;

		ctrl.$onInit = () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;
			ctrl.demographic = ctrl.resolve.demographic;

			ctrl.loadMHAPatientProfiles();

			$scope.$watch("$ctrl.currentIntegration", (newValue, oldValue) =>
			{
				if (newValue !== oldValue)
				{
					ctrl.currentProfile = ctrl.currentIntegration.patient;
					ctrl.updateConnectionStatus();
				}
			});
		}

		ctrl.loadMHAPatientProfiles = async () =>
		{
			try
			{
				ctrl.currentProfile = null;
				ctrl.integrationTabs = [];
				ctrl.currentIntegration = null;
				ctrl.integrationsList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;
				for (let integration of ctrl.integrationsList)
				{
					let patient = (await mhaDemographicApi.getMHAPatient(integration.id, ctrl.demographic.demographicNo)).data.body;
					if (patient)
					{
						if(patient.link_status === PatientTo1.LinkStatusEnum.CONFIRMED ||
							patient.link_status === PatientTo1.LinkStatusEnum.VERIFIED)
						{
							// add computed attribute for display, inputs get upset when they cannot assign to a ng-model
							let province = patient.address_province_code !== "UNKNOWN" ? patient.address_province_code : "";
							let city = patient.city != null ? patient.city : "";
							patient.city_province = `${city} ${province}`;
						}
						integration.patient = patient;
					}
					integration.inviteSent = false;

					ctrl.integrationTabs.push({
						label: integration.siteName,
						value: integration,
					})
				}

				if (ctrl.integrationTabs.length > 0)
				{
					ctrl.currentIntegration = ctrl.integrationTabs[0].value;
					ctrl.currentProfile = ctrl.currentIntegration.patient;
					ctrl.updateConnectionStatus();
				}
				else
				{
					// close modal
					ctrl.onCancel();
				}
			}
			catch(err)
			{
				console.error(`Failed to load MHA patient profiles with error ${err}`);
			}
		};

		ctrl.getLocalPatientName = () =>
		{
			return `${ctrl.demographic.lastName}, ${ctrl.demographic.firstName}`;
		}

		ctrl.getLocalPatientHinAndProv = () =>
		{
			return `${ctrl.demographic.hin} ${ctrl.demographic.hcType}`;
		}

		ctrl.getCurrentPatientName = () =>
		{
			if (ctrl.currentProfile)
			{
				return `${ctrl.currentProfile.last_name}, ${ctrl.currentProfile.first_name}`;
			}
			return "";
		};

		ctrl.getConnectionStatusHuman = (patientConnectionStatus) =>
		{
			if (patientConnectionStatus === PatientTo1.LinkStatusEnum.CONFIRMED ||
				patientConnectionStatus === PatientTo1.LinkStatusEnum.VERIFIED)
			{
				return "Patient is a CONFIRMED user";
			}
			else if (patientConnectionStatus === PatientTo1.LinkStatusEnum.CLINICREJECTED)
			{
				return "Patient has been REJECTED by clinic";
			}
			else
			{
				return "Patient is a UNCONFIRMED user";
			}
		}

		ctrl.getCurrentPatientHinAndProv = () =>
		{
			if (ctrl.currentProfile)
			{
				return `${ctrl.currentProfile.health_number} ${ctrl.currentProfile.health_care_province_code}`;
			}
			return "";
		};

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

		ctrl.cancelConnection = async () =>
		{
			if(ctrl.currentIntegration)
			{
				let userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Cancel Connection?",
					"Are you sure you want to cancel this patients MyHealthAccess connection?", ctrl.resolve.style);

				if (userOk)
				{
					ctrl.connectionStatusChanged = true;
					let integrationId = ctrl.currentIntegration.id;

					if (integrationId)
					{
						await mhaDemographicApi.rejectPatientConnection(integrationId, ctrl.demographic.demographicNo);
						ctrl.loadMHAPatientProfiles();
					}
				}
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close(ctrl.connectionStatusChanged);
		}

		ctrl.updateConnectionStatus = () =>
		{
			if(ctrl.currentProfile)
			{
				ctrl.connectionStatus = ctrl.getConnectionStatusHuman(ctrl.currentProfile.link_status);
			}
			else
			{
				ctrl.connectionStatus = "No Connection";
			}
		}

		ctrl.hasActiveConnection = () =>
		{
			if(ctrl.currentProfile)
			{
				return ctrl.currentProfile.link_status === PatientTo1.LinkStatusEnum.CONFIRMED ||
					ctrl.currentProfile.link_status === PatientTo1.LinkStatusEnum.VERIFIED;
			}
			return false;
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