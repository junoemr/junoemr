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
		ctrl.isMultisiteEnabled = false;
		ctrl.isLoading = false;
		ctrl.providerHasSite = false;

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

		ctrl.checkProviderSite = async () =>
		{
			if (!ctrl.isMultisiteEnabled)
			{
				return true;
			}

			const bookingSiteId = await ctrl.siteFromClinicId(ctrl.resolve.clinicId);
			const siteList = (await sitesApi.getSitesByProvider(ctrl.bookProviderNo)).data.body;

			for (let providerSite of siteList)
			{
				if (providerSite.siteId === bookingSiteId)
				{
					return true;
				}
			}
			return false;
		}

		ctrl.assignToMe = async () =>
		{
			ctrl.bookProviderNo = (await providerService.getMe()).providerNo;
			ctrl.providerHasSite = false;
			ctrl.providerHasSite = await ctrl.checkProviderSite();
		}

		ctrl.onProviderSelect = async () =>
		{
			ctrl.providerHasSite = false;
			ctrl.providerHasSite = await ctrl.checkProviderSite();
			$scope.$digest();
		}

		ctrl.bookQueuedAppointment = async () =>
		{
			let bookQueuedAppointmentTransfer = {
				siteId: await ctrl.siteFromClinicId(ctrl.resolve.clinicId),
				providerNo: ctrl.bookProviderNo,
			};

			try
			{
				ctrl.isLoading = true;
				return (await aqsQueuedAppointmentApi.bookQueuedAppointment(ctrl.resolve.queueId, ctrl.resolve.queuedAppointmentId, bookQueuedAppointmentTransfer)).data.body;
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

			return null;
		}

		// get the appointment site from the clinic id
		ctrl.siteFromClinicId = async (clinicId) =>
		{
			let integrationList = (await mhaIntegrationApi.searchIntegrations(null, true)).data.body;

			for (let integration of integrationList)
			{
				if (integration.remoteId === clinicId)
				{
					return integration.siteId;
				}
			}

			Juno.Common.Util.errorAlert($uibModal,
			                            "Failed to book appointment",
			                            "The clinic this queued appointment was booked for is not connected to your Juno server." +
					                            " Please contact support. Clinic Id [" + clinicId + "]");
			throw "No integration for clinicId [" + clinicId + "]";
		}

		ctrl.bookAndStartTelehealth = async () =>
		{
			let appointment = await ctrl.bookQueuedAppointment();
			if (appointment)
			{
				Juno.Common.Util.openTelehealthWindow(appointment.demographicNo, appointment.id, ctrl.isMultisiteEnabled ? appointment.location : null);
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close();
		};

		ctrl.bookTooltip = (okMsg) =>
		{
			if (!ctrl.bookProviderNo)
			{
				return "Select a provider"
			}
			else if (!ctrl.providerHasSite)
			{
				return "Provider is not assigned to the site of this appointment";
			}
			return okMsg;
		}

	}]
});