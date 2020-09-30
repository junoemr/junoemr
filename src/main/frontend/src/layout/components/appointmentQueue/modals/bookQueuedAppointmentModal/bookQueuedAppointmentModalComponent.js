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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../../../../common/components/junoComponentConstants";
import {ScheduleApi, SitesApi, SystemPreferenceApi} from "../../../../../../generated";

angular.module('Layout.Components.Modal').component('bookQueuedAppointmentModal',
                                                    {
  templateUrl: 'src/layout/components/appointmentQueue/modals/bookQueuedAppointmentModal/bookQueuedAppointmentModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
  controller: ['$scope',
	  '$http',
	  '$httpParamSerializer',
	  'providerService',
	  function ($scope,
	            $http,
	            $httpParamSerializer,
	            providerService)
  {
		let ctrl = this

	  // load apis
	  ctrl.scheduleApi = new ScheduleApi($http, $httpParamSerializer, '../ws/rs');
	  ctrl.sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
	  ctrl.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

	  $scope.LABEL_POSITION = LABEL_POSITION;
	  $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
	  $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		ctrl.bookingDto = {
			demographic: null,
			notes: "",
			reason: "",
			duration: "",
			reasonType: null,
			appointmentType: null,
			critical: false,
			virtual: false,
			siteId: null,
		}

		ctrl.appointmentTypeOptions = [];
		ctrl.reasonTypeOptions = [];
		ctrl.siteOptions = [];

		ctrl.$onInit = async () =>
		{
			ctrl.isMultisiteEnabled = (await ctrl.systemPreferenceApi.getPropertyEnabled("multisites")).data.body;

			ctrl.loadAppointmentTypeOptions();
			ctrl.loadAppointmentReasonTypes();

			if (ctrl.isMultisiteEnabled)
			{
				ctrl.loadSiteOptions();
			}
		}

		ctrl.onCancel = () =>
		{
			ctrl.modalInstance.close();
		}

		ctrl.canSubmit = () =>
		{
			return ctrl.bookingDto.demographic != null;
		}

		ctrl.onAppointmentTypeSelected = (appointmentTypeId) =>
		{
			const apptType = ctrl.appointmentTypeOptions.find((apptType) => apptType.value === appointmentTypeId)

			if (apptType)
			{
				ctrl.applyAppointmentTemplate(apptType.data);
			}
			else
			{
				console.error("Error looking up Appointment type with id [" + reasonId + "]");
			}
		}

	  // apply the appointment type template
	  ctrl.applyAppointmentTemplate = (template) =>
	  {
			ctrl.bookingDto.duration = template.duration.toString();
			if (template.location && template.location !== "0")
			{
				ctrl.bookingDto.siteId = ctrl.siteOptions.find((site) => site.label === template.location).value;
			}
			ctrl.bookingDto.notes = template.notes;
			ctrl.bookingDto.reason = template.reason;
	  }

		ctrl.loadAppointmentTypeOptions = async () =>
		{
			const appointmentTypes = (await ctrl.scheduleApi.getAppointmentTypes()).data.body;
			ctrl.appointmentTypeOptions = [];

			appointmentTypes.forEach((type) =>
			{
				ctrl.appointmentTypeOptions.push(
				{
					label: type.name,
					value: type.id,
					data: type,
				});
			});
		}

		ctrl.loadAppointmentReasonTypes = async () =>
		{
			const reasonTypes = (await ctrl.scheduleApi.getAppointmentReasons()).data.body;
			ctrl.reasonTypeOptions = [];

			reasonTypes.forEach((type) =>
      {
      	ctrl.reasonTypeOptions.push(
        {
	        label: type.label,
	        value: type.id,
        })
      });
		}

		ctrl.loadSiteOptions = async () =>
		{
			const provider = (await providerService.getMe());
			const siteOptions = (await ctrl.sitesApi.getSitesByProvider(provider.providerNo)).data.body;
			ctrl.siteOptions = [];

			siteOptions.forEach((site) =>
			{
				ctrl.siteOptions.push(
				{
					label: site.name,
					value: site.siteId,
				})
			});
		}

  }]
});