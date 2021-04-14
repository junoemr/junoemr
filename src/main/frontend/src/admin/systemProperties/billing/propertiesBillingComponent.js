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

import {SystemPreferenceApi} from "../../../../generated/api/SystemPreferenceApi";

angular.module('Admin').component('systemPropertiesBilling',
    {
        templateUrl: 'src/admin/systemProperties/billing/propertiesBilling.jsp',
        bindings: {},
        controller: [
            '$scope',
            '$http',
            '$httpParamSerializer',
            '$state',
            '$uibModal',
            'billingService',
            function ($scope, $http, $httpParamSerializer, $state, $uibModal, billingService) {

                let ctrl = this;
                let systemPreferenceApi = new SystemPreferenceApi($http, $httpParamSerializer, '../ws/rs');
                const PLACEHOLDER = 'Set the default Service Location Code'

                ctrl.selectedValue = null;
                ctrl.codes = [];

                ctrl.propertiesList =
                    [
                        {
                            name: "Service Location Code",
                            description: "Change the default service location code",
                            propertyName: "service_location_code",
                            value: ctrl.selectedValue,
                        }
                    ];

                ctrl.$onInit = async () =>
                {
                     ctrl.load("service_location_code");
                     await ctrl.getServiceLocationCodes();
                };

                ctrl.getServiceLocationCodes = async () =>
                {
                    const codes = (await billingService.getBCBillingVisitCodes()).data.body;

                    codes.forEach((code) =>
				    {
				        ctrl.codes.push(
                            {
                                label: code.visitType + " | " + code.visitDescription,
                                value: code.visitType,
                            })
				    });
                }

                ctrl.load = (property) =>
                {
                    systemPreferenceApi.getPreferenceValue(property)
                        .then((response) =>
                        {
                            ctrl.selectedValue = response.data.body || PLACEHOLDER;
                        })
                };

                /**
                 * Persist new property value
                 * @param property property to update
                 * @param value set the value in the database
                 */
                ctrl.updateProperty = (property) =>
                {
                    if (systemPreferenceApi.putPreferenceValue(property.propertyName, ctrl.selectedValue ))
                    {
                        Juno.Common.Util.successAlert($uibModal, "Success", "Service Location Code updated");
                    }
                    else {
                        Juno.Common.Util.errorAlert($uibModal, "Error", "Service Location Code was not successfully updated.");

                    }
                };
            }
        ]
    });