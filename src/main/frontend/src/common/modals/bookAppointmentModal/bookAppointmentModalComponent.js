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

import {JUNO_BUTTON_COLOR, JUNO_BUTTON_COLOR_PATTERN, LABEL_POSITION} from "../../components/junoComponentConstants";
import {ScheduleApi, SitesApi, SystemPreferenceApi} from "../../../../generated";
import AppointmentBooking from "./appointmentBooking";
import {SystemProperties} from "../../services/systemPreferenceServiceConstants";

/**
 * Booking modal.
 * Args:
 * title - the title to display at the top of the modal
 * bookingData - [optional] an instance of AppointmentBooking. This data is filled out / edited and yielded in the booking callback
 * onCreateCallback - method called when user creates a new appointment. It is supplied with the a AppointmentBooking object.
 * style - the modal style
 */
angular.module('Common.Components').component('bookAppointmentModal',
{
  templateUrl: 'src/common/modals/bookAppointmentModal/bookAppointmentModal.jsp',
	bindings: {
		modalInstance: "<",
		resolve: "<",
	},
  controller: ['$scope',
	  '$http',
	  '$httpParamSerializer',
	  'providerService',
	  'mhaService',
	  function ($scope,
	            $http,
	            $httpParamSerializer,
	            providerService,
	            mhaService)
	  {
		  let ctrl = this

		  // load apis
		  ctrl.scheduleApi = new ScheduleApi($http, $httpParamSerializer, '../ws/rs');
		  ctrl.sitesApi = new SitesApi($http, $httpParamSerializer, '../ws/rs');
		  ctrl.systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');

		  $scope.LABEL_POSITION = LABEL_POSITION;
		  $scope.JUNO_BUTTON_COLOR_PATTERN = JUNO_BUTTON_COLOR_PATTERN;
		  $scope.JUNO_BUTTON_COLOR = JUNO_BUTTON_COLOR;

		  ctrl.appointmentTypeOptions = [];
		  ctrl.reasonTypeOptions = [];
		  ctrl.siteOptions = [];
		  ctrl.isLoading = false;

		  ctrl.$onInit = async () =>
		  {
			  ctrl.resolve.bookingData = ctrl.resolve.bookingData || new AppointmentBooking();

			  ctrl.isMultisiteEnabled = (await ctrl.systemPreferenceApi.getPropertyEnabled(SystemProperties.Multisites)).data.body;
			  ctrl.loadAppointmentTypeOptions();
			  ctrl.loadAppointmentReasonTypes();

			  if (ctrl.isMultisiteEnabled)
			  {
				  await ctrl.loadSiteOptions();

				  // default to first site if non provided
				  if (!ctrl.resolve.bookingData.siteId && ctrl.siteOptions.length > 0)
				  {
				  	ctrl.resolve.bookingData.siteId = ctrl.siteOptions[0].value;
				  }
			  }

				// default to 15 min duration if none provided
			  if (!ctrl.resolve.bookingData.duration)
			  {
			  	ctrl.resolve.bookingData.duration = AppointmentBooking.DEFAULT_BOOKING_DURATION;
			  }
		  }

		  ctrl.onCancel = () =>
		  {
			  ctrl.modalInstance.close();
		  }

		  ctrl.onCreate = async () =>
		  {
		  	if (ctrl.resolve.onCreateCallback)
			  {
			  	ctrl.isLoading = true;
			  	await ctrl.resolve.onCreateCallback(ctrl.resolve.bookingData);
			  	ctrl.isLoading = false;
			  }

			  ctrl.modalInstance.close();
		  }

			ctrl.canSubmit = () =>
			{
				return ctrl.resolve.bookingData.demographic != null && !ctrl.isLoading;
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
				ctrl.resolve.bookingData.duration = template.duration.toString();
				if (template.location && template.location !== "0")
				{
					ctrl.resolve.bookingData.siteId = ctrl.siteOptions.find((site) => site.label === template.location).value;
				}
				ctrl.resolve.bookingData.notes = template.notes;
				ctrl.resolve.bookingData.reason = template.reason;
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