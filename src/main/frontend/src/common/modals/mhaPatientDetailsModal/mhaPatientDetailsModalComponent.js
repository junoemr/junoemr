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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE} from "../../components/junoComponentConstants";
import {MhaDemographicApi, MhaIntegrationApi} from "../../../../generated";

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

		ctrl.patientProfiles = [];
		ctrl.currentProfile = null;

		ctrl.connectionStatusChanged = false;

		ctrl.$onInit = () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

			if (ctrl.resolve.style === JUNO_STYLE.DRACULA)
			{
				// we are inside an bootstrap transclude component, restyle it.
				angular.element(document.querySelector(".modal-content")).addClass("juno-style-dracula-background");
			}

			ctrl.loadMHAPatientProfiles();
		}

		ctrl.loadMHAPatientProfiles = async () =>
		{
			try
			{
				ctrl.currentProfile = null;
				ctrl.patientProfiles = [];
				let integrationsList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;
				for (let integration of integrationsList)
				{
					let patient = (await mhaDemographicApi.getMHAPatient(integration.id, ctrl.resolve.demographicNo)).data.body;
					if (patient && patient.link_status === "ACTIVE")
					{
						// add computed attribute for display, inputs get upset when they cannot assign to a ng-model
						let province = patient.address_province_code !== "UNKNOWN" ? patient.address_province_code : "";
						let city = patient.city != null ? patient.city : "";
						patient.city_province = `${city} ${province}`;
						patient.connection_status = ctrl.getConnectionStatusHuman(patient.link_status)

						ctrl.patientProfiles.push({
							label: integration.siteName,
							integrationId: integration.id,
							value: patient,
						})
					}
				}

				if (ctrl.patientProfiles.length > 0)
				{
					ctrl.currentProfile = ctrl.patientProfiles[0].value;
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

		ctrl.getCurrentPatientName = () =>
		{
			if (ctrl.currentProfile)
			{
				return `${ctrl.currentProfile.first_name}, ${ctrl.currentProfile.last_name}`;
			}
			return "";
		};

		ctrl.getConnectionStatusHuman = (patientConnectionStatus) =>
		{
			if (patientConnectionStatus === "ACTIVE")
			{
				return "Patient is a CONFIRMED user";
			}
			else if (patientConnectionStatus === "CLINIC_REJECTED")
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

		ctrl.cancelConnection = async () =>
		{
			let userOk = await Juno.Common.Util.confirmationDialog($uibModal, "Cancel Connection?",
					"Are you sure you want to cancel this patients MyHealthAccess connection?", ctrl.resolve.style);

			if (userOk)
			{
				ctrl.connectionStatusChanged = true;
				let integrationId = ctrl.patientProfiles.find((profile) => profile.value === ctrl.currentProfile).integrationId

				if (integrationId)
				{
					await mhaDemographicApi.rejectPatientConnection(integrationId, ctrl.resolve.demographicNo);
					ctrl.loadMHAPatientProfiles();
				}
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close(ctrl.connectionStatusChanged);
		}
	}]
});