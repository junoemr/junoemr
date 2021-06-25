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

import {MhaDemographicApi, MhaIntegrationApi} from "../../../../../generated";
import { MHA_PATIENT_CONNECTION_ACTIONS } from "./mhaPatientConnectionConstants"
import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, JUNO_STYLE} from "../../../../common/components/junoComponentConstants";

angular.module('Record.Details').component('mhaPatientConnection', {
	templateUrl: 'src/record/details/components/mhaPatientConnection/mhaPatientConnection.jsp',
	bindings: {
		demographic: "<",
		componentStyle: "<?",
		onSiteListChange: "&?",
	},
	controller: [
		'$scope',
		'$location',
		'$window',
		'$http',
		'$httpParamSerializer',
		'$uibModal',
		function ($scope,
		          $location,
		          $window,
		          $http,
		          $httpParamSerializer,
		          $uibModal)
	{
		let ctrl = this;

		ctrl.isConfirmed = false;
		ctrl.inviteSent = false;
		ctrl.integrationsList = [];

		// load apis
		let mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer,
				'../ws/rs');
		let mhaDemographicApi = new MhaDemographicApi($http, $httpParamSerializer,
				'../ws/rs');

		ctrl.$onInit = () =>
		{
			ctrl.componentStyle = ctrl.componentStyle || JUNO_STYLE.DEFAULT;
			ctrl.loadMhaPatientProfiles();
		}

		ctrl.$onChanges = (changesObj) =>
		{
			if (changesObj.demographic)
			{
				ctrl.loadMhaPatientProfiles();
			}
		}

		// ============= public methods =============

		ctrl.getButtonText = () =>
		{
			return "View or Edit MHA Status";
		}

		ctrl.getToolTip = () =>
		{
			return "View or Edit MHA Status";
		}

		ctrl.getButtonColor = () =>
		{
			return JUNO_BUTTON_COLOR.PRIMARY;
		}

		ctrl.getButtonColorPattern = () =>
		{
			return JUNO_BUTTON_COLOR_PATTERN.FILL;
		}

		ctrl.hasEmail = () =>
		{
			return this.demographic && this.demographic.email && this.demographic.email !== "";
		}

		ctrl.onClick = () =>
		{
			ctrl.openPatientModal();
		}

		ctrl.openPatientModal = async () =>
		{
			try
			{
				let connectionChange = await $uibModal.open(
					{
						component: 'mhaPatientDetailsModal',
						backdrop: 'static',
						windowClass: "juno-simple-modal-window",
						resolve: {
							style: () => ctrl.componentStyle,
							demographic: () => ctrl.demographic,
						}
					}
				).result;

				if (connectionChange)
				{
					// re check confirmation status
					await ctrl.loadMhaPatientProfiles();
				}
			}
			catch(err)
			{
				// user pressed ESC key
			}
		}

		// ============ private methods ==============

		ctrl.loadMhaPatientProfiles = async () =>
		{
			if (ctrl.demographic && ctrl.demographic.demographicNo)
			{
				try
				{
					ctrl.isConfirmed = false;
					ctrl.integrationsList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;
					let siteList = [];
					for (let integration of ctrl.integrationsList)
					{
						let isConfirmed = (await mhaDemographicApi.isPatientConfirmed(integration.id, ctrl.demographic.demographicNo)).data.body;
						ctrl.isConfirmed = ctrl.isConfirmed || isConfirmed;

						if (isConfirmed)
						{
							siteList.push(integration.siteName);
						}
					}


					if (ctrl.onSiteListChange)
					{
						ctrl.onSiteListChange({sites: siteList});
					}
				} catch (err)
				{
					console.error("Failed to check MHA connection status with error: " + err.toString());
				}
			}
		}

		// re-check patient connection status
		$scope.$on(MHA_PATIENT_CONNECTION_ACTIONS.REFRESH, () =>
		{
			ctrl.loadMhaPatientProfiles();
		});
	}]
});