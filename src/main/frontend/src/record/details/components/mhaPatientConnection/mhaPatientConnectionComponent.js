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

import {MhaDemographicApi, MhaIntegrationApi, SitesApi} from "../../../../../generated";
import {MhaPatientApi} from "../../../../../generated/api/MhaPatientApi";
import {JUNO_BUTTON_COLOR, JUNO_STYLE} from "../../../../common/components/junoComponentConstants";

angular.module('Record.Details').component('mhaPatientConnection', {
	templateUrl: 'src/record/details/components/mhaPatientConnection/mhaPatientConnection.jsp',
	bindings: {
		demographicNo: "<",
		demographicEmail: "<",
		componentStyle: "<?",
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

		// ============= public methods =============

		ctrl.getButtonText = () =>
		{
			if (this.isConfirmed)
			{
				return "View or Edit MHA Status";
			}
			else if (this.hasEmail())
			{
				return "Invite to MyHealthAccess";
			}
			else
			{
				return "Unable to Invite to MyHealthAccess";
			}
		}

		ctrl.getToolTip = () =>
		{
			if (this.isConfirmed)
			{
				return "View or Edit MHA Status";
			}
			else if (this.hasEmail())
			{
				return "Invite patient to MHA via email";
			}
			else
			{
				return "Demographic must have an email address, to be invited to MHA";
			}
		}


		ctrl.getButtonColor = () =>
		{
			if (this.isConfirmed)
			{
				return JUNO_BUTTON_COLOR.PRIMARY
			}
			else if (ctrl.hasEmail())
			{
				return JUNO_BUTTON_COLOR.BASE;
			}
			else
			{
				return JUNO_BUTTON_COLOR.TRANSPARENT;
			}
		}

		ctrl.iconClasses = () =>
		{
			if (this.isConfirmed)
			{
				return [];
			}
			else
			{
				return ["icon-primary"];
			}
		}

		ctrl.hasEmail = () =>
		{
			return this.demographicEmail && this.demographicEmail !== "";
		}

		ctrl.onClick = () =>
		{
			if (ctrl.isConfirmed)
			{
				ctrl.openPatientModal();
			}
		}

		ctrl.openPatientModal = async () =>
		{
			try
			{
				await $uibModal.open(
					{
						component: 'mhaPatientDetailsModal',
						backdrop: 'static',
						windowClass: "juno-modal",
						resolve: {
							style: () => ctrl.componentStyle,
							demographicNo: () => ctrl.demographicNo,
						}
					}
				).result;
			}
			catch(err)
			{
				// user pressed ESC key
			}
		}

		// ============ private methods ==============

		ctrl.loadMhaPatientProfiles = async () =>
		{
			try
			{
				ctrl.isConfirmed = false;
				let integrationsList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;
				for (let integration of integrationsList)
				{
					ctrl.isConfirmed = ctrl.isConfirmed || (await mhaDemographicApi.isPatientConfirmed(integration.id, ctrl.demographicNo)).data.body;
				}
			}
			catch (err)
			{
				console.error("Failed to check MHA connection status with error: " + err.toString());
			}
		}
	}]
});