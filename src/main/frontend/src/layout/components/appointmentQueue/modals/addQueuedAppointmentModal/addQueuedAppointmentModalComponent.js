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
	JUNO_BUTTON_COLOR,
	JUNO_BUTTON_COLOR_PATTERN,
	JUNO_STYLE, LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import {
	AqsQueuedAppointmentApi,
	SitesApi,
	SystemPreferenceApi,
	BookQueuedAppointmentTransfer,
	MhaIntegrationApi
} from "../../../../../../generated";

angular.module('Layout.Components.Modal').component('addQueuedAppointmentModal',
{
	templateUrl: 'src/layout/components/appointmentQueue/modals/addQueuedAppointmentModal/addQueuedAppointmentModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
	controller: ['$scope',
		"$http",
		"$httpParamSerializer",
		"$uibModal",
		"providerService",
		function ($scope,
							$http,
							$httpParamSerializer,
							$uibModal,
							providerService)
	{
		let ctrl = this;

		// load api
		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer,
				'../ws/rs');
		let sitesApi = new SitesApi($http, $httpParamSerializer, "../ws/rs");
		let aqsQueuedAppointmentApi = new AqsQueuedAppointmentApi($http, $httpParamSerializer, "../ws/rs");
		let mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer, "../ws/rs");

		ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		ctrl.LABEL_POSITION = LABEL_POSITION;

		ctrl.bookProviderNo = null;
		ctrl.providerOptions = [];
		ctrl.siteOptions = [];
		ctrl.siteSelection = null;
		ctrl.isMultisiteEnabled = false;
		ctrl.isLoading = false;

		ctrl.$onInit = async () =>
		{
			ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

			ctrl.isMultisiteEnabled = (await systemPreferenceApi.getPropertyEnabled("multisites")).data.body;
			ctrl.loadProviderList();
		}

		ctrl.loadProviderList = async () =>
		{
			try
			{
				const providers = (await providerService.searchProviders({active: true}));
				ctrl.providerOptions = providers.map((provider) => {return {value: provider.providerNo, label: `${provider.name} (${provider.providerNo})`}});
			}
			catch (err)
			{
				console.error("Could not fetch provider list with error", err)
			}
		}

		ctrl.onProviderSelectChange = (newProviderNo) =>
		{
			if (ctrl.isMultisiteEnabled && newProviderNo)
			{
				ctrl.loadSiteOptions(newProviderNo);
			}
		}

		ctrl.assignToMe = async () =>
		{
			ctrl.bookProviderNo = (await providerService.getMe()).providerNo;
			ctrl.loadSiteOptions(ctrl.bookProviderNo)
		}

		ctrl.loadSiteOptions = async (providerNo) =>
		{
			ctrl.siteSelection = null;

			// load site and integration list
			let integrationList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;
			let siteList = (await sitesApi.getSitesByProvider(providerNo)).data.body;

			//filter out sites that don't have an MHA integration
			siteList = siteList.filter((site) => integrationList.find((integration) => site.siteId === integration.siteId))

			ctrl.siteOptions = siteList.map((site) => { return {label: site.name, value: site.siteId}});
		}

		ctrl.bookQueuedAppointment = async () =>
		{
			let bookQueuedAppointmentTransfer = {};
			if (ctrl.isMultisiteEnabled)
			{
				bookQueuedAppointmentTransfer.siteId = ctrl.siteSelection;
			}

			bookQueuedAppointmentTransfer.providerNo = ctrl.bookProviderNo;

			try
			{
				ctrl.isLoading = true;
				await aqsQueuedAppointmentApi.bookQueuedAppointment(ctrl.resolve.queueId, ctrl.resolve.queuedAppointmentId, bookQueuedAppointmentTransfer);
			}
			catch(err)
			{
				Juno.Common.Util.errorAlert($uibModal,
				                            "Failed to book appointment",
				                            "Could not schedule the queued appointment. It may have been canceled");
			}
			finally
			{
				ctrl.isLoading = false;

				// refresh the queued appointment list
				if (ctrl.resolve.loadQueuesCallback)
				{
					ctrl.resolve.loadQueuesCallback();
				}

				ctrl.modalInstance.close();
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close();
		};

	}]
});