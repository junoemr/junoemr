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

import {BILLING_REGION} from "../../../billing/billingConstants";
import {AppointmentApi, SystemPreferenceApi} from "../../../../generated";
import SystemPreferenceService from "../../../lib/system/service/SystemPreferenceService";

angular.module('Integration.Mha').component('billingRedirect', {
    bindings: {
    },
    controller: [
        "$scope",
        "$http",
        "$httpParamSerializer",
        "$stateParams",
        "$location",
        "scheduleService",
        "securityService",
        function (
            $scope,
            $http,
            $httpParamSerializer,
            $stateParams,
            $location,
            scheduleService,
            securityService)
    {
        const ctrl = this;
        const systemPreferenceService = new SystemPreferenceService($http, $httpParamSerializer);
        const appointmentApi = new AppointmentApi($http, $httpParamSerializer, '../ws/rs');

        ctrl.$onInit = async () =>
        {
            await this.redirectToBillingPage();
        }

        /**
         * redirect to the appropriate billing page for this Juno server.
         */
        ctrl.redirectToBillingPage = async () =>
        {
            try
            {
                const billingRegion = (await systemPreferenceService.getProperty("billing_type"));
                const appointment = (await appointmentApi.getAppointment($stateParams.appointmentNo)).data.body;
                const appointmentStartTime = Juno.Common.Util.getDatetimeNoTimezoneMoment(`${appointment.appointmentDate}T${appointment.startTime}`);

                var params = {
                    demographic_no: $stateParams.demographicNo,
                    providerNo: $stateParams.providerNo,
                    providerview: $stateParams.providerNo,
                    user_no: securityService.getUser().providerNo,

                    billForm: "GP",
                    billRegion: billingRegion,
                    bNewForm: 1,

                    apptProvider_no: $stateParams.providerNo,
                    appointment_no: $stateParams.appointmentNo,
                    appointmentDate: Juno.Common.Util.formatMomentDate(appointmentStartTime),
                    status: appointment.status,
                    start_time: Juno.Common.Util.formatMomentTime(appointmentStartTime),
                };
                window.location.href = scheduleService.getBillingLink(params);
            }
            catch(error)
            {
                console.error("Failed to redirect to billing.");
                console.error(error.toString());
                $location.url("/dashboard");
            }
        }
    }]
});