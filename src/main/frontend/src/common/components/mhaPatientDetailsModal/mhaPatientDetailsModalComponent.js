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

import {LABEL_POSITION, JUNO_BUTTON_COLOR, JUNO_STYLE} from "../junoComponentConstants";
import {MhaDemographicApi, MhaIntegrationApi} from "../../../../generated";

angular.module('Common.Components').component('mhaPatientDetailsModal',
{
	templateUrl: 'src/common/components/mhaPatientDetailsModal/mhaPatientDetailsModal.jsp',
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
		let mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer,
				'../ws/rs');
		let mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer,
				'../ws/rs');

		$scope.LABEL_POSITION = LABEL_POSITION;
		$scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		ctrl.patientProfiles = [];
		ctrl.currentProfile = null;

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
					if (patient)
					{
						ctrl.patientProfiles.push({
							label: integration.siteName,
							value: patient,
						})
					}
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

		ctrl.getCurrentPatientHinAndProv = () =>
		{
			if (ctrl.currentProfile)
			{
				return `${ctrl.currentProfile.health_number} ${ctrl.currentProfile.health_care_province_code}`;
			}
			return "";
		};

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.dismiss();
		}
	}]
});