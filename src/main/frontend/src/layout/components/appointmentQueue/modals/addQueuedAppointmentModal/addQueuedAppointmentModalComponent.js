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
	JUNO_STYLE,
	LABEL_POSITION
} from "../../../../../common/components/junoComponentConstants";
import {AqsQueuedAppointmentApi, MhaIntegrationApi, SitesApi, SystemPreferenceApi} from "../../../../../../generated";
import {SystemProperties} from "../../../../../common/services/systemPreferenceServiceConstants";
import {API_BASE_PATH} from "../../../../../lib/constants/ApiConstants";
import ToastErrorHandler from "../../../../../lib/error/handler/ToastErrorHandler";
import ToastService from "../../../../../lib/alerts/service/ToastService";

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
		function($scope,
		         $http,
		         $httpParamSerializer,
		         $uibModal,
		         providerService)
	{
		let ctrl = this;

		// load api
		let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, API_BASE_PATH);
		let sitesApi = new SitesApi($http, $httpParamSerializer, API_BASE_PATH);
		let aqsQueuedAppointmentApi = new AqsQueuedAppointmentApi($http, $httpParamSerializer, API_BASE_PATH);
		let mhaIntegrationApi = new MhaIntegrationApi($http, $httpParamSerializer, API_BASE_PATH);
		ctrl.errorHandler = new ToastErrorHandler();
		ctrl.toastService = new ToastService();

		ctrl.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;
		ctrl.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		ctrl.LABEL_POSITION = LABEL_POSITION;

		ctrl.bookProviderNo = null;
		ctrl.providerOptions = [];
		ctrl.isMultisiteEnabled = false;
		ctrl.isLoading = true;
		ctrl.providerHasSite = false;

		ctrl.$onInit = async () =>
		{
			try
			{
				ctrl.resolve.style = ctrl.resolve.style || JUNO_STYLE.DEFAULT;

				ctrl.isMultisiteEnabled = (await systemPreferenceApi.getPropertyEnabled(SystemProperties.Multisites)).data.body;
				ctrl.providerOptions = await ctrl.loadProviderList();
			}
			catch(err)
			{
				ctrl.errorHandler.handleError(err);
			}
			ctrl.isLoading = false;
		}

		ctrl.loadProviderList = async () =>
		{
			let providers = (await providerService.searchProviders({active: true}));

			return providers.map((provider) =>
			{
				return {
					value: provider.providerNo,
					label: `${provider.name} (${provider.providerNo})`,
				};
			});
		}

		ctrl.checkProviderSite = async () =>
		{
			if (!ctrl.isMultisiteEnabled)
			{
				return true;
			}

			let bookingSiteId = ctrl.resolve.siteId;
			if (!ctrl.resolve.siteId)
			{
				bookingSiteId = await ctrl.siteFromClinicId(ctrl.resolve.clinicId);
			}
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
			try
			{
				ctrl.bookProviderNo = (await providerService.getMe()).providerNo;
				ctrl.providerHasSite = false;
				ctrl.providerHasSite = await ctrl.checkProviderSite();
			}
			catch(e)
			{
				ctrl.errorHandler.handleError(e);
			}
		}

		ctrl.onProviderSelect = async (option) =>
		{
			try
			{
				ctrl.bookProviderNo = option.value;
				ctrl.providerHasSite = false;
				ctrl.providerHasSite = await ctrl.checkProviderSite();
				$scope.$digest();
			}
			catch(e)
			{
				ctrl.errorHandler.handleError(e);
			}
		}

		ctrl.bookQueuedAppointment = async () =>
		{
			let siteId = null;
			if (ctrl.isMultisiteEnabled)
			{
				siteId = ctrl.resolve.siteId
				if (!siteId)
				{
					siteId = await ctrl.siteFromClinicId(ctrl.resolve.clinicId);
				}
			}

			let bookQueuedAppointmentTransfer = {
				siteId: siteId,
				providerNo: ctrl.bookProviderNo,
			};

			try
			{
				ctrl.isLoading = true;
				return (await aqsQueuedAppointmentApi.bookQueuedAppointment(ctrl.resolve.queueId, ctrl.resolve.queuedAppointmentId, bookQueuedAppointmentTransfer)).data.body;
			}
			catch(error)
			{
				Juno.Common.Util.errorAlert($uibModal,"Failed to book appointment", error.data.error.message);
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

		ctrl.bookButtonDisabled = () =>
		{
			return !ctrl.bookProviderNo || ctrl.isLoading || !ctrl.providerHasSite;
		}

		ctrl.bookVirtualButtonDisabled = () =>
		{
			return !ctrl.bookProviderNo || ctrl.isLoading || !ctrl.providerHasSite || !ctrl.resolve.isVirtual;
		}

	}]
});